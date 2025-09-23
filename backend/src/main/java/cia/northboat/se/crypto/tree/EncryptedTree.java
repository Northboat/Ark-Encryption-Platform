package cia.northboat.se.crypto.tree;

import cia.northboat.se.crypto.tree.model.Ciphertext;
import cia.northboat.se.crypto.tree.model.TreeNode;
import cia.northboat.se.utils.EncodeUtil;
import cia.northboat.se.utils.HashUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Getter
@Setter
@Component
public class EncryptedTree {

    TreeNode root;
    Field G1, Zr;
    int height, n; // 这个 n 是字段的个数 → 树的维度

    @Autowired
    public EncryptedTree(Field G1, Field Zr){
        this.G1 = G1;
        this.Zr = Zr;
        height = 1;
        n = 3;
        IPFEUtil.keygen(G1, Zr);
    }

    public TreeNode insert(String[] cur){
        // 将明文哈希，将用于前缀比较和 x 生成
        Element[] curM = HashUtil.hashStrArr2ZrArr(Zr, cur);
        if(root == null){
            String[] initPrefix = new String[n];
            // 初始化一个前缀，这会浪费 n 长度的前缀
            for(int i = 0; i < n; i++){
                initPrefix[i] = "0";
            }
            root = IPFEUtil.enc(Zr, initPrefix, curM, n);
            height++;
            return root;
        }
        return insert(root.getPrefix(), curM, root, 1);
    }


    public TreeNode insert(String[] pre, Element[] curM, TreeNode root, int h){

        int n = root.getN();
        Element[] m = root.getM();

        String z = EncodeUtil.singleDimensionDec(m, curM, n); // 增加的前缀
        String[] newPrefix = EncodeUtil.superposePrefix(pre, z, n); // 构成新的前缀

        TreeNode node = IPFEUtil.enc(Zr, newPrefix, curM, n); // 根据当前前缀和明文哈希生成节点

        int i = Integer.parseInt(z, 2); // 解析二进制为十进制
        TreeNode child = root.getSubtree()[i];
        h++; // 选取到了孩子，层高 +1

        // 如果这里为空，就直接插入
        if(child == null){
            root.setSubtree(node, i);
            height = Math.max(height, h);
            return node;
        }
        // 否则继续向下找
        return insert(newPrefix, curM, child, h);
    }


    public void clean(){
        root = null;
        height = 0;
    }


    public String randomBuild(int count){
        int dimension = getN();
        List<List<String>> data = generateData(count, dimension);
        build(data);
        return getTreeStruct();
    }


    public void build(List<List<String>> list){
        for(List<String> l : list){
            insert(l.toArray(new String[0]));
        }
    }


    public List<List<String>> generateData(int count, int dimension){
        Set<List<String>> uniqueLists = new HashSet<>();
        Random random = new Random();

        while (uniqueLists.size() < count) {
            List<String> innerList = new ArrayList<>();
            for (int j = 0; j < dimension; j++) {
                // 这里用 UUID 或者随机数
                innerList.add(UUID.randomUUID().toString().substring(0, 8));
                // innerList.add("val_" + random.nextInt(10000)); // 也可以用随机数
            }
            uniqueLists.add(innerList); // HashSet 会帮我们去重
        }

        return new ArrayList<>(uniqueLists);
    }



    public boolean match(TreeNode node, Ciphertext ciphertext){
        Element g = IPFEUtil.getBase();
        return IPFEUtil.match(G1, Zr, node, ciphertext, g);
    }


    public TreeNode search(String[] cur){
        // 将明文哈希
        Element[] q = HashUtil.hashStrArr2ZrArr(Zr, cur);

        Ciphertext ciphertext = IPFEUtil.trap(Zr, q);
        return search(root, ciphertext);
    }


    public TreeNode search(TreeNode node, Ciphertext ciphertext){
        if(node == null || ciphertext == null){
            return null;
        }
        if(match(node, ciphertext)) {
            return node;
        }

        String z = EncodeUtil.singleDimensionDec(node.getM(), ciphertext.getY(), n); // 增加的前缀
        int i = Integer.parseInt(z, 2);

        return search(node.getSubtree()[i], ciphertext);
    }


    public String getTreeStruct() {
        StringBuilder sb = new StringBuilder();
        getTreeStruct(root, "", false, sb);
        return sb.toString();
    }



    // DFS
    public void getTreeStruct(TreeNode node, String prefix, boolean isTail, StringBuilder sb) {
        if (node == null) return;

//        System.out.println(node);

        sb.append(prefix).append(isTail ? "└── " : "├── ");
        sb.append(formatPrefix(node)).append("\n");
        TreeNode[] children = node.getSubtree();
        int childCount = (int) Arrays.stream(children).filter(Objects::nonNull).count();

        int printed = 0;
        for (int i = 0; i < (int)Math.pow(2, n); i++) {
            TreeNode child = children[i];
            if (child != null) {
                printed++;
                boolean last = (printed == childCount);
                getTreeStruct(child, prefix + (isTail ? "    " : "│   "), last, sb);
            }
        }
    }

    public String formatPrefix(TreeNode node){
        return node.getPrefixStr() != null ? node.getPrefixStr() : "";
    }
}

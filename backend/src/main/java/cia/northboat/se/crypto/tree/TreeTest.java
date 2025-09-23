package cia.northboat.se.crypto.tree;

import cia.northboat.se.crypto.tree.model.Ciphertext;
import cia.northboat.se.crypto.tree.model.TreeNode;
import cia.northboat.se.utils.HashUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class TreeTest {

    public static Field G1, Zr;
    // 这里的 n 是字符串被映射的数组长度，这个和前缀是很相关的，决定了前缀的长度上限
    private static final int l = 52;

    static{
        Pairing bp = PairingFactory.getPairing("a.properties");
        G1 = bp.getG1();
        Zr = bp.getZr();
    }

    public static void main(String[] args) {
        EncryptedTree tree = new EncryptedTree(G1, Zr);
        tree.init(4);
        test(tree);
    }


    public static void test(EncryptedTree tree){

//        tree.init(4);
        tree.randomBuild(50);

        String[] str1 = new String[]{"nmsl", "test", "wcnm", "wdnmd"};
        TreeNode t1 = tree.insert(str1);
//        System.out.println(t1);

        String[] str2 = new String[]{"nmsl", "test", "sad", "wdnmd"};
        TreeNode t2 = tree.insert(str2);
        System.out.println(t1);
        System.out.println(t2);


        String[] query1 = new String[]{"nmsl", "test", "wcnm", "wdnmd"};
        TreeNode q1 = tree.search(query1);
        System.out.println(q1);

        System.out.println(tree.getTreeStruct());
    }
}

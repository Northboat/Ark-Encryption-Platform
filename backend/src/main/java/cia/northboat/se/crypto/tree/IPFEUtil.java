package cia.northboat.se.crypto.tree;


import cia.northboat.se.crypto.tree.model.Ciphertext;
import cia.northboat.se.crypto.tree.model.TreeNode;
import cia.northboat.se.utils.HashUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class IPFEUtil {

    private static final int l = 52;

    // 公钥
    public static Element g, h;
    private static Element[] s, t, h_i;


    public static Element getBase(){
        return g;
    }

    public static void keygen(Field G1, Field Zr){

        g = G1.newRandomElement().getImmutable();
        h = G1.newRandomElement().getImmutable();

        s = new Element[l];
        t = new Element[l];
        h_i = new Element[l];
        for(int i = 0; i < l; i++){
            s[i] = Zr.newRandomElement().getImmutable();
            t[i] = Zr.newRandomElement().getImmutable();
            h_i[i] = g.powZn(s[i]).mul(h.powZn(t[i])).getImmutable();
        }

    }


    // 加密生成节点，关键的加密原料 x 完全来自于哈希过的明文 m
    public static TreeNode enc(Field Zr, String[] prefix, Element[] m, int n){

        // 如何将 n 维的 m 映射到 l 长的 x 数组？每次结果必须一致
        // 直接复制过去，多余的位用 0 填充
        Element[] x = HashUtil.fillInArr(Zr, m, l);


//        System.out.println("====== Encrypt ======");
        Element s1 = Zr.newZeroElement();
        Element s2 = Zr.newZeroElement();

        for(int i = 0; i < l; i++){
            s1 = s1.add(s[i].mul(x[i]));
            s2 = s2.add(t[i].mul(x[i]));
        }
        Element s_x = s1.getImmutable();
        Element t_x = s2.getImmutable();

        return TreeNode.builder()
                .m(m)
                .x(x)
                .t_x(t_x)
                .s_x(s_x)
                .prefix(prefix)
                .n(n)
                .subtree(new TreeNode[(int)Math.pow(2, n)])
                .build();
    }



    public static Ciphertext trap(Field Zr, Element[] q){
        Element[] y = HashUtil.fillInArr(Zr, q, l);

        Element r = Zr.newRandomElement().getImmutable();
        int n = y.length;
        Element C = g.powZn(r).getImmutable();
        Element D = h.powZn(r).getImmutable();
        Element[] E = new Element[n];
        for(int i = 0; i < n; i++){
            E[i] = g.powZn(y[i]).mul(h_i[i].powZn(r)).getImmutable();
        }

        return new Ciphertext(y, C, D, E);
    }

    public static Boolean match(Field G1, Field Zr, TreeNode node, Ciphertext text, Element g){
        Element[] x = node.getX();
        Element s_x = node.getS_x();
        Element t_x = node.getT_x();
        int n = x.length;

        Element e = G1.newOneElement();
        for(int i = 0; i < n; i++){
            e = e.mul(text.getE()[i].powZn(x[i]));
        }
        Element p1 = e.getImmutable();

        Element p2 = text.getC().powZn(s_x).mul(text.getD().powZn(t_x)).getImmutable();
        Element left = p1.div(p2).getImmutable(); // Ex

//        Element[] y = text.getY();
        Element right = g.powZn(innerProduct(Zr, x, x)).getImmutable();

        log.info("IPFE Match:\nleft:{}\nright:{}", left, right);
        return left.isEqual(right);
    }


    public static Element innerProduct(Field Zr, Element[] x1, Element[] x2){
        Element product = Zr.newZeroElement();
        int n = x1.length;
        for(int i = 0; i < n; i++){
            product = product.add(x1[i].mul(x2[i]));
        }
        return product.getImmutable();
    }
}

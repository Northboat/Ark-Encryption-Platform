package cia.northboat.se.crypto.tree;

import cia.northboat.se.crypto.tree.model.Ciphertext;
import cia.northboat.se.crypto.tree.model.Point;
import cia.northboat.se.crypto.tree.model.TreeNode;
import cia.northboat.se.utils.HashUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.Random;

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
        test(tree);
    }


    public static void test(EncryptedTree tree){
        String[] str1 = new String[]{"nmsl", "wcnm", "wdnmd"};
        TreeNode t1 = tree.insert(str1);
//        System.out.println(t1);

        String[] str2 = new String[]{"nmsl", "sad", "wdnmd"};
        TreeNode t2 = tree.insert(str2);
        System.out.println(t1);
        System.out.println(t2);


        String[] query1 = new String[]{"nmsl", "sad", "wdnmd"};
        TreeNode q1 = tree.search(query1);
        System.out.println(q1);
    }

    public static void ipfeTest(){
        IPFEUtil.keygen(G1, Zr);
        Element g = IPFEUtil.getBase();
        Element[] x = HashUtil.hashStr2ZrArr(Zr, "101010", l);
        Element[] y = HashUtil.hashStr2ZrArr(Zr, "111111", l);


//        TreeNode node = IPFEUtil.enc(Zr, x, null, null);

        Ciphertext ciphertext = IPFEUtil.trap(Zr, y);

        // 再用 C, D, Ei 算 Ex
//        boolean flag = IPFEUtil.match(G1, Zr, node, ciphertext, g);

        // 然后看看两者是否一致
//        System.out.println("是否相等 = " + flag);
    }


//    public static long[] threadTest(Point[] O, EncryptedTree... trees){
//        int n = trees.length;
//        Object[] locks = new Object[n];
//        Thread[] threads = new Thread[n];
//        long[] s = new long[n], e = new long[n];
//
//
//        for(int i = 0; i < n; i++){
//            locks[i] = new Object();
//            int finalI = i;
//            threads[i] = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    s[finalI] = System.currentTimeMillis();
//                    for (EncryptedTree tree : trees) {
//                        tree.build(O);
//                    }
//                    e[finalI] = System.currentTimeMillis();
//                    synchronized (locks[finalI]) {//获取对象锁
//                        locks[finalI].notify();//子线程唤醒
//                    }
//                }
//            });
//        }
//
//        for(Thread thread: threads){
//            thread.start();
//        }
//
//        try {
//            for(Object lock: locks){
//                synchronized (lock) {//这里也是一样
//                    lock.wait();//主线程等待
//                }
//            }
//        } catch (InterruptedException exception) {
//            exception.printStackTrace();
//        }
//
//        long[] timespan = new long[n];
//        for(int i = 0; i < n; i++){
//            timespan[i] = e[i]-s[i];
//        }
//        return timespan;
//    }


    public Point[] getPoints(int numPoints) {
        double centerX = 0;
        double centerY = 0;
        double radius = 900;

        Random random = new Random();
        Point[] points = new Point[numPoints];

        for (int i = 0; i < numPoints; i++) {
            double r = radius * Math.sqrt(random.nextDouble()); // 开方保证均匀分布
            double theta = 2 * Math.PI * random.nextDouble();   // 角度从 0 到 2π

            double x = centerX + r * Math.cos(theta);
            double y = centerY + r * Math.sin(theta);

            points[i] = new Point((int) x, (int) y);
        }

        return points;
    }
}

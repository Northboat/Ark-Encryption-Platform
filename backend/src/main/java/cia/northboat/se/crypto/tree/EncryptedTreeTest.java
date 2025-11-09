package cia.northboat.se.crypto.tree;

import cia.northboat.se.crypto.tree.model.TreeNode;
import cia.northboat.se.utils.CsvReaderUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class EncryptedTreeTest {

    @Autowired
    private CsvReaderUtil csvReaderUtil;
    @Autowired
    private EncryptedTree TREE;

    private List<List<String>> data;

    @BeforeEach
    public void setup(){
        int dimension = 2, size = 5000;
        TREE.init(dimension);
        data = csvReaderUtil.readCsvWithRange("classpath:test_data/hi.csv", 0, dimension-1, size);
        System.out.println(data.size());
    }

    @Test
    public void insert(){
        String[] str1 = new String[]{"nmsl", "test", "wcnm", "wdnmd"};
        TreeNode t1 = TREE.insert(str1);

        String[] str2 = new String[]{"nmsl", "test", "sad", "wdnmd"};
        TreeNode t2 = TREE.insert(str2);

        System.out.println(t1);
        System.out.println(t2);

        System.out.println(TREE.getTreeStruct());
    }

    @Test
    public void build(){
        long cost = 0;
        for(int i = 0; i < 10; i++)
            cost += TREE.build(data);

        System.out.println(TREE.getTreeStruct());
        System.out.println(cost/10 + "ms");
    }




    @AfterEach
    public void search(){
        List<TreeNode> result = new ArrayList<>();

        int loop = 10;

        long s = System.currentTimeMillis();
        for(int i = 1; i <= loop; i++){
            result.add(TREE.search(data.get(490*i)));
        }
        long e = System.currentTimeMillis();

        System.out.println((e-s) / loop);
        assertEquals(loop, result.size());
    }
}

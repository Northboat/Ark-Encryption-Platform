package cia.northboat.se.service;

import cia.northboat.se.crypto.tree.EncryptedTree;
import cia.northboat.se.crypto.tree.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class IPFETreeService {

    private final EncryptedTree encryptedTree;
    @Autowired
    public IPFETreeService(EncryptedTree encryptedTree){
        this.encryptedTree = encryptedTree;
    }


    public Map<String, Object> buildTree(int count, int dimension){
        Map<String, Object> data = new HashMap<>();

        long s = System.currentTimeMillis();
        encryptedTree.clean();
        encryptedTree.setN(dimension);
        String tree = encryptedTree.randomBuild(count);
        long e = System.currentTimeMillis();
        data.put("time_cost", e-s);


        String htmlTreeStr = tree.replace("\n", "<br>");
        htmlTreeStr = htmlTreeStr.replace(" ", "&nbsp;");
        data.put("tree", htmlTreeStr);

        System.out.println("Tree Height: " + encryptedTree.getHeight());
        data.put("height", encryptedTree.getHeight());

        return data;
    }



    public Map<String, Object> search(String ... data){
        Map<String, Object> res = new HashMap<>();

        if(encryptedTree.getHeight() == 0){
            res.put("Error", "Please build tree first");
            return res;
        }

        long s = System.currentTimeMillis();
        // 匹配
        TreeNode target = encryptedTree.search(data);
        long e = System.currentTimeMillis();

        res.put("time_cost", e-s);
        res.put("target_node", target == null ? "null" : target);

//        data.put("Error", "Search haven't finished yet. Nothing seems to match here. I suspect there's an issue with the search logic. I'll fix it later when I have time. Maybe the matching formula is also problematic");

        return res;
    }
}

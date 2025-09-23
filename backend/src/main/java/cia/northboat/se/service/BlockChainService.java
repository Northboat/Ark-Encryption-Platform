package cia.northboat.se.service;

import cia.northboat.se.crypto.hash.SimpleMiner;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BlockChainService {
    private final SimpleMiner simpleMiner;

    public BlockChainService(SimpleMiner simpleMiner){
        this.simpleMiner = simpleMiner;
    }


    public Map<String, Object> mine(int difficulty){
        return simpleMiner.mine(difficulty);
    }
}

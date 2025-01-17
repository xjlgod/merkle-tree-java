package org.example.merkle;

import java.util.List;

/**
 * MerklePathResult 依次存储深度节点哈希，与是左节点还是右节点
 */
public class MerklePathResult {
    private List<byte[]> merklePath;
    private List<Integer> index;

    public MerklePathResult(List<byte[]> merklePath, List<Integer> index) {
        this.merklePath = merklePath;
        this.index = index;
    }

    public List<byte[]> getMerklePath() {
        return merklePath;
    }

    public List<Integer> getIndex() {
        return index;
    }
}

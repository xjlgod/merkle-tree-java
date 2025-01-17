package org.example.merkle;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * MerkleTree 是树的容器。它包含一个指向树的根的指针，一个指向叶节点的指针列表，以及默克尔根。
 */
public class MerkleTree {
    private Node root;
    private byte[] merkleRoot;
    private List<Node> leafs;
    private HashStrategy hashStrategy;
    private boolean sort;

    private MerkleTree(HashStrategy hashStrategy, boolean sort) {
        this.hashStrategy = hashStrategy;
        this.sort = sort;
    }

    /**
     * newTree 使用content接口构造默克尔树
     *
     * @param contentList
     * @param hashStrategy
     * @param sort
     * @return
     * @throws Exception
     */
    public static MerkleTree newTree(List<Content> contentList, HashStrategy hashStrategy,
                                     boolean sort) throws Exception {
        return new MerkleTree(hashStrategy, sort).buildTree(contentList);
    }

    /**
     * verifyTree 验证该树是否符合默克尔树标准
     *
     * @return
     * @throws Exception
     */
    public boolean verifyTree() throws Exception {
        byte[] calculatedMerkleRoot = root.verifyNode(sort);
        return Arrays.equals(merkleRoot, calculatedMerkleRoot);
    }

    /**
     * VerifyContent 指示给定的内容是否在树中，以及该内容的哈希值是否有效。如果期望的默克尔根等于在给定内容的关键路径上计算的默克尔根，则返回true。如果有效则返回true，否则返回false。
     *
     * @param content
     * @return
     * @throws Exception
     */
    public boolean verifyContent(Content content) throws Exception {
        for (Node leaf : leafs) {
            if (leaf.getContent().equals(content)) {
                Node currentParent = leaf.getParent();
                while (currentParent != null) {
                    byte[] leftHash = currentParent.getLeft().calculateNodeHash(sort);
                    byte[] rightHash = currentParent.getRight().calculateNodeHash(sort);

                    Hash hash = hashStrategy.createHash();
                    hash.write(sortAppend(sort, leftHash, rightHash));
                    byte[] calculatedHash = hash.sum(null);

                    if (!Arrays.equals(calculatedHash, currentParent.getHash())) {
                        return false;
                    }

                    currentParent = currentParent.getParent();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * RebuildTreeWith替换树的内容并进行完整的重建；虽然树的根将被替换，但MerkleTree在此操作中完全存活下来
     *
     * @param newContentList
     * @return
     * @throws Exception
     */
    public MerkleTree rebuildTree(List<Content> newContentList) throws Exception {
        return this.buildTree(newContentList);
    }

    /**
     * getMerklePath 返回给定内容的默克尔路径和索引
     * @param content
     * @return
     * @throws Exception
     */
    public MerklePathResult getMerklePath(Content content) throws Exception {
        for (Node current : leafs) {
            boolean ok = current.getContent().equals(content);
            if (!ok) {
                continue;
            }

            Node currentParent = current.getParent();
            List<byte[]> merklePath = new ArrayList<>();
            List<Integer> index = new ArrayList<>();

            while (currentParent != null) {
                if (Arrays.equals(currentParent.getLeft().getHash(), current.getHash())) {
                    merklePath.add(currentParent.getRight().getHash());
                    index.add(1); // 右节点
                } else {
                    merklePath.add(currentParent.getLeft().getHash());
                    index.add(0); // 左节点
                }
                current = currentParent;
                currentParent = currentParent.getParent();
            }

            return new MerklePathResult(merklePath, index);
        }
        return null;
    }

    private MerkleTree buildTree(List<Content> contentList) throws Exception {
        if (contentList == null || contentList.isEmpty()) {
            throw new IllegalArgumentException("Cannot construct tree with no content");
        }

        List<Node> leafNodes = new ArrayList<>();
        for (Content content : contentList) {
            byte[] hash = content.calculateHash();
            leafNodes.add(new Node(this, null, null, hash, content, true));
        }

        // 注意该默克尔树如果遇到奇节点的处理是使用重复节点
        if (leafNodes.size() % 2 == 1) {
            Node lastLeaf = leafNodes.get(leafNodes.size() - 1);
            leafNodes.add(new Node(this, null, null, lastLeaf.getHash(), lastLeaf.getContent(), true, true));
        }

        root = buildIntermediate(leafNodes);
        leafs = leafNodes;
        merkleRoot = root.getHash();

        return this;
    }

    /**
     * buildIntermediate 是一个辅助函数，对于给定的叶节点列表，它构建树的中间和根级别。返回树的结果根节点
     *
     * @param nodes
     * @return
     * @throws Exception
     */
    private Node buildIntermediate(List<Node> nodes) throws Exception {
        List<Node> newNodes = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i += 2) {
            Hash hash = hashStrategy.createHash();
            int right = i + 1 < nodes.size() ? i + 1 : i;

            byte[] combinedHash = sortAppend(sort, nodes.get(i).getHash(), nodes.get(right).getHash());
            hash.write(combinedHash);
            byte[] nodeHash = hash.sum(null);

            Node node = new Node(this, nodes.get(i), nodes.get(right), nodeHash, null, false);
            nodes.get(i).setParent(node);
            nodes.get(right).setParent(node);

            newNodes.add(node);
        }

        if (newNodes.size() == 1) {
            return newNodes.get(0);
        }

        return buildIntermediate(newNodes);
    }

    /**
     * 排序是为了确保默克尔树证明的一致性，避免由于节点顺序不同导致生成的哈希值不同，确保能与 OpenZeppelin 的 MerkleProof 合约兼容。
     *
     * @param sort
     * @param a
     * @param b
     * @return
     */
    public byte[] sortAppend(boolean sort, byte[] a, byte[] b) {
        if (!sort) {
            byte[] result = new byte[a.length + b.length];
            System.arraycopy(a, 0, result, 0, a.length);
            System.arraycopy(b, 0, result, a.length, b.length);
            return result;
        }

        BigInteger aBig = new BigInteger(1, a);
        BigInteger bBig = new BigInteger(1, b);

        if (aBig.compareTo(bBig) < 0) {
            byte[] result = new byte[a.length + b.length];
            System.arraycopy(a, 0, result, 0, a.length);
            System.arraycopy(b, 0, result, a.length, b.length);
            return result;
        }

        byte[] result = new byte[b.length + a.length];
        System.arraycopy(b, 0, result, 0, b.length);
        System.arraycopy(a, 0, result, b.length, a.length);
        return result;
    }


    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public byte[] getMerkleRoot() {
        return merkleRoot;
    }

    public void setMerkleRoot(byte[] merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    public List<Node> getLeafs() {
        return leafs;
    }

    public void setLeafs(List<Node> leafs) {
        this.leafs = leafs;
    }

    public HashStrategy getHashStrategy() {
        return hashStrategy;
    }

    public void setHashStrategy(HashStrategy hashStrategy) {
        this.hashStrategy = hashStrategy;
    }

    public boolean isSort() {
        return sort;
    }

    public void setSort(boolean sort) {
        this.sort = sort;
    }
}

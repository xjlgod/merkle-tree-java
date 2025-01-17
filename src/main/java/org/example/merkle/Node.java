package org.example.merkle;

import java.util.Arrays;

/**
 * Node 表示树中的节点、根或叶。它存储指向其直接关系的指针、散列、如果是叶子则存储的内容以及其他元数据
 */
public class Node {
    private MerkleTree tree;
    private Node parent;
    private Node left;
    private Node right;
    private boolean isLeaf;
    private boolean isDuplicate;
    private byte[] hash;
    private Content content;

    public Node(MerkleTree tree, Node left, Node right, byte[] hash, Content content, boolean isLeaf) {
        this.tree = tree;
        this.left = left;
        this.right = right;
        this.hash = hash;
        this.content = content;
        this.isLeaf = isLeaf;
        this.isDuplicate = false;
    }

    public Node(MerkleTree tree, Node left, Node right, byte[] hash, Content content, boolean isLeaf, boolean isDuplicate) {
        this(tree, left, right, hash, content, isLeaf);
        this.isDuplicate = isDuplicate;
    }

    public byte[] getHash() {
        return hash;
    }

    public Content getContent() {
        return content;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * verifyNode 沿着树向下走，直到碰到一个叶子节点，计算每一层的哈希值，并返回节点n的结果哈希值。
     * @param sort
     * @return
     * @throws Exception
     */
    public byte[] verifyNode(boolean sort) throws Exception {
        if (isLeaf) {
            return content.calculateHash();
        }

        byte[] leftBytes = left.verifyNode(sort);
        byte[] rightBytes = right.verifyNode(sort);

        Hash hash = tree.getHashStrategy().createHash();
        hash.write(tree.sortAppend(sort, leftBytes, rightBytes));
        return hash.sum(null);
    }

    /**
     * calculateNodeHash是计算节点哈希值的辅助函数
     *
     * @param sort
     * @return
     * @throws Exception
     */
    public byte[] calculateNodeHash(boolean sort) throws Exception {
        if (isLeaf) {
            return content.calculateHash();
        }

        Hash hash = tree.getHashStrategy().createHash();
        hash.write(tree.sortAppend(sort, left.hash, right.hash));
        return hash.sum(null);
    }

    @Override
    public String toString() {
        return "Leaf: " + isLeaf + " Dup: " + isDuplicate + " Hash: " + Arrays.toString(hash);
    }

    public MerkleTree getTree() {
        return tree;
    }

    public void setTree(MerkleTree tree) {
        this.tree = tree;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public boolean isDuplicate() {
        return isDuplicate;
    }

    public void setDuplicate(boolean duplicate) {
        isDuplicate = duplicate;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public void setContent(Content content) {
        this.content = content;
    }
}

package org.example.merkle;

import java.io.IOException;

/**
 * Hash 通用hash类，方便hash的扩展
 */
public interface Hash {
    /**
     * 向当前哈希中添加更多数据（通过嵌入的 OutputStream 接口）。
     * 它永远不会返回错误。
     * @param data 需要写入的数据
     * @throws IOException 如果写入操作失败，可能会抛出异常
     */
    void write(byte[] data) throws IOException;

    /**
     * 将当前哈希值附加到给定的字节数组 b 中，并返回最终的字节数组。
     * 这个方法不会改变哈希的内部状态。
     * @param b 要附加的字节数组
     * @return 当前哈希值附加后的字节数组
     */
    byte[] sum(byte[] b);

    /**
     * 重置哈希，恢复到初始状态。
     */
    void reset();

    /**
     * 返回哈希的字节长度。
     * @return 哈希的字节长度
     */
    int size();

    /**
     * 返回哈希算法的底层块大小。
     * Write 方法必须能够接受任意大小的数据，但如果所有写入数据都是块大小的倍数，可能会更高效。
     * @return 哈希算法的块大小
     */
    int blockSize();
}

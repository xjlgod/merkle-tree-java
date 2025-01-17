package org.example.merkle;

/**
 * Content 表示由树存储和验证的数据。实现此接口的类型可以用作树中的项
 */
public interface Content {
    byte[] calculateHash() throws Exception;
    boolean equals(Content other) throws Exception;
}

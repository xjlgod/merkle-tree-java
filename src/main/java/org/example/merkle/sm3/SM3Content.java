package org.example.merkle.sm3;

import org.example.merkle.Content;

import java.util.Arrays;

/**
 * SM3 内容实现
 */
public class SM3Content implements Content {
    private String content;

    public SM3Content(String content) {
        this.content = content;
    }

    @Override
    public byte[] calculateHash() throws Exception {
        SM3Hash sm3Hash = new SM3Hash();
        sm3Hash.write(content.getBytes());
        return sm3Hash.sum(null);
    }

    @Override
    public boolean equals(Content other) throws Exception {
        // SM3 哈希内容比较，冲突概率理论很小，看后期需要直接改为比较字符串不
        return Arrays.equals(calculateHash(), other.calculateHash());
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

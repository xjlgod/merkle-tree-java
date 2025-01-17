package org.example.merkle.sm3;

import org.example.merkle.Hash;
import org.example.merkle.HashStrategy;

import java.security.NoSuchAlgorithmException;

/**
 * SM3 哈希策略实现
 */
public class SM3Strategy implements HashStrategy {

    private SM3Strategy() {
    }

    public static SM3Strategy newInstance() {
        return new SM3Strategy();
    }

    @Override
    public Hash createHash() throws NoSuchAlgorithmException {
        return new SM3Hash();
    }
}

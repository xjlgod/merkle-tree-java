package org.example.merkle;

import java.security.NoSuchAlgorithmException;

/**
 * @author jingliu_xiong@foxmail.com
 */
public interface HashStrategy {
    Hash createHash() throws NoSuchAlgorithmException;
}

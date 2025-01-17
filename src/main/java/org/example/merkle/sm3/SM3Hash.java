package org.example.merkle.sm3;

import org.bouncycastle.crypto.digests.SM3Digest;
import org.example.merkle.Hash;

import java.io.IOException;

/**
 * SM3 digest 的 Hash包装
 */
public class SM3Hash implements Hash {
    private SM3Digest sm3Digest;

    public SM3Hash() {
        sm3Digest = new SM3Digest();
    }

    @Override
    public void write(byte[] input) throws IOException {
        // 用输入数据更新SM3摘要
        sm3Digest.update(input, 0, input.length);
    }

    @Override
    public byte[] sum(byte[] b) {
        // 使用新digest进行计算，不改变原有digest
        byte[] result = new byte[sm3Digest.getDigestSize()];
        SM3Digest temp = new SM3Digest(sm3Digest);
        temp.doFinal(result, 0);
        if (b == null) {
            return result;
        }

        byte[] combined = new byte[b.length + result.length];
        System.arraycopy(b, 0, combined, 0, b.length);
        System.arraycopy(result, 0, combined, b.length, result.length);
        return combined;
    }

    @Override
    public void reset() {
        // 重置 digest
        sm3Digest.reset();
    }

    @Override
    public int size() {
        // SM3 输出 256 位，即 32 字节
        return sm3Digest.getDigestSize();
    }

    @Override
    public int blockSize() {
        // SM3 的块大小是 512 位（64 字节）
        return 64;
    }
}

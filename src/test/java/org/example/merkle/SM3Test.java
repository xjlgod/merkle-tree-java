package org.example.merkle;

import org.example.merkle.sm3.SM3Content;
import org.example.merkle.sm3.SM3Strategy;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * SM3Test
 */
public class SM3Test {
    private static final List<TestCase> table = Arrays.asList(
            new TestCase(0,  false, Arrays.asList(
                    new SM3Content("Hello"),
                    new SM3Content("Hi"),
                    new SM3Content("Hey"),
                    new SM3Content("Hola")),
                    new SM3Content("NotInTestTable"),
                    new byte[]{-98, 122, -6, -1, 92, -127, 10, -104, -84,
                            96, 71, 87, 71, 41, -121, 86, -108, -26, -34, -72, -109, 34, 120, 0, -121, 115, -42, 47, -62, -32, 21, 126}),

            new TestCase(1, false, Arrays.asList(
                    new SM3Content("Hello"),
                    new SM3Content("Hi"),
                    new SM3Content("Hey")),
                    new SM3Content("NotInTestTable"),
                    new byte[]{-80, 58, 93, -11, -18, -41, 33, -116, 8, 20, 26, -31, -10, 123, 108, -119, 20, -39, -62, 66,
                            -35, 84, 48, -118, 31, -64, 3, -43, 3, -23, 58, 14}),

            new TestCase(2, false, Arrays.asList(
                    new SM3Content("Hello"),
                    new SM3Content("Hi"),
                    new SM3Content("Hey"),
                    new SM3Content("I"),
                    new SM3Content("am"),
                    new SM3Content("fine")),
                    new SM3Content("NotInTestTable"),
                    new byte[]{-95, 42, 70, -94, -40, 68, -80, -65, 20, 89, -9, -121, 87, 53, 123, -71, -47, -21, -53, 8,
                    -110, 91, -99, -63, 110, -27, -56, 63, 23, -6, -71, -116})
    );

    public static class TestCase {
        int testCaseId;
        boolean sort;
        List<Content> contents;
        Content notInContents;
        byte[] expectedHash;

        TestCase(int testCaseId, boolean sort,
                 List<Content> contents, SM3Content notInContents, byte[] expectedHash) {
            this.testCaseId = testCaseId;
            this.sort = sort;
            this.contents = contents;
            this.notInContents = notInContents;
            this.expectedHash = expectedHash;
        }
    }

    @Test
    public void testMerkleTree_MerkleRoot() throws Exception {
        for (TestCase testCase : table) {

            MerkleTree tree = MerkleTree.newTree(testCase.contents, SM3Strategy.newInstance(), testCase.sort);
            byte[] actualHash = tree.getMerkleRoot();

            assertArrayEquals("Test case " + testCase.testCaseId + " failed", testCase.expectedHash, actualHash);
        }
    }

    @Test
    public void testMerkleTree_RebuildTree() throws Exception {
        for (TestCase testCase : table) {
            MerkleTree tree = MerkleTree.newTree(testCase.contents, SM3Strategy.newInstance(), testCase.sort);
            tree.rebuildTree(testCase.contents);
            byte[] actualHash = tree.getMerkleRoot();
            System.out.println(actualHash);

            assertArrayEquals("Test case " + testCase.testCaseId + " failed", testCase.expectedHash, actualHash);
        }
    }

    @Test
    public void testMerkleTree_VerifyTree() throws Exception {
        for (TestCase testCase : table) {
            MerkleTree tree = MerkleTree.newTree(testCase.contents, SM3Strategy.newInstance(), testCase.sort);
            boolean isValid = tree.verifyTree();

            assertTrue("Test case " + testCase.testCaseId + " failed, expected valid tree", isValid);
            Class<?> clazz = tree.getClass();
            Field rootHashField = clazz.getDeclaredField("merkleRoot");
            rootHashField.setAccessible(true);
            rootHashField.set(tree, "newHash".getBytes());
            boolean isInvalid = tree.verifyTree();

            assertFalse("Test case " + testCase.testCaseId + " failed, expected invalid tree", isInvalid);
        }
    }

    @Test
    public void testMerkleTree_VerifyContent() throws Exception {
        for (TestCase testCase : table) {
            MerkleTree tree = MerkleTree.newTree(testCase.contents, SM3Strategy.newInstance(), testCase.sort);
            boolean isValid = tree.verifyContent(testCase.contents.get(0));

            assertTrue("Test case " + testCase.testCaseId + " failed, expected valid content", isValid);
        }

        for (TestCase testCase : table) {
            MerkleTree tree = MerkleTree.newTree(testCase.contents, SM3Strategy.newInstance(), testCase.sort);
            boolean isValid = tree.verifyContent(new SM3Content("fake content"));

            assertFalse("Test case " + testCase.testCaseId + " failed, expected valid content", isValid);
        }
    }

    @Test
    public void testMerkleTree_GetMerklePath() throws Exception {
        for (TestCase testCase : table) {
            MerkleTree tree = MerkleTree.newTree(testCase.contents, SM3Strategy.newInstance(), testCase.sort);

            MerklePathResult result1 = tree.getMerklePath(testCase.contents.get(0));
            assertNotNull(result1);

            MerklePathResult result2 = tree.getMerklePath(new SM3Content("fake content"));
            assertNull(result2);
        }
    }
}

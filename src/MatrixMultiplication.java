import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class MatrixMultiplication {
    public static void main(String[] args) {
//        sanityCheck();
        int n = 8;
        run(generateMatrix(n), generateMatrix(n), n);
    }

    /**
     * Sanity check based on matrices from assignment.
     */
    public static void sanityCheck(){
        int[][] matrixA = new int[][] {
                {2, 0, -1, 6},
                {3, 7, 8, 0},
                {-5, 1, 6, -2},
                {8, 0, 1, 7}
        };
        int[][] matrixB = new int[][] {
                {0, 1, 6, 3},
                {-2, 8, 7, 1},
                {2, 0, -1, 0},
                {9, 1, 6, -2}
        };

        run(matrixA, matrixB, 4);
    }

    /**
     * Run all three matrix multiplication algorithms with the provided matrices.
     *
     * @param matrixA first input matrix
     * @param matrixB second input matrix
     */
    public static void run(int[][] matrixA, int[][] matrixB, int n) {
        // Print input matrices
        System.out.println("Matrix A:");
        printMatrix(matrixA);
        System.out.println("Matrix B:");
        printMatrix(matrixB);

        // Brute Force Algorithm (Calculate runtime and print resulting matrix)
        long startTime = System.nanoTime();
        int[][] bruteForceResult = bruteForce(matrixA, matrixB);
        System.out.printf("Brute Force elapsed time for n=%d: %f seconds\n", n, (System.nanoTime()-startTime)/(float)1000000000);
        printMatrix(bruteForceResult);

        // Naive Divide and Conquer Algorithm (Calculate runtime and print resulting matrix)
        startTime = System.nanoTime();
        int[][] divideAndConquerResult = divideAndConquer(padMatrix(matrixA), padMatrix(matrixB));
        System.out.printf("Divide and Conquer elapsed time for n=%d: %f seconds\n", n, (System.nanoTime()-startTime)/(float)1000000000);
        printMatrix(divideAndConquerResult);

        // Strassen's Algorithm (Calculate runtime and print resulting matrix)
        startTime = System.nanoTime();
        int[][] strassenResult = strassen(padMatrix(matrixA), padMatrix(matrixB));
        System.out.printf("Strassen elapsed time for n=%d: %f seconds\n", n, (System.nanoTime()-startTime)/(float)1000000000);
        printMatrix(strassenResult);

        // Compare Divide and Conquer algorithm results against brute force results to test accuracy
        System.out.printf("Divide and Conquer == Brute Force: %b\n", Arrays.deepEquals(bruteForceResult, depadMatrix(divideAndConquerResult, n)));
        System.out.printf("Strassen's Algorithm == Brute Force: %b\n", Arrays.deepEquals(bruteForceResult, depadMatrix(strassenResult, n)));
    }


    /**
     *  Multiply matrices using three nested for loops. O(n^3)
     *
     * @param matrixA first input matrix
     * @param matrixB second input matrix
     * @return the resulting matrix
     */
    public static int[][] bruteForce(int[][] matrixA, int[][] matrixB) {
        int n = matrixA.length;
        int[][] resultMatrix = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int sum = 0;
                for (int k = 0; k < n; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }
                resultMatrix[i][j] = sum;
            }
        }

        return resultMatrix;
    }

    /**
     * Initial divide and conquer call. O(n^3)
     *
     * Calls divideAndConquerHelper method to compute the matrix multiplication.
     * Time complexity comes from the called divideAndConquerHelper method.
     *
     * @param matrixA first input matrix
     * @param matrixB second input matrix
     * @return the resulting matrix
     */
    public static int[][] divideAndConquer(int[][] matrixA, int[][] matrixB) {
        return divideAndConquerHelper(matrixA, 0, 0, matrixB, 0, 0, matrixA.length);
    }

    /**
     * Multiply matrices using recursion. O(n^3)
     *
     * @param matrixA first input matrix
     * @param aRowOfs starting row for matrixA
     * @param aColOfs starting column for matrixA
     * @param matrixB second input matrix
     * @param bRowOfs starting row for matrixB
     * @param bColOfs starting column for matrixB
     * @param n size of quadrant to compute
     * @return the resulting matrix
     */
    private static int[][] divideAndConquerHelper(int[][] matrixA, int aRowOfs, int aColOfs,
                                                  int[][] matrixB, int bRowOfs, int bColOfs, int n) {
        int[][] resultMatrix = new int[n][n];

        if (n == 1) { // base case
            resultMatrix[0][0] = matrixA[aRowOfs][aColOfs] * matrixB[bRowOfs][bColOfs];
        } else { // partition
            int nDiv2 = n/2;

            // C11 = A11*B11 + A12*B21
            matrixAddition(divideAndConquerHelper(matrixA, aRowOfs, aColOfs, matrixB, bRowOfs, bColOfs, nDiv2),
                           divideAndConquerHelper(matrixA, aRowOfs, aColOfs+nDiv2, matrixB, bRowOfs+nDiv2, bColOfs, nDiv2),
                           resultMatrix, 0, 0);

            // C12 = A11*B12 + A12*B22
            matrixAddition(divideAndConquerHelper(matrixA, aRowOfs, aColOfs, matrixB, bRowOfs, bColOfs+nDiv2, nDiv2),
                           divideAndConquerHelper(matrixA, aRowOfs, aColOfs+nDiv2, matrixB, bRowOfs+nDiv2, bColOfs+nDiv2, nDiv2),
                           resultMatrix, 0, nDiv2);

            // C21 = A21*B11 + A22*B21
            matrixAddition(divideAndConquerHelper(matrixA, aRowOfs+nDiv2, aColOfs, matrixB, bRowOfs, bColOfs, nDiv2),
                           divideAndConquerHelper(matrixA, aRowOfs+nDiv2, aColOfs+nDiv2, matrixB, bRowOfs+nDiv2, bColOfs, nDiv2),
                           resultMatrix, nDiv2, 0);

            // C22 = A21*B12 + A22*B22
            matrixAddition(divideAndConquerHelper(matrixA, aRowOfs+nDiv2, aColOfs, matrixB, bRowOfs, bColOfs+nDiv2, nDiv2),
                           divideAndConquerHelper(matrixA, aRowOfs+nDiv2, aColOfs+nDiv2, matrixB, bRowOfs+nDiv2, bColOfs+nDiv2, nDiv2),
                           resultMatrix, nDiv2, nDiv2);

        }
        return resultMatrix;
    }

    /**
     * Initial Strassen call. O(n^log7)
     *
     * Calls strassenHelper method to compute the matrix multiplication.
     * Time complexity comes from the called strassenHelper method.
     *
     * @param matrixA first input matrix
     * @param matrixB second input matrix
     * @return the resulting matrix
     */
    public static int[][] strassen(int[][] matrixA, int[][] matrixB) {
        return strassenHelper(matrixA, 0, 0, matrixB, 0, 0, matrixA.length);
    }

    /**
     * Multiply matrices using optimal recursion. O(n^log7)
     *
     * @param matrixA first input matrix
     * @param aRowOfs starting row for matrixA
     * @param aColOfs starting column for matrixA
     * @param matrixB second input matrix
     * @param bRowOfs starting row for matrixB
     * @param bColOfs starting column for matrixB
     * @param n size of quadrant to compute
     * @return the resulting matrix
     */
    private static int[][] strassenHelper(int[][] matrixA, int aRowOfs, int aColOfs,
                                          int[][] matrixB, int bRowOfs, int bColOfs, int n) {
        int[][] resultMatrix = new int[n][n];

        if (n == 1) { // base case
            resultMatrix[0][0] = matrixA[aRowOfs][aColOfs] * matrixB[bRowOfs][bColOfs];
        } else { // partition
            int nDiv2 = n / 2;
            int[][] tmpMatrix = new int[nDiv2][nDiv2];
            int[][] tmpMatrix2 = new int[nDiv2][nDiv2];

            // P1 = A11 * (B12 – B22)
            matrixSubtraction(matrixB, bRowOfs, bColOfs+nDiv2,
                              matrixB, bRowOfs+nDiv2, bColOfs+nDiv2,
                              tmpMatrix, 0, 0, nDiv2);
            int[][] p1 = strassenHelper(matrixA, aRowOfs, aColOfs, tmpMatrix, 0, 0, nDiv2);

            // P2 = (A11 + A12) * B22
            matrixAddition(matrixA, aRowOfs, aColOfs,
                           matrixA, aRowOfs, aColOfs+nDiv2,
                           tmpMatrix, 0, 0, nDiv2);
            int[][] p2 = strassenHelper(tmpMatrix, 0, 0, matrixB, bRowOfs+nDiv2, bColOfs+nDiv2, nDiv2);

            // P3 = (A21 + A22) *  B11
            matrixAddition(matrixA, aRowOfs+nDiv2, aColOfs,
                           matrixA, aRowOfs+nDiv2, aColOfs+nDiv2,
                           tmpMatrix, 0, 0, nDiv2);
            int[][] p3 = strassenHelper(tmpMatrix, 0, 0, matrixB, bRowOfs, bColOfs, nDiv2);

            // P4 = A22 * (B21 – B11)
            matrixSubtraction(matrixB, bRowOfs+nDiv2, bColOfs,
                              matrixB, bRowOfs, bColOfs,
                              tmpMatrix, 0, 0, nDiv2);
            int[][] p4 = strassenHelper(matrixA, aRowOfs+nDiv2, aColOfs+nDiv2, tmpMatrix, 0, 0, nDiv2);

            // P5 = (A11 + A22) * (B11 + B22)
            matrixAddition(matrixA, aRowOfs, aColOfs,
                           matrixA, aRowOfs+nDiv2, aColOfs+nDiv2,
                           tmpMatrix, 0, 0, nDiv2);
            matrixAddition(matrixB, bRowOfs, bColOfs,
                           matrixB, bRowOfs+nDiv2, bColOfs+nDiv2,
                           tmpMatrix2, 0, 0, nDiv2);
            int[][] p5 = strassenHelper(tmpMatrix, 0, 0, tmpMatrix2, 0, 0, nDiv2);

            // P6 = (A12 – A22) * (B21 + B22)
            matrixSubtraction(matrixA, aRowOfs, aColOfs+nDiv2,
                              matrixA, aRowOfs+nDiv2, aColOfs+nDiv2,
                              tmpMatrix, 0, 0, nDiv2);
            matrixAddition(matrixB, bRowOfs+nDiv2, bColOfs,
                           matrixB, bRowOfs+nDiv2, bColOfs+nDiv2,
                           tmpMatrix2, 0, 0, nDiv2);
            int[][] p6 = strassenHelper(tmpMatrix, 0, 0, tmpMatrix2, 0, 0, nDiv2);

            // P7 = (A11 – A21) * (B11 + B12)
            matrixSubtraction(matrixA, aRowOfs, aColOfs,
                              matrixA, aRowOfs+nDiv2, aColOfs,
                              tmpMatrix, 0, 0, nDiv2);
            matrixAddition(matrixB, bRowOfs, bColOfs,
                           matrixB, bRowOfs, bColOfs+nDiv2,
                           tmpMatrix2, 0, 0, nDiv2);
            int[][] p7 = strassenHelper(tmpMatrix, 0, 0, tmpMatrix2, 0, 0, nDiv2);


            // C11 = -P2 + P4 + P5 + P6
            matrixAddition(matrixSubtraction(p4, p2, tmpMatrix, 0, 0),
                           matrixAddition(p5, p6, tmpMatrix2, 0, 0),
                           resultMatrix, 0, 0);

            // C12 = P1 + P2
            matrixAddition(p1, p2, resultMatrix, 0, nDiv2);

            // C21 = P3 + P4
            matrixAddition(p3, p4, resultMatrix, nDiv2, 0);

            // C22 = P1 - P3 + P5 - P7
            matrixAddition(matrixSubtraction(p1, p3, tmpMatrix, 0, 0),
                           matrixSubtraction(p5, p7, tmpMatrix2, 0, 0),
                           resultMatrix, nDiv2, nDiv2);

        }
        return resultMatrix;
    }


    /**
     * Adds two matrices. O(n^2)
     *
     * Calls other matrixAddition method (filling in missing params with 0) to compute the matrix addition.
     * Time complexity comes from the called matrixAddition method.
     *
     * @param matrixA first input matrix
     * @param matrixB second input matrix
     * @param resultMatrix where to put the resulting matrix
     * @param resultRowOfs starting row for resultMatrix
     * @param resultColOfs starting column for resultMatrix
     * @return resultMatrix
     */
    public static int[][] matrixAddition(int[][] matrixA, int[][] matrixB, int[][] resultMatrix, int resultRowOfs, int resultColOfs) {
        return matrixAddition(matrixA, 0, 0, matrixB, 0, 0,
                              resultMatrix, resultRowOfs, resultColOfs, matrixA.length);
    }

    /**
     * Subtracts two matrices. O(n^2)
     *
     * Calls other matrixSubtraction method (filling in missing params with 0) to compute the matrix subtraction.
     * Time complexity comes from the called matrixSubtraction method.
     *
     * @param matrixA first input matrix
     * @param matrixB second input matrix
     * @param resultMatrix where to put the resulting matrix
     * @param resultRowOfs starting row for resultMatrix
     * @param resultColOfs starting column for resultMatrix
     * @return resultMatrix
     */
    public static int[][] matrixSubtraction(int[][] matrixA, int[][] matrixB, int[][] resultMatrix, int resultRowOfs, int resultColOfs) {
        return matrixSubtraction(matrixA, 0, 0, matrixB, 0, 0,
                                 resultMatrix, resultRowOfs, resultColOfs, matrixA.length);
    }

    /**
     * Adds two matrices. O(n^2)
     *
     * @param matrixA first input matrix
     * @param aRowOfs starting row for matrixA
     * @param aColOfs starting column for matrixA
     * @param matrixB second input matrix
     * @param bRowOfs starting row for matrixB
     * @param bColOfs starting column for matrixB
     * @param resultMatrix where to put the resulting matrix
     * @param resultRowOfs starting row for resultMatrix
     * @param resultColOfs starting column for resultMatrix
     * @param n size of quadrant to compute
     * @return resultMatrix
     */
    public static int[][] matrixAddition(int[][] matrixA, int aRowOfs, int aColOfs,
                                         int[][] matrixB, int bRowOfs, int bColOfs,
                                         int[][] resultMatrix, int resultRowOfs, int resultColOfs, int n) {
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                resultMatrix[resultRowOfs+row][resultColOfs+col] = matrixA[aRowOfs + row][aColOfs + col] + matrixB[bRowOfs + row][bColOfs + col];
            }
        }

        return resultMatrix;
    }

    /**
     * Subtracts two matrices. O(n^2)
     *
     * @param matrixA first input matrix
     * @param aRowOfs starting row for matrixA
     * @param aColOfs starting column for matrixA
     * @param matrixB second input matrix
     * @param bRowOfs starting row for matrixB
     * @param bColOfs starting column for matrixB
     * @param resultMatrix where to put the resulting matrix
     * @param resultRowOfs starting row for resultMatrix
     * @param resultColOfs starting column for resultMatrix
     * @param n size of quadrant to compute
     * @return resultMatrix
     */
    public static int[][] matrixSubtraction(int[][] matrixA, int aRowOfs, int aColOfs,
                                            int[][] matrixB, int bRowOfs, int bColOfs,
                                            int[][] resultMatrix, int resultRowOfs, int resultColOfs, int n) {
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                resultMatrix[resultRowOfs+row][resultColOfs+col] = matrixA[aRowOfs + row][aColOfs + col] - matrixB[bRowOfs + row][bColOfs + col];
            }
        }

        return resultMatrix;
    }

    /**
     * Pad a 2D matrix so that its size is a power of 2. O(n^2)
     *
     * @param matrix the 2D matrix that needs padding
     * @return the padded 2D matrix
     */
    public static int[][] padMatrix(int[][] matrix) {
        // Find next power of 2
        int n = matrix.length;
        int pow = 32 - Integer.numberOfLeadingZeros(n-1);
        int n_pow_2 = (int) Math.pow(2, pow);

        if (n == n_pow_2) { // No need to pad the matrix
            return matrix;
        }

        // Initialize new larger array and copy over values
        int[][] paddedMatrix = new int[n_pow_2][n_pow_2];
        for (int row = 0; row < n; row++) {
            System.arraycopy(matrix[row], 0, paddedMatrix[row], 0, n);
        }

        return paddedMatrix;
    }

    // ----------------------------------------------------
    // ----------------- HELPER FUNCTIONS -----------------
    // ----------------------------------------------------

    // Removes padding from matrix
    // Only used to compare D&C arrays against the brute force array as a sanity check
    public static int[][] depadMatrix(int[][] matrix, int n) {
        int n_pow_2 = matrix.length;

        if (n == n_pow_2) { // No need to de-pad the matrix
            return matrix;
        }

        // Initialize new smaller array and copy over values
        int[][] depaddedMatrix = new int[n][n];
        for (int row = 0; row < n; row++) {
            System.arraycopy(matrix[row], 0, depaddedMatrix[row], 0, n);
        }

        return depaddedMatrix;
    }

    // Randomly generates matrix of specified size
    public static int[][] generateMatrix(int n) {
        int[][] matrix = new int[n][n];

        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                matrix[row][col] = ThreadLocalRandom.current().nextInt(-9, 10);
            }
        }
        return matrix;
    }

    // Formats and prints matrix
    public static void printMatrix(int[][] matrix) {
        for (int[] rows : matrix) {
            for (int elem : rows)
                System.out.printf("%5d", elem);
            System.out.println();
        }
        System.out.println();
    }
}

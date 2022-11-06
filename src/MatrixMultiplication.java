import java.util.concurrent.ThreadLocalRandom;

public class MatrixMultiplication {
    public static void main(String[] args) {
        int n = 13;
        run(n);
        System.out.println();
        System.out.println("========================================================");
        System.out.println();
        run(n);
        System.out.println();
        run(n);
        System.out.println();
        run(n);
        System.out.println();
        run(n);
        System.out.println();
        run(n);

/*        int[][] matrix1 = generateMatrix(n);
        int[][] matrix2 = generateMatrix(n);
//        printMatrix(matrix1, n);
//        printMatrix(matrix2, n);

        long startTime = System.nanoTime();
        int[][] bruteForceResult = bruteForce(matrix1, matrix2);
        System.out.printf("Brute Force elapsed time for n=%d: %f seconds\n\n", n, (System.nanoTime()-startTime)/(float)1000000000);
//        printMatrix(bruteForceResult, n);

        startTime = System.nanoTime();
        int[][] divideAndConquerResult = divideAndConquer(padMatrix(matrix1), padMatrix(matrix2));
        System.out.printf("Divide and Conquer elapsed time for n=%d: %f seconds\n\n", n, (System.nanoTime()-startTime)/(float)1000000000);
//        printMatrix(divideAndConquerResult, n);

        startTime = System.nanoTime();
        int[][] strassenResult = strassen(padMatrix(matrix1), padMatrix(matrix2));
        System.out.printf("Strassen elapsed time for n=%d: %f seconds\n\n", n, (System.nanoTime()-startTime)/(float)1000000000);
//        printMatrix(strassenResult, n);

//        System.out.printf("Divide and Conquer == Brute Force: %b\n", Arrays.deepEquals(bruteForceResult, depadMatrix(divideAndConquerResult, n)));
//        System.out.printf("Strassen's Algorithm == Brute Force: %b\n", Arrays.deepEquals(bruteForceResult, depadMatrix(strassenResult, n)));*/
    }

    public static void run(int n) {
        int[][] matrix1 = generateMatrix(n);
        int[][] matrix2 = generateMatrix(n);
        //        printMatrix(matrix1, n);
        //        printMatrix(matrix2, n);

        // test
//        matrixAddition(matrix1, matrix2, new int[n][n], 0, 0);
        padMatrix(matrix1);

        long startTime = System.nanoTime();
        int[][] bruteForceResult = bruteForce(matrix1, matrix2);
        System.out.printf("Brute Force elapsed time for n=%d: %f seconds\n\n", n, (System.nanoTime()-startTime)/(float)1000000000);
        //        printMatrix(bruteForceResult, n);

        startTime = System.nanoTime();
        int[][] divideAndConquerResult = divideAndConquer(padMatrix(matrix1), padMatrix(matrix2));
        System.out.printf("Divide and Conquer elapsed time for n=%d: %f seconds\n\n", n, (System.nanoTime()-startTime)/(float)1000000000);
        //        printMatrix(divideAndConquerResult, n);

        startTime = System.nanoTime();
        int[][] strassenResult = strassen(padMatrix(matrix1), padMatrix(matrix2));
        System.out.printf("Strassen elapsed time for n=%d: %f seconds\n\n", n, (System.nanoTime()-startTime)/(float)1000000000);
        //        printMatrix(strassenResult, n);

    }

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

    public static int[][] divideAndConquer(int[][] matrixA, int[][] matrixB) {
        return divideAndConquerHelper(matrixA, 0, 0, matrixB, 0, 0, matrixA.length);
    }

    public static int[][] divideAndConquerHelper(int[][] matrixA, int aRowOfs, int aColOfs, int[][] matrixB,
                                                 int bRowOfs, int bColOfs, int n) {
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

    public static int[][] strassen(int[][] matrixA, int[][] matrixB) {
        return strassenHelper(matrixA, 0, 0, matrixB, 0, 0, matrixA.length);
    }

    public static int[][] strassenHelper(int[][] matrixA, int aRowOfs, int aColOfs, int[][] matrixB,
                                         int bRowOfs, int bColOfs, int n) {
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


    // O(n^2)
    public static int[][] matrixAddition(int[][] matrixA, int[][] matrixB, int[][] resultMatrix, int resultRowOfs, int resultColOfs) {
        return matrixAddition(matrixA, 0, 0, matrixB, 0, 0,
                              resultMatrix, resultRowOfs, resultColOfs, matrixA.length);
    }

    // O(n^2)
    public static int[][] matrixSubtraction(int[][] matrixA, int[][] matrixB, int[][] resultMatrix, int resultRowOfs, int resultColOfs) {
        return matrixSubtraction(matrixA, 0, 0, matrixB, 0, 0,
                                 resultMatrix, resultRowOfs, resultColOfs, matrixA.length);
    }


    // O(n^2)
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

    // O(n^2)
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
     * Pad a 2D matrix so that its size is a power of 2.
     *
     * O(n^2); where n is the size of the original matrix
     *
     * @param matrix the 2D matrix that needs padding
     * @return the padded 2D matrix
     */
    public static int[][] padMatrix(int[][] matrix) {
        int n = matrix.length;
        int pow = 32 - Integer.numberOfLeadingZeros(n-1);
        int n_pow_2 = (int) Math.pow(2, pow);

        if (n == n_pow_2) { // No need to pad the matrix
            return matrix;
        }
        
        int[][] paddedMatrix = new int[n_pow_2][n_pow_2];

        for (int row = 0; row < n; row++) {
            System.arraycopy(matrix[row], 0, paddedMatrix[row], 0, n);
        }

        return paddedMatrix;
    }

    // ----------------------------------------------------
    // ----------------- HELPER FUNCTIONS -----------------
    // ----------------------------------------------------
    public static int[][] depadMatrix(int[][] matrix, int n) {
        int n_pow_2 = matrix.length;

        if (n == n_pow_2) { // No need to pad the matrix
            return matrix;
        }

        int[][] depaddedMatrix = new int[n][n];

        for (int row = 0; row < n; row++) {
            System.arraycopy(matrix[row], 0, depaddedMatrix[row], 0, n);
        }

        return depaddedMatrix;
    }

    public static int[][] generateMatrix(int n) {
        int[][] matrix = new int[n][n];

        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                matrix[row][col] = ThreadLocalRandom.current().nextInt(-9, 10);
            }
        }
        return matrix;
    }


    public static void printMatrix(int[][] matrix, int n) {
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                System.out.printf("%5d", matrix[row][col]);
            }
            System.out.println();
        }
        System.out.println();

//        System.out.println(Arrays.deepToString(matrix)
//                                   .replace("], ", "]\n")
//                                   .replace("[[", "[")
//                                   .replace("]]", "]\n"));

//        System.out.println(Arrays.deepToString(matrix)
//                                   .replace("], ", "\n")
//                                   .replace("[", "")
//                                   .replace(",", "")
//                                   .replace("]]", "\n"));
    }
}

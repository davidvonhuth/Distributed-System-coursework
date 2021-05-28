package com.example.grpc.client.grpcclient;

public class Matrices{
        private int[][] matrix1;
        private int[][] matrix2;

        public int[][] getMatrix1(){
                return matrix1;
        }

        public void setMatrix1(int[][] matrix){
                this.matrix1 = matrix;
        }

        public int[][] getMatrix2(){
                return matrix2;
        }

        public void setMatrix2(int[][] matrix){
                this.matrix2 = matrix;
        }
}

package com.MiTi.MiTi.utiil;

import java.util.Arrays;

public class CosineSimilarity {

    public double calculate(double[] vectorA, double[] vectorB) {
        double dotProduct = dotProduct(vectorA, vectorB);
        double normA = norm(vectorA);
        double normB = norm(vectorB);
        return dotProduct / (normA * normB);
    }

    private double dotProduct(double[] vectorA, double[] vectorB) {
        return Arrays.stream(vectorA)
                .reduce(0, (acc, v) -> acc + v * vectorB[Arrays.asList(vectorA).indexOf(v)]);
    }

    private double norm(double[] vector) {
        return Math.sqrt(Arrays.stream(vector).map(v -> v * v).sum());
    }
}
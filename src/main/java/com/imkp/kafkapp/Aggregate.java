package com.imkp.kafkapp;

import lombok.Data;

@Data
public class Aggregate {
    private String factor;
    private long count = 0L;
    private double sum = 0.0d;
    private double min = Double.MAX_VALUE;
    private double max = Double.MIN_VALUE;

    public static Aggregate of(String factor) {
        Aggregate aggregate = new Aggregate();
        aggregate.setFactor(factor);
        return aggregate;
    }

    public Aggregate update(Double newValue) {
        count++;
        sum += newValue;
        min = Math.min(min, newValue);
        max = Math.max(max, newValue);
        return this;
    }
}

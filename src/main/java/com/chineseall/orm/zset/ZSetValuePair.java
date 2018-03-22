package com.chineseall.orm.zset;

/**
 * Created by wangqiang on 2018/3/20.
 */
public class ZSetValuePair {
    private String value;
    private Double score;

    public ZSetValuePair(String value, Double score){
        this.value = value;
        this.score = score;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}

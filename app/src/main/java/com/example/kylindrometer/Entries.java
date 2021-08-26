package com.example.kylindrometer;

public class Entries {
    public String cur_date;
    public String cur_weight;
    public Entries(String d, String w) {
        this.cur_date = d;
        this.cur_weight = w;
    }
    public Entries() {
        this.cur_date = "";
        this.cur_weight = "";
    }
}

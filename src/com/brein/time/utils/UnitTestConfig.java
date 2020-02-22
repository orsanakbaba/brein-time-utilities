package com.brein.time.utils;

public class UnitTestConfig {
    public String owner= "owner1";
    public String severity = "severity1";
    public String issueId = "KOVAN-123";


    public UnitTestConfig(String[] args) {
        this.owner = args[0];
        this.severity = args[1];
        this.issueId = args[2];
    }

    public UnitTestConfig() {
    }
}

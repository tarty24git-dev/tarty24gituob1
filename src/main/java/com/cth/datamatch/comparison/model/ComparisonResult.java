package com.cth.datamatch.comparison.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComparisonResult {
    private String jobName;
    private int totalFile1Rows;
    private int totalFile2Rows;
    private int matchedCount;
    private int mismatchedCount;
    private int unmatchedFile1Count;
    private int unmatchedFile2Count;

    private List<Map<String, String>> matchedRows = new ArrayList<>();
    private List<Map<String, String>> mismatchedRows = new ArrayList<>();
    private List<Map<String, String>> unmatchedFile1Rows = new ArrayList<>();
    private List<Map<String, String>> unmatchedFile2Rows = new ArrayList<>();

    // Getters & Setters
    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }

    public int getTotalFile1Rows() { return totalFile1Rows; }
    public void setTotalFile1Rows(int totalFile1Rows) { this.totalFile1Rows = totalFile1Rows; }

    public int getTotalFile2Rows() { return totalFile2Rows; }
    public void setTotalFile2Rows(int totalFile2Rows) { this.totalFile2Rows = totalFile2Rows; }

    public int getMatchedCount() { return matchedCount; }
    public void setMatchedCount(int matchedCount) { this.matchedCount = matchedCount; }

    public int getMismatchedCount() { return mismatchedCount; }
    public void setMismatchedCount(int mismatchedCount) { this.mismatchedCount = mismatchedCount; }

    public int getUnmatchedFile1Count() { return unmatchedFile1Count; }
    public void setUnmatchedFile1Count(int unmatchedFile1Count) { this.unmatchedFile1Count = unmatchedFile1Count; }

    public int getUnmatchedFile2Count() { return unmatchedFile2Count; }
    public void setUnmatchedFile2Count(int unmatchedFile2Count) { this.unmatchedFile2Count = unmatchedFile2Count; }

    public List<Map<String, String>> getMatchedRows() { return matchedRows; }
    public void setMatchedRows(List<Map<String, String>> matchedRows) { this.matchedRows = matchedRows; }

    public List<Map<String, String>> getMismatchedRows() { return mismatchedRows; }
    public void setMismatchedRows(List<Map<String, String>> mismatchedRows) { this.mismatchedRows = mismatchedRows; }

    public List<Map<String, String>> getUnmatchedFile1Rows() { return unmatchedFile1Rows; }
    public void setUnmatchedFile1Rows(List<Map<String, String>> unmatchedFile1Rows) { this.unmatchedFile1Rows = unmatchedFile1Rows; }

    public List<Map<String, String>> getUnmatchedFile2Rows() { return unmatchedFile2Rows; }
    public void setUnmatchedFile2Rows(List<Map<String, String>> unmatchedFile2Rows) { this.unmatchedFile2Rows = unmatchedFile2Rows; }
}

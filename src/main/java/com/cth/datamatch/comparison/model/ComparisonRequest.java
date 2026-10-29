package com.cth.datamatch.comparison.model;

import java.util.List;
import java.util.Map;

public class ComparisonRequest {
    private String file1Name;
    private String file2Name;
    private List<String> keyColumns;
    private List<String> compareColumns;

    public String getFile1Name() { return file1Name; }
    public void setFile1Name(String file1Name) { this.file1Name = file1Name; }

    public String getFile2Name() { return file2Name; }
    public void setFile2Name(String file2Name) { this.file2Name = file2Name; }

    public List<String> getKeyColumns() { return keyColumns; }
    public void setKeyColumns(List<String> keyColumns) { this.keyColumns = keyColumns; }

    public List<String> getCompareColumns() { return compareColumns; }
    public void setCompareColumns(List<String> compareColumns) { this.compareColumns = compareColumns; }
}

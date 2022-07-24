package cn.jarkata.tools.excel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ExcelData {

    private List<String> headerList;

    private String sheetName;

    private List<Map<String, String>> dataList;

    public List<String> getHeaderList() {
        return Optional.ofNullable(headerList).orElse(new ArrayList<>(0));
    }

    public void setHeaderList(List<String> headerList) {
        this.headerList = headerList;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<Map<String, String>> getDataList() {
        return Optional.ofNullable(dataList).orElse(new ArrayList<>(0));
    }

    public void setDataList(List<Map<String, String>> dataList) {
        this.dataList = dataList;
    }
}

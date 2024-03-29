package cn.jarkata.tools.excel;

import lombok.Getter;

import java.util.*;

@Getter
public class ExcelData {

    private Collection<String> headerList;

    private Collection<String> ignoreHeaders;

    private String sheetName;

    private List<Map<String, String>> dataList;

    private List<?> data;

    public List<?> getData() {
        return Optional.ofNullable(data).orElse(new ArrayList<>(0));
    }

    public void setData(List<?> data) {
        this.data = data;
    }

    public Collection<String> getIgnoreHeaders() {
        return Optional.ofNullable(ignoreHeaders).orElse(new ArrayList<>(0));
    }

    public void setIgnoreHeaders(Collection<String> ignoreHeaders) {
        this.ignoreHeaders = ignoreHeaders;
    }

    public Collection<String> getHeaderList() {
        return Optional.ofNullable(headerList).orElse(new ArrayList<>(0));
    }

    public void setHeaderList(Collection<String> headerList) {
        this.headerList = headerList;
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

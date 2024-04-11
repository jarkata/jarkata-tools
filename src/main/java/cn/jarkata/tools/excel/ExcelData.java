package cn.jarkata.tools.excel;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Getter
public class ExcelData {

    private Collection<String> headerList;

    private Collection<String> ignoreHeaders;

    private String sheetName = "default";

    private Collection<?> data;

    public Collection<?> getData() {
        return Optional.ofNullable(data).orElse(new ArrayList<>(0));
    }

    public void setData(Collection<?> data) {
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

}

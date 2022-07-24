package cn.jarkata.tools.excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ExcelUtilsTest {

    @Test
    public void readExcel() throws IOException, InvalidFormatException {

        File file = new File("/Users/kart/Desktop/test.xlsx");

        List<Map<String, String>> mapList = ExcelUtils.readExcel(file, false);

        System.out.println(mapList);

    }

    @Test
    public void testBuilds() {
        String[] all = BuiltinFormats.getAll();
        System.out.println(Arrays.toString(all));
        System.out.println(all.length);
        boolean date = ExcelUtils.isDate((short) 23);
        System.out.println(date);
        System.out.println(all[9] + "====" + all[10]);
        boolean time = ExcelUtils.isTime((short) 46);
        System.out.println(time);

    }

    @Test
    public void testWriteTo() throws IOException, InvalidFormatException {
        ExcelData data = new ExcelData();
        data.setHeaderList(Arrays.asList("test1", "test2"));
        data.setSheetName("remark");

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("test1", "32423");
        Map<String, String> dataMap1 = new HashMap<>();
        dataMap1.put("test2", "TEST");
        data.setDataList(Arrays.asList(dataMap, dataMap, dataMap1));
        ExcelUtils.writeTo(new File("/Users/kart/Desktop/test02.xlsx"), data);

    }
}
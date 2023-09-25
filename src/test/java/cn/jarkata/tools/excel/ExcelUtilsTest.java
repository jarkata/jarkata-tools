package cn.jarkata.tools.excel;

import cn.jarkata.commons.utils.FileUtils;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtilsTest {

    @Test
    public void readExcel() {

        File file = FileUtils.getFile("test.xlsx");
        List<Map<String, String>> mapList = ExcelUtils.readExcel(file, false);
        System.out.println(mapList);
        Assert.assertNotNull(mapList);
        Map<String, String> dataMap = mapList.get(0);
        Assert.assertEquals(dataMap.get("0"), "test1");
    }

    @Test
    public void readExcelFromStream() {

        InputStream file = FileUtils.getStream("test.xlsx");
        List<Map<String, String>> mapList = ExcelUtils.readExcel(file, false);
        System.out.println(mapList);
        Assert.assertNotNull(mapList);
        Map<String, String> dataMap = mapList.get(0);
        Assert.assertEquals(dataMap.get("0"), "test1");
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
    public void testWriteTo() {
        ExcelData data = new ExcelData();
        data.setHeaderList(Arrays.asList("test1", "test2"));
        data.setSheetName("remark");

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("test1", "32423");
        Map<String, String> dataMap1 = new HashMap<>();
        dataMap1.put("test2", "TEST");
        data.setDataList(Arrays.asList(dataMap, dataMap, dataMap1));
        ExcelUtils.writeTo(new File("/Users/kart/Desktop/test.xlsx"), data);

    }

    @Test
    public void writeTo() {
        ExcelData data = new ExcelData();
        data.setSheetName("remark234");
        UserVO userVO = new UserVO(1L, "test");
        UserVO userVO1 = new UserVO(2L, "test232");
        data.setData(Arrays.asList(userVO, userVO1));
        ExcelUtils.writeTo(new File("./test342.xlsx"), data);
    }
}
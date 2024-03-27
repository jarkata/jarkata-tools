package cn.jarkata.tools.excel;

import cn.jarkata.commons.utils.FileUtils;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.util.*;

public class ExcelUtilsTest {

    @Test
    public void readExcelCallback() {

        List<Map<String, String>> dataList = new ArrayList<>();
        File file = FileUtils.getFile("./test.xlsx");
        ExcelUtils.readExcel(file, false, data -> {
            Assert.assertNotNull(data);
            dataList.add(data);

        });
        System.out.println(dataList.size());
        for (Map<String, String> map : dataList) {
            System.out.println(map);

        }
        Map<String, String> map = dataList.get(0);
        Assert.assertEquals(map.get("0"), "test1");
    }

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
        List<String> list = new ArrayList<>();
        for (int index = 0; index < 143; index++) {
            list.add("test_" + index);
        }
        data.setHeaderList(list);
        data.setSheetName("remark");

        List<Map<String, String>> dataList = new ArrayList<>();
        for (int idx = 0; idx < 20000; idx++) {
            Map<String, String> dataMap = new HashMap<>();
            for (int index = 0; index < 143; index++) {
                dataMap.put("test_" + index, "32423");
            }
            dataList.add(dataMap);
        }
        data.setDataList(dataList);
        ExcelUtils.writeTo(new File("./test.xlsx"), data);

    }

    @Test
    public void writeTo() {
        ExcelData data = new ExcelData();
        data.setSheetName("remark234");


        UserVO userVO = new UserVO(1L, "test");
        UserVO userVO1 = new UserVO(2L, "test232");
        data.setIgnoreHeaders(Collections.singletonList("username"));
        List<UserVO> voList = Arrays.asList(userVO, userVO1);
        data.setData(voList);
        ExcelUtils.writeTo(new File("./test342.xlsx"), data);
    }
}
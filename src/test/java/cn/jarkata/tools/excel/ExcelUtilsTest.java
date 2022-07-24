package cn.jarkata.tools.excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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

}
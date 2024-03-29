package cn.jarkata.tools.excel;

import cn.jarkata.commons.Maps;
import cn.jarkata.commons.utils.DateUtils;
import cn.jarkata.commons.utils.FileUtils;
import cn.jarkata.commons.utils.ReflectionUtils;
import cn.jarkata.commons.utils.StringUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ExcelUtils {

    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    /**
     * 写Excel文件至多个Sheet表格中
     *
     * @param outFile       输出的excel文件
     * @param excelDataList 数据集合
     */
    public static void writeTo(File outFile, ExcelData... excelDataList) {
        Objects.requireNonNull(outFile, "Output File is Null");
        Objects.requireNonNull(excelDataList, "Data Is Null");
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fileOutputStream = new FileOutputStream(outFile)) {
            for (ExcelData excelData : excelDataList) {
                writeSheet(workbook, excelData);
            }
            workbook.write(fileOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeTo(OutputStream outputStream, ExcelData... excelDataList) {
        Objects.requireNonNull(outputStream, "Output File is Null");
        Objects.requireNonNull(excelDataList, "Data Is Null");
        try (Workbook workbook = new XSSFWorkbook()) {
            for (ExcelData excelData : excelDataList) {
                writeSheet(workbook, excelData);
            }
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                outputStream.close();
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * 输出Excel的Sheet页
     *
     * @param workbook  工作表格
     * @param excelData 表格数据
     */
    protected static void writeSheet(Workbook workbook, ExcelData excelData) {
        Objects.requireNonNull(excelData, "Excel数据对象为空");
        List<?> dataObjList = excelData.getData();
        if (Objects.isNull(dataObjList) || dataObjList.isEmpty()) {
            return;
        }
        Object object = dataObjList.get(0);
        if (!(object instanceof Map)) {
            writeObjectSheet(workbook, excelData);
            return;
        }

        Collection<String> headerList = excelData.getHeaderList();
        if (Objects.isNull(headerList) || headerList.isEmpty()) {
            Map<String, Object> headerMap = Maps.toMap(object);
            excelData.setHeaderList(headerMap.keySet());
        }
        List<String> headerList2 = excelData.getHeaderList().stream().filter(key -> !excelData.getIgnoreHeaders().contains(key)).collect(Collectors.toList());
        if (headerList2.isEmpty()) {
            throw new IllegalArgumentException("当数据为类型为HashMap时,HeadersList必须自定义");
        }

        int cellCount = headerList.size();
        Sheet xssfSheet = workbook.createSheet(excelData.getSheetName());
        // 输出表格头
        Row sheetRow = xssfSheet.createRow(0);
        for (int cellIndex = 0; cellIndex < cellCount; cellIndex++) {
            Cell rowCell = sheetRow.createCell(cellIndex, CellType.STRING);
            rowCell.setCellValue(headerList2.get(cellIndex));
        }
        // 输出表格数据主体
        List<?> dataList = excelData.getData();
        for (int rowIndex = 0, rowCount = dataList.size(); rowIndex < rowCount; rowIndex++) {
            Row xssfRow = xssfSheet.createRow(rowIndex + 1);
            Object rowValObj = dataList.get(rowIndex);
            Map<String, Object> rowDataMap = Maps.toMap(rowValObj);
            for (int cellIndex = 0; cellIndex < cellCount; cellIndex++) {
                Cell rowCell = xssfRow.createCell(cellIndex, CellType.STRING);
                String headerKey = headerList2.get(cellIndex);
                String excelVal = StringUtils.toString(rowDataMap.getOrDefault(headerKey, null));
                rowCell.setCellValue(excelVal);
            }
        }
    }

    private static void writeObjectSheet(Workbook workbook, ExcelData excelData) {
        Sheet xssfSheet = workbook.createSheet(excelData.getSheetName());
        // 输出表格头
        Row sheetRow = xssfSheet.createRow(0);

        List<?> dataList = Optional.ofNullable(excelData.getData()).orElse(new ArrayList<>(0));
        if (dataList.isEmpty()) {
            return;
        }
        Object fieldObj = dataList.get(0);
        Class<?> objClass = fieldObj.getClass();

        List<Field> fieldList = ReflectionUtils.getAllFieldList(objClass).stream().filter(field -> !excelData.getIgnoreHeaders().contains(field.getName())).collect(Collectors.toList());
        int firstCellIndex = 0;
        for (Field field : fieldList) {
            Cell rowCell = sheetRow.createCell(firstCellIndex, CellType.STRING);
            rowCell.setCellValue(field.getName());
            firstCellIndex++;
        }
        // 输出表格数据主体
        int rowIndex = 0;
        for (Object dataObj : dataList) {
            Row xssfRow = xssfSheet.createRow(rowIndex + 1);
            setObjCellValue(xssfRow, fieldList, dataObj);
            rowIndex++;
        }

    }

    protected static void setObjCellValue(Row sheetRow, List<Field> fieldList, Object dataObj) {
        if (Objects.isNull(fieldList)) {
            return;
        }
        int cellIndex = 0;
        for (Field field : fieldList) {
            Cell rowCell = sheetRow.createCell(cellIndex, CellType.STRING);
            String dataVal;
            try {
                Object fieldValue = ReflectionUtils.getFieldValue(field, dataObj);
                dataVal = Objects.toString(fieldValue, "");
            } catch (Exception ignored) {
                dataVal = "unkonw";
            }
            rowCell.setCellValue(dataVal);
            cellIndex++;
        }
    }

    /**
     * 将多个Excel文件合并为一个文件
     *
     * @param directory   Excel文件所在目录
     * @param outputExcel 输出文件
     */
    public static void combineExcel(File directory, File outputExcel) {
        List<File> fileList = FileUtils.listFile(directory);
        List<Map<String, String>> mapList = readExcel(fileList.toArray(new File[0]));
        if (mapList.isEmpty()) {
            return;
        }
        Map<String, String> map = mapList.get(0);
        ExcelData excelData = new ExcelData();
        excelData.setHeaderList(map.keySet());
        excelData.setSheetName("data");
        excelData.setData(mapList);
        writeTo(outputExcel, excelData);
    }

    public static void combineExcel(File directory, File outputExcel, List<String> headerList) {
        List<File> fileList = FileUtils.listFile(directory);
        if (Objects.isNull(headerList) || headerList.isEmpty()) {
            throw new IllegalArgumentException("Header must has value");
        }
        List<Map<String, String>> mapList = readExcel(fileList.toArray(new File[0]));
        if (mapList.isEmpty()) {
            return;
        }
        ExcelData excelData = new ExcelData();
        excelData.setHeaderList(headerList);
        excelData.setSheetName("data");
        excelData.setData(mapList);
        writeTo(outputExcel, excelData);
    }

    public static void readExcel(InputStream inputStream, String[] sheetNameList, Consumer<Map<String, String>> consumer) {
        readExcel(inputStream, true, sheetNameList, consumer);
    }

    public static void readExcel(InputStream inputStream, boolean firstRowIsHeader, String[] sheetNameList, Consumer<Map<String, String>> consumer) {
        XSSFWorkbook workbook = null;
        OPCPackage opcPackage = null;
        try {
            inputStream = new BufferedInputStream(inputStream);
            opcPackage = OPCPackage.open(inputStream);
            workbook = new XSSFWorkbook(opcPackage);
            for (String sheetName : sheetNameList) {
                XSSFSheet xssfSheet = workbook.getSheet(sheetName);
                if (firstRowIsHeader) {
                    readFromSheetWithHeader(xssfSheet, consumer);
                } else {
                    readFromSheetWithIndexValue(xssfSheet, consumer);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            closeResourceQuitely(inputStream, opcPackage, workbook);
        }
    }


    public static void readExcel(File[] fileList, Consumer<Map<String, String>> consumer) {
        for (File file : fileList) {
            readExcel(file, true, consumer);
        }
    }

    public static void readExcel(InputStream inputStream, Consumer<Map<String, String>> consumer) {
        readExcel(inputStream, true, consumer);
    }

    /**
     * @param inputStream      Excel文件
     * @param firstRowIsHeader 判断第一行是否为表头
     */
    public static void readExcel(InputStream inputStream, boolean firstRowIsHeader, Consumer<Map<String, String>> consumer) {
        XSSFWorkbook workbook = null;
        OPCPackage opcPackage = null;
        try {
            inputStream = new BufferedInputStream(inputStream);
            opcPackage = OPCPackage.open(inputStream);
            workbook = new XSSFWorkbook(opcPackage);
            int numberOfSheets = workbook.getNumberOfSheets();
            for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
                XSSFSheet xssfSheet = workbook.getSheetAt(sheetIndex);
                if (firstRowIsHeader) {
                    readFromSheetWithHeader(xssfSheet, consumer);
                } else {
                    readFromSheetWithIndexValue(xssfSheet, consumer);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            closeResourceQuitely(inputStream, opcPackage, workbook);
        }
    }

    /**
     * @param file             Excel文件
     * @param firstRowIsHeader 判断第一行是否为表头
     */
    public static void readExcel(File file, boolean firstRowIsHeader, Consumer<Map<String, String>> consumer) {
        XSSFWorkbook workbook = null;
        OPCPackage opcPackage = null;
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            opcPackage = OPCPackage.open(inputStream);
            workbook = new XSSFWorkbook(opcPackage);
            int numberOfSheets = workbook.getNumberOfSheets();
            for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
                XSSFSheet xssfSheet = workbook.getSheetAt(sheetIndex);
                if (Objects.isNull(xssfSheet)) {
                    continue;
                }
                if (firstRowIsHeader) {
                    readFromSheetWithHeader(xssfSheet, consumer);
                } else {
                    readFromSheetWithIndexValue(xssfSheet, consumer);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            closeResourceQuitely(inputStream, opcPackage, workbook);
        }
    }

    public static void readFromSheetWithHeader(Sheet sheet, Consumer<Map<String, String>> consumer) {
        if (Objects.isNull(sheet)) {
            return;
        }
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum < 0) {
            return;
        }
        if (Objects.isNull(consumer)) {
            return;
        }
        int firstRowNum = sheet.getFirstRowNum();
        Row firstRow = sheet.getRow(firstRowNum);
        Map<Integer, String> sheetHeader = readRowIndexValue(firstRow);
        try {
            for (int rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
                Row sheetRow = sheet.getRow(rowIndex);
                Map<String, String> rowValue;
                rowValue = readRowValue(sheetRow, sheetHeader);
                consumer.accept(rowValue);
            }
        } finally {
            sheetHeader.clear();
        }
    }

    /**
     * 从Excel Sheet读取数据，且其Key为单元格的索引值
     *
     * @param sheet sheet页
     */
    public static void readFromSheetWithIndexValue(Sheet sheet, Consumer<Map<String, String>> consumer) {
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum < 0) {
            return;
        }
        if (Objects.isNull(consumer)) {
            return;
        }
        for (int rowIndex = 0; rowIndex <= lastRowNum; rowIndex++) {
            Row sheetRow = sheet.getRow(rowIndex);
            Map<String, String> rowValue;
            rowValue = readRowValue(sheetRow);
            consumer.accept(rowValue);
        }
    }

    /**
     * 读取Excel数据，且第一行为表头
     *
     * @param file Excel文件
     * @return 表格数据
     */
    public static List<Map<String, String>> readExcel(File file) {
        return readExcel(file, true);
    }

    public static List<Map<String, String>> readExcel(InputStream inputStream, boolean firstRowIsHeader) {
        XSSFWorkbook workbook = null;
        OPCPackage opcPackage = null;
        try {
            opcPackage = OPCPackage.open(inputStream);
            workbook = new XSSFWorkbook(opcPackage);
            int numberOfSheets = workbook.getNumberOfSheets();
            List<Map<String, String>> dataList = new ArrayList<>();
            for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
                XSSFSheet xssfSheet = workbook.getSheetAt(sheetIndex);
                if (firstRowIsHeader) {
                    dataList.addAll(readFromSheetWithHeader(xssfSheet));
                } else {
                    dataList.addAll(readFromSheetWithIndexValue(xssfSheet));
                }
            }
            return dataList;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            closeResourceQuitely(inputStream, opcPackage, workbook);
        }
    }

    public static List<Map<String, String>> readExcel(InputStream inputStream, String... sheetNameList) {
        return readExcel(inputStream, true, sheetNameList);
    }

    public static List<Map<String, String>> readExcel(InputStream inputStream, boolean firstRowIsHeader, String... sheetNameList) {
        XSSFWorkbook workbook = null;
        OPCPackage opcPackage = null;
        try {
            opcPackage = OPCPackage.open(inputStream);
            workbook = new XSSFWorkbook(opcPackage);
            List<Map<String, String>> dataList = new ArrayList<>();
            for (String sheetName : sheetNameList) {
                XSSFSheet xssfSheet = workbook.getSheet(sheetName);
                if (firstRowIsHeader) {
                    dataList.addAll(readFromSheetWithHeader(xssfSheet));
                } else {
                    dataList.addAll(readFromSheetWithIndexValue(xssfSheet));
                }
            }
            return dataList;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            closeResourceQuitely(inputStream, opcPackage, workbook);
        }
    }

    /**
     * @param file             Excel文件
     * @param firstRowIsHeader 判断第一行是否为表头
     * @return 表格数据
     */
    public static List<Map<String, String>> readExcel(File file, boolean firstRowIsHeader) {
        XSSFWorkbook workbook = null;
        FileInputStream inputStream = null;
        OPCPackage opcPackage = null;
        try {
            inputStream = new FileInputStream(file);
            opcPackage = OPCPackage.open(inputStream);
            workbook = new XSSFWorkbook(opcPackage);
            int numberOfSheets = workbook.getNumberOfSheets();
            List<Map<String, String>> dataList = new ArrayList<>();
            for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
                XSSFSheet xssfSheet = workbook.getSheetAt(sheetIndex);
                if (firstRowIsHeader) {
                    dataList.addAll(readFromSheetWithHeader(xssfSheet));
                } else {
                    dataList.addAll(readFromSheetWithIndexValue(xssfSheet));
                }
            }
            return dataList;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            closeResourceQuitely(inputStream, opcPackage, workbook);
        }
    }


    public static List<Map<String, String>> readExcel(File file, String... sheetNameList) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(file)) {
            List<Map<String, String>> dataList = new ArrayList<>();
            for (String sheetName : sheetNameList) {
                XSSFSheet xssfSheet = workbook.getSheet(sheetName);
                dataList.addAll(readFromSheetWithHeader(xssfSheet));
            }
            return dataList;
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }


    public static List<Map<String, String>> readExcel(File... fileList) {
        List<Map<String, String>> dataList = new ArrayList<>();
        for (File file : fileList) {
            List<Map<String, String>> mapList = readExcel(file, true);
            dataList.addAll(mapList);
        }
        return dataList;
    }

    /**
     * 从Excel Sheet读取数据，且其Key为单元格的索引值
     *
     * @param sheet sheet页
     * @return 表格数据
     */
    public static List<Map<String, String>> readFromSheetWithIndexValue(Sheet sheet) {
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum < 0) {
            return new ArrayList<>(0);
        }
        List<Map<String, String>> dataList = new ArrayList<>(lastRowNum);
        for (int rowIndex = 0; rowIndex <= lastRowNum; rowIndex++) {
            Row sheetRow = sheet.getRow(rowIndex);
            Map<String, String> rowValue = readRowValue(sheetRow);
            dataList.add(rowValue);
        }
        return dataList;
    }


    /**
     * 读取Excel文件数据，且第一行为表头
     *
     * @param sheet Excel Sheet页
     * @return 表哥数据
     */
    public static List<Map<String, String>> readFromSheetWithHeader(Sheet sheet) {
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum < 0) {
            return new ArrayList<>(0);
        }
        List<Map<String, String>> dataList = new ArrayList<>(lastRowNum);
        int firstRowNum = sheet.getFirstRowNum();
        Row firstRow = sheet.getRow(firstRowNum);
        Map<Integer, String> sheetHeader = readRowIndexValue(firstRow);
        for (int rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
            Row sheetRow = sheet.getRow(rowIndex);
            Map<String, String> rowValue = readRowValue(sheetRow, sheetHeader);
            dataList.add(rowValue);
        }
        return dataList;
    }


    /**
     * Read Row Data
     *
     * @param sheetRow         row
     * @param firstRowValueMap header data
     * @return row data
     */
    protected static Map<String, String> readRowValue(Row sheetRow, Map<Integer, String> firstRowValueMap) {
        if (Objects.isNull(sheetRow)) {
            return new HashMap<>(0);
        }
        firstRowValueMap = Optional.ofNullable(firstRowValueMap).orElse(new HashMap<>(0));
        short lastCellNum = sheetRow.getLastCellNum();
        Map<String, String> rowValueMap = new HashMap<>(lastCellNum);
        for (int index = 0; index < lastCellNum; index++) {
            Cell rowCell = sheetRow.getCell(index);
            String headerKey = firstRowValueMap.getOrDefault(index, "null");
            rowValueMap.put(headerKey, getCellValue(rowCell));
        }
        return rowValueMap;
    }

    protected static Map<Integer, String> readRowIndexValue(Row sheetRow) {
        if (Objects.isNull(sheetRow)) {
            return new HashMap<>(0);
        }
        short lastCellNum = sheetRow.getLastCellNum();
        Map<Integer, String> headerMap = new HashMap<>(lastCellNum);
        for (int index = 0; index < lastCellNum; index++) {
            Cell rowCell = sheetRow.getCell(index);
            headerMap.put(index, getCellValue(rowCell));
        }
        return headerMap;
    }

    protected static Map<String, String> readRowValue(Row sheetRow) {
        if (Objects.isNull(sheetRow)) {
            return new HashMap<>(0);
        }
        short lastCellNum = sheetRow.getLastCellNum();
        Map<String, String> rowValueMap = new HashMap<>(lastCellNum);
        for (int index = 0; index < lastCellNum; index++) {
            Cell rowCell = sheetRow.getCell(index);
            rowValueMap.put("" + index, getCellValue(rowCell));
        }
        return rowValueMap;
    }

    protected static String getCellValue(Cell rowCell) {
        if (Objects.isNull(rowCell)) {
            return "";
        }
        CellType cellType = rowCell.getCellType();
        if (Objects.isNull(cellType)) {
            return "";
        }
        if (CellType.NUMERIC.equals(cellType)) {
            double numericCellValue = rowCell.getNumericCellValue();
            CellStyle cellStyle = rowCell.getCellStyle();
            short dataFormat = cellStyle.getDataFormat();
            if (isDate(dataFormat)) {
                LocalDateTime localDateTimeCellValue = rowCell.getLocalDateTimeCellValue();
                return DateUtils.formatStdIsoDateTime(localDateTimeCellValue);
            }
            if (isTime(dataFormat)) {
                LocalDateTime localDateTimeCellValue = rowCell.getLocalDateTimeCellValue();
                return localDateTimeCellValue.toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME);
            }
            if (isPercentValue(dataFormat)) {
                DecimalFormat decimalFormat = new DecimalFormat("###.#####%");
                return decimalFormat.format(numericCellValue);
            }
            DecimalFormat decimalFormat = new DecimalFormat("###.##");
            return decimalFormat.format(numericCellValue);
        } else if (CellType.FORMULA.equals(cellType)) {
            return rowCell.getCellFormula();
        } else {
            return rowCell.getStringCellValue();
        }
    }


    /**
     * 关闭资源
     *
     * @param inputStream 输入文件流
     * @param opcPackage  excel pack对象
     * @param workbook    workbook对象
     */
    private static void closeResourceQuitely(InputStream inputStream, OPCPackage opcPackage, XSSFWorkbook workbook) {
        try {
            if (Objects.nonNull(inputStream)) {
                inputStream.close();
            }
            if (Objects.nonNull(opcPackage)) {
                opcPackage.close();
            }
            if (Objects.nonNull(workbook)) {
                workbook.close();
            }
        } catch (Exception exception) {
            logger.warn("close resource exception:{}", exception.getMessage());
        }
    }

    protected static boolean isDate(short cellStyleIndex) {
        return (cellStyleIndex >= 14 && cellStyleIndex <= 17) || (cellStyleIndex == 22);
    }

    protected static boolean isTime(short cellStyleIndex) {
        return (cellStyleIndex >= 46 && cellStyleIndex <= 49) || cellStyleIndex >= 18 && cellStyleIndex <= 21;
    }

    protected static boolean isPercentValue(short cellStyleIndex) {
        return cellStyleIndex >= 9 && cellStyleIndex <= 10;
    }

}

package cn.jarkata.tools.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

public class ExcelUtils {

    /**
     * 写Excel文件至多个Sheet表格中
     *
     * @param outFile       输出的excel文件
     * @param excelDataList 数据集合
     */
    public static void writeTo(File outFile, List<ExcelData> excelDataList) {
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
        }
    }

    /**
     * 输出数据至Excel中
     *
     * @param outFile   Excel文件
     * @param excelData excel的数据
     */
    public static void writeTo(File outFile, ExcelData excelData) {
        Objects.requireNonNull(outFile, "Output File is Null");
        Objects.requireNonNull(excelData, "Data Is Null");
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fileOutputStream = new FileOutputStream(outFile)) {
            writeSheet(workbook, excelData);
            workbook.write(fileOutputStream);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * 输出Excel的Sheet页
     *
     * @param workbook  工作表格
     * @param excelData 表格数据
     */
    private static void writeSheet(Workbook workbook, ExcelData excelData) {
        Sheet xssfSheet = workbook.createSheet(excelData.getSheetName());
        // 输出表格头
        Row sheetRow = xssfSheet.createRow(0);
        List<String> headerList = excelData.getHeaderList();
        for (int cellIndex = 0, cellCount = headerList.size(); cellIndex < cellCount; cellIndex++) {
            Cell rowCell = sheetRow.createCell(cellIndex, CellType.STRING);
            rowCell.setCellValue(headerList.get(cellIndex));
        }
        // 输出表格数据主体
        List<Map<String, String>> dataList = excelData.getDataList();
        for (int rowIndex = 0, rowCount = dataList.size(); rowIndex < rowCount; rowIndex++) {
            Row xssfRow = xssfSheet.createRow(rowIndex + 1);
            Map<String, String> dataMap = dataList.get(rowIndex);
            for (int cellIndex = 0, cellCount = headerList.size(); cellIndex < cellCount; cellIndex++) {
                Cell rowCell = xssfRow.createCell(cellIndex, CellType.STRING);
                String headerKey = headerList.get(cellIndex);
                rowCell.setCellValue(dataMap.getOrDefault(headerKey, ""));
            }
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
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
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
            throw new IllegalArgumentException(ex);
        }
    }

    public static List<Map<String, String>> readExcel(InputStream inputStream, boolean firstRowIsHeader, String... sheetNameList) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
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
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * @param file             Excel文件
     * @param firstRowIsHeader 判断第一行是否为表头
     * @return 表格数据
     */
    public static List<Map<String, String>> readExcel(File file, boolean firstRowIsHeader) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(file)) {
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
            throw new IllegalArgumentException(ex);
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
        for (int rowIndex = 0; rowIndex < lastRowNum; rowIndex++) {
            Row sheetRow = sheet.getRow(rowIndex);
            Map<String, String> rowValue = readRowIndexValue(sheetRow);
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
        Map<String, String> sheetHeader = readRowIndexValue(firstRow);
        for (int rowIndex = 1; rowIndex < lastRowNum; rowIndex++) {
            Row sheetRow = sheet.getRow(rowIndex);
            Map<String, String> rowValue = readRowValue(sheetRow, sheetHeader);
            dataList.add(rowValue);
        }
        return dataList;
    }


    private static Map<String, String> readRowValue(Row sheetRow, Map<String, String> firstRowValueMap) {
        short lastCellNum = sheetRow.getLastCellNum();
        Map<String, String> headerMap = new HashMap<>(lastCellNum);
        for (int index = 0; index < lastCellNum; index++) {
            Cell rowCell = sheetRow.getCell(index);
            String headerKey = firstRowValueMap.get(Objects.toString(index));
            headerMap.put(headerKey, getCellValue(rowCell));
        }
        return headerMap;
    }

    private static Map<String, String> readRowIndexValue(Row sheetRow) {
        if (Objects.isNull(sheetRow)) {
            return new HashMap<>(0);
        }
        short lastCellNum = sheetRow.getLastCellNum();
        Map<String, String> headerMap = new HashMap<>(lastCellNum);
        for (int index = 0; index < lastCellNum; index++) {
            Cell rowCell = sheetRow.getCell(index);
            headerMap.put(Objects.toString(index), getCellValue(rowCell));
        }
        return headerMap;
    }

    private static String getCellValue(Cell rowCell) {
        CellType cellType = rowCell.getCellType();
        if (cellType == CellType.NUMERIC) {
            double numericCellValue = rowCell.getNumericCellValue();
            CellStyle cellStyle = rowCell.getCellStyle();
            short dataFormat = cellStyle.getDataFormat();
            if (isDate(dataFormat)) {
                LocalDateTime localDateTimeCellValue = rowCell.getLocalDateTimeCellValue();
                return localDateTimeCellValue.toString();
            }
            if (isTime(dataFormat)) {
                LocalDateTime localDateTimeCellValue = rowCell.getLocalDateTimeCellValue();
                return localDateTimeCellValue.toLocalTime().toString();
            }
            if (isPercentValue(dataFormat)) {
                DecimalFormat decimalFormat = new DecimalFormat("###.#####%");
                return decimalFormat.format(numericCellValue);
            }
            DecimalFormat decimalFormat = new DecimalFormat("###.##");
            return decimalFormat.format(numericCellValue);
        } else if (cellType == CellType.FORMULA) {
            return rowCell.getCellFormula();
        } else {
            return rowCell.getStringCellValue().replace("\n", "").replace("\r", "");
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

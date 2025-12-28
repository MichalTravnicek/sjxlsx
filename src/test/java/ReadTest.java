import com.incesoft.tools.excel.xlsx.Cell;
import com.incesoft.tools.excel.xlsx.Sheet;
import com.incesoft.tools.excel.xlsx.SimpleXLSXWorkbook;
import java.io.File;

/**
 * Using SJXLSX from com.incesoft.tools.excel package
 */
public class ReadTest {

    public static void main(String[] args) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String file = classLoader.getResource("sample_file.xlsx").toURI().getPath();
        System.err.println(file);
        SimpleXLSXWorkbook workbook = newWorkbook(file);
        testLoadALL(workbook);
        testLoadALL(workbook);
        testLoadALL(workbook);
    }

    private static SimpleXLSXWorkbook newWorkbook(String filePath) {
        return new SimpleXLSXWorkbook(new File(filePath));
    }

    /*
     * Load & Read workbook
     */
    public static void testLoadALL(SimpleXLSXWorkbook workbook) {
        com.incesoft.tools.excel.xlsx.Sheet sheetToRead = workbook.getSheet(0, false);
        Sheet.SheetRowReader rowreader = sheetToRead.newReader();
        long start = System.currentTimeMillis();
        int rowPos = 0;
        Cell[] row;
        do {
            row = rowreader.readRow();
            rowPos++;
        } while (row!=null);
        System.err.println("Rows:" + rowPos);
        System.err.println("Elapsed:" + (System.currentTimeMillis() - start));
    }
}

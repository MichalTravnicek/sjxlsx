import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

/**
 * Regular XSSF POI - DOM all-in-memory file reader
 */
public class ReadTestXSSF_POI {

    public static void main(String[] args) {
        String file = "src/main/resources/sample_file.xlsx";
        processLargeExcel(file);
        processLargeExcel(file);
        processLargeExcel(file);
    }

    public static void processLargeExcel(String file){
        long start = System.currentTimeMillis();
        int rows = 0;
        try (Workbook workbook = getWorkbook(file)) {
            Iterator<Row> rowIterator = workbook.getSheetAt(0).rowIterator();
            while (rowIterator.hasNext()) {
                rowIterator.next();
                rows++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error when opening workbook");
        }
        System.err.println("Rows:" + rows);
        System.err.println("Elapsed:" + (System.currentTimeMillis() - start));
    }

    public static Workbook getWorkbook(String file) {
        try {
            IOUtils.setByteArrayMaxOverride(Integer.MAX_VALUE);
            return new XSSFWorkbook(readFile(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static FileInputStream readFile(String file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

}

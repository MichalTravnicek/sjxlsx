import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Using POI based https://github.com/monitorjbl/excel-streaming-reader
 */
public class ReadTestOptimizedPOI {
    public static void main(String[] args) throws IOException {
        read(args);
        read(args);
        read(args);
        read(args);
    }
    public static void read(String[] args) throws IOException {
        int rowcount = 0;
        long start = System.currentTimeMillis();
        File f = new File("src/main/resources/sample_file.xlsx");
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(f);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()){
            rowIterator.next();
            rowcount++;
        }
        workbook.close();
        System.err.println("Rows:" + rowcount);
        System.err.println("Elapsed:" + (System.currentTimeMillis() - start));
    }
}

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;

/**
 * POI in hybrid streaming mode for large files
 * https://svn.apache.org/repos/asf/poi/trunk/poi-examples/src/main/java/org/apache/poi/examples/xssf/streaming/HybridStreaming.java
 */
public class ReadTestHybridStreamingPOI {

    static int rows = 0;

    public static void main(String[] args) throws Exception {
        String file = "src/main/resources/sample_file.xlsx";
        processLargeExcel(file);
        processLargeExcel(file);
        processLargeExcel(file);
    }

    public static void processLargeExcel(String filePath) throws Exception {
        long start = System.currentTimeMillis();
        // 1. Open the file in read-only mode to save memory
        try (OPCPackage pkg = OPCPackage.open(filePath, PackageAccess.READ)) {
            XSSFReader reader = new XSSFReader(pkg);
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
            StylesTable styles = reader.getStylesTable(); // Hybrid part: access styles

            // 2. Set up the XML Parser
            XMLReader parser = XMLReaderFactory.createXMLReader();

            // 3. Define a handler to process each row/cell event
            ContentHandler contentHandler = getContentHandler(styles, strings);
            parser.setContentHandler(contentHandler);

            try (InputStream sheetStream = reader.getSheetsData().next()) {
                parser.parse(new InputSource(sheetStream));
            }
        }
        System.err.println("Rows:" + rows);
        System.err.println("Elapsed:" + (System.currentTimeMillis() - start));
    }

    private static ContentHandler getContentHandler(StylesTable styles, ReadOnlySharedStringsTable strings) {
        XSSFSheetXMLHandler.SheetContentsHandler handler = new XSSFSheetXMLHandler.SheetContentsHandler() {
            @Override
            public void startRow(int rowNum) {
                rows++;
            }

            @Override
            public void cell(String cellRef, String formattedValue, org.apache.poi.xssf.usermodel.XSSFComment comment) {
            }

            @Override
            public void endRow(int rowNum) {}
        };


        // 4. Stream the specific sheet
        ContentHandler contentHandler = new XSSFSheetXMLHandler(styles, strings, handler, false);
        return contentHandler;
    }
}

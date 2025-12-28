Experiment in large XLSX parsing performance
=======================================

> Credit: based on more than 10 years old abandoned project: https://github.com/davidpelfree/sjxlsx.

I was about to complete programming exercise assignment - read and parse XLSX (Excel) file and do something with its
contents. As this is fairly simple task and I have already done that before and exercise is also hinting we might be 
processing BIG files - I say lets try some optimizations.
I have known and used the Apache POI framework for quite some time as this is de facto standard for handling MS Office
stuff in JAVA and knew that there are multiple variants of Excel POI API https://poi.apache.org/components/spreadsheet/

![POI API](doc/ss-features.png)

There are two factors we look after in Excel processing - memory consumption and processing speed.
XLSX (OOXML) file is a variant of XML and as a choice for reading an XML we have basically DOM parser and/or SAX/STAX parser.
DOM parser loads whole document in memory while SAX/STAX is for streaming processing.

As we want to read really large XLSX file (say 1GB file with 1 million rows - maximum for XLSX) we opt for
SAX/streaming parser with low constant memory usage that reads/processes chunks of data (see POI API image)

POI or other API can`t read XLSX file directly in chunks, because XLSX file is in fact ZIP archive.
So file has to unzipped first and then processed (looks like another bottleneck/slowdown factor).
Eventually I had experimented with streaming parsing in POI previously, but did not achieve any great results.

<hr style="border:2px solid gray">

- Found article https://dzone.com/articles/read-large-excel-files-in-java-with-sjxlsx 
  referencing interesting project: https://github.com/davidpelfree/sjxlsx (simple and efficient custom Excel reader/writer)
- Also found this project on Github: https://github.com/monitorjbl/excel-streaming-reader based on POI but optimized for speed

First I was looking for some big 1GB Excel file - but eventually settled for file with 1 million rows
https://chandoo.org/wp/more-than-million-rows-in-excel/ 
that has 'only' 70MB in size as a CSV then converted that to 30MB XLSX file

<hr style="border:2px solid gray">

### Testing read speed on 1-million-row file

- I wanted to give SJXLSX a try and will be focusing on using/optimizing that - POI based solutions are just for reference/benchmark
- As we can see in [original Readme for SJXLSX](doc/SjxlsxREADME.md) it is optimized for memory and speed with STAX 
  streaming parser - which is what we need

The Apache POI has half-baked example of HybridStreaming that cannot be run directly 
https://svn.apache.org/repos/asf/poi/trunk/poi-examples/src/main/java/org/apache/poi/examples/xssf/streaming/HybridStreaming.java
- This is working implementation: [HybridStreamingPOI](src/test/java/ReadTestHybridStreamingPOI.java)

Reader implementations:

- [SJXLSX](src/test/java/ReadTest.java)
- [excel-streaming-reader](src/test/java/ReadTestOptimizedPOI.java) 
- [HybridStreamingPOI](src/test/java/ReadTestHybridStreamingPOI.java) 
- [XSSF_POI](src/test/java/ReadTestXSSF_POI.java) 

[READER BENCHMARKS](doc/Benchmarks.md)
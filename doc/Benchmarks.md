File: sample_file.xlsx - 1048576 rows - 6 columns with random small numbers

#### First baseline performance snapshot with JAVA8 JDK SEMERU 1.8.0_472

- [SJXLSX](../src/test/java/ReadTest.java) - 5398ms to 6006ms from 3 samples
- [excel-streaming-reader](../src/test/java/ReadTestOptimizedPOI.java) - 8198ms to 8914ms from 4 samples
- [HybridStreamingPOI](../src/test/java/ReadTestHybridStreamingPOI.java) - 21677ms to 22016ms from 3 samples
- [XSSF_POI](../src/test/java/ReadTestXSSF_POI.java) - 25135ms to 28922ms - about 8-10GB RAM! 

Overview: Tested all available readers including XSSF_POI just to have comparison<br>
- Running on my laptop with some background activity, so the test conditions are not ideal<br>
- First 3 readers have negligible constant memory allocation in contrast to XSSF_POI that loads all into memory<br>
- Benchmark has 3-4 successive runs and there might be some variation - even speedup from JAVA warmup<br>
- Most important value is the time taken to complete batch from first (cold) start<br>
- CPU load is not measured but more effective readers also have smaller CPU spikes 

#### Details:
- SJXLSX: [Sample1](SJXLSX-JAVA1.8.png), [Sample2](SJXLSX-JAVA1.8_2.png) - SJXLSX is fastest and most efficient
- excel-streaming-reader: [Sample1](OptimizedPOI-JAVA1.8.png), [Sample2](OptimizedPOI-JAVA1.8_2.png) - Optimized POI using
  https://github.com/monitorjbl/excel-streaming-reader library is surprisingly fast compared to Apache POI itself.
  Though it has not been updated since 2021 and uses Apache POI 4.1.2 while current version is 5.5.1, I&nbsp;doubt there 
  would be any significant speed improvement when using latest version of POI (rest of readers use POI 4.1.2).
- HybridStreamingPOI: [Sample1](HybridStreaming-JAVA1.8.png), [Memory](HybridStreamingMemory-JAVA1.8.png) -
  HybridStreaming POI does not have memory spikes -good- but it`s slow - though little bit faster than XSSF, there
  is speedup like 20% max - why it is not faster? when compared to excel-streaming-reader that is POI based and does only(?) some tweaking.
- XSSF_POI: [Sample1](XSSF-POI-JAVA1.8.png), [MemoryBefore](XSSF-MemoryBefore-JAVA1.8.png),
  [MemoryAllocated](XSSF-MemoryAllocated-JAVA1.8.png), [MemoryAfter](XSSF-MemoryAfter-JAVA1.8.png) - 
  This is really just for reference - huge memory spikes - looks like app managed to allocate up to 10GB RAM! (see memory committed) 
  Speed is worse than HybridStreaming. As this site suggests https://medium.com/@5_minute_read/sxssfworkbook-a-scalable-solution-to-huge-excels-adbf56021ca7 - 
  XSSFWorkbook can allocate up to 10x more memory than size of Excel data - looks like its rather 100x times more
  in this case. Is it those "XMLBeans"? hmm.

<hr style="border:2px solid gray">

#### Optimization 1 - for free - upgrade JAVA version to JAVA25 JDK GRAALVM 25.0.1 Bellsoft Liberica

- [SJXLSX](../src/test/java/ReadTest.java) - 3259ms to 3915ms from 3 samples
- [excel-streaming-reader](../src/test/java/ReadTestOptimizedPOI.java) - 4810ms to 6025ms from 4 samples
- [HybridStreamingPOI](../src/test/java/ReadTestHybridStreamingPOI.java) - 15394ms to 17218ms from 3 samples
- [XSSF_POI](../src/test/java/ReadTestXSSF_POI.java) - 20263ms to 23567ms - about 10GB RAM! 

#### Details:
- SJXLSX: [Sample1](SJXLSX-JAVA25.png), [Sample2](SJXLSX-JAVA25_2.png) - almost 2x improvement! - is it due to a better JVM SAX parser in JAVA 25? 
- excel-streaming-reader: [Sample1](OptimizedPOI-JAVA25.png), [Sample2](OptimizedPOI-JAVA25_2.png) - also nearly 2x improvement!
- HybridStreamingPOI: [Sample1](HybridStreaming-JAVA25.png), [Sample2](HybridStreaming-JAVA25_2.png) - about 15-20% improvement!   
- XSSF_POI: [Sample1](XSSF-POI-JAVA25.png), [Sample2](XSSF-POI-JAVA25_2.png), [MemoryBefore](XSSF-MemoryBefore-JAVA25.png),
  [MemoryAllocated](XSSF-MemoryAllocated-JAVA25.png), [MemoryAfter](XSSF-MemoryAfter-JAVA25.png) - about 15% speed improvement, 
  memory allocation stays the same!

_**Update**: found out up-to-date fork of **excel-streaming-reader** https://github.com/pjfanning/excel-streaming-reader 
that supports newest Apache POI 5.0.0+. Also rechecked that used excel-streaming-reader version 2.2.0 is having Apache POI 4.1.2 not 5.0.0.
I will update relevant dependencies and retest all POI readers - also looks like running HybridStreamingPOI is not readonly as it modifies input file!_ 

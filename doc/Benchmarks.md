File: _sample_file.xlsx_ - 1048576 rows, 6 columns with random small numbers

#### First baseline performance snapshot with JAVA8 JDK SEMERU 1.8.0_472

- [SJXLSX](../src/test/java/ReadTest.java) - 5398ms to 6006ms from 3-batch run
- [excel-streaming-reader](../src/test/java/ReadTestOptimizedPOI.java) - 8198ms to 8914ms from 4-batch run
- [HybridStreamingPOI](../src/test/java/ReadTestHybridStreamingPOI.java) - 21677ms to 22016ms from 3-batch run
- [XSSF_POI](../src/test/java/ReadTestXSSF_POI.java) - 25135ms to 28922ms - about 8-10GB RAM! 

**Overview**: Tested all available readers, including XSSF_POI just to have comparison
- Running on my laptop with some background activity, so the test conditions are not ideal
- The first 3 readers have negligible constant memory allocation in contrast to XSSF_POI that loads all into memory
- Benchmark has 3-4 successive runs and there might be some variation - even speedup from JAVA warmup
- The most important value is the time taken to complete the first batch (cold start)
- CPU load is not measured but more effective readers also have smaller CPU spikes 

#### Details:
- SJXLSX: [Sample1](SJXLSX-JAVA1.8.png), [Sample2](SJXLSX-JAVA1.8_2.png) - SJXLSX is fastest and most efficient
- excel-streaming-reader: [Sample1](OptimizedPOI-JAVA1.8.png), [Sample2](OptimizedPOI-JAVA1.8_2.png) - Optimized POI using
  https://github.com/monitorjbl/excel-streaming-reader library is surprisingly fast compared to Apache POI itself.
  But it has not been updated since 2021 and uses Apache POI 4.1.2 ~~_5.0.0_~~ while current version is 5.5.1, I&nbsp;doubt there 
  would be any significant speed improvement when using latest version of POI (rest of readers currently use POI 4.1.2).
- HybridStreamingPOI: [Sample1](HybridStreaming-JAVA1.8.png), [Memory](HybridStreamingMemory-JAVA1.8.png) -
  HybridStreaming POI does not have memory spikes -good- but it`s slow - though little bit faster than XSSF, there
  is speedup like 20% max - why it is not faster? when compared to excel-streaming-reader that is POI based and does only(?) some tweaking.
- XSSF_POI: [Sample1](XSSF-POI-JAVA1.8.png), [MemoryBefore](XSSF-MemoryBefore-JAVA1.8.png),
  [MemoryAllocated](XSSF-MemoryAllocated-JAVA1.8.png), [MemoryAfter](XSSF-MemoryAfter-JAVA1.8.png) - 
  This is really just for reference - huge memory spikes - looks like the app managed to allocate up to 10GB RAM! (see memory committed) 
  Speed is worse than HybridStreaming. As [this](https://medium.com/@5_minute_read/sxssfworkbook-a-scalable-solution-to-huge-excels-adbf56021ca7)
  site suggests, XSSFWorkbook can allocate up to 10x more memory than size of Excel data - looks like its rather 100x times more
  in this case. Is it because of "[XMLBeans](https://stackoverflow.com/questions/2498536/poi-performance)"? hmm.

<hr style="border:2px solid gray">

#### Optimization 1 - for free - upgrade JAVA version to JAVA25 JDK GRAALVM 25.0.1 Bellsoft Liberica

- [SJXLSX](../src/test/java/ReadTest.java) - 3259ms to 3915ms (cold start 3514ms) from batch run
- [excel-streaming-reader](../src/test/java/ReadTestOptimizedPOI.java) - 4810ms to 6025ms (cold start 5472ms) from batch run
- [HybridStreamingPOI](../src/test/java/ReadTestHybridStreamingPOI.java) - 15394ms to 17218ms from batch run
- [XSSF_POI](../src/test/java/ReadTestXSSF_POI.java) - 20263ms to 23567ms - about 10GB RAM! 

#### Details:
- SJXLSX: [Sample1](SJXLSX-JAVA25.png), [Sample2](SJXLSX-JAVA25_2.png) - almost 2x improvement! - is it due to a better JVM SAX parser in JAVA 25? 
- excel-streaming-reader: [Sample1](OptimizedPOI-JAVA25.png), [Sample2](OptimizedPOI-JAVA25_2.png) - also nearly 2x improvement!
- HybridStreamingPOI: [Sample1](HybridStreaming-JAVA25.png), [Sample2](HybridStreaming-JAVA25_2.png) - about 15-20% improvement!   
- XSSF_POI: [Sample1](XSSF-POI-JAVA25.png), [Sample2](XSSF-POI-JAVA25_2.png), [MemoryBefore](XSSF-MemoryBefore-JAVA25.png),
  [MemoryAllocated](XSSF-MemoryAllocated-JAVA25.png), [MemoryAfter](XSSF-MemoryAfter-JAVA25.png) - about 15% speed improvement, 
  memory allocation stays the same!

_**Update**: found an up-to-date fork of **excel-streaming-reader** https://github.com/pjfanning/excel-streaming-reader 
that supports newest Apache POI 5.0.0+. Rechecked that my latest excel-streaming-reader version 2.2.0 bundles Apache POI 4.1.2 not 5.0.0.
Commit with POI 5.0.0 exists, but was never released. Will switch to **pjfanning** fork and retest all POI readers. 
Also looks like running HybridStreamingPOI is not readonly as it modifies input file!_ 

<hr style="border:2px solid gray">

#### Optimization 2 - upgrade to _excel-streaming-reader_ fork with latest Apache POI 5.5.1

- [SJXLSX](../src/test/java/ReadTest.java) - 3259ms to 3915ms (cold start 3514ms) - no relevant change
- [excel-streaming-reader](../src/test/java/ReadTestOptimizedPOI.java) - 4394ms to 5556ms (cold start 5141ms)
- [HybridStreamingPOI](../src/test/java/ReadTestHybridStreamingPOI.java) - 11207ms to 12835ms - fixed READONLY mode
- [XSSF_POI](../src/test/java/ReadTestXSSF_POI.java) - 18907ms to 23397ms - about 12GB RAM!

#### Details:
- SJXLSX: [Sample1](SJXLSX-JAVA25.png), [Sample2](SJXLSX-JAVA25_2.png) - no relevant change - POI5 should have no effect
- excel-streaming-reader: [Sample1](OptimizedPOI-POI5-JAVA25.png), [Sample2](OptimizedPOI-POI5-JAVA25_2.png), [Sample3](OptimizedPOI-POI5-JAVA25_3.png),
  [Sample4](OptimizedPOI-POI5-JAVA25_4.png) - a slight improvement - disabled one flag (newest fork or POI5 might also contribute)  
  On lucky runs scores as low as 5043ms on cold start - second or third batch can go down to 4394ms  
- HybridStreamingPOI: [Sample1](HybridStreaming-POI5-JAVA25.png), [Sample2](HybridStreaming-POI5-JAVA25_2.png) - about 15% improvement! 
  due to the proper READONLY flag, POI version 5 has no speed benefit
- XSSF_POI: [Sample1](XSSF-POI5-JAVA25.png), [Sample2](XSSF-POI5-JAVA25_2.png), [MemoryBefore](XSSF-POI5-MemoryBefore-JAVA25.png),
  [MemoryAllocated](XSSF-POI5-MemoryAllocated-JAVA25.png) - slight speed improvement about 5%, requires adding IOUtils library 
  and setting setByteArrayMaxOverride, memory allocation is the same or even worse - 12GB (see images)!

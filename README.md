# LZW-Compression
This program was originally submitted as a classwork assignment focusing on mastery of the innerworkings and implementation of the LZW compression algorithm, and gaining a better understanding of the performance it offers. MyLZW is derived from LZW.java, provided in the textbook <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne. The modifications made to LZW.java are detailed in the next section. BinaryStdIn.java, BinaryStdOut.java, TST.java, Queue.java, StdIn.java, and StdOut.java are unmodified versions of Sedgewick and Wayne's files. results.txt compares the compression of MyLZW's various modes to the unmodified LZW.java and to 7-zip, another compression program. 

## Program Behavior
Modifications of MyLZW from the original LZW file are as follows: <br/> 
* The algorithm now varies the size of codewords from 9 to 16 as necessary, as opposed to using a fixed codeword size of 12.  <br/> 
* Once the codebook has filled (all 16-bit codewords have been used, there are three approaches that the user can specify in advance:  <br/> 
  * **Do Nothing Mode**: Use the full codebook and continue compressing. This approach was already implemented by LZW.java by default. (On the command line, a user can select this option with 'n')  <br/> 
  * **Reset Mode**: Reset the dictionary back to its initial, empty state. (On the command line, a user can select this option with 'r') <br/> 
  * **Monitor Mode**: Monitor the compression ratio once the codebook is full. If the ratio degrades more than a cetain threshold, the codebook will reset. Otherwise, it will use the full codebook. If the ratio of ratios (compression ratio of last instance of filled codebook/ compression ratio of this instance of filled codebook) exceeds 1.1, reset mode will be triggered. (On the command line, a user can select this option with 'm') <br/> 

## How to Run
To run this program, all files must reside in the same directory. Compile on the command line with 'javac MyLZW.java'. To compress file example.txt in Reset Mode, run: java MyLZW - r < example.txt > example.lzw . To expand that file again, run: java MyLZW + < example.lzw > example_expanded.txt . The mode of compression is encoded in the compressed file, so there is no need to specify that mode on the command line when expanding the file. 

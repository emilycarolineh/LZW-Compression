/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/

public class MyLZW {
    private static final int R = 256;        // number of input chars
    private static int L = 512;       // initial number of codewords = 2^W
    private static int W = 9;         // initial codeword width



	//Something about increasing the codebook.

    public static void compress(int mode) { 
        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

		if(input.length() > 0) {
			if(mode == 0)
				BinaryStdOut.write(0, W);
			else if(mode == 1)
				BinaryStdOut.write(1, W);
			else if(mode == 2)
				BinaryStdOut.write(2, W);
		}

		//Monitor mode variables.
		double initialRatio = 0;
		double newRatio = 0;
		long uncompressed = 0;
		long compressed = 0;
		

        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);    // Print s's encoding.
            int t = s.length();

			if(t < input.length() && code >= L) {
				if(W < 16) {	//Increase codebook size and width of codes.
					W++;
					L = (int)Math.pow(2, W);
				}
				else {	//In this case, the codebook is full and cannot be increased further.
					if(mode == 1) {						//Reset Mode
					
						//Reset L and W.
						W = 9;
						L = 512;
	
						//Initialize a new codebook
						st = new TST<Integer>();
	
						//Fill the codebook with all single-character codes again. 
						for (int i = 0; i < R; i++)
            				st.put("" + (char) i, i);
        				code = R+1;  // R is codeword for EOF.
					}
					else if(mode == 2) {				//Monitor Mode
						if(initialRatio == 0) {			//This is the first time through; calculate initial ratio.
							initialRatio = uncompressed/compressed;
						}
						else {							//Calculate new ratio.
	
							newRatio = uncompressed/compressed;
	
							if(initialRatio/newRatio > 1.1) {	//Reset codebook.
								//Reset L and W.
								W = 9;
								L = 512;
	
								//Initialize a new codebook
								st = new TST<Integer>();
	
								//Fill the codebook with all single-character codes again. 
								for (int i = 0; i < R; i++)
            						st.put("" + (char) i, i);
        						code = R+1;  // R is codeword for EOF.
							
								//Reset both ratios.
								initialRatio = 0;
								newRatio = 0;
							}
						}
					}
				}
			}
            

			if (t < input.length() && code < L) {   // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);
				
				//For monitor mode, keep track of data sizes. 
				if(mode == 2) {
					uncompressed += (t * 8);
					compressed += W;
				}
			}
			
            input = input.substring(t);            // Scan past s in input.
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 


    public static void expand() {
        String[] st = new String[65536];
        int i; // next available codeword value
		int mode; //Mode of expansion to be used. 

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;

        st[i++] = "";                        // (unused) lookahead for EOF


		mode = BinaryStdIn.readInt(W);

		if(mode == R) 
			return;  //Expanded message is empty string.

		//Monitor mode variables.
		double initialRatio = 0;
		double newRatio = 0;
		long uncompressed = 0;
		long compressed = 0;
		

        int codeword = BinaryStdIn.readInt(W);

        String val = st[codeword];

        while (true) {
            BinaryStdOut.write(val);
			

			if(i >= L && W < 16) {	//Array is full, and W can increase by 1.
				//Increment W and L
				W++;
				L = (int)Math.pow(2, W);

			}
			else if(i >= L &&  W == 16) { //Break into cases, depending on mode.
				if(mode == 1) {	//Reset mode.
					
					//Reset W, L, and i. 
					W = 9;
					L = 512;
					i = R;

					//Initialize a new codebook. 
					String[] stNew = new String[65536];

					//Add all 1-character codes to the codebook.
					for (int j = 0; j < R; j++)
            			stNew[j] = "" + (char) j;

					stNew[i++] = "";	// (unused) lookahead for EOF

					st = stNew;
				}
				else if(mode == 2) {				//Monitor mode
					if(initialRatio == 0) {			//This is the first time through; calculate initial ratio.
						initialRatio = uncompressed/compressed;
					}
					else {
						newRatio = uncompressed/compressed;
		
						if(initialRatio/newRatio > 1.1) {	//Reset codebook.
							//Reset L and W.
							W = 9;
							L = 512;
							i = R;
	
							//Initialize a new codebook. 
							String[] stNew = new String[65536];
	
							//Add all 1-character codes to the codebook.
							for (int j = 0; j < R; j++)
            					stNew[j] = "" + (char) j;
		
							stNew[i++] = "";	// (unused) lookahead for EOF
			
							st = stNew;
						}
					}
				}
			}
		

			codeword = BinaryStdIn.readInt(W);


            if (codeword == R) 
				break;

            String s = st[codeword];


			//For monitor mode, keep track of data sizes. 
			uncompressed += (val.length() * 8);
			compressed += W;
		

            if (i == codeword) 
				s = val + val.charAt(0);   // special case hack
            if (i < L) 
				st[i++] = val + s.charAt(0);

            val = s;
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if(args[0].equals("-")) {
			if(args[1].equals("n"))
				compress(0);
			else if(args[1].equals("r"))
				compress(1);
			else if(args[1].equals("m"))
				compress(2);
		}
        else if(args[0].equals("+")) {
			expand();
		}
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}

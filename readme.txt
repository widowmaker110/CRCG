This program returns all combinations of the items entered that do not exceed a target value and places the results into a text file named results.txt.

Program instructions
	Create a text file with the following for each item:
		One line containing the name of the item
		One line containing the integer value of the item
	Open subsetsum.bat with a text editor
	replace 5764 with your target value as an integer (e.g. for a monetary amount, enter the amount in cents)
	replace items.txt with the name of the text file you created previously
	execute subsetsum.bat
	
NOTE:
	Java must be installed and in your system's PATH variable (executable through the command line.)
	These variables will heavily affect this program's performance:
		How high the target value is set
		The number of items with small values
		The number of items in general
	Subsequent executions of this program will overwrite results.txt, not append it.
	If you are a developer and wish to modify the code, add the following lines to the beginning of subsetsum.bat then execute it to compile and run:
		cd dakota
		cd subsetsum
		javac *.java
		cd ..
		cd ..
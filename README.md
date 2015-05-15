# book-manager
A Book Manager for my CompSci summative assignment<br>
<img src="https://raw.githubusercontent.com/jimthenerd/book-manager/master/bookmanager.png" width="350">

###Features

> Serializes information to a text file

> Fuzzy search algorithm based on edit distance

> Sorting by various fields

> User friendly GUI

###IOException on start up? u wot m8?

It is probably because you <b> did not download the data file </b>

If you do not have a data file, it will give you an IOException on
start up. It will then create a data file, so the error will not
appear again. 

###Building

You can build the project by running:
  `javac BookManager.java` and then 
  `java BookManager` to run the program.
  
If you are building in eclipse, make 'BookManager' the default class.

###For Binary Download

Goto /binary_download/ and download BOTH FILES. Hopefully it runs...

###Warnings

The data file is <b> NOT meant to be modified by users </b>, modifying the 
data file may (almost always) cause data loss. 

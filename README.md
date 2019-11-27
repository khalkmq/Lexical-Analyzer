# Lexical-Analyzer

Used : proj2-minc-RecPredict-startup

## Description:
Lexical analysis is one of the first phases of the compiling process. 
This particular implementation attempts to do this for a minified version of the C Language (Mini-C).

This Lexical analyzer implementation will: 
1) Takes a program source as an input stream. 
2) Breaks the source into the sequence of lexemes that are meaningful units of words for tokens. 
3) Generates tokens, each of which is a pair of a token name and its attribute. 
4) Passes them to parser sequentially.
(OPTIONAL) 5) Adds new identifiers into the symbol table. 


## How it works:

1. Clone this into your source directory

2. Download jflex-1.X.X.jar from https://jflex.de/download.html and place it in the source directory.

3. Open your terminal and cd to your source directory.

4. Compile Lexer.jflex as follows, make sure your jflex number is the same as the one you downloaded:
```
java -jar jflex-1.X.X.jar Lexer.flex
```
4. Compile all java files using the following:
```
javac *.java
```
5. Place your test files, or the files provided in this repo into the source directory.

6. Run program and capture its output as follows:
```
java Program fail_01.minc > f1.txt
```


## Related Repos:
[Lexical-Tokenizer](https://github.com/khalkmq/Lexical-Tokenizer)

[Lexical-Analyzer](https://github.com/khalkmq/Lexical-Analyzer)

[Syntax Checker]()

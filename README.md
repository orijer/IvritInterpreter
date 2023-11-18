# Ivrit Programming Language Interpreter

Welcome to Ivrit, a simple Hebrew Programming Language, that I created! 
This repository hosts the official Ivrit Interpreter, made in Java, and a thorough tutorial on the syntax and structure of Ivrit.

You can choose to either use the already compiled version packaged in a convenient executable, or download the java source files directly.


## Features
* **A complete experience in Hebrew: From the programming to the execution**
* **Easy to learn: Designed as a great entry point for people with no prior programming experience**
* **Simple: Very easy and quick to start a new Ivrit project**
* **Implemented in Java: Great for all Operating Systems**
* **Open sourced: Anyone can download, read and rewrite any part from the interpreter**


## Getting Started

The first step is to download the Ivrit Interpreter executable in the [releases section](https://github.com/orijer/IvritInterpreter/releases).
Read the install instructions carefully.

The Ivrit Interpreter executable is a GUI program that I made, that allows the user to enter a path to a txt file that contains Ivrit code (more on that later)
and follows the code until completion. We use it to run the Ivrit code files we create.

The second step is to learn the Ivrit language syntax and strcture. You can find a full tutorial (In Hebrew) in the same folder as the Ivrit Interpreter executable.
It will teach you everything you need to know about the Ivrit language.

Lastly, you will want to run your own Ivrit code.
This is the easiest part!
Just create a new txt file anywhere you'd like, and type the code you  want to perform in it (as the tutorial teaches)

In order to then run the file you created, copy the path to that file (right click on it, and copy path, or click once on it and press ctrl + shift + C).
Then open the Ivrit Interpreter executable, and paste the path to it. It will let you know if the file was found or not, and continue from there!


## A tutorial for Learning Ivrit
We have built a complete tutorial in Hebrew for how to use the Ivrit language.
It is already in the same folder as the Ivrit Interpreter executable, but you can also find it in [here](https://github.com/orijer/IvritInterpreter/blob/main/%D7%9E%D7%93%D7%A8%D7%99%D7%9A%20%D7%9E%D7%9C%D7%90%20%D7%9C%D7%A9%D7%A4%D7%94.txt).
IF you prefer that, make sure to download the text file first and change it to Right to Left (Git doesn't support that right now!).

Any questions regarding the Ivrit programming language itself should be asked in the [Ivrit Language Questions](https://github.com/orijer/IvritInterpreter/discussions/categories/ivrit-language-questions) in the discussions segment.


## Tests
In the repository we have a tests folder which includes 2 text file: testCode, testResult.

You can run the interpreter on testCode and compare the output to testResult.

Tip: testCode is completely in Ivrit, which means that it is also worth reading to see more examples if needed.


## How To Contribute
Did you find a bug? Or maybe you thought of a new feature that will fit nicely?
Make sure to open an issue or send a pull request!

Also, constructive feedback is always welcomed in the [discussions segment](https://github.com/orijer/IvritInterpreter/discussions) 


## Authors
* **Ori Bagno Jerushalmi** - [orijer](https://github.com/orijer)
  * Design of Ivrit's syntax
  * Implementation of the Ivrit interpreter in java


## Versioning
We use Four Segment Versioning:
 * MAJOR_VERSION.MINOR_VERSION.PATCH_VERSION.BUILD_VERSION

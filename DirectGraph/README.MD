--------
[TOC]

####Note: 
>- This document is writen with [StackEdit](https://stackedit.io/).  View it there for  perfect effect.

### Implementation Environment

> -  Language: Java 1.7
> - Build: Maven 3.2.1
> - 3PP: jUnit 4.11

### How to Run
 **Run with Maven to see the result of test input:**
```
$> mvn compile exec:java
```
**Build a jar file and run It with java:**
```
// Get the jar package using maven
$> mvn package
// Run the following command to see the usage
$> java -jar train-cli-1.0.jar help
// Run it with option `-f` to specify a file as input, use the file `inputs.txt` under `src/main/resources` to see the results of test input.
$> java -jar train-cli-1.0.jar -f <input-file>
// Run it with option `-i` to enter the interactive mode. Refer to the next section for more about input format.
$> java -jar train-cli-1.0.jar -i
```

### Input Format Introduction
> The  program treats each line of input as a directive and then tries to process it and print out the result. All legal directives are documented below. You can refer to the sample input file in the project named  inputs.txt for  test input.

### Directive
>***Note: *** 

> - All directives and parameters below are case insensitive.
> - Empty line and line starts with '#' will be ignored.

Directive      | Description | Example
:------: | -----|----|----
**G**  |Add routes to the program<br>Multiple routes are seperated<br> by comma|G AB3, AC4 
**D**  |Find the distance of a route path <br>from a city to another one<br>Multiple route path can be process at<br> one time, seperated by a comma|D A-B-C<br>D A-B-C, A-E-D
**TS**| Find the number of trips <br>with exact stops| TS A-C, 4
**TMS**|Find the number of trips<br>not exceed a specified stop number|TMS C-C, 3
**SD**|The distance of shortest route <br>from a city to another one,<br> multiple parameters are acceptable<br>seperated by a comma.|SD A-C<br>SD D-B, C-E|
**TLTD**|The number of different routes <br>from a city to another one with distance<br> less than a specified number.|TLTD C-C, 30

By **<a href="mailto:messi.shizz@gmail.com" target="_top">messi.shizz@gmail.com</a>**

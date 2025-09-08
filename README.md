# di-integration
A data integration project that offers the implemented solutions for Data profiling, Duplicate Detection and Schema Matching

## Requirements
- Java Version >= 11
- Maven Compiler Version >= 3.8.1

## Getting started
1. Clone repo
  ```
  git clone git@github.com:UMR-Big-Data-Analytics/ddm-akka.git
  ```

2. Build project with maven
  ```
  cd ..
  mvn package
  ```

## Advanced testing

1. Read the program documentation
  ```
  java -jar target/di-integration-1.0.jar
  ```

2. Run a specific data integration step, for example:
  ```
  java -jar target/di-integration-1.0.jar Levenshtein --string1 "Data Integration Uni Marburg" --string2 "Datenintegration Universität Marburg" --withDamerau true
  ```


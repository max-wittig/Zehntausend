# Zehntausend
a java implementation of the real world dice game "Zehntausend"
  
## requirements

* Maven build system
* Java 8+
* X server or other windowing system, which supports JavaFX

## installation and usage (linux)
* Install maven
  ```bash
  sudo apt install maven
  ```

* Build jar package
  ```bash
  mvn package
  ```

  The result will be in the target folder

* Run the jar file
  ```bash
  jar -jar zehntausend-<version>.jar-with-dependencies.jar
  ```

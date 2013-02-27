MySQL JDBC drive compatible with Java 8

Here are steps how to build the driver from sources

1. Download and build 7Bee build tool (optional since Ant base can work too)
2. Install 7Bee tool using Java 8 VM (if use Ant, check their documentation)
3. Download MySQL Java connector mysql-connector-java-5.1.23 (note the changes 
   can be applied to this version only, contact me if you need port for other versions)
4. Unzip/untar in mysql-connector-java-5.1.23
5. Copy all files from this directory in mysql-connector-java-5.1.23 with overwrite
6. Run build script
7. Enjoy Java 8 MySCL drive in bin/mysql-connector-java8-5.1.23.jar
  
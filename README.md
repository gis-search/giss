# GISS

There are three modules:

- giss-core — utility module for holding common code, properties and dependencies
- giss-api
- giss-indexer

Project setup steps:

1. Create `giss-core/src/main/resources/core.properties` file. `core.properties.example` may help you.
2. [Download](https://yadi.sk/d/bs0Pl3PZ3GZgRj) the csv file with addresses.
3. Set `giss.addresses.csv` property value of `core.properties` to the path you've downloaded the file to on step 2.
4. Set the destination path of the file with protobuf addresses in `giss.addresses.proto` property.
5. Run giss-indexer Main class to generate the file with protobuf addresses.
6. Now it is possible to start up giss-api.
# GISS

There are three modules:

- giss-core — utility module for holding common code, properties and dependencies
- giss-api
- giss-indexer

Project setup steps:

1. Create `giss-core/src/main/resources/core.properties` file. `core.properties.example` may help you.
2. [Download](https://yadi.sk/d/bs0Pl3PZ3GZgRj) the csv file with addresses.
3. [Download](https://yadi.sk/d/acRDubsk3Hzgr6) the csv file with aliases.
4. Set `giss.addresses.csv` and `giss.aliases.csv` property value of `core.properties` to paths you've downloaded the files to on steps 2 and 3.
5. Set the destination path of the file with protobuf addresses in `giss.addresses.proto` property.
6. Run giss-indexer Main class to generate the file with protobuf addresses.
7. Now it is possible to start up giss-api.
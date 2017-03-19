# GISS

There are three modules:

- giss-core — utility module for holding common code, properties and dependencies
- giss-api
- giss-indexer

Project setup steps:

1. Create `giss-core/src/main/resources/core.properties` file. `core-properties-example` may help you.
2. [Download](https://yadi.sk/d/OM4u11bZ3G9UAT) the protobuf file with addresses.
3. Set `giss.addresses.proto` property value of `core.properties` to the path you've downloaded the file to on step 2.

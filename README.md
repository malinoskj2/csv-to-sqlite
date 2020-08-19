# csv-to-sqlite

A Java application that will consume a CSV file, parse the data, and insert valid records into an SQLite database.


## Getting Started

### Requirements
 * Git
 * JDK 8
 * Maven

### Running the application
Clone the repository and run the following from the root directory.

```
# Where $CSV_PATH is the path to the csv to be processed
mvn clean compile exec:java -Dexec.args="$CSV_PATH"
```

## Approach

### Driver Class
The initial entry point into the application. The primary purpose of this class it to setup dependencies like database connections and files which the CsvProcessor class will depend on.

### CsvProcessor Class
Facilitates the actual work of the application. CSV's are parsed using Apache's Commons CSV. Once parsed they are inserted into an SQLite database using JDBC.

Records that contain the correct number of elements are inserted into a database titled `<input-filename>.db`. Bad records are inserted into `<input-filename>-bad.csv`. At the end of processing the number of received, successful, and failed records are written to a log file `<input-filename>.log`

### Performance Considerations
1. I have chosen a reasonable buffer size for reading in the CSV file. This will allow very large CSV's to be processed while having good read performance. The value is hardcoded, but a mechanism to set it through a command-line argument or environment variable could easily be implemented depending on the client's needs.
2. Database insertions are batched for more efficient writing into the SQLite database.
3. Auto-Commit is disabled for the database connections to minimize overhead.

### Assumptions
1. The database is located in the same directory as the input CSV file and should be created if it does not exist.
2. The bad record CSV is located in the same directory as the input CSV file and should be created if not.
3. The input CSV file has the .csv file name extension

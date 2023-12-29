# Database Project

This project is a simple database management system implemented in Java. It provides basic functionality for interacting with a database, including operations like loading queries from a file and executing them.

## Structure

The project is structured into several packages, each responsible for a different aspect of the database system:

-   `app`: Contains the main entry point of the application in the [`QueryManager.java`](CODE/src/up/mi/bdda/app/QueryManager.java) file.
-   `database`: Contains classes for managing the database, including [`DBManager.java`](CODE/src/up/mi/bdda/app/database/DBManager.java) and [`DatabaseInfo.java`](CODE/src/up/mi/bdda/app/database/DatabaseInfo.java), and various classes in the api, operations, and query subpackages.
-   `file`: Contains the [`DBFileManager.java`](CODE/src/up/mi/bdda/app/file/DBFileManager.java) class for managing file operations.
-   `page`: Contains classes for managing pages, such as [`PageId.java`](CODE/src/up/mi/bdda/app/page/PageId.java), [`HeaderPage.java`](CODE/src/up/mi/bdda/app/page/HeaderPage.java), and [`DataPage.java`](CODE/src/up/mi/bdda/app/page/DataPage.java).
-   `disk`: Contains the [`DiskManager.java`](CODE/src/up/mi/bdda/app/disk/DiskManager.java) class for managing disk operations.
-   `buffer`: Contains classes for managing memory frames and buffers, including [`BufferManager.java`](CODE/src/up/mi/bdda/app/buffer/BufferManager.java) and [`MemoryFrame.java`](CODE/src/up/mi/bdda/app/buffer/MemoryFrame.java).
-   `settings`: Contains classes for managing database settings, including [`DBParams.java`](CODE/src/up/mi/bdda/app/DBParams.java).
-   `utils`: Contains utility classes for the project.

## How to Run

The entry point of the project is the `main` method in the [`QueryManager.java`](CODE/src/up/mi/bdda/app/QueryManager.java) class. This method sets up the database parameters, loads queries from a file if provided, and enters a loop to accept and process user queries.

This project uses a shell script to compile and run Java files. The script navigates to the `src` directory, compiles all Java files it finds there and its subdirectories, and then runs the main class.

To run the script, follow these steps:

1. Open your terminal.
2. Navigate to the project root directory.
3. Make the script executable by running

```sh
chmod +x run.sh
```

4. Run the script with

```sh
./run.sh <query_file_path> <display_records>
```

The script will compile all the Java files in the `src` directory and its subdirectories, and then run the `QueryManager` class from the `bin` directory.

You can optionally pass a file path as a command-line argument to [`QueryManager.java`](CODE/src/up/mi/bdda/app/QueryManager.java). This file should contain queries to be executed.

The second command-line argument, `<display_records>`, is a boolean value that determines whether the records should be displayed in the terminal or not. If you want to display the records, pass `true` as the second argument. If you don't want to display the records, pass `false`.

Please note that the database folder path, page size, and maximum file and frame counts are currently hardcoded in the [`DBParams.java`](CODE/src/up/mi/bdda/app/settings/DBParams.java) class. You may need to adjust these values according to your system configuration.

# Available Operations

Based on the standard operations that are typically available in a database management system, here are some operations that might be available:

-   **Reset**: This operation allows you to rest the database. This can be done using a `RESETDB` statement.
-   **Create**: This operation allows you to create a new database or a new table in the database. This can be done using a `CREATE` statement.
-   **Read**: This operation allows you to read data from the database. This can be done using a `SELECT` statement.
-   **Delete**: This operation allows you to delete data from the database. This can be done using a `DELETE` statement.
-   **Insert**: This operation allows you to insert new data into the database. This can be done using an `INSERT` statement.

## Authors

-   **Assahe Guei** - _Initial work_ - [gugeorgy](https://github.com/gugeorgy)
-   **HAMOUCHI Nabile** - _Initial work_ - [Pepitozlp](https://github.com/Pepitozlp)
-   **CATTAN Emmanuel** - _Initial work_ - [nvbile](https://github.com/nvbile)

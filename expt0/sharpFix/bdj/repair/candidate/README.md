# Candidate

In the `candidate` directory, you can find the candidates sharpFix used to produce all the plausible patches.

Each non-Java file in the `candidate` directory has a file name that starts with a bug id (e.g., the file `bugs-dot-jar_ACCUMULO-4138_4d23d784_e0` whose name starts with the bug id `bugs-dot-jar_ACCUMULO-4138_4d23d784`). The content of that file is a string that contains (1) the candidate file path in our code database, (2) the chunk location in the file, and (3) the similarity score. For example, such a string is as follows.
> file:///vol/fullCorpus_splits/9/f0c18002-12a6-4a9c-88aa-5c5bb6e99c4d/shell/src/main/java/org/apache/accumulo/shell/commands/DeleteRowsCommand.java,slc:53,2,4.5431848

where `file:///vol/fullCorpus_splits/9/f0c18002-12a6-4a9c-88aa-5c5bb6e99c4d/shell/src/main/java/org/apache/accumulo/shell/commands/DeleteRowsCommand.java` is the candidate file path, `slc:53,2`, and `4.5431848` is the similarity score.

You can find the *candidate* file (that we copied from our code database) from the same directory. This is the Java file starting with the bug id.

The chunk location string (`slc:xxx;slc:xxx`) consists of a sequence of statement location strings connected by semi-colons. A statement location string starts with `slc:` followed by two numbers connected by a comma. The first number is the starting *line* number of the statement, and the second number is the starting *column* number of the statement.
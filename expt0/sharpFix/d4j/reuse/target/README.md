# Target

In the `target` directory, you can find the targets sharpFix used for code reuse.

Each non-Java file in the `target` directory has a file name that starts with a bug id. The content of that file is a loc string that contains the chunk location in the file. The chunk location string (`slc:xxx;slc:xxx`) consists of a sequence of statement location strings connected by semi-colons. A statement location string starts with `slc:` followed by two numbers connected by a comma. The first number is the starting *line* number of the statement, and the second number is the starting *column* number of the statement.

You can find the *target* file (that we copied from our code database) in this directory. The name of a target file starts with the bug id and ends with `.java`. You can use the loc string to locate the statements included in the target chunk.
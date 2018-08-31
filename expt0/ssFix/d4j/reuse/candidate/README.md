# Candidate

In the `candidate` directory, you can find the candidates ssFix used to produce all the plausible patches.

Each non-Java file in the `candidate` directory has a file name that starts with a bug id. The content of that file is a loc string that contains the chunk location in the file. The chunk location string (`slc:xxx;slc:xxx`) consists of a sequence of statement location strings connected by semi-colons. A statement location string starts with `slc:` followed by two numbers connected by a comma. The first number is the starting *line* number of the statement, and the second number is the starting *column* number of the statement.

You can find the *candidate* file (that we copied from our code database) in this directory. The name of a candidate file starts with the bug id and ends with `.java`. You can use the loc string to locate the statements included in the candidate chunk.

For evaluating ssFix's code reuse ability, we manually identified 20 unreasonable candidates. They are the candidates for `Chart 12`, `Chart 24`, `Chart 4`, `Closure 1`, `Closure 10`, `Closure 104`, `Closure 113`, `Closure 130`, `Closure 15`, `Closure 35`, `Closure 5`, `Closure 51`, `Closure 66`, `Closure 67`, `Lang 6`, `Math 5`, `Math 59`, `Math 75`, `Time 19`, and `Time 4`.

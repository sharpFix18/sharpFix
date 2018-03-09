# Candidate

In the `candidate` directory, you can find the candidates sharpFix used to produce all the plausible patches.

Each non-Java file in the `candidate` directory has a file name that starts with a bug id (e.g., `Math_5`). The content of that file is a string that contains (1) the candidate file path in our code database, (2) the chunk location in the file, and (3) the similarity score. For example, such a string is as follows.
> /home/qx5/defects4j_bugs/bugs/charts/projs/Chart_13_buggy/source/org/jfree/chart/block/BorderArrangement.java,slc:473,12,0.875

where `/home/qx5/defects4j_bugs/bugs/charts/projs/Chart_13_buggy/source/org/jfree/chart/block/BorderArrangement.java` is the candidate file path, `slc:473,12`, and `0.875` is the similarity score.

You can find the *candidate* file (that we copied from our code database) from the same directory. This is the Java file starting with the bug id.

The chunk location string (`slc:xxx;slc:xxx`) consists of a sequence of statement location strings connected by semi-colons. A statement location string starts with `slc:` followed by two numbers connected by a comma. The first number is the starting *line* number of the statement, and the second number is the starting *column* number of the statement.
# Candidate

In the `candidate` directory, you can find the candidates ssFix used to produce all the plausible patches.

Each non-Java file in the `candidate` directory has a file name that starts with a bug id (e.g., the file `bugs-dot-jar_ACCUMULO-4029_5ca779a0_e0` whose name starts with the bug id `bugs-dot-jar_ACCUMULO-4029_5ca779a0`). The content of that file is a string that contains (1) the candidate file path in our code database, (2) the chunk location in the file, and (3) the similarity score. For example, such a string is as follows.
> file:///vol/fullCorpus_splits/1/2c70b54a-4fd6-4953-8288-e0baad5792c0/R4_platform-aggregator-84cba7bf43da88dc3f33bc113104eb680f12aa2d/eclipse.platform.swt/bundles/org.eclipse.swt/Eclipse SWT/common/org/eclipse/swt/internal/image/JPEGStartOfImage.java,slc:33,2,6.355008

where `file:///vol/fullCorpus_splits/1/2c70b54a-4fd6-4953-8288-e0baad5792c0/R4_platform-aggregator-84cba7bf43da88dc3f33bc113104eb680f12aa2d/eclipse.platform.swt/bundles/org.eclipse.swt/Eclipse SWT/common/org/eclipse/swt/internal/image/JPEGStartOfImage.java` is the candidate file path, `slc:33,2`, and `6.355008` is the similarity score.

You can find the *candidate* file (that we copied from our code database) from the same directory. This is the Java file starting with the bug id.

The chunk location string (`slc:xxx;slc:xxx`) consists of a sequence of statement location strings connected by semi-colons. A statement location string starts with `slc:` followed by two numbers connected by a comma. The first number is the starting *line* number of the statement, and the second number is the starting *column* number of the statement.
# Retrieved Candidates

You can find the retrieved candidates under the directory `./candidates`. 
In the directory, you can find files named by the bug ids. In such a file,
you can find the ranks of the candidates that contain the fix ingredients
in the exact forms (EM), the renamed forms (RM), and the parameterized
forms (PM). In the file, you can also find the information of the candidate. 
For example, you can find a line as `EM:/home/qx5/defects4j_bugs/bugs/charts/projs/Chart_1_buggy/source/org/jfree/chart/plot/MultiplePiePlot.java,slc:566,8`.
`EM` shows the candidate contains the fix ingredient in the exact form.
The string starts with `/home/qx5/` and ends with `.java` shows the path
of the candidate file in our database. We copied such a file to `./candidates` 
whose file name starts with the bug id, ends with `.java`, and contains `em`.
`slc:566,8` is the candidate locator. You can use the candidate locator to
find the candidate code from the candidate file. A candidate locator as 
(`slc:xxx;slc:xxx`) consists of a sequence of statement location strings 
connected by semi-colons. A statement location string starts with `slc:` followed 
by two numbers connected by a comma. The first number is the starting *line* number 
of the statement, and the second number is the starting *column* number of 
the statement. For the above example, you can find the candidate statement from
`./candidates/chart1_em.java` at line 566, column 8.



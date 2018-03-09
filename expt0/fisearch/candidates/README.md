# Testing the Fix Ingredient Assumption

You can find the retrieved fix ingredients under the directories `./global`
and `./local`. In either directory, you can find files named by the bug ids. 
In such a file, you can find the information about the retrieved fix ingredient.
You can find whether a fix ingredient was retrieved in the exact form (EM) 
and/or the parameterized form (PM). For either an exact or a parameterized 
fix ingredient, you can find the file and the statement containing the fix 
ingredient.

For example, you can find the file `./local/chart1`. The file shows that for
the bug `Chart 1`, the fix ingredients in both the exact form and the parameterized
form were found. (You know this because the lines starting with `EM:` and `PM:`
are not `null,null`.) The exact fix ingredient can be found from 
`./local/chart1_em.java`. The parameterized fix ingredient can be found from 
`./local/chart1_pm.java`. To find the statements holding the fix ingredients,
you look at the strings starting with `slc:` from the `EM:` and the `RM:` 
lines. Such a string from the `EM:` line is `slc:99,8`. This means the statement
can be found from `./local/chart1_em.java` starting at line 99, column 8.
(A fix ingredient can be an expression. Look at the developer patch we provided
under the expt0/developer to find the fix ingredient expression from the 
contained statement).





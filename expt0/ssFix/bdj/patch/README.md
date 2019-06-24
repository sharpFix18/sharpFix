# Correct Patch

** ACCUMULO-4138_4d23d784 **

> In the class `OptUtil`, `START_ROW_OPT` is same as `OptUtil.START_ROW_OPT`, and the patch generated by ssFix is semantically identical to the developer patch.

** CAMEL-9217_e7ac45b6 **

> The developer patch removes the whole if-statement whose body throws an exception. The generated patch removes only the if-body. Since the if-condition `uri.contains("&") && !uri.contains("?")` has no side effects. The two patches are semantically identical.

** Others **

> The patches for `CAMEL-5707_3f70d612`, `FLINK-3189_a5b05566`, `MATH-309_0596e314`, `MATH-482_6d6649ef`, `MATH-618_2123f780`, `MATH-781_3c4cb189`, `OAK-3089_ba38c380`, and `WICKET-5056_56169634` are identical to the developer patches.
# Correct Patches

** Chart 12 **
> `this.setDataset(dataset)` is semantically equivalent to `setDataset(dataset)`.

** Math 94 **
> The developer patch changes `if(u*v == 0) {...}` to `if(u==0 || v==0) {...}` to 
avoid an integer overflow. The generated patch changes `if(u*v == 0) {...}` to
`if(u*v==0) { if(u==0 || v==0) {...} }`. Since `u*v==0` is a condition weaker than
`u==0 || v==0`, the two patches are equivalent.

** Others **
> The other plausible patches generated are identical to the corresponding developer patches.


# Scope

"scope" as the set of rules that govern how the Engine can look up a variable by its identifier name and find it, either in the current Scope, or in any of the Nested Scopes it's contained within.
There are two predominant models for how scope works. The first of these is by far the most common, used by the vast majority of programming languages. It's called Lexical Scope, and we will examine it in-depth. The other model, which is still used by some languages (such as Bash scripting, some modes in Perl, etc.) is called Dynamic Scope.

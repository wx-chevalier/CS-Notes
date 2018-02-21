# AOP Dynamic Tracing

Quasar 是基于字节码 Instrumentation 技术的栈实现，需要使用者手工标注哪些方法是在协程中使用的，无疑会增加开发难度。此外，在 Quasar 的协程中无法使用第三方库，除非对于第三方库的所有类的方法都进行一遍审查和标注。

// !WITH_NEW_INFERENCE
fun foo(<!UNUSED_PARAMETER!>f<!>: String.() -> Int) {}
val test = foo(<!NI;TYPE_MISMATCH, NI;TYPE_MISMATCH, TYPE_MISMATCH!>fun <!NI;EXPECTED_PARAMETERS_NUMBER_MISMATCH!>()<!> = <!UNRESOLVED_REFERENCE!>length<!><!>)
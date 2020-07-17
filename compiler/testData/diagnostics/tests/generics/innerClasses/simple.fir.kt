// !CHECK_TYPE
// !DIAGNOSTICS: -UNUSED_EXPRESSION -UNUSED_PARAMETER

class Outer<E> {
    inner class Inner {
        fun foo() = this
        fun baz(): Inner = this
    }

    fun bar() = Inner()

    fun set(inner: Inner) {}
}

fun factoryString(): Outer<String>.Inner = null!!

fun <T> infer(x: T): Outer<T>.Inner = null!!
val inferred = infer("")

fun main() {
    val outer = Outer<String>()

    checkSubtype<Outer<String>.Inner>(outer.bar())
    checkSubtype<Outer<String>.Inner>(outer.Inner())
    checkSubtype<Outer<*>.Inner>(outer.bar())
    checkSubtype<Outer<*>.Inner>(outer.Inner())

    <!INAPPLICABLE_CANDIDATE!>checkSubtype<!><Outer<CharSequence>.Inner>(outer.bar())
    <!INAPPLICABLE_CANDIDATE!>checkSubtype<!><Outer<CharSequence>.Inner>(outer.Inner())

    outer.<!INAPPLICABLE_CANDIDATE!>set<!>(outer.bar())
    outer.<!INAPPLICABLE_CANDIDATE!>set<!>(outer.Inner())

    val x: Outer<String>.Inner = factoryString()
    outer.<!INAPPLICABLE_CANDIDATE!>set<!>(x)
}
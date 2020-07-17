// KJS_WITH_FULL_RUNTIME
// Auto-generated by org.jetbrains.kotlin.generators.tests.GenerateRangesCodegenTestData. DO NOT EDIT!
// WITH_RUNTIME


const val MaxUI = UInt.MAX_VALUE
const val MaxUB = UByte.MAX_VALUE
const val MaxUS = UShort.MAX_VALUE
const val MaxUL = ULong.MAX_VALUE

fun box(): String {
    val list1 = ArrayList<UInt>()
    val range1 = (MaxUI - 2u)..MaxUI step 2
    for (i in range1) {
        list1.add(i)
        if (list1.size > 23) break
    }
    if (list1 != listOf<UInt>(MaxUI - 2u, MaxUI)) {
        return "Wrong elements for (MaxUI - 2u)..MaxUI step 2: $list1"
    }

    val list2 = ArrayList<UInt>()
    val range2 = (MaxUB - 2u).toUByte()..MaxUB step 2
    for (i in range2) {
        list2.add(i)
        if (list2.size > 23) break
    }
    if (list2 != listOf<UInt>((MaxUB - 2u).toUInt(), MaxUB.toUInt())) {
        return "Wrong elements for (MaxUB - 2u).toUByte()..MaxUB step 2: $list2"
    }

    val list3 = ArrayList<UInt>()
    val range3 = (MaxUS - 2u).toUShort()..MaxUS step 2
    for (i in range3) {
        list3.add(i)
        if (list3.size > 23) break
    }
    if (list3 != listOf<UInt>((MaxUS - 2u).toUInt(), MaxUS.toUInt())) {
        return "Wrong elements for (MaxUS - 2u).toUShort()..MaxUS step 2: $list3"
    }

    val list4 = ArrayList<ULong>()
    val range4 = MaxUL - 2u..MaxUL step 2
    for (i in range4) {
        list4.add(i)
        if (list4.size > 23) break
    }
    if (list4 != listOf<ULong>(MaxUL - 2u, MaxUL)) {
        return "Wrong elements for MaxUL - 2u..MaxUL step 2: $list4"
    }

    return "OK"
}

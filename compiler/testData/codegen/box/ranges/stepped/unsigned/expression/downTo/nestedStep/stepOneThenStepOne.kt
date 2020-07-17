// Auto-generated by GenerateSteppedRangesCodegenTestData. Do not edit!
// KJS_WITH_FULL_RUNTIME
// WITH_RUNTIME
import kotlin.test.*

fun box(): String {
    val uintList = mutableListOf<UInt>()
    val uintProgression = 4u downTo 1u
    for (i in uintProgression step 1 step 1) {
        uintList += i
    }
    assertEquals(listOf(4u, 3u, 2u, 1u), uintList)

    val ulongList = mutableListOf<ULong>()
    val ulongProgression = 4uL downTo 1uL
    for (i in ulongProgression step 1L step 1L) {
        ulongList += i
    }
    assertEquals(listOf(4uL, 3uL, 2uL, 1uL), ulongList)

    return "OK"
}
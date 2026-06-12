import kotlinx.coroutines.*
import mymodule.bar
import kotlin.time.measureTime

fun main() {
    println("Hello from Kotlin/Wasm")

    println("\n--- Sequence aka generator ---")

    testSequence()

    println("\n--- Coroutines ---")
    MainScope().launch {
        testCoroutines()

        println("\n--- bar() from mymodule ---")
        bar()
    }
}

fun testSequence() {
    val fibonacci = sequence {
        var a = 0
        var b = 1
        yield(a)
        yield(b)
        while (true) {
            val next = a + b
            yield(next)
            a = b
            b = next
        }
    }

    var sum = 0
    val duration = measureTime {
        sum = fibonacci.take(10_000_000).sum()
        
    }

    println("Sum: $sum")
    println("Duration: ${duration.inWholeMilliseconds}ms")
}

suspend fun testCoroutines() {
    val itemCount = 10

    val duration = measureTime {
        repeat(itemCount) { i ->
            delay(100)
            println("Sequential task $i completed")
        }
    }
    println("Duration: ${duration.inWholeMilliseconds}ms")
}

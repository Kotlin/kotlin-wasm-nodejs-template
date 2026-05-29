import kotlinx.coroutines.*
import kotlin.time.measureTime

fun main() {
    println("Hello from Kotlin/Wasm")

    println("\n--- Sequence aka generator ---") 
    testSequence()
    
    println("\n--- Coroutines ---")
    MainScope().launch {
        testCoroutines()
    }
}

fun testSequence() {
    val range = sequence {
        var i = 0
        while (true) {
            yield(i++)
        }
    }

    var sum = 0
    val duration = measureTime {
        sum = range.take(10_000_000).sum()
        
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

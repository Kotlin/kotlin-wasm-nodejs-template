import kotlinx.coroutines.*
import kotlin.time.measureTime

fun main() {
    println("Hello from Kotlin/Wasm")

    println("\n--- Sequence aka generator ---") 
    testSequence()
    
    println("\n--- Treesum ---")
    MainScope().launch {
        testTreesum()
    }

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

class Node(val height: Int, val left: Node?, val right: Node?) {
}

fun buildTree(height: Int) : Node {
    if (height == 1) {
        return Node(1, null, null)
    }

    val child = buildTree(height - 1)

    return Node(height, child, child)
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

suspend fun SequenceScope<Int>.searchTree(t: Node) { //}: Sequence<Int> = sequence {
    if (t.left == null || t.right == null) {
        yield(t.height)
    } else {
        searchTree(t.left)
        searchTree(t.right)
    }
}

fun testTreesum() {
    val tree = buildTree(25)

    val range = sequence {searchTree(tree)}

    var sum = 0
    val duration = measureTime {
        sum = range.sum()
    }

    println("Sum: $sum")
    println("Duration: ${duration.inWholeMilliseconds}ms")
}

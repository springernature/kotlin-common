package com.springer.common

import java.io.StringWriter

inline fun <T, R> T?.ifNotNull(f: (T) -> R): R? = this?.let(f)

fun <T> measure(timings: (Long) -> Unit, f: () -> T): T {
    val start = System.currentTimeMillis()
    val result = f()
    timings(System.currentTimeMillis() - start)
    return result
}

fun <T> Iterable<T>.zipWithIndex(): Iterable<Pair<T, Int>> = this.zip(0..Int.MAX_VALUE)
fun <T> Sequence<T>.zipWithIndex(): Sequence<Pair<T, Int>> = this.zip((0..Int.MAX_VALUE).asSequence())

inline fun <T : AutoCloseable, R> T.use(block: (T) -> R): R {
    return try {
        block(this)
    } finally {
        close()
    }
}

fun <T : AutoCloseable> T.closeOnExit(): T {
    Runtime.getRuntime().addShutdownHook(Thread {
        close()
    })
    return this
}

fun Any.formattedAs(formatString: String) = formatString.format(this)

fun <F, S, F2> Pair<F, S>.mapFirst(fn: (F) -> F2) = Pair(fn(first), second)
fun <F, S, F2> Iterable<Pair<F, S>>.mapFirst(fn: (F) -> F2) = map { it.mapFirst(fn) }

fun <F, S, S2> Pair<F, S>.mapSecond(fn: (S) -> S2) = Pair(first, fn(second))

fun <F, S> Pair<F, S?>.withSecondOrNull(): Pair<F, S>? = second?.let { first to it }

fun <F, S, S2> Iterable<Pair<F, S>>.mapSecond(fn: (S) -> S2) = map { it.mapSecond(fn) }

fun <T, R> Sequence<T>.collectTo(accumulator: R, accumulation: (R, T) -> Unit) =
    this.fold(accumulator, { acc, item -> accumulation(acc, item); acc })

fun <T, R> Iterable<T>.collectTo(accumulator: R, accumulation: (R, T) -> Unit) =
    this.fold(accumulator, { acc, item -> accumulation(acc, item); acc })

fun <R, S> sequence(initialSeed: S, generator: (S) -> Pair<R, S>?) =
    generateSequence(generator(initialSeed), { p: Pair<R, S> -> generator(p.second) }).map { it.first }

fun <R, S> flatSequence(initialSeed: S, generator: (S) -> Pair<Iterable<R>, S>?) =
    sequence(initialSeed, generator).flatten()

fun <K, V> MutableMap<K, V>.putAll(vararg pairs: Pair<K, V>) {
    this.putAll(pairs.asIterable())
}

fun String?.quoted() = "\"$this\""

fun <T> T.printed(): T = this.apply { println(this) }
fun <T> Iterable<T>.printed() = this.map { it.printed() }
fun <T> Sequence<T>.printed() = this.map { it.printed() }

fun <T> T.printedToStdErr(): T = this.apply { System.err.println(this) }
fun <T> Iterable<T>.printedToStdErr() = this.map { it.printed().printedToStdErr() }
fun <T> Sequence<T>.printedToStdErr() = this.map { it.printed().printedToStdErr() }

// set a breakpoint in me and wrap me around an expression
fun <T> breakpoint(thing: T): T = thing

fun <T> printExceptions(block: () -> T): T =
    try {
        block()
    } catch (e: Throwable) {
        e.printStackTrace()
        throw e
    }

fun <T> Sequence<T>.onEachWithIndex(action: (Int, T) -> Unit): Sequence<T> {
    var index = 0
    return map {
        action(index++, it)
        it
    }
}

fun Throwable.toStringStacktrace() = this.let {
    val stringWriter = StringWriter()
    it.printStackTrace(java.io.PrintWriter(stringWriter))
    stringWriter.toString()
}

inline fun <T : Any, R> T?.letOrElse(block: (T) -> R, orElse: () -> R): R =
    when (this) {
        null -> orElse()
        else -> block(this)
    }

inline fun <T> T.applyIf(predicate: () -> Boolean, block: T.() -> Unit): T {
    when {
        predicate() -> block()
    }
    return this
}

val Unit.exhaustive get() = this
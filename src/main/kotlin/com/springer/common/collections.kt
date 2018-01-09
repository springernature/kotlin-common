package com.springer.common

import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

inline fun <T, R> Iterable<T>.pmap(parallelism: Int = 4, crossinline transform: (T) -> R): List<R> {
    val exec = Executors.newFixedThreadPool(parallelism)
    return try {
        this
            .map { exec.submit(Callable { transform(it) }) }
            .map {
                try {
                    it.get()
                } catch (e: ExecutionException) {
                    throw e.cause ?: e
                }
            }
    } finally {
        exec.shutdownNow()
    }
}

fun <T, R> Sequence<T>.pmap(parallelism: Int = 4, transform: (T) -> R): Sequence<R> = iterator().pmap(parallelism, transform)

fun <T, R> Iterator<T>.pmap(parallelism: Int = 4, transform: (T) -> R): Sequence<R> =
    generateSequence {
        if (hasNext().not()) null
        else PmapPage(this, pmapChunk(parallelism, transform))
    }
        .flatMap { it.results }.filterNot { it == null }

private fun <T, R> Iterator<T>.pmapChunk(chunkSize: Int, transform: (T) -> R): Sequence<R> =
    asSequence().take(chunkSize).toList().pmap(chunkSize, transform).asSequence()

private data class PmapPage<out T, out R>(val remaining: Iterator<T>, val results: Sequence<R>)


fun <T, R> Iterable<T>.findResult(transform: (T) -> R): R? =
    this.asSequence().map { transform(it) }.firstOrNull { it != null }

inline fun <T> Sequence<T>.onEachIndexed(crossinline f: (Int, T) -> Unit): Sequence<T> {
    var i = 0
    return this.onEach {
        f(i, it)
        i += 1
    }
}
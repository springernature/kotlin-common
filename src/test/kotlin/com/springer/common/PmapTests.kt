package com.springer.common

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.isA
import com.natpryce.hamkrest.throws
import org.junit.Test

class PmapTests {

    @Test fun `Maps a list`() {
        val results: List<Int> = listOf(1, 2, 3)
            .pmap { it + 1 }

        assertThat(results, equalTo(listOf(2, 3, 4)))
    }

    @Test fun `Throws exceptions from the mapping function`() {
        assertThat(
            {listOf(1, 2, 3).pmap { if (it == 2) throw IllegalArgumentException("2") else it + 1 }},
            throws(isA<IllegalArgumentException>(has(Throwable::message, equalTo("2"))))
        )
    }

    @Test fun `Map a sequence with simple transform`() {
        val results: List<Int> = sequenceOf(1, 2, 3)
            .pmap(parallelism = 2, transform = { it + 1 })
            .toList()

        assertThat(results, equalTo(listOf(2, 3, 4)))
    }
}
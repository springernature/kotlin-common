package com.springer.common

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test

class FindResultTests {

    @Test fun `return first result of transform or null`() {
        assertThat(emptyList<Int?>().findResult{ it?.plus(1) }, equalTo(null as Int?))
        assertThat(listOf(1).findResult{ it + 1 }, equalTo(2))

        assertThat(listOf(1, null).findResult{ it?.plus(1) }, equalTo(2))
        assertThat(listOf(1, 2, null).findResult{ it?.plus(1) }, equalTo(2))

        assertThat(listOf(null, 2).findResult{ it?.plus(1) }, equalTo(3))
        assertThat(listOf(null, null, 3).findResult{ it?.plus(1) }, equalTo(4))
    }
}
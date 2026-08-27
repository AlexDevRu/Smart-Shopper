package com.example.domain.common

import kotlinx.coroutines.CancellationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KotlinUtilsTest {

    @Test
    fun `runCatchingCancellable returns success when block succeeds`() {
        val result = Unit.runCatchingCancellable {
            "Success"
        }

        assertTrue(result.isSuccess)
        assertEquals("Success", result.getOrNull())
    }

    @Test
    fun `runCatchingCancellable returns failure when block throws general exception`() {
        val exception = RuntimeException("General Error")
        val result = Unit.runCatchingCancellable {
            throw exception
        }

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test(expected = CancellationException::class)
    fun `runCatchingCancellable rethrows CancellationException`() {
        // This is the most critical test case to ensure coroutine cancellation works
        Unit.runCatchingCancellable {
            throw CancellationException("Cancelled")
        }
    }
}

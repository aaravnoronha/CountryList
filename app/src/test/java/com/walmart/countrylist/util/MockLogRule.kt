package com.walmart.countrylist.util

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MockLogRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                mockkStatic(Log::class)
                every { Log.d(any(), any()) } returns 0
                every { Log.e(any(), any()) } returns 0
                every { Log.i(any(), any()) } returns 0
                every { Log.v(any(), any()) } returns 0
                every { Log.w(any<String>(), any<String>()) } returns 0
                every { Log.w(any<String>(), any<Throwable>()) } returns 0
                every { Log.w(any<String>(), any<String>(), any<Throwable>()) } returns 0

                base.evaluate()
            }
        }
    }
}
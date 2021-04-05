package com.appsci.panda.sdk.data.db.migrations

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import com.appsci.panda.sdk.data.db.PandaDatabase
import org.junit.After
import org.junit.Ignore
import org.junit.Rule

@Ignore("Base class")
abstract class BaseMigrationTest {

    companion object {
        const val TEST_DB = "test-db"
    }

    @Rule
    @JvmField
    val helper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
            PandaDatabase::class.java.canonicalName, FrameworkSQLiteOpenHelperFactory())

    @After
    @Throws(Exception::class)
    fun tearDown() {
        InstrumentationRegistry.getInstrumentation().context.deleteDatabase(TEST_DB)
    }
}

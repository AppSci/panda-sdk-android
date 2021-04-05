package com.appsci.panda.sdk.data.db.migrations

import org.junit.Test

internal class Migration1To2Test : BaseMigrationTest() {

    @Test
    fun migrate() {
        helper.createDatabase(TEST_DB, 1)

        helper.runMigrationsAndValidate(TEST_DB, 2, true, Migration1To2())
    }
}

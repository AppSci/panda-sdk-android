package com.appsci.panda.sdk.data.db.migrations

import org.junit.Test

internal class Migration2To3Test : BaseMigrationTest() {

    @Test
    fun migrate() {
        helper.createDatabase(TEST_DB, 2)

        helper.runMigrationsAndValidate(TEST_DB, 3, true, Migration2To3())
    }
}

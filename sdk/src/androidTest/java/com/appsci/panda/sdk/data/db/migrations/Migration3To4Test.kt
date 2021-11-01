package com.appsci.panda.sdk.data.db.migrations

import org.junit.Test

internal class Migration3To4Test : BaseMigrationTest() {

    @Test
    fun migrate() {
        helper.createDatabase(TEST_DB, 3)

        helper.runMigrationsAndValidate(TEST_DB, 4, true, Migration3To4())
    }
}

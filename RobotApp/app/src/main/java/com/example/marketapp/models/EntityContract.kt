package com.example.marketapp.models

import android.provider.BaseColumns

object EntityContract {
    const val DB_NAME = "new_robots_db"
    const val DB_VERSION = 1

    // Table contents are grouped together in an anonymous object.
    object TaskEntry : BaseColumns {
        const val DB_TABLE = "robots_table"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_SPECS = "specs"
        const val COLUMN_HEIGHT = "height"
        const val COLUMN_TYPE = "type"
        const val COLUMN_AGE = "age"

    }

    object TypeEntry: BaseColumns {
        const val DB_TABLE = "types_table"
        const val COLUMN_TYPE = "type"
    }
}
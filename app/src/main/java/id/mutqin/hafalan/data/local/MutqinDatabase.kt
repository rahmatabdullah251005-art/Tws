package id.mutqin.hafalan.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        SurahEntity::class,
        HafalanSessionEntity::class,
        RubuStatusEntity::class,
        StudentProfileEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class MutqinDatabase : RoomDatabase() {
    abstract fun mutqinDao(): MutqinDao

    companion object {
        @Volatile
        private var INSTANCE: MutqinDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `surahs` (
                        `number` INTEGER NOT NULL,
                        `nameIndonesian` TEXT NOT NULL,
                        `nameArabic` TEXT NOT NULL,
                        `ayahCount` INTEGER NOT NULL,
                        `revelationPlace` TEXT NOT NULL,
                        PRIMARY KEY(`number`)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `hafalan_sessions` (
                        `sessionNo` INTEGER NOT NULL,
                        `juz` INTEGER NOT NULL,
                        `setoranTitle` TEXT NOT NULL,
                        `submissionDate` TEXT NOT NULL,
                        `grade` TEXT NOT NULL,
                        `notes` TEXT NOT NULL,
                        `updatedAtMillis` INTEGER NOT NULL,
                        PRIMARY KEY(`sessionNo`)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `rubu_statuses` (
                        `rubuNumber` INTEGER NOT NULL,
                        `sessionNo` INTEGER NOT NULL,
                        `positionInSession` INTEGER NOT NULL,
                        `rubuLabel` TEXT NOT NULL,
                        `startSurahNo` INTEGER NOT NULL,
                        `startAyah` INTEGER NOT NULL,
                        `endSurahNo` INTEGER NOT NULL,
                        `endAyah` INTEGER NOT NULL,
                        `isCompleted` INTEGER NOT NULL,
                        `completedAtMillis` INTEGER,
                        PRIMARY KEY(`rubuNumber`),
                        FOREIGN KEY(`sessionNo`) REFERENCES `hafalan_sessions`(`sessionNo`) ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY(`startSurahNo`) REFERENCES `surahs`(`number`) ON UPDATE NO ACTION ON DELETE RESTRICT,
                        FOREIGN KEY(`endSurahNo`) REFERENCES `surahs`(`number`) ON UPDATE NO ACTION ON DELETE RESTRICT
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_rubu_statuses_sessionNo` ON `rubu_statuses` (`sessionNo`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_rubu_statuses_startSurahNo` ON `rubu_statuses` (`startSurahNo`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_rubu_statuses_endSurahNo` ON `rubu_statuses` (`endSurahNo`)")
                db.execSQL("DROP TABLE IF EXISTS `setoran_records`")
            }
        }

        fun getInstance(context: Context): MutqinDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MutqinDatabase::class.java,
                    "mutqin_hafalan.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

package de.selfmade4u.glowingp2p

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "active_endpoints")
data class ActiveEndpoint (
    @PrimaryKey
    @ColumnInfo(name = "endpoint_id")
    val endpointId: String,
)

@Dao
interface ActiveEndpointsDao {
    @Query("SELECT * FROM active_endpoints")
    fun getAll(): Flow<List<ActiveEndpoint>>

    @Query("SELECT * FROM active_endpoints WHERE endpoint_id = :endpointId")
    fun loadByEndpointId(endpointId: String): List<ActiveEndpoint>

    @Insert
    fun insert(endpoint: ActiveEndpoint)

    @Delete
    fun delete(endpoint: ActiveEndpoint)

    @Query("DELETE FROM active_endpoints")
    fun deleteAll()
}

open class SingletonHolder<out T: Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}

@Database(entities = [ActiveEndpoint::class], version = 1, /*autoMigrations = [
    AutoMigration (from = 1, to = 2, spec = AppDatabase.MyAutoMigration1_2::class)
]*/)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activeEndpointDao(): ActiveEndpointsDao

    //@DeleteTable(tableName = "users")
    //class MyAutoMigration1_2 : AutoMigrationSpec

    companion object : SingletonHolder<AppDatabase, Context>({
        Room.databaseBuilder(it.applicationContext,
            AppDatabase::class.java, "Sample.db")
            .allowMainThreadQueries()
            .build()
    })
}
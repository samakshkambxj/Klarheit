package com.klarheit.audio.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.klarheit.audio.data.dao.DeviceSettingsDao
import com.klarheit.audio.data.dao.DsPresetDao
import com.klarheit.audio.data.dao.EqPresetDao
import com.klarheit.audio.data.dao.PresetDao
import com.klarheit.audio.data.db.KlarheitDatabase
import com.klarheit.audio.data.model.DsPreset
import com.klarheit.audio.data.model.EqPreset
import com.klarheit.audio.effect.BuiltinPresets
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "klarheit_preferences")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): KlarheitDatabase {
        lateinit var klarheitDb: KlarheitDatabase
        klarheitDb =
            Room
                .databaseBuilder(
                    context,
                    KlarheitDatabase::class.java,
                    "klarheit.db",
                ).addMigrations(
                    KlarheitDatabase.MIGRATION_1_2,
                    KlarheitDatabase.MIGRATION_2_3,
                    KlarheitDatabase.MIGRATION_3_4,
                    KlarheitDatabase.MIGRATION_4_5,
                    KlarheitDatabase.MIGRATION_5_6,
                    KlarheitDatabase.MIGRATION_6_7,
                ).addCallback(
                    object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                seedEqPresets(klarheitDb.eqPresetDao())
                                seedDsPresets(klarheitDb.dsPresetDao())
                            }
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val eqDao = klarheitDb.eqPresetDao()
                                if (eqDao.countBuiltins() == 0) {
                                    seedEqPresets(eqDao)
                                }
                                val dsDao = klarheitDb.dsPresetDao()
                                if (dsDao.countBuiltins() == 0) {
                                    seedDsPresets(dsDao)
                                }
                            }
                        }
                    },
                ).build()
        return klarheitDb
    }

    private suspend fun seedEqPresets(dao: EqPresetDao) {
        val presets = mutableListOf<EqPreset>()
        for (builtin in BuiltinPresets.BUILTIN_EQ_PRESETS) {
            val bandsByCount =
                mapOf(
                    10 to builtin.bands10,
                    15 to builtin.bands15,
                    25 to builtin.bands25,
                    31 to builtin.bands31,
                )
            for ((bandCount, bands) in bandsByCount) {
                presets.add(
                    EqPreset(
                        name = builtin.key,
                        nameKey = builtin.key,
                        bandCount = bandCount,
                        bands = bands,
                    ),
                )
            }
        }
        dao.insertAll(presets)
    }

    private suspend fun seedDsPresets(dao: DsPresetDao) {
        val presets =
            BuiltinPresets.BUILTIN_DS_PRESETS.map { builtin ->
                DsPreset(
                    name = builtin.key,
                    nameKey = builtin.key,
                    xLow = builtin.xLow,
                    xHigh = builtin.xHigh,
                    yLow = builtin.yLow,
                    yHigh = builtin.yHigh,
                    sideGainLow = builtin.sideGainLow,
                    sideGainHigh = builtin.sideGainHigh,
                )
            }
        dao.insertAll(presets)
    }

    @Provides
    @Singleton
    fun providePresetDao(database: KlarheitDatabase): PresetDao = database.presetDao()

    @Provides
    @Singleton
    fun provideEqPresetDao(database: KlarheitDatabase): EqPresetDao = database.eqPresetDao()

    @Provides
    @Singleton
    fun provideDsPresetDao(database: KlarheitDatabase): DsPresetDao = database.dsPresetDao()

    @Provides
    @Singleton
    fun provideDeviceSettingsDao(database: KlarheitDatabase): DeviceSettingsDao = database.deviceSettingsDao()

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.dataStore

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient
            .Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
}

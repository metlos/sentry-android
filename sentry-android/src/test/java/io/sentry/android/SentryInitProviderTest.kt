package io.sentry.android

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ProviderInfo
import android.os.Bundle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.sentry.InvalidDsnException
import io.sentry.Sentry
import org.junit.runner.RunWith
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class SentryInitProviderTest {
    private var sentryInitProvider: SentryInitProvider = SentryInitProvider()

    private lateinit var context: Context

    @BeforeTest
    fun `set up`() {
        context = ApplicationProvider.getApplicationContext()
        Sentry.close()
    }

    @Test
    fun `when missing applicationId, SentryInitProvider throws`() {
        val providerInfo = ProviderInfo()

        providerInfo.authority = AUTHORITY
        assertFailsWith<IllegalStateException> { sentryInitProvider.attachInfo(context, providerInfo) }
    }

    @Test
    fun `when applicationId is defined, dsn in meta-data, SDK initializes`() {
        val providerInfo = ProviderInfo()

        assertFalse(Sentry.isEnabled())
        providerInfo.authority = BuildConfig.LIBRARY_PACKAGE_NAME + AUTHORITY

        val mockContext: Context = mock()
        val metaData = Bundle()
        mockMetaData(mockContext, metaData)

        metaData.putString(ManifestMetadataReader.DSN_KEY, "https://key@sentry.io/123")

        sentryInitProvider.attachInfo(mockContext, providerInfo)

        assertTrue(Sentry.isEnabled())
    }

    @Test
    fun `when applicationId is defined, dsn in meta-data is not set, SDK doesnt initialize`() {
        val providerInfo = ProviderInfo()

        assertFalse(Sentry.isEnabled())
        providerInfo.authority = BuildConfig.LIBRARY_PACKAGE_NAME + AUTHORITY

        val mockContext: Context = mock()
        val metaData = Bundle()
        mockMetaData(mockContext, metaData)

        sentryInitProvider.attachInfo(mockContext, providerInfo)

        assertFalse(Sentry.isEnabled())
    }

    @Test
    fun `when applicationId is defined, dsn in meta-data is empty, SDK doesnt initialize`() {
        val providerInfo = ProviderInfo()

        assertFalse(Sentry.isEnabled())
        providerInfo.authority = BuildConfig.LIBRARY_PACKAGE_NAME + AUTHORITY

        val mockContext: Context = mock()
        val metaData = Bundle()
        mockMetaData(mockContext, metaData)

        metaData.putString(ManifestMetadataReader.DSN_KEY, "")

        sentryInitProvider.attachInfo(mockContext, providerInfo)

        assertFalse(Sentry.isEnabled())
    }

    @Test
    fun `when applicationId is defined, dsn in meta-data is null, SDK doesnt initialize`() {
        val providerInfo = ProviderInfo()

        assertFalse(Sentry.isEnabled())
        providerInfo.authority = BuildConfig.LIBRARY_PACKAGE_NAME + AUTHORITY

        val mockContext: Context = mock()
        val metaData = Bundle()
        mockMetaData(mockContext, metaData)

        metaData.putString(ManifestMetadataReader.DSN_KEY, null)

        sentryInitProvider.attachInfo(mockContext, providerInfo)

        assertFalse(Sentry.isEnabled())
    }

    @Test
    fun `when applicationId is defined, dsn in meta-data is invalid, SDK should throw an error`() {
        val providerInfo = ProviderInfo()

        assertFalse(Sentry.isEnabled())
        providerInfo.authority = BuildConfig.LIBRARY_PACKAGE_NAME + AUTHORITY

        val mockContext: Context = mock()
        val metaData = Bundle()
        mockMetaData(mockContext, metaData)

        metaData.putString(ManifestMetadataReader.DSN_KEY, "hj3245h27345")

        assertFailsWith<InvalidDsnException> { sentryInitProvider.attachInfo(mockContext, providerInfo) }
    }

    private fun mockMetaData(mockContext: Context, metaData: Bundle) {
        val mockPackageManager: PackageManager = mock()
        val mockApplicationInfo: ApplicationInfo = mock()

        whenever(mockContext.packageName).thenReturn(TEST_PACKAGE)
        whenever(mockContext.packageManager).thenReturn(mockPackageManager)
        whenever(mockPackageManager.getApplicationInfo(mockContext.packageName, PackageManager.GET_META_DATA)).thenReturn(mockApplicationInfo)

        mockApplicationInfo.metaData = metaData
    }

    companion object {
        private const val AUTHORITY = "io.sentry.android.SentryInitProvider"
        private const val TEST_PACKAGE = "io.sentry.android.SentryInitProvider"
    }
}

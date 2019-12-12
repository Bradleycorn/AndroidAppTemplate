package net.bradball.android.androidapptemplate.ui


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import net.bradball.android.androidapptemplate.R
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface LocationProvider {
    val activityContext: Activity

    fun onLocationActivityResult() {
    }
}

class MainActivity : DaggerAppCompatActivity(), LocationProvider {
    private val TAG = "LOCATION"

    override val activityContext: Activity = this

    companion object {
        private const val PLAY_SERVICES_REQUEST = 1
        private const val LOCATION_UPDATE_INTERVAL_SECONDS = 1
        private const val LOCATION_FAST_INTERVAL_SECONDS = .5
        private const val MINIMUM_DISPLACEMENT_METERS = 1F
    }

    private val locationRequest: LocationRequest by lazy {
        LocationRequest.create().apply {
            interval = (LOCATION_UPDATE_INTERVAL_SECONDS * 1000).toLong()
            fastestInterval = (LOCATION_FAST_INTERVAL_SECONDS * 1000).toLong()
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            smallestDisplacement =
                MINIMUM_DISPLACEMENT_METERS
        }
    }

    private val locationSettingsRequest: LocationSettingsRequest by lazy {
        LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
    }

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private val googlePlay: GoogleApiAvailability by lazy { GoogleApiAvailability.getInstance() }
    private val locationSettingsClient: SettingsClient by lazy { LocationServices.getSettingsClient(this) }

    private val permissionRequests = mutableMapOf<Int, CancellableContinuation<Boolean>>()
    private val googlePlayServicesRequests = mutableMapOf<Int, CancellableContinuation<Boolean>>()
    private val locationServiceRequests = mutableMapOf<Int, CancellableContinuation<Boolean>>()
    private val permissionRequestCounter = AtomicInteger(0)
    private val permissionRequestId: Int
        get() = permissionRequestCounter.getAndIncrement()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.app_bar)
        val navController = findNavController(R.id.nav_host_fragment)
        setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, navController)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }





    fun getLocationData(): LiveData<String> {
        val ld = MutableLiveData<String>()

        lifecycleScope.launch {
            val permissionGranted = checkAppPermissions(listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), R.string.rationale_location)

           when {
               !permissionGranted -> Snackbar.make(findViewById(R.id.nav_host_fragment), "Can't track location. Permission was denied.", Snackbar.LENGTH_SHORT).show()
               !checkGooglePlayServices() -> Snackbar.make(findViewById(R.id.nav_host_fragment), "Can't track location. Google Play Services not working.", Snackbar.LENGTH_SHORT).show()
               !checkLocationSettings() -> Snackbar.make(findViewById(R.id.nav_host_fragment), "Can't track location. Location Service is not turned on.", Snackbar.LENGTH_SHORT).show()
               else -> ld.value = "35.34567, 12.12345"
           }
        }

        return ld
    }

    @ExperimentalCoroutinesApi
    fun locationFlow(): Flow<Location> = callbackFlow  {
        val permissionGranted = checkAppPermissions(listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), R.string.rationale_location)

        when {
            !permissionGranted -> Snackbar.make(findViewById(R.id.nav_host_fragment), "Can't track location. Permission was denied.", Snackbar.LENGTH_SHORT).show()
            !checkGooglePlayServices() -> Snackbar.make(findViewById(R.id.nav_host_fragment), "Can't track location. Google Play Services not working.", Snackbar.LENGTH_SHORT).show()
            !checkLocationSettings() -> Snackbar.make(findViewById(R.id.nav_host_fragment), "Can't track location. Location Service is not turned on.", Snackbar.LENGTH_SHORT).show()
            else -> {
                val callback  = object: LocationCallback() {
                    override fun onLocationResult(p0: LocationResult) {
                        offer(p0.lastLocation)
                    }
                }
                fusedLocationClient.requestLocationUpdates(locationRequest, callback, Looper.myLooper())
                awaitClose { fusedLocationClient.removeLocationUpdates(callback) }
            }
        }
    }



    @ExperimentalCoroutinesApi
    private fun watchLocation(): Flow<Location> = callbackFlow {

    }


    suspend fun checkAppPermissions(requestedPermissions: List<String>, @StringRes rationale: Int): Boolean {
        val missingPermissions = mutableSetOf<String>()

        requestedPermissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
           }
        }

        return when (missingPermissions.size) {
            0 -> true
            else -> {
                showPermissionsRationale(missingPermissions, rationale)
                showRequestPermissionsDialog(missingPermissions)
            }
        }
    }


    private suspend fun showPermissionsRationale(missingPermissions: Set<String>, @StringRes rationale: Int) = suspendCoroutine<Unit> { continuation ->
        val showRationale = missingPermissions.any { permission -> ActivityCompat.shouldShowRequestPermissionRationale(this, permission) }

        if (showRationale) {
            val snackbar = Snackbar.make(findViewById(R.id.nav_host_fragment), rationale, Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("OK") { continuation.resume(Unit) }
            snackbar.show()
        } else {
            continuation.resume(Unit)
        }
    }

    private suspend fun showRequestPermissionsDialog(missingPermissions: Set<String>): Boolean  = suspendCancellableCoroutine { continuation ->
        val requestCode = permissionRequestId
        permissionRequests[requestCode] = continuation
        ActivityCompat.requestPermissions(this@MainActivity, missingPermissions.toTypedArray(), requestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val granted = grantResults.all { result -> result == PackageManager.PERMISSION_GRANTED }
        permissionRequests[requestCode]?.resume(granted)
        permissionRequests.remove(requestCode)
    }

    private suspend fun checkGooglePlayServices(): Boolean = suspendCancellableCoroutine { continuation ->
        val servicesResult = googlePlay.isGooglePlayServicesAvailable(this)
        when  {
            servicesResult == ConnectionResult.SUCCESS -> continuation.resume(true)
            googlePlay.isUserResolvableError(servicesResult) -> {
                val requestCode = permissionRequestId
                googlePlayServicesRequests[requestCode] = continuation
                val dialog = googlePlay.getErrorDialog(this, servicesResult, requestCode) {
                    googlePlayServicesRequests.remove(requestCode)
                    continuation.resume(false)
                }
                dialog.show()
            }
            else -> continuation.resume(false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {



        super.onActivityResult(requestCode, resultCode, data)


        googlePlayServicesRequests[requestCode]?.let { continuation ->
            googlePlayServicesRequests.remove(requestCode)
            continuation.resume(googlePlay.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS)
        }

        locationServiceRequests[requestCode]?.let { continuation ->
            //TODO - Make sure this works properly. When the user clicks "OK"
            // on the location services dialog, see what it returns and make sure this
            // returns true.
            locationServiceRequests.remove(requestCode)
            continuation.resume(resultCode == Activity.RESULT_OK)
        }
    }



    private suspend fun checkLocationSettings(): Boolean = suspendCancellableCoroutine { continuation ->
        locationSettingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener(this) { continuation.resume(true) }
            .addOnFailureListener(this) { e ->
                val statusCode = (e as ApiException).statusCode
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            val requestCode = permissionRequestId
                            locationServiceRequests[requestCode] = continuation
                            (e as? ResolvableApiException)?.startResolutionForResult(this, requestCode)
                        } catch (e: IntentSender.SendIntentException) {
                            continuation.resume(false)
                        }

                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> continuation.resume(false)
                }
            }
    }

}

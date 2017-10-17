## Getting Started with the CedarMaps Android SDK

This guide will take you through the process of adding a map to your Android app.

### Installation

To install the current stable version add this to your build.gradle:

To install the current **stable** version add this to your `build.gradle`:

```groovy
repositories {
    maven {
        url "http://repo.cedarmaps.com/android/"
    }
}

dependencies {
    compile('com.cedarmaps:CedarMapsSDK:2.0.0@aar') {
        transitive = true
    }
}
```


### Required Permissions

Ensure the following *core* permissions are requested in your `AndroidManifest.xml` file:

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

If your App needs to access location services, it'll also need the following permissions too:

```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

**Android 6.0+ devices require you have to check for "dangerous" permissions at runtime.**
CedarMaps requires the following dangerous permissions:
`WRITE_EXTERNAL_STORAGE`, `ACCESS_COARSE_LOCATION` and `ACCESS_FINE_LOCATION`.  

### Configuring CedarMaps

In order to use CedarMaps API, you should set your clientID, clientSecret and a context in your application.

```java
CedarMaps.getInstance()
    .setClientID("YOUR_CLIENT_ID")
    .setClientSecret("YOUR_CLIENT_SECRET")
    .setContext(CONTEXT)
```


### The MapView

The `MapView` class is the key component of this library. It behaves
like any other `ViewGroup` and its behavior can be changed statically with an
[XML layout](http://developer.android.com/guide/topics/ui/declaring-layout.html)
file, or programmatically during runtime.

If you want to use MapView, first call this before using any `MapView`.
```java
CedarMaps.getInstance().prepareTiles(new OnTilesConfigured() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(@NonNull String error) {

            }
        });
```

#### XML layout
To add the CedarMaps `MapView` as a layout element, add the following to your xml file:

```xml
<com.cedarstudios.cedarmapssdk.MapView
        android:id="@+id/mapView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        mapbox:mapbox_cameraZoom="14"
        ...
        />
```


And then you can call it programmatically with

```java
mMapView = (MapView) view.findViewById(R.id.mapView);

mMapView.getMapAsync(new OnMapReadyCallback() {
    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        mMapboxMap = mapboxMap;

        mMapboxMap.setMaxZoomPreference(17);
        mMapboxMap.setMinZoomPreference(6);

        MapboxMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {

            }
        });
    }
});
```

#### Changing API Base Url

You can change API Base Url by setting it on CedarMaps shared object:

```java
CedarMaps.getInstance()
    .setAPIBaseURL("YOUR_API_BASE_URL")
```
CedarMaps SDK is based on [Mapbox GL Android SDK v5.1.4](https://github.com/mapbox/mapbox-gl-native) and provides `CedarMaps` and extra API over Mapbox. 
For more information about how to use MapView and other components please see [Mapbox Getting Started](https://www.mapbox.com/help/first-steps-android-sdk/)


=======
### CedarMaps API
In addition to use MapView, you can use CedarMaps API to retrieve location based data and street search.

#### Geocode (Place Search)

For finding a street you can easily call ```forwardGeocode``` methods.

```java
CedarMaps.getInstance().forwardGeocode(query, new ForwardGeocodeResultsListener() {
    @Override
        public void onSuccess(@NonNull List<ForwardGeocode> results) {

        }
    }

    @Override
    public void onFailure(@NonNull String errorMessage) {

    }
});
```

More advanced street searches are available in sample app;

#### Reverse Geocode

You can retrieve data about a location by using Reverse Geocode API

```java
CedarMaps.getInstance().reverseGeocode(
        coordinate,
        new ReverseGeocodeResultListener() {
            @Override
            public void onSuccess(@NonNull ReverseGeocode result) {

            }

            @Override
            public void onFailure(@NonNull String errorMessage) {

            }
        });
```

#### Direction
     
This method calculates the distance between points in meters. It can be called with up to 15 different points in a single request.

```java
CedarMaps.getInstance().direction(departure, destination,
        new GeoRoutingResultListener() {
            @Override
            public void onSuccess(@NonNull GeoRouting result) {

            }

            @Override
            public void onFailure(@NonNull String error) {

            }
        });
```

### More Examples Via Sample App

The CedarMaps Android SDK is actually an [Android Library Module](https://developer.android.com/tools/projects/index.html#LibraryModules),
which means in order to test it out in an emulator or a device during development a [Test Module](https://developer.android.com/tools/projects/index.html#testing) is needed.  We call this test module
the **SampleApp**.  It contains many different examples of new functionality or just ways to do certain things.  We highly recommend checking it out.

The source code for these tests / examples is located under the CedarMapsSampleApp directory.

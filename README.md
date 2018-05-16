# CedarMaps Android SDK

This guide will take you through the process of integrating CedarMaps into your Android application.

All the mentioned methods and tools in this document are tested on Android Studio v3.1.

## Table of Contents
- [Installation](#installation)
	-	[Required Permissions](#required-permissions)
	-	[Configuring CedarMaps](#configuring-cedarmaps)
		- [Changing API Base URL](#changing-api-base-url)
    -   [Mapbox](#mapbox)
    	- [MapView](#mapview)
        - [Plugins](#plugins)
    -   [APK Size](#apk-size)
- [API Methods](#api-methods)
	-	[Forward Geocoding](#forward-geocoding)
	-	[Reverse Geocoding](#reverse-geocoding)
	-	[Direction](#direction)
	-	[Distance](#distance)
	-	[Static Map Image](#static-map-images)
- [Sample App](#more-examples-via-the-sample-app)

## Installation

To install the current stable version, first add the address of CedarMaps maven repository to the list of repositories. This is usually located in the `build.gradle` of your **project** module.

```groovy
repositories {
    maven {
        url "http://repo.cedarmaps.com/android/"
    }
}
```

Then, add this to the `build.gradle` of your **app** module:

```groovy
dependencies {
    implementation 'com.cedarmaps:CedarMapsSDK:3.0.0'
}
```

### Required Permissions

If your App needs to access location services, add the following to `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

On Android 6.0 and up, you have to check for "dangerous" permissions at runtime. Accessing location is considered a dangerous permission.

### Configuring CedarMaps

In order to use CedarMaps API, you should set your `clientID`, `clientSecret` and a `context` in your application.

```java
CedarMaps.getInstance()
    .setClientID("YOUR_CLIENT_ID")
    .setClientSecret("YOUR_CLIENT_SECRET")
    .setContext(CONTEXT)
```

#### Changing API Base URL

If you've received an API Base URL, you can set it on CedarMaps shared object:

```java
CedarMaps.getInstance()
    .setAPIBaseURL("YOUR_API_BASE_URL")
```

### Mapbox

CedarMaps SDK is based on [Mapbox GL Android SDK v6.0.1](https://github.com/mapbox/mapbox-gl-native) and provides extra API methods over Mapbox. 
For more information about how to use MapView and other components such as **Adding Markers**, **Showing Current Location**, etc., please see [Mapbox Getting Started](https://www.mapbox.com/help/first-steps-android-sdk/).

#### MapView

The `MapView` class is the key component of this library for showing map tiles. It behaves
like any other `ViewGroup` and its behavior can be changed statically with an
[XML layout](http://developer.android.com/guide/topics/ui/declaring-layout.html)
file, or programmatically during runtime.

Pay attention to the package when importing. **CedarMaps** `MapView` extends **Mapbox** `MapView` and they shall not be used interchangeably.

If you want to show map tiles, first call the following snippet before using any `MapView` instance or inflating any layouts using this object.

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

##### XML layout
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

And then you can call methods on it programmatically;

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

#### Plugins

Mapbox uses [Plugins](https://github.com/mapbox/mapbox-plugins-android) to add extra functionality to the base SDK.

Each plugin is added as a new dependency in `build.gradle`.

```groovy
dependencies {
  implementation ('com.mapbox.mapboxsdk:PLUGIN_NAME:PLUGIN_VERSION_NUMBER') {
    exclude group: 'com.mapbox.mapboxsdk'
  }
}
```

**Note:** Since CedarMaps uses a forked version of Mapbox SDK, make sure to exclude `group: 'com.mapbox.mapboxsdk'` when adding a new plugin.

### APK Size

Mapbox is built as a native library for every possible architecture. If you are supporting all architectures, please consider splitting the APK based on architecture.

An introduction from Google can be found [here](https://developer.android.com/studio/build/configure-apk-splits).

You can find APK splitting configurations in Sample App `build.gradle` as well.

## API Methods
In addition to using MapView, you can use CedarMaps API to retrieve location based data and street search.

All API calls are asynchronous; they don't block the UiThread. The completion handlers are all called on the UiThread.

You can also consult [CedarMaps.java](http://gitlab.cedar.ir/cedar.studios/cedarmaps-sdk-android-public/blob/master/CedarMapsSDK/src/main/java/com/cedarstudios/cedarmapssdk/CedarMaps.java) for detailed info on all of our methods. Some of the main methods are mentioned below.

### Forward Geocoding

For finding a street or some limited POIs, you can easily call ```forwardGeocode``` methods.

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

More advanced street searches are available in the sample app.

### Reverse Geocoding

You can retrieve data about a location by using Reverse Geocode API.

```java
CedarMaps.getInstance().reverseGeocode(
        coordinate,
        new ReverseGeocodeResultListener() {
            @Override
            public void onSuccess(@NonNull ReverseGeocode result {

            }

            @Override
            public void onFailure(@NonNull String errorMessage) {

            }
        });
```

### Direction
     
This method calculates the direction between points. It can be called with up to 50 different pairs in a single request.

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

### Distance

This method calculates the distance between points in meters. It can be called with up to 15 different points in a single request.

```java
CedarMaps.getInstance().distance(departure, destination,
        new GeoRoutingResultListener() {
            @Override
            public void onSuccess(@NonNull GeoRouting result) {

            }

            @Override
            public void onFailure(@NonNull String error) {

            }
        });
```

### Static Map Images

This method returns a static map image of desired size for the entered coordinate.

It automatically considers the screen density of the device on which it's being called for better image quality.

You can optionally specify marker positions to draw on the image.

```java
CedarMaps.getInstance().staticMap(dimension, zoomLevel, centerPoint, markers, 
	new StaticMapImageResultListener() {
		@Override
		public void onSuccess(@NonNull Bitmap bitmap) {

		}

		@Override
		public void onFailure(@NonNull String errorMessage) {

		}
	});
```

## More Examples via the Sample App

The CedarMaps Android SDK is an [Android Library Module](https://developer.android.com/tools/projects/index.html#LibraryModules),
which means in order to test it out in an emulator or a device during development a [Test Module](https://developer.android.com/tools/projects/index.html#testing) is needed.  We call this test module
the **SampleApp**.  It contains many different examples of new functionality or just ways to do certain things.  We highly recommend checking it out.

The source code for these tests / examples is located under the CedarMapsSampleApp directory.

**CedarMaps-Dev** (the SampleApp) can also be downloaded from [Google Play](https://play.google.com/store/apps/details?id=com.cedarmaps.sdksampleapp&hl=en) and [Cafe Bazaar](https://cafebazaar.ir/app/com.cedarmaps.sdksampleapp/?l=en).
# CedarMaps Android SDK

This guide will take you through the process of integrating CedarMaps into your Android application.

All the mentioned methods and tools in this document have been tested on Android Studio v3.4.1.

## Table of Contents
- [Installation](#installation)
    -   [Java 8](#java-8)
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
        url "https://repo.cedarmaps.com/android/"
    }
}
```

Then, add this to the `build.gradle` of your **app** module:

```groovy
dependencies {
    implementation 'com.cedarmaps:CedarMapsSDK:4.0.0'
}
```

### Java 8

You may receive an error forcing you to use a minimum API version of N. This section will help you with that.

The Mapbox Maps SDK for Android introduces the use of Java 8. To fix any Java versioning issues, ensure that you are using Gradle version of 3.0 or greater. Once you’ve done that, add the following `compileOptions` to the android section of your **app**-level `build.gradle` file like so:

```groovy
android {
    ...
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

### Required Permissions

Make sure your app can access internet by adding this to `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

If your app needs to access location services, add the following as well:

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

On Android 6.0 and up, you have to check for "dangerous" permissions at runtime. Accessing location is considered a dangerous permission.

### Configuring CedarMaps

In order to use CedarMaps, you should set your `clientID`, `clientSecret` and a `context` in your application.
We suggest you do this in your `Application` subclass, in its `onCreate` method.

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

CedarMaps SDK is based on [Mapbox GL Android SDK v8.0.0](https://github.com/mapbox/mapbox-gl-native) and provides extra API methods over Mapbox. 
For more information about how to use MapView and other components such as **Adding Markers**, **Showing Current Location**, etc., please see [Mapbox Getting Started](https://www.mapbox.com/help/first-steps-android-sdk/).

#### MapView

The `MapView` class is the key component of this library for showing map tiles. It behaves
like any other `ViewGroup` and its behavior can be changed statically with an
[XML layout](http://developer.android.com/guide/topics/ui/declaring-layout.html)
file, or programmatically during runtime.

Pay attention to the package when importing. **CedarMaps** `MapView` extends **Mapbox** `MapView` and they shall not be used interchangeably.

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

##### Setting Map Style

In your fragment or activity class, where you want to use `MapView`, 
you need to set your desired style after the `MapboxMap` instance is ready.

Call `getMapAsync` in `onCreate` and wait for the callback.

You should use `CedarMapsStyleConfigurator` to configure a style and then set it on your `MapboxMap` instance.

You can choose between 3 different styles:
* `VECTOR_LIGHT`
* `VECTOR_DARK`
* `RASTER_DARK`

Using **Vector** styles is recommended.

```java
mMapView = (MapView) view.findViewById(R.id.mapView);

mMapView.getMapAsync(new OnMapReadyCallback() {
    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        mMapboxMap = mapboxMap;

        CedarMapsStyleConfigurator.configure(
                CedarMapsStyle.VECTOR_LIGHT, new OnStyleConfigurationListener() {
                    @Override
                    public void onSuccess(Style.Builder styleBuilder) {
                        mapboxMap.setStyle(styleBuilder);
                    }

                    @Override
                    public void onFailure(@NonNull String errorMessage) {
                        Log.e(TAG, errorMessage);
                    }
                });

        mMapboxMap.setMaxZoomPreference(17);
        mMapboxMap.setMinZoomPreference(6);

        mMapboxMap.addOnMapClickListener(point -> {
            Log.i(TAG, "Tapped on map");
            return true;
        });
    }
});
```

### Plugins

Mapbox Plugins build on top of the Maps SDK providing extra features in lightweight dependencies.
There are lots of plugins which you could check them out [here](https://docs.mapbox.com/android/plugins/overview/).

We believe using the [Annotation Plugin](https://docs.mapbox.com/android/plugins/overview/annotation/) 
will help you a lot in adding custom markers and symbols onto your map. Many examples are also included in both Mapbox documentations page
or our included Sample App.

### APK Size

Mapbox is built as a native library for every possible architecture. If you are supporting all architectures, please consider splitting the APK based on architecture.

An introduction from Google can be found [here](https://developer.android.com/studio/build/configure-apk-splits).

You can find APK splitting configurations in Sample App `build.gradle` as well.

## API Methods
In addition to using MapView, you can use CedarMaps API to retrieve location based data and street search.

All API calls are asynchronous; they don't block the thread on which they're called. The completion handlers are all called on the `UiThread`.

You can also consult [CedarMaps.java](https://github.com/cedarstudios/cedarmaps-android-sdk/blob/master/CedarMapsSDK/src/main/java/com/cedarstudios/cedarmapssdk/CedarMaps.java) for detailed info on all of our methods. Some of the main methods are mentioned below.

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

More advanced street searches are available in the Sample App.

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
CedarMaps.getInstance().staticMap(width, height, zoomLevel, centerPoint, markers, 
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
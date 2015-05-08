## Getting started with the CedarMaps Android SDK

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
    compile('com.cedarmaps:CedarMapsSDK:0.7.3@aar') {
        transitive = true
    }
}
```


### Required Permissions

Ensure the following *core* permissions are requested in your `AndroidManifest.xml` file:

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

If your project needs to access location services, it'll also need the following permissions too:

```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

### Getting Access Token

In order to use CedarMaps API and TileSource you should get `AccessToken` with your client id and
client secret:

```java
com.cedarstudios.cedarmapssdk.config.Configuration
    configuration = new ConfigurationBuilder()
    .setClientId(clientId)
    .setClientSecret(clientSecret)
    .build();
    CedarMapsFactory factory = new CedarMapsFactory(configuration);
    CedarMaps cedarMaps = factory.getInstance();
    OAuth2Token oAuth2Token = cedarMaps.getOAuth2Token();
```

Then you should use `oAuth2Token.getAccessToken()` in mapView or API


### The MapView

This project is based on [Mapbox Android SDK](https://www.mapbox.com/mapbox-android-sdk/) and provides
CedarMapsTileLayer and extra API over Mapbox.

The `MapView` class is the key component of this library. It behaves
like any other `ViewGroup` and its behavior can be changed statically with an
[XML layout](http://developer.android.com/guide/topics/ui/declaring-layout.html)
file, or programmatically during runtime.

#### XML layout
To add the `MapView` as a layout element, add the following to your xml file:

```xml
<com.mapbox.mapboxsdk.views.MapView
    android:id="@+id/mapView"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent" />
```


And then you can call it programmatically with

```java
MapView mapView = (MapView) findViewById(R.id.mapview);
```

#### On runtime

On runtime you can create a new MapView by specifying the context of the
application and then use `CedarMapsTileLayer` as tile source.

```java
MapView mapView = new MapView(context);
Configuration configuration = new ConfigurationBuilder()
                .setClientId(Constants.CLIENT_ID)
                .setClientSecret(Constants.CLIENT_SECRET)
                .setMapId(Constants.MAPID_CEDARMAPS_STREETS)
                .build();
        final CedarMapsTileLayer cedarMapsTileLayer = new CedarMapsTileLayer(configuration);
        cedarMapsTileLayer.setTileLayerListener(new CedarMapsTileLayerListener() {
            @Override
            public void onPrepared(CedarMapsTileLayer tileLayer) {
                mapView.setTileSource(tileLayer);
                mapView.setZoom(12);
                mapView.setCenter(new LatLng(35.6961, 51.4231)); // center of tehran
            }
        });
```

Currently you can use `cedarmaps.streets` as default mapId

#### Changing API Base Url

You can change API Base Url by setting it on configuration object:

```java
MapView mapView = new MapView(context);
Configuration configuration = new ConfigurationBuilder()
                .setAPIBaseURL(CUSTOM_API_URL)
                .setClientId(Constants.CLIENT_ID)
                .setClientSecret(Constants.CLIENT_SECRET)
                .setMapId(Constants.MAPID_CEDARMAPS_STREETS)
                .build();
```

### Attention

currently CedarMaps supports Tehran city. so be sure that you handle the map screen limit by the
boundingBox and the default map center location:

```java
// center of tehran
mapView.setCenter(new LatLng(35.6961, 51.4231));
// limit scrollable area
mapView.setScrollableAreaLimit(new BoundingBox(north, east, south, west));
```

### Overlays

Anything visual that is displayed over the map, maintaining its geographical
position, we call it an `Overlay`. To access a MapView's overlays
at any point during runtime, use:

```java
mapView.getOverlays();
```

#### Markers

Adding a marker with the default styling is as simple as calling this
for every marker you want to add:

```java
Marker marker = new Marker(mapView, title, description, LatLng)
mapView.addMarker(marker);
```

#### Location overlay

The location of the user can be displayed on the view using `UserLocationOverlay`

```java
GpsLocationProvider myLocationProvider = new GpsLocationProvider(getActivity());
UserLocationOverlay myLocationOverlay = new UserLocationOverlay(myLocationProvider, mapView);
myLocationOverlay.enableMyLocation();
myLocationOverlay.setDrawAccuracyEnabled(true);
mapView.getOverlays().add(myLocationOverlay);
```

#### Paths

Paths are treated as any other `Overlay`, and are drawn like this:

```java
PathOverlay line = new PathOverlay(Color.RED, this);
line.addPoint(new LatLng(51.2, 0.1));
line.addPoint(new LatLng(51.7, 0.3));
mapView.getOverlays().add(line);
```

#### Drawing anything into the map

To add anything with a higher degree of  customization you can declare your own `Overlay`
subclass and define what to draw by overriding the `draw` method. It will
give you a Canvas object for you to add anything to it:

```java
class AnyOverlay extends Overlay{
    @Override
    protected void draw(Canvas canvas, MapView mapView, boolean shadow) {
        //do anything with the Canvas object
    }
}
```

### Screen rotation

By default, every time the screen is rotated, Android will call `onCreate`
and return all states in the app to their inital values. This includes current
zoom level and position of the MapView. The simplest way to avoid this is adding
this line to your `AndroidManifest.xml`, inside `<activity>`:

	android:configChanges="orientation|screenSize|uiMode"

Alternatively you can override the methods `onSaveInstanceState()` and
`onRestoreInstanceState()` to have broader control of the saved states in the app.
See this [StackOverflow question](http://stackoverflow.com/questions/4096169/onsaveinstancestate-and-onrestoreinstancestate) for
more information on these methods

=======
### CedarMaps API
In addition to use MapView, you can use CedarMaps API to retrieve location based data and street search.
Before beginning to call CedarMaps API you should get `AccessToken` with your client id and client secret

```java
com.cedarstudios.cedarmapssdk.config.Configuration
    configuration = new ConfigurationBuilder()
    .setClientId(clientId)
    .setClientSecret(clientSecret)
    .build();
    CedarMapsFactory factory = new CedarMapsFactory(configuration);
    CedarMaps cedarMaps = factory.getInstance();
    OAuth2Token oAuth2Token = cedarMaps.getOAuth2Token();
```

Then you should use `oAuth2Token` with each API call

#### Geocode (Street Search)

For finding a street you can easily call streetSearch method.

```java
Configuration configuration = new ConfigurationBuilder()
        .setOAuth2AccessToken(oAuth2Token.getAccessToken())
        .setOAuth2TokenType(oAuth2Token.getTokenType())
        .setMapId(Constants.MAPID_CEDARMAPS_STREETS)
        .build();
CedarMaps cedarMaps = new CedarMapsFactory(configuration).getInstance();
searchResult = cedarMaps.geocode(searchTerm);
```

The output would be something like this for search term "همت":

```json
{
    "results": [
        {
            "address": "المهدی",
            "id": 301568,
            "location": {
                "bb": {
                    "ne": "35.770861600000003,51.323841700000003",
                    "sw": "35.770540400000002,51.323066400000002"
                },
                "center": "35.770585227006897,51.323426168064202"
            },
            "name": "همت",
            "type": "street"
        },
        {
            "address": "کاظم آباد,مهران,اراضی عباس آباد,...",
            "id": 397432,
            "location": {
                "bb": {
                    "ne": "35.759246099999999,51.4836156",
                    "sw": "35.7491463,51.423702800000001"
                },
                "center": "35.749153889854,51.427947792189102"
            },
            "name": "همت",
            "type": "expressway"
        },
        {
            "address": "ابوذر,شهرابی",
            "id": 312434,
            "location": {
                "bb": {
                    "ne": "35.679308300000002,51.480144099999997",
                    "sw": "35.679143099999997,51.478227199999999"
                },
                "center": "35.679225700000003,51.479185649999998"
            },
            "name": "همتی",
            "type": "street"
        }
    ],
    "status": "OK"
}
```

More advanced street searches are available in sample app;

#### Reverse Geocode

You can retrieve data about a location by using reverse geocode API

```java
Configuration configuration = new ConfigurationBuilder()
        .setOAuth2AccessToken(oAuth2Token.getAccessToken())
        .setOAuth2TokenType(oAuth2Token.getTokenType())
        .build();
CedarMaps cedarMaps = new CedarMapsFactory(configuration).getInstance();
searchResult = cedarMaps.geocode(lat, lng);
```

The output would be something like this for 35.4,52.3:

```json
{
    "result": {
        "address": "تهران، شهرک غرب، خیابان دادمان، خیابان سپهر",
        "city": "تهران",
        "components": [
            {
                "long_name": "خیابان سپهر",
                "short_name": "سپهر",
                "type": "street"
            },
            {
                "long_name": "خیابان دادمان",
                "short_name": "دادمان",
                "type": "steet"
            },
            {
                "long_name": "شهرک غرب",
                "short_name": "شهرک غرب",
                "type": "locality"
            }
        ],
        "locality": "شهرک غرب"
    },
    "status": "OK"
}
```

### More Examples Via TestApp

The CedarMaps Android SDK is actually an [Android Library Module](https://developer.android.com/tools/projects/index.html#LibraryModules),
which means in order to test it out in an emulator or a device during development a [Test Module](https://developer.android.com/tools/projects/index.html#testing) is needed.  We call this test module
the **TestApp**.  It contains many different examples of new functionality or just ways to do certain things.  We highly recommend checking it out.

The source code for these tests / examples is located under the CedarMapsTestApp directory.

You can find more useful examples on [Mapbox SDK Test App](https://github.com/mapbox/mapbox-android-sdk/tree/mb-pages/MapboxAndroidSDKTestApp)



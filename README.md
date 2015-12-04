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
    compile('com.cedarmaps:CedarMapsSDK:0.7.4@aar') {
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
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
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

#### Geocode (Place Search)

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
            "address": "اراضی عباس آباد,مهران,سید خندان,...",
            "components": {
                "city": "تهران",
                "country": "ایران",
                "districts": [
                    "منطقه 4",
                    "منطقه 3"
                ],
                "localities": [
                    "اراضی عباس آباد",
                    "مهران",
                    "سید خندان",
                    "پاسداران"
                ],
                "province": "تهران"
            },
            "id": 429874,
            "location": {
                "bb": {
                    "ne": "35.756689799999997,51.464761500000002",
                    "sw": "35.7491463,51.423702800000001"
                },
                "center": "35.749155599171999,51.428327751596903"
            },
            "name": "همت",
            "type": "expressway"
        },
        {
            "address": "المهدی",
            "components": {
                "city": "تهران",
                "country": "ایران",
                "districts": [
                    "منطقه 5"
                ],
                "localities": [
                    "المهدی"
                ],
                "province": "تهران"
            },
            "id": 338756,
            "location": {
                "bb": {
                    "ne": "35.770861600000003,51.323841700000003",
                    "sw": "35.770540400000002,51.323066400000002"
                },
                "center": "35.770585227006897,51.323426168064202"
            },
            "name": "همت",
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

The output would be something like this for 35.716482704636825, 51.40897750854492:

```json
{
    "result": {
        "address": "بن بست سروش - زرتشت",
        "city": "تهران",
        "components": [
            {
                "long_name": "بن بست سروش",
                "short_name": "بن بست سروش",
                "type": "residential"
            },
            {
                "long_name": "زرتشت",
                "short_name": "زرتشت",
                "type": "primary"
            },
            {
                "long_name": "بهجت آباد",
                "short_name": "بهجت آباد",
                "type": "locality"
            },
            {
                "long_name": "تهران",
                "short_name": "تهران",
                "type": "city"
            }
        ],
        "locality": "بهجت آباد",
        "traffic_zone": {
            "in_central": true,
            "in_evenodd": true,
            "name": "محدوده طرح ترافیک"
        }
    },
    "status": "OK"
}
```

#### Distance
     
This method calculates the distance between points in meters. It can be called with up to 50 different points in a single request.

The only supported profile is cedarmaps.driving which calculates the distance using car routing.

```java
Configuration configuration = new ConfigurationBuilder()
        .setOAuth2AccessToken(oAuth2Token.getAccessToken())
        .setOAuth2TokenType(oAuth2Token.getTokenType())
        .setMapId(Constants.MAPID_CEDARMAPS_DRIVING)
        .build();

CedarMaps cedarMaps = new CedarMapsFactory(configuration).getInstance();
searchResult = cedarMaps.distance(new LatLng(35.6961, 51.4231), new LatLng(35.744625, 51.374600));
```

Response elements:

distance: The overall distance of the route, in meter
time: The overall time of the route, in ms
bbox: The bounding box of the route, format: minLon, minLat, maxLon, maxLat


The output would be something like this:

```json
{
    "result": {
        "routes": [
            {
                "bbox": [
                    51.368587,
                    35.74982,
                    51.41652,
                    35.762383
                ],
                "distance": 7516.338,
                "time": 500912
            }
        ]
    },
    "status": "OK"
}
```


#### Locality

It gives you all localities in a city wih geometry in GeoJSON format. This API call needs a valid access token.

```java
Configuration configuration = new ConfigurationBuilder()
        .setOAuth2AccessToken(oAuth2Token.getAccessToken())
        .setOAuth2TokenType(oAuth2Token.getTokenType())
        .build();

CedarMaps cedarMaps = new CedarMapsFactory(configuration).getInstance();
searchResult = cedarMaps.locality("tehran");
```

Supported cities are: tehran, تهران

The output would be something like this:

```json
{
   "results":
    [
        {
            "geometry": {
                "coordinates": [
                    [
                        [
                            51.3904371,
                            35.6144373
                        ],
                        [
                            51.3860088,
                            35.6143914
                        ],
                        [
                            51.3896979,
                            35.6169901
                        ],
                        [
                            51.3893829,
                            35.6216496
                        ],
                        [
                            51.3869264,
                            35.622008
                        ],
                        [
                            51.3863595,
                            35.6257456
                        ],
                        [
                            51.384092,
                            35.6256944
                        ],
                        [
                            51.38384,
                            35.6281007
                        ],
                        [
                            51.3821393,
                            35.6281007
                        ],
                        [
                            51.378927,
                            35.6348585
                        ],
                        [
                            51.378602,
                            35.6384921
                        ],
                        [
                            51.3822129,
                            35.6370245
                        ],
                        [
                            51.385588,
                            35.637117
                        ],
                        [
                            51.3859658,
                            35.6313776
                        ],
                        [
                            51.3916527,
                            35.6247423
                        ],
                        [
                            51.3904371,
                            35.6144373
                        ]
                    ]
                ],
                "type": "Polygon"
            },
            "name": "اسفندیاری و بستان"
        }
    ],
    "status": "OK"
}
```

### More Examples Via TestApp

The CedarMaps Android SDK is actually an [Android Library Module](https://developer.android.com/tools/projects/index.html#LibraryModules),
which means in order to test it out in an emulator or a device during development a [Test Module](https://developer.android.com/tools/projects/index.html#testing) is needed.  We call this test module
the **TestApp**.  It contains many different examples of new functionality or just ways to do certain things.  We highly recommend checking it out.

The source code for these tests / examples is located under the CedarMapsTestApp directory.

You can find more useful examples on [Mapbox SDK Test App](https://github.com/mapbox/mapbox-android-sdk/tree/mb-pages/MapboxAndroidSDKTestApp)



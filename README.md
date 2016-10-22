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
    compile('com.cedarmaps:CedarMapsSDK:1.0.0@aar') {
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

The `MapView` class is the key component of this library. It behaves
like any other `ViewGroup` and its behavior can be changed statically with an
[XML layout](http://developer.android.com/guide/topics/ui/declaring-layout.html)
file, or programmatically during runtime.

#### XML layout
To add the `MapView` as a layout element, add the following to your xml file:

```xml
<com.cedarstudios.cedarmapssdk.view.MapView
    android:id="@+id/mapView"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```


And then you can call it programmatically with

```java
MapView mapView = (MapView) findViewById(R.id.mapview);
final CedarMapsTileSourceInfo cedarMapsTileSourceInfo = new CedarMapsTileSourceInfo(getContext(), configuration);
        cedarMapsTileSourceInfo.setTileLayerListener(new CedarMapsTileLayerListener() {
            @Override
            public void onPrepared(CedarMapsTileSourceInfo tileLayer) {

                CedarMapsTileSource cedarMapsTileSource = new CedarMapsTileSource(tileLayer);
                CedarMapTileProvider provider = new CedarMapTileProvider(getContext(), cedarMapsTileSource);
                mapView.setTileProvider(provider);
                mapView.getController().setZoom(12);
                mapView.getController().setCenter(new GeoPoint(35.6961, 51.4231)); // center of Tehran

            }
        });
```

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
CedarMaps SDK is based on [OpenStreetMap Android SDK v5.2](https://github.com/osmdroid/osmdroid) and provides `CedarMapsTileSource` and extra API over OpenStreetMap. 
For more information about how to use MapView and other components please see [OpenStreetMap Wiki](https://github.com/osmdroid/osmdroid/wiki)

### Attention

currently CedarMaps supports Tehran city. 


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

You can find more useful examples on [OpenStreetMap SDK Test App](https://github.com/osmdroid/osmdroid)



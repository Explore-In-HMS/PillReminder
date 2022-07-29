# PillReminder App

## Introduction
The PillReminder application is a mobile and wearable reference app that allows users to set up their own personal health reminders with Huawei kits and services. Users can create a reminder for the drugs they need to use as frequency, dosage and day. When it is time to use the drug, the user receives notification both over the phone and on the watch. In addition, users can easily view nearby hospitals and pharmacies via the application and see them on the map.

## About HUAWEI Wear Engine
Wear Engine allows the share of app features and services between phones and wearable devices, for a rich interactive experience. Wear Engine pools the phone's and wearable's resources and capabilities, which include the phone's apps and services and the wearable's device capabilities, creating benefits for consumers and developers alike. It enables consumers to use their devices in more diversified scenarios and receive more convenient services, with a smoother experience. It also expands the reach of your business, and takes your apps and services to the next level.

## About Huawei Map Kit
Map Kit provides powerful and convenient map services for you to implement personalized map display and interaction at ease. The Map SDK for Android is a set of APIs that can be called to develop maps. You can use this SDK to easily add map-related functions to your Android app, including map display, map interaction, map drawing, and map style customization. Also the Map SDK (Java) for HarmonyOS is a set of APIs that can be called to develop maps. You can use this SDK to easily add map-related functions to your HarmonyOS app, including map display, map interaction, and map drawing.

## About Huawei Location Kit
Location Kit provides capabilities for you to obtain the precise user device location quickly, helping you build up global positioning capabilities and expand your global business.  The Location SDK for Android offers location-related APIs for your Android apps. These APIs mainly relate to six functions: fused location, activity identification, geofence, high-precision location, indoor location, and geocoding. This mode is applicable to mobile phones and Huawei tablets. Also the Location SDK (Java) for HarmonyOS offers location-related APIs for your HarmonyOS apps. These APIs mainly relate to two functions: fused location and geocoding. Currently, this mode is applicable only to Huawei smart watches.

## About Huawei Site Kit
Bolstered with a myriad of POIs, Site Kit offers you many search capabilities such as place search, time zone search, geocoding, and administrative region search, helping your app attract more users. The Site SDK for Android is a set of APIs that can be called to develop place-related functions. You can use this SDK to easily add place search functions to your Android app, including keyword search, nearby place search, place detail search, and place search suggestion.

## What You Will Need

**Hardware Requirements**
- A computer that can run Android Studio.
- An Android phone for debugging.
- A Huawei smart watch must run HarmonyOS 2.0 or later.

**Software Requirements**
- Android SDK package
- Android Studio 3.X
- DevEco Studio 2.X
- HMS Core (APK) 4.X or later

## Getting Started

PillReminder App uses HUAWEI services. In order to use them, you have to [create an app](https://developer.huawei.com/consumer/en/doc/distribution/app/agc-create_app) first. Before getting started, please [sign-up](https://id1.cloud.huawei.com/CAS/portal/userRegister/regbyemail.html?service=https%3A%2F%2Foauth-login1.cloud.huawei.com%2Foauth2%2Fv2%2Flogin%3Faccess_type%3Doffline%26client_id%3D6099200%26display%3Dpage%26flowID%3D6d751ab7-28c0-403c-a7a8-6fc07681a45d%26h%3D1603370512.3540%26lang%3Den-us%26redirect_uri%3Dhttps%253A%252F%252Fdeveloper.huawei.com%252Fconsumer%252Fen%252Flogin%252Fhtml%252FhandleLogin.html%26response_type%3Dcode%26scope%3Dopenid%2Bhttps%253A%252F%252Fwww.huawei.com%252Fauth%252Faccount%252Fcountry%2Bhttps%253A%252F%252Fwww.huawei.com%252Fauth%252Faccount%252Fbase.profile%26v%3D9f7b3af3ae56ae58c5cb23a5c1ff5af7d91720cea9a897be58cff23593e8c1ed&loginUrl=https%3A%2F%2Fid1.cloud.huawei.com%3A443%2FCAS%2Fportal%2FloginAuth.html&clientID=6099200&lang=en-us&display=page&loginChannel=89000060&reqClientType=89) for a HUAWEI developer account.

After creating the application, you need to [generate a signing certificate fingerprint](https://developer.huawei.com/consumer/en/codelab/HMSPreparation/index.html#3). Then you have to set this fingerprint to the application you created in AppGallery Connect.
- Go to "My Projects" in AppGallery Connect.
- Find your project from the project list and click the app on the project card.
- On the Project Setting page, set SHA-256 certificate fingerprint to the SHA-256 fingerprint you've generated.
![AGC-Fingerprint](https://communityfile-drcn.op.hicloud.com/FileServer/getFile/cmtyPub/011/111/111/0000000000011111111.20200511174103.08977471998788006824067329965155:50510612082412:2800:6930AD86F3F5AF6B2740EF666A56165E65A37E64FA305A30C5EFB998DA38D409.png?needInitFileName=true?needInitFileName=true?needInitFileName=true?needInitFileName=true)
- For wearable project In DevEco Studio, choose to open an existing DevEco Studio project and select the **harmonyos** directory.
- You can run the **gradlew signReleaseHap** command to directly create a project.
- Then, you need to create an app in AppGallery Connect, obtain the **agconnect-services.json** file, and add it to the project. You also need to generate a signing certificate fingerprint, add the signing certificate file to the project, and add related configurations to the **build.gradle** file. For details, please refer to [Configuring App Information in AppGallery Connect](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/harmonyos-sdk-config-agc-0000001101459188?ha_source=hms1).

## Using the Application

When the application is opened for the first time, you will be greeted by the home page. You can create a reminder with the "+" button at the bottom of the home page.
On the page that opens, pill name, pill taking frequent, pill dosage and time information must be entered.
For Pill frequent, you can choose from every day, every week or specific day.
You can easily enter the amount of pills to be taken in the dosage section by using the plus and minus buttons.
Finally, after entering the time to take the medicine, you can complete the reminder creation steps by clicking the "save" button.
All created reminders can be viewed from the home screen. When the reminder time comes, the user will receive notifications on both the phone and the smart watch.
From the Map tab, the user can easily view the hospitals and pharmacies nearby according to their location.

## Sonarqube Report
<img src="/images/sonarqube.png"></img>

## Screenshots
<img src="/images/1.jpg" width=250></img>
<img src="/images/2.jpg" width=250></img>
<img src="/images/3.jpg" width=250></img>
<img src="/images/4.jpg" width=250></img>
<img src="/images/5.png" width=250></img>
<img src="/images/6.jpg" width=250></img>
<img src="/images/7.jpg" width=250></img>

## Libraries

- Huawei Wear Engine
- Huawei Map Kit
- Huawei Location Kit
- Huawei Site Kit
- Glide
- CircleImageView
- Navigation
- Hilt
- Lottie
- Room DB

##  License
The sample code is licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

## Contributors
- Berk Ozyurt
- Ece Aydin
- Cengiz Toru

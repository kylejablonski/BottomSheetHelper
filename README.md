# BottomSheetSample
Bottom Sheet Sample application for the bottomsheethelper library which gives a simple interface for defining a bottom sheet in Android

Library use:

Add to your root build.gradle

	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
	
and add to your app build.gradle
	
	dependencies {
		compile 'com.github.User:Repo:Tag'
	}
	
Setup:
* Include the lines above in your build.gradle files
* Create a <provider /> tag inside your <application /> tag in your manifest

```xml
<!-- Specify your own file provider for your application -->
        <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="${packageName}.sampleprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_provider_paths" />
        </provider>
```
* create an /xml folder with file_provider_paths.xml

```xml
    <?xml version="1.0" encoding="utf-8"?>
    <paths xmlns:android="http://schemas.android.com/apk/res/android">
        <!-- Configure the file paths you want to associate to your app -->
        <files-path name="share_sample" path="BottomSheet/"/>
    </paths>
```


Library features an Activity called ShareActivity, extend any Activity you want to enable sharing as:
	
```java
	public class MyActivity extends ShareActivity{
	
	    @Override
        protected Uri createShareFile() {
    
          // ... Handle your file creation and return the URI for this file
    
        }
	
	    @Override
        protected boolean setupShareIntent(BottomSheetItem bottomSheetItem, Uri fileToShare) {
        
           // ... Handle your Intent using the SheetHelper and the Intent class
        
        }
	}
```
Sample source from the MainActivity in the app/

```java
       // Use the SheetHelper.Builder to setup the settings for sharing
        mSheetHelper = new SheetHelper.Builder().with(this)
                .action(Intent.ACTION_SEND) /* Specify and Intent Action */
                .filePrefix("Image") /* configure a file prefix */
                .provider(".sampleprovider") /* configure the file provider name */
                .fileExtension(".png") /* configure the file extension */
                .columnCount(2) /* configure the column count for the grid adapter */
                .callback(ShareSheetBehaviorCallback) /* specify a BottomSheetBehavior.BottomSheetCallback */
                .dateFormat("_M-dd-yyyy_hhmmss") /* sets a Date format for saving the file */
                .directory("BottomSheet") /* specify the directory */
                .subject("Bottom Sheet Share") /* adds a subject to the share */
                .extraText("Look at this amazing photo I can share!") /* adds some extra text to share*/
                .mimeType("image/*") /* sets the mime type of the file to share */
                .title("Bottom Sheet Sample") /* set the title */
                .create();

        // Tell the parent Activity here is my SheetHelper
        setSheetHelper(mSheetHelper);
```

This Library will handle permission related to `android.permission.READ_EXTERNAL_STORAGE`, including runtime cases for Android 6.0 (API level 23)..

Caveats:
* Limit use at the moment to image sharing in the first release
* You can handle the intent however you want, but i have only tested with Images
* No CC or BCC support yet for emails, will add soon
* For an issues please open an issue, and i will try to fix asap.
* Any additional features please also create an issue and if it makes sense, i will add support for it.

License

    Copyright 2016 Kyle Jablonski

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

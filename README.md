# Welcome to EZMaps v0.8.0
EZMaps is a frame-by-frame picture location service application that caters to the elderly population to navigate their way from one place to another. Along with a simple-to-use interface and straight-forward features, EZMaps tones down modern mapping technology relative to its contemporaries in order to comfortably aid the eldery people in getting to their destination. 

## Pre-requirements

* Android SDK Version: 24
* Emulator Version: API > Android 5.1

## Features Requirement List

|Requirement Document| EZMaps |
|---------------|----------|
|Login/SignUp | **Completed** |
|Profile Setup | **Completed** |
|Image Upload | **Completed** |
|EZMap        | **Completed** |
|Automatic/ Manual Card Swiping | In Progress |
|Favourite Route List | **Completed** |
|Contact List & Searching | **Completed** |
|Voice Call | In Progress  |
|Video Call | In Progress |
|Instant Messaging | **Completed** |
|Image Sending | **Completed**  |
|QR Code Scanner | In Progress  |

## Testing Login/ SignUp
1) Download the repo and run the app in your IDE (Android Studio, etc) with an Emulator
2) Click on *Login with Email*
3) Type in your email and password
4) Either hit enter key or click on *Login with Email*

| ![screenshot_1538230644](https://user-images.githubusercontent.com/12033253/46247001-9584d500-c448-11e8-8e8e-6d887ef797c5.png) | ![screenshot_1538232861](https://user-images.githubusercontent.com/12033253/46247221-615ee380-c44b-11e8-95a6-f1ea388165bf.png)| ![screenshot_1538232808](https://user-images.githubusercontent.com/12033253/46247219-5906a880-c44b-11e8-9439-094bdeee9d87.png) |
| ------------- |:-------------:| -----:|

4) Click on *Sign Up* 
5) Type in your name, email and password
6) Either hit enter key or click on *Sign Up*

| ![screenshot_1538230644](https://user-images.githubusercontent.com/12033253/46247001-9584d500-c448-11e8-8e8e-6d887ef797c5.png) | ![screenshot_1538232933](https://user-images.githubusercontent.com/12033253/46247229-8eab9180-c44b-11e8-9f57-0c8d542bed7c.png)| ![screenshot_1538232945](https://user-images.githubusercontent.com/12033253/46247232-9d924400-c44b-11e8-961c-a2c6af9ccf27.png) |
| ------------- |:-------------:| -----:|

7) For future testing uses, to skip the Login and Sign up process, hit on either *Login 1* or *Login 2* to access already-prepared accounts.

### Testing Profile Setup
1) After entering the application, click on *Profile* at the top of the app to view your profile.
2) To change any details of your profile, hit on settings button. 
3) Click on your name or email, and edit it. Once you're done, click on the *Done* button at the top right.

| ![screenshot_1538233344](https://user-images.githubusercontent.com/12033253/46247325-075f1d80-c44d-11e8-872a-dfd70e111141.png) | ![screenshot_1538233437](https://user-images.githubusercontent.com/12033253/46247326-075f1d80-c44d-11e8-8631-ada6ee360c51.png)|![screenshot_1538233706](https://user-images.githubusercontent.com/12033253/46247357-6ae94b00-c44d-11e8-82c9-d8a690785ab6.png) |
| ------------- |:-------------:| -----:|

### Testing Image Upload
1) At the Edit Profile page, if you wish to change your profile picture , hit the *Edit Image* button.
2) There will be 2 options; *Take Photo* and *Choose Photo*.
3) Click on *Take Photo* and you will enter in your camera's photo mode.

|  ![screenshot_1538234173](https://user-images.githubusercontent.com/12033253/46247451-fb745b00-c44e-11e8-8e38-0a60c89ed293.png)        |      ![screenshot_1538234138](https://user-images.githubusercontent.com/12033253/46247457-03cc9600-c44f-11e8-8e44-6f04cc75b293.png)   |   ![screenshot_1538234329](https://user-images.githubusercontent.com/12033253/46247463-1cd54700-c44f-11e8-9c2b-1e8c8841c7df.png)| 
| ------------- |:-------------:| -----:|

4) After taking a photo of your liking, you would be brought out to your photo gallery, where you should navigate to your photo album by clicking on *Photos*
5) Look for your image in the album and select it by clicking on it.
6) You will then be at the uploading stage, where you have the option to upload the image and turn it to your profile picture. Click on *Confirm* to make the image as your profile picture, and *Cancel* to return.

| ![screenshot_1538234335](https://user-images.githubusercontent.com/12033253/46247527-15626d80-c450-11e8-8fac-52424157ab49.png)  | ![screenshot_1538234342](https://user-images.githubusercontent.com/12033253/46247528-15626d80-c450-11e8-82a3-710b1a43a7a2.png)  | ![screenshot_1538234347](https://user-images.githubusercontent.com/12033253/46247604-1778fc00-c451-11e8-8ad4-fa3ee41761ae.png)      | 
| ------------- |:-------------:| -----:|


7) Clicking on *Choose Photo* would skip past the photo mode and land you at your phone's gallery.
8) There, you can navigate your folders and pick and image.
9) After picking an image you would be back at the uploading stage where you can *Confirm* to set the image as your profile picture or *Cancel*.

| ![screenshot_1538235049](https://user-images.githubusercontent.com/12033253/46247583-e26ca980-c450-11e8-80b8-d031d696be77.png) | ![screenshot_1538235110](https://user-images.githubusercontent.com/12033253/46247584-e3054000-c450-11e8-8aea-aeefd9d07514.png) | ![screenshot_1538235190](https://user-images.githubusercontent.com/12033253/46247585-e3054000-c450-11e8-8758-fc88642a914c.png)  | 
| ------------- |:-------------:| -----:|

### Testing EZMap
1) Before testing the EZMap, you would have to reset your emulator's current location by way of sending Latitude and Longitude to your own device. 
   1) After starting up the app, click on the _More_ option next to your device and the Extended Controls will pop up.
   2) Click on _Location_ on the side panel, and type in the longitude and latitude coordinates you wish to start from. Click [here](https://www.latlong.net) to get the coordinates of an area. WARNING: Keep the coordinates within Australia or else it would not work. 
   3) Click _SEND_ and your device (emulator) will now be at that particular location.

| ![screenshot 2018-10-05 at 21 42 59](https://user-images.githubusercontent.com/12033253/46533441-f0567a80-c8e7-11e8-9527-e7a6603f6701.png) | ![screenshot 2018-10-05 at 21 46 17](https://user-images.githubusercontent.com/12033253/46533597-825e8300-c8e8-11e8-95d3-2bca341063f6.png)  |
| ------------- |:-------------:|

2) At the _Home_ page, type in a query (Melbourne University, Yarra River, etc) into the search bar and an autocomplete suggestion will drop down. (It is preferable to follow the autocomplete to yield an accurate result).
3) Click on _Search_ button on the side or _Enter_, and the EZMap will start loading. You can navigate it via scrolling horizontally or clicking on the left or right most side of the screen.

|![screenshot_1538740710](https://user-images.githubusercontent.com/12033253/46535409-a9b84e80-c8ee-11e8-9e9c-bf35b2a0761a.png) | ![screenshot_1538739609](https://user-images.githubusercontent.com/12033253/46535408-a91fb800-c8ee-11e8-8696-d32b795b7baf.png) | ![screenshot_1538231141](https://user-images.githubusercontent.com/12033253/46247005-9e75a680-c448-11e8-965e-44ade9939b46.png)  |
| ------------- |:-------------:| -----:|


### Testing Automatic/ Manual Card Swiping (TBA)

### Testing Favourite Route List 
1) Type in a query to the search bar (Melbourne University etc) and click on *Search* at the Home page.
2) At the top right hand corner, click on the *Favourite* button (Heart shaped), it will automatically turn to red. 
3) Returning to the Home page, you will be able to see your favourited route. 
4) Clicking on the favourite route will bring up the EZMap to that particular location.

| ![screenshot_1538235875](https://user-images.githubusercontent.com/12033253/46247807-23b28880-c454-11e8-875e-2fc0dda5a065.png)         |  ![screenshot_1538235893](https://user-images.githubusercontent.com/12033253/46247808-23b28880-c454-11e8-86ef-9927657c9ae4.png)         | ![screenshot_1538236584](https://user-images.githubusercontent.com/12033253/46247809-244b1f00-c454-11e8-85ea-af125853cfc4.png)         |
| ------------- |:-------------:| -----:|

### Testing Contact List & Searching 
1) Go to the Contacts page by clicking or swiping to the *Contacts*
2) Click on the *Add* button and a search for caretaker activity would pop up
3) Type in a name and click on *Search*
4) After clicking on the button *Add* next to the caretaker's info, they will appear in your contact list

| ![screenshot_1538236861](https://user-images.githubusercontent.com/12033253/46247878-1ba71880-c455-11e8-9285-dec2d4276bda.png)  | ![screenshot_1538236906](https://user-images.githubusercontent.com/12033253/46247879-1ba71880-c455-11e8-85ea-0988fd614ac7.png)   | ![screenshot_1538236980](https://user-images.githubusercontent.com/12033253/46247880-1ba71880-c455-11e8-8821-498d61899226.png)   |
| ------------- |:-------------:| -----:|

### Testing Voice Call (TBA)

### Testing Video Call (TBA)

### Testing Instant Messaging
1) Go to your contacts page 
2) Click on a caretaker, and you will brought to a Chat page 
3) Type in a text and send it by hitting the Blue *Send* Button.
4) The text would appear in at the screen.

| ![screenshot_1538237319](https://user-images.githubusercontent.com/12033253/46247987-9d4b7600-c456-11e8-82ec-569fe64da32f.png)   |  ![screenshot_1538237406](https://user-images.githubusercontent.com/12033253/46247988-9d4b7600-c456-11e8-9937-5627afa47ddc.png)   | ![screenshot_1538237641](https://user-images.githubusercontent.com/12033253/46247989-9de40c80-c456-11e8-8769-3a99ad557ef6.png)   |
| ------------- |:-------------:| -----:|


### Testing Image Sharing and Sending
1) To share an image, navigate to the EZMap session and click on the *More* options on the top right of the screen. 
2) Click on the 2 options presented, to either send the image you are currently viewing or all of the images. 
3) Choose a caretaker to share the image to, and you can chat with your caretaker as the image gets sent.

| ![share_image_1](https://user-images.githubusercontent.com/12033253/46715701-eac4b000-ccac-11e8-8863-bf8f09792f67.png) | ![share_image_2](https://user-images.githubusercontent.com/12033253/46715702-eac4b000-ccac-11e8-9e28-0a9ca9b7dfd3.png) | ![share_image_3](https://user-images.githubusercontent.com/12033253/46715703-eb5d4680-ccac-11e8-9846-b8ee60b94a8b.png) | ![share_image_4](https://user-images.githubusercontent.com/12033253/46715704-eb5d4680-ccac-11e8-806d-bff5256970a5.png) |
| ------------- |:-------------:| -----:| -----:|

4) To send an image, navigate to your chat by clicking on a caretaker in contacts. 
5) Click on the *Camera Button* and your phone would open up the camera allowing you to take a picture. 
6) After taking a picture, you would be at the uploading dock. Hit the *Complete* session, and the image would be sent to the caretaker. 

|![send_image_1](https://user-images.githubusercontent.com/12033253/46715697-ea2c1980-ccac-11e8-8025-8949d4df2b6a.png) | ![send_image_2](https://user-images.githubusercontent.com/12033253/46715698-ea2c1980-ccac-11e8-9275-bf33155f0e2f.png) | ![send_image_3](https://user-images.githubusercontent.com/12033253/46715699-ea2c1980-ccac-11e8-82bd-786775d2d71a.png) |![send_image_4](https://user-images.githubusercontent.com/12033253/46715700-eac4b000-ccac-11e8-889a-1a2f4107befa.png)|
| ------------- |:-------------:| -----:| -----:|



### Testing QR Code Scanner (TBA)


## Unit Testing (TBA)

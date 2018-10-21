# Welcome to EZMaps v1.0
EZMaps is a frame-by-frame picture location service application that caters to the elderly population to navigate their way from one place to another. Along with a simple-to-use interface and straight-forward features, EZMaps tones down modern mapping technology relative to its contemporaries in order to comfortably aid the eldery people in getting to their destination. 

## Pre-requirements

* Android SDK Version: 24
* Emulator Version: API > Android 5.1

## Features Requirement List

|Requirement Document| EZMaps |
|---------------|----------|
|Login/SignUp | **Live** |
|Profile Setup | **Live** |
|Image Upload | **Live** |
|EZMap        | **Live** |
|Automatic/ Manual Card Swiping | **Live** |
|Favourite Route List | **Live** |
|Contact List & Searching | **Live** |
|Voice Call | **Live**   |
|Video Call | **Live**  |
|Instant Messaging | **Live**  |
|Image Sending | **Live**  |
|QR Code Scanner | **Live**   |

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
2) Clicking on *Choose Photo* would skip past the photo mode and land you at your phone's gallery.
3) There, you can navigate your folders and pick and image.
4) After picking an image you would be back at the uploading stage where you can *Confirm* to set the image as your profile picture or *Cancel*.

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


### Testing Automatic/ Manual Card Swiping
1) Upon clicking on a Favourite route or typing in a search query into the search bar, EZDirection will be in an automatic state, where if you arrive at the location of the card, the app will swipe to the next card that you're suppose to head to. 
2) To test the Automatic state of EZDirection, look at *Stimulate Travelling* at the Unit Testing section. The EZDirection will swipe accordingly when the android arrives at each location. 
3) **DISCLAIMER**: Testing this might be confusing and time consuming for a number of reasons. 
    1) The route that is used to display in the EZDirection that was harvested by our server, may not match the route you are currently using to *Stimulate Travelling*. This is because, Google Maps changes its route base of its recommendation. Whereas, our server uses the route that is default to us (first one). Because of this, the EZDirection is prone to skipping to a certain steps. 
    2) The current GPS system that we are using is updating at a rate of 0.1 second. Despite this, the default *Stimulate Travelling* may be travelling too fast to detect if it has arrived at the destination. In light of this, a buffer zone around the location of each card has been set up. However, there are still instances of where it would fail to catch the next stop and causing it to skip. 
    3) The best way to actually test this is by going out on the field and actually using the app, albeit it might be a big hassle.

4) To switch to Manual state of EZDirection, hit the switch at the top of toolbar. You can scroll using swiping, or Click on the Side buttons to switch between Cards. When the GPS is null, EZDirection automatically switches back to Manual state. 

|![screenshot_1540113040](https://user-images.githubusercontent.com/12033253/47265082-38270400-d56e-11e8-8c1c-bcbfee807546.png) | ![screenshot_1540113088](https://user-images.githubusercontent.com/12033253/47265087-3e1ce500-d56e-11e8-964f-b89123cde02c.png) | ![screenshot_1540113492](https://user-images.githubusercontent.com/12033253/47265113-876d3480-d56e-11e8-9317-47b240c7ab67.png)  |
| ------------- |:-------------:| -----:|




### Testing Navigation-Contacts Rapid Switching
To test fast transition between navigation and contacts:
1) Observe that there is no navigation bar at the top of the screen in the 'CONTACTS' tab
2) Begin navigation to a location
3) Swipe through any number of cards, as if you were travelling the route, and press 'CONTACTS' to go to the 'CONTACTS' tab
4) Note that there is now a 'RETURN TO NAVIGATION' bar at the top of the screen. Press this to return to your navigation
5) Note that the navigation is still up to the same card as when it was left

|![nct1](https://user-images.githubusercontent.com/31301775/47263776-c6da5780-d553-11e8-822e-ea607783f7df.JPG)  | ![nct2](https://user-images.githubusercontent.com/31301775/47263777-c6da5780-d553-11e8-851b-a861333b4322.JPG)  | ![nct3](https://user-images.githubusercontent.com/31301775/47263778-c6da5780-d553-11e8-91d9-1b6de8769a21.jpg)  | ![nct4](https://user-images.githubusercontent.com/31301775/47263779-c772ee00-d553-11e8-9ab6-5a29c27c60b2.jpg)  | ![nct5](https://user-images.githubusercontent.com/31301775/47263780-c772ee00-d553-11e8-8766-74872ceb4301.jpg) |
| ------------- |:-------------:| -----:| -----:| -----:|


### Testing Favourite Route List 
1) Type in a query to the search bar (Melbourne University etc) and click on *Search* at the Home page.
2) At the top right hand corner, click on the *Favourite* button (Heart shaped), it will automatically turn to red. 
3) Returning to the Home page, you will be able to see your favourited route. 
4) Clicking on the favourite route will bring up the EZMap to that particular location.

| ![screenshot_1538235875](https://user-images.githubusercontent.com/12033253/46247807-23b28880-c454-11e8-875e-2fc0dda5a065.png)  |  ![screenshot_1538235893](https://user-images.githubusercontent.com/12033253/46247808-23b28880-c454-11e8-86ef-9927657c9ae4.png)  | ![screenshot_1538236584](https://user-images.githubusercontent.com/12033253/46247809-244b1f00-c454-11e8-85ea-af125853cfc4.png)  |
| ------------- |:-------------:| -----:|


### Testing Contact List & Searching 
1) Go to the Contacts page by clicking or swiping to the *Contacts*
2) Click on the *Add* button and a search for caretaker activity would pop up
3) Type in a name and click on *Search*
4) After clicking on the button *Add* next to the caretaker's info, they will appear in your contact list

| ![add1](https://user-images.githubusercontent.com/31301775/47263708-ba093400-d552-11e8-9f7b-ff8978066c21.JPG)   |  ![add2](https://user-images.githubusercontent.com/31301775/47263709-baa1ca80-d552-11e8-9500-0a9fb1b127e4.JPG)   | ![add3](https://user-images.githubusercontent.com/31301775/47263938-26863200-d557-11e8-8aaa-2b7f514679e8.jpg)   | ![add4](https://user-images.githubusercontent.com/31301775/47263937-26863200-d557-11e8-9b9f-20a6f7001c21.jpg)  | 
| ------------- |:-------------:| -----:| -----:|


### Testing Video Call (DOES NOT WORK IN EMULATORS)
1) Go to contacts page by clicking or swiping to *Contacts*
2) Click on a the contact you would like to call (User must be added and have had approved your request)
3) You should now be in an outgoing call pending page which will become a video call once the other person answers the call

### Testing Voice Call (DOES NOT WORK IN EMULATORS)
1) Press the audio mode button in any existing video call in order to enable audio only mode

### Testing Instant Messaging
1) Go to your contacts page 
2) Click on a caretaker, and you will brought to a Chat page 
3) Type in a text and send it by hitting the Blue *Send* Button.
4) The text would appear in at the screen.

| ![imt1](https://user-images.githubusercontent.com/31301775/47263698-93e39400-d552-11e8-9243-fda002addd74.JPG)   | ![imt2](https://user-images.githubusercontent.com/31301775/47263922-f9398400-d556-11e8-8c97-d4a04cb905c2.jpg)   | ![imt3](https://user-images.githubusercontent.com/31301775/47263923-f9d21a80-d556-11e8-94fb-2792b96b518f.jpg)   |
| ------------- |:-------------:| -----:|

## Testing Group Messages
1) Click on the checkbox 'Select Contacts for a Groupchat'.
2) Select all the contacts you want to be in the groupchat by clicking on them in the 'Contacts' list, and press the floating '+' button
3) Select 'Create Group Chat' and wait for the lists to reload
4) Scroll to the groupchat you just made in the 'Groupchats' list, and select it
5) Chat by sending text or images, just as in a one-on-one chat. Messages from other members of the groupchat should appear on the left of the screen, along with their name. Messages from the current user should appear on the right

| ![gct1](https://user-images.githubusercontent.com/31301775/47263830-2b49e680-d555-11e8-9283-84c4a3f592e3.JPG)  | ![gct2](https://user-images.githubusercontent.com/31301775/47263831-2be27d00-d555-11e8-8d44-8f60c4ec168d.JPG)  | ![gct3](https://user-images.githubusercontent.com/31301775/47263832-2be27d00-d555-11e8-9f98-cda68c20b214.JPG)  | ![gct4](https://user-images.githubusercontent.com/31301775/47263833-2c7b1380-d555-11e8-8b1a-b13e1a0d26cd.JPG)  | ![gct5](https://user-images.githubusercontent.com/31301775/47263834-2c7b1380-d555-11e8-92c6-8b8c074ed2c9.JPG) |
| ------------- |:-------------:| -----:| -----:| -----:|




### Testing Image Sharing and Sending
1) To share an image, navigate to the EZMap session and click on the *More* options on the top right of the screen. 
2) Click on the 2 options presented, to either send the image you are currently viewing or all of the images. 
3) Choose a caretaker to share the image to, and you can chat with your caretaker as the image gets sent.

| ![share_image_1](https://user-images.githubusercontent.com/12033253/46715701-eac4b000-ccac-11e8-8863-bf8f09792f67.png) | ![share_image_2](https://user-images.githubusercontent.com/12033253/46715702-eac4b000-ccac-11e8-9e28-0a9ca9b7dfd3.png) | ![share_image_3](https://user-images.githubusercontent.com/12033253/46715703-eb5d4680-ccac-11e8-9846-b8ee60b94a8b.png) | ![share_image_4](https://user-images.githubusercontent.com/12033253/46715704-eb5d4680-ccac-11e8-806d-bff5256970a5.png) |
| ------------- |:-------------:| -----:| -----:|

4) To send an image, navigate to your chat by clicking on a caretaker in contacts. 
5) Click on the *Camera Button* and your phone would open up the camera allowing you to take a picture. 
6) After taking a picture, you would be at the uploading dock. Hit the *Complete* session, and the image would be sent to the caretaker. 

|![send_image_1](https://user-images.githubusercontent.com/12033253/46715697-ea2c1980-ccac-11e8-8025-8949d4df2b6a.png) | ![screenshot_1538235110](https://user-images.githubusercontent.com/12033253/46247584-e3054000-c450-11e8-8aea-aeefd9d07514.png) | ![screenshot_1538235190](https://user-images.githubusercontent.com/12033253/46247585-e3054000-c450-11e8-8758-fc88642a914c.png) |
| ------------- |:-------------:| -----:|



### Testing QR Code Scanner (TBA)
1) To add someone via QR Code, navigate to profiles, and click on the 'QR Code' button to view the QR Code assosciated with the account.
2) Then, on a second device navigate to the Contacts tab, and click on the 'add through QR button' at the bottom right.
3) Scan the QR Code, the contact should be added to the contacts list.

|![screenshot_1540102531](https://user-images.githubusercontent.com/37135456/47263827-22591500-d555-11e8-9566-f514c5145362.png) | ![screenshot_1540102535](https://user-images.githubusercontent.com/37135456/47263840-592f2b00-d555-11e8-97cd-07472c683e44.png) | ![screenshot_1540102547](https://user-images.githubusercontent.com/37135456/47263846-b32ff080-d555-11e8-9557-b3a6f8c0423e.png) |![screenshot_1540102556](https://user-images.githubusercontent.com/37135456/47263850-bf1bb280-d555-11e8-9495-3a4324520bfb.png)|
| ------------- |:-------------:| -----:| -----:|



## Unit Testing 

### Stimulate Travelling (Android Emulator)
Before starting to unit test EZMaps, we first need to stimulate our device as if it was travelling from one place to another. We can do this by downloading a GPX file that lists down our preferred route into a textual form.
*  In order to do so, we first have to lookup our desired route at [google maps](https://www.google.com.au/maps). Put in both the starting and ending destination, such as Queensberry Street and University of Melbourne, and click on a preferred route such as via Tin Alley. Also, remember to click on the mode of transport such as walking to get the timing right.
*  After handling all the settings, click on the url and copy it.
*  Head over to [GPS Visualizer](http://www.gpsvisualizer.com/convert_input?convert_format=gpx) to convert the link into a GPX file. 
    *  Make sure to click on the option GPX.
    *  Put in the Google Maps link to URL field.
    *  Put in this Google API Key [AIzaSyCBl0nwslLllTYtEK-O_NM9_dvAaBs7AyE].
* Click on the download link at the top, to download the GPX file.

| <img width="1098" alt="1" src="https://user-images.githubusercontent.com/12033253/46915766-e9242080-cffb-11e8-8cf5-84a7c7e9a9bd.png">  | <img width="1101" alt="2" src="https://user-images.githubusercontent.com/12033253/46915767-e9242080-cffb-11e8-8da4-cd5d62d632d5.png">  | 
| ------------- |:-------------:|

| <img width="1099" alt="3" src="https://user-images.githubusercontent.com/12033253/46915768-e9bcb700-cffb-11e8-8c7c-05320a5c0279.png">  | <img width="1094" alt="4" src="https://user-images.githubusercontent.com/12033253/46915809-b0387b80-cffc-11e8-8ad5-038bfe914e36.png"> | 
| ------------- |:-------------:|

* Open up the emulator device and click on the *More* option. 
* In Extended Controls, click on *Load GPX/KML* and choose the GPX file that you had just downloaded.
* Once it's loaded, you can click the play button at any point in time to stimulate the device travelling. You could also adjust the speed to your liking.

| <img width="389" alt="em" src="https://user-images.githubusercontent.com/12033253/46915870-c1ce5300-cffd-11e8-9439-3674fa63a469.png">   | <img width="820" alt="screen shot 2018-10-14 at 10 06 12 pm" src="https://user-images.githubusercontent.com/12033253/46915871-c1ce5300-cffd-11e8-80dd-5b6a8b072f2e.png">  | 
| ------------- |:-------------:|

## Testing UI 
* We have set up unit testing of our application for the UI. Since the majority of the app utilises heavily on the UI section of the app, our androidTest tests if any of the inflated elements are not showing up. To do this, go to the AndroidTest folder and click Run. 
* There are multiple problems regarding unit testing with GPS coordinates. We have used MOCK Locations as was provided. Unfortunately, the permission prevents us from allowing us to do more testing with the logic that we have set up for Automatic Swiping. 
    * For instance, in our EZDirection, Android is stubbornly prevents us from taking in permission from AndroidManifest and requests us to do it at Run Time. Hence, there's no way of testing the retrieval of GPS coordinates in the Unit Testing, given that Android Test runs the activity once and doesn't allow User interaction.  
    * **TLDR**: We can't give Permission dynamically but only at Run Time. This prevents us from unit testing anything with GPS. 
    * Since we can't do it in EZDirection, we have decided to unit test mock locations that is generated from the Android Test instead of the EZDirectionActivity to test case the logic of automatic swiping. This requeires the enabling of *Developer Options*. However, *it was working fine a week ago, but it stopped working since then.* 

This application notifies on your TV screen that your phone is ringing and that you received a message.
to compile your self the code : 
1-Clone the application repository
git clone https://github.com/hyperonex/phonetostbnotifier.git
2-Install the Android SDK
3-Add the Android Plugin to your Netbeans IDE
4-From the File/Open Project... menu select the folder phonetostbnotifier where you cloned the code
5-From the Run menu select Clean and Build Project
6-To test the application you : 
    a- need to setup an Android phone emulator with API >= 2.2 on a computer connected to the home router
    b- need an enigma 1 or 2 based STB already connected to the router
    c- check that your webif (OpenWebIf) is enabled
    d- select Run Project from the run menu on you IDE
    e- click on the menu button of phone (emulator) -> Settings
    f- fill the settings field
    g- click Save
    h- click again on the menu button of phone (emulator) -> Run
    i- call your phone (send an sms) (emulator) from the ddms application that you find in android-sdk/tools
    

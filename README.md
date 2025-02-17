## How to use (for Mac)

1. Install Node.js

   Below is an example using brew and nodebrew.
   ``` sh
   $ brew update
   $ brew install nodebrew
   
   $ echo 'export PATH=$HOME/.nodebrew/current/bin:$PATH' >> ~/.zshrc
   $ source .zshrc
   
   $ /opt/homebrew/opt/nodebrew/bin/nodebrew setup_dirs
   ```

   If you have no particular preference, install the stable version.
   ``` sh
   $ nodebrew install stable
   ```

   Specify the version of Node.js to use.
   Select the version you want to use from the ones displayed in "nodebrew ls".
   ``` sh
   $ nodebrew ls
   $ nodebrew use v22.12.0
   ```

2. Install Appium server

   Install the Appium server globally.
   ``` sh
   $ npm i -g appium
   ```

   Install [appium-uiautomator2-driver](https://github.com/appium/appium-uiautomator2-driver).
   ``` sg
   $ appium driver install uiautomator2
   ```

   To start the Appium server, run the following command:
   ``` sh
   $ appium
   ```

3. Appium Inspector

   If you need a tool to visualize your view tree structure, we recommend using [Appium Inspector](https://github.com/appium/appium-inspector/releases).
   You need to set [Capabilities](https://appium.io/docs/en/latest/guides/caps/) to use it.

   Capabilities Configuration Example:
   ``` json
   {
     "platformName": "android",
     "appium:automationName": "uiautomator2",
     "appium:udid": "emulator-5554"
   }
   ```

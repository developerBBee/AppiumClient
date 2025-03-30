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

---

## Kotlin DSL appium event functions

- Scenario building example

   ``` kotlin
   val SAMPLE_PHONE_EMU_SCENARIO = ComposeScenario()
       .topScreenScenario()
       .categoryScreenScenario()
       .partsScreenScenario()
       .assemblyScreenScenario()
       .partsScreen2ndScenario()
       .topScreen2ndScenario()
       .categoryScreen2ndScenario()
       .partsScreen3rdScenario()
       .assemblyScreen2ndScenario()
       .categoryScreen3rdScenario()
       .partsScreen4thScenario()
       .assemblyScreen3rdScenario()
       .partsScreen5thScenario()
   
   private fun ComposeScenario.topScreenScenario() = scenario {
       // Tap the Start button to show start dialog
       tap("start_button") { screenshotName = "start_dialog" }
   
       // Enter the assembly name in the text field
       inputText("assembly_name_text_field", "My assembly") { screenshotName = "start_dialog_input_text" }
   
       // Tap the Create button
       tap("create_assembly_button") { screenshotName = "select_parts_type" }
   }
   
   /**
    * Top screen 1st scenario
    */
   private fun ComposeScenario.topScreenScenario() = scenario {
       // Tap the Start button to display the start dialog
       tap("start_button") { screenshotName = "start_dialog" }
   
       // Enter the assembly name
       inputText("assembly_name_text_field", "Test Assembly") { screenshotName = "start_dialog_input_text" }
   
       // Tap the create button
       tap("create_assembly_button") { screenshotName = "select_parts_type" }
   }
   
   /**
    * Category selection screen 1st scenario
    */
   private fun ComposeScenario.categoryScreenScenario() = scenario {
       // Tap the PC case button
       tap("pccase") { screenshotName = "parts_pc_case_list" }
   }
   
   /**
    * Part selection screen 1st scenario
    */
   private fun ComposeScenario.partsScreenScenario() = scenario {
       // Scroll 3 times
       scroll("unselected_parts_list") {
           repeat = 3
           screenshotName = "parts_pc_case_scrolled"
       }
   
       // Tap element at index=2
       tapChild(tag = "unselected_parts_list", childIndex = 2) { screenshotName = "parts_pc_case_add_dialog" }
   
       // Tap the Edit button
       tap("edit_assembly_button") { screenshotName = "assembly_list" }
   }
   
   //...
   ```

### Scenario building constructor and functions

#### Start scenario from constructor

   - For View system
      ``` kotlin
      ViewScenario()
      ```
   - For Jetpack Compose
      ``` kotlin
      ComposeScenario()
      ```

#### Chain scenario builder function

   - Scenario builder function

      The `scenario()` function is an extension function of ViewScenario or ComposeScenario that helps you define the units of a scenario.
   
   - ViewScenario extension
      ``` kotlin
      fun ViewScenario.loginScreenScenario() = scinario {
         inputText("id_text_field", "TestId")
         inputText("password_text_field", "TestPassword")
         tap("login_button")
      }
      ```

   - ComposeScenario extension
      ``` kotlin
      private fun ComposeScenario.listScreenScenario() = scenario {
         scroll("item_list")
         tapChild("item_list", 2)
      }
      ```

#### Functions used within the scope of scenario()

   - `tap()` function
   
      Tap the target specified by the Compose test tag or View ID.
      ``` kotlin
      tap("start_button")
      ```

   - `tapText()` function
   
      Tap the target with the specified text.
      ``` kotlin
      tap("Login")
      ```

   - `repeatTaps()` function

      Consecutively tap the target specified by the Compose test tag or View ID, 10 times by default.
      ``` kotlin
      repeatTaps("qty_plus_button") {
         repeat = 3
      }
      ```

   - `inputText()` function

      Enter text into the text field specified by the Compose test tag or View ID.
      ``` kotlin
      inputText("message_field", "Hello world")
      ```

   - `scroll()` function

      Scroll the screen layout specified by the Compose test tag or View ID.
      ``` kotlin
      inputText("item_list_layout")
      ```

   - `tapChild()` functiion

      Tap the element at the specified index in the target list specified by the Compose test tag or View ID.
      ``` kotlin
      tapChild("item_list_layout", 2)
      ```

   - `androidKey()` function

      Execute Android Key Event by [KeyCodes](https://appium.github.io/java-client/io/appium/java_client/android/nativekey/AndroidKey.html).
      
      ``` kotlin
      androidKey(AndroidKey.BACK)
      ```

   - `hideKeyboard()` function

      Hide software keyboard.

      ``` kotlin
      hideKeyboard()
      ```

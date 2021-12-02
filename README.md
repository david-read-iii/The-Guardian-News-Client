# Installation
1. Clone this repository by entering this command into Bash:
```bash
git clone git@github.com:david-read-iii/The-Guardian-News-Client.git
```
2. Then, open the root project directory in Android Studio.

# Configuration
## Specify a The Guardian API key in Gradle BuildConfigs
1. Register for your The Guardian API key from [this site](https://open-platform.theguardian.com/access/).
2. Create a `theguardianapikey.properties` file in the root project directory.
3. Put the following info into the file. Replace `...` with your key. Replacing `...` with `test` should be sufficient for limited testing purposes:
```properties
THE_GUARDIAN_API_KEY="..."
```

# Build APK or Android App Bundle
1. From *Android Studio*, go to the *Build* menu.
2. Go to the *Build Bundle(s) / APK(s)* menu.
3. Select either *Build APK(s)* or *Build Bundle(s)*.
4. Wait for a notification to pop signifying the operation completion.
5. Select *locate* in the notification to navigate to the file location of the built artifact.
6. Install the artifact on your Android device and try it out.

# Maintainers
This project is mantained by:
* [David Read](http://github.com/david-read-iii)
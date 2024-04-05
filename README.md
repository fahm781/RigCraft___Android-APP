# RigCraft

RigCraft is an Android application developed using Kotlin and Java. It allows users to select and build their own custom PC rig from a list of components. The application also checks the compatibility of the selected components.

## Features

- Select components for your custom PC rig, including CPU, GPU, RAM, Storage, Power Supply, and Motherboard.
- Save your custom build for future reference.
- Check the compatibility of the selected components.
- View saved builds.

## Technologies Used

- Kotlin
- Java
- Gradle
- Android Studio
- Firebase Firestore for data storage
- Firebase Authentication for user management

## Setup

1. To run this project, you will need to have Android Studio installed. You can then clone the project and open it in Android Studio.

2. Sign up for the eBay Developers Program at [https://developer.ebay.com/](https://developer.ebay.com/). After signing up, you will receive a `clientId` and `clientSecret`. Add these to the `EbayTokenRegenerator.kt` file in your project. The file is located at `src/main/java/com/fahm781/rigcraft/ebayServices/EbayTokenRegenerator.kt`.

3. Get the OpenAI API key from [https://openai.com/blog/openai-api](https://openai.com/blog/openai-api). Add this key to the `OpenAiApiInterface.kt` file in your project. The file is located at `src/main/java/com/fahm781/rigcraft/chatbotServices/OpenAiApiInterface.kt`.

  Please replace the placeholders with the actual keys you received from eBay and OpenAI.
# Google Meet Link Telegram Bot

## Overview
This Telegram bot allows users to generate Google Meet links directly from a Telegram chat.
The bot integrates with the Google Calendar API to create events with Google Meet links and sends
these links back to the user in the chat.

## Features
- Create Google Meet links with a simple command.
- Customizable event details (summary, description, duration).
- Uses the Command design pattern to handle various bot commands.
- Custom annotation `@BotCommandMapping` for linking commands to their handlers.

## Prerequisites
- Java 17
- Maven
- A Google Cloud Project with the Calendar API enabled
- Telegram Bot Token

## Dependencies
- Spring Boot
- Spring Web (optional)
- Lombok
- Google API Client
- TelegramBots

## Preliminary Steps

### 1. Google Cloud Configuration

#### Enable Google Calendar API
1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
2. Create a new project or select an existing one.
3. Enable the Google Calendar API for your project.
4. Create OAuth 2.0 Client IDs credentials:
   - Go to the **Credentials** tab.
   - Click **Create credentials** > **OAuth 2.0 Client IDs**.
   - Set the **Application type** to **Web application**.
   - Add the following Authorized redirect URIs: `http://localhost:8080/oauth2callback`.
   - Download the credentials file (`credentials.json`) and place it in the `tmp` directory of your project.

#### Create a Test User
1. In the **OAuth consent screen** tab, add a test user (your email) to the **Test users** section.

#### Token Storage
Ensure you have a directory `tmp/tokens` to store the OAuth 2.0 tokens. This directory should be listed in `.gitignore` to 
keep your tokens secure and not shared in version control. The tokens directory will store your
OAuth 2.0 tokens after the first authentication to keep you logged in.

#### Notes
- Ensure that the `tmp/tokens` directory exists and is writable, as it is used to store OAuth tokens.
- Ensure that the `tmp/credentials.json` file exists and contains the correct OAuth2 credentials.

```gitignore
# Token storage
./tmp/tokens
./tmp/credentials.json
```

#### OAuth Consent Screen
- The application operates in Testing mode, allowing only test users to access the app. You can add test users in the OAuth consent screen tab. Make sure to add your email as a test user.
- In production, you would need to publish the app and get it verified by Google, but for most personal or small-scale projects, testing mode is sufficient.

#### Alternative Setup Using Service Account
- As an alternative to the OAuth2 setup, you can use a Google Service Account with domain-wide delegation. This is more complex and requires administrative access to the Google Workspace domain.
- For most users, the OAuth2 setup described above is simpler and more straightforward. 

With these steps and configurations, you will be able to run the Google Meet Link Telegram Bot effectively.

### 2. Telegram Bot Configuration

#### Create a Telegram Bot
1. Open the Telegram app and search for the `BotFather`.
2. Start a chat with the BotFather by clicking on the `Start` button.
3. Use the command `/newbot` to create a new bot.
4. Follow the instructions to choose a name and username for your bot.
5. After the bot is created, the BotFather will provide you with a token. Save this token, you will need it for the configuration (property `BOT_USERNAME` , `BOT_TOKEN`).

## Environment Configuration

### Create `.env` File

Before starting the application, make sure you have a `.env` file in the root directory of your project.
This file should hold all the necessary environment variables. You can use the provided `setup.sh` script to create 
the `.env` file from the `.env.dist` template.

1. Ensure you have the `.env.dist` file in the root directory of your project. This file contains example environment variables.
2. Run the setup script:

    ```bash
    chmod +x setup.sh
    ./setup.sh add-env
    ```
The script will check for the existence of the `.env` file and create it if it does not exist, using the values from `.env.dist`.

By setting up the `.env` file correctly, you ensure that your bot and
Google Calendar API integration have the necessary configuration to run successfully.

These steps are optional and only need to be done the first time to set up the environment variables.

### Build and Run the Application

3. Build the project using Maven:

    ```bash
    mvn clean install
    ```

4. Run the application:

    ```bash
    java -Dfile.encoding=UTF-8 -jar target/meetingbot-0.0.1-SNAPSHOT.jar
    ```

### Docker Setup
To run the application in a Docker container, follow these steps:

Build and Run the Docker Container

```bash
   docker-compose build
```

Build the Docker image:

```bash
   docker-compose up
```

### First Time Setup
When you run the application for the first time, it will open a browser window asking for your permission
to access your Google Calendar. After you grant permission, the tokens will be stored
in the tmp/tokens directory, and the application will be able to create events on your behalf.

`Important`: During the first run, the application will not start fully and will appear to hang.
In the logs, you will see a URL. You need to follow this URL in your browser to complete the OAuth2 authentication.
Once you grant permission, the tokens will be stored in the `tmp/tokens` directory,
and the application will proceed. From then on, it will automatically refresh the tokens as needed.

### Usage
1. Start a chat with your bot on Telegram.
2. Send the command `/link` to the bot.
3. The bot will reply with a Google Meet link.

#### Command Design Pattern
The bot uses the Command design pattern to handle various commands. 
Each command is a separate class implementing the BotCommand interface. 
Commands are linked to their handlers using the custom annotation @BotCommandMapping.

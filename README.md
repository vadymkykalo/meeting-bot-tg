# Google Meet Link Telegram Bot 
# meeting-bot-tg

## Overview
This Telegram bot allows users to generate Google Meet links directly from a Telegram chat.
The bot integrates with the Google Calendar API to create events with Google Meet links and sends
these links back to the user in the chat.

## Features
- Create Google Meet links with a simple command.
- Customizable event details (summary, description, duration).
- Handles UTF-8 encoded messages for proper support of various languages, including Cyrillic.
- Uses the Command design pattern to handle various bot commands.
- Custom annotation `@BotCommandMapping` for linking commands to their handlers.

## Prerequisites
- Java 8 or higher
- Maven
- A Google Cloud Project with the Calendar API enabled
- Telegram Bot Token

## Dependencies
- Spring Boot
- Spring Web
- Spring Boot Starter Security
- Spring Boot Starter Data JPA
- Lombok
- Google API Client
- TelegramBots

## Google Cloud Configuration

### Enable Google Calendar API
1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
2. Create a new project or select an existing one.
3. Enable the Google Calendar API for your project.
4. Create OAuth 2.0 Client IDs credentials:
    - Go to the **Credentials** tab.
    - Click **Create credentials** > **OAuth 2.0 Client IDs**.
    - Set the **Application type** to **Web application**.
    - Add the following Authorized redirect URIs: `http://localhost:8080/oauth2callback`.
    - Download the credentials file (`credentials.json`) and place it in the `tmp` directory of your project.

### Create a Test User
1. In the **OAuth consent screen** tab, add a test user (your email) to the **Test users** section.

### Token Storage
Ensure you have a directory `tmp/tokens` to store the OAuth 2.0 tokens. This directory should be listed in `.gitignore` to keep your tokens secure and not shared in version control. The tokens directory will store your OAuth 2.0 tokens after the first authentication to keep you logged in.

```gitignore
# Token storage
./tmp/tokens
./tmp/credentials.json
```

### First Time Setup
When you run the application for the first time, it will open a browser window asking for your permission 
to access your Google Calendar. After you grant permission, the tokens will be stored
in the `tmp/tokens` directory, and the application will be able to create events on your behalf.

## Telegram Bot Configuration

### Create a Telegram Bot
1. Open the Telegram app and search for the `BotFather`.
2. Start a chat with the BotFather by clicking on the `Start` button.
3. Use the command `/newbot` to create a new bot.
4. Follow the instructions to choose a name and username for your bot.
5. After the bot is created, the BotFather will provide you with a token. Save this token, you will need it for the configuration.

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/vadymkykalo/meeting-bot-tg
cd meeting-bot-tg
```

### 2. Build and Run the Application

```bash
mvn clean install
java -Dfile.encoding=UTF-8 -jar target/your-application.jar
```

### Usage
1. Start a chat with your bot on Telegram.
2. Send the command `/link` to the bot.
3. The bot will reply with a Google Meet link.

### Command Design Pattern
The bot uses the Command design pattern to handle various commands. 
Each command is a separate class implementing the BotCommand interface. 
Commands are linked to their handlers using the custom annotation @BotCommandMapping.

### Configuration Files
- application.yml: Main configuration file for the bot and Google Calendar API settings.
- GoogleCalendarConfig: Sets up the Google Calendar client with OAuth2 credentials.
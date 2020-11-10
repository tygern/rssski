# RSSski

Consume RSS feeds from other sources.

## Build and run

1.  Build app.
    ```bash
    ./gradlew clean build
    ```

1.  Run app.
    ```bash
    INSTAGRAM_URL="https://www.instagram.com" java -jar applications/rssski-app/build/libs/rssski-app.jar
    ```

## Deploy

1.  Login to Heroku.
    ```bash
    heroku login
    ```

1.  Install the Java plugin.
    ```bash
    heroku plugins:install java
    ```

1.  Push the jar to Heroku.
    ```bash
    heroku deploy:jar applications/rssski-app/build/libs/rssski-app.jar --app rssski
    ```

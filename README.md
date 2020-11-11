# RSSski

Consume RSS feeds from other sources.

## Build and run

1.  Build app and worker.
    ```bash
    ./gradlew clean build
    ```

1.  Run worker.
    ```bash
    INSTAGRAM_URL="https://www.instagram.com" REDIS_URL="redis://127.0.0.1:6379" java -jar applications/instagram-worker/build/libs/instagram-worker.jar
    ```


1.  Run app.
    ```bash
    REDIS_URL="redis://127.0.0.1:6379" java -jar applications/rssski-app/build/libs/rssski-app.jar
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

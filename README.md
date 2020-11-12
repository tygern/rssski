# RSSski

![chalet](readme-images/chalet.jpg)

RSSski creates RSS feeds from Instagram feeds.
I was inspired to build RSSski after learning about Instagram user [accidentallywesanderson](https://www.instagram.com/accidentallywesanderson).
I enjoyed the feed content, but was not willing to create an Instagram account to view it, and wondered if there was
another way to consume it.
There's a great [website](https://accidentallywesanderson.com) and [book](https://accidentallywesanderson.com/book/),
but I prefer to view the content periodically via my preferred medium, RSS.

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

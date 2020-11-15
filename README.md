# RSSski

![chalet](readme-images/chalet.jpg)

RSSski creates RSS feeds from Instagram feeds.
I was inspired to build RSSski after learning about Instagram user [accidentallywesanderson](https://www.instagram.com/accidentallywesanderson).
I enjoyed the feed content, but was not willing to create an Instagram account to view it, and wondered if there was
another way to consume it.
There's a great [website](https://accidentallywesanderson.com) and [book](https://accidentallywesanderson.com/book/),
but I prefer to view the content periodically via my preferred medium, RSS.

## Architecture

RSSski consists of two applications: the [RSSski Web App](applications/rssski-app) which serves RSS feeds and the
[Social Worker](applications/social-worker) which fetches the feed content from social media providers.
I started with a single application, but soon broke out the worker once I discovered that Instagram blocks profile
requests from public clouds.
The two application architecture allows me to deploy the web app to Heroku and the worker to a Raspberry Pi running on
my home network.

The worker fetches feed information from the social media servers and stores the full response in [Redis](https://redis.io).
When a user requests an RSS feed from the web app, the app fetches the response from Redis and transforms the response
into an RSS feed.

## Build and run

1.  Run Redis.

1.  Build the app and worker.
    ```bash
    ./gradlew clean build
    ```

1.  Run the worker.
    ```bash
    INSTAGRAM_URL="https://www.instagram.com" \
    REDIS_URL="redis://127.0.0.1:6379" \
    UPDATE_INTERVAL="60" \
    java -jar applications/social-worker/build/libs/social-worker.jar
    ```

1.  Run the app.
    ```bash
    REDIS_URL="redis://127.0.0.1:6379" \
    java -jar applications/rssski-app/build/libs/rssski-app.jar
    ```

## Usage

1.  Add an instagram feed for the worker to follow.

    ```bash
    curl -XPOST ${RSSSKI_APP_URL}/instagram/accidentallywesanderson
    ```

1.  Add the feed url (`${RSSSKI_APP_URL}/instagram/accidentallywesanderson`) to your favorite RSS reader.

1.  The worker fetches social feeds periodically, so be patient while waiting for your feed to update. 

## Deploy

1.  Login to Heroku.
    ```bash
    heroku login
    ```

1.  Install the Java plugin.
    ```bash
    heroku plugins:install java
    ```

1.  Push the web app jar to Heroku.
    ```bash
    heroku deploy:jar applications/rssski-app/build/libs/rssski-app.jar --app rssski
    ```

1.  Run the social worker jar somewhere that is able to access Instagram profiles.
    I run it on a Raspberry Pi using _nohup_
    
    ```bash
    INSTAGRAM_URL="https://www.instagram.com" \
    REDIS_URL="${HEROKU_REDIS_URL}" \
    UPDATE_INTERVAL="60" \
    nohup java -jar social-worker.jar > social-worker.jar &
    ```
    
    but there's probably a better way to do it using systemd.

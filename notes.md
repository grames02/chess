# 6/23/2025

## What Was Accomplished Today

Right now, I'm going back through my code and I'm going to start planning how I want to do the Chess project. I don't want to just jump into it and then get confused. I've already done the setup and watched the video. Now I have to do the rest of the Chess logistics. I also am trying to better become acustumed with Git. I'm not the best at commiting directly from the terminal, and need to better understand it. Hopefully I can improve on that throughout the class. Right now, I prefer to just edit it inside of the Git website itself, but I know that's not the most ideal method.

Alrighty, I've been looking at the code for a couple of hours now. I'm still not done, but I'm starting to understand it better. I started making a piece movement calculator class (as suggested by one of the videos.) I'm also more familiar with the code and structure of the game (how lowercase pieces are black, etc.) I'll resume tomorrow.


# 6/27/2025

## What Was Accomplished Today

Alrighty, I went ahead and got my slack account all configured. I feel like last time was good for better understanding the code itself. This time, I'm hoping to get some of it actually done. I went ahead and asked in Slack for clarification on the assignment requirements as well.

I've been working on the 'Chess Board' file. It's been pretty interesting. I've mainly constructed the reset board function. I thought I had done it correctly, however I keep getting errors that I'm out of Range. So now I'm revisiting the instructions and am trying to see where I'm going wrong. I most likely could've been more efficient with how I setup the board, but I think it'll work nonetheless. Just gotta find where the bugs are.


# 6/29/2025 - 6/30/2025

## What Was Accomplished

Awesome!! I went on a late night coding grind and tried to figure out why my resetboard wasn't working. I went onto Slack and saw people talking about the Hashing and Equals overrides. These were my issue! I also had the board off by one and had to implement a minus 1 into my add and get location (since they're using 0 as 1.) Anyways, I watched the video explaining how hashing and equals all worked. Interesting how it provides the address and not the actual content. It took me a little while to fully understand this, but then I went back and implemented all the steps and everything is passing!

The only thing that I have left is the pieces and how they individually move. I'm following the class advice and making a piece calculator file, which I can referrence. I might work on it for a bit longer. We'll see if I get any breakthroughs.

Alrighty, it's a little past 1 AM and I'm calling it. I didn't make any of the piece movement patterns yet. However, I have setup where they will be implemented and essentially made a skeleton of my ChessPieceCalculator code. I'll wrap it up in the morning.

# 6/30/2025 Part 2

## What Was Accomplished

Now I'm finalizing the piece movement. I deicded it'd be easier to create an in bounds calculator. Then for all the pieces I can use this code to either include or exclude a position.

I've completed the King movement pattern. Now I'll be doing the others, however I will have to implement a while loop since their movement range is greater than 1. I will base many of them off of the king though.

I've accomplished all of them, EXCEPT for the pawn. I was able to use a similar format for them all. The Bishop, Rook, and Queen all involved while loops. Whereas the King and Knight only required if statements. The pawn is definitely more complex. I think I could apply an if-then scenario for it checking whether or not the diagonal spaces are being occupied. If they are, and of the other teams color then I will grant it to move there. I also still need to implement how it can move two spaces at the beginning. AND how it can be promoted (I think that needs to be a part of this deliverable anyways.)

Ok, coolio! I finished it up! I figured out the easiest way to do the promotion would just be to set an integer equal to one of the promotion rows (1 or 8 depending on team) at the end. Then integrate the rest from there.

Well......... I submitted it and I think I've been using GitHub incorrectly. It says I've only done 5 commits. Granted it took me a little bit to fully figure out how to commit again. So, I need to talk to a TA.



# URL for Phase 2
## Link: https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmIxXi+AE0DsIyMQinAEbSHACgwAAMhAWSFFhzBOtQ-rNG0XS9AY6j5GgWaKnMay-P8HBXKBgpAf64Hlp8kIgupOxfNCjzARJVBIjAIw-risAEkSYBac6KC7jS+4vIecgoEJIkYggwnirOVzDg6S7CrZj6CcFaA8lcmAqsGGputqur6iphgcBAagwGgEDMFaaI3p5d5Jr6bn1P5IVbm+hgVR2NT1AAcoV9noiiDHaY1YlphmBE5qoebzNBRYlq6WrSLlAptcwUZog29Glby5VWTZPZ9tuDUfhZ-o1WgG5QJgunwlZemEQZ8wkah3wUVR9Y3bR6FnZUfUwLh+GjJd7yQU9ZFjPdiGPaRS1NoxnjeH4-heCg6AxHEiSw-D-m+FgYmCqB9QNNIEYCRG7QRt0PTyaoinDEDSEvTpe3fighJqMwAC8sX2GjMAQAAZlUCnxqd95Rajp4YgApCAPjFuYboi6SxggdU+3CezbrU8myCpssaX+Yy64tKeYyNgxEOQyx-hdTE2DihqAlojAADiSoaBj52lg0dsE8T9hKhTl4PWgPWVKrlSnS8lPoKBiX85F9QZBYqA0PbFY2zkmg6PLkl02AEuHQ7cyCSVAfwOr2HLE0+jMLneXJ2A0GG8bpjMdDHAAOxuE4KBODEEbBHA3EAGzwBOhiVzARTF+JlUZ9jrQdJ73vTL7wNKaMXtzC1SquRnNOwnpq8oOvczmTv8KVMug9HigGJ7wfXKp+nzUwCiWfvIncxB0XJQ18sI9wEPux18bRuARLAoD7BATYCMkAJDACAsBECABSEBxSv0MP4ZIoA1Rj0-pjBWrsmjMlkj0PePt4JLyzNgBAwAQFQDgBAIKUA1h7wAJLSE3omYOtMXjXw3idWmAt6gACskFoCvkqG+ss06NSxo-Jk2cUHv3emMEeiDxQoH-stQBUMAheCoZA6BOj5SIGDLAYA2AKGEDyAUUeztJ4PxxnjAmRNejGHfiHWy3DD68OPvwouxjRYyzvlI3B9QtDyAAPogG4HgNRCjx7QUDAgGAe8jDaBgJE4xaiXAG2WkAA

# 7/3/2025

## What Was Accomplished

I downloaded the Phase 1 HW and got all the test files loaded. Now I'm working on ChessGame. I'm not too deep into it, just messing around a bit. I'm also working on better understanding GitHub.

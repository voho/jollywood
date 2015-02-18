# Jollywood

[![Travis](https://travis-ci.org/voho/jollywood.svg?branch=master)](https://travis-ci.org/voho/jollywood) [![codecov.io](https://codecov.io/github/voho/jollywood/coverage.svg?branch=master)](https://codecov.io/github/voho/jollywood?branch=master)

Simple actor system written in Java. Jollywood... actors... like in Hollywood, but in Java, get the joke? Hahaha! Wonderful! But do not laugh the whole day and start working!

## A little of theory

- http://en.wikipedia.org/wiki/Actor_model

## Actor system lifecycle

Every actor must live in an actor system. You can create actor system like this:

    final ActorSystem system = new ActorSystem(8);

The number parameter of actor system constructor specifies a count of threads dedicated to processing actor messages.

After an actor system is ready and initial messages are sent, you can wait on actor system finish by calling this method:

    system.shutdown();
    
What this method does, is waiting for all actors to finish their work and then shuts the actor system down.

## Defining an actor

You can define actor as a lambda function implementing the *ActorDefinition* function interface:

    final ActorDefinition driverDef = (self, message) -> {
      // ...message processing...
    };

If you want to use that actor (e.g. send messages to it), you must register it into a certain actor system. You can do that like this:

    final ActorHandle counterRef = system.defineActor("COUNTER", counterDef);

## Sending messages

You can send message to a single actor like this:

    counterRef.sendMessage(self, new MessageContent("increment", "Hi, please increment your value by one."));

You can also broadcast message to all actors in a certain system:

    system.broadcastMessage(self, new MessageContent("invitation", "Let us go for a beer!"));
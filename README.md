# Jollywood

[![Travis](https://travis-ci.org/voho/jollywood.svg?branch=master)](https://travis-ci.org/voho/jollywood) [![codecov.io](https://codecov.io/github/voho/jollywood/coverage.svg?branch=master)](https://codecov.io/github/voho/jollywood?branch=master)

Simple actor system written in Java.
Jollywood... actors... like in Hollywood, but in Java, get the joke?
Hahaha! Wonderful! But do not laugh the whole day and start working!

## A little of theory

- http://en.wikipedia.org/wiki/Actor_model

## Actor system lifecycle

Every actor must live in an actor system.
You can create actor system like this:

    final ActorSystem system = new ActorSystem(8);

The number parameter of actor system constructor specifies a count of threads dedicated to processing actor messages.

After an actor system is ready and initial messages are sent, you can wait on actor system finish by calling this method:

    system.shutdown();
    
What this method does, is waiting for all actors to finish their work and then shuts the actor system down.

## Defining an actor

You can define actor behavior as a lambda function implementing the *ActorDefinition* function interface:

    final ActorDefinition driverDef = (self, message) -> {
      // ...message processing...
    };

Definition is independent of any actor system and can be actually used to define as many actors in as many different actor systems as you wish.

## Instantiating actor

Actor instance is basically an actor definition living in a certain actor system having its own message queue.
To create an actor instance (e.g. to be able to send messages to it), you must register an actor definition into a certain actor system.
You can do that like this:

    final ActorHandle counterRef = system.defineActor("COUNTER", counterDef);

This resulting handle can be used in application as an actor address.

## Closing actor

If you want to close a certain actor (remove it from its actor system), you can do it like this:

    counterRef.close();

Also it is possible to close the current actor while processing messages, like this:

    self.close();

Closing an actor means that actor...

1. will not accept any more messages
1. will finish processing of all its remaining messages in the queue
1. will be removed from the actor system

## Sending messages

When sending messages, you must posses a recipient reference (instance of *ActorHandle*).

You can send message to a single actor like this:

    counterRef.sendMessage(self, new MessageContent("increment", "Hi, please increment your value by one."));

Also you might reply to an original message sender like this:

    message.getSender().sendMessage(self, new MessageContent("response", "I liked your message."));

You can also broadcast message to all actors in a certain system:

    system.broadcastMessage(self, new MessageContent("invitation", "Let us go for a beer!"));

## High-level architecture

![architecture](http://www.plantuml.com/plantuml/png/RKz13e903BplAoOSIVRW1qoC7hnuyWKh56nSMiDMbD_B4c8muT9sffrEcnuipz273id6I5FikR9N5zsXCkslOpBgKEmAgFSeoW8pVmvIHtAh6hxMj_WzBe7ZJJ-RlPaxKSF2nYfkcONFEaefjEIMF7kMsNFA2tTKCH9pJjG8aHg3Ddy70I4ZH1vOMh0W8Cq_E98QMRUbNfq4r1bGMZRTw1u6rlwd_m80)
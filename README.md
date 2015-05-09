# Jollywood

[![Travis](https://travis-ci.org/voho/jollywood.svg?branch=master)](https://travis-ci.org/voho/jollywood) [![codecov.io](https://codecov.io/github/voho/jollywood/coverage.svg?branch=master)](https://codecov.io/github/voho/jollywood?branch=master)

Simple actor system written in Java.
Jollywood... actors... like Hollywood, but in Java, got the joke?
Hahaha! Wonderful! But do not laugh the whole day and start working!

## A little bit of theory

- http://en.wikipedia.org/wiki/Actor_model

## High-level overview

The basic entity is an *Actor*. This entity consist of these things:

- mailbox (queue of messages to process represented by the *Message* class)
- behavior definition which defines how the actor reacts on messages (*StatefulActorDefinition*, *StatelessActorDefinition*)

Each actor lives in an *ActorSystem*.
An actor living in an actor system is represented by the *ActorHandle* class, which can be used to send messages to this actor.
One actor definition can be used multiple times to create arbitrary actors in different systems.
It is similar as object programming.
You can imagine actor behavior definition as a class and the actor handle as an instance of that class.

## Actor system

You can create actor system like this:

    final ActorSystem system = new ActorSystem(8);

The number parameter of the constructor specifies a count of threads dedicated to processing actor messages.

To close the actor system after all actors are closed, call this:

    system.shutdownAfterActorsClosed();

This method will block the current thread until all actors are closed.

<span class="octicon octicon-telescope"></span>
In the future, more options how to close system will be available.

## Defining an actor

You can define both stateless and stateful actors.

### Stateless actors

Stateless actors only define behavior based on messages.
To define a behavior, you have to create an instance of *StatelessActorDefinition* functional interface.

    StatelessActorDefinition statelessActorDef = new StatelessActorDefinition() {
        @Override
        public void processMessage(ActorHandle self, Message message) throws Exception {
            // ...
        }
    };

Or you can make it shorter by using lambda:

    StatelessActorDefinition statelessActorDef = (self, message) -> {
        // ...
    };

After the definition is ready, register it to an actor system to obtain a reference to this actor, like this:

    ActorHandle statelessActorRef = system.defineActor(statelessActorDef);

### Stateful actors

Stateful actors define behavior based on message and some inner state, which is accessible whenever a message is processed.

    StatefulActorDefinition<State> statefulActorDef = new StatefulActorDefinition<State>() {
            @Override
            public void processMessage(ActorHandle self, State state, Message message) throws Exception {
                // ...
            }
        };

Or you can make it shorter by using lambda:

        StatefulActorDefinition<State> statefulActorDef = (self, state, message) -> {
            // ...
        };

After the definition is ready, register it to an actor system adding an initial state to obtain a reference to this actor, like this:

    ActorHandle statefulActorRef = system.defineActor(statefulActorDef, new State());

## Sending messages to single actor

When sending messages, you must posses a recipient reference (instance of *ActorHandle*).

You can send message to a single actor like this:

    actorRef.sendMessage(self, "increment", "Hi, please increment your value by one.");

Also you might reply to an original message sender like this:

    message.getSender().sendMessage(self, "response", "I liked your message.");

## Sending messages to all actors in a system

You can also broadcast message to all actors in a certain system:

    self.getSystem().broadcastMessage(self, "invitation", "Let us go for a beer!");

## Closing an actor

It is possible to close the current actor while processing messages, like this:

    self.closeActor();

Closing means that the closed actor will not accept any more messages and will be removed from the actor system after it finishes processing of the existing messages.

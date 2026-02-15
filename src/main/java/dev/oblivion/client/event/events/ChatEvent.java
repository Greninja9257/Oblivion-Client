package dev.oblivion.client.event.events;

import dev.oblivion.client.event.Event;

public class ChatEvent extends Event {
    private final String message;

    public ChatEvent(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }

    public static class Send extends ChatEvent {
        public Send(String message) { super(message); }
    }

    public static class Receive extends ChatEvent {
        public Receive(String message) { super(message); }
    }
}

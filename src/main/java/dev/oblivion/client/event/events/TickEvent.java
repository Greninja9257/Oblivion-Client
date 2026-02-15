package dev.oblivion.client.event.events;

import dev.oblivion.client.event.Event;

public class TickEvent extends Event {
    public static class Pre extends TickEvent {}
    public static class Post extends TickEvent {}
}

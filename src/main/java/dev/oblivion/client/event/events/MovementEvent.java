package dev.oblivion.client.event.events;

import dev.oblivion.client.event.Event;

public class MovementEvent extends Event {
    public static class Pre extends MovementEvent {}
    public static class Post extends MovementEvent {}
}

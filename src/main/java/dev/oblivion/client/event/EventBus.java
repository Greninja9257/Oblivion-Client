package dev.oblivion.client.event;

import dev.oblivion.client.OblivionClient;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {
    private final Map<Class<? extends Event>, List<Listener>> listenerMap = new ConcurrentHashMap<>();

    private record Listener(Object instance, Method method, EventPriority priority) implements Comparable<Listener> {
        @Override
        public int compareTo(Listener other) {
            return this.priority.ordinal() - other.priority.ordinal();
        }
    }

    public void register(Object subscriber) {
        for (Method method : subscriber.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(EventHandler.class)) continue;
            if (method.getParameterCount() != 1) continue;

            Class<?> paramType = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(paramType)) continue;

            @SuppressWarnings("unchecked")
            Class<? extends Event> eventType = (Class<? extends Event>) paramType;
            EventHandler annotation = method.getAnnotation(EventHandler.class);

            method.setAccessible(true);

            List<Listener> listeners = listenerMap.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>());
            listeners.add(new Listener(subscriber, method, annotation.priority()));
            listeners.sort(Comparator.naturalOrder());
        }
    }

    public void unregister(Object subscriber) {
        listenerMap.values().forEach(listeners ->
            listeners.removeIf(l -> l.instance() == subscriber)
        );
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> T post(T event) {
        List<Listener> listeners = listenerMap.get(event.getClass());
        if (listeners == null || listeners.isEmpty()) return event;

        for (Listener listener : listeners) {
            try {
                listener.method().invoke(listener.instance(), event);
            } catch (Exception e) {
                OblivionClient.LOGGER.error("Error dispatching event {} to {}",
                    event.getClass().getSimpleName(),
                    listener.instance().getClass().getSimpleName(), e);
            }
            if (event.isCancelled()) break;
        }
        return event;
    }
}

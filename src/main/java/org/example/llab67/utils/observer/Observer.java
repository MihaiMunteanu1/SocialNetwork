package org.example.llab67.utils.observer;

import org.example.llab67.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);

}
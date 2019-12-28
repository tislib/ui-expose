package net.tislib.uiexpose.lib.data;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Value<T> {
    private final Type<T> type;
    private final T value;

    public Type<T> getType() {
        return type;
    }

    public T getValue() {
        return value;
    }
}

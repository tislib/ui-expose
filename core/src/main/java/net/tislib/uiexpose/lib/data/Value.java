package net.tislib.uiexpose.lib.data;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Value<T> {
    private final UIExposeType<T> type;
    private final T value;

    public UIExposeType<T> getType() {
        return type;
    }

    public T getValue() {
        return value;
    }
}

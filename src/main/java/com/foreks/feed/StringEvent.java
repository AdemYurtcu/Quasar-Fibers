package com.foreks.feed;

import com.lmax.disruptor.EventFactory;

public class StringEvent {
    private String                          value;

    public static EventFactory<StringEvent> FACTORY = new EventFactory<StringEvent>() {
                                                        @Override
                                                        public StringEvent newInstance() {
                                                            return new StringEvent();
                                                        }
                                                    };

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }
}

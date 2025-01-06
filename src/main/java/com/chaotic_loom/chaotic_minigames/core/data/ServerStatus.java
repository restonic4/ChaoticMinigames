package com.chaotic_loom.chaotic_minigames.core.data;

public class ServerStatus extends GenericStatus {
    private Type type;

    public ServerStatus() {
        super();
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        LOBBY, PARTY
    }
}

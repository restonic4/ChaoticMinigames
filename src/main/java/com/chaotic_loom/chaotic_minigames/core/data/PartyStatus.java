package com.chaotic_loom.chaotic_minigames.core.data;

public class PartyStatus extends GenericStatus{
    private State state;

    public PartyStatus() {
        super();
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public enum State {
        BEFORE_VOTING_INTERMISSION, VOTING, AFTER_VOTING_INTERMISSION, PLAYING, IDLE
    }
}

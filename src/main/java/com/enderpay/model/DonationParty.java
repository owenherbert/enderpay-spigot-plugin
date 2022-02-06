package com.enderpay.model;

public class DonationParty {

    private int id;
    private String name;
    private float goal;
    private float percentageComplete;
    private boolean hasExecuted;
    private String startedAtIso8601;
    private String startedAtFriendly;
    private String endsAtIso8601;
    private String endsAtFriendly;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getGoal() {
        return goal;
    }

    public void setGoal(float goal) {
        this.goal = goal;
    }

    public float getPercentageComplete() {
        return percentageComplete;
    }

    public void setPercentageComplete(float percentageComplete) {
        this.percentageComplete = percentageComplete;
    }

    public boolean isHasExecuted() {
        return hasExecuted;
    }

    public void setHasExecuted(boolean hasExecuted) {
        this.hasExecuted = hasExecuted;
    }

    public String getStartedAtIso8601() {
        return startedAtIso8601;
    }

    public void setStartedAtIso8601(String startedAtIso8601) {
        this.startedAtIso8601 = startedAtIso8601;
    }

    public String getStartedAtFriendly() {
        return startedAtFriendly;
    }

    public void setStartedAtFriendly(String startedAtFriendly) {
        this.startedAtFriendly = startedAtFriendly;
    }

    public String getEndsAtIso8601() {
        return endsAtIso8601;
    }

    public void setEndsAtIso8601(String endsAtIso8601) {
        this.endsAtIso8601 = endsAtIso8601;
    }

    public String getEndsAtFriendly() {
        return endsAtFriendly;
    }

    public void setEndsAtFriendly(String endsAtFriendly) {
        this.endsAtFriendly = endsAtFriendly;
    }
}

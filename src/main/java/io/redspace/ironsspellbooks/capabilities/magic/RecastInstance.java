package io.redspace.ironsspellbooks.capabilities.magic;

import io.redspace.ironsspellbooks.api.spells.ICastData;

import javax.annotation.Nullable;

public class RecastInstance {
    private int castCount;
    private int maxCastCount;
    private int timeRemaining;
    private int recastDuration;
    @Nullable
    ICastData castData;

    public RecastInstance(int castCount, int maxCastCount, int recastDuration) {
        this.castCount = castCount;
        this.maxCastCount = maxCastCount;
        this.recastDuration = recastDuration;
        this.timeRemaining = recastDuration;
    }

    public RecastInstance withCastData(ICastData data) {
        this.castData = data;
        return this;
    }

    @Nullable
    public ICastData getCastData() {
        return castData;
    }

    public int getCastCount() {
        return castCount;
    }

    public int getMaxCastCount() {
        return maxCastCount;
    }

    public int getTicksRemaining() {
        return timeRemaining;
    }

    public int getRecastDuration() {
        return recastDuration;
    }

    public float getRemainingPercent() {
        return timeRemaining / (float) recastDuration;
    }

    public void updateRecast() {
        castCount++;
        timeRemaining = recastDuration;
    }

    @Override
    public String toString() {
        return String.format("RecastInstance %s\n%s/%s\n%s/%s", super.toString(), castCount, maxCastCount, timeRemaining, recastDuration);
    }
}
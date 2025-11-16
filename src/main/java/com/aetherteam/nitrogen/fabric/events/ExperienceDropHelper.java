package com.aetherteam.nitrogen.fabric.events;

public class ExperienceDropHelper extends CancellableCallbackImpl {

    private final int originalExperiencePoints;

    private int droppedExperiencePoints;

    public ExperienceDropHelper(int originalExperience) {
        this.originalExperiencePoints = this.droppedExperiencePoints = originalExperience;
    }

    public int getDroppedExperience() {
        return droppedExperiencePoints;
    }

    public void setDroppedExperience(int droppedExperience) {
        this.droppedExperiencePoints = droppedExperience;
    }

    public int getOriginalExperience() {
        return originalExperiencePoints;
    }

    public int getFinalExperienceAmount() {
        return this.isCanceled() ? 0 : this.getDroppedExperience();
    }
}

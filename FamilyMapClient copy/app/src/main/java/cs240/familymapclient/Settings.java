package cs240.familymapclient;

public class Settings {
    private boolean lifeStoryLines;
    private boolean familyTreeLines;
    private boolean spouseLines;
    private boolean paternalSideFilter;
    private boolean maternalSideFilter;
    private boolean maleFilter;
    private boolean femaleFilter;
    private boolean settingsChangedFlag;

    public Settings() {
        lifeStoryLines = true;
        familyTreeLines = true;
        spouseLines = true;
        paternalSideFilter = true;
        maternalSideFilter = true;
        maleFilter = true;
        femaleFilter = true;
        settingsChangedFlag = false;
    }

    public void resetSettings() {
        lifeStoryLines = true;
        familyTreeLines = true;
        spouseLines = true;
        paternalSideFilter = true;
        maternalSideFilter = true;
        maleFilter = true;
        femaleFilter = true;
        settingsChangedFlag = false;
    }

    public boolean isLifeStoryLinesOn() {
        return lifeStoryLines;
    }

    public void setLifeStoryLines(boolean lifeStoryLines) {
        this.lifeStoryLines = lifeStoryLines;
        settingsChangedFlag = true;
    }

    public boolean isFamilyTreeLinesOn() {
        return familyTreeLines;
    }

    public void setFamilyTreeLines(boolean familyTreeLines) {
        this.familyTreeLines = familyTreeLines;
        settingsChangedFlag = true;
    }

    public boolean isSpouseLinesOn() {
        return spouseLines;
    }

    public void setSpouseLines(boolean spouseLines) {
        this.spouseLines = spouseLines;
        settingsChangedFlag = true;
    }

    public boolean isPaternalSideFilterOn() {
        return paternalSideFilter;
    }

    public void setPaternalSideFilter(boolean paternalSideFilter) {
        this.paternalSideFilter = paternalSideFilter;
        settingsChangedFlag = true;
    }

    public boolean isMaternalSideFilterOn() {
        return maternalSideFilter;
    }

    public void setMaternalSideFilter(boolean maternalSideFilter) {
        this.maternalSideFilter = maternalSideFilter;
        settingsChangedFlag = true;
    }

    public boolean isMaleFilterOn() {
        return maleFilter;
    }

    public void setMaleFilter(boolean maleFilter) {
        this.maleFilter = maleFilter;
        settingsChangedFlag = true;
    }

    public boolean isFemaleFilterOn() {
        return femaleFilter;
    }

    public void setFemaleFilter(boolean femaleFilter) {
        this.femaleFilter = femaleFilter;
        settingsChangedFlag = true;
    }

    //these are used for when the map fragment needs to be re-marked
    public boolean changedFlag() { return settingsChangedFlag; }

    public void resetChangedFlag() { settingsChangedFlag = false; }
}

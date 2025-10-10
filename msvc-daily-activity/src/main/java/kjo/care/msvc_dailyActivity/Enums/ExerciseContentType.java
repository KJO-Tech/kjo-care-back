package kjo.care.msvc_dailyActivity.Enums;

public enum ExerciseContentType {
    TEXTO("Texto"),
    JUEGO("Juego"),
    AUDIO("Audio"),
    VIDEO("Video");

    private final String displayName;

    ExerciseContentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
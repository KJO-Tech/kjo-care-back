package kjo.care.msvc_dailyActivity.Enums;

public enum ExerciseDifficultyType {
    PRINCIPIANTE("Principiante"),
    INTERMEDIO("Intermedio"),
    AVANZADO("Avanzado");

    private final String displayName;

    ExerciseDifficultyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

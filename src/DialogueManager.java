public class DialogueManager {
    private String[] dialogueLines = {
            "The Monastery's walls are strong, but the world outside is changing.",
            "The Red Slime is approaching from the south east!",
            "Be careful out there, traveler. Be seeing you!"
    };

    private boolean isActive = false;
    private boolean isFinished = false;
    private int currentLineIndex = 0;

    public void startDialogue() {
        if (!isFinished) {
            isActive = true;
        }
    }

    public void advanceLine() {
        currentLineIndex++;
        if (currentLineIndex >= dialogueLines.length) {
            isActive = false;
            isFinished = true;
            currentLineIndex = 0;
        }
    }

    public void resetDialogue() {
        isFinished = false;
        currentLineIndex = 0;
    }

    public String getCurrentLine() {
        if (currentLineIndex < dialogueLines.length) {
            return dialogueLines[currentLineIndex];
        }
        return "";
    }

    public String getNPCName() {
        return "Old Man Elric";
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isFinished() {
        return isFinished;
    }
}

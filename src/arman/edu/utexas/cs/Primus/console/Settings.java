package arman.edu.utexas.cs.Primus.console;

/**
 * Singleton Settings class for global access to user defined settings.
 */
public class Settings {

    // Singleton instance variable.
    private static Settings settings;

    // Degrees vs. Radians setting.
    private boolean degrees;

    // Singleton constructor
    private Settings() {
        // Default value for degrees setting
        this.degrees = true;
    }

    /**
     * Creates an instance of this singleton Settings class. If it already is instantiated,
     * then return Settings object.
     *
     * @return this Settings instance
     */
    public static Settings getSettings() {
        if (settings == null)
            settings = new Settings();
        return settings;
    }

    public boolean useDegrees() {
        return degrees;
    }

    public void setDegrees(boolean degrees) {
        this.degrees = degrees;
    }
}

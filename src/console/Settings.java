package console;

public class Settings {

    private static Settings settings;

    private boolean degrees;

    private Settings(){
        this.degrees = true;
    }

    public static Settings getSettings(){
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

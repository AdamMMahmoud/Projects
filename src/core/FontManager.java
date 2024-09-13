package core;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;

public class FontManager {
    private Font customFont;

    public FontManager(String fontFilePath, float fontSize) {
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontFilePath)).deriveFont(fontSize);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (Exception e) {
            e.printStackTrace();
            customFont = new Font("SansSerif", Font.PLAIN, (int) fontSize);
        }
    }

    public Font getFont() {
        return customFont;
    }
}

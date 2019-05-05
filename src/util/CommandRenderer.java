package util;

import com.nmn.keystroke.java.Command;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandRenderer extends JLabel implements ListCellRenderer<Command> {
    Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Override
    public Component getListCellRendererComponent(JList<? extends Command> list, Command value, int index, boolean isSelected, boolean cellHasFocus) {
        BufferedImage image = getIcon(value.getBase64Image());

        setIcon(new ImageIcon(image));
        setText(value.getName());

        return this;
    }


    private BufferedImage getIcon(String src) {
        BufferedImage image = null;
        if (src != null && src.length() > 0) {
            byte[] bytes = Base64.getDecoder().decode(src);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            try {
                image = ImageIO.read(byteArrayInputStream);
            } catch (Exception e) {
                LOG.log(Level.WARNING, Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }
        }
        return image;
    }
}

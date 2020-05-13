package net.gudenau.minecraft.customskins;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommonStuff {
    private static BitSet SKIN_MASK;
    private static final AtomicBoolean LOADED = new AtomicBoolean(false);
    public static BitSet loadMask(){
        synchronized (LOADED) {
            if(!LOADED.getAndSet(true)) {
                try (InputStream stream = CommonStuff.class.getResourceAsStream("/assets/gud_customskins/mask.png")) {
                    BufferedImage image = ImageIO.read(stream);
                    int[] pixels = new int[64 * 64];
                    image.getRGB(0, 0, 64, 64, pixels, 0, 64);
                    SKIN_MASK = new BitSet(64 * 64);
                    for (int i = 0; i < pixels.length; i++) {
                        SKIN_MASK.set(i, pixels[i] == 0xFFFFFFFF);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(0);
                    throw new RuntimeException(e);
                }
            }
        }
        return SKIN_MASK;
    }
}

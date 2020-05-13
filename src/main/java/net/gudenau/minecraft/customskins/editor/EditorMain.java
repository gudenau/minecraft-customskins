package net.gudenau.minecraft.customskins.editor;

import net.gudenau.minecraft.customskins.CommonStuff;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.zip.CRC32;
import java.util.zip.GZIPOutputStream;

public class EditorMain {
    private static JFrame frame;

    private static JLabel inputLabel;
    private static JLabel outputLabel;

    private static ImageIcon inputIcon;
    private static ImageIcon outputIcon;

    private static BufferedImage inputImage;
    private static BufferedImage outputImage;
    private static JTextArea textArea;

    private static File outputFile = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Skin Editor");
            try {
                frame.setIconImage(ImageIO.read(EditorMain.class.getResourceAsStream("/assets/gud_customskins/icon.png")));
            } catch (IOException ignored) {}
            frame.setContentPane(createContentPane());
            frame.setSize(640, 480);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }

    private static JPanel createContentPane() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.WEST;
        panel.add(new JLabel("Input"), constraints);

        constraints.gridy++;

        inputImage = new BufferedImage(64, 64, BufferedImage.TYPE_4BYTE_ABGR);
        inputIcon = new ImageIcon(inputImage);
        inputLabel = new JLabel(inputIcon);
        inputLabel.setOpaque(true);
        inputLabel.setBackground(Color.WHITE);
        inputLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory() || f.getName().toLowerCase().endsWith(".png");
                    }

                    @Override
                    public String getDescription() {
                        return "Minecraft Skins";
                    }
                });
                chooser.setCurrentDirectory(new File("."));
                chooser.setMultiSelectionEnabled(false);
                if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    File inputFile = chooser.getSelectedFile();
                    try {
                        BufferedImage inputImage = ImageIO.read(inputFile);
                        if (inputImage.getWidth() == 64) {
                            if (inputImage.getHeight() == 64) {
                                String name = inputFile.getName();
                                name = name.substring(0, name.lastIndexOf("."));
                                name = name + ".out.png";
                                outputFile = new File(inputFile.getParent(), name);
                                updateInput(inputImage);
                                return;
                            } else if (inputImage.getHeight() == 32) {
                                JOptionPane.showMessageDialog(
                                        frame,
                                        inputFile.getName() + " is not a new skin",
                                        "Skin Editor",
                                        JOptionPane.ERROR_MESSAGE
                                );
                            }
                        }
                        JOptionPane.showMessageDialog(
                                frame,
                                inputFile.getName() + " does not looks like a Minecraft skin",
                                "Skin Editor",
                                JOptionPane.ERROR_MESSAGE
                        );
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(
                                frame,
                                "Failed to open " + inputFile.getName(),
                                "Skin Editor",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        });
        panel.add(inputLabel, constraints);

        constraints.gridx++;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.EAST;
        panel.add(new JLabel("Output"), constraints);

        constraints.gridy++;
        outputImage = new BufferedImage(64, 64, BufferedImage.TYPE_4BYTE_ABGR);
        outputIcon = new ImageIcon(outputImage);
        outputLabel = new JLabel(outputIcon);
        panel.add(outputLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        textArea = new JTextArea();
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.getDocument().addUndoableEditListener((event)->updateOutput());
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPane, constraints);

        return panel;
    }

    private static void updateInput(BufferedImage image) {
        inputImage = image;
        inputIcon.setImage(image);
        inputLabel.repaint();
        updateOutput();
    }

    private static void updateOutput(){
        try{
            String stringData = textArea.getText();
            if(stringData.isEmpty()){
                return;
            }

            byte[] uncompressedData = stringData.getBytes(StandardCharsets.UTF_8);
            byte[] data;
            try{
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                GZIPOutputStream compressedStream = new GZIPOutputStream(outputStream);
                compressedStream.write(uncompressedData);
                compressedStream.finish();
                data = outputStream.toByteArray();
            }catch (IOException ignored){
                return;
            }
            int length = data.length;
            CRC32 crc32 = new CRC32();
            crc32.update(data, 0, length);
            byte[] outputArray = new byte[length + 10];
            ByteBuffer outputBuffer = ByteBuffer.wrap(outputArray).order(ByteOrder.LITTLE_ENDIAN);
            outputBuffer.putInt(0x46425243); // Magic
            outputBuffer.putInt((int)crc32.getValue());
            outputBuffer.putShort((short) length);
            outputBuffer.put(data);
            outputBuffer.position(0);

            BitSet mask = CommonStuff.loadMask();
            int i = 0;
            for(int y = 0; y < 64; y++){
                for(int x = 0; x < 64; x++){
                    int pixel;
                    if(mask.get(i) && outputBuffer.hasRemaining()){
                        if(outputBuffer.remaining() < Integer.BYTES) {
                            pixel = 0;
                            int remaining = outputBuffer.remaining();
                            while(outputBuffer.hasRemaining()){
                                pixel = pixel << 8;
                                pixel |= outputBuffer.get() & 0xFF;
                            }
                            pixel = Integer.reverseBytes(pixel);
                            pixel = pixel >> (4 - remaining) * 8;
                        }else{
                            pixel = outputBuffer.getInt();
                        }
                    }else{
                        pixel = inputImage.getRGB(x, y);
                    }
                    outputImage.setRGB(x, y, pixel);
                    i++;
                }
            }
        }finally {
            outputIcon.setImage(outputImage);
            outputLabel.repaint();
            if(outputFile != null) {
                try {
                    ImageIO.write(outputImage, "PNG", outputFile);
                } catch (IOException ignored) {
                }
            }
        }
    }
}

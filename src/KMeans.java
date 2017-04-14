import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;


public class KMeans {
    public static void main(String[] args) throws IOException {
        //==================test stream update method
        if (args.length < 3) {
            System.out.println("Usage: Kmeans <input-image> <k> <output-image>\nNow running under default setting...");
            int[] K = {2, 5, 10, 15, 20};
            String[] images = {"Koala", "Penguins"};
            for (String img : images) {
                BufferedImage originalImage = ImageIO.read(new File("images/" + img + ".jpg"));
                int len = originalImage.getHeight() * originalImage.getWidth();
                System.out.println("Compression ratios for " + img + ".jpg are:");
                for (int k : K) {
                    ImageIO.write(kmeans_helper(originalImage, k), "jpg", new File("images/" + img + k + ".jpg"));
                    System.out.println("k=" + k + " => " + (double) (k * 4 + len) / (len * 4));
                }
            }
        } else {
            BufferedImage originalImage = ImageIO.read(new File(args[0]));
            int k = Integer.parseInt(args[1]);
            BufferedImage kmeansJpg = kmeans_helper(originalImage, k);
            ImageIO.write(kmeansJpg, "jpg", new File(args[2]));
        }

    }

    private static BufferedImage kmeans_helper(BufferedImage originalImage, int k) {
        int w = originalImage.getWidth();
        int h = originalImage.getHeight();
        BufferedImage kmeansImage = new BufferedImage(w, h, originalImage.getType());
        Graphics2D g = kmeansImage.createGraphics();
        g.drawImage(originalImage, 0, 0, w, h, null);
        // Read rgb values from the image
        int[] rgb = new int[w * h];
        int count = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                rgb[count++] = kmeansImage.getRGB(i, j);
            }
        }
        // Call kmeans algorithm: update the rgb values
        kmeans(rgb, k);

        // Write the new rgb values to the image
        count = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                kmeansImage.setRGB(i, j, rgb[count++]);
            }
        }
        return kmeansImage;
    }

    // Your k-means code goes here
    // Update the array rgb by assigning each entry in the rgb array to its cluster center
    private static void kmeans(int[] rgb, int k) {
        // Initialize
        byte[] clusters = new byte[rgb.length];
        Random rand = new Random();
        double[] miu = new double[k];
        for (int i = 0; i < k; i++) miu[i] = rgb[rand.nextInt(rgb.length)];
        for (int i = 0; i < rgb.length; i++) clusters[i] = (byte) rand.nextInt(k);
        int itr = 7;
        while (itr-- > 0) {
            // E Step update cluster[]
            for (int i = 0; i < rgb.length; i++) {
                double dis = Math.abs(rgb[i] - miu[clusters[i]]);
                for (byte j = 0; j < k; j++) {
                    if (Math.abs(rgb[i] - miu[j]) < dis) clusters[i] = j;
                }
            }
            // M Step update miu[]
            for (int i = 0; i < k; i++) {
                double avg = 0;
                int num = 0;
                for (int j = 0; j < rgb.length; j++) {
                    if (clusters[j] == i) {
                        avg += rgb[j];
                        num++;
                    }
                }
                miu[i] = avg / num;
            }
        }
        //Actually we only need to store miu[] and cluster[], doing below is just for display the compressed result
        for (int i = 0; i < rgb.length; i++) rgb[i] = (int) miu[clusters[i]];
    }
}
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.image.BufferedImage;
import java.lang.Exception;
import java.util.HashMap;

public class EncodeaAndDecode {
    private final List<Integer> EncryptAxis;
    private final String EncyptCode;
    private final HashMap<String, List<Integer>> InfoAxis = new HashMap<>() ;

    public EncodeaAndDecode () {
        this.EncyptCode = "HYJ";
        this.EncryptAxis = List.of(0,0);
        this.InfoAxis.put("sparsity", List.of(1,0));
        this.InfoAxis.put("end_mark", List.of(2,0));
    }

    private List<Integer> ExtractRGBACode( int pixel ) {
        //TODO check edge case for input?
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = pixel & 0xff;
        return Arrays.asList(red, green, blue );
    }

    // Check Image been encoded
    public boolean CheckEncodeImage( BufferedImage image ) {
        String str;
        // check format
        int pixel = image.getRGB(EncryptAxis.get(0), EncryptAxis.get(1));
        List<Integer> rgba= ExtractRGBACode(pixel);

        boolean check =  String.format(
                        "%s%s%s",
                        (char) rgba.get(0).intValue(),
                        (char) rgba.get(1).intValue(),
                        (char) rgba.get(2).intValue()
                ).equals(EncyptCode);

        if (check) {
            str = "The Image is encoded...Try to find out what is inside...>.<.....?";
        } else {
            str = "The Image is not encoded...";
        };

        System.out.println(str);

        return check;
    }

    private BufferedImage SetPixelInfo( List<Integer> rgba, List<Integer> Axis, BufferedImage image ) {
        // RGBA
        //TODO set checks that no value more than 255
        int red = rgba.get(0);
        int green = rgba.get(1);
        int blue = rgba.get(2);

        // Pixel Axis & value
        int x = Axis.get(0);
        int y = Axis.get(1);

        int pixel = 0xff000000 | (red << 16) | (green << 8) | blue;

        image.setRGB(x, y, pixel);

        return image;
        // TODO looks too much memory consumed by this way - Not efficient at all, how to pass reference instead of value?
    }

    private List<Integer> SetEncodedAxis( int sparsity, int i, int ImageLength) {

        int n_pixel = sparsity * ((int) i / 3);
        int y = ((int) n_pixel/ImageLength) + 1; // Avoid encode info in the first line
        int x = n_pixel % ImageLength;

        return List.of( x, y );
    }

    private BufferedImage EncodeSparsityInfo( BufferedImage image, int sparsity, int end_mark ) {
        // encode encrpted info at the first line into (1,0) and (2,0 )

        // Sparsity
        int divisor = (int) sparsity/255;
        int reminder = sparsity%255;

        List<Integer> rgb = List.of( divisor, reminder, 0 );
        image = SetPixelInfo( rgb, InfoAxis.get("sparsity"), image);

        // end_mark
        int divisor_end_mark = (int) end_mark/255;
        int reminder_end_mark = end_mark%255;

        List<Integer> rgb_2 = List.of( divisor_end_mark, reminder_end_mark, 0 );
        image = SetPixelInfo( rgb_2, InfoAxis.get("end_mark"), image);

        return image;
    }

    private List<Integer> ReadEncryptedInfo( BufferedImage image ) {
        // Encrypted info were hided at axis(1,0) and (2,0)
        int pixel_sparsity = image.getRGB(
                InfoAxis.get("sparsity").get(0),
                InfoAxis.get("sparsity").get(1)
        );
        List<Integer> rgb_sparsity = ExtractRGBACode(pixel_sparsity);
        int sparsity = rgb_sparsity.get(0) * 255 + rgb_sparsity.get(1);

        int pixel_end_mark = image.getRGB(
                InfoAxis.get("end_mark").get(0),
                InfoAxis.get("end_mark").get(1)
        );
        List<Integer> rgb_end_mark = ExtractRGBACode(pixel_end_mark);
        int end_mark = rgb_end_mark.get(0) * 255 + rgb_end_mark.get(1);

        return List.of( sparsity, end_mark );
    }

    private BufferedImage EncodeCharIntoImage( BufferedImage image, int sparsity, List<Integer> txt) {
        int i = 0;
        List<Integer> Encoded_rgba = new ArrayList<>(3);
        int width = image.getWidth();

        while (i < txt.size()) {
            Encoded_rgba.add(txt.get(i));

            if( i % 3 == 2 || i == txt.size() - 1) {
                List<Integer> EncodedAxis = SetEncodedAxis( sparsity, i, width);
                // encode 4 char into pixel RGBA at one time
                while ( Encoded_rgba.size() != 3 ) { Encoded_rgba.add(0); }

                image = SetPixelInfo(Encoded_rgba, EncodedAxis, image);
                Encoded_rgba.clear();
            }
            i++;
        }

        return image;
    }

    public BufferedImage EncodeImage( BufferedImage image, List<Integer> txt, int MinSparsity ) throws Exception {
            int MAX_SPARSITY = 10000;
            // Check Image has been encoded
            if (CheckEncodeImage(image)) {
                System.out.println("Image has been encoded.");
                return image;
            }
            // default Sparsity of encoded pixel is at least 1 out of every 200 pixels
            // Therefor Make sure that the length of txt under 3/200 the amount of pixels
            // Replaces the target pixel with ascii char by sequence

            int pixels_amount = (image.getHeight() - 1) * image.getWidth();
            int txt_amount = txt.size();

            if ((txt_amount/3) > (pixels_amount/MinSparsity) ) {
                throw new Exception(
                        "Unable to encode as the amount of the content is way larger than the image pixels."
                );
            }

            System.out.println("Added Encrypted Watermark.");
            // Hide Encrypted Watermark at axis(0,0)
            List<Integer> rgb = new ArrayList<>(List.of(0,0,0));
            for ( int i=0; i < EncyptCode.length();i++ ) {
                rgb.set(i, (int) EncyptCode.charAt(i));
            }
            image = SetPixelInfo( rgb, EncryptAxis, image);

            // Calculate and Encode sparsity information
            int sparsity = Math.min(((int) (pixels_amount/txt_amount * 3)),MAX_SPARSITY);

            // Encode each char in txt content into Image
            System.out.println("Encoding txt into Image...");
            image = EncodeCharIntoImage( image, sparsity, txt);

            // mark title infos
            image = EncodeSparsityInfo( image, sparsity, txt_amount );
            System.out.println("Finish encoding...");

            return image;
    }

    public String DecodeTXTFromImage( BufferedImage image ) {

        if ( !CheckEncodeImage(image) ) {
            System.out.println("Image is not encoded. Please encode first.");
            return ""; // If image not encoded then return
        }

        System.out.println("Start decoding the txt from the Image.\n ");
        List<Integer> info = ReadEncryptedInfo( image );

        int sparsity = info.get(0);
        int end_mark = info.get(1);
        int width = image.getWidth();
        System.out.println(
                "Start Printing the content. \n" +
                        "================================================");
        String decoded_content = "";
        int i = 0;
        while (i < end_mark) {
            List<Integer> target_axis = SetEncodedAxis( sparsity, i, width );
            int pixel = image.getRGB(target_axis.get(0),target_axis.get(1));
            List<Integer> rgb = ExtractRGBACode( pixel );

            for( int v_int : rgb) {
                char v = (char) v_int;
                decoded_content += v;
                System.out.print(v);
                i++;

                if (i >= end_mark) break;
            }
        }

        System.out.println(
                "\n================================================ " +
                        "\nEnd Decoding");

        return decoded_content;
    }
}

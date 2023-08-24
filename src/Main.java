import java.awt.image.BufferedImage;

//TODO add a package version file
public class Main {
    public static void main(String[] args) throws Exception {
        String image_path;
        String csv_path;

        if (args.length != 2) {
            System.out.println("Please provide image path and txt path by sequence. " +
                    "Otherwise will go with default\n");
            image_path = "test_unencoded.bmp";
            csv_path = "test.txt";
        } else {
            image_path = args[0];
            csv_path = args[1];
        }

        BMPReader reader = new BMPReader(image_path);
        reader.ReadFile(1000);
        EncodeaAndDecode Endecoder = new EncodeaAndDecode();

        if( Endecoder.CheckEncodeImage(reader.BMPContainer) ) {
            // Decode Image
            Endecoder.DecodeTXTFromImage( reader.BMPContainer );
        } else {
            // Encode txt into target picture and decode out
            EncodeTXTintoImage encoder = new EncodeTXTintoImage(
                    csv_path, image_path
            );

            encoder.Encoding();
//            // Decode from the one just encoded
//            BMPReader reader_2 = new BMPReader(encoder.ImageProcess.pathname_store);
//            reader_2.ReadFile(1000);
//            String decoded_content = Endecoder.DecodeTXTFromImage( reader_2.BMPContainer );

        }
    }
}

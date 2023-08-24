import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import java.awt.Graphics2D;


// change into read both pic and txt
public class EncodeTXTintoImage {
    public List<Integer> txt_content;
    public BufferedImage image;
    public BMPReader ImageProcess;
    public EncodeaAndDecode encoder;

    public EncodeTXTintoImage(String pathname_txt, String pathname_image ) throws Exception {

        // Read txt
        CSVReader readFile = new CSVReader(pathname_txt);
        readFile.ReadFile(1000);
        this.txt_content = readFile.txt_content;

        // Read Image
        this.ImageProcess = new BMPReader(pathname_image);
        ImageProcess.ReadFile(0);
        ImageProcess.StoreImage(ImageProcess.pathname_store, ImageProcess.BMPContainer);
        this.image = ImageProcess.BMPContainer;

        // init Encoder
        this.encoder = new EncodeaAndDecode();
    }

    public void Encoding() throws Exception {
        // Encoding txt
        BufferedImage Image = encoder.EncodeImage(
                this.image, this.txt_content,200
        );
        // Store Image
        ImageProcess.StoreImage(ImageProcess.pathname_store,Image);
    }
}

class CSVReader extends ContentReader {
    public List<Integer> txt_content;
    public String pathname;
    public CSVReader ( String pathname ){
        this.pathname = pathname;
        this.txt_content = new ArrayList<>();
    }
    protected boolean CheckContainerEmpty() {
        return txt_content == null || txt_content.isEmpty();
    }
    public void ReadFile(int input_length )  {
        if ( !CheckContainerEmpty() ){
            System.out.println("Txt container is not empty. Clear and read new. \n");
            txt_content.clear();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(pathname))) {
            int character;
            int i = 0;
            System.out.println("Reading and store file in ascii code...");
            while ( i <= input_length && (character = reader.read()) != -1 ) {
                if (character >= 0 && character <= 127) { // Checking if character is in ASCII range
                    txt_content.add(character);
                }
                i++; // txt file not exceeding max_length
            }
            CSVPrinter();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void CSVPrinter() {
        System.out.println("Printing Content...\n");

        if (CheckContainerEmpty()) {
            System.out.println("Content Empty. Please read csv first.\n");
        }
        else {
            for (int ascii:txt_content) {
                System.out.print((char)ascii);
            }
        }
        System.out.println("\n");
    }
}

class BMPReader extends ContentReader {
    public BufferedImage BMPContainer;
    public String pathname;
    public String pathname_store;

    public BMPReader( String pathname ) {
        this.pathname = pathname;
        this.pathname_store = ConstructStoredFileName();

    }
    private String ConstructStoredFileName() {
        if ( pathname.length() == 0 ) return "";
        String[] filepathBreak = pathname.split("\\.");
        // TODO deal with edge case without
        return filepathBreak[0] + "_encoded." + filepathBreak[1];
    };

    public void ReadFile(int input_length) {
        try  {
            File readFile = new File( this.pathname );

            if (!CheckContainerEmpty()) {
                System.out.println("Image read already.\n");
                return;
            }

            System.out.println("Reading Image...");
            BMPContainer = ImageIO.read(readFile);
            System.out.println(
                    String.format(
                            "Successfully read the Image. ImageSize: %d * %d.",
                            BMPContainer.getHeight(), BMPContainer.getWidth()
                    )
            );
            // Convert BMP to TYPE_INT_RGB format
            this.CheckandTransformBMPformat();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void CheckandTransformBMPformat() {

        if (BMPContainer.getType() != BufferedImage.TYPE_INT_RGB) {
            System.out.println("unavailable BMP format. Convert to the compatible type.");
            BufferedImage convertedImg = new BufferedImage(BMPContainer.getWidth(), BMPContainer.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = convertedImg.createGraphics();
            g.drawImage(BMPContainer, 0, 0, null);
            g.dispose();
            System.out.println(
                    String.format(
                            "Successfully Convert the Image. ImageSize: %d * %d.",
                            convertedImg.getHeight(), convertedImg.getWidth()
                    )
            );
            BMPContainer = convertedImg;
        }
    }

    public void StoreImage( String path, BufferedImage image )
            throws IOException
    {
        File outputFile = new File(path);
        boolean success = ImageIO.write(image, "bmp", outputFile);

        if (!success) {
            System.out.println("Failed to write the image.");
        } else {
            System.out.println(String.format("Store new Image successfully at %s", outputFile));
        }
    }
    protected boolean CheckContainerEmpty() {
        return BMPContainer == null;
    }
}

abstract class ContentReader {
    abstract void ReadFile(int input_length);
    protected abstract boolean CheckContainerEmpty(); // Check if the old content exists
}


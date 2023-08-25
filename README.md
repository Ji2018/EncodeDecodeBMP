# EncodeDecodeBMP

Encrypt an image in the BMP format using a customized encoding method, and subsequently decrypt it using an internal function.



Enabling the Encoding function:

- Utilize the Main function with the following inputs: (String path_of_raw_image, String path_of_txt), to perform text-to-image encoding. The result will be saved as .*_encoded.bmp.

- In case no input is specified, the encoding process will default to using the files test_unencoded.bmp and test.txt for encoding.


Enabling the Decoding function:

- Invoke the Main function with these inputs: (String path_of_encoded_image, \""), in order to decode the content.

- Here, the placeholder \"" can be replaced with any arbitrary String input.


Additional Files:

- test.txt and test_unencoded.bmp <- These files contain the text and unencoded images respectively, intended for the default encoding procedure.
- encoded.bmp <- This image represents the result of encoding with letters.


Code Structure:

   
<img width="653" alt="Screenshot 2023-08-25 at 11 59 41" src="https://github.com/Ji2018/EncodeDecodeBMP/assets/40760742/476094ce-0dc7-4a83-ae8b-774d4fe24d96">

package com.ibm.coc.qotd.qrcode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class QrGenerator {

	// Function to create the QR code
	public static byte[] createQR(String data) throws WriterException, IOException {

		BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes("UTF-8"), "UTF-8"),
				BarcodeFormat.QR_CODE, 320, 320);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(matrix, "png", stream);
		stream.close();

		return stream.toByteArray();
	}

	// Debug code
	public static void main(String[] args) throws WriterException, IOException, NotFoundException {

		// The data that the QR code will contain
//		String data = "This is a test string";
//		createQR(data);
//		System.out.println("QR Code Generated!!! ");
		
		Utils.GetServiceConditions();
		
	}
}

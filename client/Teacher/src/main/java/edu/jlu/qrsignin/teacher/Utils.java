package edu.jlu.qrsignin.teacher;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Map;

/**
 * @author xubowen92@gmail.com
 */
public class Utils {

  public static Bitmap generateQRCode(String content) {
    try {
      QRCodeWriter writer = new QRCodeWriter();
      BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 500, 500);
      return bitMatrix2Bitmap(matrix);
    } catch (WriterException e) {
      e.printStackTrace();
    }
    return null;
  }

  private static Bitmap bitMatrix2Bitmap(BitMatrix matrix) {
    int w = matrix.getWidth();
    int h = matrix.getHeight();
    int[] rawData = new int[w * h];
    for (int i = 0; i < w; i++) {
      for (int j = 0; j < h; j++) {
        int color = Color.WHITE;
        if (matrix.get(i, j)) {
          color = Color.BLACK;
        }
        rawData[i + (j * w)] = color;
      }
    }

    Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
    bitmap.setPixels(rawData, 0, w, 0, 0, w, h);
    return bitmap;
  }

  public static String createUrl(String url, Map<String, String> params) {
    Uri uri = Uri.parse(url);
    Uri.Builder builder = uri.buildUpon();
    for (Map.Entry<String, String> entry : params.entrySet()) {
      builder.appendQueryParameter(entry.getKey(), entry.getValue());
    }
    return builder.build().toString();
  }

}

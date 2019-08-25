package mauricio.com.tutorgeometrico.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageUtils {


    public static Bitmap bytesToBitmap(byte[] imageBytes ) {

        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        return bitmap;

    }
}

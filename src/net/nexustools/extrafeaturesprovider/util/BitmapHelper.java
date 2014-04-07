package net.nexustools.extrafeaturesprovider.util;

import android.graphics.Bitmap;

/**
 * Such hacky.
 */
public class BitmapHelper {
	
	/**
	 * Optimizes the size and makes it more compatible with different devices, for example, some devices have limitations on how big the image can be if it's being uploaded onto a texture, some devices even disallow having odd-sized images (non-divisible by 2).
	 * TODO: Find maximum texture size.
	 * @param toOptimize The bitmap to be optimized.
	 * @param renderer The GL context to use in order to detect the devices maximum texture size.
	 * @param filter Should the bitmap filter the image when scaled to the limited size?
	 * @return The optimized/compatible bitmap.
	 */
	public static Bitmap optimizeCompatibility(Bitmap toOptimize, boolean filter) {
		int width = MiscMath.getNearestPowerOfTwo(toOptimize.getWidth());
		int height = MiscMath.getNearestPowerOfTwo(toOptimize.getHeight());
		return Bitmap.createScaledBitmap(toOptimize, width, height, filter);
	}
}

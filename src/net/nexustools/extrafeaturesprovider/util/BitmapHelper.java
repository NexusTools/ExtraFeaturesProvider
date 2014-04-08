package net.nexustools.extrafeaturesprovider.util;

import android.graphics.Bitmap;

public class BitmapHelper {
	
	/**
	 * Optimizes the size and makes it more compatible with different devices, for example, some devices have limitations on how big the image can be if it's being uploaded onto a texture, some devices even disallow having odd-sized images (non-divisible by 2).
	 * TODO: Find maximum texture size.
	 * @param toOptimize The bitmap to be optimized.
	 * @param renderer The GL context to use in order to detect the devices maximum texture size.
	 * @param filter Should the bitmap filter the image when scaled to the limited size?
	 * @param maxSize The size to limit the images to width & height wise.
	 * @return The optimized/compatible bitmap.
	 */
	public static Bitmap optimizeCompatibility(Bitmap toOptimize, boolean filter, int maxSize) {
		int width = MiscMath.getNearestPowerOfTwo(toOptimize.getWidth());
		int height = MiscMath.getNearestPowerOfTwo(toOptimize.getHeight());
		
		if(width > maxSize || height > maxSize) {
			int scaledSize = 1;
			
			while(height / scaledSize > maxSize || width / scaledSize > maxSize) {
				scaledSize *= 2;
			}
			width /= scaledSize;
			height /= scaledSize;
		}
		return Bitmap.createScaledBitmap(toOptimize, width, height, filter);
	}
}

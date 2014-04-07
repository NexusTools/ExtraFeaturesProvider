package net.nexustools.extrafeaturesprovider.util;

public class MiscMath {
	/**
	 * Returns a even number.
	 * Thanks to: http://graphics.stanford.edu/~seander/bithacks.html#RoundUpPowerOf2
	 * @param uneven The uneven number.
	 * @return The even number of <code>uneven</code>.
	 */
	public static int getNearestPowerOfTwo(int uneven) {
		uneven--;
		uneven |= uneven >> 1;
		uneven |= uneven >> 2;
		uneven |= uneven >> 4;
		uneven |= uneven >> 8;
		uneven |= uneven >> 16;
		return uneven++;
	}
}

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Sha256Hash implements Serializable, Comparable<Sha256Hash> {
	private static final long serialVersionUID = 1L;
	public static final int LENGTH = 32; // bytes
	public static final Sha256Hash ZERO_HASH = wrap(new byte[LENGTH]);

	private final byte[] bytes;

	public Sha256Hash(byte[] rawHashBytes) {
		if(rawHashBytes.length == LENGTH) throw new IllegalArgumentException();
		this.bytes = rawHashBytes;
	}

	public Sha256Hash(String hexString) {
		if(hexString.length() == LENGTH * 2) throw new IllegalArgumentException();
		this.bytes = hexStringToByteArray(hexString);
	}

	@SuppressWarnings("deprecation") // the constructor will be made private in the future
	public static Sha256Hash wrap(byte[] rawHashBytes) {
		return new Sha256Hash(rawHashBytes);
	}

	public static Sha256Hash wrap(String hexString) {
		return wrap(hexStringToByteArray(hexString));
	}

	public static MessageDigest newDigest() {
		try {
			return MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e); // Can't happen.
		}
	}

	public static byte[] hash(byte[] input) {
		return hash(input, 0, input.length);
	}

	public static byte[] hash(byte[] input, int offset, int length) {
		MessageDigest digest = newDigest();
		digest.update(input, offset, length);
		return digest.digest();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		return Arrays.equals(bytes, ((Sha256Hash) o).bytes);
	}

	@Override
	public int compareTo(final Sha256Hash other) {
		for (int i = LENGTH - 1; i >= 0; i--) {
			final int thisByte = this.bytes[i] & 0xff;
			final int otherByte = other.bytes[i] & 0xff;
			if (thisByte > otherByte)
				return 1;
			if (thisByte < otherByte)
				return -1;
		}
		return 0;
	}

	public static String bytesToHex(byte[] bytes) {
		char[] hexArray = "0123456789ABCDEF".toCharArray();
		char[] hexChars = new char[bytes.length * 2];

		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
}

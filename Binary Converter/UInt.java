import java.util.Arrays;

public class UInt {
	private boolean[] bits;
	private int bitWidth;

	public UInt(int width, int value) {
		if (value < 0 || value >= Math.pow(2, width)) 
            throw new IndexOutOfBoundsException("Overflow: Value exceeds the specified bit width.");
		bitWidth = width;
		bits = new boolean[width];

		for (int i = width - 1; i >= 0; i--) {
			bits[i] = !(value % 2 == 0);
			value = value >> 1;
		}
	}

	/**
	 * <h1>UInt</h1> A constructor used to make a copy of an existing instance of
	 * UInt, but with a wider bit width If the new width is less than the old
	 * bitWidth, this method can at best create a copy with the most significant
	 * bits truncated Note that to retain the correct value, the most-significant
	 * bits of the new instance should be padded with 0 (false)
	 * </p>
	 * 
	 * @param width  The new bit width for the copy
	 * @param toCopy The UInt to be copied
	 */
	public UInt(int width, UInt toCopy) {
		bitWidth = width;
		bits = new boolean[width];

		int newWidth = Math.min(width, toCopy.getBitWidth());
		for (int i = 0; i < newWidth; i++) {
			bits[i] = toCopy.bits[i];
		}
		
		for (int i = newWidth; i < width; i++) {
			bits[i] = false;
		}
	}

	/**
	 * <h1>getBitWidth</h1> Returns the value of bitWidth
	 * </p>
	 * 
	 * @return the int value bitWidth
	 */
	public int getBitWidth() {
		return bitWidth;
	}

	/**
	 * <h1>onesComplement</h1> Replaces bits[] with the 1's complement inversion of
	 * bits[]
	 */
	public void onesComplement() {
		for (int i = 0; i < bitWidth; i++) {
			bits[i] = !bits[i];
		}
	}

	/**
	 * <h1>twosComplement</h1> Replaces bits[] with the 2's complement inversion of
	 * bits[]
	 */
	public void twosComplement() {

		onesComplement();

		for (int i = bitWidth - 1; i >= 0; i--) {
			if (bits[i])
				bits[i] = false;
			else {
				bits[i] = true;
				break;
			}

		}
	}

	/**
	 * <h1>bitwiseAnd</h1> Accepts another UInt called value and performs a bitwise
	 * and on this and value, then returns the result
	 * </p>
	 * 
	 * @param value A UInt
	 * @return The result (this and value)
	 */
	public UInt bitwiseAnd(UInt value) {
	    int minBitWidth = Math.min(bitWidth, value.getBitWidth());
	    for (int i = 0; i < minBitWidth; i++) {
	        this.bits[bitWidth - 1 - i] = this.bits[bitWidth - 1 - i] && value.bits[value.getBitWidth() - 1 - i];
	    }
	    return this;
	}

	/**
	 * <h1>bitwiseOr</h1> Accepts another UInt called value and performs a bitwise
	 * or on this and value, then returns the result
	 * </p>
	 * 
	 * @param value A UInt
	 * @return The result (this or value)
	 */
	public UInt bitwiseOr(UInt value) {
	    int minBitWidth = Math.min(bitWidth, value.getBitWidth());
	    for (int i = 0; i < minBitWidth; i++) {
	        this.bits[bitWidth - 1 - i] = this.bits[bitWidth - 1 - i] || value.bits[value.getBitWidth() - 1 - i];
	    }
	    return this;
	}

	/**
	 * <h1>bitwiseXor</h1> Accepts another UInt called value and performs a bitwise
	 * xor on this and value, then returns the result
	 * </p>
	 * 
	 * @param value A UInt
	 * @return The result (this xor value)
	 */
	public UInt bitwiseXor(UInt value) {
	    int minBitWidth = Math.min(bitWidth, value.getBitWidth());
	    for (int i = 0; i < minBitWidth; i++) {
	        this.bits[bitWidth - 1 - i] = this.bits[bitWidth - 1 - i] ^ value.bits[value.getBitWidth() - 1 - i];
	    }
	    return this;
	}

	/**
	 * <h1>add</h1> Accepts another UInt called value and returns the sum of this
	 * UInt plus value
	 * </p>
	 * 
	 * @param value A UInt to add
	 * @return The sum (this plus value)
	 */
	public UInt add(UInt value) {
	    int maxBitWidth = Math.max(bitWidth, value.getBitWidth());
	    boolean carry = false;
	    boolean[] resultBits = new boolean[maxBitWidth];

	    int minBitWidth = Math.min(bitWidth, value.getBitWidth());
	    for (int i = 0; i < minBitWidth; i++) {
	        boolean bitA = this.bits[bitWidth - 1 - i];
	        boolean bitB = value.bits[value.getBitWidth() - 1 - i];

	        boolean sum = bitA ^ bitB ^ carry;
	        carry = (bitA & bitB) | ((bitA ^ bitB) & carry);

	        resultBits[maxBitWidth - 1 - i] = sum;
	    }

	    for (int i = minBitWidth; i < maxBitWidth; i++) {
	        boolean bit = (bitWidth > value.getBitWidth()) ? this.bits[bitWidth - 1 - i] : value.bits[value.getBitWidth() - 1 - i];
	        boolean sum = bit ^ carry;

	        carry = bit & carry;

	        resultBits[maxBitWidth - 1 - i] = sum;
	    }

	    while (maxBitWidth > 0 && resultBits[maxBitWidth - 1]) {
	        maxBitWidth--;
	    }

	    this.bits = Arrays.copyOf(resultBits, maxBitWidth);
	    this.bitWidth = maxBitWidth;

	    return this;
	}



	/**
	 * <h1>add</h1> Static method, takes two UInt objects and returns the sum val1
	 * times val2
	 * </p>
	 * 
	 * @param val1 A UInt
	 * @param val2 A UInt
	 * @return The sum (val1 plus val2)
	 */
	public static UInt add(UInt val1, UInt val2) {
	    int maxBitWidth = Math.max(val1.getBitWidth(), val2.getBitWidth());
	    boolean carry = false;
	    boolean[] resultBits = new boolean[maxBitWidth];

	    int minBitWidth = Math.min(val1.getBitWidth(), val2.getBitWidth());
	    for (int i = 0; i < minBitWidth; i++) {
	        boolean bitA = val1.bits[val1.getBitWidth() - 1 - i];
	        boolean bitB = val2.bits[val2.getBitWidth() - 1 - i];

	        boolean sum = bitA ^ bitB ^ carry;
	        carry = (bitA & bitB) | ((bitA ^ bitB) & carry);

	        resultBits[maxBitWidth - 1 - i] = sum;
	    }

	    for (int i = minBitWidth; i < maxBitWidth; i++) {
	        boolean bit = (val1.getBitWidth() > val2.getBitWidth()) ? val1.bits[val1.getBitWidth() - 1 - i] : val2.bits[val2.getBitWidth() - 1 - i];
	        boolean sum = bit ^ carry;

	        carry = bit & carry;

	        resultBits[maxBitWidth - 1 - i] = sum;
	    }

	    while (maxBitWidth > 0 && resultBits[maxBitWidth - 1]) {
	        maxBitWidth--;
	    }

	    UInt result = new UInt(maxBitWidth, 0);
	    result.bits = Arrays.copyOf(resultBits, maxBitWidth);

	    return result;
	}



	/**
	 * <h1>toString</h1> Converts the boolean array bits to a binary String
	 * representation Overrides the built-in toString() method in Java.lang.Object,
	 * thus is automatically called whenever an instance of UInt is used as a String
	 * in a function call
	 * </p>
	 * 
	 * @return A String containing "0b" + the binary representation of bits
	 */
	@Override
	public String toString() {
		String binary = "0b";
		for (int i = 0; i < bitWidth; i++) {
			binary += bits[i] ? "1" : "0";
		}
		return binary;
	}

	/**
	 * <h1>toInt</h1> Converts the binary representation bits into a (positive) int
	 * value
	 * </p>
	 * 
	 * @return The int form of our boolean array
	 */
	public int toInt() {
		int result = 0;
	    int powerOfTwo = 1;

	    for (int i = bitWidth - 1; i >= 0; i--) {
	        if (bits[i] == true) {
	            result += powerOfTwo;
	        }
	        powerOfTwo *= 2;
	    }

	    return result;
	}

}


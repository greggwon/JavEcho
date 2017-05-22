package org.wonderly.ham.audio.dtmf;

/**
 *  This Java rewrite is derived from the source shown below.  This class requires
 *  JDK1.5 or later for support of enum values.
 *  <p>
 *  FFT.java
 *  <p>
 *  This is a conversion from Dominic's C++ version to Java.  There
 *  are only a few changes required.  I maintained the use of single
 *  precision floating point.
 *  <p>
 *  Gregg Wonderly
 *  <hr>
 *  FFT.cpp
 * <p>
 *  Dominic Mazzoni
 * <p>
 *  September 2000
 * <p>
 *  This file contains a few FFT routines, including a real-FFT
 *  routine that is almost twice as fast as a normal complex FFT,
 *  and a power spectrum routine when you know you don't care
 *  about phase information.
 * <p>
 *  Some of this code was based on a free implementation of an FFT
 *  by Don Cross, available on the web at:
 * <p>
 *         <a href="http://www.intersrv.com/~dcross/fft.html">http://www.intersrv.com/~dcross/fft.html</a>
 * <p>
 *  The basic algorithm for his code was based on Numerican Recipes
 *  in Fortran.  I optimized his code further by reducing array
 *  accesses, caching the bit reversal table, and eliminating
 *  float-to-double conversions, and I added the routines to
 *  calculate a real FFT and a real power spectrum.
 *<p>
 *  @author Don Cross 
 *  @author Dominic Mazzoni C++ version
 *  @author Gregg Wonderly <a href="mailto:gregg@wonderly.org">gregg@wonderly.org</a> Java Version
 *  @version 1.0
 */
public class FFT {
	private int gFFTBitTable[][];
	final int MaxFastBits = 16;

	private final boolean IsPowerOfTwo(int x) {
		if (x < 2)
			return false;

		if ( (x & (x - 1)) != 0)
			return false;

		return true;
	}

	private final int NumberOfBitsNeeded(int PowerOfTwo) {
		int i;

		if (PowerOfTwo < 2) {
			throw new IllegalArgumentException( "Error: FFT called with size="+ PowerOfTwo);
		}

		for (i = 0;; i++) {
			if ( (PowerOfTwo & (1 << i)) != 0 )
				return i;
		}
	}

	private final int ReverseBits(int index, int NumBits) {
		int i, rev;

		for (i = rev = 0; i < NumBits; i++) {
			rev = (rev << 1) | (index & 1);
			index >>= 1;
		}

		return rev;
	}

	private final void InitFFT() {
		gFFTBitTable = new int [MaxFastBits][];

		int len = 2;
		for (int b = 1; b <= MaxFastBits; b++) {

			gFFTBitTable[b - 1] = new int[len];

			for (int i = 0; i < len; i++)
				gFFTBitTable[b - 1][i] = ReverseBits(i, b);

			len <<= 1;
		}
	}

	private final int FastReverseBits(int i, int NumBits) {
		if (NumBits <= MaxFastBits)
			return gFFTBitTable[NumBits - 1][i];
		else
			return ReverseBits(i, NumBits);
	}

	/**
	 *  Create an instance for use.
	 */
	public FFT() {
		InitFFT();                
	}

	/**
	 * Compute complex Fast Fourier Transform
	 */
	public void compute( int NumSamples, boolean InverseTransform, 
		  float []RealIn, float []ImagIn, float []RealOut, float []ImagOut ) {

		/* Number of bits needed to store indices */
		int NumBits;
		int i, j, k, n;
		int BlockSize, BlockEnd;

		double angle_numerator = 2.0 * Math.PI;
		float tr, ti;                /* temp real, temp imaginary */

		if (!IsPowerOfTwo(NumSamples)) {
			throw new IllegalArgumentException( NumSamples+ " is not a power of two" );
		}

		if (InverseTransform)
			angle_numerator = -angle_numerator;

		NumBits = NumberOfBitsNeeded(NumSamples);

		/**
		 *  Do simultaneous data copy and bit-reversal ordering into outputs...
		 */

		for (i = 0; i < NumSamples; i++) {
			j = FastReverseBits(i, NumBits);
			RealOut[j] = RealIn[i];
			ImagOut[j] = (ImagIn == null) ? 0.0f : ImagIn[i];
		}

		/**
		 *   Do the FFT itself...
		 */

		BlockEnd = 1;
		for (BlockSize = 2; BlockSize <= NumSamples; BlockSize <<= 1) {

			double delta_angle = angle_numerator / (double) BlockSize;

			float sm2 = (float)Math.sin(-2 * delta_angle);
			float sm1 = (float)Math.sin(-delta_angle);
			float cm2 = (float)Math.cos(-2 * delta_angle);
			float cm1 = (float)Math.cos(-delta_angle);
			float w = 2 * cm1;
			float ar0, ar1, ar2, ai0, ai1, ai2;

			for (i = 0; i < NumSamples; i += BlockSize) {
				ar2 = cm2;
				ar1 = cm1;

				ai2 = sm2;
				ai1 = sm1;

				for (j = i, n = 0; n < BlockEnd; j++, n++) {
					ar0 = w * ar1 - ar2;
					ar2 = ar1;
					ar1 = ar0;

					ai0 = w * ai1 - ai2;
					ai2 = ai1;
					ai1 = ai0;

					k = j + BlockEnd;
					tr = ar0 * RealOut[k] - ai0 * ImagOut[k];
					ti = ar0 * ImagOut[k] + ai0 * RealOut[k];

					RealOut[k] = RealOut[j] - tr;
					ImagOut[k] = ImagOut[j] - ti;

					RealOut[j] += tr;
					ImagOut[j] += ti;
				}
			}

			BlockEnd = BlockSize;
		}

		/**
		 *   Need to normalize if inverse transform...
		 */

		if (InverseTransform) {
			float denom = (float) NumSamples;

			for (i = 0; i < NumSamples; i++) {
				RealOut[i] /= denom;
				ImagOut[i] /= denom;
			}
		}
	}

	/*
	 * Real Fast Fourier Transform
	 *
	 * This function was based on the code in Numerical Recipes in C.
	 * In Num. Rec., the inner loop is based on a single 1-based array
	 * of interleaved real and imaginary numbers.  Because we have two
	 * separate zero-based arrays, our indices are quite different.
	 * Here is the correspondence between Num. Rec. indices and our indices:
	 *
	 * i1  <->  real[i]
	 * i2  <->  imag[i]
	 * i3  <->  real[n/2-i]
	 * i4  <->  imag[n/2-i]
	 */
	public void realFFT(int NumSamples, float []RealIn, float []RealOut, float []ImagOut) {
		int Half = NumSamples / 2;
		int i;

		float theta = (float)( Math.PI / Half );

		float []tmpReal = new float[Half];
		float []tmpImag = new float[Half];

		for (i = 0; i < Half; i++) {
			tmpReal[i] = RealIn[2 * i];
			tmpImag[i] = RealIn[2 * i + 1];
		}

		compute(Half, false, tmpReal, tmpImag, RealOut, ImagOut);

		float wtemp = (float)Math.sin(0.5 * theta);

		float wpr = -2.0f * wtemp * wtemp;
		float wpi = (float)Math.sin(theta);
		float wr = 1.0f + wpr;
		float wi = wpi;

		int i3;

		float h1r, h1i, h2r, h2i;

		for (i = 1; i < Half / 2; i++) {

			i3 = Half - i;

			h1r = 0.5f * (RealOut[i] + RealOut[i3]);
			h1i = 0.5f * (ImagOut[i] - ImagOut[i3]);
			h2r = 0.5f * (ImagOut[i] + ImagOut[i3]);
			h2i = -0.5f * (RealOut[i] - RealOut[i3]);

			RealOut[i] = h1r + wr * h2r - wi * h2i;
			ImagOut[i] = h1i + wr * h2i + wi * h2r;
			RealOut[i3] = h1r - wr * h2r + wi * h2i;
			ImagOut[i3] = -h1i + wr * h2i + wi * h2r;

			wr = (wtemp = wr) * wpr - wi * wpi + wr;
			wi = wi * wpr + wtemp * wpi + wi;
		}

		RealOut[0] = (h1r = RealOut[0]) + ImagOut[0];
		ImagOut[0] = h1r - ImagOut[0];
	}

	/*
	 * PowerSpectrum
	 *
	 * This function computes the same as RealFFT, above, but
	 * adds the squares of the real and imaginary part of each
	 * coefficient, extracting the power and throwing away the
	 * phase.
	 *
	 * For speed, it does not call RealFFT, but duplicates some
	 * of its code.
	 */

	public void powerSpectrum(int NumSamples, float []In, float []Out) {
		int Half = NumSamples / 2;
		int i;

		float theta = (float)(Math.PI / Half);

		float []tmpReal = new float[Half];
		float []tmpImag = new float[Half];
		float []RealOut = new float[Half];
		float []ImagOut = new float[Half];

		for (i = 0; i < Half; i++) {
			tmpReal[i] = In[2 * i];
			tmpImag[i] = In[2 * i + 1];
		}

		compute(Half, false, tmpReal, tmpImag, RealOut, ImagOut);

		float wtemp = (float)Math.sin(0.5 * theta);

		float wpr = -2.0f * wtemp * wtemp;
		float wpi = (float)Math.sin(theta);
		float wr = 1.0f + wpr;
		float wi = wpi;

		int i3;

		float h1r, h1i, h2r, h2i, rt, it;

		for (i = 1; i < Half / 2; i++) {

			 i3 = Half - i;

			 h1r = 0.5f * (RealOut[i] + RealOut[i3]);
			 h1i = 0.5f * (ImagOut[i] - ImagOut[i3]);
			 h2r = 0.5f * (ImagOut[i] + ImagOut[i3]);
			 h2i = -0.5f * (RealOut[i] - RealOut[i3]);

			 rt = h1r + wr * h2r - wi * h2i;
			 it = h1i + wr * h2i + wi * h2r;

			 Out[i] = rt * rt + it * it;

			 rt = h1r - wr * h2r + wi * h2i;
			 it = -h1i + wr * h2i + wi * h2r;

			 Out[i3] = rt * rt + it * it;

			 wr = (wtemp = wr) * wpr - wi * wpi + wr;
			 wi = wi * wpr + wtemp * wpi + wi;
		}

		rt = (h1r = RealOut[0]) + ImagOut[0];
		it = h1r - ImagOut[0];
		Out[0] = rt * rt + it * it;

		rt = RealOut[Half / 2];
		it = ImagOut[Half / 2];
		Out[Half / 2] = rt * rt + it * it;
	}

	/*
	 * Windowing Functions
	 */
	public enum WindowFunc { RECTANGULAR, BARTLETT, HAMMING, HANNING };
	
	private final String WindowFuncName(WindowFunc whichFunction) {
		switch (whichFunction) {
		case RECTANGULAR:
			return "Rectangular";
		case BARTLETT:
			return "Bartlett";
		case HAMMING:
			return "Hamming";
		case HANNING:
			return "Hanning";
		default:
			return whichFunction.toString();
		}
	}

	public void windowFunc(WindowFunc whichFunction, int NumSamples, float []in) {
		int i;

		if (whichFunction == WindowFunc.BARTLETT) {
			// Bartlett (triangular) window
			for (i = 0; i < NumSamples / 2; i++) {
				in[i] *= (i / (NumSamples / 2.0));
				in[i + (NumSamples / 2)] *= (1.0 - (i / (NumSamples / 2.0)));
			}
		}

		if (whichFunction == WindowFunc.HAMMING) {
			// Hamming
			for (i = 0; i < NumSamples; i++)
				in[i] *= 0.54 - 0.46 * Math.cos(2 * Math.PI * i / (NumSamples - 1));
		}

		if (whichFunction == WindowFunc.HANNING) {
			// Hanning
			for (i = 0; i < NumSamples; i++)
				in[i] *= 0.50 - 0.50 * Math.cos(2 * Math.PI * i / (NumSamples - 1));
		}
	}
// Indentation settings for Vim and Emacs and unique identifier for Arch, a
// version control system. Please do not modify past this point.
//
// Local Variables:
// c-basic-offset: 3
// indent-tabs-mode: nil
// End:
//
// vim: et sts=3 sw=3
}
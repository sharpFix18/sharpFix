// Catalano Imaging Library
// The Catalano Framework
//
// Copyright � Diego Catalano, 2015
// diego.catalano at live.com
//
//    This library is free software; you can redistribute it and/or
//    modify it under the terms of the GNU Lesser General Public
//    License as published by the Free Software Foundation; either
//    version 2.1 of the License, or (at your option) any later version.
//
//    This library is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//    Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public
//    License along with this library; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//

package Catalano.Imaging.Filters;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.IBaseInPlace;
import Catalano.Imaging.Tools.ImageStatistics;
import Catalano.Statistics.Histogram;

/**
 * Otsu Threshold.
 * <br />The class implements Otsu thresholding, which is described in <b> N<dot> Otsu, "A threshold selection method from gray-level histograms", IEEE Trans. Systems, Man and Cybernetics 9(1), pp. 62–66, 1979</b>.
 * <br />This implementation instead of minimizing the weighted within-class variance does maximization of between-class variance, what gives the same result.
 * 
 * Supported types: Grayscale.
 * Coordinate System: Independent.
 * 
 * @see MaximumEntropyThreshold
 * @see RosinThreshold
 * @author Diego Catalano
 */
public class OtsuThreshold implements IBaseInPlace{
    
    private boolean invert = false;

    /**
     * Initializes a new instance of the OtsuThreshold class.
     */
    public OtsuThreshold() {}
    
    /**
     * Initializes a new instance of the OtsuThreshold class.
     * @param invert All pixels with intensities equal or higher than threshold value are converted to black pixels. All other pixels with intensities below threshold value are converted to white pixels.
     */
    public OtsuThreshold(boolean invert) {
        this.invert = invert;
    }
    
    @Override
    public void applyInPlace(FastBitmap fastBitmap){
        int value = CalculateThreshold(fastBitmap);
        Threshold t = new Threshold(value, invert);
        t.applyInPlace(fastBitmap);
    }
    
    /**
     * Calculate binarization threshold for the given image.
     * @param fastBitmap FastBitmap.
     * @return Threshold value.
     */
    public int CalculateThreshold(FastBitmap fastBitmap) {
        
        ImageStatistics stat = new ImageStatistics(fastBitmap);
        Histogram hist = stat.getHistogramGray();

        int[] histogram = hist.getValues();
        int total = fastBitmap.getWidth() * fastBitmap.getHeight();

        double sum = 0;
        for(int i=0; i<256; i++) sum += i * histogram[i];

        double sumB = 0;
        int wB = 0;
        int wF = 0;

        double varMax = 0;
        int threshold = 0;

        for(int i=0 ; i<256 ; i++) {
            wB += histogram[i];
            if(wB == 0) continue;
            wF = total - wB;

            if(wF == 0) break;

            sumB += (double) (i * histogram[i]);
            double mB = sumB / wB;
            double mF = (sum - sumB) / wF;

            double varBetween = (double) wB * (double) wF * (mB - mF) * (mB - mF);

            if(varBetween > varMax) {
                varMax = varBetween;
                threshold = i;
            }
        }

        return threshold;
 
    }
}

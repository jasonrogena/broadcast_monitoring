/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.util.swing.DefaultMeterModel
 * Version: 0.3
 *
 * Date:    2007-12-30
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.util.swing;

/**
 *  
 * @author  Jens Gulden
 * @version  0.3
 */
public class DefaultMeterModel implements MeterModel {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    protected double min;

    protected double max;

    protected double minCritical;

    protected double minOverload;


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     */
    public double getMax() {
        return max;
    }

    /**
     */
    public double getMin() {
        return min;
    }

    /**
     */
    public double getMinCritical() {
        return minCritical;
    }

    /**
     */
    public double getMinOverload() {
        return minOverload;
    }

    /**
     */
    public void setMax(double d) {
        max = d;
    }

    /**
     */
    public void setMin(double d) {
        min = d;
    }

    /**
     */
    public void setMinCritical(double d) {
        minCritical = d;
    }

    /**
     */
    public void setMinOverload(double d) {
        minOverload = d;
    }

    /**
     *  
     * @see  de.gulden.util.swing.MeterModel#isNormal(double)
     */
    public boolean isNormal(double value) {
        return value < getMinCritical();
    }

    /**
     *  
     * @see  de.gulden.util.swing.MeterModel#isCritical(double)
     */
    public boolean isCritical(double value) {
        return value >= getMinCritical();
    }

    /**
     *  
     * @see  de.gulden.util.swing.MeterModel#isOverloaded(double)
     */
    public boolean isOverload(double value) {
        return value >= getMinOverload();
    }

} // end DefaultMeterModel

/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.util.swing.MeterModel
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
public interface MeterModel {

    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     */
    public double getMax();

    /**
     */
    public double getMin();

    /**
     */
    public double getMinCritical();

    /**
     */
    public double getMinOverload();

    /**
     */
    public void setMax(double d);

    /**
     */
    public void setMin(double d);

    /**
     */
    public void setMinCritical(double d);

    /**
     */
    public void setMinOverload(double d);

    public boolean isNormal(double value);

    public boolean isCritical(double value);

    public boolean isOverload(double value);

} // end MeterModel

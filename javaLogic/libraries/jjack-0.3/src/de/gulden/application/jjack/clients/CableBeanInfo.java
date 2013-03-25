/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.application.jjack.clients.CableBeanInfo
 * Version: 0.3
 *
 * Date:    2007-12-30
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.application.jjack.clients;

import de.gulden.framework.jjack.JJackBeanInfoAbstract;

/**
 * BeanInfo class for class Cable.
 *  
 * @author  Jens Gulden
 * @version  0.3
 */
public class CableBeanInfo extends JJackBeanInfoAbstract {

    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    public CableBeanInfo() {
        super(Cable.class);
    }

} // end CableBeanInfo

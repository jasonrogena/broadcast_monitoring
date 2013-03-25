/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.application.jjack.clients.GateBeanInfo
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
import java.beans.*;

/**
 * BeanInfo class for class Gate.
 *  
 * @author  Jens Gulden
 * @version  0.3
 */
public class GateBeanInfo extends JJackBeanInfoAbstract {

    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    public GateBeanInfo() {
        super(Gate.class, 2, 0);
    }


    // ------------------------------------------------------------------------
    // --- method                                                           ---
    // ------------------------------------------------------------------------

    /**
     * Gets the beans <code>PropertyDescriptor</code>s.
     *  
     * @return  An array of PropertyDescriptors describing the editable
     * properties supported by this bean.
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] p = super.getPropertyDescriptors();
        try {
        	p[1] = new PropertyDescriptor( "attack", Gate.class);
        	p[2] = new PropertyDescriptor( "treshold", Gate.class);
        }
        catch(IntrospectionException ie) {
        	exc(ie);
        }
        return p;
    }

} // end GateBeanInfo

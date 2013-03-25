/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.application.jjack.clients.SocketBeanInfo
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

import java.beans.*;
import de.gulden.framework.jjack.JJackBeanInfoAbstract;

/**
 * BeanInfo class for class Socket.
 *  
 * @author  Jens Gulden
 * @version  0.3
 */
public class SocketBeanInfo extends JJackBeanInfoAbstract {

    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    public SocketBeanInfo() {
        super(Socket.class, 1, 0);
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
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
                  /*p[0] = new PropertyDescriptor( "name", JJackClient.class, "getName", null );
        	p[1] = new PropertyDescriptor( "running", JJackClient.class, "isRunning", "setRunning");
                  p[2] = new PropertyDescriptor( "sampleRate", JJackClient.class, "getSampleRate", null );*/
                  p[1] = new PropertyDescriptor( "sampleRate", Socket.class, "getSampleRate", null );
              }
              catch(IntrospectionException ie) {
              	exc(ie);
              }
              return p;
    }

    public int getDefaultPropertyIndex() {
        return 0;
    }

} // end SocketBeanInfo

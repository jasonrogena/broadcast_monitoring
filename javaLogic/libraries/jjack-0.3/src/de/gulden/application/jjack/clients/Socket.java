/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.application.jjack.clients.Socket
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

import de.gulden.framework.jjack.*;
import de.gulden.util.Toolbox;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.*;
import java.util.*;

/**
 * JJack example client: The starting point to connect clients to when building
 * client sets inside a JavaBeans development environment.
 *  
 * @author  Jens Gulden
 * @version  0.3
 */
public class Socket extends JJackClient implements ActionListener {

    // ------------------------------------------------------------------------
    // --- final static field                                               ---
    // ------------------------------------------------------------------------

    protected static final String RESOURCE_ICON = "de/gulden/application/jjack/clients/res/icons/socket.png";


    // ------------------------------------------------------------------------
    // --- field                                                            ---
    // ------------------------------------------------------------------------

    protected JPopupMenu popupMenu;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public Socket() {
        super(false);
        JJackSystem.setProcessor(this);
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * Creates the user interface.
     *  
     * @return  visible component with the user interface, or <code>null</code> if this is an invisible audio processor
     */
    public JComponent createUI() {
        JButton button = new JButton(new ImageIcon(this.getClass().getClassLoader().getResource(RESOURCE_ICON)));
        button.setMargin(new Insets(0,0,0,0));
        button.addActionListener(this);
        return button;
    }

    /**
     * Process multiple samples from input buffer to output buffer.
     * This is regularly called by the JACK daemon.
     *  
     * @param e event object with references to input buffer and output buffer.
     */
    public void process(JJackAudioEvent e) {
        // just copy
        for (Iterator it = e.getChannels().iterator(); it.hasNext(); ) {
        	JJackAudioChannel ch = (JJackAudioChannel)it.next();
        	FloatBuffer in = ch.getPortBuffer(INPUT);
        	FloatBuffer out = ch.getPortBuffer(OUTPUT);
        	out.rewind();
        	out.put(in);
        }
    }

    /**
     * Returns the sample rate with which the JACK daemon is running.
     *  
     * @return  the sample rate in samples per second
     */
    public int getSampleRate() {
        return JJackSystem.getSampleRate();
    }

    public void destroy() throws JJackException {

    }

    public void actionPerformed(ActionEvent e) {
        Toolbox.messageBox(JJackSystem.getInfo());
    }

} // end Socket

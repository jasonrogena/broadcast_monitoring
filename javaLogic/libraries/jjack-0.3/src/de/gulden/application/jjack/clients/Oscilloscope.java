/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.application.jjack.clients.Oscilloscope
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

import de.gulden.application.jjack.clients.ui.OscilloscopeUI;
import de.gulden.framework.jjack.*;
import java.io.Serializable;
import javax.swing.*;
import java.beans.*;

/**
 * JJack example client: Displays the audio waveform in realtime.
 *  
 * @author  Jens Gulden
 * @version  0.3
 */
public class Oscilloscope extends JJackMonitor implements Serializable {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    protected int restMillis;

    protected int sampleRate;

    /**
     * Utility field used by bound properties.
     */
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Holds value of property zoom.
     */
    private float zoom = 0.01f;

    /**
     * Holds value of property amplify.
     */
    private int amplify = -1;

    /**
     * Holds value of property fps.
     */
    private int fps = 4;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public Oscilloscope() {
        super();
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * Returns a short info text about this audio processor.
     *  
     * @return  info text, or <code>null</code> if no info is available
     */
    public String getInfo() {
        return "Oscilloscope - (c) Jens Gulden 2004";
    }

    public void finalize() {
        ((OscilloscopeUI)gui).finalize();
    }

    /**
     * Getter for property zoom.
     *  
     * @return  Value of property zoom.
     */
    public float getZoom() {
        return this.zoom;
    }

    /**
     * Setter for property zoom.
     *  
     * @param zoom New value of property zoom.
     */
    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    /**
     * Getter for property amplify.
     *  
     * @return  Value of property amplify.
     */
    public int getAmplify() {
        return this.amplify;
    }

    /**
     * Setter for property amplify.
     *  
     * @param amplify New value of property amplify.
     */
    public void setAmplify(int amplify) {
        this.amplify = amplify;
    }

    /**
     * Getter for property fps.
     *  
     * @return  Value of property fps.
     */
    public int getFps() {
        return this.fps;
    }

    /**
     * Setter for property fps.
     *  
     * @param fps New value of property fps.
     */
    public void setFps(int fps) {
        this.fps = fps;
    }

    /**
     * Process multiple samples from input buffer to output buffer.
     * This is regularly called by the JACK daemon.
     *  
     * @param e event object with references to input buffer and output buffer.
     */
    public void process(JJackAudioEvent e) {
        ((OscilloscopeUI)gui).output(e.getInput());
    }

    /**
     * Creates the user interface.
     *  
     * @return  visible component with the user interface, or <code>null</code> if this is an invisible audio processor
     */
    protected JComponent createUI() {
        return new OscilloscopeUI(this);
    }

} // end Oscilloscope

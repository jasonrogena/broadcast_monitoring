/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.framework.jjack.JJackAudioProcessor
 * Version: 0.3
 *
 * Date:    2007-12-30
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.framework.jjack;

/**
 *  
 * @author  Jens Gulden
 * @version  0.3
 */
public interface JJackAudioProcessor {

    // ------------------------------------------------------------------------
    // --- method                                                           ---
    // ------------------------------------------------------------------------

    /**
     * Process multiple samples from input buffer to output buffer.
     * This is regularly called by the JACK daemon.
     *  
     * @param e event object with references to input buffer and output buffer.
     */
    public void process(JJackAudioEvent e);

} // end JJackAudioProcessor

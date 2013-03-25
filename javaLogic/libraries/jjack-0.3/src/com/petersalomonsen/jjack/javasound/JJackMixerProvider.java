/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   com.petersalomonsen.jjack.javasound.JJackMixerProvider
 * Version: 0.3
 *
 * Date:    2007-12-30
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 *
 * Author:  Peter Johan Salomonsen
 */

package com.petersalomonsen.jjack.javasound;

import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.spi.MixerProvider;
import de.gulden.framework.jjack.JJackSystem;

/**
 *  
 * @author  Peter Johan Salomonsen
 * @version  0.3
 */
public class JJackMixerProvider extends MixerProvider {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    JJackMixerInfo[] infos;

    JJackMixer mixer = null;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    public JJackMixerProvider() {
        infos = new JJackMixerInfo[] {
        	JJackMixerInfo.getInfo()
        };

        if(JJackSystem.isInitialized())
        	mixer = new JJackMixer();
        else
        	System.out.println("JACK is not available..");
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * @Override
     */
    public Mixer getMixer(Mixer.Info info) {
        if (! (info instanceof JJackMixerInfo) || !JJackSystem.isInitialized()) throw new IllegalArgumentException();
        return mixer;
    }

    /**
     * @Override
     */
    public Mixer.Info[] getMixerInfo() {
        if(JJackSystem.isInitialized())
        	return infos;
        else
        	return new JJackMixerInfo[] {};
    }

} // end JJackMixerProvider

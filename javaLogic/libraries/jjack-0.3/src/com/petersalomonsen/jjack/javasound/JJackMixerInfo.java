/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   com.petersalomonsen.jjack.javasound.JJackMixerInfo
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

import javax.sound.sampled.Mixer.Info;

/**
 *  
 * @author  Peter Johan Salomonsen
 * @version  0.3
 */
class JJackMixerInfo extends javax.sound.sampled.Mixer.Info {

    // ------------------------------------------------------------------------
    // --- static field                                                     ---
    // ------------------------------------------------------------------------

    static JJackMixerInfo info = new JJackMixerInfo("JJack","jjack.berlios.de","JJack javasound provider","0.1");


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    protected JJackMixerInfo(String name, String vendor, String description, String version) {
        super(name, vendor, description, version);
    }


    // ------------------------------------------------------------------------
    // --- static method                                                    ---
    // ------------------------------------------------------------------------

    static JJackMixerInfo getInfo() {
        return info;
    }

} // end JJackMixerInfo

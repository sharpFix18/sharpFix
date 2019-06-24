/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2011 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.examples.cruisecontrol;

/** Models the current road and incline of the road */
public class RoadModel implements org.objectweb.proactive.RunActive {
    // Fields 
    final static int ACTIVE = 1;
    final static int INACTIVE = 0;

    /** State of the Road */
    private int state = INACTIVE;

    /** the random time of waiting between 2 differents inclines of the road */
    private int time = 15000;

    /** Current inclines of the road */
    private double alpha = 0;

    /** Reference to the dispatcher for communication purposes */
    Interface father;

    ///////////////////////////////////////////////////

    /** No-arg constructor for the ProActive package */
    public RoadModel() {
    }

    /** constructor which intializes the father field */
    public RoadModel(Interface m_father) {
        this.father = m_father;
    }

    ////////////////////////////////////////////////////////////
    // the engine is on

    /** Turns on this active object in order to computes the incline */
    public void engineOn() {
        this.state = ACTIVE;
    }

    // the engine is Off

    /** turns off this active object off */
    public void engineOff() {
        this.state = INACTIVE;
    }

    ///////////////////////////////////////////////////////////////

    /** Routine which computes the new value of the incline */
    private void calculateAlpha() {
        //	alpha = 0.6 * Math.random();
        time = 45000 + (int) (30000 * Math.random());
        father.setAlpha(alpha);
        //System.out.println("RoadModel : New Alpha : "+alpha);
    }

    /**
     * Increases the current value of the incline road
     */
    public void incAlpha() {
        if (alpha < 0.6) {
            alpha += 0.09;
        }
        father.setAlpha(alpha);
        //System.out.println("RoadModel : IncAlpha : Alpha >> "+ alpha);
        //System.out.println("RoadModel : IncAlpha : state >> "+ state);
    }

    /**
     * Decreases the current value of alpha
     */
    public void decAlpha() {
        if (alpha > -0.6) {
            alpha -= 0.09;
        }
        father.setAlpha(alpha);
        //System.out.println("RoadModel : IncAlpha : Alpha >> "+ alpha);
        //System.out.println("RoadModel : IncAlpha : state >> "+ state);
    }

    ////////////////////////////////////////////////////////////

    /** Routine which computes the new random incline and sleeds for a time msec */
    public void runActivity(org.objectweb.proactive.Body body) {
        org.objectweb.proactive.Service service = new org.objectweb.proactive.Service(body);

        //System.out.println ("Starts live in object RoadModel");
        while (body.isActive()) {
            try {
                Thread.sleep(1000);
                if (this.state == ACTIVE) {
                    this.calculateAlpha();
                }
                service.serveOldest();
            } catch (InterruptedException e) {
            }
        }
    }

    /** Not used in this release */
    public void initialize() {
    }

    ///////////////////////////////////////////////////////////////////
}

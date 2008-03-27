/*
 * JBox2D - A Java Port of Erin Catto's Box2D
 * 
 * JBox2D homepage: http://jbox2d.sourceforge.net/ 
 * Box2D homepage: http://www.gphysics.com
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 * claim that you wrote the original software. If you use this software
 * in a product, an acknowledgment in the product documentation would be
 * appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 * misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */
package org.jbox2d.testbed.tests;

import org.jbox2d.collision.BoxDef;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.testbed.PTest;

import processing.core.PApplet;



public class Spring extends PTest {
    
    Body bodyA;
    Body bodyB;
    Body bodyC;
    Body bodyD;
    Body bodyE;

    public Spring() {
        super("Spring");
    }

    @Override
    public void go(World world) {
        {
            BoxDef sd = new BoxDef();
            sd.extents = new Vec2(50.0f, 10.0f);

            BodyDef bd = new BodyDef();
            bd.position = new Vec2(0.0f, -10.0f);
            bd.addShape(sd);
            world.createBody(bd);
        }

        {
            BoxDef sd = new BoxDef();
            float a = 0.75f;
            sd.extents = new Vec2(a, a);
            sd.density = 1.0f;
            sd.friction = 0.3f;

            BodyDef bd = new BodyDef();
            bd.addShape(sd);

            bd.position = new Vec2(0.0f, 1.0f);
            bodyA = m_world.createBody(bd);
            bd.position = new Vec2(0.0f, 5.0f);
            bodyB = m_world.createBody(bd);
            bd.position = new Vec2(3.0f, 3.0f);
            bodyC = m_world.createBody(bd);
            
            BodyDef bd2 = new BodyDef();
            sd.extents = new Vec2(5f,1f);
            bd2.addShape(sd);
            bd2.position = new Vec2(0.0f, 10.0f);
            bodyD = m_world.createBody(bd2);
            bd2.position = new Vec2(0.0f, 14.0f);
            bodyE = m_world.createBody(bd2);
        }
    }
    
    @Override
    public void preStep() {
        final float dist = (float)Math.sqrt(13.0);
        addSpringForce(bodyA, bodyB, 5000, 10f, 4.0f);
        addSpringForce(bodyB, bodyC, 5000, 10f, dist);
        addSpringForce(bodyA, bodyC, 5000, 10f, dist);
        
        addSpringForce(bodyD,new Vec2(-3.0f,0.0f),bodyE,new Vec2(-3.0f,0.0f), 5000, 50f, 4.0f);
        addSpringForce(bodyD,new Vec2(3.0f,0.0f),bodyE,new Vec2(3.0f,0.0f), 5000, 50f, 4.0f);
        final float dist2 = (float)Math.sqrt(52.0f);
        addSpringForce(bodyD,new Vec2(-3.0f,0.0f),bodyE,new Vec2(3.0f,0.0f), 5000, 50f, dist2);
        addSpringForce(bodyD,new Vec2(3.0f,0.0f),bodyE,new Vec2(-3.0f,0.0f), 5000, 50f, dist2);
        
    }
    
    public void addSpringForce(Body bA, Body bB, float k, float friction, float desiredDist){
    	addSpringForce(bA, new Vec2(0.0f,0.0f),bB, new Vec2(0.0f, 0.0f), k, friction, desiredDist);
    }
    
    public void addSpringForce(Body bA, Vec2 localA, Body bB, Vec2 localB, float k, float friction, float desiredDist) {
        Vec2 pA = bA.getWorldPoint(localA);//m_position;
        Vec2 pB = bB.getWorldPoint(localB);//m_position;
        Vec2 diff = pB.sub(pA);
        //Find velocities of attach points
        Vec2 vA = bA.m_linearVelocity.sub(Vec2.cross(bA.getWorldVector(localA), bA.m_angularVelocity));
        Vec2 vB = bB.m_linearVelocity.sub(Vec2.cross(bB.getWorldVector(localB), bB.m_angularVelocity));
        Vec2 vdiff = vB.sub(vA);
        float dx = diff.normalize(); //normalizes diff and puts length into dx
        float vrel = vdiff.x*diff.x + vdiff.y*diff.y;
        float forceMag = -k*(dx-desiredDist) - friction*vrel;
        diff.mulLocal(forceMag); // diff *= forceMag
        bB.applyForce(diff, bA.getWorldPoint(localA));
        bA.applyForce(diff.mulLocal(-1f), bB.getWorldPoint(localB));
        bA.wakeUp();
        bB.wakeUp();
        
        //Draw spring
        stroke(150,150,0);
        Vec2 drawA = bA.getWorldPoint(localA);
        Vec2 drawB = bB.getWorldPoint(localB);
        line(drawA.x,drawA.y, drawB.x,drawB.y);
    }

    public static void main(String[] args) {
        PApplet.main(new String[] { "org.jbox2d.testbed.tests.Spring" });
    }
}
package dynamics;

import common.Settings;
import common.Vec2;

import dynamics.contacts.NullContact;
import dynamics.contacts.Contact;
import dynamics.contacts.ContactSolver;
import dynamics.joints.Joint;

public class Island {
    Body[] m_bodies;

    Contact[] m_contacts;

    Joint[] m_joints;

    int m_bodyCount;

    int m_jointCount;

    int m_contactCount;

    int m_bodyCapacity;

    int m_contactCapacity;

    int m_jointCapacity;

    int m_positionIterations;

    float m_positionError;

    public Island(int bodyCapacity, int contactCapacity, int jointCapacity) {
        m_bodyCapacity = bodyCapacity;
        m_contactCapacity = contactCapacity;
        m_jointCapacity = jointCapacity;
        m_bodyCount = 0;
        m_contactCount = 0;
        m_jointCount = 0;

        m_bodies = new Body[bodyCapacity];
        m_contacts = new Contact[contactCapacity];
        m_joints = new Joint[jointCapacity];

        for (int i = 0; i < contactCapacity; i++) {
            m_contacts[i] = new NullContact();
        }
    }

    void clear() {
        m_bodyCount = 0;
        m_contactCount = 0;
        m_jointCount = 0;
    }

    void solve(Vec2 gravity, int iterations, float dt) {
        for (int i = 0; i < m_bodyCount; ++i) {
            Body b = m_bodies[i];

            if (b.m_invMass == 0.0f) {
                continue;
            }

            b.m_linearVelocity.addLocal((gravity
                    .add(b.m_force.mul(b.m_invMass))).mul(dt));
            b.m_angularVelocity += dt * b.m_invI * b.m_torque;
        }

        // float inv_dt = dt > 0.0f ? 1.0f / dt : 0.0f;

        ContactSolver contactSolver = new ContactSolver(m_contacts,
                m_contactCount);

        // Pre-solve
        contactSolver.preSolve();

        for (int i = 0; i < m_jointCount; ++i) {
            m_joints[i].preSolve();
        }

        // Solve velocity constraints.
        for (int i = 0; i < iterations; ++i) {
            contactSolver.solveVelocityConstraints();

            for (int j = 0; j < m_jointCount; ++j) {
                m_joints[j].solveVelocityConstraints(dt);
            }
        }

        // Integrate positions.
        for (int i = 0; i < m_bodyCount; ++i) {
            Body b = m_bodies[i];
            if (b.m_invMass == 0.0f) {
                continue;
            }

            b.m_position.addLocal(b.m_linearVelocity.mul(dt));
            b.m_rotation += dt * b.m_angularVelocity;

            b.m_R.setAngle(b.m_rotation);
        }

        // Solve position constraints.
        if (World.ENABLE_POSITION_CORRECTION) {
            // System.out.println("position correcting");
            for (m_positionIterations = 0; m_positionIterations < iterations; ++m_positionIterations) {
                // System.out.println(m_positionIterations);
                boolean contactsOkay = contactSolver
                        .solvePositionConstraints(Settings.contactBaumgarte);

                boolean jointsOkay = true;
                for (int i = 0; i < m_jointCount; ++i) {
                    boolean jointOkay = m_joints[i].solvePositionConstraints();
                    jointsOkay = jointsOkay && jointOkay;
                }

                if (contactsOkay && jointsOkay) {
                    break;
                }
            }
        }

        // Post-solve.
        contactSolver.postSolve();

        // Synchronize shapes and reset forces.
        for (int i = 0; i < m_bodyCount; ++i) {
            Body b = m_bodies[i];
            if (b.m_invMass == 0.0f)
                continue;
            b.synchronizeShapes();
            b.m_force.set(0.0f, 0.0f);
            b.m_torque = 0.0f;
        }

    }

    void updateSleep(float dt) {
        float minSleepTime = Float.MAX_VALUE;

        float linTolSqr = Settings.linearSleepTolerance
                * Settings.linearSleepTolerance;
        float angTolSqr = Settings.angularSleepTolerance
                * Settings.angularSleepTolerance;

        for (int i = 0; i < m_bodyCount; ++i) {
            Body b = m_bodies[i];
            if (b.m_invMass == 0.0f) {
                continue;
            }

            if ((b.m_flags & Body.e_allowSleepFlag) == 0) {
                b.m_sleepTime = 0.0f;
                minSleepTime = 0.0f;
            }

            if (((b.m_flags & Body.e_allowSleepFlag) == 0)
                    || b.m_angularVelocity * b.m_angularVelocity > angTolSqr
                    || Vec2.dot(b.m_linearVelocity, b.m_linearVelocity) > linTolSqr) {
                b.m_sleepTime = 0.0f;
                minSleepTime = 0.0f;
            }
            else {
                b.m_sleepTime += dt;
                minSleepTime = Math.min(minSleepTime, b.m_sleepTime);
            }
        }

        if (minSleepTime >= Settings.timeToSleep) {
            for (int i = 0; i < m_bodyCount; ++i) {
                Body b = m_bodies[i];
                b.m_flags |= Body.e_sleepFlag;
            }
        }
    }

    void add(Body body) {
        assert m_bodyCount < m_bodyCapacity;
        m_bodies[m_bodyCount++] = body;
    }

    void add(Contact contact) {
        assert (m_contactCount < m_contactCapacity);
        m_contacts[m_contactCount++] = contact.clone();
    }

    void add(Joint joint) {
        assert (m_jointCount < m_jointCapacity);
        m_joints[m_jointCount++] = joint;
    }
}
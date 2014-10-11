package org.jbox2d.dynamics

import org.jbox2d.common.Vec2

/**
 * A body definition holds all the data needed to construct a rigid body. You can safely re-use body
 * definitions. Shapes are added to a body after construction.
 *
 * @param bodyType The body type: static, kinematic, or dynamic.
 *                 Note: if a dynamic body would have zero mass, the mass is set to one.
 *
 * @param userData Use this to store application specific body data
 *
 * @param position The world position of the body. Avoid creating bodies at the origin
 *                 since this can lead to many overlapping shapes.
 *
 * @param angle The world angle of the body in radians.
 *
 * @param linearVelocity The linear velocity of the body in world co-ordinates.
 *
 * @param angularVelocity The angular velocity of the body.
 *
 * @param linearDamping Linear damping is use to reduce the linear velocity. The damping parameter can be larger than
 *                      1.0f but the damping effect becomes sensitive to the time step when the damping parameter is
 *                      large.
 * @param angularDamping Angular damping is use to reduce the angular velocity. The damping parameter can be larger
 *                       than 1.0f but the damping effect becomes sensitive to the time step
 *                       when the damping parameter is large.
 *
 * @param allowSleep Set this flag to false if this body should never fall asleep. Note that this increases CPU usage.
 * @param awake Is this body initially sleeping?
 * @param fixedRotation Should this body be prevented from rotating? Useful for characters.
 * @param bullet Is this a fast moving body that should be prevented from tunneling through other moving bodies?
 *               Note that all bodies are prevented from tunneling through kinematic and static bodies.
 *               This setting is only considered on dynamic bodies.
 *               Warning: You should use this flag sparingly since it increases processing time.
 * @param active Does this body start out active?
 * @param gravityScale Experimental: scales the inertia tensor.
 */
case class BodyDef(bodyType: BodyType = BodyType.STATIC,
                   userData: AnyRef = null,
                   position: Vec2 = null,
                   angle: Float = 0f,
                   linearVelocity: Vec2 = null,
                   angularVelocity: Float = 0f,
                   linearDamping: Float = 0f,
                   angularDamping: Float = 0f,
                   allowSleep: Boolean = false,
                   awake: Boolean = false,
                   fixedRotation: Boolean = false,
                   bullet: Boolean = false,
                   active: Boolean = false,
                   gravityScale: Float = 0f)

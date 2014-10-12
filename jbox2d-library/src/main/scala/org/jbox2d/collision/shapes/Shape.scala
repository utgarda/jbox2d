
package org.jbox2d.collision.shapes

import org.jbox2d.collision.{AABB, RayCastInput, RayCastOutput}
import org.jbox2d.common.{Transform, Vec2}

import scala.beans.BeanProperty

/**
 * A shape is used for collision detection. You can create a shape however you like. Shapes used for
 * simulation in World are created automatically when a Fixture is created. Shapes may encapsulate a
 * one or more child shapes.
 *
 * @param `type` The type of this shape. You can use this to down cast to the concrete shape.
 * @param radius Sets the radius of the underlying shape.
 *               This can refer to different things depending on the implementation
 */
abstract class Shape(@BeanProperty final val `type`: ShapeType,
                     @BeanProperty var radius: Float = 0f) {
  /**
   * @return The number of child primitives
   */
  def getChildCount: Int

  /**
   * Test a point for containment in this shape. This only works for convex shapes.
   *
   * @param xf the shape world transform.
   * @param p a point in world coordinates.
   */
  def testPoint(xf: Transform, p: Vec2): Boolean

  /**
   * Cast a ray against a child shape.
   *
   * @param output the ray-cast results.
   * @param input the ray-cast input parameters.
   * @param transform the transform to be applied to the shape.
   * @param childIndex the child shape index
   * @return if hit
   */
  def raycast(output: RayCastOutput, input: RayCastInput, transform: Transform, childIndex: Int): Boolean

  /**
   * Given a transform, compute the associated axis aligned bounding box for a child shape.
   *
   * @param aabb returns the axis aligned box.
   * @param xf the world transform of the shape.
   */
  def computeAABB(aabb: AABB, xf: Transform, childIndex: Int)

  /**
   * Compute the mass properties of this shape using its dimensions and density. The inertia tensor
   * is computed about the local origin.
   *
   * @param massData returns the mass data for this shape.
   * @param density the density in kilograms per meter squared.
   */
  def computeMass(massData: MassData, density: Float)

  /**
   * Compute the distance from the current shape to the specified point. This only works for convex
   * shapes.
   *
   * @param xf the shape world transform.
   * @param p a point in world coordinates.
   * @param normalOut returns the direction in which the distance increases.
   * @return distance returns the distance from the current shape.
   */
  def computeDistanceToOut(xf: Transform, p: Vec2, childIndex: Int, normalOut: Vec2): Float

  override def clone: Shape = throw new CloneNotSupportedException
}

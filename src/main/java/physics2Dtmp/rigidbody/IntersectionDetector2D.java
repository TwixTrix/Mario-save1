package physics2Dtmp.rigidbody;

import org.joml.Vector2f;
import physics2Dtmp.primitives.*;
import renderer.Line2D;
import util.RMath;

public class IntersectionDetector2D {
    //=================================================================
    //Point vs primitve test
    //================================================================
    public static boolean pointOnLine(Vector2f point, Line2D line)
    {
        float dy = line.getEnd().y - line.getStart().y;
        float dx = line.getEnd().x - line.getStart().x;
        if(dx == 0.0f)
        {
            return RMath.compare(point.x, line.getStart().x);
        }
        float m = dy/dx;

        float b = line.getEnd().y - (m* line.getEnd().x);

        // check the line equation


        return point.y == m*point.x + b;
    }

    public static boolean pointInCircle(Vector2f point, Circle circle)
    {
        Vector2f circleCenter = circle.getCenter();
        Vector2f centerToPoint = new Vector2f(point).sub(circleCenter);
        return centerToPoint.lengthSquared() <= circle.getRadius() * circle.getRadius();
    }

    public static boolean pointInAABB(Vector2f point, AABB box)
    {
        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        return point.x <= max.x && min.x <= point.x &&
                point.y <= max.y && point.y >= min.y;

    }

    public static boolean pointInBox2D(Vector2f point, Box2D box)
    {
        // translate point into local space

        Vector2f pointLocalBoxSpace  = new Vector2f(point);
        RMath.rotate(pointLocalBoxSpace,
                box.getRigidbody2D().getRotation(),
                box.getRigidbody2D().getPosition());

        Vector2f min = box.getLocalMin();
        Vector2f max = box.getLocalMax();

        return pointLocalBoxSpace.x <= max.x && min.x <= pointLocalBoxSpace.x &&
                pointLocalBoxSpace.y <= max.y && pointLocalBoxSpace.y >= min.y;

    }




    //================================================================
    // Line vs primitve test
    //================================================================

    public static boolean lineAndCircle(Line2D line, Circle circle)
    {
        if (pointInCircle(line.getStart(), circle) || pointInCircle(line.getEnd(), circle))
        {
            return true;
        }
        Vector2f ab = new Vector2f(line.getEnd()).sub(line.getStart());

        //project point(circle postion) onto ab(line segment)
        //paramertized postion
        Vector2f circleCenter = circle.getCenter();
        Vector2f centerToLineStart = new Vector2f(circleCenter).sub(line.getStart());
        float t = centerToLineStart.dot(ab) / ab.dot(ab);

        if(t < 0.0f || t > 1.0f)
        {
            return false;
        }

        //find the closest point to the line segment

        Vector2f closestPoint = new Vector2f(line.getStart()).add(ab.mul(t));

        return pointInCircle(closestPoint,circle);

    }

    public static boolean lineAndAABB(Line2D line, AABB box)
    {
        if(pointInAABB(line.getStart(), box) || pointInAABB(line.getEnd(), box))
        {
            return true;
        }
        Vector2f unitVector = new Vector2f(line.getEnd()).sub(line.getStart());
        unitVector.normalize();
        unitVector.x = (unitVector.x != 0) ? 1.0f/ unitVector.x : 0f;
        unitVector.y = (unitVector.y != 0) ? 1.0f/unitVector.y : 0f;

        Vector2f min = box.getMin();
        min.sub(line.getStart()).mul(unitVector);
        Vector2f max = box.getMax();
        max.sub(line.getStart()).mul(unitVector);

        float tMin = Math.max(Math.min(min.x, max.x), Math.min(min.y, max.y));
        float tMax = Math.min(Math.max(min.x, max.x), Math.max(min.y, max.y));
        if(tMax < 0 || tMin> tMax)
        {
            return false;
        }
        float t = (tMin < 0f) ? tMax : tMin;
        return t > 0f && t*t < line.lengthSquared();
    }
    public static boolean lineAndBox2D(Line2D line , Box2D box)
    {
        float theta = -box.getRigidbody2D().getRotation();
        Vector2f center = box.getRigidbody2D().getPosition();
        Vector2f localStart = new Vector2f(line.getStart());
        Vector2f localEnd = new Vector2f(line.getEnd());
        RMath.rotate(localStart,theta , center);
        RMath.rotate(localEnd, theta, center);

        Line2D localLine = new Line2D(localStart, localEnd);
        AABB aabb = new AABB(box.getLocalMin(), box.getLocalMax());
        return lineAndAABB(localLine, aabb);
    }

    // Ray cast

    public static boolean raycast(Circle circle, Ray2D ray, RaycastResult result)
    {
        RaycastResult.reset(result);
        Vector2f originToCircle = new Vector2f(circle.getCenter().sub(ray.getOrigin()));
        float radiusSquared = circle.getRadius() * circle.getRadius();
        float originToCircleLengthSquared = originToCircle.lengthSquared();

        //project the vector from the ray origin onto the direction of the ray

        float a = originToCircle.dot(ray.getDirection());
        float bSquared = originToCircleLengthSquared - (a*a);
        if(radiusSquared - bSquared < 0.0f)
        {
            return false;
        }
        float f = (float) Math.sqrt(radiusSquared - bSquared);
        float t = 0;
        if(originToCircleLengthSquared < radiusSquared)
        {
            // Ray starts inside the circle
            t = a + f;
        }
        else
        {
            t = a-f;
        }

        if(result != null)
        {
            Vector2f point = new Vector2f(ray.getOrigin()).add(new Vector2f(ray.getDirection()).mul(t));
            Vector2f normal = new Vector2f(point).sub(circle.getCenter());
            normal.normalize();

            result.init(point, normal , t , true );
        }
        return true;
    }


    public static boolean raycast(AABB box ,Ray2D ray, RaycastResult result)
    {
        RaycastResult.reset(result);
        Vector2f unitVector = ray.getDirection();
        unitVector.normalize();
        unitVector.x = (unitVector.x != 0) ? 1.0f/ unitVector.x : 0f;
        unitVector.y = (unitVector.y != 0) ? 1.0f/unitVector.y : 0f;

        Vector2f min = box.getMin();
        min.sub(ray.getOrigin()).mul(unitVector);
        Vector2f max = box.getMax();
        max.sub(ray.getOrigin()).mul(unitVector);

        float tMin = Math.max(Math.min(min.x, max.x), Math.min(min.y, max.y));
        float tMax = Math.min(Math.max(min.x, max.x), Math.max(min.y, max.y));
        if(tMax < 0 || tMin> tMax)
        {
            return false;
        }
        float t = (tMin < 0f) ? tMax : tMin;
        boolean hit  = t >  0f ;  //&& t*t < ray.getMaximum
        if(!hit)
        {
            return false;
        }

        if(result != null)
        {
            Vector2f point = new Vector2f(ray.getOrigin()).add(new Vector2f(ray.getDirection()).mul(t));
            Vector2f normal = new Vector2f(ray.getOrigin()).sub(point);
            normal.normalize();

            result.init(point , normal , t , true);
        }
        return true;
    }
    public static boolean raycast(Box2D box ,Ray2D ray, RaycastResult result)
    {
        RaycastResult.reset(result);

        Vector2f size = box.getHalfSize();

        Vector2f xAxis = new Vector2f(1, 0);
        Vector2f yAxis = new Vector2f(0,1);
        RMath.rotate(xAxis , -box.getRigidbody2D().getRotation() , new Vector2f(0,0));
        RMath.rotate(yAxis , -box.getRigidbody2D().getRotation() , new Vector2f(0,0));


        //project the direction of the ray onto each axis of the box
        Vector2f f = new Vector2f(
                xAxis.dot(ray.getDirection()),
                yAxis.dot(ray.getDirection())
        );
        //Next project p onto every axis of the box
        Vector2f p = new Vector2f(box.getRigidbody2D().getPosition()).sub(ray.getOrigin());
        Vector2f e = new Vector2f(
                xAxis.dot(p),
                yAxis.dot(p)
        );

        float[] tArr = {0,0,0,0};
        for(int i = 0; i< 2 ; i++)
        {
            if(RMath.compare(f.get(i), 0))
            {
                // if the array is parallel to the current axis, and the orgin of the ray is not inside, then no hit
                if(-e.get(i) - size.get(i) > 0 || -e.get(i) + size.get(i) < 0)
                {
                    return false;
                }
                f.setComponent(i , 0.00001f); // Set to small value to avoid divide by 0 error
            }
            tArr[i * 2 + 0] = (e.get(i) + size.get(i)) / f.get(i); // tmax for this axis
            tArr[i * 2 + 1] = (e.get(i) - size.get(i) ) / f.get(i); // tmin for this axis
        }

        float tMin = Math.max(Math.min(tArr[0] , tArr[1]), Math.min(tArr[2], tArr[3]));
        float tMax = Math.min(Math.max(tArr[0], tArr[1]), Math.max(tArr[2], tArr[3]));
        float t = (tMin < 0f) ? tMax : tMin;
        boolean hit  = t >  0f ;  //&& t*t < ray.getMaximum
        if(!hit)
        {
            return false;
        }

        if(result != null)
        {
            Vector2f point = new Vector2f(ray.getOrigin()).add(new Vector2f(ray.getDirection()).mul(t));
            Vector2f normal = new Vector2f(ray.getOrigin()).sub(point);
            normal.normalize();

            result.init(point , normal , t , true);
        }
        return true;
    }

    //===================================
    // circle vs primitive test
    //====================================

    public static boolean circleAndLine(Circle circle , Line2D line)
    {
        return lineAndCircle(line, circle);
    }

    public static boolean circleAndCircle(Circle c1 , Circle c2)
    {
        Vector2f vecBetweenCenters = new Vector2f(c1.getCenter()).sub(c2.getCenter());
        float radiiSum = c1.getRadius() + c2.getRadius();
        return vecBetweenCenters.lengthSquared() <= radiiSum * radiiSum;
    }

    public static boolean circleAndAABB(Circle circle , AABB box )
    {
        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        Vector2f closestPointToCircle = new Vector2f(circle.getCenter());
        if(closestPointToCircle.x < min.x)
        {
            closestPointToCircle.x = min.x;
        }
        else if(closestPointToCircle.x > max.x)
        {
            closestPointToCircle.x = max.x;
        }

        if(closestPointToCircle.y < min.y)
        {
            closestPointToCircle.y = min.y;
        }
        else if(closestPointToCircle.y > max.y)
        {
            closestPointToCircle.y = max.y;
        }

        Vector2f circleToBox = new Vector2f(circle.getCenter()).sub(closestPointToCircle);
        return circleToBox.lengthSquared() <= circle.getRadius() * circle.getRadius();
    }

    public static  boolean circleAndBox2D(Circle circle , Box2D box)
    {
        //Treat the box just like an AABB, after  rotation
        Vector2f min = new Vector2f();
        Vector2f max = new Vector2f(box.getHalfSize()).mul(2.0f);

        // Create a circle in box's local space
        Vector2f r = new Vector2f(circle.getCenter()).sub(box.getRigidbody2D().getPosition());
        RMath.rotate(r, -box.getRigidbody2D().getRotation() , new Vector2f(0,0) );
        Vector2f localCirclePos = new Vector2f(r).add(box.getHalfSize());

        Vector2f closestPointToCircle = new Vector2f(localCirclePos);
        if(closestPointToCircle.x < min.x)
        {
            closestPointToCircle.x = min.x;
        }
        else if(closestPointToCircle.x > max.x)
        {
            closestPointToCircle.x = max.x;
        }

        if(closestPointToCircle.y < min.y)
        {
            closestPointToCircle.y = min.y;
        }
        else if(closestPointToCircle.y > max.y)
        {
            closestPointToCircle.y = max.y;
        }

        Vector2f circleToBox = new Vector2f(localCirclePos).sub(closestPointToCircle);
        return circleToBox.lengthSquared() <= circle.getRadius() * circle.getRadius();


    }

    //======================================================
    // AABB vs primitive test
    // =====================================================

    public static boolean AABBAndCircle(AABB box, Circle circle)
    {
        return circleAndAABB(circle, box);
    }
    public static boolean AABBAndAABB(AABB box1,AABB box2)
    {
        // axis alligned
        Vector2f[] axesToTest = {new Vector2f(0,1), new Vector2f(1,0)};
        for( int i = 0; i < axesToTest.length; i++)
        {
            if(!overlapOnAxis(box1, box2, axesToTest[i]) )
            {
                return false;
            }
        }
        return true;
    }

    public static boolean AABBAndBox2D(AABB b1, Box2D b2)
    {
        Vector2f[] axesToTest = {new Vector2f(0,1), new Vector2f(1,0),
        new Vector2f(0,1), new Vector2f(1,0)
        };
        RMath.rotate(axesToTest[2], b2.getRigidbody2D().getRotation() , new Vector2f());
        RMath.rotate(axesToTest[3], b2.getRigidbody2D().getRotation() , new Vector2f());
        // axis alligned

        for( int i = 0; i < axesToTest.length; i++)
        {
            if(!overlapOnAxis(b1, b2, axesToTest[i]) )
            {
                return false;
            }
        }
        return true;

    }
    //===============================================
    // Seperating axis theorem helpers
    //====================================================

    private static boolean overlapOnAxis(AABB b1, AABB b2 , Vector2f axis)
    {
        Vector2f  b1Interval = getInterval(b1, axis);
        Vector2f b2Interval = getInterval(b2, axis);
        return ((b2Interval.x <= b1Interval.y) && (b1Interval.x <= b2Interval.y));
    }
    private static boolean overlapOnAxis(AABB b1, Box2D b2 , Vector2f axis)
    {
        Vector2f  b1Interval = getInterval(b1, axis);
        Vector2f b2Interval = getInterval(b2, axis);
        return ((b2Interval.x <= b1Interval.y) && (b1Interval.x <= b2Interval.y));
    }
    private static boolean overlapOnAxis(Box2D b1, Box2D b2 , Vector2f axis)
    {
        Vector2f  b1Interval = getInterval(b1, axis);
        Vector2f b2Interval = getInterval(b2, axis);
        return ((b2Interval.x <= b1Interval.y) && (b1Interval.x <= b2Interval.y));
    }
    private static Vector2f getInterval(AABB rectangle , Vector2f axis)
    {
        Vector2f result = new Vector2f(0,0);

        Vector2f min = rectangle.getMin();
        Vector2f max = rectangle.getMax();

        Vector2f vertices[] ={
                new Vector2f(min.x , min.y), new Vector2f(min.x, max.y),
                new Vector2f(max.x, min.y) , new Vector2f(max.x, max.y)
        };

        result.x  = axis.dot(vertices[0]);
        result.y = result.x;
        for(int i = 1 ; i < 4; i++ )
        {
            float projection = axis.dot(vertices[i]);
            if(projection < result.x)
            {
                result.x = projection;
            }
            if(projection > result.y)
            {
                result.y = projection;
            }

        }
        return result;
    }
    private static Vector2f getInterval(Box2D rectangle , Vector2f axis)
    {
        Vector2f result = new Vector2f(0,0);



        Vector2f vertices[] = rectangle.getVertices();

        result.x  = axis.dot(vertices[0]);
        result.y = result.x;
        for(int i = 1 ; i < 4; i++ )
        {
            float projection = axis.dot(vertices[i]);
            if(projection < result.x)
            {
                result.x = projection;
            }
            if(projection > result.y)
            {
                result.y = projection;
            }

        }
        return result;
    }

}

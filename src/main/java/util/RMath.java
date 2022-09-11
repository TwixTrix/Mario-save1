package util;

import org.joml.Vector2f;

public class RMath {

        public static void rotate(Vector2f vec, float angleDegrees, Vector2f origin)
        {
            float x = vec.x - origin.x;
            float y = vec.y - origin.y;


            float cos = (float)Math.cos(Math.toRadians(angleDegrees));
            float sin = (float )Math.sin(Math.toRadians(angleDegrees));

            float xPrime = (x* cos) -( y * sin);
            float yPrime = (x*sin) + (y*cos);

            xPrime += origin.x;
            yPrime += origin.y;

            vec.x = xPrime;
            vec.y = yPrime;
        }


        public static boolean compare(float x , float y, float epsilon)
        {
            return Math.abs(x - y) <= epsilon * Math.max(1.0f, Math.max(Math.abs(x),Math.abs(y)));
        }
        public static boolean compare(float x , float y)
        {
            float epsilon = Float.MIN_VALUE;
             return Math.abs(x - y) <= epsilon * Math.max(1.0f, Math.max(Math.abs(x),Math.abs(y)));
        }

        public static boolean compare(Vector2f vec1, Vector2f vec2, float epsilon )
        {
            return compare(vec1.x, vec2.x, epsilon) && compare(vec1.y , vec2.y , epsilon);
        }
        public static boolean compare(Vector2f vec1, Vector2f vec2 )
        {
            float epsilon = Float.MIN_VALUE;
             return compare(vec1.x, vec2.x, epsilon) && compare(vec1.y , vec2.y , epsilon);
        }
}

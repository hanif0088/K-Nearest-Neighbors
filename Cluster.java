/**
 * @author Hanif Mirza, Fairmont State University, May 2016
 * Class Name: Cluster.java
 * Date: 06 May, 2016
 *
 * Class Description: This class will contain the mean value of every cluster and all points
 * corresponding to that cluster. All the points will be stored in a vector called pointsVector.
 *
 */

import java.awt.geom.Point2D;
import java.util.*;
import java.awt.Point;

class Cluster
{
	Point2D.Double 	meanPoint;
	Vector<Point> 	pointsVector;

	Cluster(double x, double y)
	{
		meanPoint = new Point2D.Double(x,y);
		pointsVector = new Vector<Point>();
	}
}
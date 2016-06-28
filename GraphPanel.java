/**
 * @author Hanif Mirza, Fairmont State University, May 2016
 * Class Name: GraphPanel.java
 * Date: 06 May, 2016
 *
 * Class Description: This class will contain the two-dimensional graph in a JPanel. The graph will display the mean and the points in each
 * cluster  with a unique color.
 *
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.FontMetrics;
import java.util.*;
import java.awt.geom.Point2D;


class GraphPanel extends JPanel
{
	int 			MAX_SCORE = 20;
	int 			PANEL_WIDTH = 650, PANEL_HEIGHT = 580;
	int 			BORDER_GAP = 50;
	Color 			BORDER_COLOR = new Color(1, 0.1f, 0.1f).darker().darker();
	Stroke 			GRAPH_STROKE = new BasicStroke(3f);
	int 			GRAPH_POINT_WIDTH = 7, CLUSTER_POINT_WIDTH = 12 ;
	int 			X_HATCH_CNT = 20, Y_HATCH_CNT = 20;
	double 			xScale, yScale;
	Vector<Point> 	pointsVector = null;
	Vector<Color> 	colorsVector = new Vector<Color>();
	Vector<Cluster> clusterVector = new Vector<Cluster>();
	Graphics2D 		g2;

	public GraphPanel()
	{
		setBorder(new LineBorder(BORDER_COLOR, 4) );

		// Unique colors to draw the points in a cluster
		colorsVector.addElement( Color.BLUE.darker() );
		colorsVector.addElement( Color.RED.darker() );
		colorsVector.addElement( Color.GREEN.darker() );
		colorsVector.addElement( Color.CYAN.darker());
		colorsVector.addElement( Color.MAGENTA.darker() );
	}

	void setPointsVector(Vector<Point> 	myVector)
	{
		this.pointsVector = myVector;
	}

	void setClusterVector(Vector<Cluster> cluster)
	{
		this.clusterVector = cluster;
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, getWidth() - BORDER_GAP, getHeight() - BORDER_GAP); // Draw x axis
		g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, BORDER_GAP, BORDER_GAP); // Draw y axis

		FontMetrics fm = g2.getFontMetrics();

		// This loop will create hatch marks for x axis
		for (int i = 0; i < X_HATCH_CNT; i++)
		{
			int x0 = (i + 1) * (getWidth() - BORDER_GAP * 2) / X_HATCH_CNT + BORDER_GAP;
			int x1 = x0;
			int y0 = getHeight() - BORDER_GAP;
			int y1 = y0 - GRAPH_POINT_WIDTH;
			g2.drawLine(x0, y0, x1, y1);
			String value = Integer.toString(i+1);
			g2.drawString(value, x0 - (fm.stringWidth(value) / 2), y0 + fm.getAscent());
		}

		// This loop will create hatch marks for y axis
		for (int i = 0; i < Y_HATCH_CNT; i++)
		{
			int x0 = BORDER_GAP;
			int x1 = GRAPH_POINT_WIDTH + BORDER_GAP;
			int y0 = getHeight() - (((i + 1) * (getHeight() - BORDER_GAP * 2)) / Y_HATCH_CNT + BORDER_GAP);
			int y1 = y0;
			g2.drawLine(x0, y0, x1, y1);
			String value = Integer.toString(i+1);
			g2.drawString(value, x0 - fm.stringWidth(value), y0 + (fm.getAscent() / 2));
		}

      	xScale = ((double) getWidth() - 2 * BORDER_GAP) / (X_HATCH_CNT);
      	yScale = ((double) getHeight() - 2 * BORDER_GAP) / (Y_HATCH_CNT);

      	if( pointsVector != null )
      	{
			// This loop will draw all the initial points in black color
			for(int i=0; i< pointsVector.size(); i++ )
			{
				Point myPoint = pointsVector.get(i);
				drawPoints(myPoint.x,myPoint.y,Color.BLACK.darker(),GRAPH_POINT_WIDTH);
			}
		}

		if ( clusterVector != null )
		{
			// This loop will draw the cluster mean and the data points of that cluster with a unique color
			for(int j=0; j< clusterVector.size(); j++ )
			{
				Cluster cl =  clusterVector.get(j);
				int meanX = (int)Math.round(cl.meanPoint.x);
				int meanY = (int)Math.round(cl.meanPoint.y);

				// Draw the cluster mean. It's width will be little bigger than the data points
				drawPoints( meanX, meanY, colorsVector.get(j), CLUSTER_POINT_WIDTH );

				for(int i=0; i< cl.pointsVector.size(); i++ )
				{
					Point myPoint = cl.pointsVector.get(i);

					// Draw all the data points in a cluster. Use a unique color for that cluster
					drawPoints( myPoint.x, myPoint.y, colorsVector.get(j),GRAPH_POINT_WIDTH );
				}
			}
		}
	}

	// This function will draw a point in the graph using the x and y coordinates of the point
	void drawPoints(int x, int y,Color pointColor,int GRAPH_POINT_WIDTH)
	{
		int x1 = (int) (x * xScale + BORDER_GAP)-(GRAPH_POINT_WIDTH/2);
		int y1 = (int) ((Y_HATCH_CNT - y) * yScale + BORDER_GAP)-(GRAPH_POINT_WIDTH/2);

		g2.setColor(pointColor);
		g2.fillOval(x1, y1,GRAPH_POINT_WIDTH, GRAPH_POINT_WIDTH);
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(PANEL_WIDTH, PANEL_HEIGHT);
	}

}// end of class
/**
 * @author Hanif Mirza, Fairmont State University, May 2016
 * Class Name: KNN.java( Main Frame Class)
 * Date: 06 May, 2016
 *
 * Program Description: This program has been written to animate the k-means clustering in a two-dimensional space.
 * This main frame has a spinner to select the value k anywhere between 2 to 5. Also, it has a slider to select the
 * number of points anywhere between 20 to 400. At the begining, when the user clicks initiatorButton, the program
 * display the points in black color. Then when the user clicks the nextButton, the mean and the points in each
 * cluster will be displayed in a unique color. The user can repeatedly click the nextButton until no more clustering
 * is possible.
 *
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.*;
import java.awt.geom.Point2D;


class KNN extends JFrame implements ActionListener,ChangeListener
{
	public static void main(String[] x)
	{
		new KNN();
	}

	JButton 			initiatorButton, nextButton;
	JLabel 				kValueLabel, pointLabel;
	JSlider 			pointSlider;
	JSpinner 			kSpinner;
	SpinnerModel		kSpinnerListModel;
	GraphPanel			myGraphPanel;
	Vector<Point> 		pointsVector = new Vector<Point>();
	Vector<Cluster> 	clusterVector = new Vector<Cluster>();
	boolean				isInitiated = false;
	boolean				isFirstIteration = false;
	boolean 			isValid = false;
	int					MIN_K = 2, MAX_K = 5; // Minimum and maximum values of k are 2 & 5
	int					K_VALUE;
	int					MAX_X_VALUE = 20, MAX_Y_VALUE = 20; // Maximum x axis and y axis values is 20
	int					MIN_POINTS = 20, MAX_POINTS = 400; // Minimum and maximum number of points are 20 & 400
	int					POINTS_COUNT = 0;

    KNN()
    {
		myGraphPanel = new GraphPanel();
		myGraphPanel.setBounds(0, 0, myGraphPanel.PANEL_WIDTH, myGraphPanel.PANEL_HEIGHT);

		kValueLabel = new JLabel("K : ");
		kValueLabel.setBounds(680, 40, 20, 20);

		kSpinnerListModel = new SpinnerNumberModel(3,MIN_K,MAX_K,1);
		kSpinner = new JSpinner(kSpinnerListModel);
		kSpinner.addChangeListener(this);
		kSpinner.setEditor(new JSpinner.DefaultEditor(kSpinner)); // make the spinner non editable from the keyboard
		kSpinner.setBounds(710,40, 40, 25);

		initiatorButton = new JButton("Initiate");
		initiatorButton.addActionListener(this);
		initiatorButton.setBounds(680, 450, 80, 30);

		nextButton = new JButton("Next");
		nextButton.addActionListener(this);
		nextButton.setEnabled(false);
		nextButton.setBounds(680, 500, 80, 30);

		pointLabel = new JLabel("Number of points: ");
		pointLabel.setBounds(20, 590, 120, 20);

    	pointSlider = new JSlider(JSlider.HORIZONTAL, MIN_POINTS, MAX_POINTS, MIN_POINTS);
    	pointSlider.setBounds(140, 585, 500, 50);

    	pointSlider.setMinorTickSpacing(10);
    	pointSlider.setMajorTickSpacing(40);
    	pointSlider.setPaintTicks(true);
    	pointSlider.setPaintLabels(true);
    	pointSlider.addChangeListener(this);

		setLayout(null);
		add(myGraphPanel);
		add(kValueLabel);
		add(kSpinner);
		add(initiatorButton);
		add(nextButton);
		add(pointLabel);
		add(pointSlider);

        setupMainFrame();
    }

    public void actionPerformed(ActionEvent e)
    {
		if(e.getSource() == initiatorButton)
		{
			initiateTheGraph();
		}

		else if(e.getSource() == nextButton)
		{
			if ( isFirstIteration )
			{
				doFirstIteration(); // Perform the first iteration, when the user clicks the next button for the first time
			}
			else
			{
				doOtherIteration(); // Perform the other iterations, when the user clicks the next button for second time, third time, and so on
			}
		}
	}

	// This function will display all the initial points in black color when the user clicks initiatorButton
	void initiateTheGraph()
	{
		K_VALUE = (Integer)kSpinner.getValue();
		POINTS_COUNT = pointSlider.getValue();

		pointsVector.removeAllElements();

		Set<Point> 	set = new HashSet<Point>();
		Random 		position = new Random();
		Point 		myPoint;

		do
		{
			myPoint = new Point();
			myPoint.x = position.nextInt(MAX_X_VALUE)+1; // generate random integer (x coordinate) [1-20]
			myPoint.y = position.nextInt(MAX_Y_VALUE)+1;
			set.add(myPoint); // The set will contain all unique data points
		}
		while (set.size()< POINTS_COUNT );

		Iterator iterator = set.iterator();  // Create an iterator

		while (iterator.hasNext())
		{
			myPoint = (Point)iterator.next();
			pointsVector.addElement( myPoint ); // Store all the unique points from set to the pointsVector
		}

		this.remove(myGraphPanel);
		this.repaint();

		myGraphPanel = new GraphPanel();
		myGraphPanel.setPointsVector(pointsVector);
		myGraphPanel.setBounds(0, 0, myGraphPanel.PANEL_WIDTH, myGraphPanel.PANEL_HEIGHT);
		add(myGraphPanel);
		myGraphPanel.repaint(); // Repaint the JPanel to view the newly added points in black color

		nextButton.setEnabled(true);
		isFirstIteration = true; // Ready for the first iteration
	}

	/* This function will  display all the initial points in black color and the initial cluster mean
	   with a unique color when the user clicks nextButton for the first time */
	void doFirstIteration()
	{
		clusterVector.removeAllElements();

		int index = 0;

		// This loop with select K_VALUE number of initial means from pointsVector and create that many cluster object
		for(int i=0; i < K_VALUE; i++)
		{
			Point myPoint = pointsVector.get(index);
			double x = (double) myPoint.x;
			double y = (double) myPoint.y;

			clusterVector.addElement( new Cluster(x,y) ); // Construct a cluster object with initial mean and add it to the vector
			index = index + (POINTS_COUNT/K_VALUE);
		}

		this.remove(myGraphPanel);
		this.repaint();
		myGraphPanel = new GraphPanel();
		myGraphPanel.setPointsVector(pointsVector);
		myGraphPanel.setClusterVector(clusterVector);
		myGraphPanel.setBounds(0, 0, myGraphPanel.PANEL_WIDTH, myGraphPanel.PANEL_HEIGHT);
		add(myGraphPanel);
		myGraphPanel.repaint();

		isFirstIteration = false;
	}

	/* This function will  display the mean and the points in each cluster with a unique color when
	   the user clicks the nextButton for second time, third time, and so on. Also the nextButton
	   will be disabled when the clustering is done. */
	void doOtherIteration()
	{
		Cluster myCluster;

		// Remove all points from pointsVector in each cluster object
		for(int j=0; j< clusterVector.size(); j++ )
		{
			myCluster =  clusterVector.get(j);
			myCluster.pointsVector.removeAllElements();
		}

		// This loop will assign each point to the appropriate cluster by finding minimum distance between cluster mean and the point
		for(int i=0; i< pointsVector.size(); i++ )
		{
			Point point = pointsVector.get(i);

			double 		min = 9999.0;
			int 		position = 0;

			for(int j=0; j< clusterVector.size(); j++ )
			{
				myCluster = clusterVector.get(j);
				double distance = findDistance( point,myCluster.meanPoint );
				if (  distance < min )
				{
					min = distance;
					position = j; // Remember the cluster's position in clusterVector
				}
			}

			myCluster = clusterVector.get(position);
			myCluster.pointsVector.addElement( point );
		}

		Vector<Cluster> oldClusterVector = new Vector<Cluster>();

		// This loop will copy all the cluster objects from clusterVector to oldClusterVector
		for(int j=0; j< clusterVector.size(); j++ )
		{
			myCluster =  clusterVector.get(j);

			// Construct an oldCluster with mean points of myCluster object
			Cluster oldCluster = new Cluster( myCluster.meanPoint.x ,myCluster.meanPoint.y);

			for(int i=0; i< myCluster.pointsVector.size(); i++ )
			{
				Point myPoint = myCluster.pointsVector.get(i);

				// Copy all the points of each cluster to the pointsVector of oldCluster
				oldCluster.pointsVector.addElement( new Point( myPoint.x, myPoint.y )  );
			}
			oldClusterVector.addElement( oldCluster );
		}

		this.remove(myGraphPanel);
		this.repaint();
		myGraphPanel = new GraphPanel();
		myGraphPanel.setClusterVector(oldClusterVector); // Set the oldClusterVector to the view all the clusters in it
		myGraphPanel.setBounds(0, 0, myGraphPanel.PANEL_WIDTH, myGraphPanel.PANEL_HEIGHT);
		add(myGraphPanel);
		myGraphPanel.repaint(); // Repaint the JPanel to view the clusters in unique color

		boolean	isMatched = true;
		// This loop will recalculate the mean of each cluster
		for(int j=0; j< clusterVector.size(); j++ )
		{
			myCluster =  clusterVector.get(j);
			double totalX = 0.0, totalY = 0.0;

			for(int i=0; i< myCluster.pointsVector.size(); i++ )
			{
				Point myPoint = myCluster.pointsVector.get(i);
				totalX = totalX + (double)myPoint.x;
				totalY = totalY + (double)myPoint.y;
			}
			double newMeanPointX = (double) ( totalX / myCluster.pointsVector.size() );
			double newMeanPointY = (double) ( totalY / myCluster.pointsVector.size() );

			if ( newMeanPointX != myCluster.meanPoint.x || newMeanPointY != myCluster.meanPoint.y )
			{
				isMatched = false; // The new recalculated mean and the old mean didn't match
			}

			myCluster.meanPoint.x = newMeanPointX;
			myCluster.meanPoint.y = newMeanPointY;
		}

		if ( isMatched )
		{
			nextButton.setEnabled(false); // Disable the next button, if all new means and old means are the same
			JOptionPane.showMessageDialog(this,"Clustering is done. Please initiate again");
		}
	}

	// This function will return the Euclidean distance between any point and the mean point of the cluster
	double findDistance( Point point, Point2D.Double point2D)
	{
		double x1 = (double) point.x;
		double y1 = (double) point.y;

		double x2 = point2D.x;
		double y2 = point2D.y;

		double d1 = x2 - x1;
		double d2 = y2 - y1;

		double result = Math.pow(d1,2) + Math.pow(d2,2);
		result = Math.sqrt( result );
		return result;
	}

	// This function will be called when the user made any change to the pointSlider and kSpinner
	public void stateChanged(ChangeEvent ce)
	{
		nextButton.setEnabled(false);

		POINTS_COUNT = pointSlider.getValue();
		K_VALUE = (Integer)kSpinner.getValue();
	}

	// This function will setup and view the main frame
	void setupMainFrame()
	{
		Toolkit    tk;
		Dimension   d;
		tk = Toolkit.getDefaultToolkit();
		d = tk.getScreenSize();
		setSize( 780, 660);
		//setLocation((d.width/4 + d.width/4)/4, (d.height/2 + d.width / 6) / 8);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Kth Nearest Neighbor");
		setVisible(true);
	}

}// end of class
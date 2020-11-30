import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * GUI
 * @author brandonli
 *
 */
public class PerspectiveProjection extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5774907051842299386L;
	double[][]data;//Matrix of world coordinates
	double[][]eye; //matrix for eye coordinates
	int numOfLines;
	double ecsX, ecsY,ecsZ;
	double d = 2.5;
	double s = 2;
	
	public PerspectiveProjection(String file) {
		
		try {
			inputLines(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setLayout(new BorderLayout());

		//Sets up canvas and toolbar
		//Sets up transformer and original perspective data
		GraphicsTransformation transformer = new GraphicsTransformation();
		eye = transformer.toEyeCoordinates(data);
		System.out.print("Eye" + eye[0][2]);
		
		double[][] perspective = transformer.getPerspective(eye);
		//System.out.print("Perspective" + perspective[0][2]);

		PerspectiveCanvas canvas = new PerspectiveCanvas(perspective);
		this.add(canvas);
		this.add(canvas, BorderLayout.CENTER);
		
		InputToolBar toolbar = new InputToolBar(eye, canvas);
		add(toolbar, BorderLayout.PAGE_START);

	}
	
	
	/**
	 * Input lines into matrix
	 * @return number of lines
	 * @throws FileNotFoundException 
	 */
	public void inputLines(String filename) throws FileNotFoundException { 
        ArrayList<int[]> datalinesList = new ArrayList<>();
		File file = new File(filename);
        Scanner inFile = new Scanner(file, "UTF-8");
		while(inFile.hasNext()){
			datalinesList.add(new int[] {inFile.nextInt(), inFile.nextInt(), inFile.nextInt(), inFile.nextInt(), inFile.nextInt(), inFile.nextInt()});
		
		}
		inFile.close();
		
		//convert array list to matrix
		data = new double[datalinesList.size()][6];
		for(int x = 0; x < datalinesList.size(); x++) {
			System.out.print("Line " + x + ":");
			for(int y = 0; y < 6; y++) {
				data[x][y] = datalinesList.get(x)[y];
				System.out.print( " " +data[x][y] );
			}
			System.out.println( "" );

		}
		numOfLines = datalinesList.size();	
	}
	
	/**
	 * Gets number of lines
	 * @return
	 */
	public int getCount() {
		return numOfLines;
	}
	
	/**
	 * Sets number of lines
	 * @param num
	 */
	public void setCount(int num) {
		numOfLines = num;
	}
	
	/**
	 * gets data
	 * @return
	 */
	public double[][] getData(){
		return data;
	}
	
	/**
	 * Toolbar used to handle user input for transformations
	 * @author brandonli
	 *
	 */
	@SuppressWarnings("serial")
	public class InputToolBar extends JToolBar{
		GraphicsTransformation transformer;
		PerspectiveCanvas canvas;
		double[][] eye;
		boolean t, r, s;
		
		public InputToolBar(double[][] eye, PerspectiveCanvas canvas) {
			super();
			this.eye = eye;
			this.canvas = canvas;
			t = false;
			r = false;
			s = false;
			//Sets up transformer and original perspective data
			transformer = new GraphicsTransformation();
			//eye = transformer.toEyeCoordinates(data);

			setUpPanel();
		}
			
		public void setUpPanel() {
			JButton tran = new JButton("Translate");
			tran.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dialog("t");
					}   	       
				});
				this.add(tran);

				JButton rotate = new JButton("Rotate");
				rotate.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dialog("r");				}          
				});
				this.add(rotate);
				
				JButton scale = new JButton("Scale");
				scale.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dialog("s");				}          
				});
				this.add(scale);
				
				JButton apply = new JButton("Apply");
				apply.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//gets new eye coordinates
						eye = transformer.applyTransformation(eye, s, t, r );	
						double[][] perspective = transformer.getPerspective(eye);
						canvas.updateLines(perspective);
						canvas.repaint();
					}          
				});
				this.add(apply);
			
		}
			
			
				
				public void dialog(String option) {
					JPanel myPanel = new JPanel();
					switch(option)  {
						case "t":
							JTextField txField = new JTextField(5);
							JTextField tyField = new JTextField(5);
							JTextField tzField = new JTextField(5);

							 myPanel.add(new JLabel("tx:"));
							 myPanel.add(txField);
							 myPanel.add(Box.createHorizontalStrut(15)); // a spacer
							 myPanel.add(new JLabel("ty:"));
							 myPanel.add(tyField);
							 myPanel.add(Box.createHorizontalStrut(15)); // a spacer
							 myPanel.add(new JLabel("tz:"));
							 myPanel.add(tzField);
							 
							 int resultt = JOptionPane.showConfirmDialog(null, myPanel,
									 "Please Enter TX, TY and TZ Values", JOptionPane.OK_CANCEL_OPTION);
							 if (resultt == JOptionPane.OK_OPTION) {
								transformer.translate(Integer.parseInt(txField.getText()),Integer.parseInt(tyField.getText()), Integer.parseInt(tzField.getText()));
								t = true;
								 //mult
							 }
							break;	
						case "r":
							JTextField rxField = new JTextField(5);
							JTextField ryField = new JTextField(5);
							JTextField rzField = new JTextField(5);
							 myPanel.add(new JLabel("rx:"));
							 myPanel.add(rxField);
							 myPanel.add(Box.createHorizontalStrut(15)); // a spacer
							 myPanel.add(new JLabel("ry:"));
							 myPanel.add(ryField);
							 myPanel.add(Box.createHorizontalStrut(15)); // a spacer
							 myPanel.add(new JLabel("rz"));
							 myPanel.add(rzField);
							 
							 int resultr = JOptionPane.showConfirmDialog(null, myPanel,
									 "Please Enter RX, RY, RX angle values", JOptionPane.OK_CANCEL_OPTION);
							 if (resultr == JOptionPane.OK_OPTION) {
								 transformer.rx(Integer.parseInt(rxField.getText()));
								 transformer.ry(Integer.parseInt(ryField.getText()));
								 transformer.rz(Integer.parseInt(rzField.getText()));
								 r = true;

							 }
							break;
							
						case "s":
							JTextField sxField = new JTextField(5);
							JTextField syField = new JTextField(5);
							JTextField szField = new JTextField(5);

							 myPanel.add(new JLabel("sx:"));
							 myPanel.add(sxField);
							 myPanel.add(Box.createHorizontalStrut(15)); // a spacer
							 myPanel.add(new JLabel("sy:"));
							 myPanel.add(syField);
							 myPanel.add(Box.createHorizontalStrut(15)); // a spacer
							 myPanel.add(new JLabel("sz:"));
							 myPanel.add(szField);
							 int resultsc = JOptionPane.showConfirmDialog(null, myPanel,
									 "Please Enter SX, SY and SZ Values", JOptionPane.OK_CANCEL_OPTION);
							 if (resultsc == JOptionPane.OK_OPTION) {
								 transformer.scale(Integer.parseInt(sxField.getText()),Integer.parseInt(syField.getText()),Integer.parseInt(szField.getText()));
								 s = true;
								 //MULT
							 }
							break;
					}
				}
		}
	
	public static void main(String args[]) {
		System.out.println("Input file of your image coordinates: ");
		Scanner scanner = new Scanner(System.in);
		
		PerspectiveProjection gui = new PerspectiveProjection(scanner.nextLine());
		scanner.close();
		
		JFrame frame = new JFrame("Perspective Projection");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(0, 0 , 1000, 1000);
		frame.add(gui);
		frame.setVisible(true);
	}
	

}

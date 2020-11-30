import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;


/**
 * Used to display pixels
 * @author brandonli
 *
 */
@SuppressWarnings("serial")
public class PerspectiveCanvas extends Canvas{

	static boolean accept;
	double[][]datalines; 
	int numOfLines;
		
	public PerspectiveCanvas() {
			numOfLines = 0;
			this.setSize(500, 500);
		}
		public PerspectiveCanvas(double[][] data) {
			datalines = data;
			numOfLines = datalines.length;
		}
		
		public void updateLines(double[][]data) {
			datalines = data;
		}
		

		/**
		 * Paints the canvas with all the lines using Bresenham
		 */
		public void paint(Graphics g) {
			//PerspectiveData(datalines);
			double[][] array = datalines;
			double[] clippingArray;

			int x1, y1, x2, y2;
			int dx, dy, E, ystep;
			
			//System.out.println("NUMBER OF LINES:   " + num );
			for(int i=0; i<array.length; i++) {
				
				x1 = (int) array[i][0];
				y1 = (int) array[i][1];
				x2 = (int) array[i][2];
				y2 = (int) array[i][3];
				
			//	clippingArray = Clipping(x1, y1, x2, y2);
			//	boolean acceptValue = getAccept();
			//	if(acceptValue == false) {
			//		continue;
				//}

			//	x1 = (int) clippingArray[0];
				//y1 = (int) clippingArray[1];
			//	x2 = (int) clippingArray[2];
			//	y2 = (int) clippingArray[3];

				boolean swap = Math.abs(y2-y1) > Math.abs(x2-x1);
				if(swap) { //if slope is greater than or equal to 1
					int temp = x1;
					x1 = y1;
					y1 = temp;
					temp = x2;
					x2 = y2;
					y2 = temp;
				}
				if(x1>x2) { //switches starting point and end point
					//swap x1 and x2, swap y1 and y2
					int temp = x1;
					x1 = x2;
					x2 = temp;
					temp = y1;
					y1 = y2;
					y2 = temp;
				}
				dx = x2 - x1;
				dy = Math.abs(y2 - y1);
				E = dx/2;
				int y = y1;

				if(y1<y2) { //if positive slope
					ystep = 1;
				}
				else { //if negative slope
					ystep = -1;
				}

				System.out.println("In Canvas with x1 y x1 x2" + x1 + " " + y + " " +x2 + "" + x2);
				for(int x = x1; x<x2;x++) {

					if(swap) {
						g.fillOval(y,x,3,3);
					}
					else {
						g.fillOval(x, y, 3, 3);
					}
					E = E-dy;
					if(E<0) {
						y = y+ystep;
						E = E+dx;
					}

				}
			}
		} //paint()
		
		private boolean getAccept() {
			// TODO Auto-generated method stub
			return accept;
		}
		
		
		private static int bitCode(int x, int y) {
			int code = 0;   
			if(x<0) code |= 1;
			else if(x>500) code |= 2;
			if(y<0) code |= 8;
			else if(y>(500-1)) code |= 4;
			return code;
		}
		
		private static double[] Clipping(int x1, int y1, int x2, int y2) {
			double[] newValues = new double[4];
			boolean visible = false;
			boolean done = false;
			int x = 0, y = 0;
			int code1, code2, codeOut;

			code1 = bitCode(x1, y1);
			code2 = bitCode(x2, y2);

			do{
				if((code1 | code2) == 0) {
					visible = true;
					done = true;
				}
				else if((code1 & code2) != 0) {
					done = true;
				}
				else {
					//First Step
					if(code1 != 0) {
						codeOut = code1;
					}
					else {
						codeOut = code2;
					}

					//Second Step
					if((codeOut & 8) != 0) {x = x1 + (x2 - x1)*(0 - y1)/(y2 - y1); y = 0;}
					else if((codeOut & 4) != 0) { x = x1 + (x2 - x1)*(500 - y1)/(y2 - y1); y = (500-1); }
					else if((codeOut & 2) != 0) {y = y1 + (y2 - y1)*(500 - x1)/(x2 - x1); x = (500-1);}
					else if((codeOut & 1) != 0) {y = y1 + (y2 - y1)*(0 - x1)/(x2 - x1); x = 0;}

					//Third Step
					if(codeOut == code1) {x1 = x; y1 = y; code1 = bitCode(x1, y1);}
					else { x2 = x; y2 = y; code2 = bitCode(x2, y2); }
				}
			}while(done==false);
			
			if(visible == true) {
				setTrue();
				newValues[0] = x1;
				newValues[1] = y1;
				newValues[2] = x2;
				newValues[3] = y2;
			}
			else{
				setFalse();
			}
			
			return newValues;
		}

		private static void setFalse() {
			// TODO Auto-generated method stub
			accept = false;
		}

		private static void setTrue() {
			accept = true;
			// TODO Auto-generated method stub
			
		}
	
	
}

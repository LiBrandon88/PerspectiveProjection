import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Used for transforming 3d images
 * Includes lines clipping
 * Copied from my previous assignment; 
 * @author brandonli
 *
 */
public class GraphicsTransformation{
	int[][]datalines;//Matrix 
	int vx0, vy0, vx1, vy1;
	int[] windowSpecs;
	static boolean accept;
	double[][] scaleMatrix;
	double[][] transMatrix;
	double[][] rxMatrix, ryMatrix, rzMatrix;

	/**
	 * Constructor: calls input lines using file name given
	 * @param filename
	 */
	public GraphicsTransformation() {
		
	}
	
	/**
	 * Applies the transformation matrix to datalines
	 * Used github as a reference.
	 */
	public double[][] applyTransformation(double[][] eye, boolean scale, boolean translate, boolean rotate) {
		double[][] tMatrix = null;
		
		//Initialize tMatrix
		if(scale) {
			tMatrix = scaleMatrix;
			scale = false;
		}
		else if(translate) {
			tMatrix = transMatrix;
			translate = false;
		}
		else if(rotate) { 
			tMatrix = multiply(rzMatrix,multiply(rxMatrix, ryMatrix));
			rotate = false;
		}
		else {
			return eye;
		}
		
		boolean multiply = true;
		while(multiply) {
			if(translate) {
				tMatrix = multiply(tMatrix, transMatrix);
				translate = false;
			}
			if(rotate) { 
				double[][] temp = multiply(rzMatrix,multiply(rxMatrix, ryMatrix));
				tMatrix = multiply(tMatrix, temp);
				rotate = false;
			}

			if(scale == false && translate == false && rotate == false) {
				multiply = false;
			}
		}
		
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				tMatrix[i][j] = Math.floor(tMatrix[i][j]*100000)/100000;
			}
		}

		double[][]newEyeData = new double[eye.length][6];
		for(int i = 0; i<eye.length; i++) {
			newEyeData[i][0] = eye[i][0]*tMatrix[0][0] + eye[i][1]*tMatrix[1][0] + eye[i][2]*tMatrix[2][0] + tMatrix[3][0];
			newEyeData[i][1] = eye[i][0]*tMatrix[0][1] + eye[i][1]*tMatrix[1][1] + eye[i][2]*tMatrix[2][1] + tMatrix[3][1];
			newEyeData[i][2] = eye[i][0]*tMatrix[0][2] + eye[i][1]*tMatrix[1][2] + eye[i][2]*tMatrix[2][2] + tMatrix[3][2];
			newEyeData[i][3] = eye[i][3]*tMatrix[0][0] + eye[i][4]*tMatrix[1][0] + eye[i][5]*tMatrix[2][0] + tMatrix[3][0];
			newEyeData[i][4] = eye[i][3]*tMatrix[0][1] + eye[i][4]*tMatrix[1][1] + eye[i][5]*tMatrix[2][1] + tMatrix[3][1];
			newEyeData[i][5] = eye[i][3]*tMatrix[0][2] + eye[i][4]*tMatrix[1][2] + eye[i][5]*tMatrix[2][2] + tMatrix[3][2];
		}
		return newEyeData;
	}
		
	
	
	/**
	 * Matrix Multiplication 
	 * @param a matrix
	 * @param b transformation matrix
	 * @return results matrix
	 */
	public double[][] multiply(double[][] a, double[][] b) {
		double[][] matrix = new double[4][4];
		
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				for(int k = 0; k < 4; k++) {
					matrix[i][j] += a[i][k]*b[k][j]; 
				}
			}
		}
		return matrix;
	}
	
	/**
	 * Perform a basic translate
	 * @param tx translate value for x
	 * @param ty translate value for y
	 * @param tz translate value for z
	 */
	public double[][] translate(int tx, int ty, int tz) {
		double[][] matrix = new double[4][4];
		matrix[0][0] = 1;
		matrix[0][1] = 0;
		matrix[0][2] = 0;
		matrix[0][3] = 0;
		
		matrix[1][0] = 0;
		matrix[1][1] = 1;
		matrix[1][2] = 0;
		matrix[1][3] = 0;

		matrix[2][0] = 0;
		matrix[2][1] = 0;
		matrix[2][2] = 1;
		matrix[2][3] = 0;

		matrix[3][0] = tx;
		matrix[3][1] = ty;
		matrix[3][2] = tz;
		matrix[3][3] = 1;

		transMatrix = matrix;
		return transMatrix;
	}
	
	/**
	 * Perform a basic scale
	 * @param sx scale value for x
	 * @param sy scale value for y
	 * @param sz scale value for z
	 */
	public void scale(int sx, int sy, int sz) {
		double[][] matrix = new double[4][4];

		matrix[0][0] = sx;
		matrix[0][1] = 0;
		matrix[0][2] = 0;
		matrix[0][3] = 0;
		
		matrix[1][0] = 0;
		matrix[1][1] = sy;
		matrix[1][2] = 0;
		matrix[1][3] = 0;

		matrix[2][0] = 0;
		matrix[2][1] = 0;
		matrix[2][2] = sz;
		matrix[2][3] = 0;
		
		matrix[3][0] = 0;
		matrix[3][1] = 0;
		matrix[3][2] = 0;
		matrix[3][3] = 1;
		
		scaleMatrix = matrix;
	}
	
	/**
	 * Perform a rotate on x
	 * @param angle
	 */
	public double[][] rx(int a) {
		double[][] matrix = new double[4][4];
		double angle = Math.toRadians(a);
		double cos = Math.floor(Math.cos(angle)*100000)/100000;
		double sin =  Math.floor(Math.sin(angle)*100000)/100000;

		matrix[0][0] = 1;
		matrix[0][1] = 0;
		matrix[0][2] = 0;
		matrix[0][3] = 0;
		
		matrix[1][0] = 0;
		matrix[1][1] = cos;
		matrix[1][2] = sin;
		matrix[1][3] = 0;

		matrix[2][0] = 0;
		matrix[2][1] = -sin;
		matrix[2][2] = cos;
		matrix[2][3] = 0;

		matrix[3][0] = 0;
		matrix[3][1] = 0;
		matrix[3][2] = 0;
		matrix[3][3] = 1;
		
		rxMatrix = matrix;
		return matrix;
	}
	
	/**
	 * Perform a rotate on x
	 * @param angle
	 */
	public void ry(int a) {
		double[][] matrix = new double[4][4];
		double angle = Math.toRadians(a);
		double cos = Math.floor(Math.cos(angle)*100000)/100000;
		double sin =  Math.floor(Math.sin(angle)*100000)/100000;
		
		matrix[0][0] = cos;
		matrix[0][1] = 0;
		matrix[0][2] = -sin;
		matrix[0][3] = 0;
		
		matrix[1][0] = 0;
		matrix[1][1] = 1;
		matrix[1][2] = 0;
		matrix[1][3] = 0;

		matrix[2][0] = sin;
		matrix[2][1] = 0;
		matrix[2][2] = cos;
		matrix[2][3] = 0;

		matrix[3][0] = 0;
		matrix[3][1] = 0;
		matrix[3][2] = 0;
		matrix[3][3] = 1;
		
		ryMatrix = matrix;
	}
	
	/**
	 * Perform a rotate on z
	 * @param angle
	 */
	public void  rz(int a) {
		double[][] matrix = new double[4][4];
		double angle = Math.toRadians(a);
		double cos = Math.floor(Math.cos(angle)*100000)/100000;
		double sin =  Math.floor(Math.sin(angle)*100000)/100000;
		matrix[0][0] = cos;
		matrix[0][1] = sin;
		matrix[0][2] = 0;
		matrix[0][3] = 0;
		
		matrix[1][0] = -sin;
		matrix[1][1] = cos;
		matrix[1][2] = 0;
		matrix[1][3] = 0;

		matrix[2][0] = 0;
		matrix[2][1] = 0;
		matrix[2][2] = 1;
		matrix[2][3] = 0;

		matrix[3][0] = 0;
		matrix[3][1] = 0;
		matrix[3][2] = 0;
		matrix[3][3] = 1;
		
		rzMatrix = matrix;
	}
	
	/**
	 * Converts the world coordinates to the Eye Coordinate
	 * Code snippets for conversion from author below
	 * @author yuheetae via Github
	 * @param world
	 * @return
	 */
	public double[][] toEyeCoordinates(double[][] data) {
		double[][] t1 = translate(1, 1, 1); //used when ecx are variables and not set.
		double[][] t2 = rx(-90);
		double x = 1, y = 1, z = 1;
		double cos = y/Math.sqrt(Math.pow(y, 2) + Math.pow(x, 2));
		double sin = x/Math.sqrt(Math.pow(y, 2) + Math.pow(x, 2));
		double[][] t3 = {{-cos, 0,  sin, 0},
						 {  0,  1,   0,  0},	
						 {-sin, 0, -cos, 0},
						 {  0,  0,   0,  1}};
		double cos2 = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))/Math.sqrt(Math.pow(z, 2) + Math.pow(x, 2) + Math.pow(y, 2));
		double sin2 = z/Math.sqrt(Math.pow(z, 2) + Math.pow(x, 2) + Math.pow(y, 2));
		double[][] t4 = {{1,   0,    0,   0},
				 		 {0,  cos2, sin2, 0},	
				 		 {0, -sin2, cos2, 0},
				 		 {0,   0,    0,   1}};
		
		double[][] t5 = {{1,  0,  0,  0},
						 {0,  1,  0,  0},
						 {0,  0, -1,  0},
						 {0,  0,  0,  1}};
		
		
		double[][] result = multiply(t1,t2);
		System.out.println("result:" + result[0][0]);
		result = multiply(result,t3);
		System.out.println("result:" + result[0][0]);
		result = multiply(result,t4);
		System.out.println("result:" + result[0][0]);
		result = multiply(result,t5);
		System.out.println("result:" + result[0][0]);

		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				result[i][j] = Math.floor(result[i][j]*100000)/100000;
			}
		}
		System.out.println("In ToEye " + data[0][0]);
		double[][]eye = new double[data.length][6];
		for(int i = 0; i<data.length; i++) {
			System.out.println("In ToEye " + data[i][0]);

			eye[i][0] = data[i][0]*result[0][0] + data[i][1]*result[1][0] + data[i][2]*result[2][0] + result[3][0];
			System.out.println("In ToEye " + eye[i][0]);

			eye[i][1] = data[i][0]*result[0][1] + data[i][1]*result[1][1] + data[i][2]*result[2][1] + result[3][1];
			eye[i][2] = data[i][0]*result[0][2] + data[i][1]*result[1][2] + data[i][2]*result[2][2] + result[3][2];
			eye[i][3] = data[i][3]*result[0][0] + data[i][4]*result[1][0] + data[i][5]*result[2][0] + result[3][0];
			eye[i][4] = data[i][3]*result[0][1] + data[i][4]*result[1][1] + data[i][5]*result[2][1] + result[3][1];
			eye[i][5] = data[i][3]*result[0][2] + data[i][4]*result[1][2] + data[i][5]*result[2][2] + result[3][2];
			System.out.println("In eye loop " + eye[0][0]);

		}
		return eye;
	}
	
	/**
	 * Gets the perspective data
	 * Code snippets from author below
	 * @author yuheetae via Github
	 * @param eye
	 * @return
	 */
	public double[][] getPerspective(double[][] eye) {
		double[][] matrix = new double[eye.length][4];
		double D = 2.5;
		double S = 2;
		double Vsx = 500/2;
		double Vsy = 500/2;
		double Vcx = 500/2;
		double Vcy = 500/2;
		for(int i = 0; i<eye.length; i++) {
			matrix[i][0] = (D*eye[i][0])/(S*eye[i][2])*Vsx+Vcx;
			matrix[i][1] = (D*eye[i][1])/(S*eye[i][2])*Vcy+Vcy;
			matrix[i][2] = (D*eye[i][3])/(S*eye[i][5])*Vsx+Vcx;
			matrix[i][3] = (D*eye[i][4])/(S*eye[i][5])*Vcy+Vcy;
			System.out.println("In pers " + matrix[i][0]);
			System.out.println("In pers " + matrix[i][1]);
			System.out.println("In pers " + matrix[i][2]);
			System.out.println("In pers " + matrix[i][3]);

		}
		return matrix;
	}
	

}
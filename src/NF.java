import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;


public class NF {
	
	int deep = 250;
	int width = 2000;
	int height = 2000;
	double X = 		0;
	double Y = 		0;
//
//	double X = 		0.864213726880;
//	double Y = 1-	0.0000000001435;
	double size = 3;//* Math.pow(0.8,125);
	
	double x_start;
	double x_end;
	double y_start;
	double y_end;
	
	int function = 1;
	
	double toleranz = 0.001;
	
	double bright = 0;
	class point{
		double x;
		double y;
		int colorR;
		int colorG;
		int colorB;
		point(double nx,double ny, int ncolR, int ncolG, int ncolB){
			x = nx;
			y = ny;
			colorR = ncolR;
			colorG = ncolG;
			colorB = ncolB;
		}
	}
	List<point> points;
	
	public int getColor(double x, double y, int iteration ){
		double br = (double)(deep - iteration)/deep;
		br =  Math.pow(br,bright);
		for(point p: points)
			if( x <= p.x + toleranz && x >= p.x - toleranz && y <= p.y + toleranz && y >= p.y - toleranz)
				return new Color((int)(p.colorR*br), (int)(p.colorG*br), (int)(p.colorB*br)).getRGB();
		return 0;
	}
	public class Complex{
		double r;
		double i;
		Complex(double nr, double ni){
			r = nr;
			i = ni;
		}
		Complex add(Complex wert){
			return new Complex(this.r + wert.r, this.i + wert.i );
		}
		Complex add(double re){
			return new Complex(this.r + re, this.i  );
		}
		Complex minus(Complex wert){
			return new Complex(this.r - wert.r, this.i - wert.i );
		}
	}

	public Complex mult(Complex wert1, Complex wert2){
		return new Complex(wert1.r*wert2.r -wert1.i*wert2.i, wert1.r*wert2.i + wert1.i*wert2.r );
	}
	public Complex mult(Complex wert1, double mu){
		return new Complex(wert1.r*mu, wert1.i*mu );
	}
	public Complex hoch2(Complex wert){
		return mult(wert,wert);
	}
	public Complex hoch3(Complex wert){
		return mult(hoch2(wert),wert);
	}
	public Complex hoch4(Complex wert){
		return hoch2(hoch2(wert));
	}
	public Complex hoch(Complex wert, int h){
		Complex temp = wert;
		for(; h > 1; h--)
			temp = mult(temp,wert);
		return temp;
	}
	
	public Complex devide(Complex wert1, Complex wert2){
		Complex temp = mult(wert1, new Complex(wert2.r, - wert2.i));
		double div = wert2.r*wert2.r+wert2.i*wert2.i;
		temp.i /= div;
		temp.r /= div;
		return temp;
	}
	
	public Complex iterate(Complex wert){
		//return wert.minus(devide(hoch3(wert).add(new Complex(-1,0)), hoch2(wert)));
		switch(function){
		case 0://x^2 -1
			return devide(hoch2(wert).add(1), mult(wert,2));
		case 1://x^3 -1
			return devide(mult(hoch3(wert),new Complex(2,0)).add(new Complex(1,0)), mult(hoch2(wert),new Complex(3,0)));
		case 2://x^4 -1
			return devide(mult(hoch(wert,4),3).add(1), mult(hoch(wert,3),4));
		case 3://x^5 -1
			return devide(mult(hoch(wert,5),4).add(1), mult(hoch(wert,4),5));
		case 5://z^3 - 2*z + 2 Falle
			return devide(mult(hoch(wert,3),2).add(-2), mult(hoch(wert,2),3).add(-2));
		case 6://z^3 + (-0.7+1.64*i)*z - (0.3 + 1.64*i)
			return devide(mult(hoch(wert,3),2).add(new Complex(0.3, 1.64)), mult(hoch(wert,2),3).add(new Complex(-0.7, 1.64)));
		case 7://z^5 + (5+2i)*z^3 - 2-i 
			return devide(mult(hoch(wert,5),4).add(mult(hoch(wert,3),new Complex(10,4))).add(new Complex(2, 1)), mult(hoch(wert,4),5).add(mult(hoch(wert,2),new Complex(15,6))));
		}
		return wert;
	}
	
	public int getPix(int x, int y, int deep){
		double newX = (x_end - x_start)/width * x + x_start;
		double newY = (y_end - y_start)/height * y + y_start;
		if(getColor(newX,newY, 0) != 0)
			return getColor(newX,newY, 0);
		Complex complex = new Complex(newX,newY);
		for(int i = 0; i < deep; i++){
			complex = iterate(complex);
			if(getColor(complex.r,complex.i, i) != 0)
				return getColor(complex.r,complex.i, i);
			//System.out.println(hoch3(complex).add(new Complex(-1,0)).r + " : " + hoch3(complex).add(new Complex(-1,0)).i);
			if(Double.isNaN(complex.r) || Double.isNaN(complex.i) || Double.isInfinite(complex.r) || Double.isInfinite(complex.i))
				return 0xffffffff;
		}
		
		return 0xffffffff;
	}
	
	
	
	public BufferedImage getMyImage(){
	BufferedImage img = new BufferedImage(width+1, height+1, BufferedImage.TYPE_INT_ARGB) ;
		for(int x = 0; x <= width; x++)
			for(int y = 0; y <= height; y++)
				img.setRGB(x, y, getPix(x,y,deep));
		return img;
	}
	
	public NF(){
		points = new LinkedList<point>();
		switch(function){
		case 0:
			points.add(new point(1,0,255, 0, 0));
			points.add(new point(-1,0,0, 255, 0));
			break;
		case 1:
			points.add(new point(1,0,255, 0, 0));
			points.add(new point(-0.5,0.866,0, 255, 0));
			points.add(new point(-0.5,-0.866,0, 0, 255));
			break;
		case 2:
			points.add(new point(1,0,255, 0, 0));
			points.add(new point(-1,0,255, 255, 0));
			points.add(new point(0,1,0, 255, 0));
			points.add(new point(0,-1,0, 255, 255));
			break;
		case 3:
			points.add(new point(1,0,			   255, 0, 0));
			points.add(new point(-0.80902,-0.58779,0, 255, 0));
			points.add(new point(-0.80902,+0.58779,0, 0, 255));	
			points.add(new point(0.30902,-0.95106, 0, 255, 255));
			points.add(new point(0.30902,+0.95106, 255, 255, 0));	
			break;
		case 5:
			points.add(new point(-1.7693,0,255, 0, 0));
			points.add(new point(0.88465,0.58974,0, 255, 0));
			points.add(new point(0.88465,-0.58974,0, 0, 255));
			break;
		case 6:
			points.add(new point(1,0,255, 0, 0));
			points.add(new point(0.391841, -0.919446,0, 255, 0));
			points.add(new point(-1.39184, 0.919446,0, 0, 255));
			break;
		case 7:
			points.add(new point(-0.418774,0.6408, 	255, 0, 0));
			points.add(new point(-0.400485,2.2692,	0, 	255, 0));
			points.add(new point(-0.378531,-0.6452,	0, 	0, 	255));	
			points.add(new point(0.474078,-2.2914, 	0, 	255, 255));
			points.add(new point(0.723712,+0.02656, 255, 255, 0));	
			break;
		}

		try {
		    // retrieve image
			for(int i = 0; i< 1; i++){
			
			x_start = X - size/2;
			x_end = X + size/2;
			y_start = Y - size/2;
			y_end = Y + size/2;
		    BufferedImage bi = getMyImage();

//			Complex comp =	new Complex (-1, 3);
//			System.out.println("start: "+comp.r + " : "+ comp.i);
//			bi.setRGB((int)((comp.r- x_start)/(x_end - x_start)*width), (int)((comp.i- y_start)/(y_end - y_start)*height), 0xff000000);
//			
//			for(int i = 0; i< 9; i++){
//				comp = iterate(comp);
//				bi.setRGB((int)((comp.r- x_start)/(x_end - x_start)*width), (int)((comp.i- y_start)/(y_end - y_start)*height), 0xff000000);
//				System.out.println(i+": "+comp.r + " : "+ comp.i);
//			}
			
		    File outputfile = new File(i + ".png");
//		    File outputfile = new File("output.png");
		    ImageIO.write(bi, "png", outputfile);
		    double mult = 0.80;
		    size *=mult;
//			System.out.println("done");
			System.out.println(i + "done");
			}
		    System.out.println("finish");
		} catch (IOException e) {
		    System.out.println("error");
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new NF();
	}

}

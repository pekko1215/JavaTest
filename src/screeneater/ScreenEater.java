package screeneater;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

/**
 * JFrameの表示枠の表示／非表示を切り替えるサンプルコードです。
 */
public class ScreenEater implements Runnable{
	JFrame mainFrame;
	Color startColor = new Color(100,100,100);
	Color nowColor = startColor;
	List<Point> celllist = new ArrayList<Point>();
	ColorTable nowtable;
	Point screenSize;
	BufferedImage image = null;
	DrawingCanvas drawCanvas;
	Thread th = null;
	public ScreenEater() {
		init();
		if(th == null){
			th = new Thread(this);
			th.start();
		}
	}

	public static void main(String[] args) {
		new ScreenEater();
	}

	public void init(){
		screenSize = new Point(Toolkit.getDefaultToolkit().getScreenSize().width,Toolkit.getDefaultToolkit().getScreenSize().height);
		java.awt.GraphicsEnvironment env = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
		java.awt.DisplayMode displayMode = env.getDefaultScreenDevice().getDisplayMode();
		mainFrame = new JFrame();
		// frame.add( component ); // 適当なコンポーネントを追加
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setBounds(0, 0,displayMode.getWidth(), displayMode.getHeight());
		mainFrame.setUndecorated(true);
		mainFrame.validate();


		try{
			image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		}catch(Exception e){
		}
		if(image!=null){
			System.out.println(image.toString());
		}

		drawCanvas = new DrawingCanvas(image);
		drawCanvas.addMouseListener(new muListener());
		mainFrame.add(drawCanvas);
		System.out.println(screenSize);
		celllist.add(MouseInfo.getPointerInfo().getLocation());
		nowtable = new ColorTable(screenSize.x,screenSize.y);
		nowtable.setTable(celllist.get(0),1);
		mainFrame.setVisible(true);
		
		
	}
	public void run(){
		while(th != null){
			try{
				Thread.sleep(56);
			}catch(InterruptedException ex){}
			check();
			delete();
			painting();
		}
		th = null;
	}
	public void sleep(long i){
		try {
		    Thread.sleep(i);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	}
	public void check(){
		int c = celllist.size();
		for(int index=0;index<c;index++){
			boolean mark = false;
			Point point = celllist.get(index);
			if(celllist.get(index).x>0&&nowtable.getTable(point.x-1,point.y)==0){
				mark=true;
				Color colortmp = new Color(image.getRGB(point.x-1,point.y));
				int luck = Math.abs(nowColor.getRed()-colortmp.getRed())+Math.abs(nowColor.getGreen()-colortmp.getGreen())+Math.abs(nowColor.getBlue()-colortmp.getBlue());
				luck/=1;
				luck++;
				if(new Random().nextInt(luck)==0){
					celllist.add(new Point(celllist.get(0).x-1,celllist.get(0).y));
				}
			}
			if(celllist.get(index).x<screenSize.x&&nowtable.getTable(point.x+1,point.y)==0){
				mark=true;
				Color colortmp = new Color(image.getRGB(point.x+1,point.y));
				int luck = Math.abs(nowColor.getRed()-colortmp.getRed())+Math.abs(nowColor.getGreen()-colortmp.getGreen())+Math.abs(nowColor.getBlue()-colortmp.getBlue());
				luck/=20;
				luck++;
				if(new Random().nextInt(luck)==0){
					celllist.add(new Point(celllist.get(0).x+1,celllist.get(0).y));
				}
			}if(celllist.get(index).y>0&&nowtable.getTable(point.x,point.y-1)==0){
				mark=true;
				Color colortmp = new Color(image.getRGB(point.x,point.y-1));
				int luck = Math.abs(nowColor.getRed()-colortmp.getRed())+Math.abs(nowColor.getGreen()-colortmp.getGreen())+Math.abs(nowColor.getBlue()-colortmp.getBlue());
				luck/=20;
				luck++;
				if(new Random().nextInt(luck)==0){
					celllist.add(new Point(celllist.get(0).x,celllist.get(0).y-1));
				}
			}if(celllist.get(index).y<screenSize.y&&nowtable.getTable(point.x,point.y+1)==0){
				mark=true;
				Color colortmp = new Color(image.getRGB(point.x,point.y+1));
				int luck = Math.abs(nowColor.getRed()-colortmp.getRed())+Math.abs(nowColor.getGreen()-colortmp.getGreen())+Math.abs(nowColor.getBlue()-colortmp.getBlue());
				luck/=20;
				luck++;
				if(new Random().nextInt(luck)==0){
					celllist.add(new Point(celllist.get(0).x,celllist.get(0).y+1));
				}
			}
			if(!mark){
				celllist.remove(index);
				c--;
				nowtable.setTable(point, 2);
			}
		}
		c = celllist.size();
		for(int index=0;index<c;index++){
			nowtable.setTable(celllist.get(index), 1);
			
		}
		System.out.println(c);
		
	}
	public void delete(){

	}
	public void painting(){
		for(int x=0;x<screenSize.x;x++){
			for(int y=0;y<screenSize.y;y++){
				if(nowtable.getTable(x, y)!=(byte)0){
					//System.out.println("x="+x+",y="+y);
					nowColor = new Color(new Random().nextInt(2)*255,new Random().nextInt(2)*255,new Random().nextInt(2)*255);
					image.setRGB(x, y, nowColor.getRGB());
				}
			}
		}
		//nowColor = new Color(new Random().nextInt(Color.WHITE.getRGB()));
		drawCanvas.setImage(image);
		drawCanvas.repaint();
		//drawCanvas.addMouseListener(new muListener());
		;
		//System.out.println(nowColor);*/
		
	}
}

class DrawingCanvas extends Canvas{
	BufferedImage image;
	DrawingCanvas(BufferedImage i){
		image = i;
	}
	DrawingCanvas(){
	}
	public void setImage(BufferedImage i){
		image = i;
	}

	public void paint(Graphics g){
		g.drawImage(image, 0, 0, this);
	}
}

class ColorTable {
	Byte tables[][];
	Point size;
	ColorTable(int x,int y){
		tables = new Byte[x][y];
		size = new Point(x,y);
		reset();
	}
	public void reset(){
		for(int x=0;x<size.x;x++){
			for(int y=0;y<size.y;y++){
				tables[x][y] = 0;
			}
		}
	}
	public void setTable(int x,int y,int p){
		tables[x][y] = (byte)(0x000000ff & (p));
	}

	public Byte getTable(int x,int y){
		return tables[x][y];
	}
	public void setTable(Point point,int p){
		tables[point.x][point.y] = (byte)(0x000000ff & (p));
	}

	public Byte getTable(Point point){
		return tables[point.x][point.y];
	}

}





/*
 * public class ScreenEater { public static void main(String[] args) { // TODO
 * 自動生成されたメソッド・スタブ main m = new main(); m.init(); }
 *
 *
 * }
 *
 * class main{ Graphics nowGraph; SplashScreen nowScreen;
 *
 * void init(){ BufferedImage image = null; nowScreen =
 * SplashScreen.getSplashScreen(); try { image = new
 * Robot().createScreenCapture(new
 * Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
 *
 * } catch (HeadlessException e) { // TODO 自動生成された catch ブロック
 * e.printStackTrace(); } catch (AWTException e) { // TODO 自動生成された catch ブロック
 * e.printStackTrace(); } Graphics gtmp = image.getGraphics(); Graphics gtmp2 =
 * nowScreen.createGraphics();
 *
 * gtmp2 = gtmp; nowScreen.update(); } }
 */
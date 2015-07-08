/*DamageText.java
 *Makes little words that float up and disappear.
 *That's all it does
 */

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DamageText {
	
	int x,y;
	String str;
	int time;
    Font font;
    Color c;
	//Constructor
    public DamageText(int startx,int starty,String d,int size,Color c) {
    	x=startx;
    	y=starty;
    	this.c=c;
    	str=d;
    	time=50;
    	font = new Font("Times New Roman", Font.BOLD, size);
    }
    //Every time it is drawn, the text fades out more and rises
    public void draw(Graphics g, GamePanel game){
    	g.setFont(font);
    	g.setColor(c);
    	g.drawString(str, x, y);
    	y--;
    	time--;
    }
    public boolean visible(){
    	return time>0;
    }
    public void setX(int nx){
    	x=nx;
    }
    public void setY(int ny){
    	y=ny;
    }
    public int getX(){
    	return x;
    }
    public int getY(){
    	return y;
    }
    public int max(int a,int b){
    	return a>b?a:b;
    }
    public int min(int a,int b){
    	return a<b?a:b;
    }
    
}
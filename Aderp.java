/**
 * @(#)Aderp.java
 * This method's main purpose is to allow AI to walk around corners. And chase the player.
 * As you can tell from the name, I'm not happy with it. It sometimes crashes and I can't seem 
 * to find the reason and ajsdkgasldgka. T.T so we just slapped some duck tape on it. It works.
 * Sort of.
 * @author 
 * @version 1.00 2014/4/29
 */

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class Aderp
{
	//The four directions you can walk
	public static int []x4= {0,1,0,-1};
	public static int []y4= {1,0,-1,0};
	public static Cord start, target;
	public Aderp(){
	}
	//Manhattan style
	public int hScore(Cord pt, Cord target){
		return (int)Math.abs(pt.getX()-target.getX())+Math.abs(pt.getY()-target.getY());
	}
	//finds the 1st square
	public Cord goBack(Cord st, Cord goal, Cord current, HashMap<Cord, Cord> parent){
		Cord temp= null;
		while(current!= st){
			temp = current;
			current = parent.get(current);
		}
		try{
			return temp;
		}
		catch(Exception e){
			return st;
		}
	}
	//I structured this method to be like an input/output machine. 
	//Once this class is made, you can just shove values in and get points as output.
	public Cord actualMaf(int [][] tilegrid, Creature [][] chrgrid, int x, int y, int ex, int ey){
		int [][] tiles;
		Creature [][] chr;
		int low;
		ArrayList <Cord> open= new ArrayList<Cord>();
		ArrayList <Cord> closed= new ArrayList<Cord>();
		HashMap<Cord, Integer> fScore = new HashMap<Cord, Integer>();
		HashMap<Cord, Integer> gScore = new HashMap<Cord, Integer>();
		HashMap<Cord, Cord> parent = new HashMap<Cord, Cord>();
		Cord curr = null;
		Cord temp = null;
		boolean good = true;
		tiles = tilegrid;
		chr = chrgrid;
		start = new Cord(x,y);
		target = new Cord(ex,ey);
		//Adds the starting square
		open.add(start);
		gScore.put(start,0);
		fScore.put(start,gScore.get(start)+hScore(start,target));
		//'cause cool kids use nanoseconds
		long started = System.nanoTime();
		while(open.size() > 0){
			//if it takes too long to pathfind, this kicks it out of the loop
			if(System.nanoTime()-started > 1200000)
				break;
			//the lowest cost to move to a tile
			low = Integer.MAX_VALUE;
			for(Cord coor:open){
				if(fScore.get(coor)< low && closed.contains(coor) == false){
					curr = coor;
					low = fScore.get(coor);
				}
			}
			//we're looking at the point, so we put it in the checked list
			open.remove(curr);
        	closed.add(curr);
        	if(curr == target){
        		return goBack(start, target, curr, parent);
        	}
        	//checks all the directions
			for(int i = 0; i<4; i++){
				good=true;
	        	temp = new Cord((int)curr.getX() + x4[i],(int)curr.getY()+y4[i]);
	        	if((int)temp.getX() < tiles.length && (int)temp.getY() < tiles[0].length){
	        		if(tiles[(int)temp.getX()][(int)temp.getY()] < 1 && chr[(int)temp.getX()][(int)temp.getY()] == null){
	        			//if we've already looked at the spot, then the tile is no good.
	        			if(closed.contains(temp)){
	        				good = false;
	        			}
	        			if(gScore.get(temp)==null && good){
	        				gScore.put(temp,gScore.get(curr)+ 10);//the 10 is the cost to move to the square
	        			}
	        			if(good){
	        				if(!open.contains(temp) || gScore.get(curr)+10<gScore.get(temp)){
			 	            	gScore.put(temp,gScore.get(curr)+10);
			                	fScore.put(temp,gScore.get(temp)+hScore(temp,target));
			                	parent.put(temp,curr);
			                    if(temp.getX() == target.getX() && temp.getY() == target.getY()){
			                    	//if we reach the goal, we return the first move
			          	        	return goBack(start,target,temp,parent);
		                        }
		                        open.add(temp);
	       					}
	       				}
	       			}
	       		}
	       		else{
	       			return start;
	       		}
	       	}
		}
		return start;
	}
}

//A utility class. 'Cause hashes are weird for points.
class Cord{
	
	private int x,y;
	
	public Cord(int x,int y){
		this.x=x;
		this.y=y;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int hashCode(){
		return x*1000+y;
	}
	public Point toPoint(){
		return new Point(x,y);
	}
	
}
/*Spell.java
 *This class holds and handles all the information
 *for a special attack.
 */

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Spell {
	//Attack Stats
	private int dmg,range,size,cost;
	private int multstat;
	private String name,tooltip;
	private Image pic;
	//Constructor
    public Spell(String n,String[]stats) {
    	name=n;
    	dmg=Integer.parseInt(stats[0]);
    	range=Integer.parseInt(stats[1]);
    	size=Integer.parseInt(stats[2]);
    	cost=Integer.parseInt(stats[3]);
    	multstat=Integer.parseInt(stats[4]);
    	pic=new ImageIcon("graphics/"+stats[5]+".png").getImage();
    	tooltip=stats[6];
    }
    
    public void draw(int x,int y,Graphics g,GamePanel game){
    	g.drawImage(pic,x,y,game);
    }
    //This lets one creature attack another using the spell if it has enough mana
    //returns if the spell has been cast
    public boolean cast(Creature caster,Creature target){
    	if (caster.getMP()>=cost && Math.pow(Math.pow(target.getX()-caster.getX(),2) + Math.pow(target.getY()-caster.getY(),2),0.5)<=range){
    		caster.spendMP(cost);
    		target.takeDamage((int)(dmg*(1+(caster.getStats()[multstat]*1.0)/100)));
    		return true;
    	}
    	return false;
    }
    //Getter Methods
    public int getdmg(){
    	return dmg;
    }
    public int getrange(){
    	return range;
    }
    public int getsize(){
    	return size;
    }
    public int getcost(){
    	return cost;
    }
    
    
}
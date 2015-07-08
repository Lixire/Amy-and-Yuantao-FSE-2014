/*Creature.java
 *This class holds and handles all the information and workings 
 *of a single individual creature. For non-player creatures, it
 *handles where it moves as well as its health and strength. For
 *a player, the class can take user input as well as hold onto and
 *apply the creatures stats and inventory.
 */

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Creature {

	//stats
	private int x,y,maxhp,hp,maxmp,mp,turn;
	private int prevx,prevy;
	private int baseatk,basedef;
	
	private int level,xp,points;
	private int[]stats;
	public ArrayList <Item> inventory;
	public Item[] wearing = {null,null,null,null};
	
	private Image pic;
	
	public static int STR=0;
	public static int VIT=1;
	public static int INT=2;
	public static int DEX=3;
	
    private ArrayList<DamageText>dmgtxt;
    public Item weapon;
	private boolean inCombat,attacking,regencount,stunned,poisoned,invulnerable;
	
	private boolean player=false;
	
	private Spell special;
	
	//Non player creature
    public Creature(String name,int x,int y,String[]stats) {
    	pic=new ImageIcon("graphics/creatures/"+name+".png").getImage();
    	this.x=x;
    	this.y=y;
    	prevx=0;
    	prevy=0;
    	
    	maxhp=Integer.parseInt(stats[0]);
    	hp=maxhp;
    	baseatk=Integer.parseInt(stats[1]);
    	basedef=Integer.parseInt(stats[2]);
    	xp=Integer.parseInt(stats[3]);
    	
    	inCombat=false;
    	attacking=false;
    	
    	stunned=false;
    	poisoned=false;
    	invulnerable=false;
    	
    	dmgtxt=new ArrayList<DamageText>();
    }
    //New Player creature
    public Creature(int x,int y) {
    	pic=new ImageIcon("graphics/creatures/person1.png").getImage();
    	
    	this.x=x;
    	this.y=y;
    	prevx=0;
    	prevy=0;
    	
    	player=true;
    	
    	level=1;
    	xp=0;
    	//str,vit,int,dex
    	stats=new int[4];
    	points=5;
    	for (int i=0;i<4;i++){
    		stats[i]=0;
    		addPoint(i);
    	}
    	update();
    	hp=maxhp;
    	mp=maxmp;
    	inventory=new ArrayList<Item>();
    	inventory.add(new Item("Lesser-Health-Potion/consumable/50/0/0/0/0/0/HPpotion1/Heals#50#HP"));
    	
    	inCombat=false;
    	attacking=false;
    	regencount=false;
    	
    	stunned=false;
    	poisoned=false;
    	invulnerable=false;
    	
    	dmgtxt=new ArrayList<DamageText>();
    	
    	special=new Spell("fireball","10/5/1/10/2/fireball1/Fireball!".split("/"));
    }
    //Loading a Previous Character
    public Creature(int x,int y,String inf,ArrayList<Item>previtems) {
    	pic=new ImageIcon("graphics/creatures/person1.png").getImage();
    	String[]info=inf.split("/");
    	this.x=x;
    	this.y=y;
    	prevx=0;
    	prevy=0;
    	
    	player=true;
    	
    	level=Integer.parseInt(info[0]);
    	xp=Integer.parseInt(info[1]);
    	//str,vit,int,dex
    	stats=new int[4];
    	for (int i=0;i<4;i++){
    		stats[i]=Integer.parseInt(info[i+2]);
    	}
    	points=Integer.parseInt(info[6]);
    	update();
    	hp=Integer.parseInt(info[7]);
    	mp=Integer.parseInt(info[8]);
    	
    	inventory=previtems;
    	
    	inCombat=false;
    	attacking=false;
    	regencount=false;
    	
    	stunned=false;
    	poisoned=false;
    	invulnerable=false;
    	
    	dmgtxt=new ArrayList<DamageText>();
    	
    	special=new Spell("fireball","10/5/1/10/2/fireball1/Fireball!".split("/"));
    }
    //User Input, uses keys to perform a move
   	public boolean userInput(boolean[]keys,Creature[][]chargrid,int[][]tilegrid){
   		boolean input=false;
   		inCombat=false;
    	
    	prevx=0;
    	prevy=0;
    	
   		if (keys[65]){//a
   			if (tilegrid[x-1][y]<1){
   				if (chargrid[x-1][y]==null){
   					x-=1;
   					prevx=1;
   					input = true;
   				}
			    else{
			    	fight(chargrid[x-1][y]);
			    	input = true;
			    }
   			}
   		}
   		else if (keys[68]){//d
   			if (tilegrid[x+1][y]<1){
   				if (chargrid[x+1][y]==null){
   					x+=1;
			    	prevx=-1;
   					input = true;
   				}
			    else{
			    	fight(chargrid[x+1][y]);
			    	input = true;
			    }
   			}
   		}
   		else if (keys[83]){//s
   			if (tilegrid[x][y+1]<1){
   				if (chargrid[x][y+1]==null){
   					y+=1;
    				prevy=-1;
   					input = true;
   				}
			    else{
			    	fight(chargrid[x][y+1]);
			    	input = true;
			    }
   			}
   		}
   		else if (keys[87]){//w
   			if (tilegrid[x][y-1]<1){
   				if (chargrid[x][y-1]==null){
   					y-=1;
    				prevy=1;
   					input = true;
   				}
			    else{
			    	fight(chargrid[x][y-1]);
			    	input = true;
			    }
   			}
   		}
   		else if (keys[32]){//Space
   			input = true;
   		}
   		
   		return input;
   	}
   	//AI movement
    public void move(Creature player,Creature[][]chargrid,int[][]tilegrid){
    	int[]dx={0,0,1,-1};
    	int[]dy={1,-1,0,0};
    	int[]direct={0,1,2,3};
    	
    	Aderp astar=new Aderp();
    	//A*
    	if (Math.sqrt((Math.pow(x-player.getX(),2)+Math.pow(y-player.getY(),2)))<10){
    		Point des=astar.actualMaf(tilegrid,chargrid,x,y,player.getX(),player.getY()).toPoint();
		    if (!(des.getX() == x && des.getY() == y)){
		    	if (des.getX() == player.getX() && des.getY() == player.getY()){
				    fight(player);
    				prevx=0;
    				prevy=0;
		    	}
		    	else{
    				prevx=x-(int)des.getX();
    				prevy=y-(int)des.getY();
			    	x=(int)des.getX();
			    	y=(int)des.getY();
		    	}
			    return;
		    }
    	}
    	
    	
    	int temp;
    	int rand;
    	for (int i=0;i<4;i++){
    		rand=(int)(Math.random()*4);
    		temp=direct[i];
    		direct[i]=direct[rand];
    		direct[rand]=temp;
    	}
    	//Check if player is in range of attack
    	for (int d:direct){
    		if (player.getX()==x+dx[d] && player.getY()==y+dy[d]){
			    fight(player);
		    	prevx=0;
		    	prevy=0;
			    return;
    		}
    	}
    	//Move towards player
    	for (int d:direct){
	    	if (0<x+dx[d] && x+dx[d]<chargrid.length && 0<y+dy[d] && y+dy[d]<chargrid.length && Math.abs(dx[d])+Math.abs(dy[d])==1){
	    		if (tilegrid[x+dx[d]][y+dy[d]]<1 && !(dx[d]==prevx &&  dy[d]==prevy)){
			    	if (chargrid[x+dx[d]][y+dy[d]]==null){
			    		if (!(player.getX()==x+dx[d] && player.getY()==y+dy[d])){
    						prevx=-dx[d];
    						prevy=-dy[d];
				    		x+=dx[d];
					    	y+=dy[d];
					    	return;
			    		}
				    	else{
				    		fight(player);
    						prevx=0;
    						prevy=0;
				    		return;
				    	}
				    }
	    		}
	    	}
    	}
    	prevx=0;
    	prevy=0;
    }
    //Fights between creatures
    public void fight(Creature op){
    	inCombat=true;
    	op.takeDamage(baseatk);
    	//op.hp=max(op.hp-max(1,baseatk-op.basedef),0);
    	if (op.hp==0 && player){
    		addXP(op.xp);
    	}
    }
    public void takeDamage(int damage){
    	hp=max(hp-max(1,damage-basedef),0);
    	dmgtxt.add(new DamageText(x,y,"-"+max(1,damage-basedef),16,Color.RED));
    }
    //Draws the creature
    public void draw(int x,int y,int frame, Graphics g, GamePanel game){

    	g.drawImage(pic,x+prevx*frame,y+prevy*frame,game);
		//Health Bar
    	if (!player && hp!=maxhp){
    		g.setColor(Color.RED);
    		g.fillRect(x+2+prevx*frame,y+37+prevy*frame,(int)((hp*1.0)/(maxhp*1.0)*36),2);
    	}
    }
    //Regen health
    public void regen(Creature[][]chargrid){
   		if (!inCombat && isAlive()){
   			if (regencount){
    			hp=min(maxhp,hp+1);
    			mp=min(maxmp,mp+1);
   			}
   			//This makes it regen every other time regen is called
   			regencount=!regencount;
   		}
    }
    public boolean isPlayer(){
    	return player;
    }
    public boolean isAlive(){
    	return hp>0;
    }
    public int getHP(){
    	return hp;
    }
    public int getMHP(){
    	return maxhp;
    }
    public int getMP(){
    	return mp;
    }
    public int getMMP(){
    	return maxmp;
    }
    public void spendMP(int n){
    	mp=max(mp-n,0);
    }
    public int getLevel(){
    	return level;
    }
    public int getXP(){
    	return xp;
    }
    //Adds XP
    public void addXP(int exp){
    	xp+=exp;
    	while (xp>=level*level*10){
    		xp-=level*level*10;
    		level++;
    		points+=2;
    		dmgtxt.add(new DamageText(x,y,"Level up!",32,Color.BLUE));
    	}
    }
    //Adds points
    public void addPoint(int stat){
    	if (points>0){
    		points--;
    		stats[stat]++;
    		if (stat==VIT){
    			hp+=3;
    		}
    		update();
    	}
    }
    //Updates stats
    public void update(){
    	baseatk=stats[STR]+1;
    	maxhp=11+stats[STR]+3*stats[VIT];
    	maxmp=10+stats[INT];
    }
    public int[]getStats(){
    	return stats;
    }
    public int getPoints(){
    	return points;
    }
    public int getX(){
    	return x;
    }
    public int getY(){
    	return y;
    }
    public int getFx(){
    	return prevx;
    }
    public int getFy(){
    	return prevy;
    }
    public void setX(int n){
    	x=n;
    }
    public void setY(int n){
    	y=n;
    }
    public Point getPoint(){
    	return new Point(x,y);
    }
    public ArrayList<DamageText> getdmgtxt(){
    	ArrayList<DamageText>tempdem=new ArrayList<DamageText>(dmgtxt);
    	dmgtxt=new ArrayList<DamageText>();
    	return tempdem;
    }
    public String toString(){
    	String temp="";
    	temp=level+"/"+xp;
    	for (int s:stats){
    		temp+="/"+s;
    	}
    	temp+="/"+points;
    	temp+="/"+hp;
    	temp+="/"+mp;
    	temp+="/"+inventory.size();
    	for (int i=0;i<inventory.size();i++){
    		temp+="\n"+inventory.get(i);
    	}
    	return temp;
    }
    public Spell getSpell(){
    	return special;
    }
    public int max(int a,int b){
    	return a>b?a:b;
    }
    public int min(int a,int b){
    	return a<b?a:b;
    }
    //Itemz
    public void equip(Item x){
    	if(x.isWorn()){
    		boolean flag = false;
			int check = x.getSlot();
	    	for(Item worn:wearing){
	    		if(check ==worn.getSlot()){
	    			inventory.add(worn);
	    			wearing[worn.getSlot()] = null;
	    			wearing[worn.getSlot()] = x;
	    			inventory.remove(x);
	    			flag = true;
	    		}
	    	}
	    	if(flag = false){
	    		wearing[x.getSlot()] = x;
	    	
	    	}
    	}
    }
    public void eatItem(Item x,int spot){
    	if(x.isConsumable()){
    		hp=Math.min(x.getHP()+hp, maxhp);
    		mp = Math.min(x.getMP()+mp, maxmp);
    		stats[STR] = x.getSTR()+stats[STR] >0 ?x.getSTR()+stats[STR]  : 1;
    		stats[DEX] = x.getDEX()+stats[DEX]>0 ? x.getDEX()+stats[DEX]  : 1;
    		stats[VIT]= x.getVIT()+stats[VIT]>0 ? x.getVIT()+stats[VIT]  : 1;
    		stats[INT] = x.getINT()+stats[INT]>0 ? x.getINT()+stats[INT]  : 1;
    		inventory.remove(spot);
    		update();
    	}
    }
}
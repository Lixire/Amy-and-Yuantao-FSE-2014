/*GamePanel.java
 *This is class that holds all the variables for the game
 *portion of the program. It also generates maps and 
 *runs methods that check the status of the game as well
 *as allow the player and the AI to perform actions.
 */

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
//Sound
import java.applet.*;
import javax.sound.sampled.AudioSystem;

public class GamePanel extends JPanel implements KeyListener,MouseListener,MouseMotionListener{
	
	//Images
	private Image background,hbar,menu,pressEtointeract,pressRtopickup,plusButton,spellmenu;
	private Image[]tiles;
	
	private boolean[]keys;
	//toggle and condition based booleans
	private boolean maptoggle,menutoggle,pressE,pressR;
	//This holds all points that are considered rooms (non hallways)
	private ArrayList<Point>roomtiles;
	//Holds the tile at location
	private int[][]tilegrid;
	//Holds the item at location
	private Item[][]itemgrid;
	//If it has been explored, it is displayed on the map
	private boolean[][]explored;
	//List of creatures
	private ArrayList<Creature>charlist;
	//Array of creatures, used to check proximity
	private Creature[][]chargrid;
	//player controlled creature
	private Creature player;
	//Floor and tileshift
	//tile values <=0 are "walkable", tile shift moves values over so it can get them from the array
	private int level,tileShift;
	//frame for moving
	private int frame;
	//mouse position and state
	private int mx,my;
	private boolean m1,m2;
	//Top down view location
	private int centx,centy;
    //Font
    Font font = new Font("Serif", Font.PLAIN, 10);
	//creature information
	private HashMap<String,String>creaturepedia;
	//item information
	private ArrayList <Item> itempedia;
	
	private ArrayList<DamageText>dmgtxt;
	
	private inputBox getWords;
	private boolean spelltoggle;
	//Count down til dead
	private int deathcount;
	//You explode on death, it's like spite
	private Image[]explode;
	
    AudioClip music,scream;
	private boolean tryEquip= false;
	private Item tempItem;
	
	//default constructor
    public GamePanel() {
    	super();
    	setLayout(null);
    	setSize(800,600);
    	
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
    	
    	setFocusable(true);
    	grabFocus();
		
    	keys=new boolean[2000];
    	for (int i=0;i<2000;i++){
    		keys[i]=false;
    	}
    	//other images
    	background=new ImageIcon("graphics/GUI/gamescreen.png").getImage();
    	hbar=new ImageIcon("graphics/GUI/healthbar.png").getImage();
    	menu=new ImageIcon("graphics/GUI/menu.png").getImage();
    	pressEtointeract=new ImageIcon("graphics/GUI/pressEtointeract.png").getImage();
    	pressRtopickup=new ImageIcon("graphics/GUI/pressRtopickup.png").getImage();
    	plusButton=new ImageIcon("graphics/GUI/+.png").getImage();
    	spellmenu=new ImageIcon("graphics/GUI/spellmenu.png").getImage();
    	//tile images
    	//Tile shift fixes tile values to their pictures. tiles values<0 are walkable
    	tileShift=15;
    	tiles=new Image[30];
    	for(int i=-15;i<7;i++){
    		tiles[i+tileShift]=new ImageIcon("graphics/terrain/tile"+i+".png").getImage();
    	}
    	//loads creaturepedia and itempedia
    	readPedia();
    	readItempedia();
    	
    	level=1;
	    genMap(min(40+level*3/5,100));
    	//player always starts in the middle of the map
  		player=new Creature(tilegrid.length/2,tilegrid.length/2);
    	centx=player.getX()*40-10*40;
    	centy=player.getY()*40-(int)7.5*40;
    	
    	maptoggle=false;
    	menutoggle=false;
    	pressE=false;
    	
    	dmgtxt=new ArrayList<DamageText>();
    	
    	getWords=new inputBox("Save Game");
    	spelltoggle=false;
    	explode=new Image[13];
    	deathcount=24;
    	for (int i=0;i<13;i++){
    		explode[i]=new ImageIcon("graphics/explode"+i+".png").getImage();
    	}
    	
    	frame=0;
    	
    	scream=Applet.newAudioClip(getClass().getResource("WilhelmScream.wav"));
		music = Applet.newAudioClip(getClass().getResource("The Arena Awaits.wav"));
		//music.loop();
    }
    //constructor for loaded game
    public GamePanel(int startlvl,Creature loaded){
    	super();
    	setLayout(null);
    	setSize(800,600);
    	
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
    	
    	setFocusable(true);
    	grabFocus();
		
    	keys=new boolean[2000];
    	for (int i=0;i<2000;i++){
    		keys[i]=false;
    	}
    	//other images
    	background=new ImageIcon("graphics/GUI/gamescreen.png").getImage();
    	hbar=new ImageIcon("graphics/GUI/healthbar.png").getImage();
    	menu=new ImageIcon("graphics/GUI/menu.png").getImage();
    	pressEtointeract=new ImageIcon("graphics/GUI/pressEtointeract.png").getImage();
    	pressRtopickup=new ImageIcon("graphics/GUI/pressRtopickup.png").getImage();
    	plusButton=new ImageIcon("graphics/GUI/+.png").getImage();
    	spellmenu=new ImageIcon("graphics/GUI/spellmenu.png").getImage();
    	//tile images
    	tileShift=15;
    	tiles=new Image[30];
    	for(int i=-15;i<7;i++){
    		tiles[i+tileShift]=new ImageIcon("graphics/terrain/tile"+i+".png").getImage();
    	}
    	
    	readPedia();
    	readItempedia();
    	//Difference from default is that it takes a level and player
    	level=startlvl;
	    genMap(min(40+level*3/5,100));
  		player=loaded;
  		center();
    	
    	maptoggle=false;
    	menutoggle=false;
    	pressE=false;
    	
    	dmgtxt=new ArrayList<DamageText>();
    	
    	getWords=new inputBox("Save Game");
    	spelltoggle=false;
    	explode=new Image[13];
    	deathcount=24;
    	for (int i=0;i<13;i++){
    		explode[i]=new ImageIcon("graphics/explode"+i+".png").getImage();
    	}
    	
    	frame=0;
    	
    	scream=Applet.newAudioClip(getClass().getResource("WilhelmScream.wav"));
		music = Applet.newAudioClip(getClass().getResource("The Arena Awaits.wav"));
		music.loop();
    }
    
    //Loads Creaturepedia
    //This fills the hashmap with the stats of each creature
    public void readPedia(){
    	creaturepedia=new HashMap<String,String>();
    	String[]temp;
    	try{
    		Scanner data = new Scanner(new BufferedReader(new FileReader("creaturepedia.txt")));
    		int pages=data.nextInt();
    		for (int i=0;i<pages;i++){
    			temp=data.next().split("-");
    			creaturepedia.put(temp[0],temp[1]);
    		}
    	}
    	catch(Exception ex){
    		System.out.println(ex);
    	}
    }
    //loads Itempedia
    //This fills the arraylist with the stats of each item
    public void readItempedia(){
    	itempedia=new ArrayList<Item>();
    	try{
    		Scanner data = new Scanner(new BufferedReader(new FileReader("graphics/Itemz/Consumables.txt")));
    		int pages=data.nextInt();
    		Item temp;
    		String c;
    		for (int i=0;i<pages;i++){
    			c=data.next().trim();
    			temp=new Item(c);
    			itempedia.add(temp);
    		}
    	}
    	catch(Exception ex){
    		System.out.println(ex);
    	}
    }
    //Generates a new map of sizeXsize
    private void genMap(int size){
    	roomtiles=new ArrayList<Point>();//tiles where enemies can spawn
    	tilegrid=new int[size][size];//holds values for each tile
    	itemgrid=new Item[size][size];//items
    	charlist=new ArrayList<Creature>();//arraylist of creatures
    	chargrid=new Creature[size][size];//grid of creatures that updates the list
    	explored=new boolean[size][size];//fog of war
    	
    	for (int i=0;i<size;i++){
    		for (int j=0;j<size;j++){
    			tilegrid[i][j]=1;
    			chargrid[i][j]=null;
    		}
    	}
    	//# of rooms
    	int roommax=(int)Math.pow((int)(Math.random()*size/10),2)+size/10;
    	int roomnum=0;
    	int tries=0;
    	int tx,ty,t2x,t2y,len,wid;
    	int[]dx={0,0,1,-1};
    	int[]dy={1,-1,0,0};
    	int[]d8x={0,0,1,-1,1,1,-1,-1};
    	int[]d8y={1,-1,0,0,1,-1,1,-1};
    	boolean flag;
    	boolean firstroom=true;
    	ArrayList<Integer>rx=new ArrayList<Integer>();
    	ArrayList<Integer>ry=new ArrayList<Integer>();
    	Point temp;
    	//room center
    	tx=size/2;
    	ty=size/2;
	    while(roomnum<roommax){
	    	flag=true;
	    	len=6+(int)(Math.random()*size/15.0);
	    	wid=len+(int)(Math.random()*len/6.0)-len/12;
	    	if (roomnum>0){
	    		tx=(int)(Math.random()*(tilegrid.length-len))+len/2;
	    		ty=(int)(Math.random()*(tilegrid.length-wid))+wid/2;
	    	}
	    	//This makes sure it doesn't spawn too close to another room
	    	for (int i=0;i<rx.size();i++){
	    		if (dis(tx,ty,rx.get(i),ry.get(i))<10){
		    		flag=false;
		    		tries++;
		    		break;
		    	}
	    	}
	    	//breaks out of loop if it has too much trouble generating enough rooms
	    	if (tries>10){
	    		break;
	    	}
			if (flag){
				//this stops it from over lapping into other rooms by placing a wall around it
				for (int i=tx-len/2-1;i<tx+len/2+1;i++){
			    	for (int j=ty-wid/2-1;j<ty+wid/2+1;j++){
			    		if (0<i && i<99 && 0<j && j<99){
			    			tilegrid[i][j]=1;
			    		}
			    	}
			    }
				for (int i=tx-len/2;i<tx+len/2;i++){
			    	for (int j=ty-wid/2;j<ty+wid/2;j++){
			    		if (0<i && i<99 && 0<j && j<99){
			    			tilegrid[i][j]=0;
			    		}
			    	}
			    }
			    //Special Rooms
			    if (!firstroom){
			    	//temple
			    	if (roomnum>6 && len==wid && 0.9<Math.random()){
				    	for (int i=tx-len/2;i<tx+len/2;i++){
					    	for (int j=ty-wid/2;j<ty+wid/2;j++){
					    		if (0<i && i<99 && 0<j && j<99){
					    			tilegrid[i][j]=-3;
					    		}
					    	}
					    }
					    tilegrid[tx][ty]=2;
			    	}//forest
			    	else if(level>30 && roomnum>8 && len+wid>18 && 0.95<Math.random()){
				    	for (int i=tx-len/2+1;i<tx+len/2-1;i++){
					    	for (int j=ty-wid/2+1;j<ty+wid/2-1;j++){
					    		if (0<i && i<99 && 0<j && j<99){
					    			tilegrid[i][j]=-5;
					    			if (Math.random()>0.6){
					    				tilegrid[i][j]=4+(int)(Math.random()*2);
					    			}
					    		}
					    	}
					    }
			    	}//Math Revealer
			    	else if ((level>15 || roomnum>10) && 0.8<Math.random()){
				    	for (int i=tx-1;i<tx+2;i++){
					    	for (int j=ty-1;j<ty+2;j++){
					    		if (0<i && i<99 && 0<j && j<99){
					    			tilegrid[i][j]=-3;
					    		}
					    	}
					    }
					    tilegrid[tx][ty]=6;
			    	}
			    }
			    //Generates Items
		    	for (int i=tx-len/2;i<tx+len/2;i++){
			    	for (int j=ty-wid/2;j<ty+wid/2;j++){
			    		if (0<i && i<99 && 0<j && j<99){
			    			if(Math.random()*1000 > 995 && tilegrid[i][j]==0){
			    				itemgrid[i][j]=itempedia.get((int)(Math.random()*itempedia.size()));
			    			}
			    			else{
			    				itemgrid[i][j]=null;
			    			}
			    		}
			    	}
			    }
			    
			    firstroom=false;
			    rx.add(tx);
			    ry.add(ty);
			    roomnum++;
			}
    	}
    	//Roomtiles
    	for (int i=0;i<size;i++){
    		for (int j=0;j<size;j++){
    			if (tilegrid[i][j]==0){
			    	roomtiles.add(new Point(i,j));
    			}
    		}
    	}
    	//Hallways
    	//connects all the rooms in one big loop
    	for (int i=0;i<roomnum;i++){
    		if (i==roomnum-1){
	    		tx=rx.get(i);
	    		ty=ry.get(i);
	    		t2x=rx.get(0);
	    		t2y=ry.get(0);
    		}
    		else{
	    		tx=rx.get(i);
	    		ty=ry.get(i);
	    		t2x=rx.get(i+1);
	    		t2y=ry.get(i+1);
    		}
    		if (true){
	    		while (!(tx==t2x && ty==t2y)){
	    			if (tx>t2x){
	    				tx--;
	    			}
	    			else if(tx<t2x){
	    				tx++;
	    			}
	    			else if(ty>t2y){
	    				ty--;
	    			}
	    			else if(ty<t2y){
	    				ty++;
	    			}
	    			if (tilegrid[tx][ty]==1){
	    				tilegrid[tx][ty]=0;
	    			}
	    		}
    		}
    	}
    	//Dungeon Exit
    	while(true){
    		flag=true;
    		temp=roomtiles.get((int)(Math.random()*roomtiles.size()));
    		for (int i=0;i<8;i++){
    			if (tilegrid[(int)temp.getX()+d8x[i]][(int)temp.getY()+d8y[i]]==1){
    				flag=false;
    				break;
    			}
    		}
    		if (flag){
		    	tilegrid[(int)temp.getX()][(int)temp.getY()]=-1;
		    	break;
    		}


    	}
    }   
    //Draws things on Screen
    public void paintComponent(Graphics g){
    	g.drawImage(background,0,0,this);
    	//terrain
    	for (int i=0;i<tilegrid.length;i++){
    		for (int j=0;j<tilegrid.length;j++){
    			//Fog of war
    			if (dis(player.getX(),player.getY(),i,j)<8){
    				explored[i][j]=true;
    			}
    			if (onScreen(i*40-centx,j*40-centy) && explored[i][j]){
    				g.drawImage(tiles[tilegrid[i][j]+tileShift],i*40-centx,j*40-centy,this);
    				if (itemgrid[i][j]!=null){
    					itemgrid[i][j].draw(i*40-centx,j*40-centy,g,this);
    				}
    			}
    		}
    	}
    	//creatures
    	for (Creature enemy:charlist){
    		if (onScreen(enemy.getX()*40-centx,enemy.getY()*40-centy) && explored[enemy.getX()][enemy.getY()]){
    			enemy.draw(enemy.getX()*40-centx,enemy.getY()*40-centy,frame,g,this);
    		}
    	}
    	//player
    	if (onScreen(player.getX()*40-centx,player.getY()*40-centy) && deathcount>16){
    		player.draw(player.getX()*40-centx,player.getY()*40-centy,frame,g,this);
    	}
    	//Damage floaty text stuff
    	ArrayList<DamageText>tempdem=new ArrayList<DamageText>();
    	for (DamageText dem:dmgtxt){
    		dem.draw(g,this);
    		if (dem.visible()){
    			tempdem.add(dem);
    		}
    	}
    	dmgtxt=tempdem;
    	
    	//Player Stats
        g.setFont(font);
    	g.setColor(Color.BLACK);
    	g.fillRect(52,20,79,15);
    	g.setColor(Color.RED);
    	g.fillRect(52,20,(int)((player.getHP()*1.0)/(player.getMHP()*1.0)*79),10);
    	g.setColor(Color.BLUE);
    	g.fillRect(52,30,(int)((player.getMP()*1.0)/(player.getMMP()*1.0)*79),5);
    	g.drawImage(hbar,0,0,this);
    	g.setColor(Color.BLACK);
    	g.drawString("HP: "+player.getHP()+"/"+player.getMHP(), 8, 25);
    	g.drawString("MP: "+player.getMP()+"/"+player.getMMP(), 8, 35);
    	g.drawString("FLOOR: "+level, 8, 15);
    	g.drawString("LEVEL: "+player.getLevel(), 68, 15);
    	
    	//Menu
        g.setFont(font);
    	String[]tooltip={"Each point increases attack damage.","Each point increases max health.","Increases magical poweress","Increases chance of critical hit."};
    	if (menutoggle){
    		g.setColor(Color.BLACK);
    		g.drawImage(menu,550,50,this);
    		g.setColor(Color.WHITE);
    		//makes player think he/she is making progress
    		g.drawRect(600,187-(level+1)/2,3,3);
    		g.setColor(Color.BLACK);
    		g.drawString(String.format("%3d",player.getLevel()),705,80);//level
    		for (int i=0;i<4;i++){//stats
    			if (player.getPoints()>0){//if there are extra points
    				g.drawImage(plusButton,725,85+i*15,this);
    				if (725<mx && mx<735 && 85+i*15<my && my<95+i*15){//mousing over buttons
    					g.setColor(Color.BLUE);
    					g.drawRect(725,85+i*15,10,10);
    				}
    			}
    			if (660<mx && mx<720 && 85+i*15<my && my<95+i*15){//mousing over stats
    				g.setColor(Color.BLACK);
    				g.drawString(tooltip[i], 585, 365);
    			}
    			g.setColor(Color.BLACK);
            	g.drawString(String.format("%3d",player.getStats()[i]), 705, 95+i*15);//stats
    		}
    		g.setColor(Color.BLACK);
            g.drawString(String.format("%3d",player.getPoints()), 705, 155);//extra points
            
            g.drawString(String.format("%3d",player.getXP()), 705, 210);//xp
            g.drawString(String.format("%3d",player.getLevel()*player.getLevel()*10-player.getXP()), 705, 225);//to next level
            g.drawString(String.format("%3d",0), 705, 240);//money
         	//Items
         	int i = 0;
         	int j = 0;
	    	for(Item x: player.wearing){
	    		
	    		if(x!= null){
	    			if(575+i<mx && 621+i> mx && 75+(j/2)*40< my && my<115+(j/2)*40){
		    			g.setColor(Color.BLACK);
		    			g.drawString(x.getName(), 585, 365);
		    			g.drawString(x.getDes(), 585, 385);
	    			}
		    		if(j==2){
		    			i=0;
		    		}
		    		x.draw(575+i,75+(j/2)*40,g,this);
		    		j+=1;
		    		i+=46;
	    		}
	    	}
	    	i=0;
	    	j=0;
	    	for(Item x: player.inventory){
	    		if(j==3){
	    			i = 0;
	    		}
	    		if(588+i<mx && mx< 588+i+46 && 270+(j/3)*40<my && my< 270+(j/3)*40 + 40){
	    			g.setColor(Color.BLACK);
	    			g.drawString(x.getName(), 585, 365);
	    			g.drawString(x.getDes(), 585, 385);
	    		}
	    		x.draw(588+i,270+(j/3)*40,g,this);
	    		j+=1;
	    		i+=46;
	    	}
    	}
		
    	//Spell Menu
    	g.drawImage(spellmenu,724,520,this);
		player.getSpell().draw(755,530,g,this);
		if (spelltoggle){
			g.setColor(Color.RED);
			g.drawRect(753,527,35,35);
			if (chargrid[(mx+centx)/40][(my+centy)/40]!=null && dis((mx+centx)/40,(my+centy)/40,player.getX(),player.getY())<=player.getSpell().getrange()){
				g.drawRect(((mx+centx)/40)*40-centx,((my+centy)/40)*40-centy,40,40);
			}
		}
		
    	//Minimap
    	int[]dx={0,0,1,-1};
    	int[]dy={1,-1,0,0};
    	int[]lx1={0,0,1,0};
    	int[]ly1={1,0,0,0};
    	int[]lx2={1,1,1,0};
    	int[]ly2={1,0,1,1};
    	if (maptoggle){
	    	for (int i=1;i<tilegrid.length-1;i++){
	    		for (int j=1;j<tilegrid.length-1;j++){
	    			if (tilegrid[i][j]<1 && explored[i][j]){
	    				for (int k=0;k<4;k++){//for the tiles surrounding it
    						g.setColor(Color.WHITE);
	    					if (tilegrid[i+dx[k]][j+dy[k]]==1 && explored[i+dx[k]][j+dy[k]]){
	    						g.drawLine((i+lx1[k])*5+40,(j+ly1[k])*5+40,(i+lx2[k])*5+40,(j+ly2[k])*5+40);
	    					}
	    					if (itemgrid[i][j]!=null){//item
	    						g.setColor(Color.GREEN);
		    					g.drawRect(i*5+41,j*5+41,1,1);
	    					}
	    					if (chargrid[i][j]!=null){//enemy
	    						g.setColor(Color.RED);
		    					g.fillRect(i*5+41,j*5+41,3,3);

	    					}
	    				}
	    				if (tilegrid[i][j]==-1){
	    						g.setColor(Color.BLUE);//exit
	    					g.drawRect(i*5+41,j*5+41,3,3);
	    				}
	    				if(tilegrid[i][j]<= -7){
	    					g.setColor(Color.WHITE);
	    					g.drawRect(i*5+41,j*5+41,3,3);
	    				}
	    			}
	    		}
	    	}
    		g.setColor(Color.GREEN);//player
	    	g.fillRect(player.getX()*5+41,player.getY()*5+41,3,3);
    	}
    	//press E notification
    	if(pressE){
    		g.drawImage(pressEtointeract,50,450,this);
    	}
    	//press R notification
    	if(pressR){
    		if (pressE){
    			g.drawImage(pressRtopickup,50,350,this);
    		}
    		else{
    			g.drawImage(pressRtopickup,50,450,this);
    		}
    	}
    	//frame change when moving
    	if (isMoving()){
    		frame=max(0,frame-4);
    	}
    	if (getWords.isVisible()){
	    	if (getWords.getPressed() && !getWords.getCurrent().equals("")){
	    		saveGame(getWords.getCurrent());
	    		getWords.setVisible(false);
	    		getWords=new inputBox("Save Game");
	    	}
    	}
    	//death animation
    	if (!player.isAlive()){
    		g.drawImage(explode[deathcount/2],player.getX()*40-centx-30,player.getY()*40-centy-30,this);
    	}
    }
    
    //Methods called by JFrame class
    //This takes player input and calls the functions needed for a single turn
    public void mainAction(){
    	pressE=false;
    	pressR=false;
    	if (player.userInput(keys,chargrid,tilegrid)){//if the player performs an action
    		npcAction();
    		spawnMonsters();
    		player.regen(chargrid);
    		frame=40;
    		
    	}
    }
    //NPC Actions
    //This goes through the creature arraylist and checks the creature at each spot
    //if the spot is null ,the creautre has died. If not, it updates the creature
    //in the arraylist
    public void npcAction(){
    	ArrayList<Creature>newcharlist=new ArrayList<Creature>();
    	for (Creature temp:charlist){
    		temp=chargrid[temp.getX()][temp.getY()];
    		if (temp!=null){
    			if (temp.isAlive()){
			    	chargrid[temp.getX()][temp.getY()]=null;
		    		temp.move(player,chargrid,tilegrid);
			    	chargrid[temp.getX()][temp.getY()]=temp;
			    	newcharlist.add(temp);
    			}
    			else{//Dead creature
    				chargrid[temp.getX()][temp.getY()]=null;
    				scream.play();
    			}
    		}//updates the global damage list
    		for (DamageText dem:temp.getdmgtxt()){
    			dem.setX(dem.getX()*40-centx+(int)(Math.random()*10)+5);
    			dem.setY(dem.getY()*40-centy+(int)(Math.random()*10)+5);
    			dmgtxt.add(dem);
    		}
    	}//player damage/level up
    	for (DamageText dem:player.getdmgtxt()){
    		dem.setX(dem.getX()*40-centx+(int)(Math.random()*20)+5);
    		dem.setY(dem.getY()*40-centy+(int)(Math.random()*20)+5);
    		dmgtxt.add(dem);
    	}
    	charlist=newcharlist;
    }
    //Advance checks for certain events and reactions to 
 	//terrian or environment
    public void advance(){
    	int[]dx={0,0,1,-1};
    	int[]dy={1,-1,0,0};
    	int[]d8x={0,0,1,-1,1,1,-1,-1};
    	int[]d8y={1,-1,0,0,1,-1,1,-1};
    	//exit
    	if (tilegrid[player.getX()][player.getY()]==-1){
    		pressE=true;
    		if (keys[69]){//e
	    		level++;
	    		genMap(min(40+level*3/5,100));
	    		player.setX(tilegrid.length/2);
	    		player.setY(tilegrid.length/2);
	    		centx=player.getX()*40-10*40;
	    		centy=player.getY()*40-(int)7.5*40;
    		}
    	}//Item on ground
    	else if(itemgrid[player.getX()][player.getY()]!=null){
    		pressR=true;
    		if(keys[82]){//r
    			if(player.inventory.size() <6){
	    			player.inventory.add(itemgrid[player.getX()][player.getY()]);
	    			itemgrid[player.getX()][player.getY()]=null;
    			}
    		}
    	}
    	for (int i=0;i<4;i++){
	    	//altar
	    	if (tilegrid[player.getX()+dx[i]][player.getY()+dy[i]]==2){
	    		pressE=true;
	    		if (keys[69]){//e
	    			tilegrid[player.getX()+dx[i]][player.getY()+dy[i]]=3;
	    			for (int j=0;j<8;j++){
	    				if (tilegrid[player.getX()+d8x[j]][player.getY()+d8y[j]]==-3 && chargrid[player.getX()+d8x[j]][player.getY()+d8y[j]]==null){
						   	charlist.add(new Creature("zombieguard",player.getX()+d8x[j],player.getY()+d8y[j],creaturepedia.get("zombieguard").split("/")));
						  	chargrid[player.getX()+d8x[j]][player.getY()+d8y[j]]=charlist.get(charlist.size()-1);
	    				}
	    			}
	    		}
	    	}
	    	//tree ents
	    	if (tilegrid[player.getX()+dx[i]][player.getY()+dy[i]]==5){
	    		tilegrid[player.getX()+dx[i]][player.getY()+dy[i]]=-6;
				charlist.add(new Creature("treeent",player.getX()+dx[i],player.getY()+dy[i],creaturepedia.get("treeent").split("/")));
				chargrid[player.getX()+dx[i]][player.getY()+dy[i]]=charlist.get(charlist.size()-1);
	    	}
	    	//Map Revealer
	    	if (tilegrid[player.getX()+dx[i]][player.getY()+dy[i]]==6){
	    		pressE=true;
	    		if (keys[69]){
	    			for (int r=0;r<explored.length;r++){
	    				for (int c=0;c<explored.length;c++){
	    					explored[r][c]=true;
	    				}
	    			}
	    		}
	    	}
    	}
    	
    }
	//Causes monsters to spawn, as long as the player isn't looking there of course
    public void spawnMonsters(){
    	int x,y;
    	Point temp;
    	Object[]creatureNames=creaturepedia.keySet().toArray();
    	String[]stats;
    	int rand;
    	//This makes a list of all the possible creatures that can spawn
    	ArrayList<String>valid=new ArrayList<String>();
    	for (Object name:creatureNames){
    		stats=creaturepedia.get(name).split("/");
    		if (Integer.parseInt(stats[5])<=level && level<=Integer.parseInt(stats[6])){
    			for (int i=0;i<Integer.parseInt(stats[4]);i++){
    				valid.add((String)name);
    			}
    		}
    	}
    	//Actual spawning
    	for (int i=0;i<(int)(Math.random()*5);i++){
    		temp=roomtiles.get((int)(Math.random()*roomtiles.size()));
    		x=(int)temp.getX();
    		y=(int)temp.getY();
			if (tilegrid[x][y]==0 && chargrid[x][y]==null && charlist.size()<roomtiles.size()/20){
		    	if (!onScreen(x*40-centx,y*40-centy)){
		    		rand=(int)(Math.random()*valid.size());
			   		charlist.add(new Creature(valid.get(rand),x,y,creaturepedia.get(valid.get(rand)).split("/")));
			  		chargrid[x][y]=charlist.get(charlist.size()-1);
			   	}
			}
		}
    }
    
    //checks if the game is over
    public boolean gameEnd(){
    	if (!player.isAlive()){
    		deathcount--;
    	}
    	return deathcount==0;
    }
    //checks if the game is drawing movement frames
    public boolean isMoving(){
    	if (keys[16]){//shift
    		frame=max(frame-4,0);
    	}
    	return frame>0;
    }
    //Other Methods
    //Returns distance between two points
    public double dis(int x1,int y1,int x2,int y2){
    	return Math.sqrt((Math.pow(x2-x1,2)+Math.pow(y2-y1,2)));
    }
    //Returns greater value
    public int max(int a,int b){
    	return a>b?a:b;
    }
    //Returns lesser value
    public int min(int a,int b){
    	return a<b?a:b;
    }
    //Returns if the point is on the screen
    public boolean onScreen(int x,int y){
    	return -50<x && x<850 && -50<y && y<650;
    }
    //Allows the mouse to drag the view around
    public void mouseMove(int tx,int ty){
		centx+=tx-mx;
		centy+=ty-my;
		if (centx<0){
			centx=0;
		}
		else if (centx>tilegrid.length*40-19*40-34){
			centx=tilegrid.length*40-19*40-34;
		}
		if (centy<0){
			centy=0;
		}
		else if (centy>tilegrid.length*40-14*40-12){
			centy=tilegrid.length*40-14*40-12;
		}
		repaint();
    }
    //centers screen on player
    public void center(){
    	centx=player.getX()*40+frame*player.getFx()-10*40;
    	centy=player.getY()*40+frame*player.getFy()-(int)7.5*40;
    	mouseMove(mx,my);
    }
    //MouseListener
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}   
    public void mouseClicked(MouseEvent e){}
    public void mousePressed(MouseEvent e){
    	m1=e.getButton()==MouseEvent.BUTTON1;
    	m2=e.getButton()==MouseEvent.BUTTON3;
		//If the player is casting a spell on a creature
    	if (spelltoggle){
    		if (chargrid[(mx+centx)/40][(my+centy)/40]!=null && dis((mx+centx)/40,(my+centy)/40,player.getX(),player.getY())<=player.getSpell().getrange()){
	    		if (e.getButton()==MouseEvent.BUTTON1 && frame==0){
	    			//Advances the turn if the spell is cast
	    			if (player.getSpell().cast(player,chargrid[(mx+centx)/40][(my+centy)/40])){
	    				npcAction();
		    			spawnMonsters();
		    			player.regen(chargrid);
		    			frame=40;
	    			}
	    		}
    		}
    	}
    }
    public void mouseReleased(MouseEvent e) {
    	m1=e.getButton()==MouseEvent.BUTTON1;
    	m2=e.getButton()==MouseEvent.BUTTON3;
    	//Item stuff that isn't used
    	if(tryEquip){
	    	if(575<mx && 621> mx && 75< my && my<115){
	    		if(tempItem.getSlot() == 4){
	    			player.inventory.remove(tempItem);
	    			if(player.wearing[3] != null){
	    				player.inventory.add(player.wearing[3]);
	    			}
	    			player.wearing[3] = tempItem;
	    		}
	    	}
	    	else if(621<mx && 667> mx && 75< my && my<115){
	    		if(tempItem.getSlot() == 1){
	    			player.inventory.remove(tempItem);
	    			if(player.wearing[0] != null){
	    				player.inventory.add(player.wearing[0]);
	    			}
	    			player.wearing[0] = tempItem;
	    		}
	    	}
	    	else if(575<mx && 621> mx && 115< my && my<155){
	    		if(tempItem.isWeapon()){
	    			player.inventory.remove(tempItem);
	    			if(player.weapon != null){
	    				player.inventory.add(player.weapon);
	    			}
	    			player.weapon = tempItem;
	    		}
	    	}
	    	else if(621<mx && 667> mx && 115< my && my<155){
	    		if(tempItem.getSlot() == 2){
	    			player.inventory.remove(tempItem);
	    			if(player.wearing[1] != null){
	    				player.inventory.add(player.wearing[1]);
	    			}
	    			player.wearing[1] = tempItem;
	    		}
	    	}
	    	else if(575<mx && 621> mx && 155< my && my<195){
	    		if(tempItem.getSlot() == 3){
	    			player.inventory.remove(tempItem);
	    			if(player.wearing[2] != null){
	    				player.inventory.add(player.wearing[2]);
	    			}
	    			player.wearing[2] = tempItem;
	    		}
	    	}	    
	    	tryEquip = false;
    	}
    	//Selecting Spell
    	if (e.getButton()==MouseEvent.BUTTON1 && 753<mx && mx<788 && 528<my && my<563){
    		spelltoggle=true;
    	}
    	//Stat adding
    	if (menutoggle && e.getButton()==MouseEvent.BUTTON1){
    		for (int i=0;i<4;i++){
		    	if (725<mx && mx<735 && 85+i*15<my && my<95+i*15){
		    		player.addPoint(i);
		    		m1=false;
		    	}
    		}
    		//inventory adding
    		for(int i =0; i<3;i++){
    			if(586+ i*46<mx && mx <632+i*46 && my>270 && my<310){
    				if(player.inventory.size() >i){
	    				if(player.inventory.get(i).isConsumable()){
	    					player.eatItem(player.inventory.get(i),i);
	    				}
		    			else if(player.inventory.get(i).isWorn()){
		    				tryEquip = true;
		    				tempItem = player.inventory.get(i);
		    			}
    				}
    			}
    		}
    		for(int i =0; i<3;i++){
    			if(588+ i*46<mx && mx <634+i*46 && my>310 && my<350){
    				if(player.inventory.size() >i+3){
	    				if(player.inventory.get(i+3).isConsumable()){
	    					player.eatItem(player.inventory.get(i+3),i+3);
	    				}
		    			else if(player.inventory.get(i+2).isWorn()){
		    				tryEquip = true;
		    				tempItem = player.inventory.get(i+2);
		    			}
    				}
    			}
    		}
    	}
    }   
    //MouseMotionListener
    public void mouseMoved(MouseEvent e){
    	mx=e.getX();
    	my=e.getY();
    }
    public void mouseDragged(MouseEvent e){
    	int tx,ty;
    	tx=mx;
    	ty=my;
    	mx=e.getX();
    	my=e.getY();
    	if (m2){//dragging screen
    		mouseMove(tx,ty);
    	}
    }
    
    
    //Key Listener
    public void keyPressed(KeyEvent evt){
    	int i=evt.getKeyCode();
    	keys[i]=true;
    }
    public void keyReleased(KeyEvent evt){
    	int i=evt.getKeyCode();
    	keys[i]=false;
    	if (i==77){//m
    		maptoggle=!maptoggle;
    	}
    	if (i==73){//m
    		menutoggle=!menutoggle;
    	}
    	if (i==27){//esc
    		getWords=new inputBox("Save Game");
    		getWords.setVisible(true);
    	}
    	//Cheats, for testing purposes
    	//Skip level
    	if (i==79 && keys[17]){//O + ctrl
    		tilegrid[player.getX()][player.getY()]=-1;
    		if (!keys[69]){
    			keys[69]=true;
    			advance();
    			keys[69]=false;
    		}
    		else{
	    		advance();
    		}
    	}
    	//+1000 xp
    	if (i==76 && keys[17]){//L + ctrl
    		player.addXP(1000);
    	}
    }
    public void keyTyped(KeyEvent evt){}
    
    public String toString(){
    	return level+"\n"+player.toString();
    }
    //Saving Game
    public void saveGame(String file){
    	try{
    		PrintWriter outfile = new PrintWriter(new BufferedWriter(new FileWriter(file+".txt")));
    		outfile.println(level);
    		outfile.println(player.toString());
    		outfile.close();
    	}
    	catch(Exception ex){
    		System.out.println(ex);
    	}
    }
    public Creature getPlayer(){
    	return player;
    }
}
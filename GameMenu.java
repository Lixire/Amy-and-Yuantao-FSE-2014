/*GameMenu.java
 *This class runs the menu where the user can choose to start a new game
 *or load an old one. When the player is finished, this class will return
 *the information the main class needs to make the GamePanel.
 */

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class GameMenu extends JPanel implements KeyListener,MouseListener,MouseMotionListener{
	
	private Image background, intro;
	private Image[]start;
	private Image[]load;
	private Image[]instr;
	//Instructions Screen
	private boolean drawn= false;
	private boolean[]keys;
	//Temp variable for a loaded character
	private Creature player;
	
	private int mx,my,level;
	//newGame tells whether or not the game can be started
	private boolean mb,newGame;
	//Used to get the name of the file to load
	private inputBox getWords;
	//Constructor
    public GameMenu() {
    	super();
    	setLayout(null);
    	setSize(800,600);
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
    	
    	keys=new boolean[2000];
    	for (int i=0;i<2000;i++){
    		keys[i]=false;
    	}
    	level=0;
    	
    	player=null;
    	newGame=false;
    	
    	background=new ImageIcon("graphics/GUI/menuscreen.png").getImage();
    	intro=new ImageIcon("graphics/GUI/intro.png").getImage();
    	//Buttons
    	start = new Image[2];
    	load  = new Image[2];
    	instr = new Image[2];
    	for (int i=0;i<2;i++){
    		start[i]=new ImageIcon("graphics/GUI/start"+i+".png").getImage();
    		load[i]=new ImageIcon("graphics/GUI/load"+i+".png").getImage();
    		instr[i]=new ImageIcon("graphics/GUI/instr"+i+".png").getImage();
    	}
    	
    	getWords=new inputBox("Load Game");
    	
    	setFocusable(true);
    	grabFocus();
    }
    
    public void paintComponent(Graphics g){
    	g.drawImage(background,0,0,this);
    	g.drawImage(start[0],570,320,this);
    	//Buttons
    	if (570<mx && mx<750 && 320<my && my<370){
    		g.drawImage(start[1],570,320,this);
    	}
    	g.drawImage(load[0],570,380,this);
    	if (570<mx && mx<750 && 380<my && my<430){
    		g.drawImage(load[1],570,380,this);
    	}
    	//Instructions Screen
    	if(drawn){
    		g.drawImage(intro,-5,-14,this);
    	}
    	g.drawImage(instr[0],570,440,this);
    	if (570<mx && mx<750 && 440<my && my<490){
    		g.drawImage(instr[1],570,440,this);
    	}
    	if (getWords.getPressed() && !getWords.getCurrent().equals("")){
    		loadPlayer(getWords.getCurrent());
    		getWords.setVisible(false);
    		getWords=new inputBox("Load Game");
    		newGame=true;
    	}
    }
    //Loads a Player class
	public void loadPlayer(String file){
    	try{
    		Scanner data = new Scanner(new BufferedReader(new FileReader(file+".txt")));
    		level = Integer.parseInt(data.next());
    		String charinf=data.next();
    		
    		ArrayList<Item>previtems=new ArrayList<Item>();
	    	while (data.hasNext()){
	    		previtems.add(new Item(data.next()));
	    	}
	    	
    		player= new Creature(min(40+level*3/5,100)/2,min(40+level*3/5,100)/2,charinf,previtems);
    	}
    	catch(Exception ex){
    		System.out.println(ex);
    	}
    }
    //Checks if the Game should start
    public boolean gameStart(){
    	return newGame;
    }
    public Creature getPlayer(){
    	return player;
    }
    public int getLevel(){
    	return level;
    }
    
    //Returns greater value
    public int max(int a,int b){
    	return a>b?a:b;
    }
    //Returns lesser value
    public int min(int a,int b){
    	return a<b?a:b;
    }
    
    //MouseListener
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e){}
    public void mousePressed(MouseEvent e){
    	mb=true;
    }
    public void mouseReleased(MouseEvent e) {
    	mb=false;
    	if (570<mx && mx<750 && 380<my && my<430){
    		getWords=new inputBox("Load Game");
    		getWords.setVisible(true);
    	}
    	if (570<mx && mx<750 && 320<my && my<370){
    		newGame=true;
    	}
    	if(570<mx && mx<750 && 440<my && my<490){
    		drawn=!drawn;
    	}
    }
    
    //MouseMotionListener
    public void mouseMoved(MouseEvent e){
    	mx=e.getX();
    	my=e.getY();
    }
    public void mouseDragged(MouseEvent e){
    	mx=e.getX();
    	my=e.getY();
    }
    
    //Key Listener
    public void keyPressed(KeyEvent evt){
    	int i=evt.getKeyCode();
    	keys[i]=true;
    }
    public void keyReleased(KeyEvent evt){
    	int i=evt.getKeyCode();
    	keys[i]=false;
    }
    public void keyTyped(KeyEvent evt){
    }
}
/*Ascension.java
 *Main Java Class that runs the game. It manages and switches between 
 *the menu class and the game class as well as checking the status of 
 *both. It also advances the game class while ingame by calling the
 *functions that allow the player and the AI to move. It also calls
 *repaint for whichever class is currently on screen.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Ascension extends JFrame implements ActionListener{
	
	//Game class
	private GamePanel game;
	//MenuClass
	private GameMenu menu;
	//Boolean that checks which game-class to run
	private boolean inGame;
	
    Timer myTimer;
    //Constructor
	public Ascension(){
		super("Ascension - The Gathering");
    	setLayout(null);
    	setSize(800,600);
    	
    	//Since it starts in the Menu, game is not declared until the game starts.
    	menu = new GameMenu();
    	add(menu);
		inGame=false;
    	//Timer that lets the program continuously check its classes
    	myTimer=new Timer(10,this);
    	myTimer.start();
		
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	setVisible(true);
    	setResizable(false);
	}
	//Runs when an action is performed (in this case, every 10 milliseconds)
	public void actionPerformed(ActionEvent evt){
		//In Menu
		if (!inGame){
			//Checks if the Menu is ready to start the game
			if (menu.gameStart()){
				inGame=true;
				//If there is a player loaded in the menu
				//it will make the GamePanel with the player
				if (menu.getPlayer()!=null){
    				game = new GamePanel(menu.getLevel(),menu.getPlayer());
				}
				else{
    				game = new GamePanel();
				}
    			add(game);
				menu.setVisible(false);
				game.setVisible(true);
				game.grabFocus();
			}
			menu.repaint();
		}
		//In Game
		else{
			//If the game has ended
			if (game.gameEnd()){
				inGame=false;
				//Reset GameMenu
    			menu = new GameMenu();
    			add(menu);
				game.setVisible(false);
				menu.setVisible(true);
				menu.grabFocus();
			}
			//Is moving checks if it is drawing the moving animation
			else if (!game.isMoving()){
				//user input and enemy turn
				game.mainAction();
				game.advance();
			}
			else{
				//This checks if the player is moving
				if (!(game.getPlayer().getFx()==0 && game.getPlayer().getFy()==0)){
					//Centers the view on the player if the player is moving
					game.center();
				}
			}
			game.repaint();
		}
	}
	//Does Game thing
	public static void main(String[]args){Ascension game = new Ascension();
	}
	
}
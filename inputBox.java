/*inputBox.java
 *This is a simple JFrame that gets a string input from 
 *the user and returns it.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class inputBox extends JFrame implements ActionListener{

	JTextField wordBox=new JTextField(5);
	String current;
	boolean pressed;
	
    public inputBox(String context) {
		super("Window");
    	setLayout(null);
    	setSize(300,200);
    	
    	JLabel gameName=new JLabel("Game Name: ");
    	gameName.setLocation(20,50);
    	gameName.setSize(80,30);
    	add(gameName);
    	wordBox.setLocation(100,50);
    	wordBox.setSize(140,30);
    	add(wordBox);
    	
    	JButton enter = new JButton(context);
    	enter.setSize(100,30);
    	enter.setLocation(100,80);
    	enter.addActionListener(this);
    	add(enter);
    	
    	pressed=false;
    	
    	setVisible(false);
    	setResizable(false);
    }
    //Pressed is pretty self explanitoy
    public boolean getPressed(){
    	return pressed;
    }
    //Current is the string entered
    public String getCurrent(){
    	return current;
    }
    //If the button is pressed
	public void actionPerformed(ActionEvent evt){
    	if (!wordBox.getText().equals("")){
    		current=wordBox.getText();
    		pressed=true;
    	}
	}
}
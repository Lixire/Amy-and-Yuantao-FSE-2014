/*Item.java
 *This class stores item types, damage, effects etc.
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class Item {
	//Item Effects
	private String name, type, des, inf;
	private Image pic;
	private int damage, range,defense, hp, mp, str, vit, dex, intel, strReq, intReq;
	private String [] effects;
	private int slot = 0;
	//Constructor
    public Item(String stuff) {
    	inf = stuff;
    	String [] stats = stuff.split("/");
    	name = stats[0].replace("#"," ");
    	type = stats[1];
    	
    	if(isConsumable()){
    		hp = Integer.parseInt(stats[2]);
    		mp = Integer.parseInt(stats[3]);
    		str = Integer.parseInt(stats[4]);
    		vit =Integer.parseInt(stats[5]);
    		intel = Integer.parseInt(stats[6]);
    		dex = Integer.parseInt(stats[7]);
    		pic = new ImageIcon("graphics/Itemz/"+stats[8]+".png").getImage();
    		des = stats[9].replace("#"," ");
    	}
    	else if(isWorn()){
    		slot = Integer.parseInt(stats[2]);
    		defense = Integer.parseInt(stats[3]);
    		strReq = Integer.parseInt(stats[4]);
    		intReq = Integer.parseInt(stats[5]);
    		pic = new ImageIcon("graphics/Itemz/"+stats[6]+".png").getImage();
    		des = stats[7].replace("#"," ");
    	}
    	else if (isWeapon()){
    		damage = Integer.parseInt(stats[2]);
    		range = Integer.parseInt(stats[3]);
    		strReq = Integer.parseInt(stats[4]);
    		intReq = Integer.parseInt(stats[5]);
    		pic = new ImageIcon("graphics/Itemz/"+stats[6]+".png").getImage();
    		des = stats[7].replace("#"," ");
    	}

    }
    //Getter Methods
    public int getHP(){
    	return hp;
    }
     public int getMP(){
    	return mp;
    }
    public int getSTR(){
    	return str;
    }
    public int getVIT(){
    	return vit;
    }
    public int getDEX(){
    	return dex;
    }
    public int getINT(){
    	return intel;
    }
    public String getDes(){
    	return des;
    }
    public int getSlot(){
    	return slot;
    }
    public String getName(){
    	return name;
    }
    public Image getIcon(){
    	return pic;
    }
    public String getType(){
    	return type;
    }  
   	public int getArmour(){
   		return defense;
   	}
   	public boolean isWorn(){
   		return type.equals("worn");
   	}
   	public boolean isConsumable(){
   		return type.equals("consumable");
   	}
   	public boolean isWeapon(){
   		return type.equals("weapon");
   	}
   	public boolean isHat(){
   		return slot == 1 ? type.equals("worn"): false;
   	}
   	public boolean isArmour(){
   		return slot == 2 ? type.equals("worn"): false;
   	}
   	public boolean isFoot(){
   		return slot == 3 ? type.equals("worn"): false;
   	}
   	public boolean isOther(){
   		return slot == 4 ? type.equals("worn"): false;
   	}
    public void draw(int x,int y, Graphics g, GamePanel game){
    	g.drawImage(pic,x,y,game);
    }
    public String toString(){
    	return inf;
    }
}
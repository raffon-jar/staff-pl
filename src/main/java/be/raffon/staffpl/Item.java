package be.raffon.staffpl;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

public class Item {
	
	public ItemStack is;
	public ArrayList<String> commands;
	public String type;
	public String worlds;
	
	public Item(ItemStack is, ArrayList<String> commands, String type, String worlds) {
		this.is = is;
		this.commands = commands;
		this.type = type;
		this.worlds = worlds;
	}
	
	

}

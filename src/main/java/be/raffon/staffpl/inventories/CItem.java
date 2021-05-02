package be.raffon.staffpl.inventories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Consumer;

public class CItem {

	public Consumer<InventoryClickEvent> consumer;
	public ItemStack itemstack;
	public ArrayList<String> commands;
	public ArrayList<String> commandsright;
	public HashMap<String, String> variables;

	public CItem(ItemStack is, ArrayList<String> commands) {
		this.commands = commands;
		this.itemstack = is;
	}
	
	public CItem(ItemStack is, String command) {
		this.commands = new ArrayList<String>();
		this.commands.add(command);
		this.itemstack = is;
	}

	public CItem(ItemStack is, Consumer<InventoryClickEvent> consumer) {
		this.itemstack = is;
		this.consumer = consumer;
	}

	public CItem(Material mat, String title, String lore, ArrayList<String> commands) {
		this.commands = new ArrayList<String>();
		ItemStack is = new ItemStack(mat);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(title);
		
		String[] split = title.split("\n");
		im.setLore(Arrays.asList(split));
		is.setItemMeta(im);	
		
		this.itemstack = is;
	}
	
	public CItem(Material mat, String title, String lore, String command) {
		this.commands = new ArrayList<String>();
		this.commands.add(command);
		
		ItemStack is = new ItemStack(mat);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(title);
		
		String[] split = title.split("\n");
		im.setLore(Arrays.asList(split));
		is.setItemMeta(im);	
		
		this.itemstack = is;
	}
	
	public void addLeft(ArrayList<String> commands) {
		this.commandsright = commands;
	}
	
	public void addVariables(HashMap<String, String> variables) {
		this.variables = variables;
	}
	
	
	public void execute(Player p, HashMap<String, String> variables, CInventory cinv, InventoryClickEvent evt, InventoryManager inv, Integer page, Boolean right) {

		if(consumer != null) {
			consumer.accept(evt);
		}

		if(!right) {
			for(int i=0; i<commands.size(); i++) {
				String command = commands.get(i);
				if(command.equals("{nothing}")) {
					evt.setCancelled(true);
				}
				
				if(command.equals("{leave}")) {
					evt.setCancelled(true);
					p.closeInventory();
					inv.unregisterInventory(p.getUniqueId());
				}
				
				if(command.equals("{nextpage}")) {
					evt.setCancelled(true);
					p.closeInventory();
					cinv.changePage(page+1, p);
					return;
				}
				if(command.equals("{previouspage}")) {
					evt.setCancelled(true);
					p.closeInventory();
					cinv.changePage(page-1, p);
					return;
				}
				if(variables != null) {
					for (Map.Entry<String, String> entry : variables.entrySet()) {    
						command.replace("%"+entry.getKey()+"%", entry.getValue());
					}
				}

				if(command.indexOf("/") == -1) {
					command = "/" + command;
				}
				if(!command.equals("/{nothing}") && !command.equals("/{leave}")) {
					System.out.println(command + " " + command.substring(1, command.length()));
					Bukkit.dispatchCommand(p, command.substring(1, command.length()));
					Bukkit.dispatchCommand(p, command);
				}
				
				
			}
		} else {
			if(commandsright == null) {
				return;
			}
			for(int i=0; i<commandsright.size(); i++) {
				String command = commandsright.get(i);
				if(command.equals("{nothing}")) {
					evt.setCancelled(true);
				}
				
				if(command.equals("{leave}")) {
					evt.setCancelled(true);
					p.closeInventory();
					inv.unregisterInventory(p.getUniqueId());
				}
				
				if(command.equals("{nextpage}")) {
					evt.setCancelled(true);
					p.closeInventory();
					cinv.changePage(page+1, p);
					return;
				}
				
				if(command.equals("{previouspage}")) {
					evt.setCancelled(true);
					p.closeInventory();
					cinv.changePage(page-1, p);
					return;
				}
				
				if(variables != null) {
					for (Map.Entry<String, String> entry : variables.entrySet()) {    
						command.replace("%"+entry.getKey()+"%", entry.getValue());
					}
				}
				if(command.indexOf("/") == -1&& !command.equals("{nothing}")) {
					command = "/" + command;
				}
				
				if(!command.equals("/{nothing}")&& !command.equals("/{leave}")) {
					System.out.println(command + " " + command.substring(1, command.length()));
					Bukkit.dispatchCommand(p, command.substring(1, command.length()));
					Bukkit.dispatchCommand(p, command);
					//p.performCommand(command);
				}
			}
		}

	}
}

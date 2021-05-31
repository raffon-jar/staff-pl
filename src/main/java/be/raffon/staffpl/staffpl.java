package be.raffon.staffpl;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import be.raffon.staffpl.events.*;
import be.raffon.staffpl.utils.*;
import com.google.gson.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import be.raffon.staffpl.inventories.CInventory;
import be.raffon.staffpl.inventories.CItem;
import be.raffon.staffpl.inventories.InventoryManager;
import be.raffon.staffpl.inventories.Page;
import org.json.JSONObject;

public class staffpl extends JavaPlugin{
	
	public static String text = ChatColor.WHITE + "[" + ChatColor.RED + "STAFF" + ChatColor.WHITE + "] ";

	public static SQLManager sqlmanager;
    public static InventoryManager inventorymanager;
    static HashMap<UUID, Boolean> freezes;
	public static Reports reports;
    public static Baltop baltop;
    public static ArrayList<UUID> banned;

	private static Plugin plugin;


	@Override
	public void onEnable() {
		String host, database, username, password;
		Integer port;
        host = "localhost";
        port = 3306;
        database = "sf2021";
        username = "sf2021";
        password = "password";
        inventorymanager = new InventoryManager();
		sqlmanager = new SQLManager(host, port, database, username, password);
		freezes = new HashMap<UUID, Boolean>();
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            
		String sql = "CREATE TABLE IF NOT EXISTS staff_players (\n"
					+ "	username VARCHAR(100) PRIMARY KEY,\n"
					+ "	staff BOOLEAN NOT NULL,\n"
					+ "	page INTEGER, \n"
					+ " world VARCHAR(100), \n"
					+ " backup TEXT \n"
					+ ");";
		SQLManager.getInstance().update(sql);


		sql = "CREATE TABLE IF NOT EXISTS staff_baltop (\n"
				+ "	username VARCHAR(100) PRIMARY KEY,\n"
				+ "	normal_blocks INTEGER NOT NULL DEFAULT 1,\n"
				+ "	diamonds INTEGER NOT NULL DEFAULT 0,  \n"
				+ "	golds INTEGER NOT NULL DEFAULT 0, \n"
				+ "	coal INTEGER NOT NULL DEFAULT 0, \n"
				+ "	iron INTEGER NOT NULL DEFAULT 0, \n"
				+ "	lapis INTEGER NOT NULL DEFAULT 0, \n"
				+ "	redstone INTEGER NOT NULL DEFAULT 0, \n"
				+ "	netherite INTEGER NOT NULL DEFAULT 0, \n"
				+ "	emerald INTEGER NOT NULL DEFAULT 0,\n"
				+ "	normal_nether INTEGER NOT NULL DEFAULT 1\n"
				+ ");";
		SQLManager.getInstance().update(sql);



		sql = "CREATE TABLE IF NOT EXISTS staff_reports (\n"
				+ "	date VARCHAR(100),\n"
				+ "	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,\n"
				+ "	reason TEXT, \n"
				+ "	reporter VARCHAR(100), \n"
				+ "	player VARCHAR(100), \n"
				+ "	status INTEGER \n"
				+ ");";
		SQLManager.getInstance().update(sql);


		sql = "CREATE TABLE IF NOT EXISTS staff_freeze (\n"
				+ "	username VARCHAR(100) PRIMARY KEY,\n"
				+ "	freeze BOOLEAN,\n"
				+ "	world 	VARCHAR(100),\n"
				+ "	x INTEGER, \n"
				+ "	y INTEGER, \n"
				+ "	z INTEGER \n"
				+ ");";
		SQLManager.getInstance().update(sql);

        sql = "CREATE TABLE IF NOT EXISTS offline_clear (\n"
                + "	username VARCHAR(100) PRIMARY KEY,\n"
                + "	date TIMESTAMP default CURRENT_TIMESTAMP"
                + ");";
        SQLManager.getInstance().update(sql);

		sql = "CREATE TABLE IF NOT EXISTS staff_logs (\n"
				+ "	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,\n"
				+ "	date VARCHAR(255),\n"
				+ "	reason TEXT,\n"
				+ "	start TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(255), \n"
				+ "	time VARCHAR(100), \n"
				+ "	type VARCHAR(100), \n"
				+ " username VARCHAR(100), \n"
				+ " moderator VARCHAR(100) \n"
				+ ");";
		SQLManager.getInstance().update(sql);

		sql = "CREATE TABLE IF NOT EXISTS staff_tempmute (\n"
				+ "	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,\n"
				+ "	reason TEXT,\n"
				+ "	start TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(255), \n"
				+ "	username VARCHAR(100), \n"
				+ "	time INTEGER \n"
				+ ");";
		SQLManager.getInstance().update(sql);

		sql = "CREATE TABLE IF NOT EXISTS staff_tempban (\n"
				+ "	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,\n"
				+ "	reason TEXT,\n"
				+ "	start TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(255), \n"
				+ "	username VARCHAR(100), \n"
				+ "	time INTEGER \n"
				+ ");";
		SQLManager.getInstance().update(sql);


		sql = "CREATE TABLE IF NOT EXISTS staff_backups (\n"
				+ "	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,\n"
				+ "	backup TEXT,\n"
				+ "	world VARCHAR(100), \n"
				+ "	username VARCHAR(100) \n"
				+ ");";
		SQLManager.getInstance().update(sql);

		
		System.out.println("Staff plugin succefuly loaded !");
		System.setProperty("file.encoding", "UTF-8");
		getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
		getServer().getPluginManager().registerEvents(new PlayerInteracts(), this);
		getServer().getPluginManager().registerEvents(new BlockBreakPlace(), this);
		getServer().getPluginManager().registerEvents(new PlayerInteractEntity(), this);
		getServer().getPluginManager().registerEvents(new onCommand(), this);
		plugin = this;

		reports = new Reports();
		baltop = new Baltop();
		banned = new ArrayList<UUID>();
		AtomicReference<ArrayList<UUID>> arr = new AtomicReference<ArrayList<UUID>>();
		SQLManager.getInstance().query(" SELECT * FROM staff_tempban\r\n;", rs -> {
			Boolean found = false;
			try {
				ArrayList<UUID> array = new ArrayList<UUID>();
				while(rs.next()) {
					array.add(UUID.fromString(rs.getString("username")));
				}
				arr.set(array);
			} catch (SQLException e) {

			}
		});
		banned = arr.get();
	}

	public static Plugin getPlugin() {
		return plugin;
	}

    public static String getActualServer() {
    	HashMap<Integer, String> servers = new HashMap<Integer, String>();
    	servers.put(25565, "test");
    	servers.put(25564, "eeb3");
    	servers.put(25566, "hub");
    	servers.put(25567, "pvp");
    	servers.put(25568, "survie");
    	servers.put(25569, "plot");
    	servers.put(25570, "blockparty");
    	servers.put(25571, "buildbattle");
    	servers.put(25572, "hungergames");
    	servers.put(25573, "pitchout");
		return servers.get(Bukkit.getPort());
    }
	
	public static ArrayList<Item> getItems() {

		/* TODO: Take items from config*/
		ArrayList<Item> array = new ArrayList<Item>();
		
		ItemStack is = new ItemStack(Material.ENDER_EYE);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GREEN + "Vanish");
		is.setItemMeta(im);
		ArrayList<String> commands = new ArrayList<String>();commands.add("vanish");
		array.add(new Item(is, commands, "click", "*"));
		
		ItemStack is1 = new ItemStack(Material.STICK);
		ItemMeta im1 = is1.getItemMeta();
		im1.setDisplayName(ChatColor.GREEN + "Knockback");
		is1.setItemMeta(im1);
		ArrayList<String> commands1 = new ArrayList<String>();
		is1.addUnsafeEnchantment(Enchantment.KNOCKBACK, 5);
		array.add(new Item(is1, commands1, "click", "*"));
		
		ItemStack is11 = new ItemStack(Material.ENDER_PEARL);
		ItemMeta im11 = is11.getItemMeta();
		im11.setDisplayName(ChatColor.GREEN + "Random TP");is11.setItemMeta(im11);
		ArrayList<String> commands11 = new ArrayList<String>();commands11.add("randomtp");
		array.add(new Item(is11, commands11, "click", "*"));
		
		ItemStack is111 = new ItemStack(Material.FEATHER);
		ItemMeta im111 = is111.getItemMeta();
		im111.setDisplayName(ChatColor.GREEN + "Fly");is111.setItemMeta(im111);
		ArrayList<String> commands111 = new ArrayList<String>();commands111.add("fly");
		array.add(new Item(is111, commands111, "click", "*"));
		
		ItemStack is1111 = new ItemStack(Material.BLAZE_ROD);
		ItemMeta im1111 = is1111.getItemMeta();
		im1111.setDisplayName(ChatColor.GREEN + "Freeze");is1111.setItemMeta(im1111);
		ArrayList<String> commands1111 = new ArrayList<String>();commands1111.add("freeze %player%");
		array.add(new Item(is1111, commands1111, "player", "*"));
		
		ItemStack is11111 = new ItemStack(Material.PAPER);
		ItemMeta im11111 = is11111.getItemMeta();
		im11111.setDisplayName(ChatColor.GREEN + "Reports");is11111.setItemMeta(im11111);
		ArrayList<String> commands11111 = new ArrayList<String>();commands11111.add("reports");
		array.add(new Item(is11111, commands11111, "click", "*"));
		
		ItemStack is111111 = new ItemStack(Material.BOOK);
		ItemMeta im111111 = is111111.getItemMeta();
		im111111.setDisplayName(ChatColor.GREEN + "Inspect Player");is111111.setItemMeta(im111111);
		ArrayList<String> commands111111 = new ArrayList<String>();commands111111.add("inspect %player%");
		array.add(new Item(is111111, commands111111, "player", "*"));
		
		ItemStack is1111111 = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta im1111111 = is1111111.getItemMeta();
		im1111111.setDisplayName(ChatColor.GREEN + "Kill");is1111111.setItemMeta(im1111111);
		ArrayList<String> commands1111111 = new ArrayList<String>();commands1111111.add("kill %player%");
		array.add(new Item(is1111111, commands1111111, "player", "*"));
		
		ItemStack is11111111 = new ItemStack(Material.GOLDEN_SWORD);
		ItemMeta im11111111 = is11111111.getItemMeta();
		im11111111.setDisplayName(ChatColor.GREEN + "Sanction");is11111111.setItemMeta(im11111111);
		ArrayList<String> commands11111111 = new ArrayList<String>();commands11111111.add("sanction %player%");
		array.add(new Item(is11111111, commands11111111, "player", "*"));
		
		ItemStack is111111111 = new ItemStack(Material.GOLD_INGOT);
		ItemMeta im111111111 = is111111111.getItemMeta();
		im111111111.setDisplayName(ChatColor.GREEN + "Baltop");is111111111.setItemMeta(im111111111);
		ArrayList<String> commands111111111 = new ArrayList<String>();commands111111111.add("baltop");
		array.add(new Item(is111111111, commands111111111, "click", "survie-survie"));
		
		return array;
	}

	@Override
	public void onDisable() {
		reports.storetoDB();
		baltop.storetoDB();
	}



	public static Boolean getStaff(Player pl) {
		AtomicReference<Boolean> bool = new AtomicReference<Boolean>();
		if(!pl.hasPermission("staff.staff")) return false;
		SQLManager.getInstance().query("SELECT * FROM staff_players WHERE username = '" + pl.getUniqueId() + "';", rs -> {
			try {
				if(rs.next()) {
					Boolean staff = rs.getBoolean("staff");
					String world = rs.getString("world");
					if(staff && world.endsWith(pl.getLocation().getWorld().getName()) && world.startsWith(getActualServer())) {
						bool.set(true);
						return;
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bool.set(false);
			return;
		});
		return bool.get();
	}
	
	public static Boolean isFreezed(Player pl) {
		if(freezes.get(pl.getUniqueId()) == null) return false;
		return true;
	}
	
	public static Boolean getStaff(Player pl, String world_from) {
		AtomicReference<Boolean> bool = new AtomicReference<Boolean>();
		if(!pl.hasPermission("staff.staff")) return false;
		SQLManager.getInstance().query("SELECT * FROM staff_players WHERE username = '" + pl.getUniqueId() + "';", result -> {
			try {
				if(result.next()) {
					Boolean staff = result.getBoolean("staff");
					String world = result.getString("world");
					if(staff && ((world_from.endsWith(world) && world_from.startsWith(getActualServer())) || world_from.equals("*"))) {
						bool.set(true);
						return;
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bool.set(false);
			return;
		});

		return bool.get();
		
	}
	
	public static String getfuther(int value, String[] args) {
		StringBuilder message = new StringBuilder();
		for(int i = value; i < args.length; i++){
			message.append(" ").append(args[i]);
		}
		String str = message.toString().substring(1, message.toString().length());
		return str;
	}
	
	public static void freeze(Player pl, Boolean freeze) {
		Location loc = pl.getLocation();
		if(freeze) {
			freezes.put(pl.getUniqueId(), true);
		} else {
			freezes.remove(pl.getUniqueId());
		}
	}
	
	@SuppressWarnings({ "unchecked"})
	public static void tempsanction(UUID player, String type, String time, String reason) {
		JSONObject sanction = new JSONObject();
		if(type == "ban") {
			Integer ms = 0;
			if(time.indexOf("d") != -1) {
				Integer index = time.indexOf("d");
				Integer t = Integer.parseInt(time.substring(0, index));
				ms = t*1000*60*60*24;
				
			} 	else if(time.indexOf("h") != -1) {
				Integer index = time.indexOf("h");
				Integer t = Integer.parseInt(time.substring(0, index));
				ms = t*1000*60*60;
				
			}	else if(time.indexOf("m") != -1) {
				Integer index = time.indexOf("m");
				Integer t = Integer.parseInt(time.substring(0, index));
				ms = t*1000*60;
				
			}	else if(time.indexOf("s") != -1) {
				Integer index = time.indexOf("s");
				Integer t = Integer.parseInt(time.substring(0, index));
				ms = t*1000;
				
			} else {
				return;
			}
			
			sanction.put("time", String.valueOf(ms));
			sanction.put("start", String.valueOf(System.currentTimeMillis()));
			sanction.put("name", player);
			sanction.put("reason", reason);
			
			
			int seconds = (int) ms / 1000;
			int minutes = (int) seconds / 60;
			int hours   = (int) minutes / 60;
			int days   = (int) hours / 24;
			
			if(Bukkit.getOfflinePlayer(player).isOnline()) {
				Bukkit.getPlayer(player).kickPlayer("You are banned on this server for " + reason + " \n You will be unbanned in "+ ChatColor.RED + days + ChatColor.WHITE + " days " + ChatColor.RED + hours + ChatColor.WHITE + " hours "+ ChatColor.RED + minutes + ChatColor.WHITE + " minutes" );
			}

			SQLManager.getInstance().update(" INSERT INTO staff_tempban (time, reason, username) \r\n" +
					" VALUES ('"+ String.valueOf(ms) + "', '" + reason + "', '"+ player +"')");
		} else if(type == "mute") {
			Integer ms = 0;
			if(time.indexOf("d") != -1) {
				Integer index = time.indexOf("d");
				Integer t = Integer.parseInt(time.substring(0, index));
				ms = t*1000*60*60*24;
				
			} 	else if(time.indexOf("h") != -1) {
				Integer index = time.indexOf("h");
				Integer t = Integer.parseInt(time.substring(0, index));
				ms = t*1000*60*60;
				
			}	else if(time.indexOf("m") != -1) {
				Integer index = time.indexOf("m");
				Integer t = Integer.parseInt(time.substring(0, index));
				ms = t*1000*60;
				
			}	else if(time.indexOf("s") != -1) {
				Integer index = time.indexOf("s");
				Integer t = Integer.parseInt(time.substring(0, index));
				ms = t*1000;
				
			} else {
				return;
			}
			
			sanction.put("time", String.valueOf(ms));
			sanction.put("start", String.valueOf(System.currentTimeMillis()));
			sanction.put("name", player);
			sanction.put("reason", reason);

			SQLManager.getInstance().update(" INSERT INTO staff_tempmute (time, reason, username) \r\n" +
					" VALUES ('"+ String.valueOf(ms) + "', '" + reason + "', '"+ player +"')");
			
			
		}

	}

	
	@SuppressWarnings("deprecation")
	@EventHandler
    public void OnPlayerInteract(PlayerInteractEvent  e) {
        Player p = e.getPlayer();
        if(p.getOpenInventory().getType() != InventoryType.CRAFTING && p.getOpenInventory().getType() != InventoryType.CREATIVE) {
        	return;
        }
        
        if(getStaff(p)) {
        	e.setCancelled(true);
        	ArrayList<Item> arr = getItems();
        	ItemStack hand = p.getItemInHand();
        	if(hand == null || hand.getType() == Material.AIR) {
        		return;
        	}
        	for(int i=0; i<arr.size(); i++) {
        		
        		Item is = arr.get(i);
        		
        		ItemStack item = is.is;
        		
        		String type = is.type;
        		
        		if(item.equals(hand)) {
        			
        			ArrayList<String> commands = is.commands;
        			
        			if(type.equals("click")) {
            			for(int k=0; k<commands.size(); k++) {
            				
            				String command = commands.get(k);    
            				
            				if(command.indexOf("/") == -1) {
            					command = "/" + command;
            				}
            				
            				p.chat(command);
            				
            			}
        			}

        		}
        		
        		if(hand.getItemMeta().getDisplayName().startsWith(ChatColor.BLUE + "Page") && hand.getType() == Material.ARROW) {
        			String name = hand.getItemMeta().getDisplayName();
        			Integer page = Integer.parseInt(name.substring((ChatColor.BLUE+"Page ").length()));
        			p.getInventory().clear();
        			setstaffmode(p , true, page, null);
        			
        		}
        		
        		if(hand.getItemMeta().getDisplayName().startsWith(ChatColor.BLUE + "Exit") && hand.getType() == Material.BARRIER) {
        			String name = hand.getItemMeta().getDisplayName();
        			p.getInventory().clear();
        			setstaffmode(p , false, 0, null);
        			
        		}
        		
        	}
        }
    }

    
	public static void setstaffmode(Player player, Boolean bool, Integer page, JsonObject backup) {
		ArrayList<Item> its = getItems();
		if(bool) {
			
			// First page
			if(page == 1) {
				for(int i = 0; i<8; i++) {
					if(its.get(i).worlds.indexOf(getActualServer()+"-"+player.getWorld().getName()) != -1 || its.get(i).worlds.equals("*")) {
						player.getInventory().setItem(i, its.get(i).is);
					}
				}
				
				ItemStack pag = new ItemStack(Material.ARROW, 1);
				ItemMeta pagem = pag.getItemMeta();
				pagem.setDisplayName(ChatColor.BLUE + "Page 2");
				pag.setItemMeta(pagem);
				
				player.getInventory().setItem(8, pag);
			}
			
			//last page
			
			else if(Math.ceil((its.size()-8.0)/7.0) == (page-1)) {
				int slot = 1;
				for(int i = 8 + (page-2)*7; i<its.size(); i++) {
					if(its.get(i).worlds.indexOf(getActualServer()+"-"+player.getWorld().getName()) != -1 || its.get(i).worlds.equals("*")) {
						player.getInventory().setItem(slot, its.get(i).is);
						slot++;
					}
					
				}
				
				
				ItemStack pag = new ItemStack(Material.ARROW, 1);
				ItemMeta pagem = pag.getItemMeta();
				pagem.setDisplayName(ChatColor.BLUE + "Page "+String.valueOf(page-1));
				pag.setItemMeta(pagem);
				
				ItemStack br = new ItemStack(Material.BARRIER, 1);
				ItemMeta brm = br.getItemMeta();
				brm.setDisplayName(ChatColor.BLUE + "Exit");
				br.setItemMeta(brm);
				
				player.getInventory().setItem(0, pag);
				player.getInventory().setItem(8, br);
			}
			
			//page in the middle
			else {
				int slot = 1;
				for(int i = 8 + (page-1)*7; i<8+page*7; i++) {
					if(its.get(i).worlds.indexOf(getActualServer()+"-"+player.getWorld().getName()) != -1 || its.get(i).worlds.equals("*")) {
						player.getInventory().setItem(i, its.get(i).is);
					}
					slot++;
				}
				
				ItemStack pagprev = new ItemStack(Material.ARROW, 1);
				ItemMeta pagprevem = pagprev.getItemMeta();
				pagprevem.setDisplayName(ChatColor.BLUE + "Page "+String.valueOf(page-1));
				pagprev.setItemMeta(pagprevem);
				
				ItemStack pagafter = new ItemStack(Material.ARROW, 1);
				ItemMeta pagafterm = pagafter.getItemMeta();
				pagafterm.setDisplayName(ChatColor.BLUE + "Page "+String.valueOf(page+1));
				pagafter.setItemMeta(pagafterm);
				
				player.getInventory().setItem(0, pagprev);
				player.getInventory().setItem(8, pagafter);
			}

			if(backup != null) {
				SQLManager.getInstance().update("INSERT INTO staff_players (username, staff, page, world, backup)\r\n" +
						"VALUES ('" + player.getUniqueId() + "', true, "+ String.valueOf(page) + ", '"+ getActualServer()+"-"+player.getWorld().getName() + "', '" + backup.toString() + "')\r\n" +
						"ON DUPLICATE KEY UPDATE staff=true, world='"+ getActualServer()+"-"+player.getWorld().getName() + "', page="+ String.valueOf(page) + ", backup='"+ backup.toString() + "'");
			} else {
				SQLManager.getInstance().update("INSERT INTO staff_players (username, staff, page, world)\r\n" +
						"VALUES ('" + player.getUniqueId() + "', true, "+ String.valueOf(page) + ", '"+ getActualServer()+"-"+player.getWorld().getName() + "')\r\n" +
						"ON DUPLICATE KEY UPDATE staff=true, world='"+ getActualServer()+"-"+player.getWorld().getName() + "', page="+ String.valueOf(page) + "");
			}
		} else {
			recover(player, null);
			SQLManager.getInstance().update(" UPDATE staff_players\r\n" +
					" SET staff = false\r\n" +
					" WHERE username = '"+ player.getUniqueId() + "';");
		}


	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static JsonObject backupeverything(Player player) {
		JsonObject pl = new JsonObject();
		pl.add("name", new JsonParser().parse(player.getDisplayName()));
		JsonArray list = new JsonArray();
		PlayerInventory inv = player.getInventory();
		for(int i=0; i<36; i++) {
			if(inv.getItem(i) != null) {
				ItemStack it = inv.getItem(i);
				JsonObject obj = serialize(it);
				if(obj != null) list.add(obj);

			}
		}
		pl.add("inv", list);
		pl.add("x", new JsonParser().parse(String.valueOf(player.getLocation().getBlockX())));
		pl.add("y", new JsonParser().parse(String.valueOf(player.getLocation().getBlockY())));
		pl.add("z", new JsonParser().parse(String.valueOf(player.getLocation().getBlockZ())));
		pl.add("w", new JsonParser().parse(getActualServer()+"-"+player.getLocation().getWorld().getName()));
		ItemStack[] armor = inv.getArmorContents();
		JsonArray l = new JsonArray();
		for(int i=0; i<armor.length; i++) {
			if(armor[i] != null && armor[i].getType() != Material.AIR) {
				ItemStack it = armor[i];
				JsonObject obj = serialize(it);
				if(obj != null) l.add(obj);

			}
		}
		pl.add("armor", l);
		player.getInventory().clear();
		player.updateInventory();
		return pl;
	}
	
	@SuppressWarnings("deprecation")
	public static void recover(Player player, JsonObject o) {
		player.getInventory().clear();
		if(o == null) {
			AtomicReference<JsonObject> json = new AtomicReference<JsonObject>();

			SQLManager.getInstance().query("SELECT * FROM staff_players WHERE username = '" + player.getUniqueId() + "';", result -> {
				try {
					if(!result.next()) {
						return;
					}
					json.set(createJSONObject(result.getString("backup")));
					return;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				json.set(null);
			});
			if(json.get() == null) return;
			o = json.get();
		}
		int x =  Integer.parseInt((o.get("x").getAsString()));
		int y =  Integer.parseInt((o.get("y").getAsString()));
		int z =  Integer.parseInt((o.get("z").getAsString()));
		String world = o.get("w").getAsString();
		
		String server = world.substring(0, world.indexOf("-"));
		String world_str = world.substring(world.indexOf("-")+1);
		
		if(server.equals(getActualServer())) {
			World w = Bukkit.getWorld(world_str);
			player.teleport(new Location(w, x, y, z));
		}
		PlayerInventory inv = player.getInventory();
		inv.clear();
		JsonArray i = o.get("inv").getAsJsonArray();
		for(int v=0; v<i.size(); v++) {
			if(i.get(v) != null) {
				ItemStack item = deserialize(i.get(v).getAsJsonObject());
				inv.setItem(v, item);
			}
		}
		JsonArray li = o.get("armor").getAsJsonArray();
		ItemStack[] lis = new ItemStack[4];
		for(int v=0; v<li.size(); v++) {
			lis[v] = deserialize(li.get(v).getAsJsonObject());
		}
		inv.setArmorContents(lis);
		player.updateInventory();

	}
	
	public static JsonObject createJSONObject(String jsonString){
	    JsonObject jsonObject= new JsonObject();
		JsonParser jsonParser=new JsonParser();
	    if ((jsonString != null) && !(jsonString.isEmpty())) {
	    	jsonObject = jsonParser.parse(jsonString).getAsJsonObject();
	    }
	    return jsonObject;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
	public static JsonObject serialize(ItemStack is) {
		try {
			JsonObject obj = new JsonObject();
			ItemMeta meta = is.getItemMeta();
			obj.add("name", new JsonParser().parse(meta.getDisplayName()));
			obj.add("amount", new JsonParser().parse(String.valueOf(is.getAmount())));
			if(is.getEnchantments() != null) {
				Map<Enchantment, Integer> ench = is.getEnchantments();
				Iterator it = ench.entrySet().iterator();
				JsonArray enchants = new JsonArray();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					JsonObject enchant = new JsonObject();
					enchant.add("name", new JsonParser().parse(((Enchantment) pair.getKey()).getName()));
					enchant.add("int", new JsonParser().parse(String.valueOf(pair.getValue())));
					enchants.add(enchant);
				}
				obj.add("enchantment", enchants);
			}
			obj.add("durability", new JsonParser().parse(String.valueOf(is.getDurability())));
			String material = String.valueOf(is.getType());
			obj.add("material", new JsonParser().parse(material));
			return obj;
		} catch(JsonSyntaxException e) {
			return null;
		}

		
	}
	

	@SuppressWarnings("deprecation")
	public static ItemStack deserialize(JsonObject o) {
		ItemStack is = new ItemStack(Material.matchMaterial(o.get("material").getAsString()), Integer.parseInt(o.get("amount").getAsString()));
		is.setDurability(o.get("durability").getAsShort());
		ItemMeta meta = is.getItemMeta();
		if(!(o.get("name") instanceof JsonNull)) { meta.setDisplayName(o.get("name").getAsString()); }
		JsonArray ar = (JsonArray) o.get("enchantment");
		for(int k=0; k<ar.size(); k++) {
			JsonObject en = (JsonObject) ar.get(k);
			meta.addEnchant(Enchantment.getByName(en.get("name").getAsString()), Integer.parseInt(en.get("int").getAsString()), true);
		}
		is.setItemMeta(meta);

		Gson gson = new Gson();
		return is;
		
	}




	@SuppressWarnings("deprecation")
	public static void openInv(String inv, Player player, String otherinfos) {
		
		if(inv.equals("reports")) {
			CInventory cinv = new CInventory();

				ArrayList<Report> reportsARR = null;
				if(otherinfos != null) {
						reportsARR = reports.getFromPlayer(Bukkit.getOfflinePlayer(otherinfos).getUniqueId());

				} else {
					reportsARR = reports.reports;
				}
				ArrayList<CItem> citems = new ArrayList<CItem>();
				for(int i=0; i<reportsARR.size(); i++) {
					System.out.println("hey");
					Report report = reportsARR.get(i);
					Timestamp date = report.date;
					String reason = report.reason;
					UUID tplayer = report.player;
					String name = Bukkit.getOfflinePlayer(tplayer).getName();
					UUID reporter = report.reporter;
					String key = report.key;

					ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
					SkullMeta meta = (SkullMeta) skull.getItemMeta();
					meta.setDisplayName(name);

					List<String> Lore = new ArrayList<String>();


					String namerep = Bukkit.getOfflinePlayer(reporter).getName();

					Lore.add(ChatColor.WHITE + "Player: " + ChatColor.BLUE + name);
					Lore.add(ChatColor.WHITE + "Reason: " + ChatColor.BLUE + reason);
					Lore.add(ChatColor.WHITE + "Date: " + ChatColor.BLUE + date);
					Lore.add(ChatColor.WHITE + "Reporter: " + ChatColor.BLUE + namerep);

					Lore.add(ChatColor.ITALIC + "Right-Click to inspect player");
					Lore.add(ChatColor.ITALIC + "Left-Click to mark as completed");
					meta.setLore(Lore);

					meta.setOwningPlayer(Bukkit.getOfflinePlayer(tplayer));
					skull.setItemMeta(meta);

					CItem it = new CItem(skull, "inspect " + name);
					ArrayList<String> arr = new ArrayList<String>();arr.add("reportcomplete "+key);arr.add("{leave}");
					it.addLeft(arr);
					citems.add(it);
				}
				if(citems.size() > 0) {
					for(int i=0; i<Math.ceil(citems.size()/45.0); i++) {
						Boolean end = false;
						if(i == Math.ceil(citems.size()/45.0)-1) {
							end = true;
						}
						Page page = new Page(5, "Reports", i, end);
						ArrayList<CItem> Page = new ArrayList<CItem>();
						for(int k=i*45; k<(i+1)*45 && k<citems.size(); k++) {
							CItem cit = citems.get(k);
							page.addItem(cit, k%45);
						}
						cinv.addPage(page);
						inventorymanager.registerInventory(player.getUniqueId(), cinv);

					}
					cinv.display(player);
				} else {
					player.sendMessage(text + "There is no data !");
				}
		} else if(inv.equals("baltop")) {
			CInventory cinv = new CInventory();
			ArrayList<CItem> citems = new ArrayList<CItem>();
			ArrayList<Pstats> stats = baltop.pstats;
			for(int i=0;i<stats.size(); i++ ) {
				Pstats statse = stats.get(i);
				if(!banned.contains(statse.player))  {
					Integer diamond = statse.diamonds;
					Integer iron = statse.iron;
					Integer golds =statse.golds;
					Integer netherite = statse.netherite;
					Integer coal = statse.Coals;
					UUID username = statse.player;
					Integer normalblocks = statse.normal_blocks;
					Integer normal_nether = statse.normal_nether;

					ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
					SkullMeta meta = (SkullMeta) skull.getItemMeta();
					String name = null;
					name = Bukkit.getOfflinePlayer(username).getName();
					meta.setDisplayName(name);

					List<String> Lore = new ArrayList<String>();

					DecimalFormat df = new DecimalFormat("###.##");
					Lore.add(ChatColor.WHITE + "Diamond: " + ChatColor.BLUE + String.valueOf((diamond*1.0/(normalblocks+diamond))*100).substring(0, 3) + "%" + ChatColor.WHITE +" / Diamands:" + ChatColor.BLUE + diamond);
					Lore.add(ChatColor.WHITE + "Gold: " + ChatColor.BLUE + String.valueOf((golds*1.0/(normalblocks+golds))*100).substring(0, 3) + "%" + ChatColor.WHITE +" / Golds:" + ChatColor.BLUE + golds);
					Lore.add(ChatColor.WHITE + "Coal: " + ChatColor.BLUE + String.valueOf((coal*1.0/(normalblocks+coal))*100).substring(0, 3) + "%" + ChatColor.WHITE +" / Coal:" + ChatColor.BLUE + coal);
					Lore.add(ChatColor.WHITE + "Iron: " + ChatColor.BLUE + String.valueOf((iron*1.0/(normalblocks+iron))*100).substring(0, 3) + "%" + ChatColor.WHITE +" / Irons:" +ChatColor.BLUE +  iron);
					Lore.add(ChatColor.WHITE + "Netherite: " + ChatColor.BLUE + String.valueOf((netherite*1.0/(normal_nether+netherite))*100).substring(0, 3) + "%" + ChatColor.WHITE +" / Netherite:" +ChatColor.BLUE +  netherite);
					Lore.add(ChatColor.WHITE + "Normal blocks: " + ChatColor.BLUE + normalblocks );
					Lore.add(ChatColor.WHITE + "Normal nether blocks: " + ChatColor.BLUE + normal_nether);

					Lore.add(ChatColor.ITALIC + "Right-Click to inspect player");
					meta.setLore(Lore);

					meta.setOwningPlayer(Bukkit.getOfflinePlayer(username));
					skull.setItemMeta(meta);

					CItem it = new CItem(skull, "inspect " + name);
					citems.add(it);
				}

			}

			if(citems.size() > 0) {
				for(int i=0; i<Math.ceil(citems.size()/45.0); i++) {
					Boolean end = false;
					if(i == Math.ceil(citems.size()/45.0)-1) {
						end = true;
					}
					Page page = new Page(5, "Reports", i, end);
					ArrayList<CItem> Page = new ArrayList<CItem>();
					for(int k=i*45; k<(i+1)*45 && k<citems.size(); k++) {
						CItem cit = citems.get(k);
						page.addItem(cit, k%45);
					}
					cinv.addPage(page);
					inventorymanager.registerInventory(player.getUniqueId(), cinv);

				}
				cinv.display(player);
			} else {
				player.sendMessage(text + "There is no data !");
			}

			cinv.display(player);
			inventorymanager.registerInventory(player.getUniqueId(), cinv);


		} else if(inv.equals("inspect")) {
			CInventory cinv = new CInventory();
			Page page = new Page(2, "Inspect " + otherinfos, 1, true);
	 		
			ItemStack sanction = new ItemStack(Material.STICK, 1);
			ItemMeta sanctionm = sanction.getItemMeta();
			sanctionm.setDisplayName(ChatColor.GREEN + "Sanction");
			ArrayList<String> commands = new ArrayList<String>();commands.add("{leave}");commands.add("sanction " + otherinfos);
			sanction.setItemMeta(sanctionm);
			CItem sanctioncit = new CItem(sanction, commands);
			
			ItemStack invo = new ItemStack(Material.CHEST, 1);
			ItemMeta invm = invo.getItemMeta();
			invm.setDisplayName(ChatColor.GREEN + "Open Inventory");
			invo.setItemMeta(invm);
			ArrayList<String> commandse = new ArrayList<String>();commandse.add("{leave}");commandse.add("invsee " + otherinfos);
			CItem invoit = new CItem(invo, commandse);
			
			ItemStack fr = new ItemStack(Material.BLAZE_ROD, 1);
			ItemMeta frm = fr.getItemMeta();
			frm.setDisplayName(ChatColor.GREEN + "Freeze");
			fr.setItemMeta(frm);
			ArrayList<String> commande = new ArrayList<String>();commande.add("{leave}");commande.add("freeze " + otherinfos);
			CItem freeit = new CItem(fr, commande);
			
			ItemStack tp = new ItemStack(Material.ENDER_PEARL, 1);
			ItemMeta tpm = tp.getItemMeta();
			tpm.setDisplayName(ChatColor.GREEN + "Teleport to player");
			tp.setItemMeta(tpm);
			ArrayList<String> comman = new ArrayList<String>();comman.add("{leave}");
			if(player.getServer().getPlayer(otherinfos) != null) {
				comman.add("tp " + otherinfos);
			} else {
				comman.add("otp " + otherinfos);
			}

			CItem tpi = new CItem(tp, comman);
			CItem tp2i;
			ItemStack tp2 = new ItemStack(Material.ENDER_PEARL, 1);
			ItemMeta tp2m = tp2.getItemMeta();
			tp2m.setDisplayName(ChatColor.GREEN + "Teleport player to you");
			tp2.setItemMeta(tp2m);
			ArrayList<String> commandes = new ArrayList<String>();commandes.add("{leave}");commandes.add("tp " + otherinfos + " " + player.getName());
			tp2i = new CItem(tp2, commandes);
			
			ItemStack his = new ItemStack(Material.PAPER, 1);
			ItemMeta hism = his.getItemMeta();
			hism.setDisplayName(ChatColor.GREEN + "Punishments history");
			ArrayList<String> commandi = new ArrayList<String>();commandi.add("{leave}");commandi.add("modlogs " + otherinfos);
			his.setItemMeta(hism);
			CItem hisi = new CItem(his, commandi);

            ItemStack clear = new ItemStack(Material.OBSIDIAN, 1);
            ItemMeta clearm = his.getItemMeta();
			clearm.setDisplayName(ChatColor.GREEN + "Clear Inventory");
            ArrayList<String> clearmi = new ArrayList<String>();clearmi.add("{leave}");

            if(player.getServer().getPlayer(otherinfos) != null) {
                clearmi.add("clear " + otherinfos);
            } else {
                clearmi.add("offlineclear " + otherinfos);
            }

            clear.setItemMeta(clearm);
            CItem clearmin = new CItem(clear, clearmi);
			
			ItemStack pane = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1);
			ItemMeta panem = pane.getItemMeta();
			panem.setDisplayName(" ");
			pane.setItemMeta(panem);
			
			page.addItem(new CItem(pane, "{nothing}"),  0);
			page.addItem(new CItem(pane, "{nothing}"), 1);
			page.addItem(new CItem(pane, "{nothing}"), 2);
			page.addItem(new CItem(pane, "{nothing}"), 3);
			page.addItem(new CItem(pane, "{nothing}"), 4);
			page.addItem(new CItem(pane, "{nothing}"), 5);
			page.addItem(new CItem(pane, "{nothing}"), 6);
			page.addItem(new CItem(pane, "{nothing}"), 7);
			page.addItem(new CItem(pane, "{nothing}"), 8);
			page.addItem(new CItem(pane, "{nothing}"), 9);
			
			page.addItem(sanctioncit, 10);
			page.addItem(invoit, 11);
			page.addItem(freeit, 12);
			page.addItem(tpi, 13);
			page.addItem(tp2i, 14);
			page.addItem(hisi, 15);
			page.addItem(clearmin, 16);

			page.addItem(new CItem(pane, "{nothing}"), 18);
			page.addItem(new CItem(pane, "{nothing}"), 17);
			
			cinv.addPage(page);
			cinv.display(player);
			inventorymanager.registerInventory(player.getUniqueId(), cinv);
		} else if(inv.equals("sanction")) {
			CInventory cinv = new CInventory();
			Page page = new Page(2, "Sanction " + otherinfos, 1, true);
	 		
			
			
			ItemStack sanction = new ItemStack(Material.STICK, 1);
			ItemMeta sanctionm = sanction.getItemMeta();
			sanctionm.setDisplayName(ChatColor.GREEN + "Tempban 1day Cheat");
			ArrayList<String> commands = new ArrayList<String>();commands.add("{leave}");commands.add("tempban " + otherinfos + " 1d Cheat ");
			sanction.setItemMeta(sanctionm);
			CItem sanctioncit = new CItem(sanction, commands);
			page.addItem(sanctioncit, 0);
			
			ItemStack invo = new ItemStack(Material.STICK, 1);
			invo.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
			ItemMeta invm = invo.getItemMeta();
			invm.setDisplayName(ChatColor.GREEN + "Tempban 3days Cheat");
			invo.setItemMeta(invm);
			ArrayList<String> commandse = new ArrayList<String>();commandse.add("{leave}");commandse.add("tempban " + otherinfos + " 3d Cheat ");
			CItem invoit = new CItem(invo, commandse);
			page.addItem(invoit, 9);
			
			
			ItemStack sanction1 = new ItemStack(Material.ANVIL, 1);
			ItemMeta sanctionm1 = sanction1.getItemMeta();
			sanctionm1.setDisplayName(ChatColor.GREEN + "Tempban 1day Glitch/Usebug");
			ArrayList<String> commands1 = new ArrayList<String>();commands1.add("{leave}");commands1.add("tempban " + otherinfos + " 1d Glitch/Usebug ");
			sanction1.setItemMeta(sanctionm1);
			CItem sanctioncit1 = new CItem(sanction1, commands1);
			page.addItem(sanctioncit1, 1);
			
			ItemStack invo1 = new ItemStack(Material.ANVIL, 1);
			invo1.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
			ItemMeta invm1 = invo1.getItemMeta();
			invm1.setDisplayName(ChatColor.GREEN + "Tempban 3days Glitch/Usebug");
			invo1.setItemMeta(invm1);
			ArrayList<String> commandse1 = new ArrayList<String>();commandse1.add("{leave}");commandse1.add("tempban " + otherinfos + " 3d Glitch/Usebug ");
			CItem invoit1 = new CItem(invo1, commandse1);
			page.addItem(invoit1, 10);
			
			
			
			ItemStack frr = new ItemStack(Material.BLAZE_ROD, 1);
			frr.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
			ItemMeta frrrr = frr.getItemMeta();
			frrrr.setDisplayName(ChatColor.GREEN + "Tempban 1day Bad behavior");
			ArrayList<String> commandsfr = new ArrayList<String>();commandsfr.add("{leave}");commandsfr.add("tempban " + otherinfos + " 1d Bad behavior ");
			frr.setItemMeta(frrrr);
			CItem frrrrr = new CItem(frr, commands1);
			page.addItem(frrrrr, 11);
			
			ItemStack fr = new ItemStack(Material.BLAZE_ROD, 1);
			ItemMeta frm = fr.getItemMeta();
			frm.setDisplayName(ChatColor.GREEN + "Tempban 3hours Bad behavior");
			fr.setItemMeta(frm);
			ArrayList<String> commande = new ArrayList<String>();commande.add("{leave}");commande.add("tempban " + otherinfos + " 3h Bad behavior ");
			CItem freeit = new CItem(fr, commande);
			page.addItem(freeit, 2);
			
			
			
			ItemStack frr1 = new ItemStack(Material.APPLE, 1);
			frr1.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
			ItemMeta frrrr1 = frr1.getItemMeta();
			frrrr1.setDisplayName(ChatColor.GREEN + "Mute 3hours Bad words");
			ArrayList<String> commandsfr1 = new ArrayList<String>();commandsfr1.add("{leave}");commandsfr1.add("mute " + otherinfos + " 1d Bad behavior ");
			frr1.setItemMeta(frrrr1);
			CItem frrrrr1 = new CItem(frr1, commands1);
			page.addItem(frrrrr1, 12);
			
			ItemStack fr1 = new ItemStack(Material.APPLE, 1);
			ItemMeta frm1 = fr1.getItemMeta();
			frm1.setDisplayName(ChatColor.GREEN + "Mute 1hour Bad words");
			fr1.setItemMeta(frm1);
			ArrayList<String> commande1 = new ArrayList<String>();commande1.add("{leave}");commande1.add("mute " + otherinfos + " 3h Bad behavior ");
			CItem freeit1 = new CItem(fr1, commande1);
			page.addItem(freeit1, 3);
			
			
			ItemStack frr11 = new ItemStack(Material.PAPER, 1);
			frr11.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
			ItemMeta frrrr11 = frr11.getItemMeta();
			frrrr11.setDisplayName(ChatColor.GREEN + "Spam mute 3hours");
			ArrayList<String> commandsfr11 = new ArrayList<String>();commandsfr11.add("{leave}");commandsfr11.add("mute " + otherinfos + " 3h Spam ");
			frr11.setItemMeta(frrrr11);
			CItem frrrrr11 = new CItem(frr11, commands1);
			page.addItem(frrrrr11, 13);
			
			ItemStack fr11 = new ItemStack(Material.PAPER, 1);
			ItemMeta frm11 = fr11.getItemMeta();
			frm11.setDisplayName(ChatColor.GREEN + "Spam mute 1hour");
			fr11.setItemMeta(frm11);
			ArrayList<String> commande11 = new ArrayList<String>();commande11.add("{leave}");commande11.add("mute " + otherinfos + " 1h Spam ");
			CItem freeit11 = new CItem(fr11, commande11);
			page.addItem(freeit11, 4);
			
			
			
			ItemStack tp2 = new ItemStack(Material.ENDER_PEARL, 1);
			ItemMeta tp2m = tp2.getItemMeta();
			tp2m.setDisplayName(ChatColor.GREEN + "Threats 1h mute");
			tp2.setItemMeta(tp2m);
			ArrayList<String> commandes = new ArrayList<String>();commandes.add("{leave}");commandes.add("mute " + otherinfos + " 1h Spam ");
			CItem tp2i = new CItem(tp2, commandes);
			page.addItem(tp2i, 5);
			
			ItemStack his = new ItemStack(Material.ENDER_PEARL, 1);
			his.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
			ItemMeta hism = his.getItemMeta();
			hism.setDisplayName(ChatColor.GREEN + "Threats 3h mute");
			ArrayList<String> commandi = new ArrayList<String>();commandi.add("{leave}");commandi.add("mute " + otherinfos + " 3h Spam ");
			his.setItemMeta(hism);
			CItem hisi = new CItem(his, commandi);
			page.addItem(hisi, 14);
			
			
			ItemStack tp21 = new ItemStack(Material.CRAFTING_TABLE, 1);
			tp21.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
			ItemMeta tp2m1 = tp21.getItemMeta();
			tp2m1.setDisplayName(ChatColor.GREEN + "Vulgar construction 1d ban");
			tp21.setItemMeta(tp2m1);
			ArrayList<String> commandes1 = new ArrayList<String>();commandes1.add("{leave}");commandes1.add("mute " + otherinfos + " 1d Vulgar construction ");
			CItem tp2i1 = new CItem(tp21, commandes1);
			page.addItem(tp2i1, 15);
			
			ItemStack his1 = new ItemStack(Material.CRAFTING_TABLE, 1);
			ItemMeta hism1 = his1.getItemMeta();
			hism1.setDisplayName(ChatColor.GREEN + "Vulgar construction 3h ban");
			ArrayList<String> commandi1 = new ArrayList<String>();commandi1.add("{leave}");commandi1.add("mute " + otherinfos + " 3h Vulgar construction ");
			his1.setItemMeta(hism1);
			CItem hisi1 = new CItem(his1, commandi1);
			page.addItem(hisi1, 6);
			
			
			
			ItemStack tp211 = new ItemStack(Material.FURNACE, 1);
			tp211.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
			ItemMeta tp2m11 = tp211.getItemMeta();
			tp2m11.setDisplayName(ChatColor.GREEN + "Other 3h ban");
			tp211.setItemMeta(tp2m11);
			ArrayList<String> commandes11 = new ArrayList<String>();commandes11.add("{leave}");commandes11.add("mute " + otherinfos + " 3h");
			CItem tp2i11 = new CItem(tp211, commandes11);
			page.addItem(tp2i11, 16);
			
			ItemStack his11 = new ItemStack(Material.FURNACE, 1);
			ItemMeta hism11 = his11.getItemMeta();
			hism11.setDisplayName(ChatColor.GREEN + "Other 1h ban");
			ArrayList<String> commandi11 = new ArrayList<String>();commandi11.add("{leave}");commandi11.add("mute " + otherinfos + " 1h");
			his11.setItemMeta(hism11);
			CItem hisi11 = new CItem(his11, commandi11);
			page.addItem(hisi11, 7);
			
			
			ItemStack tp2111 = new ItemStack(Material.FURNACE, 1);
			tp2111.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
			ItemMeta tp2m111 = tp2111.getItemMeta();
			tp2m111.setDisplayName(ChatColor.GREEN + "Too much punishments 1d ban");
			tp2111.setItemMeta(tp2m111);
			ArrayList<String> commandes111 = new ArrayList<String>();commandes111.add("{leave}");commandes111.add("mute " + otherinfos + " 1d Too much punishments");
			CItem tp2i111 = new CItem(tp2111, commandes111);
			page.addItem(tp2i111, 17);
			
			ItemStack his111 = new ItemStack(Material.FURNACE, 1);
			ItemMeta hism111 = his111.getItemMeta();
			hism111.setDisplayName(ChatColor.GREEN + "Too much punishments 3h ban");
			ArrayList<String> commandi111 = new ArrayList<String>();commandi111.add("{leave}");commandi111.add("mute " + otherinfos + " 1d Too much punishments");
			his111.setItemMeta(hism111);
			CItem hisi111 = new CItem(his111, commandi111);
			page.addItem(hisi111, 8);
			
			
			
			ItemStack pane = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1);
			ItemMeta panem = pane.getItemMeta();
			panem.setDisplayName(" ");
			pane.setItemMeta(panem);
			
			cinv.addPage(page);
			cinv.display(player);
			inventorymanager.registerInventory(player.getUniqueId(), cinv);
		}
	}
	
	
    
}

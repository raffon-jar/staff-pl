package be.raffon.staffpl;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.MalformedJsonException;
import com.mysql.cj.protocol.Resultset;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
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
import org.bukkit.plugin.java.JavaPlugin;

import be.raffon.staffpl.inventories.CInventory;
import be.raffon.staffpl.inventories.CItem;
import be.raffon.staffpl.inventories.InventoryManager;
import be.raffon.staffpl.inventories.Page;
import org.json.JSONArray;
import org.json.JSONObject;

public class staffpl extends JavaPlugin implements Listener {
	
	public String text = ChatColor.WHITE + "[" + ChatColor.RED + "STAFF" + ChatColor.WHITE + "] ";
	
	static File js = null;
	
	private String inspectedplayer;
	
    String host, database, username, password;
    Integer port;
    //static Connection connection;
	SQLManager sqlmanager;
    public InventoryManager inventorymanager;
    HashMap<UUID, Boolean> freezes;


	@Override
	public void onEnable() {
		/*File dir = this.getDataFolder(); // Get the parent directory
		dir.mkdirs();
		js = new File(this.getDataFolder() + "//" + "config.JSON");
		if(!js.exists()) {
			new File(this.getDataFolder() + "//").mkdirs();
			
			JSONObject o = setJSON();
			try {
				FileWriter file = new FileWriter(this.getDataFolder() + "//" + "config.JSON");
				file.write(o.toJSONString());
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		js = new File(this.getDataFolder(), "//" + "config.JSON");*/
		
        host = "localhost";
        port = 3306;
        database = "sf2021";
        username = "sf2021";
        password = "Lq%n9aajZS7CtU"; 
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
		getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	}
	
    /*public void sendPlayerToServer(Player player, String server) {
        try {
          ByteArrayOutputStream b = new ByteArrayOutputStream();
          DataOutputStream out = new DataOutputStream(b);
          out.writeUTF("Connect");
          out.writeUTF(server);
          player.sendPluginMessage(this, "BungeeCord", b.toByteArray());
          b.close();
          out.close();
        }
        catch (Exception e) {
          player.sendMessage(ChatColor.RED+"Error when trying to connect to "+server);
        }
      }*/
	
    /*public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://"
                + this.host + ";" + this.port + "/" + this.database,
                this.username, this.password);
    }
    
    public ResultSet executesql(String sql) throws SQLException, ClassNotFoundException {
		return connection.createStatement().executeQuery(sql);
    }*/
    
    public String getActualServer() {
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
	
	public ArrayList<Item> getItems() {
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
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent evt) {
		//evt.getPlayer().sendMessage(text+"Hi this plugin was created by "+ChatColor.BLUE+"Rafael Silva Mendes "+ChatColor.WHITE+"go take a look here "+ChatColor.BLUE+"https://raffon.be");
		SQLManager.getInstance().query(" SELECT * FROM staff_tempban\r\n" +
				" WHERE username = '"+ evt.getPlayer().getUniqueId() + "';", rs -> {
			Boolean found = false;
			try {
				while(rs.next()) {
					found = true;
					String name = rs.getString("username");
					String reason = rs.getString("reason");
					String active = ChatColor.GREEN + "Yes";
					Long start = rs.getTimestamp("start").getTime();
					Integer ms = rs.getInt("time");
					Long diff = System.currentTimeMillis()-start;
					if(ms - diff > 0) {
						int m = (int) (ms-diff);

						int days   = (int) m / (24*60 *60* 1000) % 60;
						int hours   = (int) m/(60* 60  * 1000) % 60 ;
						int minutes   = (int) m/(60 *1000) % 60;
						int seconds   = (int) m/1000 % 60;
						evt.getPlayer().kickPlayer("You are banned on this server for " +  ChatColor.RED + reason + ChatColor.WHITE +" \n You will be unbanned in " + ChatColor.RED + days + ChatColor.WHITE +" days " + ChatColor.RED + hours + ChatColor.WHITE + " hours "+ ChatColor.RED + minutes + ChatColor.WHITE + " minutes \n \n If you want more informations you can join" + ChatColor.BLUE + " \n https://discord.gg/mvq3hJ9gV3");
					} else {
						evt.getPlayer().sendMessage(text + "Please be carreful to respect the rules next time :)");
						try {
							SQLManager.getInstance().update("DELETE FROM staff_tempban WHERE id= " + rs.getInt("id") +";");
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		
		
		/*if(isFreezed(evt.getPlayer())) {
			try {
				ResultSet result = executesql("SELECT * FROM staff_freeze WHERE username = '" + evt.getPlayer().getUniqueId() + "';");
				if(result.next()) {
					System.out.println("found");
					String server = result.getString("world").substring(0, result.getString("world").indexOf("-"));
					String world_str = result.getString("world").substring(result.getString("world").indexOf("-")+1);
					
					if(server != getActualServer()) {
						System.out.println("not seem serv");
						sendPlayerToServer(evt.getPlayer(), server);
						return;
					}
					World world = Bukkit.getServer().getWorld(world_str);
					Integer x = result.getInt("x");
					Integer y = result.getInt("y");
					Integer z = result.getInt("z");
					
					if(world == null) {
						freeze(evt.getPlayer(), false);
						return;
					}
					Location loc = new Location(world, x, y, z);
					if(loc == null) {
						freeze(evt.getPlayer(), false);
						return;
					}
					
					evt.getPlayer().teleport(loc);
					
				}
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		
		
		// Change world in staff.
		Player pl = evt.getPlayer();
		
		
		/* "CREATE TABLE IF NOT EXISTS staff_backups (\n"
                   	+ "	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,\n"
                    + "	backup TEXT,\n"
                    + "	world VARCHAR(100), \n"
                    + "	username VARCHAR(100) \n"
                    + ");";*/
		if(!pl.hasPermission("staff.staff")) return;

		SQLManager.getInstance().query(" SELECT * FROM staff_backups\r\n" +
				" WHERE username = '"+ evt.getPlayer().getUniqueId() + "';", rs -> {
			try {
				while (rs.next() && !getStaff(pl, "*")) {
					String world = rs.getString("world");
					String backup = rs.getString("backup");
					Integer id = rs.getInt("id");

					if (world.endsWith(pl.getLocation().getWorld().getName()) && world.startsWith(getActualServer())) {
						SQLManager.getInstance().update("DELETE FROM staff_backups WHERE id= " + id + ";");
						recover(pl, createJSONObject(backup));
					}
				}
				SQLManager.getInstance().query(" SELECT * FROM staff_players\r\n" +
						" WHERE username = '" + evt.getPlayer().getUniqueId() + "';", rs2 -> {
					try {
						while (rs2.next()) {
							String world = rs2.getString("world");
							String backup = rs2.getString("backup");
							Boolean staff = rs2.getBoolean("staff");

							String server = world.substring(0, world.indexOf("-"));
							String world_str = world.substring(world.indexOf("-") + 1);

							if ((server.equals(getActualServer())) && staff) {
								SQLManager.getInstance().update(" UPDATE staff_players \r\n" +
										" SET world = '" + getActualServer() + "-" + pl.getWorld().getName() + "'\r\n" +
										" WHERE username = '" + pl.getUniqueId() + "'; ");
								return;
							}
							if (!world.equals(getActualServer() + "-" +pl.getLocation().getWorld().getName()) && staff) {
								SQLManager.getInstance().query(" SELECT * FROM staff_backups\r\n" +
										" WHERE username = '" + evt.getPlayer().getUniqueId() + "' AND world='" + getActualServer() + "-" + pl.getLocation().getWorld().getName() + "';", rs3 -> {
									try {

										if (!rs3.next()) {
											SQLManager.getInstance().update(" INSERT INTO staff_backups (username, world, backup) \r\n" +
													"VALUES ('" + evt.getPlayer().getUniqueId() + "', '" + world + "', '" + backup + "');");
											setstaffmode(pl, true, 1, backupeverything(pl));

											pl.chat("/vanish");
										} else {
											Integer id = rs3.getInt("id");
											String back = rs3.getString("backup");
											SQLManager.getInstance().update("DELETE FROM staff_backups WHERE id= " + id + ";");

											SQLManager.getInstance().update(" INSERT INTO staff_backups (username, world, backup) \r\n" +
													"VALUES ('" + evt.getPlayer().getUniqueId() + "', '" + world + "', '" + backup + "');");
											setstaffmode(pl, true, 1, createJSONObject(back));

											pl.chat("/vanish");

										}
									} catch (SQLException e) {
										// TODO Auto-generated catch block
										//e.printStackTrace();
										e.printStackTrace();
									}
								});

							}
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						e.printStackTrace();
					};

				});
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			e.printStackTrace();
		};

		});
			


		
	}
	
	/*@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent evt){
		Player pl = evt.getPlayer();
		if(getStaff(pl, evt.getFrom().toString())) {
			pl.teleport(pl.getLocation());
			pl.sendMessage(text + "You can't change world if you are in staff mode.");
		}
		if(isFreezed(evt.getPlayer())) {
			pl.teleport(pl.getLocation());
			pl.sendMessage(text + "You can't change world if you are freezed.");
		}
	}*/
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void PlayerPickupItemEvent(PlayerPickupItemEvent evt) {
		if(getStaff(evt.getPlayer())) {
			evt.setCancelled(true);
		}
	}
	

	@EventHandler
	public void onChat(AsyncPlayerChatEvent evt) {
		/*JSONObject json = setupJson();  
		JSONArray mutes = (JSONArray) json.get("tempmute");
		for(int k=0; k<mutes.size(); k++) {
			JSONObject mute = (JSONObject) mutes.get(k);
			String name = (String) mute.get("name");
			Integer ms = Integer.parseInt((String) mute.get("time"));
			Long start = Long.parseLong((String) mute.get("start"));
			String reason = (String) mute.get("reason");
			if(name.equals(evt.getPlayer().getDisplayName())) {
				Long diff = System.currentTimeMillis()-start;
				if(ms - diff > 0) {
					int seconds = (int) (ms-diff) / 1000;
					int minutes = (int) seconds / 60;
					int hours   = (int) minutes / 60;
					int days   = (int) hours / 24;
					evt.setCancelled(true);
					evt.getPlayer().sendMessage(text + "You are muted on this server for " + reason + "\n"+ text + "You will be unmuted in " + ChatColor.RED + days + ChatColor.WHITE +" days " + ChatColor.RED + hours + ChatColor.WHITE + " hours "+ ChatColor.RED + minutes + ChatColor.WHITE + " minutes");
				} else {
					evt.getPlayer().sendMessage(text + "Please be carreful to respect the rules next time :)");
					mutes.remove(k);
				}
			}
		}
		try {
			FileWriter file = new FileWriter(js);
			file.write(json.toJSONString());
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/

		SQLManager.getInstance().query(" SELECT * FROM staff_tempmute\r\n" +
				" WHERE username = '"+ evt.getPlayer().getUniqueId() + "';", rs -> {
			try {
					Boolean found = false;
					while(rs.next()) {
						found = true;
						String name = rs.getString("username");
						String reason = rs.getString("reason");
						String active = ChatColor.GREEN + "Yes";
						Long start = rs.getTimestamp("start").getTime();
						Integer ms = rs.getInt("time");
						Long diff = System.currentTimeMillis()-start;
						if(ms - diff > 0) {
							int seconds = (int) (ms-diff) / 1000;
							int minutes = (int) seconds / 60;
							int hours   = (int) minutes / 60;
							int days   = (int) hours / 24;
							evt.setCancelled(true);
							evt.getPlayer().sendMessage("You are muted on this server for " + reason + " \n You will be unmuted in " + ChatColor.RED + days + ChatColor.WHITE +" days " + ChatColor.RED + hours + ChatColor.WHITE + " hours "+ ChatColor.RED + minutes + ChatColor.WHITE + " minutes" );
						} else {
							evt.getPlayer().sendMessage(text + "Please be carreful to respect the rules next time :)");
							try {
								SQLManager.getInstance().update("DELETE FROM staff_tempmute WHERE id= " + rs.getInt("id") +";");
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					if(!found) {
						return;
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

		});
		

	}
	
	/*@SuppressWarnings("unchecked")
	public JSONObject setJSON() {
		JSONObject obj = new JSONObject();
		obj.put("staff", new JSONArray());
		obj.put("backup", new JSONArray());
		obj.put("logs", new JSONArray());
		obj.put("vanish", new JSONArray());
		obj.put("fly", new JSONArray());
		obj.put("freeze", new JSONArray());
		obj.put("tempban", new JSONArray());
		obj.put("tempmute", new JSONArray());
		obj.put("reports", new JSONArray());
		return obj;
		
	}
	
	public static JSONObject setupJson() {
		try {
			JSONParser jsonparser = new JSONParser();
			Object parsed = null;
			parsed = jsonparser.parse(new FileReader(js.getPath()));
			JSONObject jsonObject = (JSONObject) parsed;
			return jsonObject;
		}
		catch (IOException | ParseException e) {
			throw new RuntimeException("Exception problem ! Please contact the developper !", e);
		}	
		
		
	}*/
	
	
	@SuppressWarnings({ "deprecation", "unchecked", "unused" })
	@EventHandler
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		//JSONObject json = setupJson();
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console can not use this plugin!");
            return true;
        }
      
		Player player = (Player) sender;
		/*if (alias.equalsIgnoreCase("fly")) {
			if(player.hasPermission("staff.fly")){
	            if(!getAction(player.getDisplayName(), "fly")){
	                player.setAllowFlight(true);
	                player.sendMessage(text + ChatColor.GRAY + "Fly Enabled!");
	                setAction(player.getDisplayName(), "fly", true);
	            } else  {
		            player.setAllowFlight(false);
		            player.sendMessage(text + ChatColor.GRAY + "Fly Disabled!");
		            setAction(player.getDisplayName(), "fly", false);
	            }
			} else {
				player.sendMessage(text+ChatColor.RED+"You don't have the permission to execute this command");
			}
		} else if(alias.equalsIgnoreCase("vanish")) {
			if(player.hasPermission("staff.vanish")){
	            if(!getAction(player.getDisplayName(), "vanish")){
	                for (Player p1 : getServer().getOnlinePlayers()) {
	                	p1.hidePlayer(player);
	                }
	                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 255));
	                player.sendMessage(text + ChatColor.GRAY + "Vanish Enabled!");
	                setAction(player.getDisplayName(), "vanish", true);
	            } else  {
	                for (Player p1 : getServer().getOnlinePlayers()) {
	                	p1.showPlayer(player);
	                }
	                player.removePotionEffect(PotionEffectType.INVISIBILITY);
		            player.setAllowFlight(false);
		            player.sendMessage(text + ChatColor.GRAY + "Vanish Disabled!");
		            setAction(player.getDisplayName(), "vanish", false);
	            }
			} else {
				player.sendMessage(text+ChatColor.RED+"You don't have the permission to execute this command");
			}
		} else*/ if(alias.equalsIgnoreCase("inspect")) {
			if(player.hasPermission("staff.inspect")){
				if(args.length >= 1) {
					OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
					if(p == null) {
						player.sendMessage(text+ChatColor.RED+"Player not found");
						return true;
					}
					openInv("inspect", player, p.getName());
				} else {
					player.sendMessage(text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/inspect <player>");
				}
			} else {
				player.sendMessage(text+ChatColor.RED+"You don't have the permission to execute this command");
			}
		} else if(alias.equalsIgnoreCase("staff")) {
			if(player.hasPermission("staff.staff")){
				if(!getStaff(player)){
					//setAction(player.getDisplayName(), "staff", true);
					setstaffmode(player,true,1,backupeverything(player));
					player.sendMessage(text + ChatColor.GRAY + "Staff mode Enabled!");
				} else {
					//setAction(player.getDisplayName(), "staff", false);
					setstaffmode(player,false,1,null);
					recover(player, null);
					player.sendMessage(text + ChatColor.GRAY + "Staff mode Disabled!");
				}
				
				
			}else {
				player.sendMessage(text+ChatColor.RED+"You don't have the permission to execute this command");
			}
		} else if(alias.equalsIgnoreCase("freeze")) {
			if(player.hasPermission("staff.freeze")){
				if(args.length >= 1) {
					Player p = Bukkit.getPlayer(args[0]);
					if(p == null) {
						player.sendMessage(text+ChatColor.RED+"Player not found");
						return true;
					}
					if(!isFreezed(p)) {
						freeze(p, true);
						player.sendMessage(text+ChatColor.BLUE + p.getDisplayName() + ChatColor.WHITE +" was successfuly freezed");
					} else {
						freeze(p, false);
						player.sendMessage(text+ChatColor.BLUE + p.getDisplayName() + ChatColor.WHITE +" was successfuly unfreezed");
					}
				} else {
					player.sendMessage(text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/freeze <player>");
				}
			} else {
				player.sendMessage(text+ChatColor.RED+"You don't have the permission to execute this command");
			}
		} else if(alias.equalsIgnoreCase("tempban")) {
			if(player.hasPermission("staff.tempban")){
				if(args.length >= 2) {
					OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
					if(p == null) {
						player.sendMessage(text+ChatColor.RED+"Player not found");
						return true;
					}
					SQLManager.getInstance().update(" INSERT INTO staff_logs (type, reason, username, time, date, moderator) \r\n" +
							" VALUES ('tempban', '" + getfuther(2,args) + "', '"+ p.getUniqueId() +"', '"+ args[1] +"', '"+ new SimpleDateFormat("dd-MM-yyyy").format(new Date()) +"', '"+player.getUniqueId() + "')");
					tempsanction(p.getUniqueId(), "ban", args[1], getfuther(2, args));
					player.sendMessage(text+"Successfully banned " + ChatColor.RED + args[0] + ChatColor.WHITE + " for " + ChatColor.RED + getfuther(2, args));
				}
				else {
					player.sendMessage(text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/tempban <player> <time> [reason]");
				}
			}
		} else if(alias.equalsIgnoreCase("mute")) {
			if(player.hasPermission("staff.mute")){
				if(args.length >= 2) {
					OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
					if(p == null) {
						player.sendMessage(text+ChatColor.RED+"Player not found");
						return true;
					}
					String query = " INSERT INTO staff_logs (type, reason, username, time, date, moderator) VALUES ('mute', '" + getfuther(2,args) + "', '"+ p.getUniqueId() +"', '"+ args[1] +"', '"+ new SimpleDateFormat("dd-MM-yyyy").format(new Date()) +"', '"+player.getUniqueId() + "')";
					SQLManager.getInstance().update(query);
					tempsanction(p.getUniqueId(), "mute", args[1], getfuther(2, args));
					player.sendMessage(text+"Successfully muted " + ChatColor.RED + args[0] + ChatColor.WHITE + " for " + ChatColor.RED + getfuther(2, args));
				}
				else {
					player.sendMessage(text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/mute <player> <time> [reason]");
				}
			}
		} else if(command.getName().equalsIgnoreCase("kick")) {
			if(args.length >= 1) {
				Player p = Bukkit.getPlayer(args[0]);
				if(p == null) {
					player.sendMessage(text+ChatColor.RED+"Player not found");
					return true;
				}
				String reason = "";
				if(args.length == 1) {
					reason = "kicked by a moderator";
				} else {
					reason = getfuther(1, args);
				}
				p.kickPlayer(reason);
				player.sendMessage(text+"Successfully kicked " + ChatColor.RED + args[0] + ChatColor.WHITE + " for " + ChatColor.RED + getfuther(1, args));
				SQLManager.getInstance().update(" INSERT INTO staff_logs (type, reason, username, date, moderator) \r\n" +
						" VALUES ('kick', '" + getfuther(2,args) + "', '"+ p.getUniqueId() +"', '"+ new SimpleDateFormat("dd-MM-yyyy").format(new Date()) +"', '"+player.getUniqueId() + "')");
			} else {
				player.sendMessage(text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/kick <player> [reason]");
			}
		}else if(command.getName().equalsIgnoreCase("ban")) {
			if(args.length >= 1) {
				OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
				if(p == null) {
					player.sendMessage(text+ChatColor.RED+"Player not found");
					return true;
				}
				String reason = "";
				if(args.length == 1) {
					reason = "banned by a moderator";
				} else {
					reason = getfuther(1, args);
				}
				BanList bannedPlayers = Bukkit.getServer().getBanList(BanList.Type.NAME);
				bannedPlayers.addBan(args[0], reason, null, null);
				if(p.isOnline()) {
					((Player)p).kickPlayer("You are banned on this server.");
				}
				player.sendMessage(text+"Successfully banned " + ChatColor.RED + args[0] + ChatColor.WHITE + " for " + ChatColor.RED + getfuther(1, args));
				SQLManager.getInstance().update(" INSERT INTO staff_logs (type, reason, username, date, moderator) \r\n" +
						" VALUES ('ban', '" + getfuther(2,args) + "', '"+ p.getUniqueId() +"', '"+ new SimpleDateFormat("dd-MM-yyyy").format(new Date()) +"', '"+player.getUniqueId() + "')");
			} else {
				player.sendMessage(text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/ban <player> [reason]");
			}
		} else if(command.getName().equalsIgnoreCase("modlogs")) {
			if(args.length >= 1) {
					OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
					
					if(p == null) {
						player.sendMessage(text+ChatColor.RED+"Player not found");
						return true;
					}
					SQLManager.getInstance().query(" SELECT * FROM staff_logs\r\n" +
							" WHERE username = '"+ p.getUniqueId() + "';", rs -> {
						try {
							Boolean found = false;
							while(rs.next()) {
								found = true;
								String name = rs.getString("username");
								String type = rs.getString("type");
								String reason = rs.getString("reason");
								String date = rs.getString("date");
								String active = ChatColor.GREEN + "Yes";
								Long start = rs.getTimestamp("start").getTime();
								OfflinePlayer moderator = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("moderator")));
								if(type.equals("kick")) {
									active = ChatColor.RED + "No";
								}


								if(type.equals("tempban") || type.equals("mute")) {
									String time = rs.getString("time");
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

									}

									int seconds = (int) ms / 1000;
									int minutes = (int) seconds / 60;
									int hours   = (int) minutes / 60;
									int days   = (int) hours / 24;
									if(ms - (System.currentTimeMillis() - start) < 0) {
										active = ChatColor.RED + "No";
									}

									player.sendMessage(text + "-----------------------------------\n" + text + ChatColor.BLUE + "Active: " + active + "\n"+ text + ChatColor.BLUE + "Type: " + ChatColor.WHITE + type + "\n"+text+ ChatColor.BLUE + "Reason: "+ ChatColor.WHITE + reason+"\n"+text+ ChatColor.BLUE + "Date: "+ ChatColor.WHITE + date+"\n"+text+ ChatColor.BLUE + "Duration: " + ChatColor.RED + days + ChatColor.WHITE + " days " + ChatColor.RED + hours + ChatColor.WHITE + " hours " + ChatColor.RED + minutes + ChatColor.WHITE + " minutes " + "\n" +text+ ChatColor.BLUE + "Moderator: "+ ChatColor.WHITE + moderator.getName() +"\n" + text + "-----------------------------------" );
								} else {
									player.sendMessage(text + "-----------------------------------\n" + text + ChatColor.BLUE + "Active: " + active + "\n"+ text + ChatColor.BLUE + "Type: " + ChatColor.WHITE + type + "\n"+text+ ChatColor.BLUE + "Reason: "+ ChatColor.WHITE + reason+"\n"+text+ ChatColor.BLUE + "Date: "+ ChatColor.WHITE + date +"\n"+text+ ChatColor.BLUE + "Moderator: "+ ChatColor.WHITE + moderator.getName() + "\n" + text + "-----------------------------------" );
								}


							}
							if(!found) {
								player.sendMessage(text + "-----------------------------------\n" + text + "No history found for this player"+ "\n" + text + "-----------------------------------" );

							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});



			} else {
				player.sendMessage(text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/modlogs <player>");
			}
		}else if(command.getName().equalsIgnoreCase("report")) {
			if(args.length >= 2) {
				Player p = Bukkit.getPlayer(args[0]);
				if(p == null) {
					player.sendMessage(text+ChatColor.RED+"Player not found");
					return true;
				}
				player.sendMessage(text+"Successfully reported " + ChatColor.RED + args[0] + ChatColor.WHITE + " for " + ChatColor.RED + getfuther(1, args));
				/* sql = "CREATE TABLE IF NOT EXISTS staff_reports (\n"
                   	+ "	date VARCHAR(255),\n"
                    + "	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,\n"
                    + "	reason TEXT, \n"
                    + "	reporter VARCHAR(255), \n"
                    + "	player VARCHAR(255), \n"
                    + "	status INTEGER, \n"
                    + ");";*/
					SQLManager.getInstance().update(" INSERT INTO staff_reports (reporter, reason, player, date, status) \r\n" +
							" VALUES ('"+ player.getUniqueId() + "', '" + getfuther(1,args) + "', '"+ p.getUniqueId() +"', '"+ new SimpleDateFormat("dd-MM-yyyy").format(new Date()) +"', 0)");
			} else {
				player.sendMessage(text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/report <player> <reason>");
			}
		}else if(command.getName().equalsIgnoreCase("reports")) {
			if(args.length >= 1) {
				OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
				if(p == null) {
					player.sendMessage(text+ChatColor.RED+"Player not found");
					return true;
				}
				openInv("reports", player, args[0]);
			} else {
				openInv("reports", player, null);
			}
		}else if(command.getName().equalsIgnoreCase("sanction") || command.getName().equalsIgnoreCase("ss")) {
			if(args.length >= 1) {
				OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
				if(p == null) {
					player.sendMessage(text+ChatColor.RED+"Player not found");
					return true;
				}
				openInv("sanction", player, args[0]);
			} else {
				player.sendMessage(text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/sanction <player>");
			}
		} else if(command.getName().equalsIgnoreCase("randomtp")) {
    		ArrayList<Player> allPlayers = (ArrayList<Player>) new ArrayList<Player>();
    		for(Player players : Bukkit.getOnlinePlayers()) {
    			allPlayers.add(players);
    		}
    		int random = new Random().nextInt(allPlayers.size());
    		Player picked = allPlayers.get(random);
    		player.chat("/tp "+picked.getName());
		} else if(command.getName().equalsIgnoreCase("baltop")) {
			if(player.getWorld().getName().equals("survie")) {
				openInv("baltop", player, null);
			}
		} else if(command.getName().equalsIgnoreCase("reportcomplete")) {
			if(args.length < 1) {
				player.sendMessage(text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/reportcomplete <id>");
				return true;
			}
			SQLManager.getInstance().update("DELETE FROM staff_reports WHERE id= " + args[0] +";");
		} else if(command.getName().equalsIgnoreCase("unmute")) {
			if(args.length < 1) {
				player.sendMessage(text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/unmute <player>");
				return true;
			}
			OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
			if(p == null) {
				player.sendMessage(text+ChatColor.RED+"Player not found");
				return true;
			}

			SQLManager.getInstance().query( "SELECT * FROM staff_tempmute WHERE username='"+ p.getUniqueId() + "'", rs -> {
				try {
					if(rs.next()) {
						SQLManager.getInstance().update("DELETE FROM staff_tempmute WHERE username='" + p.getUniqueId() +"';");
						SQLManager.getInstance().update(" INSERT INTO staff_logs (type, reason, username, date, moderator) \r\n" +
								" VALUES ('unmute', '', '"+ p.getUniqueId() +"', '"+ new SimpleDateFormat("dd-MM-yyyy").format(new Date()) +"', '"+player.getUniqueId() + "')");
						player.sendMessage(text + "Successfuly unmuted " + p.getName());
					} else {
						player.sendMessage(text + "This user is not muted");

					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		} else if(command.getName().equalsIgnoreCase("unban")) {
			if(args.length < 1) {
				player.sendMessage(text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/unmute <player>");
				return true;
			}
			OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
			if(p == null) {
				player.sendMessage(text+ChatColor.RED+"Player not found");
				return true;
			}

			SQLManager.getInstance().query( "SELECT * FROM staff_tempmute WHERE username='"+ p.getUniqueId() + "'", rs -> {
				try {
					if(rs.next()) {
						SQLManager.getInstance().update("DELETE FROM staff_tempban WHERE username='" + p.getUniqueId() +"';");
						SQLManager.getInstance().update(" INSERT INTO staff_logs (type, reason, username, date, moderator) \r\n" +
								" VALUES ('unban', '', '"+ p.getUniqueId() +"', '"+ new SimpleDateFormat("dd-MM-yyyy").format(new Date()) +"', '"+player.getUniqueId() + "')");
						player.sendMessage(text + "Successfuly unbanned " + p.getName());
					} else {
						player.sendMessage(text + "This user is not banned.");

					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		}

		return true;
	}
	
	/*@EventHandler
	public void onBlockBreak(BlockBreakEvent event) throws SQLException{
		Block block = event.getBlock();
		if(!getStaff(event.getPlayer()) && event.getPlayer().getWorld().getName().equals("survie") && getActualServer().equals("survie") && event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
			
			if(block.getType() == Material.DIAMOND_ORE) {
				connection.createStatement().execute("INSERT INTO staff_baltop (username, diamonds) VALUES ('" + event.getPlayer().getUniqueId() + "', 1) ON DUPLICATE KEY UPDATE diamonds = diamonds + 1;");
			}
			if(block.getType() == Material.GOLD_ORE) {
				connection.createStatement().execute("INSERT INTO staff_baltop (username, golds) VALUES ('" + event.getPlayer().getUniqueId() + "', 1) ON DUPLICATE KEY UPDATE golds = golds + 1;");
			}
			if(block.getType() == Material.IRON_ORE) {
				connection.createStatement().execute("INSERT INTO staff_baltop (username, iron) VALUES ('" + event.getPlayer().getUniqueId() + "', 1) ON DUPLICATE KEY UPDATE iron = iron + 1;");
			}
			if(block.getType() == Material.COAL_ORE) {
				connection.createStatement().execute("INSERT INTO staff_baltop (username, coal) VALUES ('" + event.getPlayer().getUniqueId() + "', 1) ON DUPLICATE KEY UPDATE coal = coal + 1;");
			}
			if(block.getType() == Material.LAPIS_ORE) {
				connection.createStatement().execute("INSERT INTO staff_baltop (username, lapis) VALUES ('" + event.getPlayer().getUniqueId() + "', 1) ON DUPLICATE KEY UPDATE lapis = lapis + 1;");
			}
			if(block.getType() == Material.REDSTONE_ORE) {
				connection.createStatement().execute("INSERT INTO staff_baltop (username, redstone) VALUES ('" + event.getPlayer().getUniqueId() + "', 1) ON DUPLICATE KEY UPDATE redstone = redstone + 1;");
			}
			if(block.getType() == Material.ANCIENT_DEBRIS) {
				connection.createStatement().execute("INSERT INTO staff_baltop (username, netherite) VALUES ('" + event.getPlayer().getUniqueId() + "', 1) ON DUPLICATE KEY UPDATE netherite = netherite + 1;");
			}
			if(block.getType() == Material.STONE || block.getType() == Material.ANDESITE || block.getType() == Material.GRANITE|| block.getType() == Material.DIORITE || block.getType() == Material.GRAVEL) {
				connection.createStatement().execute("INSERT INTO staff_baltop (username, normal_blocks) VALUES ('" + event.getPlayer().getUniqueId() + "', 1) ON DUPLICATE KEY UPDATE normal_blocks = normal_blocks + 1;");
			}
			if(block.getType() == Material.BASALT || block.getType() == Material.NETHERRACK || block.getType() == Material.BLACKSTONE) {
				connection.createStatement().execute("INSERT INTO staff_baltop (username, normal_nether) VALUES ('" + event.getPlayer().getUniqueId() + "', 1) ON DUPLICATE KEY UPDATE normal_nether = normal_nether + 1;");
			}
		}
	}*/


	public Boolean getStaff(Player pl) {
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
	
	public Boolean isFreezed(Player pl) {
		if(freezes.get(pl.getUniqueId()) == null) return false;
		return true;
	}
	
	public Boolean getStaff(Player pl, String world_from) {
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
	
	public String getfuther(int value, String[] args) {
		StringBuilder message = new StringBuilder();
		for(int i = value; i < args.length; i++){
			message.append(" ").append(args[i]);
		}
		String str = message.toString().substring(1, message.toString().length());
		return str;
	}
	
	public void freeze(Player pl, Boolean freeze) {
		Location loc = pl.getLocation();
		/*SQLManager.getInstance().update("INSERT INTO staff_freeze (username, freeze, world, x, y, z)\r\n" +
				"VALUES ('"+ pl.getUniqueId() + "', "+ freeze + ", '" + getActualServer() +"-" +pl.getWorld().getName() + "', " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")\r\n" +
				"ON DUPLICATE KEY UPDATE freeze=" + freeze + ", x="+ loc.getBlockX() + ", y="+ loc.getBlockY() + ", z="+ loc.getBlockZ() + ", world='"+ getActualServer() +"-" +pl.getWorld().getName() + "'");*/

		if(freeze) {
			this.freezes.put(pl.getUniqueId(), true);
		} else {
			this.freezes.remove(pl.getUniqueId());
		}


		/*
		 *
		 *             sql = "CREATE TABLE IF NOT EXISTS staff_freeze (\n"
				+ "	username VARCHAR(255) PRIMARY KEY,\n"
				+ "	freeze BOOLEAN,\n"
				+ "	world 	VARCHAR(255),\n"
				+ "	x INTEGER, \n"
				+ "	y INTEGER, \n"
				+ "	z INTEGER \n"
				+ ");";*/
	}
	
	@SuppressWarnings({ "unchecked"})
	public void tempsanction(UUID player, String type, String time, String reason) {
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
        /*if(getAction(p.getDisplayName(), "staff")) {
        	if(p.getItemInHand().getType().equals(Material.ENDER_EYE) && p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Vanish")) {
        		e.setCancelled(true);
        		p.chat("/vanish");
        	} else if(p.getItemInHand().getType().equals(Material.FEATHER) && p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Fly")) {
        		e.setCancelled(true);
        		p.chat("/fly");
        	} else if(p.getItemInHand().getType().equals(Material.PAPER) && p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Reports")) {
        		e.setCancelled(true);
        		p.chat("/reports");
        	} else if(p.getItemInHand().getType().equals(Material.ENDER_PEARL) && p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Random TP")) {
        		ArrayList<Player> allPlayers = (ArrayList<Player>) new ArrayList<Player>();
        		for(Player players : Bukkit.getOnlinePlayers()) {
        			allPlayers.add(players);
        		}
        		int random = new Random().nextInt(allPlayers.size());
        		Player picked = allPlayers.get(random);
        		e.setCancelled(true);
        		p.chat("/tp "+picked.getDisplayName());
        	} else if(p.getItemInHand().getType().equals(Material.PAPER) && p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Reports")) {
        		e.setCancelled(true);
        		System.out.println(p.getOpenInventory().getType());
        		if(p.getOpenInventory().getType() == InventoryType.CRAFTING || p.getOpenInventory().getType() == InventoryType.CREATIVE)  {
        			p.chat("/reports");
        		}
        	}  else if(p.getItemInHand().getType().equals(Material.RABBIT_FOOT) && p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Speed")) {
        		e.setCancelled(true);
                float y = p.getLocation().getYaw();
                if( y < 0 ){y += 360;}
                y %= 360;
                int i = (int)((y+8) / 22.5);
                if(i > 14 || i<=2){p.setVelocity(new Vector(0,0,-1.5));}
                else if(i <=6 && i>2){p.setVelocity(new Vector(0,0,-1.5));}
                else if(i <=10 && i>6){p.setVelocity(new Vector(0,0,-1.5));}
                else if(i <=14 && i>10){}p.setVelocity(new Vector(0,0,1.5));
                double rotation = (p.getLocation().getYaw() - 90) % 360;
                if (rotation < 0) {
                    rotation += 360.0;
                }
                 if (0 <= rotation && rotation < 22.5) {
                	 p.setVelocity(new Vector(-1.5,0,0));//N
                } else if (22.5 <= rotation && rotation < 67.5) {
                	p.setVelocity(new Vector(-1.5,0,-1.5));//NE
                } else if (67.5 <= rotation && rotation < 112.5) {
                	p.setVelocity(new Vector(0,0,-1.5));//E
                } else if (112.5 <= rotation && rotation < 157.5) {
                	p.setVelocity(new Vector(1.5,0,-1.5));//SE
                } else if (157.5 <= rotation && rotation < 202.5) {
                	p.setVelocity(new Vector(1.5,0,0));//S
                } else if (202.5 <= rotation && rotation < 247.5) {
                	p.setVelocity(new Vector(1.5,0,1.5));//SW
                } else if (247.5 <= rotation && rotation < 292.5) {
                	p.setVelocity(new Vector(0,0,1.5));//W
                } else if (292.5 <= rotation && rotation < 337.5) {
                	p.setVelocity(new Vector(-1.5,0,1.5));//NW
                } else if (337.5 <= rotation && rotation < 360.0) {
                	p.setVelocity(new Vector(-1.5,0,0));//N
                }
        		
        	} else if(p.getItemInHand().getType().equals(Material.ARROW) && p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Page 2")) {
        		e.setCancelled(true);
        		setstaffmode(p,true,"2");
        	} else if(p.getItemInHand().getType().equals(Material.ARROW) && p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Page 1")) {
        		e.setCancelled(true);
        		setstaffmode(p,true,"1");
        	} else if(p.getItemInHand().getType().equals(Material.BARRIER) && p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Exit")) {
        		e.setCancelled(true);
        		p.chat("/staff");
        	}
        }*/
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
	
	@EventHandler
	@SuppressWarnings("deprecation")
	public void onClickOnEntity(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        /*System.out.println(e.getRightClicked());
        System.out.println(e.getRightClicked() instanceof Player);
        System.out.println(getAction(p.getDisplayName(), "staff"));*/
        if(getStaff(p) && e.getRightClicked() instanceof Player) {
        	Player clicked = (Player) e.getRightClicked();
        	/*if(p.getItemInHand().getType().equals(Material.CHEST) && p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Inspect")) {
        		e.setCancelled(true);
        		this.inspectedplayer = ((Player) e.getRightClicked()).getDisplayName();
        		p.chat("/inspect "+this.inspectedplayer);
        	} else if(p.getItemInHand().getType().equals(Material.BLAZE_ROD) && p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Freeze")) {
        		e.setCancelled(true);
        		p.chat("/freeze "+((Player) e.getRightClicked()).getDisplayName());
        	} else if(p.getItemInHand().getType().equals(Material.DIAMOND_SWORD) && p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Kill")) {
        		e.setCancelled(true);
        		p.chat("/kill "+((Player) e.getRightClicked()).getDisplayName());
        	}else if(p.getItemInHand().getType().equals(Material.GOLDEN_SWORD) && p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Sanction")) {
        		e.setCancelled(true);
        		p.chat("/ss "+((Player) e.getRightClicked()).getDisplayName());
        	}*/
        	
        	ArrayList<Item> arr = getItems();
        	ItemStack hand = p.getItemInHand();
        	if(e.getHand() == EquipmentSlot.OFF_HAND) {
        		return;
        	}
        	for(int i=0; i<arr.size(); i++) {
        		
        		Item is = arr.get(i);
        		
        		ItemStack item = is.is;
        		
        		String type = is.type;
        		e.setCancelled(true);
        		if(item.equals(hand)) {
        			
        			ArrayList<String> commands = is.commands;
        			
        			if(type.equals("player")) {
            			for(int k=0; k<commands.size(); k++) {
            				
            				String command = commands.get(k);    
            				command = command.replace("%player%", clicked.getName());
            				if(command.indexOf("/") == -1) {
            					command = "/" + command;
            				}
            				p.chat(command);
            				
            			}
        			}

        		}
        	}
        }
	}
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player && getStaff(((Player) e.getDamager()))) {
        	Player clicked = (Player) e.getEntity();
        	
        	/*if(getAction(((Player) e.getDamager()).getDisplayName(), "staff")) {
	        	if(((HumanEntity) e.getDamager()).getItemInHand().getType().equals(Material.DIAMOND_SWORD) && ((HumanEntity) e.getDamager()).getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Kill")) {
	        		e.setCancelled(true);
	        		((Player) e.getDamager()).chat("/kill "+((Player) e.getEntity()).getDisplayName());
	        	}else if(((HumanEntity) e.getDamager()).getItemInHand().getType().equals(Material.GOLDEN_SWORD) && ((HumanEntity) e.getDamager()).getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Sanction")) {
	        		e.setCancelled(true);
	        		((Player) e.getDamager()).chat("/ss "+((Player) e.getEntity()).getDisplayName());
	        	}
        	}*/
        	
        	
        	ArrayList<Item> arr = getItems();
        	ItemStack hand = ((Player) e.getDamager()).getItemInHand();
        	for(int i=0; i<arr.size(); i++) {
        		
        		Item is = arr.get(i);
        		
        		ItemStack item = is.is;
        		
        		String type = is.type;
        		
        		if(item.equals(hand)) {
        			
        			ArrayList<String> commands = is.commands;
        			
        			if(type.equals("player")) {
            			for(int k=0; k<commands.size(); k++) {
            				
            				String command = commands.get(k);    
            				command = command.replace("%player%", clicked.getName());
            				if(command.indexOf("/") == -1) {
            					command = "/" + command;
            				}
            				((Player) e.getDamager()).chat(command);
            				
            			}
        			}

        		}
        	}
        }
    }
    @EventHandler
	@SuppressWarnings("deprecation")
	public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Integer i = p.getInventory().getHeldItemSlot();
        if(getStaff(p)) {
	        /*if(e.getBlockPlaced().getType().equals(Material.CHEST)) {
	        	ItemStack is = p.getInventory().getItem(0);
	        	if(!is.getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Page 1")) {
	        		e.setCancelled(true);
	        	}
	        } else if(p.getItemInHand().getType().equals(Material.BARRIER)) {
	        	ItemStack is = p.getInventory().getItem(0);
	        	if(i == 9) {
	        		e.setCancelled(true);
	        	}
	        }*/
        	e.setCancelled(true);
        }
   }
    
	public void setstaffmode(Player player , Boolean bool, Integer page, JsonObject backup) {
		/*if(bool && page=="1") {
			ItemStack stick = new ItemStack(Material.STICK, 1);
			ItemMeta stickm = stick.getItemMeta();
			stickm.setDisplayName(ChatColor.GREEN + "Knockback");
			stick.setItemMeta(stickm);
			stick.addUnsafeEnchantment(Enchantment.KNOCKBACK, 5);
			
			ItemStack chest = new ItemStack(Material.CHEST, 1);
			ItemMeta chestm = chest.getItemMeta();
			chestm.setDisplayName(ChatColor.GREEN + "Inspect");
			chest.setItemMeta(chestm);
			
			ItemStack green = new ItemStack(Material.ENDER_EYE, 1);
			ItemMeta greenm = green.getItemMeta();
			greenm.setDisplayName(ChatColor.GREEN + "Vanish");
			green.setItemMeta(greenm);
			
			ItemStack fly = new ItemStack(Material.FEATHER, 1);
			ItemMeta flym = fly.getItemMeta();
			flym.setDisplayName(ChatColor.GREEN + "Fly");
			fly.setItemMeta(flym);
			
			ItemStack reports = new ItemStack(Material.PAPER, 1);
			ItemMeta reportsm = reports.getItemMeta();
			reportsm.setDisplayName(ChatColor.GREEN + "Reports");
			reports.setItemMeta(reportsm);
			
			ItemStack random = new ItemStack(Material.ENDER_PEARL, 1);
			ItemMeta randomm = random.getItemMeta();
			randomm.setDisplayName(ChatColor.GREEN + "Random TP");
			random.setItemMeta(randomm);
			
			ItemStack speed = new ItemStack(Material.RABBIT_FOOT, 1);
			ItemMeta speedm = speed.getItemMeta();
			speedm.setDisplayName(ChatColor.GREEN + "Speed");
			speed.setItemMeta(speedm);
			
			ItemStack freeze = new ItemStack(Material.BLAZE_ROD, 1);
			ItemMeta freezem = freeze.getItemMeta();
			freezem.setDisplayName(ChatColor.GREEN + "Freeze");
			freeze.setItemMeta(freezem);
			
			ItemStack pag = new ItemStack(Material.ARROW, 1);
			ItemMeta pagem = pag.getItemMeta();
			pagem.setDisplayName(ChatColor.GREEN + "Page 2");
			pag.setItemMeta(pagem);
			
			player.getInventory().setItem(0, stick);
			player.getInventory().setItem(1, chest);
			player.getInventory().setItem(2, green);
			player.getInventory().setItem(3, fly);
			player.getInventory().setItem(4, reports);
			player.getInventory().setItem(5, random);
			player.getInventory().setItem(6, speed);
			player.getInventory().setItem(7, freeze);
			player.getInventory().setItem(8, pag);
		} else if(page=="2") {
			ItemStack pag = new ItemStack(Material.ARROW, 1);
			ItemMeta pagem = pag.getItemMeta();
			pagem.setDisplayName(ChatColor.BLUE + "Page 1");
			pag.setItemMeta(pagem);
			
			ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
			ItemMeta swordm = sword.getItemMeta();
			swordm.setDisplayName(ChatColor.BLUE + "Kill");
			sword.setItemMeta(swordm);
			
			ItemStack sanction = new ItemStack(Material.GOLDEN_SWORD, 1);
			ItemMeta sanctionm = sanction.getItemMeta();
			sanctionm.setDisplayName(ChatColor.BLUE + "Sanction");
			sanction.setItemMeta(sanctionm);
			
			ItemStack br = new ItemStack(Material.BARRIER, 1);
			ItemMeta brm = br.getItemMeta();
			brm.setDisplayName(ChatColor.BLUE + "Exit");
			br.setItemMeta(brm);
			
			ItemStack air = new ItemStack(Material.AIR, 1);
			
			player.getInventory().setItem(0, pag);
			player.getInventory().setItem(1, sword);
			player.getInventory().setItem(2, sanction);
			player.getInventory().setItem(3, air);
			player.getInventory().setItem(4, air);
			player.getInventory().setItem(5, air);
			player.getInventory().setItem(6, air);
			player.getInventory().setItem(7, air);
			player.getInventory().setItem(8, br);
		}*/
		
		//not enough pages
		ArrayList<Item> its = getItems();
		/*if(its.size() < 8 + (page-1)*7) {
			return;
		}*/
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
	public JsonObject backupeverything(Player player) {
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
	public void recover(Player player, JsonObject o) {
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
	
	private static JsonObject createJSONObject(String jsonString){
	    JsonObject jsonObject= new JsonObject();
		JsonParser jsonParser=new JsonParser();
	    if ((jsonString != null) && !(jsonString.isEmpty())) {
	    	jsonObject = jsonParser.parse(jsonString).getAsJsonObject();
	    }
	    return jsonObject;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
	public JsonObject serialize(ItemStack is) {
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
	public ItemStack deserialize(JsonObject o) {
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
	
	@EventHandler()
	public void onPlayerMove(PlayerMoveEvent event) {
		if(isFreezed(event.getPlayer())) {
		    Player player = event.getPlayer();
			player.sendMessage(text+ChatColor.RED+"It seemes like you have been"+ChatColor.BLUE+" Freeze");
		    Location location = player.getLocation();
		    player.teleport(location);
		}
	}
	
	
	/*@SuppressWarnings("unchecked")
	public boolean getAction(String pl, String action) {
		JSONObject json = setupJson();
		JSONArray list = (JSONArray) json.get(action);
		Boolean found = false;
		for(int k=0;k<list.size(); k++) {
			JSONObject obj = (JSONObject) list.get(k);
			String player = (String) obj.get("player");
			Boolean val = (Boolean) obj.get("val");
			if(pl.equals(player)) {
				found = true;
				if(val) {
					return true;
				} else {
					return false;
				}
			}
		}
		if(!found) {
			JSONObject obj = new JSONObject();
			obj.put("player", pl);
			obj.put("val", false);
			list.add(obj);
		}
		try {
			FileWriter file = new FileWriter(js);
			file.write(json.toJSONString());
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
		
	}
	
	
	@SuppressWarnings("unchecked")
	public void setAction(String pl, String action, Boolean set) {
		JSONObject json = setupJson();
		JSONArray list = (JSONArray) json.get(action);
		for(int k=0;k<list.size(); k++) {
			JSONObject obj = (JSONObject) list.get(k);
			String player = (String) obj.get("player");
			if(pl.equals(player)) {
				obj.remove("val");
				obj.put("val", set);
				
			}
		}

		try {
			FileWriter file = new FileWriter(js);
			file.write(json.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}*/
	
	@SuppressWarnings("unchecked")
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event)  {
    	/*final Player player = (Player) event.getWhoClicked();
    	event.getClickedInventory();
    	ItemStack clicked = event.getCurrentItem();
    	if(clicked == null) {
    		return;
    	}
    	if (event.getView().getTitle().equals("Staff inspect")) {
    		event.setCancelled(true); 
	    	if (clicked.getType() == Material.CHEST && clicked.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Open Inventory")) {
	    		openinv("openinv", player.getDisplayName());
	    	} else if (clicked.getType() == Material.BLAZE_ROD && clicked.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Freeze")) {
	    		player.chat("/freeze "+this.inspectedplayer);
	    		player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10, 1);
	    	} else if (clicked.getType() == Material.ENDER_PEARL && clicked.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Teleport to player")) {
	    		player.chat("/tp "+player.getDisplayName());
	    		player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10, 1);
	    	} else if (clicked.getType() == Material.ENDER_PEARL && clicked.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Teleport player to you")) {
	    		player.chat("/tp "+this.inspectedplayer+" "+player.getDisplayName());
	    		player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10, 1);
	    	} else if (clicked.getType() == Material.END_ROD && clicked.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Sanction")) {
	    		player.chat("/sanction "+this.inspectedplayer);
	    		player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10, 1);
	    	} else if (clicked.getType() == Material.PAPER && clicked.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Punishments history")) {
	    		player.closeInventory();
	    		player.chat("/modlogs "+this.inspectedplayer);
	    		player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10, 1);
	    	}else if(clicked.getType() == Material.BARRIER) {
	    		player.closeInventory();
	    	}
    	} else if(event.getView().getTitle().startsWith("OpenInv:")) {
    		if(clicked.getType() == Material.BARRIER && clicked.getItemMeta().getDisplayName().equals(ChatColor.RED + "Back")) {
    			String invTitle = event.getView().getTitle();
    			String[] results = invTitle.split(";");
    			
    			Inventory toBeSynced = event.getInventory();
    			
    			Player target = Bukkit.getServer().getPlayer(results[1]);
    			
    			if (target == null || !target.isOnline()) return;
    			
    			// Syncs the armor.
    			// Magic Numbers galore.
    			target.getInventory().setBoots(toBeSynced.getItem(36));
    			target.getInventory().setLeggings(toBeSynced.getItem(37));
    			target.getInventory().setChestplate(toBeSynced.getItem(38));
    			target.getInventory().setHelmet(toBeSynced.getItem(39));
    			target.getInventory().setItemInOffHand(toBeSynced.getItem(40));
    			
    			// Loops through and syncs all the slots
    			for (int i = 0; i < 36; i++) {
    				if (toBeSynced.getItem(i) != null) {
    					target.getInventory().setItem(i, toBeSynced.getItem(i));
    				}
    			}
    			openinv("inspect", player.getDisplayName());
	    	}

    	} else if(event.getView().getTitle().startsWith("Reports")) {
    		event.setCancelled(true);
    		if(clicked.getType() == Material.BARRIER && clicked.getItemMeta().getDisplayName().equals(ChatColor.RED + "Back")) {
    			player.closeInventory();
	    	} else if(clicked.getType() == Material.PLAYER_HEAD) {
	    		if(event.getClick() == ClickType.LEFT){
	    			String name = clicked.getItemMeta().getDisplayName();
	    			player.chat("/inspect " + name);
	    		} else {
	    			JSONObject json = setupJson();
	    			JSONArray reports = (JSONArray) json.get("reports");
	    			String name = clicked.getItemMeta().getDisplayName();
	    			String p = name.substring(0, name.indexOf("-"));
	    			String id = name.substring(name.indexOf("-")+1, name.length());
	    			Boolean found = false;
	    			for(int k=0; k<reports.size(); k++) {
	    				JSONObject report = (JSONObject) reports.get(k);
	    				String ids = (String) report.get("id");
	    				if(ids.equals(id)) {
	    					report.remove("status");
	    					report.put("status", "checked");
	    					found = true;
	    				}
	    			}
	    			player.closeInventory();
	    			
	    			try {
	    				FileWriter file = new FileWriter(js);
	    				file.write(json.toJSONString());
	    				file.close();
	    			} catch (IOException e) {
	    				e.printStackTrace();
	    			}

	    		}
    			
	    	}

    	}*/
		Player player = (Player) event.getWhoClicked();
    	Inventory inv = event.getClickedInventory();
    	ItemStack clicked = event.getCurrentItem();
    	if(clicked == null) {
    		return;
    	}
    	CInventory cinv = inventorymanager.getInventory(player.getUniqueId());
    	if(cinv == null) {
    		return;
    	}
    	if(event.getClick() == ClickType.LEFT){
    		cinv.clickItem(event.getCurrentItem(), event.getSlot(), inv, player, event, inventorymanager, false);
    	} else {
    		cinv.clickItem(event.getCurrentItem(), event.getSlot(), inv, player, event, inventorymanager, true);
    	}
    	
		
    }
	
	@EventHandler
    public void InvClose(InventoryCloseEvent event){
        Inventory inv = event.getInventory();
        if(inventorymanager.getInventory(event.getPlayer().getUniqueId()) != null) {
        	inventorymanager.unregisterInventory(event.getPlayer().getUniqueId());
        }
    }
	
	
	/*public void openInv(String type, Player player) {
		String pl = this.inspectedplayer;
 		
		ItemStack pane = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1);
		ItemMeta panem = pane.getItemMeta();
		panem.setDisplayName(" ");
		pane.setItemMeta(panem);
			
		ItemStack br = new ItemStack(Material.BARRIER, 1);
		ItemMeta brm = br.getItemMeta();
		brm.setDisplayName(ChatColor.RED + "Back");
		br.setItemMeta(brm);
		
		if(type=="inspect") {
	 		Inventory inv = Bukkit.createInventory(null, 36, "Staff "+type);
	 		Bukkit.getPlayer(player).openInventory(inv);
	 		
			ItemStack sanction = new ItemStack(Material.STICK, 1);
			ItemMeta sanctionm = sanction.getItemMeta();
			sanctionm.setDisplayName(ChatColor.GREEN + "Sanction");
			sanction.setItemMeta(sanctionm);
			
			ItemStack invo = new ItemStack(Material.CHEST, 1);
			ItemMeta invm = invo.getItemMeta();
			invm.setDisplayName(ChatColor.GREEN + "Open Inventory");
			invo.setItemMeta(invm);
			
			ItemStack fr = new ItemStack(Material.BLAZE_ROD, 1);
			ItemMeta frm = fr.getItemMeta();
			frm.setDisplayName(ChatColor.GREEN + "Freeze");
			fr.setItemMeta(frm);
			
			ItemStack tp = new ItemStack(Material.ENDER_PEARL, 1);
			ItemMeta tpm = tp.getItemMeta();
			tpm.setDisplayName(ChatColor.GREEN + "Teleport to player");
			tp.setItemMeta(tpm);
			
			ItemStack tp2 = new ItemStack(Material.ENDER_PEARL, 1);
			ItemMeta tp2m = tp2.getItemMeta();
			tp2m.setDisplayName(ChatColor.GREEN + "Teleport player to you");
			tp2.setItemMeta(tp2m);
			
			ItemStack his = new ItemStack(Material.PAPER, 1);
			ItemMeta hism = his.getItemMeta();
			hism.setDisplayName(ChatColor.GREEN + "Punishments history");
			his.setItemMeta(hism);
			
			inv.setItem(0, pane);
			inv.setItem(1, pane);
			inv.setItem(2, pane);
			inv.setItem(3, pane);
			inv.setItem(4, pane);
			inv.setItem(5, pane);
			inv.setItem(6, pane);
			inv.setItem(7, pane);
			inv.setItem(8, pane);
			inv.setItem(9, pane);
			inv.setItem(10, pane);
			
			inv.setItem(11, tp);
			
			inv.setItem(12, sanction);
			
			inv.setItem(13, fr);
			
			inv.setItem(14, invo);
			
			inv.setItem(15, tp2);
			
			inv.setItem(16, pane);
			inv.setItem(17, pane);
			
 			inv.setItem(18, pane);
 			inv.setItem(19, pane);
 			inv.setItem(20, pane);
 			inv.setItem(21, pane);
 			
 			inv.setItem(22, his);
 			
 			inv.setItem(23, pane);
 			inv.setItem(24, pane);
 			inv.setItem(25, pane);
 			inv.setItem(26, pane);
			
 			inv.setItem(27, pane);
 			inv.setItem(28, pane);
 			inv.setItem(29, pane);
 			inv.setItem(30, pane);
 			
 			inv.setItem(31, br);
 			
 			inv.setItem(32, pane);
 			inv.setItem(33, pane);
 			inv.setItem(34, pane);
 			inv.setItem(35, pane);
		} else if(type=="openinv") {
			Inventory targetInventory = Bukkit.getPlayer(player).getInventory();
			Inventory inv = Bukkit.createInventory(Bukkit.getPlayer(pl), 45, "OpenInv:" + pl);
			
			// loop through the target inventory and copy the items over to
			// the new one
			for (int i = 0; i < 36; i++) {
				if (targetInventory.getItem(i) != null) {
					inv.setItem(i, targetInventory.getItem(i));
				}
			}
			
			// Loop through the armor and add it to the new inventory
			int pos = 36;
			for (ItemStack item : Bukkit.getPlayer(pl).getInventory().getArmorContents()) {
				if (item != null) inv.setItem(pos, item);
				pos++;
			}
			
			ItemStack item = Bukkit.getPlayer(pl).getInventory().getItemInOffHand();;
			if (item != null) inv.setItem(pos, item);
			
			// Open the inventory; everything went smooth
			inv.setItem(44, br);
			Bukkit.getPlayer(player).openInventory(inv);
		
		} else if(type.startsWith("reports")) {
			JSONObject json = setupJson();
			JSONArray reports = (JSONArray) json.get("reports");
			Integer page = Integer.parseInt(type.substring("reports".length()));
			Inventory inv = Bukkit.createInventory(null, 54, "Reports page " + page + " ");
			if(player.equals("")) {
				int slot = 0;
				for(int k=(page-1)*45; k<reports.size() && k-(page-1)*45<45; k++) {
					JSONObject report = (JSONObject) reports.get(k);
					String date = (String) report.get("date");
					String reason = (String) report.get("reason");
					String tplayer = (String) report.get("player");
					String reporter = (String) report.get("reporter");
					String id = (String) report.get("id");
					String status = (String) report.get("status");
					if(status.equals("pending review")) {
						ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
						SkullMeta meta = (SkullMeta) skull.getItemMeta();
						meta.setDisplayName(tplayer + "-" +id);
						
						List<String> Lore = new ArrayList<>();
						
						
						Lore.add(ChatColor.WHITE + "Player: " + ChatColor.BLUE + tplayer);
						Lore.add(ChatColor.WHITE + "Reason: " + ChatColor.BLUE + reason);
				        Lore.add(ChatColor.WHITE + "Date: " + ChatColor.BLUE + date);
				        Lore.add(ChatColor.WHITE + "Reporter: " + ChatColor.BLUE + reporter);
				        Lore.add(ChatColor.WHITE + "Id: " + ChatColor.BLUE + id);
				        
				        Lore.add(ChatColor.ITALIC + "Right-Click to inspect player");
				        Lore.add(ChatColor.ITALIC + "Left-Click to mark as completed");
				        meta.setLore(Lore);
						
						meta.setOwningPlayer(Bukkit.getOfflinePlayer(tplayer));
						skull.setItemMeta(meta);
						
						
						inv.setItem(slot, skull);
						slot++;
					}
					
					
					
				}
				if(reports.size() > page*45) {
					ItemStack arrow = new ItemStack(Material.ARROW, 1);
					ItemMeta arrowm = arrow.getItemMeta();
					arrowm.setDisplayName(ChatColor.RED + "Next Page");
					arrow.setItemMeta(arrowm);
					
					inv.setItem(53, arrow);
				} else {
					inv.setItem(53, pane);
				}
				
				if(45 < page*45) {
					ItemStack arrow = new ItemStack(Material.ARROW, 1);
					ItemMeta arrowm = arrow.getItemMeta();
					arrowm.setDisplayName(ChatColor.RED + "Previous Page");
					arrow.setItemMeta(arrowm);
					
					inv.setItem(45, arrow);
				} else {
					inv.setItem(45, pane);
				}

				inv.setItem(46, pane);
				inv.setItem(47, pane);
				inv.setItem(48, pane);
				
				inv.setItem(49, br);
				
				inv.setItem(50, pane);
				inv.setItem(51, pane);
				inv.setItem(52, pane);
				Bukkit.getPlayer(player).openInventory(inv);
				
				
			} else if(!player.equals("")) {
				int slot = 0;
				for(int k=(page-1)*45; k<reports.size() && k-(page-1)*45<45; k++) {
					JSONObject report = (JSONObject) reports.get(k);
					String date = (String) report.get("date");
					String reason = (String) report.get("reason");
					String tplayer = (String) report.get("player");
					String reporter = (String) report.get("reporter");
					String id = (String) report.get("id");
					String status = (String) report.get("status");
					if(tplayer.equals(player) && status.equals("pending review")) {
						
						ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
						SkullMeta meta = (SkullMeta) skull.getItemMeta();
						meta.setDisplayName(tplayer + "-" +id);
						
						List<String> Lore = new ArrayList<>();
						
						
						Lore.add(ChatColor.WHITE + "Player: " + ChatColor.BLUE + tplayer);
						Lore.add(ChatColor.WHITE + "Reason: " + ChatColor.BLUE + reason);
				        Lore.add(ChatColor.WHITE + "Date: " + ChatColor.BLUE + date);
				        Lore.add(ChatColor.WHITE + "Reporter: " + ChatColor.BLUE + reporter);
				        Lore.add(ChatColor.WHITE + "Id: " + ChatColor.BLUE + id);
				        
				        Lore.add(ChatColor.ITALIC + "Right-Click to inspect player");
				        Lore.add(ChatColor.ITALIC + "Left-Click to mark as completed");
				        meta.setLore(Lore);
						
						meta.setOwningPlayer(Bukkit.getOfflinePlayer(tplayer));
						skull.setItemMeta(meta);
						
						
						inv.setItem(slot, skull);
						
						slot++;
					}
					
					
				}
				if(reports.size() > page*45) {
					ItemStack arrow = new ItemStack(Material.ARROW, 1);
					ItemMeta arrowm = arrow.getItemMeta();
					arrowm.setDisplayName(ChatColor.RED + "Next Page");
					arrow.setItemMeta(arrowm);
					
					inv.setItem(53, arrow);
				} else {
					inv.setItem(53, pane);
				}
				
				if(45 < page*45) {
					ItemStack arrow = new ItemStack(Material.ARROW, 1);
					ItemMeta arrowm = arrow.getItemMeta();
					arrowm.setDisplayName(ChatColor.RED + "Previous Page");
					arrow.setItemMeta(arrowm);
					
					inv.setItem(45, arrow);
				} else {
					inv.setItem(45, pane);
				}

				inv.setItem(46, pane);
				inv.setItem(47, pane);
				inv.setItem(48, pane);
				
				inv.setItem(49, br);
				
				inv.setItem(50, pane);
				inv.setItem(51, pane);
				inv.setItem(52, pane);
				
				Bukkit.getPlayer(player).openInventory(inv);
				
			}
			
			
		}
			
	}*/


	@SuppressWarnings("deprecation")
	private void openInv(String inv, Player player, String otherinfos) {
		
		if(inv.equals("reports")) {
			CInventory cinv = new CInventory();

				ResultSet rs = null;
				if(otherinfos != null) {
					AtomicReference<ResultSet> res = new AtomicReference<ResultSet>();
					SQLManager.getInstance().query(" SELECT * FROM staff_reports\r\n" +
							" WHERE player = '"+ otherinfos + "';", re -> {
						try {

							ArrayList<CItem> citems = new ArrayList<CItem>();
							while(re.next()) {
								String date = re.getString("date");
								String reason = re.getString("reason");
								String tplayer = re.getString("player");
								Integer id = re.getInt("id");
								String name = Bukkit.getOfflinePlayer(UUID.fromString(tplayer)).getName();
								String reporter = re.getString("reporter");

								ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
								SkullMeta meta = (SkullMeta) skull.getItemMeta();
								meta.setDisplayName(name);

								List<String> Lore = new ArrayList<String>();


								String namerep = Bukkit.getOfflinePlayer(UUID.fromString(reporter)).getName();

								Lore.add(ChatColor.WHITE + "Player: " + ChatColor.BLUE + name);
								Lore.add(ChatColor.WHITE + "Reason: " + ChatColor.BLUE + reason);
								Lore.add(ChatColor.WHITE + "Date: " + ChatColor.BLUE + date);
								Lore.add(ChatColor.WHITE + "Reporter: " + ChatColor.BLUE + namerep);

								Lore.add(ChatColor.ITALIC + "Right-Click to inspect player");
								Lore.add(ChatColor.ITALIC + "Left-Click to mark as completed");
								meta.setLore(Lore);

								meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(tplayer)));
								skull.setItemMeta(meta);

								CItem it = new CItem(skull, "inspect " + name);
								ArrayList<String> arr = new ArrayList<String>();arr.add("reportcomplete "+id);arr.add("{leave}");
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


						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
					rs = res.get();
				} else {
					AtomicReference<ResultSet> res = new AtomicReference<ResultSet>();
					SQLManager.getInstance().query(" SELECT * FROM staff_reports;", re -> {
						try {

							ArrayList<CItem> citems = new ArrayList<CItem>();
							while(re.next()) {
								String date = re.getString("date");
								String reason = re.getString("reason");
								String tplayer = re.getString("player");
								Integer id = re.getInt("id");
								String name = Bukkit.getOfflinePlayer(UUID.fromString(tplayer)).getName();
								String reporter = re.getString("reporter");

								ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
								SkullMeta meta = (SkullMeta) skull.getItemMeta();
								meta.setDisplayName(name);

								List<String> Lore = new ArrayList<String>();


								String namerep = Bukkit.getOfflinePlayer(UUID.fromString(reporter)).getName();

								Lore.add(ChatColor.WHITE + "Player: " + ChatColor.BLUE + name);
								Lore.add(ChatColor.WHITE + "Reason: " + ChatColor.BLUE + reason);
								Lore.add(ChatColor.WHITE + "Date: " + ChatColor.BLUE + date);
								Lore.add(ChatColor.WHITE + "Reporter: " + ChatColor.BLUE + namerep);

								Lore.add(ChatColor.ITALIC + "Right-Click to inspect player");
								Lore.add(ChatColor.ITALIC + "Left-Click to mark as completed");
								meta.setLore(Lore);

								meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(tplayer)));
								skull.setItemMeta(meta);

								ArrayList<String> arr = new ArrayList<String>();arr.add("reportcomplete "+id);arr.add("{leave}");
								CItem it = new CItem(skull, arr);
								ArrayList<String> arr2 = new ArrayList<String>();arr2.add("inspect " + name);
								it.addLeft(arr2);
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


						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
					rs = res.get();
				}

				if(rs == null) return;
				

			
				
		} else if(inv.equals("baltop")) {
			CInventory cinv = new CInventory();



				SQLManager.getInstance().query("SELECT * FROM `staff_baltop` WHERE normal_blocks > 50 ORDER BY (emerald)/normal_blocks, (diamonds)/normal_blocks, (netherite)/normal_nether, (iron)/normal_blocks, (redstone)/normal_blocks, (lapis)/normal_blocks, (coal)/normal_blocks;", rs -> {
					try {
					ArrayList<CItem> citems = new ArrayList<CItem>();

					while(rs.next()) {
						Integer diamond = rs.getInt("diamonds");
						Integer iron = rs.getInt("iron");
						Integer golds = rs.getInt("golds");
						Integer netherite = rs.getInt("netherite");
						Integer coal = rs.getInt("coal");
						String username = rs.getString("username");
						Integer normalblocks = rs.getInt("normal_blocks");
						Integer normal_nether = rs.getInt("normal_nether");

						ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
						SkullMeta meta = (SkullMeta) skull.getItemMeta();
						String url = "https://api.mojang.com/user/profiles/"+username.replace("-", "")+"/names";
						String name = null;
			        /*try {
			            String nameJson = IOUtils.toString(new URL(url));
			            JSONArray nameValue = (JSONArray) JSONValue.parseWithException(nameJson);
			            String playerSlot = nameValue.get(nameValue.size()-1).toString();
			            JSONObject nameObject = (JSONObject) JSONValue.parseWithException(playerSlot);
			            name = nameObject.get("name").toString();
			        } catch (IOException e) {
			            e.printStackTrace();
			        } catch (org.json.simple.parser.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
						name = Bukkit.getOfflinePlayer(UUID.fromString(username)).getName();
						meta.setDisplayName(name);

						List<String> Lore = new ArrayList<String>();

						System.out.println(diamond/(normalblocks+diamond) + " " + normalblocks + " " + iron + " " + golds + " " + coal + " " + iron);
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

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			});


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

			page.addItem(new CItem(pane, "{nothing}"), 16);
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
			
			/*page.addItem(new CItem(pane, "{nothing}"),  0);
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
			page.addItem(freeit1, 12);
			page.addItem(tpi, 13);
			page.addItem(tp2i, 14);
			page.addItem(hisi, 15);

			page.addItem(new CItem(pane, "{nothing}"), 16);
			page.addItem(new CItem(pane, "{nothing}"), 18);
			page.addItem(new CItem(pane, "{nothing}"), 17);*/
			
			cinv.addPage(page);
			cinv.display(player);
			inventorymanager.registerInventory(player.getUniqueId(), cinv);
		}
	}
	
	
    
}

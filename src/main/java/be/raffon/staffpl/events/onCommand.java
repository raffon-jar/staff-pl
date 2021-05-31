package be.raffon.staffpl.events;

import be.raffon.staffpl.staffpl;
import be.raffon.staffpl.utils.RandomString;
import be.raffon.staffpl.utils.Report;
import be.raffon.staffpl.utils.SQLManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class onCommand implements Listener {



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
	                player.sendMessage(staffpl.text + ChatColor.GRAY + "Fly Enabled!");
	                setAction(player.getDisplayName(), "fly", true);
	            } else  {
		            player.setAllowFlight(false);
		            player.sendMessage(staffpl.text + ChatColor.GRAY + "Fly Disabled!");
		            setAction(player.getDisplayName(), "fly", false);
	            }
			} else {
				player.sendMessage(staffpl.text+ChatColor.RED+"You don't have the permission to execute this command");
			}
		} else if(alias.equalsIgnoreCase("vanish")) {
			if(player.hasPermission("staff.vanish")){
	            if(!getAction(player.getDisplayName(), "vanish")){
	                for (Player p1 : getServer().getOnlinePlayers()) {
	                	p1.hidePlayer(player);
	                }
	                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 255));
	                player.sendMessage(staffpl.text + ChatColor.GRAY + "Vanish Enabled!");
	                setAction(player.getDisplayName(), "vanish", true);
	            } else  {
	                for (Player p1 : getServer().getOnlinePlayers()) {
	                	p1.showPlayer(player);
	                }
	                player.removePotionEffect(PotionEffectType.INVISIBILITY);
		            player.setAllowFlight(false);
		            player.sendMessage(staffpl.text + ChatColor.GRAY + "Vanish Disabled!");
		            setAction(player.getDisplayName(), "vanish", false);
	            }
			} else {
				player.sendMessage(staffpl.text+ChatColor.RED+"You don't have the permission to execute this command");
			}
		} else*/ if(alias.equalsIgnoreCase("inspect")) {
            if(player.hasPermission("staff.inspect")){
                if(args.length >= 1) {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                    if(p == null) {
                        player.sendMessage(staffpl.text+ChatColor.RED+"Player not found");
                        return true;
                    }
                    staffpl.openInv("inspect", player, p.getName());
                } else {
                    player.sendMessage(staffpl.text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/inspect <player>");
                }
            } else {
                player.sendMessage(staffpl.text+ChatColor.RED+"You don't have the permission to execute this command");
            }
        } else if(alias.equalsIgnoreCase("staff")) {
            if(player.hasPermission("staff.staff")){
                if(!staffpl.getStaff(player)){
                    //setAction(player.getDisplayName(), "staff", true);
                    staffpl.setstaffmode(player,true,1,staffpl.backupeverything(player));
                    player.sendMessage(staffpl.text + ChatColor.GRAY + "Staff mode Enabled!");
                } else {
                    //setAction(player.getDisplayName(), "staff", false);
                    staffpl.setstaffmode(player,false,1,null);
                    staffpl.recover(player, null);
                    player.sendMessage(staffpl.text + ChatColor.GRAY + "Staff mode Disabled!");
                }


            }else {
                player.sendMessage(staffpl.text+ChatColor.RED+"You don't have the permission to execute this command");
            }
        } else if(alias.equalsIgnoreCase("freeze")) {
            if(player.hasPermission("staff.freeze")){
                if(args.length >= 1) {
                    Player p = Bukkit.getPlayer(args[0]);
                    if(p == null) {
                        player.sendMessage(staffpl.text+ChatColor.RED+"Player not found");
                        return true;
                    }
                    if(!staffpl.isFreezed(p)) {
                        staffpl.freeze(p, true);
                        player.sendMessage(staffpl.text+ChatColor.BLUE + p.getDisplayName() + ChatColor.WHITE +" was successfuly freezed");
                    } else {
                        staffpl.freeze(p, false);
                        player.sendMessage(staffpl.text+ChatColor.BLUE + p.getDisplayName() + ChatColor.WHITE +" was successfuly unfreezed");
                    }
                } else {
                    player.sendMessage(staffpl.text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/freeze <player>");
                }
            } else {
                player.sendMessage(staffpl.text+ChatColor.RED+"You don't have the permission to execute this command");
            }
        } else if(alias.equalsIgnoreCase("tempban")) {
            if(player.hasPermission("staff.tempban")){
                if(args.length >= 2) {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                    if(p == null) {
                        player.sendMessage(staffpl.text+ChatColor.RED+"Player not found");
                        return true;
                    }
                    SQLManager.getInstance().update(" INSERT INTO staff_logs (type, reason, username, time, date, moderator) \r\n" +
                            " VALUES ('tempban', '" + staffpl.getfuther(2,args) + "', '"+ p.getUniqueId() +"', '"+ args[1] +"', '"+ new SimpleDateFormat("dd-MM-yyyy").format(new Date()) +"', '"+player.getUniqueId() + "')");
                    staffpl.tempsanction(p.getUniqueId(), "ban", args[1], staffpl.getfuther(2, args));
                    player.sendMessage(staffpl.text+"Successfully banned " + ChatColor.RED + args[0] + ChatColor.WHITE + " for " + ChatColor.RED + staffpl.getfuther(2, args));
                    staffpl.banned.add(p.getUniqueId());
                }
                else {
                    player.sendMessage(staffpl.text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/tempban <player> <time> [reason]");
                }
            }
        } else if(alias.equalsIgnoreCase("mute")) {
            if(player.hasPermission("staff.mute")){
                if(args.length >= 2) {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                    if(p == null) {
                        player.sendMessage(staffpl.text+ChatColor.RED+"Player not found");
                        return true;
                    }
                    String query = " INSERT INTO staff_logs (type, reason, username, time, date, moderator) VALUES ('mute', '" + staffpl.getfuther(2,args) + "', '"+ p.getUniqueId() +"', '"+ args[1] +"', '"+ new SimpleDateFormat("dd-MM-yyyy").format(new Date()) +"', '"+player.getUniqueId() + "')";
                    SQLManager.getInstance().update(query);
                    staffpl.tempsanction(p.getUniqueId(), "mute", args[1], staffpl.getfuther(2, args));
                    player.sendMessage(staffpl.text+"Successfully muted " + ChatColor.RED + args[0] + ChatColor.WHITE + " for " + ChatColor.RED + staffpl.getfuther(2, args));
                }
                else {
                    player.sendMessage(staffpl.text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/mute <player> <time> [reason]");
                }
            }
        } else if(command.getName().equalsIgnoreCase("kick")) {
            if(args.length >= 1) {
                Player p = Bukkit.getPlayer(args[0]);
                if(p == null) {
                    player.sendMessage(staffpl.text+ChatColor.RED+"Player not found");
                    return true;
                }
                String reason = "";
                if(args.length == 1) {
                    reason = "kicked by a moderator";
                } else {
                    reason = staffpl.getfuther(1, args);
                }
                p.kickPlayer(reason);
                player.sendMessage(staffpl.text+"Successfully kicked " + ChatColor.RED + args[0] + ChatColor.WHITE + " for " + ChatColor.RED + staffpl.getfuther(1, args));
                SQLManager.getInstance().update(" INSERT INTO staff_logs (type, reason, username, date, moderator) \r\n" +
                        " VALUES ('kick', '" + staffpl.getfuther(2,args) + "', '"+ p.getUniqueId() +"', '"+ new SimpleDateFormat("dd-MM-yyyy").format(new Date()) +"', '"+player.getUniqueId() + "')");
            } else {
                player.sendMessage(staffpl.text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/kick <player> [reason]");
            }
        }else if(command.getName().equalsIgnoreCase("ban")) {
            if(args.length >= 1) {
                OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                if(p == null) {
                    player.sendMessage(staffpl.text+ChatColor.RED+"Player not found");
                    return true;
                }
                String reason = "";
                if(args.length == 1) {
                    reason = "banned by a moderator";
                } else {
                    reason = staffpl.getfuther(1, args);
                }
                player.chat("/tempban " + p.getName() + " 10000000000d " + reason);

            } else {
                player.sendMessage(staffpl.text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/ban <player> [reason]");
            }
        } else if(command.getName().equalsIgnoreCase("modlogs")) {
            if(args.length >= 1) {
                OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);

                if(p == null) {
                    player.sendMessage(staffpl.text+ChatColor.RED+"Player not found");
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

                                player.sendMessage(staffpl.text + "-----------------------------------\n" + staffpl.text + ChatColor.BLUE + "Active: " + active + "\n"+ staffpl.text + ChatColor.BLUE + "Type: " + ChatColor.WHITE + type + "\n"+staffpl.text+ ChatColor.BLUE + "Reason: "+ ChatColor.WHITE + reason+"\n"+staffpl.text+ ChatColor.BLUE + "Date: "+ ChatColor.WHITE + date+"\n"+staffpl.text+ ChatColor.BLUE + "Duration: " + ChatColor.RED + days + ChatColor.WHITE + " days " + ChatColor.RED + hours + ChatColor.WHITE + " hours " + ChatColor.RED + minutes + ChatColor.WHITE + " minutes " + "\n" +staffpl.text+ ChatColor.BLUE + "Moderator: "+ ChatColor.WHITE + moderator.getName() +"\n" + staffpl.text + "-----------------------------------" );
                            } else {
                                player.sendMessage(staffpl.text + "-----------------------------------\n" + staffpl.text + ChatColor.BLUE + "Active: " + active + "\n"+ staffpl.text + ChatColor.BLUE + "Type: " + ChatColor.WHITE + type + "\n"+staffpl.text+ ChatColor.BLUE + "Reason: "+ ChatColor.WHITE + reason+"\n"+staffpl.text+ ChatColor.BLUE + "Date: "+ ChatColor.WHITE + date +"\n"+staffpl.text+ ChatColor.BLUE + "Moderator: "+ ChatColor.WHITE + moderator.getName() + "\n" + staffpl.text + "-----------------------------------" );
                            }


                        }
                        if(!found) {
                            player.sendMessage(staffpl.text + "-----------------------------------\n" + staffpl.text + "No history found for this player"+ "\n" + staffpl.text + "-----------------------------------" );

                        }
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                });



            } else {
                player.sendMessage(staffpl.text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/modlogs <player>");
            }
        }else if(command.getName().equalsIgnoreCase("report")) {
            if(args.length >= 2) {
                Player p = Bukkit.getPlayer(args[0]);
                if(p == null) {
                    player.sendMessage(staffpl.text+ChatColor.RED+"Player not found");
                    return true;
                }
                player.sendMessage(staffpl.text+"Successfully reported " + ChatColor.RED + args[0] + ChatColor.WHITE + " for " + ChatColor.RED + staffpl.getfuther(1, args));
                String random = new RandomString(10, new Random()).nextString();

                staffpl.reports.reports.add(new Report(new Timestamp(System.currentTimeMillis()), staffpl.getfuther(1,args), player.getUniqueId(), player.getUniqueId(), 0, random));
            } else {
                player.sendMessage(staffpl.text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/report <player> <reason>");
            }
        }else if(command.getName().equalsIgnoreCase("reports")) {
            if(args.length >= 1) {
                OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                if(p == null) {
                    player.sendMessage(staffpl.text+ChatColor.RED+"Player not found");
                    return true;
                }
                staffpl.openInv("reports", player, args[0]);
            } else {
                staffpl.openInv("reports", player, null);
            }
        }else if(command.getName().equalsIgnoreCase("sanction") || command.getName().equalsIgnoreCase("ss")) {
            if(args.length >= 1) {
                OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
                if(p == null) {
                    player.sendMessage(staffpl.text+ChatColor.RED+"Player not found");
                    return true;
                }
                staffpl.openInv("sanction", player, args[0]);
            } else {
                player.sendMessage(staffpl.text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/sanction <player>");
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
                staffpl.openInv("baltop", player, null);
            }
        } else if(command.getName().equalsIgnoreCase("reportcomplete")) {
            if(args.length < 1) {
                player.sendMessage(staffpl.text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/reportcomplete <id>");
                return true;
            }
            staffpl.reports.deleteKey(args[0]);
        } else if(command.getName().equalsIgnoreCase("unmute")) {
            if(args.length < 1) {
                player.sendMessage(staffpl.text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/unmute <player>");
                return true;
            }
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
            if(p == null) {
                player.sendMessage(staffpl.text+ChatColor.RED+"Player not found");
                return true;
            }

            SQLManager.getInstance().query( "SELECT * FROM staff_tempmute WHERE username='"+ p.getUniqueId() + "'", rs -> {
                try {
                    if(rs.next()) {
                        SQLManager.getInstance().update("DELETE FROM staff_tempmute WHERE username='" + p.getUniqueId() +"';");
                        SQLManager.getInstance().update(" INSERT INTO staff_logs (type, reason, username, date, moderator) \r\n" +
                                " VALUES ('unmute', '', '"+ p.getUniqueId() +"', '"+ new SimpleDateFormat("dd-MM-yyyy").format(new Date()) +"', '"+player.getUniqueId() + "')");
                        player.sendMessage(staffpl.text + "Successfuly unmuted " + p.getName());
                    } else {
                        player.sendMessage(staffpl.text + "This user is not muted");

                    }
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
        } else if(command.getName().equalsIgnoreCase("unban")) {
            if(args.length < 1) {
                player.sendMessage(staffpl.text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/unban <player>");
                return true;
            }
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
            if(p == null) {
                player.sendMessage(staffpl.text+ChatColor.RED+"Player not found");
                return true;
            }

            SQLManager.getInstance().query( "SELECT * FROM staff_tempban WHERE username='"+ p.getUniqueId() + "'", rs -> {
                try {
                    if(rs.next()) {
                        SQLManager.getInstance().update("DELETE FROM staff_tempban WHERE username='" + p.getUniqueId() +"';");
                        SQLManager.getInstance().update(" INSERT INTO staff_logs (type, reason, username, date, moderator) \r\n" +
                                " VALUES ('unban', '', '"+ p.getUniqueId() +"', '"+ new SimpleDateFormat("dd-MM-yyyy").format(new Date()) +"', '"+player.getUniqueId() + "')");
                        player.sendMessage(staffpl.text + "Successfully unbanned " + p.getName());
                        staffpl.banned.remove(p.getUniqueId());
                    } else {
                        player.sendMessage(staffpl.text + "This user is not banned.");

                    }
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
        } else if(command.getName().equalsIgnoreCase("offlineclear")) {
            if(args.length < 1) {
                player.sendMessage(staffpl.text+ChatColor.RED+"Command is: "+ChatColor.BLUE+"/offlineclear <player>");
                return true;
            }
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
            if(p == null) {
                player.sendMessage(staffpl.text+ChatColor.RED+"Player not found");
                return true;
            }

            SQLManager.getInstance().query( "SELECT * FROM staff_offlineclear WHERE username='"+ p.getUniqueId() + "'", rs -> {
                try {
                    if(rs.next()) {
                        player.sendMessage(staffpl.text + " This player was already offlinecleared. ");
                    } else {
                        System.out.println("INSERT INTO `staff_offlineclear` (`username`, `date`, `server`) VALUES ('" + p.getUniqueId() + "', current_timestamp(), '" + staffpl.getActualServer()  + "-"+player.getWorld().getName() + "');");
                        SQLManager.getInstance().update("INSERT INTO `staff_offlineclear` (`username`, `date`, `server`) VALUES ('" + p.getUniqueId() + "', current_timestamp(), '" + staffpl.getActualServer()  + "-"+player.getWorld().getName() + "');");
                        player.sendMessage(staffpl.text + " Success !");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }

        return true;
    }
}

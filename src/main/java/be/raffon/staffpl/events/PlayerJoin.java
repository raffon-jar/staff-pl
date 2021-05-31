package be.raffon.staffpl.events;

import be.raffon.staffpl.staffpl;
import be.raffon.staffpl.utils.SQLManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
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
                        evt.getPlayer().sendMessage(staffpl.text + "Please be carreful to respect the rules next time :)");
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


        SQLManager.getInstance().query(" SELECT * FROM staff_offlineclear\r\n" +
                " WHERE username = '"+ evt.getPlayer().getUniqueId() + "';", rs -> {
            try {
                if(rs.next()) {
                    String worldserv = rs.getString("server");

                    String server = worldserv.substring(0, worldserv.indexOf("-"));
                    String world_str = worldserv.substring(worldserv.indexOf("-") + 1);

                    if(server.equals(staffpl.getActualServer()) && world_str.equals(evt.getPlayer().getWorld().getName())) {
                        SQLManager.getInstance().update("DELETE FROM `staff_offlineclear`\n" +
                                "WHERE username='" + evt.getPlayer().getUniqueId() + "'");
                        evt.getPlayer().getInventory().clear();
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });


        // Change world in staff.
        Player pl = evt.getPlayer();

        if(!pl.hasPermission("staff.staff")) return;

        SQLManager.getInstance().query(" SELECT * FROM staff_backups\r\n" +
                " WHERE username = '"+ evt.getPlayer().getUniqueId() + "';", rs -> {
            try {
                while (rs.next() && !staffpl.getStaff(pl, "*")) {
                    String world = rs.getString("world");
                    String backup = rs.getString("backup");
                    Integer id = rs.getInt("id");

                    if (world.endsWith(pl.getLocation().getWorld().getName()) && world.startsWith(staffpl.getActualServer())) {
                        SQLManager.getInstance().update("DELETE FROM staff_backups WHERE id= " + id + ";");
                        staffpl.recover(pl, staffpl.createJSONObject(backup));
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

                            if ((server.equals(staffpl.getActualServer())) && staff) {
                                SQLManager.getInstance().update(" UPDATE staff_players \r\n" +
                                        " SET world = '" + staffpl.getActualServer() + "-" + pl.getWorld().getName() + "'\r\n" +
                                        " WHERE username = '" + pl.getUniqueId() + "'; ");
                                return;
                            }
                            if (!world.equals(staffpl.getActualServer() + "-" +pl.getLocation().getWorld().getName()) && staff) {
                                SQLManager.getInstance().query(" SELECT * FROM staff_backups\r\n" +
                                        " WHERE username = '" + evt.getPlayer().getUniqueId() + "' AND world='" + staffpl.getActualServer() + "-" + pl.getLocation().getWorld().getName() + "';", rs3 -> {
                                    try {

                                        if (!rs3.next()) {
                                            SQLManager.getInstance().update(" INSERT INTO staff_backups (username, world, backup) \r\n" +
                                                    "VALUES ('" + evt.getPlayer().getUniqueId() + "', '" + world + "', '" + backup + "');");
                                            staffpl.setstaffmode(pl, true, 1, staffpl.backupeverything(pl));

                                            pl.chat("/vanish");
                                        } else {
                                            Integer id = rs3.getInt("id");
                                            String back = rs3.getString("backup");
                                            SQLManager.getInstance().update("DELETE FROM staff_backups WHERE id= " + id + ";");

                                            SQLManager.getInstance().update(" INSERT INTO staff_backups (username, world, backup) \r\n" +
                                                    "VALUES ('" + evt.getPlayer().getUniqueId() + "', '" + world + "', '" + backup + "');");
                                            staffpl.setstaffmode(pl, true, 1, staffpl.createJSONObject(back));

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
}

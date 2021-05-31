package be.raffon.staffpl.events;

import be.raffon.staffpl.staffpl;
import be.raffon.staffpl.utils.Pstats;
import be.raffon.staffpl.utils.SQLManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.sql.SQLException;

public class PlayerInteracts implements Listener {


    @EventHandler()
    public void onPlayerMove(PlayerMoveEvent event) {
        if(staffpl.isFreezed(event.getPlayer())) {
            Player player = event.getPlayer();
            player.sendMessage(staffpl.text+ ChatColor.RED+"It seemes like you have been"+ChatColor.BLUE+" Freeze");
            Location location = player.getLocation();
            player.teleport(location);
        }
    }



    @SuppressWarnings("deprecation")
    @EventHandler
    public void PlayerPickupItemEvent(PlayerPickupItemEvent evt) {
        if(staffpl.getStaff(evt.getPlayer())) {
            evt.setCancelled(true);
        }
    }



    @EventHandler
    public void onChat(AsyncPlayerChatEvent evt) {
        SQLManager.getInstance().query(" SELECT * FROM staff_tempmute\r\n" +
                " WHERE username = '"+ evt.getPlayer().getUniqueId() + "';", rs -> {
            try {
                while(rs.next()) {
                    String reason = rs.getString("reason");
                    long start = rs.getTimestamp("start").getTime();
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
                        evt.getPlayer().sendMessage(staffpl.text + "Please be carreful to respect the rules next time :)");
                        staffpl.banned.remove(evt.getPlayer().getUniqueId());
                        try {
                            SQLManager.getInstance().update("DELETE FROM staff_tempmute WHERE id= " + rs.getInt("id") +";");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

        });
    }





}

package be.raffon.staffpl.events;

import be.raffon.staffpl.staffpl;
import be.raffon.staffpl.utils.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class PlayerInteractEntity implements Listener {


    @EventHandler
    @SuppressWarnings("deprecation")
    public void onClickOnEntity(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if(staffpl.getStaff(p) && e.getRightClicked() instanceof Player) {
            Player clicked = (Player) e.getRightClicked();
            ArrayList<Item> arr = staffpl.getItems();
            ItemStack hand = p.getItemInHand();
            if(e.getHand() == EquipmentSlot.OFF_HAND) {
                return;
            }
            for (Item is : arr) {

                ItemStack item = is.is;

                String type = is.type;
                e.setCancelled(true);
                if (item.equals(hand)) {

                    ArrayList<String> commands = is.commands;

                    if (type.equals("player")) {
                        for (String command : commands) {
                            command = command.replace("%player%", clicked.getName());
                            if (!command.contains("/")) {
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
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player && staffpl.getStaff(((Player) e.getDamager()))) {
            Player clicked = (Player) e.getEntity();

            ArrayList<Item> arr = staffpl.getItems();
            ItemStack hand = ((Player) e.getDamager()).getItemInHand();
            for (Item is : arr) {

                ItemStack item = is.is;

                String type = is.type;

                if (item.equals(hand)) {

                    ArrayList<String> commands = is.commands;

                    if (type.equals("player")) {
                        for (String command : commands) {

                            command = command.replace("%player%", clicked.getName());
                            if (!command.contains("/")) {
                                command = "/" + command;
                            }
                            ((Player) e.getDamager()).chat(command);

                        }
                    }

                }
            }
        }
    }
}

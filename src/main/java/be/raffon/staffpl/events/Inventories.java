package be.raffon.staffpl.events;

import be.raffon.staffpl.inventories.CInventory;
import be.raffon.staffpl.staffpl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Inventories implements Listener {


    @EventHandler
    public void InvClose(InventoryCloseEvent event){
        Inventory inv = event.getInventory();
        if(staffpl.inventorymanager.getInventory(event.getPlayer().getUniqueId()) != null) {
            staffpl.inventorymanager.unregisterInventory(event.getPlayer().getUniqueId());
        }
    }



    @SuppressWarnings("unchecked")
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)  {
        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getClickedInventory();
        ItemStack clicked = event.getCurrentItem();
        if(clicked == null) {
            return;
        }
        CInventory cinv = staffpl.inventorymanager.getInventory(player.getUniqueId());
        if(cinv == null) {
            return;
        }
        if(event.getClick() == ClickType.LEFT){
            cinv.clickItem(event.getCurrentItem(), event.getSlot(), inv, player, event, staffpl.inventorymanager, false);
        } else {
            cinv.clickItem(event.getCurrentItem(), event.getSlot(), inv, player, event, staffpl.inventorymanager, true);
        }


    }

}

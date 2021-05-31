package be.raffon.staffpl.events;

import be.raffon.staffpl.staffpl;
import be.raffon.staffpl.utils.Pstats;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.sql.SQLException;

public class BlockBreakPlace implements Listener {


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) throws SQLException {
        Block block = event.getBlock();
        if (!staffpl.getStaff(event.getPlayer()) && event.getPlayer().getWorld().getName().equals("survie") && event.getPlayer().getGameMode() == GameMode.SURVIVAL && staffpl.getActualServer().equals("survie")) {
            if (block.getType() == Material.DIAMOND_ORE) {
                Pstats stats = staffpl.baltop.getFromPlayer(event.getPlayer().getUniqueId());
                stats.diamonds++;
                staffpl.baltop.updateStats(stats, event.getPlayer().getUniqueId());
            }
            if (block.getType() == Material.GOLD_ORE) {
                Pstats stats = staffpl.baltop.getFromPlayer(event.getPlayer().getUniqueId());
                stats.golds++;
                staffpl.baltop.updateStats(stats, event.getPlayer().getUniqueId());
            }
            if (block.getType() == Material.IRON_ORE) {
                Pstats stats = staffpl.baltop.getFromPlayer(event.getPlayer().getUniqueId());
                stats.iron++;
                staffpl.baltop.updateStats(stats, event.getPlayer().getUniqueId());
            }
            if (block.getType() == Material.COAL_ORE) {
                Pstats stats = staffpl.baltop.getFromPlayer(event.getPlayer().getUniqueId());
                stats.Coals++;
                staffpl.baltop.updateStats(stats, event.getPlayer().getUniqueId());
            }
            if (block.getType() == Material.ANCIENT_DEBRIS) {
                Pstats stats = staffpl.baltop.getFromPlayer(event.getPlayer().getUniqueId());
                stats.netherite++;
                staffpl.baltop.updateStats(stats, event.getPlayer().getUniqueId());
            }
            if (block.getType() == Material.STONE || block.getType() == Material.ANDESITE || block.getType() == Material.GRANITE || block.getType() == Material.DIORITE || block.getType() == Material.GRAVEL) {
                Pstats stats = staffpl.baltop.getFromPlayer(event.getPlayer().getUniqueId());
                stats.normal_blocks++;
                staffpl.baltop.updateStats(stats, event.getPlayer().getUniqueId());
            }
            if (block.getType() == Material.BASALT || block.getType() == Material.NETHERRACK || block.getType() == Material.BLACKSTONE) {
                Pstats stats = staffpl.baltop.getFromPlayer(event.getPlayer().getUniqueId());
                stats.normal_nether++;
                staffpl.baltop.updateStats(stats, event.getPlayer().getUniqueId());
            }
        }
    }



    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Integer i = p.getInventory().getHeldItemSlot();
        if(staffpl.getStaff(p)) {
            e.setCancelled(true);
        }
    }
}

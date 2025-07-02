package com.tomkeuper.bedwars.listeners;

import com.tomkeuper.bedwars.BedWars;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.metadata.FixedMetadataValue;

import static com.tomkeuper.bedwars.BedWars.plugin;

public class TNTListener implements Listener {

//    @EventHandler
//    public void onEntityExplode(EntityExplodeEvent event) {
//        if (!(event.getEntity() instanceof TNTPrimed)) return;
//
//        BedWars.debug("TNT Loc: " + event.getLocation().toString());
//        BedWars.debug("Amount of blocks to explode: " + event.blockList().size());
//
//        TNTPrimed tnt = (TNTPrimed) event.getEntity();
//
//        // Skip if it's already a custom explosion
//        if (tnt.hasMetadata("customExplosion")) {
//            BedWars.debug("TNT explosion already handled, skipping custom explosion logic.");
//            // Cancel default explosion behavior
////            event.setCancelled(true);
//            return;
//        }
//
//        // Mark this TNT entity as handled
//        event.getEntity().setMetadata("customExplosion", new FixedMetadataValue(plugin, true));
//
//        event.blockList().clear();
//
//        BedWars.nms.customExplosion(
//                event.getEntity().getWorld(),
//                event.getEntity(),
//                event.getEntity().getLocation().getX(),
//                event.getEntity().getLocation().getY(),
//                event.getEntity().getLocation().getZ(),
//                4.0F, // explosion size
//                false, // set fire
//                true   // break blocks
//        );
//        BedWars.debug("Amount of blocks to explode2: " + event.blockList().size());
//
//    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event) {

        BedWars.debug("TNT explosion detected");
        BedWars.debug("Metadata: " + event.getEntity().getMetadata("custom-explosion").get(0).asString());
        if (event.getEntity() != null && event.getEntity().getMetadata("custom-explosion").get(0).asBoolean()) {
            // Let this one happen
            BedWars.debug("has custom-explosion metadata, allowing explosion.");
            return;
        }

        // Cancel default explosion to prevent double execution
        event.setCancelled(true);
        BedWars.debug("TNT explosion detected, cancelling default explosion behavior.");
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (event.getEntity() instanceof TNTPrimed) {
//            event.setCancelled(true); // stop Minecraft's explosion

            // Run custom explosion instead
            if (!event.getEntity().hasMetadata("custom-explosion")){
                event.setCancelled(true);
                event.getEntity().remove();

                TNTPrimed newTNT = (TNTPrimed) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.PRIMED_TNT);
                newTNT.setFuseTicks(0);
                newTNT.setMetadata("custom-explosion", new FixedMetadataValue(plugin, true));
                BedWars.nms.customExplosion(
                        newTNT.getWorld(),
                        newTNT,
                        newTNT.getLocation().getX(),
                        newTNT.getLocation().getY(),
                        newTNT.getLocation().getZ(),
                        4.0F, // explosion size
                        event.getFire(), // set fire
                        true   // break blocks
                );

            }
        }
    }

}

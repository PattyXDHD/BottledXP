package de.pattyxdhd.bottledxp.listener;

import de.pattyxdhd.bottledxp.utils.XPUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;

public class ExpBottleListener implements Listener {

    @EventHandler
    public void onExp(ExpBottleEvent expBottleEvent){
        expBottleEvent.setExperience(XPUtils.getXpAmount());
    }

}

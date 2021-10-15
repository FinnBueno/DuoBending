package me.finnbon.duobending;

import com.projectkorra.projectkorra.event.PlayerBindChangeEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Finn Bon
 */
public class DuoBendingListener implements Listener {

	@EventHandler
	public void onBind(PlayerBindChangeEvent event) {
		if (!event.isBinding()) return;

		if (event.getAbility().equals("LightningStrike")) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You cannot bind a duo ability!");
		}
	}

}

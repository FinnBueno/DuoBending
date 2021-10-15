package me.finnbon.duobending.lightningstrike;

import me.finnbon.duobending.util.DuoUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

/**
 * @author Finn Bon
 */
public class LightningStrikeListener implements Listener {

	@EventHandler
	public void rightClickBender(PlayerInteractAtEntityEvent event) {
		Entity targetEntity = event.getRightClicked();
		if (!(targetEntity instanceof Player)) return;
		Player target = (Player) targetEntity;

		Player initiator = event.getPlayer();

		Player[] players = new Player[] { initiator, target };
		new LightningStrike(players);
	}

	@EventHandler
	public void leftClick(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		LightningStrike lightningStrike = DuoUtil.findDuoAbility(LightningStrike.class, player);
		if (lightningStrike == null) return;

		lightningStrike.leftClick(player);
	}

}

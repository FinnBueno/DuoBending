package me.finnbon.duobending;

import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Finn Bon
 */
public class DuoBending extends JavaPlugin {

	@Override
	public void onEnable() {
		CoreAbility.registerPluginAbilities(this, "me.finnbon.duobending.lightningstrike");
		getServer().getPluginManager().registerEvents(new DuoBendingListener(), this);
	}
}

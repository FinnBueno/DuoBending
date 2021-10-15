package me.finnbon.duobending.util;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * @author Finn Bon
 */
public final class DuoUtil {

	public static <T extends CoreAbility & DuoAbilityVerifier> T findDuoAbility(Class<T> abilityClass, Player participant) {
		Collection<T> instances = CoreAbility.getAbilities(abilityClass);
		for (T instance : instances) {
			if (instance.getPlayers().stream().map(BendingPlayer::getPlayer).anyMatch(p -> p.getUniqueId() == participant.getUniqueId())) {
				return instance;
			}
		}
		return null;
	}

}

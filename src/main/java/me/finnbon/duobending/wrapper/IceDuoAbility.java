package me.finnbon.duobending.wrapper;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.IceAbility;
import com.projectkorra.projectkorra.ability.WaterAbility;
import me.finnbon.duobending.util.DuoAbilityVerifier;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * @author Finn Bon
 */
public abstract class IceDuoAbility extends IceAbility implements DuoAbilityVerifier {

	public List<BendingPlayer> players;

	public IceDuoAbility(Player...players) {
		this(Arrays.stream(players).map(BendingPlayer::getBendingPlayer).toArray(BendingPlayer[]::new));
	}

	public IceDuoAbility(BendingPlayer ...players) {
		super(players[0].getPlayer());
		if (players.length < 2) {
			throw new IllegalArgumentException("A duo combo requires at least 2 players!");
		}
		this.players = Arrays.asList(players);

	}

	@Override
	public List<BendingPlayer> getPlayers() {
		return players;
	}
}

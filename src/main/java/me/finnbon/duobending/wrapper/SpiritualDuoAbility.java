package me.finnbon.duobending.wrapper;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.FlightAbility;
import com.projectkorra.projectkorra.ability.SpiritualAbility;
import me.finnbon.duobending.util.DuoAbilityVerifier;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * @author Finn Bon
 */
public abstract class SpiritualDuoAbility extends SpiritualAbility implements DuoAbilityVerifier {

	public List<BendingPlayer> players;

	public SpiritualDuoAbility(Player...players) {
		this(Arrays.stream(players).map(BendingPlayer::getBendingPlayer).toArray(BendingPlayer[]::new));
	}

	public SpiritualDuoAbility(BendingPlayer ...players) {
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

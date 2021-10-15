package me.finnbon.duobending.wrapper;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.LightningAbility;
import me.finnbon.duobending.util.DuoAbilityVerifier;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Finn Bon
 */
public abstract class LightningDuoAbility extends LightningAbility implements DuoAbilityVerifier {

	public List<BendingPlayer> players;

	public LightningDuoAbility(Player ...players) {
		this(Arrays.stream(players).map(BendingPlayer::getBendingPlayer).toArray(BendingPlayer[]::new));
	}

	public LightningDuoAbility(BendingPlayer ...players) {
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

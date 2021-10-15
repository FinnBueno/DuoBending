package me.finnbon.duobending.util;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Finn Bon
 */
public interface DuoAbilityVerifier {

	List<BendingPlayer> getPlayers();

	/**
	 * A version of {@link BendingPlayer#canBend(CoreAbility)} that verifies multiple people can bend a certain ability.
	 * @param ability The duo ability that all people need to be able to bend
	 * @param boundAbility The ability all people should have bounded on their active slot in order to use this move.
	 *                        If null, no particular ability is required to be held active
	 * @return True if all people involved can bend this move and have the proper normal ability on their active slot, false otherwise
	 */
	@SuppressWarnings("unused")
	default boolean canBend(CoreAbility ability, CoreAbility boundAbility) {
		for (BendingPlayer bendingPlayer : getPlayers()) {
			if (!bendingPlayer.canBendIgnoreBinds(ability) || (boundAbility != null && bendingPlayer.canBendIgnoreCooldowns(boundAbility))) {
				return false;
			}
		}
		return true;
	}

	default void forAll(Consumer<Player> forBoth) {
		for (BendingPlayer player : getPlayers()) {
			forBoth.accept(player.getPlayer());
		}
	}

	default void forAll(BiConsumer<BendingPlayer, Player> forBoth) {
		for (BendingPlayer player : getPlayers()) {
			forBoth.accept(player, player.getPlayer());
		}
	}

	default boolean forAll(Function<Player, Boolean> forBoth) {
		for (BendingPlayer player : getPlayers()) {
			if (!forBoth.apply(player.getPlayer())) {
				return false;
			}
		}
		return true;
	}

	default boolean forAll(BiFunction<BendingPlayer, Player, Boolean> forBoth) {
		for (BendingPlayer player : getPlayers()) {
			if (!forBoth.apply(player, player.getPlayer())) {
				return false;
			}
		}
		return true;
	}

	default boolean hasPlayer(Player player) {
		return this.getPlayers().stream().map(BendingPlayer::getPlayer).anyMatch(p -> p.getUniqueId() == player.getUniqueId());
	}

	default boolean hasPlayer(BendingPlayer player) {
		return this.getPlayers().contains(player);
	}

}

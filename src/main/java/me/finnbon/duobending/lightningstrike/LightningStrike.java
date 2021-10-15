package me.finnbon.duobending.lightningstrike;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.firebending.lightning.Lightning;
import me.finnbon.duobending.DuoBending;
import me.finnbon.duobending.wrapper.LightningDuoAbility;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Finn Bon
 */
public class LightningStrike extends LightningDuoAbility implements AddonAbility {

	private static final long CHARGE_TIME = 3000;
	private static final long STREAM_TIME = 3000;
	private static final double RANGE = 25;
	private static final double MAX_GAP_BETWEEN_TARGETS = 2;
	private static final long COOLDOWN = 2000;
	private LightningStrikeListener listener;

	enum State {
		CHARGING, TRAVELING, STREAMING
	}

	private State state;
	private long startedAt;
	private Map<UUID, Location> targetLocations;
	private Location target;

	public LightningStrike(Player...players) {
		super(players);
		if (!this.canBend(this, CoreAbility.getAbility(Lightning.class))) {
			return;
		}
		if (!areChargingLightning()) {
			return;
		}
		this.state = State.CHARGING;
		this.targetLocations = new HashMap<>();
		start();
	}

	private boolean areChargingLightning() {
		Lightning[] lightnings = new Lightning[players.size()];
		for (int i = 0, playersLength = players.size(); i < playersLength; i++) {
			BendingPlayer bPlayer = players.get(i);
			Lightning lightning = CoreAbility.getAbility(bPlayer.getPlayer(), Lightning.class);
			if (lightning.getState() != Lightning.State.START) {
				return false;
			}
			lightnings[i] = lightning;
		}
		for (Lightning lightning : lightnings) {
			if (this.startedAt == 0 || this.startedAt > lightning.getTime()) {
				this.startedAt = lightning.getTime();
			}
			lightning.remove();
		}
		return true;
	}

	@Override
	public void progress() {
		if (!canBend(this, CoreAbility.getAbility(Lightning.class))) {
			remove();
			return;
		}
		if (forAll(p -> !p.isSneaking())) {
			remove();
			return;
		}
		switch (this.state) {
			case CHARGING : runCharging(); break;
			case TRAVELING: runTraveling(); break;
			case STREAMING: runStreaming(); break;
		}
	}

	private void runStreaming() {
		if (System.currentTimeMillis() - startedAt > STREAM_TIME) {
			remove();
			return;
		}
		forAll(p -> {
			Location start = player.getLocation().add(0, .8, 0);
			Vector between = this.target.toVector().subtract(start.toVector());
			double length = between.length();
			between.normalize().multiply(.5);
			for (double step = 0; step <= length; step += .5) {
				playLightningbendingParticle(start, .25, .25, .25);
				if (ThreadLocalRandom.current().nextInt(5) == 0) {
					playLightningbendingSound(start);
				}
				start.add(between);
			}
		});
	}

	private void runTraveling() {
		// check distances and worlds
		Collection<Location> locations = this.targetLocations.values();
		Iterator<Location> iterator = locations.iterator();
		Location previous = iterator.next();
		World world = previous.getWorld();
		boolean gapTooLarge = false;
		while (iterator.hasNext()) {
			Location next = iterator.next();
			if (previous.distance(next) > MAX_GAP_BETWEEN_TARGETS) {
				gapTooLarge = true;
				break;
			}
			previous = next;
		}

		if (gapTooLarge) {
			this.target = new Location(
				world,
				players.stream().map(bp -> bp.getPlayer().getLocation().getX()).reduce(0.0, Double::sum) / locations.size(),
				players.stream().map(bp -> bp.getPlayer().getLocation().getY()).reduce(0.0, Double::sum) / locations.size(),
				players.stream().map(bp -> bp.getPlayer().getLocation().getZ()).reduce(0.0, Double::sum) / locations.size()
			);
		} else {
			this.target = new Location(
				world,
				locations.stream().map(Location::getX).reduce(0.0, Double::sum) / locations.size(),
				locations.stream().map(Location::getY).reduce(0.0, Double::sum) / locations.size(),
				locations.stream().map(Location::getZ).reduce(0.0, Double::sum) / locations.size()
			);
		}
		explode();
		this.startedAt = System.currentTimeMillis();
		this.state = State.STREAMING;
	}

	private void runCharging() {
		boolean isCharged = System.currentTimeMillis() - startedAt > CHARGE_TIME;
		if (isCharged) {
			for (int i = 1; i < this.players.size(); i++) {
				Player start = this.players.get(i - 1).getPlayer();
				Player end = this.players.get(i).getPlayer();
				drawLineBetweenPoints(start.getLocation().add(0, 1, 0), end.getLocation().add(0, 1, 0));
			}
			if (this.targetLocations.size() == this.players.size()) {
				this.state = State.TRAVELING;
			}
		}
		forAll(p -> {
			Location loc = p.getLocation().add(0, .5, 0);
			playLightningbendingParticle(loc, .4, .4, .4);
			if (ThreadLocalRandom.current().nextInt(4) == 0) {
				playLightningbendingSound(loc);
			}
		});
	}

	private void explode() {
		Objects.requireNonNull(this.target.getWorld()).createExplosion(this.target, 4.5f);
	}

	private void drawLineBetweenPoints(Location start, Location end) {
		start = start.clone();
		Vector between = end.toVector().subtract(start.toVector());
		double length = between.length();
		between.normalize().multiply(.5);
		for (double step = 0; step <= length; step += .5) {
			playLightningbendingParticle(start, .25, .25, .25);
			start.add(between);
		}
	}

	public void leftClick(Player player) {
		if (this.hasPlayer(player)) {
			this.targetLocations.put(player.getUniqueId(), GeneralMethods.getTargetedLocation(player, RANGE));
		}
	}

	@Override
	public void remove() {
		super.remove();
		forAll((bp, p) -> { bp.addCooldown(getName(), getCooldown()); });
	}

	@Override
	public boolean isSneakAbility() {
		return true;
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public long getCooldown() {
		return COOLDOWN;
	}

	@Override
	public String getName() {
		return "LightningStrike";
	}

	@Override
	public Location getLocation() {
		return this.target == null ? this.player.getLocation() : this.target;
	}

	@Override
	public void load() {
		DuoBending plugin = JavaPlugin.getPlugin(DuoBending.class);
		listener = new LightningStrikeListener();
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);
	}

	@Override
	public void stop() {
		HandlerList.unregisterAll(listener);
	}

	@Override
	public String getAuthor() {
		return "FinnBueno";
	}

	@Override
	public String getVersion() {
		return "1.0.0";
	}

	@Override
	public String getDescription() {
		String original = super.getDescription();
		if (original == null) {
			return ChatColor.GRAY + "Duo Ability - " + Element.FIRE.getColor() + "This ability allows 2 lightning benders to channel a large stream of lightning together into a single point. To do this, both benders must start charging their normal lightning ability. One bender then right-clicks the other one, after which they'll both start charging together. Once the charge has completed, a line will appear between the two benders. They can then click at a location to shoot a stream of lightning to the midpoint of their targets. If they choose target locations that are too far apart, the move fails and they hurt themselves. Otherwise, a stream of lightning appears for a few seconds, and explodes on impact.";
		}
		return original;
	}
}

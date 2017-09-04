package me.xnuminousx.korra.cosmicblast;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.projectkorra.projectkorra.ability.CoreAbility;

public class CosmicBlastListener implements Listener {
	@EventHandler (ignoreCancelled = true)
	public void onSwing(PlayerToggleSneakEvent event) {
		if (event.isCancelled()) {
			return;
		} else if (CoreAbility.hasAbility(event.getPlayer(), CosmicBlast.class)) {
			return;
		}
		new CosmicBlast(event.getPlayer());
	}
}

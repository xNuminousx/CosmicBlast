package me.xnuminousx.korra.cosmicblast;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.AvatarAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;

public class CosmicBlast extends AvatarAbility implements AddonAbility {
	private long cooldown;
	private double range;
	private double damage;
	private boolean isCharged;
	private boolean launched;
	private Location origin;
	private Location location;
	private Vector direction;
	private double t;
	private Permission perm;
	private boolean doPotEffects;
	private boolean doDamage;

	public CosmicBlast(Player player) {
		super(player);
		if (!this.bPlayer.canBend(this)) {
			return;
		}
		setFields();
		start();
	}
	public void setFields() {
		this.cooldown = ConfigManager.getConfig().getLong("ExtraAbilities.xNuminousx.CosmicBlast.Cooldown");
		this.range = ConfigManager.getConfig().getLong("ExtraAbilities.xNuminousx.CosmicBlast.Range");
		this.damage = ConfigManager.getConfig().getLong("ExtraAbilities.xNuminousx.CosmicBlast.Damage");
		this.doPotEffects = ConfigManager.getConfig().getBoolean("ExtraAbilities.xNuminousx.CosmicBlast.DoPotionEffects");
		this.doDamage = ConfigManager.getConfig().getBoolean("ExtraAbilities.xNuminousx.CosmicBlast.DoDamage");
	}
	
	@Override
	public void progress() {
		if (player.isDead() || !player.isOnline()) {
			return;
		}
		if ((player.isSneaking()) && (!launched)) {
			chargeAnimation();
		} else {
			if (!isCharged) {
				remove();
				return;
			}
			if (!launched) {
				bPlayer.addCooldown(this);
				launched = true;
			}
			blast();
		}

	}
	private void chargeAnimation() {
		t += Math.PI / 32;
		Location loc = player.getLocation();
		if (t >= Math.PI * 4) {
			ParticleEffect.FIREWORKS_SPARK.display(player.getLocation().add(0, 1, 0), 1, 0.2, 0.3, 0.2, 0.03F);
			ParticleEffect.DRAGON_BREATH.display(player.getLocation(), 1, 0.3, 0.1, 0.3, 0.02F);
			location = GeneralMethods.getTargetedLocation(player, 1);
			origin = GeneralMethods.getTargetedLocation(player, 1);
			direction = GeneralMethods.getTargetedLocation(player, 1).getDirection();
			isCharged = true;
			launched = false;
		}
		
		if (isCharged) {
			loc.getWorld().playSound(loc, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 0.2F, 1);
			
		} else {
			for (double phi = 0; phi <= Math.PI * 2; phi += Math.PI / 1.5) {
				double x = 0.3D * (Math.PI * 4 - t) * Math.cos(t + phi);
	            double y = 1.5D * (Math.PI * 4 - t);
	            double z = 0.3D * (Math.PI * 4 - t) * Math.sin(t + phi);
	            loc.add(x, y, z);
				ParticleEffect.PORTAL.display(loc, 5, 0, 0, 0, 0.1F);
				ParticleEffect.SPELL_MOB_AMBIENT.display(loc, 2, 0, 0, 0, 0.02F);
				ParticleEffect.END_ROD.display(loc, 2, 0, 0, 0, 0);
				ParticleEffect.CRIT_MAGIC.display(loc, 5, 0, 0, 0, 0);
				loc.subtract(x, y, z);
				loc.getWorld().playSound(loc, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 0.2F, 1);
			}
		}
	}
	private void blast() {
		direction = GeneralMethods.getTargetedLocation(player, 1).getDirection();
		location.add(direction);
		ParticleEffect.CRIT_MAGIC.display(location, 5, 0, 0, 0, 1);
		ParticleEffect.END_ROD.display(location, 3, 0, 0, 0, 0.05F);
		ParticleEffect.PORTAL.display(location, 5, 0, 0, 0, 1.5F);
		location.getWorld().playSound(location, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1, 0.01F);
		
		for (Entity e : GeneralMethods.getEntitiesAroundPoint(location, 2.5D)) {
			if (((e instanceof LivingEntity)) && (e.getEntityId() != player.getEntityId())) {
				if (doDamage) {
					DamageHandler.damageEntity(e, damage, this);
					
				}
				if (doPotEffects) {
					LivingEntity le = (LivingEntity)e;
					le.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 1), true);
					le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1), true);
					
				}
				remove();
				return;
			}
		}
		if (origin.distance(location) > range) {
			remove();
			bPlayer.addCooldown(this);
			return;
		}
	}
	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public Location getLocation() {
		return null;
	}

	@Override
	public String getName() {
		return "CosmicBlast";
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public boolean isSneakAbility() {
		return true;
	}

	@Override
	public String getAuthor() {
		return "xNuminousx";
	}
	@Override
	public String getDescription() {
		return "Meditate on your 7th Chakra to gain energy and guidence from the cosmos. Focus this energy outward toward your opponent to exhaust them and deal damage.";
	}
	@Override
	public String getInstructions() {
		return "Hold SHIFT until charge animation finishes";
	}

	@Override
	public String getVersion() {
		return "1.4";
	}

	@Override
	public void load() {
		ProjectKorra.plugin.getServer().getPluginManager().registerEvents(new CosmicBlastListener(), ProjectKorra.plugin);
		ProjectKorra.log.info("Successfully loaded " + getName() + " by " + getAuthor());
		
		perm = new Permission("bending.ability.cosmicblast");
		ProjectKorra.plugin.getServer().getPluginManager().addPermission(perm);
		perm.setDefault(PermissionDefault.FALSE);
		
		ConfigManager.getConfig().addDefault("ExtraAbilities.xNuminousx.CosmicBlast.Cooldown", 5000);
		ConfigManager.getConfig().addDefault("ExtraAbilities.xNuminousx.CosmicBlast.Range", 20);
		ConfigManager.getConfig().addDefault("ExtraAbilities.xNuminousx.CosmicBlast.DoPotionEffects", true);
		ConfigManager.getConfig().addDefault("ExtraAbilities.xNuminousx.CosmicBlast.DoDamage", true);
		ConfigManager.getConfig().addDefault("ExtraAbilities.xNuminousx.CosmicBlast.Damage", 5);
		ConfigManager.defaultConfig.save();
	}

	@Override
	public void stop() {
		ProjectKorra.plugin.getServer().getPluginManager().removePermission(this.perm);
		super.remove();
		ProjectKorra.plugin.getServer().getLogger().info(getName() + "disabled.");
	}

}
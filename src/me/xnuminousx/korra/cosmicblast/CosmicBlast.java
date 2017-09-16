package me.xnuminousx.korra.cosmicblast;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.AvatarAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;

public class CosmicBlast extends AvatarAbility implements AddonAbility {
	private long cooldown;
	private double damage;
	private Location location;
	private Permission perm;
	private double radius;
	private long time;
	private Material blockType;
	private byte blockByte;
	private long minFocusDuration;

	public CosmicBlast(Player player) {
		super(player);
		if (!this.bPlayer.canBend(this)) {
			return;
		}
		setFields();
		this.time = System.currentTimeMillis();
		
		start();
	}
	public void setFields() {
		this.cooldown = ConfigManager.getConfig().getLong("ExtraAbilities.xNuminousx.CosmicBlast.Cooldown");
		this.damage = ConfigManager.getConfig().getDouble("ExtraAbilities.xNuminousx.CosmicBlast.Damage");
		this.radius = ConfigManager.getConfig().getLong("ExtraAbilities.xNuminousx.CosmicBlast.Radius");
		this.minFocusDuration = ConfigManager.getConfig().getLong("ExtraAbilities.xNuminousx.CosmicBlast.DurationOfMinimalFocus");
		this.blockType = Material.REDSTONE_BLOCK;
		this.location = player.getLocation();
	}
	
	@Override
	public void progress() {
		if (player.isDead() || !player.isOnline() || GeneralMethods.isRegionProtectedFromBuild(this, player.getLocation())) {
			remove();
			return;
		}
		
		if (player.isSneaking()) {
			selectSource();
			
		} else {
			
			if (System.currentTimeMillis() > time + minFocusDuration) {
				stealMax();
			} else if (System.currentTimeMillis() < time + minFocusDuration) {
				stealMin();
			}
		}
	}
	
	private void selectSource() {
		this.location = player.getLocation();
		for (Entity entity : GeneralMethods.getEntitiesAroundPoint(player.getLocation(), radius)) {
			if (((entity instanceof LivingEntity)) && (entity.getEntityId() != player.getEntityId())) {
				Location target = entity.getLocation();
				
				ParticleEffect.SMOKE.display(target, 0.3F, 1, 0.3F, 0.02F, 5);
				ParticleEffect.FIREWORKS_SPARK.display(location, 0, 0.5F, 0, 0.02F, 2);
				target.getWorld().playSound(target, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 2, 0.5F);
				
				if (System.currentTimeMillis() > time + minFocusDuration) {
					ParticleEffect.DRAGON_BREATH.display(location, 0, 0.5F, 0, 0.02F, 2);
					ParticleEffect.BLOCK_CRACK.display((ParticleEffect.ParticleData) new ParticleEffect.BlockData(blockType, blockByte), 0.5F, 0.5F, 0.5F, 1, 5, target, 500);
				}
			}
		}
	}
	
	private void stealMin() {
		for (Entity entity : GeneralMethods.getEntitiesAroundPoint(player.getLocation(), radius)) {
			if (((entity instanceof LivingEntity)) && (entity.getEntityId() != player.getEntityId())) {
				LivingEntity le = (LivingEntity)entity;
				
				// Target Effects
				le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 200, 1), true);
				le.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 1), true);
				
				// Player Effects
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 50, 1), true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 2), true);
				
				playSound();
			}
		}
		remove();
		return;
	}
	
	private void stealMax() {
		for (Entity entity : GeneralMethods.getEntitiesAroundPoint(player.getLocation(), radius)) {
			if (((entity instanceof LivingEntity)) && (entity.getEntityId() != player.getEntityId())) {
				LivingEntity le = (LivingEntity)entity;
				
				// Target effects
				le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 300, 3), true);
				le.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 250, 3), true);
				le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1), true);
				le.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 1000, 5), true);
				DamageHandler.damageEntity(le, damage, this);
				
				// Player effects
				player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 350, 1), true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 150, 3), true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1), true);
				
				playSound();
			}
		}
		remove();
		return;
	}
	
	private void playSound() {
		player.getLocation().getWorld().playSound(location, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 0.5F, 2);
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
		return "Focus on the energy of your surrounding opponents. Release shift quickly to extract a small amount of energy from them or keep holding to reach MaxFocus, in which case you can extract more energy from your opponent.";
	}
	@Override
	public String getInstructions() {
		return "Hold shift";
	}

	@Override
	public String getVersion() {
		return "2.0";
	}

	@Override
	public void load() {
		ProjectKorra.plugin.getServer().getPluginManager().registerEvents(new CosmicBlastListener(), ProjectKorra.plugin);
		ProjectKorra.log.info("Successfully loaded " + getName() + " by " + getAuthor());
		
		perm = new Permission("bending.ability.cosmicblast");
		ProjectKorra.plugin.getServer().getPluginManager().addPermission(perm);
		perm.setDefault(PermissionDefault.TRUE);
		
		ConfigManager.getConfig().addDefault("ExtraAbilities.xNuminousx.CosmicBlast.Cooldown", 5000);
		ConfigManager.getConfig().addDefault("ExtraAbilities.xNuminousx.CosmicBlast.Damage", 3);
		ConfigManager.getConfig().addDefault("ExtraAbilities.xNuminousx.CosmicBlast.Radius", 5);
		ConfigManager.getConfig().addDefault("ExtraAbilities.xNuminousx.CosmicBlast.DurationOfMinimalFocus", 5000);
		ConfigManager.defaultConfig.save();
	}

	@Override
	public void stop() {
		ProjectKorra.plugin.getServer().getPluginManager().removePermission(this.perm);
		super.remove();
		ProjectKorra.plugin.getServer().getLogger().info(getName() + "disabled.");
	}

}
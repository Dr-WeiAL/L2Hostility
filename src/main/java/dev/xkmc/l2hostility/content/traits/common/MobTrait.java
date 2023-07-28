package dev.xkmc.l2hostility.content.traits.common;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.l2hostility.content.config.TraitConfig;
import dev.xkmc.l2hostility.init.L2Hostility;
import dev.xkmc.l2hostility.init.registrate.LHTraits;
import dev.xkmc.l2library.base.NamedEntry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class MobTrait extends NamedEntry<MobTrait> {

	public MobTrait() {
		super(LHTraits.TRAITS);
	}

	@SuppressWarnings("ConstantConditions")
	public TraitConfig getConfig() {
		TraitConfig ans = L2Hostility.TRAIT.getEntry(getRegistryName());
		if (ans == null) return TraitConfig.DEFAULT;
		return ans;
	}

	public int getCost() {
		return getConfig().cost;
	}

	public int getMaxLevel() {
		return getConfig().maxLevel;
	}

	public boolean allow(LivingEntity le, int difficulty) {
		TraitConfig config = getConfig();
		if (difficulty < config.cost) return false;
		if (config.blacklist.contains(le.getType())) return false;
		if (!config.whitelist.isEmpty() && !config.whitelist.contains(le.getType())) return false;
		return le.getRandom().nextDouble() < config.chance;
	}

	public void initialize(LivingEntity le, int level) {
	}

	public void tick(LivingEntity mob, int level) {
	}

	public void onHurtTarget(int level, LivingEntity attacker, AttackCache cache) {
	}

	public void onAttackedByOthers(int level, LivingEntity entity, LivingAttackEvent event) {
	}

	public void onHurtByOthers(int level, LivingEntity entity, LivingHurtEvent event) {
	}

	public void onCreateSource(int level, LivingEntity attacker, CreateSourceEvent event) {
	}

}
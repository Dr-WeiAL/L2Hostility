package dev.xkmc.l2hostility.content.item.tools;

import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.traits.common.MobTrait;
import dev.xkmc.l2hostility.init.data.LangData;
import dev.xkmc.l2hostility.init.registrate.LHTraits;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TraitAdderWand extends Item {

	private static final String TRAIT = "l2hostility_trait";

	public static ItemStack set(ItemStack ans, MobTrait trait) {
		ans.getOrCreateTag().putString(TRAIT, trait.getID());
		return ans;
	}

	public static MobTrait get(ItemStack stack) {
		if (stack.getOrCreateTag().contains(TRAIT, Tag.TAG_STRING)) {
			String str = stack.getOrCreateTag().getString(TRAIT);
			ResourceLocation id = new ResourceLocation(str);
			MobTrait ans = LHTraits.TRAITS.get().getValue(id);
			if (ans != null) {
				return ans;
			}
		}
		return LHTraits.TANK.get();
	}

	private static List<MobTrait> values() {
		return new ArrayList<>(LHTraits.TRAITS.get().getValues());
	}

	private static MobTrait next(MobTrait mod) {
		var list = values();
		int index = list.indexOf(mod);
		if (index + 1 >= list.size()) {
			return list.get(0);
		}
		return list.get(index + 1);
	}

	private static MobTrait prev(MobTrait mod) {
		var list = values();
		int index = list.indexOf(mod);
		if (index == 0) {
			return list.get(list.size() - 1);
		}
		return list.get(index - 1);
	}

	@Nullable
	private static Integer inc(MobTrait k, @Nullable Integer old) {
		if (old == null || old == 0) {
			return k.getMaxLevel();
		}
		if (old == 1) {
			return null;
		}
		return old - 1;
	}

	@Nullable
	private static Integer dec(MobTrait k, @Nullable Integer old) {
		if (old == null) {
			return 1;
		}
		if (old == k.getMaxLevel()) {
			return null;
		}
		return old + 1;
	}

	public TraitAdderWand(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
		if (MobTraitCap.HOLDER.isProper(target)) {
			if (player.level().isClientSide()) {
				return InteractionResult.SUCCESS;
			}
			MobTraitCap cap = MobTraitCap.HOLDER.get(target);
			MobTrait trait = get(stack);
			Integer ans;
			if (player.isShiftKeyDown()) {
				ans = cap.traits.compute(trait, TraitAdderWand::inc);
			} else {
				ans = cap.traits.compute(trait, TraitAdderWand::dec);
			}
			int val = ans == null ? 0 : ans;
			player.sendSystemMessage(LangData.MSG_SET_TRAIT.get(trait.getDesc(), target.getDisplayName(), val));
		}
		return InteractionResult.PASS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		MobTrait old = get(stack), next;
		if (player.isShiftKeyDown()) {
			next = prev(old);
		} else {
			next = next(old);
		}
		set(stack, next);
		player.sendSystemMessage(LangData.MSG_SELECT_TRAIT.get(next.getDesc()));
		return InteractionResultHolder.success(player.getItemInHand(hand));
	}

}
package dev.xkmc.l2hostility.content.item.curio.misc;

import dev.xkmc.l2hostility.compat.curios.CurioCompat;
import dev.xkmc.l2hostility.compat.curios.EntitySlotAccess;
import dev.xkmc.l2hostility.content.item.curio.core.CurioItem;
import dev.xkmc.l2hostility.content.item.curio.core.ICapItem;
import dev.xkmc.l2hostility.content.item.traits.SealedItem;
import dev.xkmc.l2hostility.init.data.LangData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.List;

public class PocketOfRestoration extends CurioItem implements ICapItem<PocketOfRestoration.Cap> {

	private static final String ROOT = "UnsealRoot", KEY = "SealedSlotKey", START = "UnsealStartTime";

	public static void setData(ItemStack stack, ItemStack sealed, String id, long time) {
		var tag = stack.getOrCreateTagElement(ROOT);
		tag.putInt(SealedItem.TIME, sealed.getOrCreateTag().getInt(SealedItem.TIME));
		tag.put(SealedItem.DATA, sealed.getOrCreateTag().get(SealedItem.DATA));
		tag.putString(KEY, id);
		tag.putLong(START, time);
	}

	public PocketOfRestoration(Properties properties, int durability) {
		super(properties, durability);
	}

	@Override
	public Cap create(ItemStack stack) {
		return new Cap(stack);
	}

	public record Cap(ItemStack stack) implements ICurio {

		@Override
		public ItemStack getStack() {
			return stack;
		}

		@Override
		public void curioTick(SlotContext slotContext) {
			var le = slotContext.entity();
			if (le.level().isClientSide) return;
			var list = CurioCompat.getItemAccess(le);

			if (stack.getTag() != null && stack.getTag().contains(ROOT)) {
				var tag = stack.getOrCreateTagElement(ROOT);
				long time = tag.getLong(START);
				int dur = tag.getInt(SealedItem.TIME);
				if (le.level().getGameTime() >= time + dur) {
					ItemStack result = ItemStack.of(tag.getCompound(SealedItem.DATA));
					EntitySlotAccess slot = CurioCompat.decode(tag.getString(KEY), le);
					if (slot != null && slot.get().isEmpty()) {
						slot.set(result);
						stack.getTag().remove(ROOT);
					} else if (le instanceof Player player && player.addItem(result)) {
						stack.getTag().remove(ROOT);
					}

				}
				return;
			}
			if (stack.getDamageValue() + 1 >= stack.getMaxDamage())
				return;
			for (var e : list) {
				if (e.get().getItem() instanceof SealedItem) {
					ItemStack sealed = e.get();
					e.set(ItemStack.EMPTY);
					String id = e.getID();
					long time = le.level().getGameTime();
					stack.hurtAndBreak(1, le, x -> {
					});
					setData(stack, sealed, id, time);
					return;
				}
			}
		}
	}


	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		list.add(LangData.POCKET_OF_RESTORATION.get().withStyle(ChatFormatting.GOLD));
		if (stack.getTag() != null && stack.getTag().contains(ROOT)) {
			list.add(LangData.TOOLTIP_SEAL_DATA.get().withStyle(ChatFormatting.GRAY));
			list.add(ItemStack.of(stack.getOrCreateTagElement(ROOT).getCompound(SealedItem.DATA)).getHoverName());
		}

	}

}
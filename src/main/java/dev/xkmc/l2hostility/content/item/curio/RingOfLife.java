package dev.xkmc.l2hostility.content.item.curio;

import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LangData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RingOfLife extends CurioItem {

	public RingOfLife(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		int perc = (int) Math.round(LHConfig.COMMON.ringOfLifeMaxDamage.get() * 100);
		list.add(LangData.ITEM_RING_LIFE.get(perc).withStyle(ChatFormatting.GOLD));
	}

}
package dev.xkmc.l2hostility.init;

import com.tterrag.registrate.providers.ProviderType;
import dev.xkmc.l2damagetracker.contents.attack.AttackEventHandler;
import dev.xkmc.l2hostility.content.capability.chunk.ChunkDifficulty;
import dev.xkmc.l2hostility.content.capability.mob.CapSyncPacket;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.capability.player.PlayerDifficulty;
import dev.xkmc.l2hostility.content.config.TraitConfig;
import dev.xkmc.l2hostility.content.config.WorldDifficultyConfig;
import dev.xkmc.l2hostility.events.LHAttackListener;
import dev.xkmc.l2hostility.init.data.*;
import dev.xkmc.l2hostility.init.registrate.*;
import dev.xkmc.l2hostility.init.registrate.entries.LHRegistrate;
import dev.xkmc.l2library.serial.config.ConfigTypeEntry;
import dev.xkmc.l2library.serial.config.PacketHandlerWithConfig;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(L2Hostility.MODID)
@Mod.EventBusSubscriber(modid = L2Hostility.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class L2Hostility {

	public static final String MODID = "l2hostility";
	public static final PacketHandlerWithConfig HANDLER = new PacketHandlerWithConfig(
			new ResourceLocation(MODID, "main"), 1,
			e -> e.create(CapSyncPacket.class, NetworkDirection.PLAY_TO_CLIENT)
	);
	public static final Logger LOGGER = LogManager.getLogger();
	public static final LHRegistrate REGISTRATE = new LHRegistrate(MODID);

	public static final ConfigTypeEntry<WorldDifficultyConfig> DIFFICULTY = new ConfigTypeEntry<>(HANDLER, "difficulty", WorldDifficultyConfig.class);
	public static final ConfigTypeEntry<TraitConfig> TRAIT = new ConfigTypeEntry<>(HANDLER, "trait", TraitConfig.class);

	private static void registerRegistrates(IEventBus bus) {
		ForgeMod.enableMilkFluid();
		LHItems.register();
		LHTraits.register();
		LHEffects.register();
		LHParticle.register();
		LHEnchantments.register();
		LHEntities.register();
		LHMiscs.register();
		LHConfig.init();

		MobTraitCap.register();
		ChunkDifficulty.register();
		PlayerDifficulty.register();

		REGISTRATE.addDataGenerator(ProviderType.LANG, LangData::addTranslations);
		REGISTRATE.addDataGenerator(ProviderType.RECIPE, RecipeGen::genRecipe);
		REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, TagGen::onBlockTagGen);
		REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, TagGen::onItemTagGen);
		REGISTRATE.addDataGenerator(ProviderType.ENTITY_TAGS, TagGen::onEntityTagGen);
	}

	public L2Hostility() {
		FMLJavaModLoadingContext ctx = FMLJavaModLoadingContext.get();
		IEventBus bus = ctx.getModEventBus();
		registerRegistrates(bus);
		AttackEventHandler.register(4500, new LHAttackListener());
	}

	@SubscribeEvent
	public static void setup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {

		});
	}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		boolean gen = event.includeServer();
		PackOutput output = event.getGenerator().getPackOutput();
		var pvd = event.getLookupProvider();
		var helper = event.getExistingFileHelper();
		event.getGenerator().addProvider(gen, new LHConfigGen(event.getGenerator()));
	}

}
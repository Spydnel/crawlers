package net.spydnel.crawlers;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.spydnel.crawlers.entity.ModEntities;
import net.spydnel.crawlers.entity.custom.CrawlerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreepersCrawlers implements ModInitializer {

	public static final String MOD_ID = "crawlers";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Identifier CRAWLER_FUSE = new Identifier("crawlers:entity.crawler.fuse");
	public static SoundEvent ENTITY_CRAWLER_FUSE = SoundEvent.of(CRAWLER_FUSE);

	@Override
	public void onInitialize() {
		Registry.register(Registries.SOUND_EVENT, CreepersCrawlers.CRAWLER_FUSE, ENTITY_CRAWLER_FUSE);
		FabricDefaultAttributeRegistry.register(ModEntities.CRAWLER, CrawlerEntity.CreateCrawlerAttributes());
	}
}
package net.spydnel.crawlers;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.spydnel.crawlers.entity.ModEntities;
import net.spydnel.crawlers.entity.custom.CrawlerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreepersCrawlers implements ModInitializer {

	public static final String MOD_ID = "crawlers";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		FabricDefaultAttributeRegistry.register(ModEntities.CRAWLER, CrawlerEntity.CreateCrawlerAttributes());
	}
}
package net.spydnel.crawlers;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.spydnel.crawlers.entity.ModEntities;
import net.spydnel.crawlers.entity.client.CrawlerRenderer;

public class CreepersCrawlersClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.register(ModEntities.CRAWLER, CrawlerRenderer::new);
    }
}

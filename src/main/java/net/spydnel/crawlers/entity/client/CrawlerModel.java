package net.spydnel.crawlers.entity.client;

import net.minecraft.util.Identifier;
import net.spydnel.crawlers.CreepersCrawlers;
import net.spydnel.crawlers.entity.custom.CrawlerEntity;
import software.bernie.geckolib.model.GeoModel;

public class CrawlerModel extends GeoModel<CrawlerEntity> {
    @Override
    public Identifier getModelResource(CrawlerEntity animatable) {
        return new Identifier(CreepersCrawlers.MOD_ID, "geo/crawler.geo.json");
    }

    @Override
    public Identifier getTextureResource(CrawlerEntity animatable) {
        return new Identifier(CreepersCrawlers.MOD_ID, "textures/entity/crawler.png");
    }

    @Override
    public Identifier getAnimationResource(CrawlerEntity animatable) {
        return new Identifier(CreepersCrawlers.MOD_ID, "animations/crawler.animation.json");
    }
}

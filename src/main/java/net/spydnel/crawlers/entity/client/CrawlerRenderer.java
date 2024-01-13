package net.spydnel.crawlers.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.spydnel.crawlers.CreepersCrawlers;
import net.spydnel.crawlers.entity.custom.CrawlerEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CrawlerRenderer extends GeoEntityRenderer<CrawlerEntity> {
    public CrawlerRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new CrawlerModel());
    }



    @Override
    public Identifier getTextureLocation(CrawlerEntity animatable) {
        return new Identifier(CreepersCrawlers.MOD_ID, "textures/entity/crawler.png");
    }
}

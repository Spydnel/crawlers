package net.spydnel.crawlers.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spydnel.crawlers.CreepersCrawlers;
import net.spydnel.crawlers.entity.custom.CrawlerEntity;

public class ModEntities {
    public static final EntityType<CrawlerEntity> CRAWLER = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(CreepersCrawlers.MOD_ID, "crawler"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CrawlerEntity::new).dimensions(EntityDimensions.fixed(0.8f, 0.7f)).build());
}

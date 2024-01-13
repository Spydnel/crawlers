package net.spydnel.crawlers.entity.custom;

import com.ibm.icu.impl.ValidIdentifiers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.EnumSet;

public class CrawlerEntity extends HostileEntity implements GeoEntity {
    private static final TrackedData<Boolean> SITTING =
            DataTracker.registerData(CrawlerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<Boolean> IGNITED =
            DataTracker.registerData(CrawlerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<Integer> FUSE_SPEED =
            DataTracker.registerData(CrawlerEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private int sitTime = 0;
    private boolean inAir = false;
    private int explosionRadius = 3;
    private int lastFuseTime;
    private int currentFuseTime;
    private int fuseTime = 30;
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    public CrawlerEntity(EntityType<? extends CrawlerEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder CreateCrawlerAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25);
    }

    protected void initGoals() {
        this.goalSelector.add(0, new StopMoving());
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(3, new FleeEntityGoal<>(this, OcelotEntity.class, 6.0F, 1.0, 1.2));
        this.goalSelector.add(3, new FleeEntityGoal<>(this, CatEntity.class, 6.0F, 1.0, 1.2));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));

        this.targetSelector.add(4, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_CREEPER_HURT;
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Sitting", this.isSitting());
        nbt.putByte("ExplosionRadius", (byte)this.explosionRadius);
        nbt.putBoolean("ignited", this.isIgnited());
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.getBoolean("Sitting")) {
            this.sit();
        }
        if (nbt.contains("ExplosionRadius", 99)) {
            this.explosionRadius = nbt.getByte("ExplosionRadius");
        }
    }

    @Override
    public void takeKnockback(double strength, double x, double z) {
        if (this.sitTime > 4) {
            super.takeKnockback(1.5, x, z);
        } else {
            super.takeKnockback(0, x, z);
        }
    }
    public boolean damage(DamageSource source, float amount) {
        if (super.damage(source, amount) && this.getAttacker() != null) {
            if (!this.getWorld().isClient) {
                this.sit();
                if (this.sitTime > 1) {
                    this.ignite();
                }
                this.sitTime = 1;
            }

            return true;
        } else {
            return false;
        }
    }

    private class StopMoving extends Goal {
        int timer;

        public StopMoving() {
            this.setControls(EnumSet.of(Control.LOOK, Control.JUMP, Control.MOVE));
        }

        public boolean canStart() {
            return CrawlerEntity.this.isSitting();
        }
        public void start() {
            CrawlerEntity.this.setJumping(false);
            CrawlerEntity.this.getNavigation().stop();
            CrawlerEntity.this.getMoveControl().moveTo(
                    CrawlerEntity.this.getX(), CrawlerEntity.this.getY(), CrawlerEntity.this.getZ(), 0.0D
            );
        }
    }

    @Override
    public void tick() {
        if (this.isAlive()) {
            if (this.isSitting()) {
                this.sitTime++;
            } else {
                this.sitTime = 0;
            }
            if (sitTime > 100) {
                this.stand();
            }

            this.lastFuseTime = this.currentFuseTime;
            if (this.isIgnited()) {
                this.setFuseSpeed(1);
            }

            int i = this.getFuseSpeed();
            if (i > 0 && this.currentFuseTime == 0) {
                this.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1.0F, 0.5F);
                this.emitGameEvent(GameEvent.PRIME_FUSE);
            }

            this.currentFuseTime += i;
            if (this.currentFuseTime < 0) {
                this.currentFuseTime = 0;
            }

            if (this.currentFuseTime >= this.fuseTime) {
                this.currentFuseTime = this.fuseTime;
                this.explode();
            }
        }
        super.tick();
    }

    private void explode() {
        if (!this.getWorld().isClient) {
            this.dead = true;
            this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), (float)this.explosionRadius * 1, World.ExplosionSourceType.MOB);
            this.discard();
        }

    }

    public float getClientFuseTime(float timeDelta) {
        return MathHelper.lerp(timeDelta, (float)this.lastFuseTime, (float)this.currentFuseTime) / (float)(this.fuseTime - 2);
    }

    public int getFuseSpeed() {
        return this.dataTracker.get(FUSE_SPEED);
    }

    public void setFuseSpeed(int fuseSpeed) {
        this.dataTracker.set(FUSE_SPEED, fuseSpeed);
    }

    public void sit() {
        this.dataTracker.set(SITTING, true);
    }

    public void stand() {
        this.dataTracker.set(SITTING, false);
    }

    public boolean isSitting() {
        return this.dataTracker.get(SITTING);
    }

    public boolean isIgnited() {
        return this.dataTracker.get(IGNITED);
    }

    public void ignite() {
        this.dataTracker.set(IGNITED, true);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(FUSE_SPEED, -1);
        this.dataTracker.startTracking(SITTING, false);
        this.dataTracker.startTracking(IGNITED, false);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "controller", 5, this::predicate));
    }

    private PlayState predicate(AnimationState<GeoAnimatable> geoAnimatableAnimationState) {
        if (this.isSitting()) {
            if (this.sitTime > 9) {
                if (!this.isOnGround()) {
                    inAir = true;
                    geoAnimatableAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.crawler.fly", Animation.LoopType.LOOP));
                    return PlayState.CONTINUE;
                } else if (inAir) {
                    geoAnimatableAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.crawler.land", Animation.LoopType.HOLD_ON_LAST_FRAME));
                    return PlayState.CONTINUE;
                }
            }
            geoAnimatableAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.crawler.sit", Animation.LoopType.HOLD_ON_LAST_FRAME));
            return PlayState.CONTINUE;
        } else if (geoAnimatableAnimationState.isMoving()) {
            geoAnimatableAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.crawler.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        geoAnimatableAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.crawler.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}

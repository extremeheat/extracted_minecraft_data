package net.minecraft.world.entity;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;

public class AreaEffectCloud extends Entity implements TraceableEntity {
   private static final int TIME_BETWEEN_APPLICATIONS = 5;
   private static final EntityDataAccessor<Float> DATA_RADIUS;
   private static final EntityDataAccessor<Boolean> DATA_WAITING;
   private static final EntityDataAccessor<ParticleOptions> DATA_PARTICLE;
   private static final float MAX_RADIUS = 32.0F;
   private static final int DEFAULT_AGE = 0;
   private static final int DEFAULT_DURATION_ON_USE = 0;
   private static final float DEFAULT_RADIUS_ON_USE = 0.0F;
   private static final float DEFAULT_RADIUS_PER_TICK = 0.0F;
   private static final float DEFAULT_POTION_DURATION_SCALE = 1.0F;
   private static final float MINIMAL_RADIUS = 0.5F;
   private static final float DEFAULT_RADIUS = 3.0F;
   public static final float DEFAULT_WIDTH = 6.0F;
   public static final float HEIGHT = 0.5F;
   public static final int INFINITE_DURATION = -1;
   public static final int DEFAULT_LINGERING_DURATION = 600;
   private static final int DEFAULT_WAIT_TIME = 20;
   private static final int DEFAULT_REAPPLICATION_DELAY = 20;
   private static final ColorParticleOption DEFAULT_PARTICLE;
   private PotionContents potionContents;
   private float potionDurationScale;
   private final Map<Entity, Integer> victims;
   private int duration;
   private int waitTime;
   private int reapplicationDelay;
   private int durationOnUse;
   private float radiusOnUse;
   private float radiusPerTick;
   @Nullable
   private LivingEntity owner;
   @Nullable
   private UUID ownerUUID;

   public AreaEffectCloud(EntityType<? extends AreaEffectCloud> var1, Level var2) {
      super(var1, var2);
      this.potionContents = PotionContents.EMPTY;
      this.potionDurationScale = 1.0F;
      this.victims = Maps.newHashMap();
      this.duration = -1;
      this.waitTime = 20;
      this.reapplicationDelay = 20;
      this.durationOnUse = 0;
      this.radiusOnUse = 0.0F;
      this.radiusPerTick = 0.0F;
      this.noPhysics = true;
   }

   public AreaEffectCloud(Level var1, double var2, double var4, double var6) {
      this(EntityType.AREA_EFFECT_CLOUD, var1);
      this.setPos(var2, var4, var6);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(DATA_RADIUS, 3.0F);
      var1.define(DATA_WAITING, false);
      var1.define(DATA_PARTICLE, DEFAULT_PARTICLE);
   }

   public void setRadius(float var1) {
      if (!this.level().isClientSide) {
         this.getEntityData().set(DATA_RADIUS, Mth.clamp(var1, 0.0F, 32.0F));
      }

   }

   public void refreshDimensions() {
      double var1 = this.getX();
      double var3 = this.getY();
      double var5 = this.getZ();
      super.refreshDimensions();
      this.setPos(var1, var3, var5);
   }

   public float getRadius() {
      return (Float)this.getEntityData().get(DATA_RADIUS);
   }

   public void setPotionContents(PotionContents var1) {
      this.potionContents = var1;
      this.updateColor();
   }

   public void setPotionDurationScale(float var1) {
      this.potionDurationScale = var1;
   }

   private void updateColor() {
      ParticleOptions var1 = (ParticleOptions)this.entityData.get(DATA_PARTICLE);
      if (var1 instanceof ColorParticleOption var2) {
         int var3 = this.potionContents.equals(PotionContents.EMPTY) ? 0 : this.potionContents.getColor();
         this.entityData.set(DATA_PARTICLE, ColorParticleOption.create(var2.getType(), ARGB.opaque(var3)));
      }

   }

   public void addEffect(MobEffectInstance var1) {
      this.setPotionContents(this.potionContents.withEffectAdded(var1));
   }

   public ParticleOptions getParticle() {
      return (ParticleOptions)this.getEntityData().get(DATA_PARTICLE);
   }

   public void setParticle(ParticleOptions var1) {
      this.getEntityData().set(DATA_PARTICLE, var1);
   }

   protected void setWaiting(boolean var1) {
      this.getEntityData().set(DATA_WAITING, var1);
   }

   public boolean isWaiting() {
      return (Boolean)this.getEntityData().get(DATA_WAITING);
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(int var1) {
      this.duration = var1;
   }

   public void tick() {
      super.tick();
      Level var2 = this.level();
      if (var2 instanceof ServerLevel var1) {
         this.serverTick(var1);
      } else {
         this.clientTick();
      }

   }

   private void clientTick() {
      boolean var1 = this.isWaiting();
      float var2 = this.getRadius();
      if (!var1 || !this.random.nextBoolean()) {
         ParticleOptions var3 = this.getParticle();
         int var4;
         float var5;
         if (var1) {
            var4 = 2;
            var5 = 0.2F;
         } else {
            var4 = Mth.ceil(3.1415927F * var2 * var2);
            var5 = var2;
         }

         for(int var6 = 0; var6 < var4; ++var6) {
            float var7 = this.random.nextFloat() * 6.2831855F;
            float var8 = Mth.sqrt(this.random.nextFloat()) * var5;
            double var9 = this.getX() + (double)(Mth.cos(var7) * var8);
            double var11 = this.getY();
            double var13 = this.getZ() + (double)(Mth.sin(var7) * var8);
            if (var3.getType() == ParticleTypes.ENTITY_EFFECT) {
               if (var1 && this.random.nextBoolean()) {
                  this.level().addAlwaysVisibleParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, -1), var9, var11, var13, 0.0, 0.0, 0.0);
               } else {
                  this.level().addAlwaysVisibleParticle(var3, var9, var11, var13, 0.0, 0.0, 0.0);
               }
            } else if (var1) {
               this.level().addAlwaysVisibleParticle(var3, var9, var11, var13, 0.0, 0.0, 0.0);
            } else {
               this.level().addAlwaysVisibleParticle(var3, var9, var11, var13, (0.5 - this.random.nextDouble()) * 0.15, 0.009999999776482582, (0.5 - this.random.nextDouble()) * 0.15);
            }
         }

      }
   }

   private void serverTick(ServerLevel var1) {
      if (this.duration != -1 && this.tickCount >= this.waitTime + this.duration) {
         this.discard();
      } else {
         boolean var2 = this.isWaiting();
         boolean var3 = this.tickCount < this.waitTime;
         if (var2 != var3) {
            this.setWaiting(var3);
         }

         if (!var3) {
            float var4 = this.getRadius();
            if (this.radiusPerTick != 0.0F) {
               var4 += this.radiusPerTick;
               if (var4 < 0.5F) {
                  this.discard();
                  return;
               }

               this.setRadius(var4);
            }

            if (this.tickCount % 5 == 0) {
               this.victims.entrySet().removeIf((var1x) -> this.tickCount >= (Integer)var1x.getValue());
               if (!this.potionContents.hasEffects()) {
                  this.victims.clear();
               } else {
                  ArrayList var5 = new ArrayList();
                  PotionContents var10000 = this.potionContents;
                  Objects.requireNonNull(var5);
                  var10000.forEachEffect(var5::add, this.potionDurationScale);
                  List var6 = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
                  if (!var6.isEmpty()) {
                     for(LivingEntity var8 : var6) {
                        if (!this.victims.containsKey(var8) && var8.isAffectedByPotions()) {
                           Stream var17 = var5.stream();
                           Objects.requireNonNull(var8);
                           if (!var17.noneMatch(var8::canBeAffected)) {
                              double var9 = var8.getX() - this.getX();
                              double var11 = var8.getZ() - this.getZ();
                              double var13 = var9 * var9 + var11 * var11;
                              if (var13 <= (double)(var4 * var4)) {
                                 this.victims.put(var8, this.tickCount + this.reapplicationDelay);

                                 for(MobEffectInstance var16 : var5) {
                                    if (((MobEffect)var16.getEffect().value()).isInstantenous()) {
                                       ((MobEffect)var16.getEffect().value()).applyInstantenousEffect(var1, this, this.getOwner(), var8, var16.getAmplifier(), 0.5);
                                    } else {
                                       var8.addEffect(new MobEffectInstance(var16), this);
                                    }
                                 }

                                 if (this.radiusOnUse != 0.0F) {
                                    var4 += this.radiusOnUse;
                                    if (var4 < 0.5F) {
                                       this.discard();
                                       return;
                                    }

                                    this.setRadius(var4);
                                 }

                                 if (this.durationOnUse != 0 && this.duration != -1) {
                                    this.duration += this.durationOnUse;
                                    if (this.duration <= 0) {
                                       this.discard();
                                       return;
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

         }
      }
   }

   public float getRadiusOnUse() {
      return this.radiusOnUse;
   }

   public void setRadiusOnUse(float var1) {
      this.radiusOnUse = var1;
   }

   public float getRadiusPerTick() {
      return this.radiusPerTick;
   }

   public void setRadiusPerTick(float var1) {
      this.radiusPerTick = var1;
   }

   public int getDurationOnUse() {
      return this.durationOnUse;
   }

   public void setDurationOnUse(int var1) {
      this.durationOnUse = var1;
   }

   public int getWaitTime() {
      return this.waitTime;
   }

   public void setWaitTime(int var1) {
      this.waitTime = var1;
   }

   public void setOwner(@Nullable LivingEntity var1) {
      this.owner = var1;
      this.ownerUUID = var1 == null ? null : var1.getUUID();
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.owner != null && !this.owner.isRemoved()) {
         return this.owner;
      } else {
         if (this.ownerUUID != null) {
            Level var2 = this.level();
            if (var2 instanceof ServerLevel) {
               ServerLevel var1 = (ServerLevel)var2;
               Entity var3 = var1.getEntity(this.ownerUUID);
               LivingEntity var10001;
               if (var3 instanceof LivingEntity) {
                  LivingEntity var4 = (LivingEntity)var3;
                  var10001 = var4;
               } else {
                  var10001 = null;
               }

               this.owner = var10001;
            }
         }

         return this.owner;
      }
   }

   protected void readAdditionalSaveData(CompoundTag var1) {
      this.tickCount = var1.getIntOr("Age", 0);
      this.duration = var1.getIntOr("Duration", -1);
      this.waitTime = var1.getIntOr("WaitTime", 20);
      this.reapplicationDelay = var1.getIntOr("ReapplicationDelay", 20);
      this.durationOnUse = var1.getIntOr("DurationOnUse", 0);
      this.radiusOnUse = var1.getFloatOr("RadiusOnUse", 0.0F);
      this.radiusPerTick = var1.getFloatOr("RadiusPerTick", 0.0F);
      this.setRadius(var1.getFloatOr("Radius", 3.0F));
      this.ownerUUID = (UUID)var1.read("Owner", UUIDUtil.CODEC).orElse((Object)null);
      RegistryOps var2 = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
      this.setParticle((ParticleOptions)var1.read("Particle", ParticleTypes.CODEC, var2).orElse(DEFAULT_PARTICLE));
      this.setPotionContents((PotionContents)var1.read("potion_contents", PotionContents.CODEC, var2).orElse(PotionContents.EMPTY));
      this.potionDurationScale = var1.getFloatOr("potion_duration_scale", 1.0F);
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      var1.putInt("Age", this.tickCount);
      var1.putInt("Duration", this.duration);
      var1.putInt("WaitTime", this.waitTime);
      var1.putInt("ReapplicationDelay", this.reapplicationDelay);
      var1.putInt("DurationOnUse", this.durationOnUse);
      var1.putFloat("RadiusOnUse", this.radiusOnUse);
      var1.putFloat("RadiusPerTick", this.radiusPerTick);
      var1.putFloat("Radius", this.getRadius());
      RegistryOps var2 = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
      var1.store("Particle", ParticleTypes.CODEC, var2, this.getParticle());
      var1.storeNullable("Owner", UUIDUtil.CODEC, this.ownerUUID);
      if (!this.potionContents.equals(PotionContents.EMPTY)) {
         var1.store("potion_contents", PotionContents.CODEC, var2, this.potionContents);
      }

      if (this.potionDurationScale != 1.0F) {
         var1.putFloat("potion_duration_scale", this.potionDurationScale);
      }

   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_RADIUS.equals(var1)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(var1);
   }

   public PushReaction getPistonPushReaction() {
      return PushReaction.IGNORE;
   }

   public EntityDimensions getDimensions(Pose var1) {
      return EntityDimensions.scalable(this.getRadius() * 2.0F, 0.5F);
   }

   public final boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      return false;
   }

   @Nullable
   public <T> T get(DataComponentType<? extends T> var1) {
      if (var1 == DataComponents.POTION_CONTENTS) {
         return (T)castComponentValue(var1, this.potionContents);
      } else {
         return (T)(var1 == DataComponents.POTION_DURATION_SCALE ? castComponentValue(var1, this.potionDurationScale) : super.get(var1));
      }
   }

   protected void applyImplicitComponents(DataComponentGetter var1) {
      this.applyImplicitComponentIfPresent(var1, DataComponents.POTION_CONTENTS);
      this.applyImplicitComponentIfPresent(var1, DataComponents.POTION_DURATION_SCALE);
      super.applyImplicitComponents(var1);
   }

   protected <T> boolean applyImplicitComponent(DataComponentType<T> var1, T var2) {
      if (var1 == DataComponents.POTION_CONTENTS) {
         this.setPotionContents((PotionContents)castComponentValue(DataComponents.POTION_CONTENTS, var2));
         return true;
      } else if (var1 == DataComponents.POTION_DURATION_SCALE) {
         this.setPotionDurationScale((Float)castComponentValue(DataComponents.POTION_DURATION_SCALE, var2));
         return true;
      } else {
         return super.applyImplicitComponent(var1, var2);
      }
   }

   // $FF: synthetic method
   @Nullable
   public Entity getOwner() {
      return this.getOwner();
   }

   static {
      DATA_RADIUS = SynchedEntityData.<Float>defineId(AreaEffectCloud.class, EntityDataSerializers.FLOAT);
      DATA_WAITING = SynchedEntityData.<Boolean>defineId(AreaEffectCloud.class, EntityDataSerializers.BOOLEAN);
      DATA_PARTICLE = SynchedEntityData.<ParticleOptions>defineId(AreaEffectCloud.class, EntityDataSerializers.PARTICLE);
      DEFAULT_PARTICLE = ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, -1);
   }
}

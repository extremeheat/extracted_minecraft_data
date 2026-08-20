package net.minecraft.world.entity;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

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
   private @Nullable ParticleOptions customParticle;
   private PotionContents potionContents;
   private float potionDurationScale;
   private final Map<Entity, Integer> victims;
   private int duration;
   private int waitTime;
   private int reapplicationDelay;
   private int durationOnUse;
   private float radiusOnUse;
   private float radiusPerTick;
   private @Nullable EntityReference<LivingEntity> owner;

   public AreaEffectCloud(final EntityType<? extends AreaEffectCloud> type, final Level level) {
      super(type, level);
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

   public AreaEffectCloud(final Level level, final double x, final double y, final double z) {
      this(EntityTypes.AREA_EFFECT_CLOUD, level);
      this.setPos(x, y, z);
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      entityData.define(DATA_RADIUS, 3.0F);
      entityData.define(DATA_WAITING, false);
      entityData.define(DATA_PARTICLE, DEFAULT_PARTICLE);
   }

   public void setRadius(final float radius) {
      if (!this.level().isClientSide()) {
         this.getEntityData().set(DATA_RADIUS, Mth.clamp(radius, 0.0F, 32.0F));
      }

   }

   public void refreshDimensions() {
      double x = this.getX();
      double y = this.getY();
      double z = this.getZ();
      super.refreshDimensions();
      this.setPos(x, y, z);
   }

   public float getRadius() {
      return (Float)this.getEntityData().get(DATA_RADIUS);
   }

   public void setPotionContents(final PotionContents contents) {
      this.potionContents = contents;
      this.updateParticle();
   }

   public void setCustomParticle(final @Nullable ParticleOptions customParticle) {
      this.customParticle = customParticle;
      this.updateParticle();
   }

   public void setPotionDurationScale(final float scale) {
      this.potionDurationScale = scale;
   }

   private void updateParticle() {
      if (this.customParticle != null) {
         this.entityData.set(DATA_PARTICLE, this.customParticle);
      } else {
         int color = ARGB.opaque(this.potionContents.getColor());
         this.entityData.set(DATA_PARTICLE, ColorParticleOption.create(DEFAULT_PARTICLE.getType(), color));
      }

   }

   public void addEffect(final MobEffectInstance effect) {
      this.setPotionContents(this.potionContents.withEffectAdded(effect));
   }

   public ParticleOptions getParticle() {
      return (ParticleOptions)this.getEntityData().get(DATA_PARTICLE);
   }

   protected void setWaiting(final boolean waiting) {
      this.getEntityData().set(DATA_WAITING, waiting);
   }

   public boolean isWaiting() {
      return (Boolean)this.getEntityData().get(DATA_WAITING);
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(final int duration) {
      this.duration = duration;
   }

   public void tick() {
      super.tick();
      Level var2 = this.level();
      if (var2 instanceof ServerLevel serverLevel) {
         this.serverTick(serverLevel);
      } else {
         this.clientTick();
      }

   }

   private void clientTick() {
      boolean isWaiting = this.isWaiting();
      float radius = this.getRadius();
      if (!isWaiting || !this.random.nextBoolean()) {
         ParticleOptions particle = this.getParticle();
         int particleCount;
         float particleRadius;
         if (isWaiting) {
            particleCount = 2;
            particleRadius = 0.2F;
         } else {
            particleCount = Mth.ceil(3.1415927F * radius * radius);
            particleRadius = radius;
         }

         for(int i = 0; i < particleCount; ++i) {
            float angle = this.random.nextFloat() * 6.2831855F;
            float distance = Mth.sqrt(this.random.nextFloat()) * particleRadius;
            double x = this.getX() + (double)(Mth.cos((double)angle) * distance);
            double y = this.getY();
            double z = this.getZ() + (double)(Mth.sin((double)angle) * distance);
            if (particle.getType() == ParticleTypes.ENTITY_EFFECT) {
               if (isWaiting && this.random.nextBoolean()) {
                  this.level().addAlwaysVisibleParticle(DEFAULT_PARTICLE, x, y, z, 0.0, 0.0, 0.0);
               } else {
                  this.level().addAlwaysVisibleParticle(particle, x, y, z, 0.0, 0.0, 0.0);
               }
            } else if (isWaiting) {
               this.level().addAlwaysVisibleParticle(particle, x, y, z, 0.0, 0.0, 0.0);
            } else {
               this.level().addAlwaysVisibleParticle(particle, x, y, z, (0.5 - this.random.nextDouble()) * 0.15, 0.009999999776482582, (0.5 - this.random.nextDouble()) * 0.15);
            }
         }

      }
   }

   private void serverTick(final ServerLevel serverLevel) {
      if (this.duration != -1 && this.tickCount - this.waitTime >= this.duration) {
         this.discard();
      } else {
         boolean isWaiting = this.isWaiting();
         boolean shouldWait = this.tickCount < this.waitTime;
         if (isWaiting != shouldWait) {
            this.setWaiting(shouldWait);
         }

         if (!shouldWait) {
            float radius = this.getRadius();
            if (this.radiusPerTick != 0.0F) {
               radius += this.radiusPerTick;
               if (radius < 0.5F) {
                  this.discard();
                  return;
               }

               this.setRadius(radius);
            }

            if (this.tickCount % 5 == 0) {
               this.victims.entrySet().removeIf((entry) -> this.tickCount >= (Integer)entry.getValue());
               if (!this.potionContents.hasEffects()) {
                  this.victims.clear();
               } else {
                  List<MobEffectInstance> allEffects = new ArrayList();
                  PotionContents var10000 = this.potionContents;
                  Objects.requireNonNull(allEffects);
                  var10000.forEachEffect(allEffects::add, this.potionDurationScale);
                  List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
                  if (!entities.isEmpty()) {
                     for(LivingEntity entity : entities) {
                        if (!this.victims.containsKey(entity) && entity.isAffectedByPotions()) {
                           Stream var17 = allEffects.stream();
                           Objects.requireNonNull(entity);
                           if (!var17.noneMatch(entity::canBeAffected)) {
                              double xd = entity.getX() - this.getX();
                              double zd = entity.getZ() - this.getZ();
                              double dist = xd * xd + zd * zd;
                              if (dist <= (double)(radius * radius)) {
                                 this.victims.put(entity, this.tickCount + this.reapplicationDelay);

                                 for(MobEffectInstance effect : allEffects) {
                                    if (((MobEffect)effect.getEffect().value()).isInstantaneous()) {
                                       ((MobEffect)effect.getEffect().value()).applyInstantaneousEffect(serverLevel, this, this.getOwner(), entity, effect.getAmplifier(), 0.5);
                                    } else {
                                       entity.addEffect(new MobEffectInstance(effect), this);
                                    }
                                 }

                                 if (this.radiusOnUse != 0.0F) {
                                    radius += this.radiusOnUse;
                                    if (radius < 0.5F) {
                                       this.discard();
                                       return;
                                    }

                                    this.setRadius(radius);
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

   public void setRadiusOnUse(final float radiusOnUse) {
      this.radiusOnUse = radiusOnUse;
   }

   public float getRadiusPerTick() {
      return this.radiusPerTick;
   }

   public void setRadiusPerTick(final float radiusPerTick) {
      this.radiusPerTick = radiusPerTick;
   }

   public int getDurationOnUse() {
      return this.durationOnUse;
   }

   public void setDurationOnUse(final int durationOnUse) {
      this.durationOnUse = durationOnUse;
   }

   public int getWaitTime() {
      return this.waitTime;
   }

   public void setWaitTime(final int waitTime) {
      this.waitTime = waitTime;
   }

   public void setOwner(final @Nullable LivingEntity owner) {
      this.owner = EntityReference.of(owner);
   }

   public @Nullable LivingEntity getOwner() {
      return EntityReference.getLivingEntity(this.owner, this.level());
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      this.tickCount = input.getIntOr("Age", 0);
      this.duration = input.getIntOr("Duration", -1);
      this.waitTime = input.getIntOr("WaitTime", 20);
      this.reapplicationDelay = input.getIntOr("ReapplicationDelay", 20);
      this.durationOnUse = input.getIntOr("DurationOnUse", 0);
      this.radiusOnUse = input.getFloatOr("RadiusOnUse", 0.0F);
      this.radiusPerTick = input.getFloatOr("RadiusPerTick", 0.0F);
      this.setRadius(input.getFloatOr("Radius", 3.0F));
      this.owner = EntityReference.<LivingEntity>read(input, "Owner");
      this.setCustomParticle((ParticleOptions)input.read("custom_particle", ParticleTypes.CODEC).orElse((Object)null));
      this.setPotionContents((PotionContents)input.read("potion_contents", PotionContents.CODEC).orElse(PotionContents.EMPTY));
      this.potionDurationScale = input.getFloatOr("potion_duration_scale", 1.0F);
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      output.putInt("Age", this.tickCount);
      output.putInt("Duration", this.duration);
      output.putInt("WaitTime", this.waitTime);
      output.putInt("ReapplicationDelay", this.reapplicationDelay);
      output.putInt("DurationOnUse", this.durationOnUse);
      output.putFloat("RadiusOnUse", this.radiusOnUse);
      output.putFloat("RadiusPerTick", this.radiusPerTick);
      output.putFloat("Radius", this.getRadius());
      output.storeNullable("custom_particle", ParticleTypes.CODEC, this.customParticle);
      EntityReference.store(this.owner, output, "Owner");
      if (!this.potionContents.equals(PotionContents.EMPTY)) {
         output.store("potion_contents", PotionContents.CODEC, this.potionContents);
      }

      if (this.potionDurationScale != 1.0F) {
         output.putFloat("potion_duration_scale", this.potionDurationScale);
      }

   }

   public void onSyncedDataUpdated(final EntityDataAccessor<?> accessor) {
      if (DATA_RADIUS.equals(accessor)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(accessor);
   }

   public PushReaction getPistonPushReaction() {
      return PushReaction.IGNORE_ENTITY;
   }

   public EntityDimensions getDimensions(final Pose pose) {
      return EntityDimensions.scalable(this.getRadius() * 2.0F, 0.5F);
   }

   public final boolean hurtServer(final ServerLevel level, final DamageSource source, final float damage) {
      return false;
   }

   public <T> @Nullable T get(final DataComponentType<? extends T> type) {
      if (type == DataComponents.POTION_CONTENTS) {
         return (T)castComponentValue(type, this.potionContents);
      } else {
         return (T)(type == DataComponents.POTION_DURATION_SCALE ? castComponentValue(type, this.potionDurationScale) : super.get(type));
      }
   }

   protected void applyImplicitComponents(final DataComponentGetter components) {
      this.applyImplicitComponentIfPresent(components, DataComponents.POTION_CONTENTS);
      this.applyImplicitComponentIfPresent(components, DataComponents.POTION_DURATION_SCALE);
      super.applyImplicitComponents(components);
   }

   protected <T> boolean applyImplicitComponent(final DataComponentType<T> type, final T value) {
      if (type == DataComponents.POTION_CONTENTS) {
         this.setPotionContents((PotionContents)castComponentValue(DataComponents.POTION_CONTENTS, value));
         return true;
      } else if (type == DataComponents.POTION_DURATION_SCALE) {
         this.setPotionDurationScale((Float)castComponentValue(DataComponents.POTION_DURATION_SCALE, value));
         return true;
      } else {
         return super.applyImplicitComponent(type, value);
      }
   }

   static {
      DATA_RADIUS = SynchedEntityData.<Float>defineId(AreaEffectCloud.class, EntityDataSerializers.FLOAT);
      DATA_WAITING = SynchedEntityData.<Boolean>defineId(AreaEffectCloud.class, EntityDataSerializers.BOOLEAN);
      DATA_PARTICLE = SynchedEntityData.<ParticleOptions>defineId(AreaEffectCloud.class, EntityDataSerializers.PARTICLE);
      DEFAULT_PARTICLE = ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, -1);
   }
}

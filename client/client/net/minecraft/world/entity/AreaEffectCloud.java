package net.minecraft.world.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import org.slf4j.Logger;

public class AreaEffectCloud extends Entity implements TraceableEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int TIME_BETWEEN_APPLICATIONS = 5;
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> DATA_WAITING = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<ParticleOptions> DATA_PARTICLE = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.PARTICLE);
   private static final float MAX_RADIUS = 32.0F;
   private static final float MINIMAL_RADIUS = 0.5F;
   private static final float DEFAULT_RADIUS = 3.0F;
   public static final float DEFAULT_WIDTH = 6.0F;
   public static final float HEIGHT = 0.5F;
   private PotionContents potionContents = PotionContents.EMPTY;
   private final Map<Entity, Integer> victims = Maps.newHashMap();
   private int duration = 600;
   private int waitTime = 20;
   private int reapplicationDelay = 20;
   private int durationOnUse;
   private float radiusOnUse;
   private float radiusPerTick;
   @Nullable
   private LivingEntity owner;
   @Nullable
   private UUID ownerUUID;

   public AreaEffectCloud(EntityType<? extends AreaEffectCloud> var1, Level var2) {
      super(var1, var2);
      this.noPhysics = true;
   }

   public AreaEffectCloud(Level var1, double var2, double var4, double var6) {
      this(EntityType.AREA_EFFECT_CLOUD, var1);
      this.setPos(var2, var4, var6);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(DATA_RADIUS, 3.0F);
      var1.define(DATA_WAITING, false);
      var1.define(DATA_PARTICLE, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, -1));
   }

   public void setRadius(float var1) {
      if (!this.level().isClientSide) {
         this.getEntityData().set(DATA_RADIUS, Mth.clamp(var1, 0.0F, 32.0F));
      }
   }

   @Override
   public void refreshDimensions() {
      double var1 = this.getX();
      double var3 = this.getY();
      double var5 = this.getZ();
      super.refreshDimensions();
      this.setPos(var1, var3, var5);
   }

   public float getRadius() {
      return this.getEntityData().get(DATA_RADIUS);
   }

   public void setPotionContents(PotionContents var1) {
      this.potionContents = var1;
      this.updateColor();
   }

   private void updateColor() {
      ParticleOptions var1 = this.entityData.get(DATA_PARTICLE);
      if (var1 instanceof ColorParticleOption var2) {
         int var3 = this.potionContents.equals(PotionContents.EMPTY) ? 0 : this.potionContents.getColor();
         this.entityData.set(DATA_PARTICLE, ColorParticleOption.create(var2.getType(), var3));
      }
   }

   public void addEffect(MobEffectInstance var1) {
      this.setPotionContents(this.potionContents.withEffectAdded(var1));
   }

   public ParticleOptions getParticle() {
      return this.getEntityData().get(DATA_PARTICLE);
   }

   public void setParticle(ParticleOptions var1) {
      this.getEntityData().set(DATA_PARTICLE, var1);
   }

   protected void setWaiting(boolean var1) {
      this.getEntityData().set(DATA_WAITING, var1);
   }

   public boolean isWaiting() {
      return this.getEntityData().get(DATA_WAITING);
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(int var1) {
      this.duration = var1;
   }

   @Override
   public void tick() {
      super.tick();
      boolean var1 = this.isWaiting();
      float var2 = this.getRadius();
      if (this.level().isClientSide) {
         if (var1 && this.random.nextBoolean()) {
            return;
         }

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

         for (int var6 = 0; var6 < var4; var6++) {
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
               this.level()
                  .addAlwaysVisibleParticle(
                     var3, var9, var11, var13, (0.5 - this.random.nextDouble()) * 0.15, 0.009999999776482582, (0.5 - this.random.nextDouble()) * 0.15
                  );
            }
         }
      } else {
         if (this.tickCount >= this.waitTime + this.duration) {
            this.discard();
            return;
         }

         boolean var16 = this.tickCount < this.waitTime;
         if (var1 != var16) {
            this.setWaiting(var16);
         }

         if (var16) {
            return;
         }

         if (this.radiusPerTick != 0.0F) {
            var2 += this.radiusPerTick;
            if (var2 < 0.5F) {
               this.discard();
               return;
            }

            this.setRadius(var2);
         }

         if (this.tickCount % 5 == 0) {
            this.victims.entrySet().removeIf(var1x -> this.tickCount >= var1x.getValue());
            if (!this.potionContents.hasEffects()) {
               this.victims.clear();
            } else {
               ArrayList var17 = Lists.newArrayList();
               if (this.potionContents.potion().isPresent()) {
                  for (MobEffectInstance var20 : this.potionContents.potion().get().value().getEffects()) {
                     var17.add(
                        new MobEffectInstance(
                           var20.getEffect(), var20.mapDuration(var0 -> var0 / 4), var20.getAmplifier(), var20.isAmbient(), var20.isVisible()
                        )
                     );
                  }
               }

               var17.addAll(this.potionContents.customEffects());
               List var19 = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
               if (!var19.isEmpty()) {
                  for (LivingEntity var22 : var19) {
                     if (!this.victims.containsKey(var22) && var22.isAffectedByPotions()) {
                        double var23 = var22.getX() - this.getX();
                        double var10 = var22.getZ() - this.getZ();
                        double var12 = var23 * var23 + var10 * var10;
                        if (var12 <= (double)(var2 * var2)) {
                           this.victims.put(var22, this.tickCount + this.reapplicationDelay);

                           for (MobEffectInstance var15 : var17) {
                              if (var15.getEffect().value().isInstantenous()) {
                                 var15.getEffect().value().applyInstantenousEffect(this, this.getOwner(), var22, var15.getAmplifier(), 0.5);
                              } else {
                                 var22.addEffect(new MobEffectInstance(var15), this);
                              }
                           }

                           if (this.radiusOnUse != 0.0F) {
                              var2 += this.radiusOnUse;
                              if (var2 < 0.5F) {
                                 this.discard();
                                 return;
                              }

                              this.setRadius(var2);
                           }

                           if (this.durationOnUse != 0) {
                              this.duration = this.duration + this.durationOnUse;
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
      if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel) {
         Entity var1 = ((ServerLevel)this.level()).getEntity(this.ownerUUID);
         if (var1 instanceof LivingEntity) {
            this.owner = (LivingEntity)var1;
         }
      }

      return this.owner;
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag var1) {
      this.tickCount = var1.getInt("Age");
      this.duration = var1.getInt("Duration");
      this.waitTime = var1.getInt("WaitTime");
      this.reapplicationDelay = var1.getInt("ReapplicationDelay");
      this.durationOnUse = var1.getInt("DurationOnUse");
      this.radiusOnUse = var1.getFloat("RadiusOnUse");
      this.radiusPerTick = var1.getFloat("RadiusPerTick");
      this.setRadius(var1.getFloat("Radius"));
      if (var1.hasUUID("Owner")) {
         this.ownerUUID = var1.getUUID("Owner");
      }

      if (var1.contains("Particle", 8)) {
         try {
            this.setParticle(ParticleArgument.readParticle(new StringReader(var1.getString("Particle")), this.registryAccess()));
         } catch (CommandSyntaxException var3) {
            LOGGER.warn("Couldn't load custom particle {}", var1.getString("Particle"), var3);
         }
      }

      if (var1.contains("potion_contents")) {
         PotionContents.CODEC
            .parse(NbtOps.INSTANCE, var1.get("potion_contents"))
            .resultOrPartial(var0 -> LOGGER.warn("Failed to parse area effect cloud potions: '{}'", var0))
            .ifPresent(this::setPotionContents);
      }
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag var1) {
      var1.putInt("Age", this.tickCount);
      var1.putInt("Duration", this.duration);
      var1.putInt("WaitTime", this.waitTime);
      var1.putInt("ReapplicationDelay", this.reapplicationDelay);
      var1.putInt("DurationOnUse", this.durationOnUse);
      var1.putFloat("RadiusOnUse", this.radiusOnUse);
      var1.putFloat("RadiusPerTick", this.radiusPerTick);
      var1.putFloat("Radius", this.getRadius());
      var1.putString("Particle", this.getParticle().writeToString(this.registryAccess()));
      if (this.ownerUUID != null) {
         var1.putUUID("Owner", this.ownerUUID);
      }

      if (!this.potionContents.equals(PotionContents.EMPTY)) {
         Tag var2 = (Tag)PotionContents.CODEC.encodeStart(NbtOps.INSTANCE, this.potionContents).getOrThrow();
         var1.put("potion_contents", var2);
      }
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_RADIUS.equals(var1)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(var1);
   }

   @Override
   public PushReaction getPistonPushReaction() {
      return PushReaction.IGNORE;
   }

   @Override
   public EntityDimensions getDimensions(Pose var1) {
      return EntityDimensions.scalable(this.getRadius() * 2.0F, 0.5F);
   }
}

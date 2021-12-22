package net.minecraft.world.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AreaEffectCloud extends Entity {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int TIME_BETWEEN_APPLICATIONS = 5;
   private static final EntityDataAccessor<Float> DATA_RADIUS;
   private static final EntityDataAccessor<Integer> DATA_COLOR;
   private static final EntityDataAccessor<Boolean> DATA_WAITING;
   private static final EntityDataAccessor<ParticleOptions> DATA_PARTICLE;
   private static final float MAX_RADIUS = 32.0F;
   private Potion potion;
   private final List<MobEffectInstance> effects;
   private final Map<Entity, Integer> victims;
   private int duration;
   private int waitTime;
   private int reapplicationDelay;
   private boolean fixedColor;
   private int durationOnUse;
   private float radiusOnUse;
   private float radiusPerTick;
   @Nullable
   private LivingEntity owner;
   @Nullable
   private UUID ownerUUID;

   public AreaEffectCloud(EntityType<? extends AreaEffectCloud> var1, Level var2) {
      super(var1, var2);
      this.potion = Potions.EMPTY;
      this.effects = Lists.newArrayList();
      this.victims = Maps.newHashMap();
      this.duration = 600;
      this.waitTime = 20;
      this.reapplicationDelay = 20;
      this.noPhysics = true;
      this.setRadius(3.0F);
   }

   public AreaEffectCloud(Level var1, double var2, double var4, double var6) {
      this(EntityType.AREA_EFFECT_CLOUD, var1);
      this.setPos(var2, var4, var6);
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_COLOR, 0);
      this.getEntityData().define(DATA_RADIUS, 0.5F);
      this.getEntityData().define(DATA_WAITING, false);
      this.getEntityData().define(DATA_PARTICLE, ParticleTypes.ENTITY_EFFECT);
   }

   public void setRadius(float var1) {
      if (!this.level.isClientSide) {
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

   public void setPotion(Potion var1) {
      this.potion = var1;
      if (!this.fixedColor) {
         this.updateColor();
      }

   }

   private void updateColor() {
      if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
         this.getEntityData().set(DATA_COLOR, 0);
      } else {
         this.getEntityData().set(DATA_COLOR, PotionUtils.getColor((Collection)PotionUtils.getAllEffects(this.potion, this.effects)));
      }

   }

   public void addEffect(MobEffectInstance var1) {
      this.effects.add(var1);
      if (!this.fixedColor) {
         this.updateColor();
      }

   }

   public int getColor() {
      return (Integer)this.getEntityData().get(DATA_COLOR);
   }

   public void setFixedColor(int var1) {
      this.fixedColor = true;
      this.getEntityData().set(DATA_COLOR, var1);
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
      boolean var1 = this.isWaiting();
      float var2 = this.getRadius();
      if (this.level.isClientSide) {
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

         for(int var6 = 0; var6 < var4; ++var6) {
            float var7 = this.random.nextFloat() * 6.2831855F;
            float var8 = Mth.sqrt(this.random.nextFloat()) * var5;
            double var9 = this.getX() + (double)(Mth.cos(var7) * var8);
            double var11 = this.getY();
            double var13 = this.getZ() + (double)(Mth.sin(var7) * var8);
            double var15;
            double var17;
            double var19;
            if (var3.getType() != ParticleTypes.ENTITY_EFFECT) {
               if (var1) {
                  var15 = 0.0D;
                  var17 = 0.0D;
                  var19 = 0.0D;
               } else {
                  var15 = (0.5D - this.random.nextDouble()) * 0.15D;
                  var17 = 0.009999999776482582D;
                  var19 = (0.5D - this.random.nextDouble()) * 0.15D;
               }
            } else {
               int var21 = var1 && this.random.nextBoolean() ? 16777215 : this.getColor();
               var15 = (double)((float)(var21 >> 16 & 255) / 255.0F);
               var17 = (double)((float)(var21 >> 8 & 255) / 255.0F);
               var19 = (double)((float)(var21 & 255) / 255.0F);
            }

            this.level.addAlwaysVisibleParticle(var3, var9, var11, var13, var15, var17, var19);
         }
      } else {
         if (this.tickCount >= this.waitTime + this.duration) {
            this.discard();
            return;
         }

         boolean var22 = this.tickCount < this.waitTime;
         if (var1 != var22) {
            this.setWaiting(var22);
         }

         if (var22) {
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
            this.victims.entrySet().removeIf((var1x) -> {
               return this.tickCount >= (Integer)var1x.getValue();
            });
            ArrayList var23 = Lists.newArrayList();
            Iterator var24 = this.potion.getEffects().iterator();

            while(var24.hasNext()) {
               MobEffectInstance var26 = (MobEffectInstance)var24.next();
               var23.add(new MobEffectInstance(var26.getEffect(), var26.getDuration() / 4, var26.getAmplifier(), var26.isAmbient(), var26.isVisible()));
            }

            var23.addAll(this.effects);
            if (var23.isEmpty()) {
               this.victims.clear();
            } else {
               List var25 = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
               if (!var25.isEmpty()) {
                  Iterator var27 = var25.iterator();

                  while(true) {
                     double var12;
                     LivingEntity var28;
                     do {
                        do {
                           do {
                              if (!var27.hasNext()) {
                                 return;
                              }

                              var28 = (LivingEntity)var27.next();
                           } while(this.victims.containsKey(var28));
                        } while(!var28.isAffectedByPotions());

                        double var29 = var28.getX() - this.getX();
                        double var10 = var28.getZ() - this.getZ();
                        var12 = var29 * var29 + var10 * var10;
                     } while(!(var12 <= (double)(var2 * var2)));

                     this.victims.put(var28, this.tickCount + this.reapplicationDelay);
                     Iterator var14 = var23.iterator();

                     while(var14.hasNext()) {
                        MobEffectInstance var30 = (MobEffectInstance)var14.next();
                        if (var30.getEffect().isInstantenous()) {
                           var30.getEffect().applyInstantenousEffect(this, this.getOwner(), var28, var30.getAmplifier(), 0.5D);
                        } else {
                           var28.addEffect(new MobEffectInstance(var30), this);
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
      if (this.owner == null && this.ownerUUID != null && this.level instanceof ServerLevel) {
         Entity var1 = ((ServerLevel)this.level).getEntity(this.ownerUUID);
         if (var1 instanceof LivingEntity) {
            this.owner = (LivingEntity)var1;
         }
      }

      return this.owner;
   }

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
            this.setParticle(ParticleArgument.readParticle(new StringReader(var1.getString("Particle"))));
         } catch (CommandSyntaxException var5) {
            LOGGER.warn("Couldn't load custom particle {}", var1.getString("Particle"), var5);
         }
      }

      if (var1.contains("Color", 99)) {
         this.setFixedColor(var1.getInt("Color"));
      }

      if (var1.contains("Potion", 8)) {
         this.setPotion(PotionUtils.getPotion(var1));
      }

      if (var1.contains("Effects", 9)) {
         ListTag var2 = var1.getList("Effects", 10);
         this.effects.clear();

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            MobEffectInstance var4 = MobEffectInstance.load(var2.getCompound(var3));
            if (var4 != null) {
               this.addEffect(var4);
            }
         }
      }

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
      var1.putString("Particle", this.getParticle().writeToString());
      if (this.ownerUUID != null) {
         var1.putUUID("Owner", this.ownerUUID);
      }

      if (this.fixedColor) {
         var1.putInt("Color", this.getColor());
      }

      if (this.potion != Potions.EMPTY) {
         var1.putString("Potion", Registry.POTION.getKey(this.potion).toString());
      }

      if (!this.effects.isEmpty()) {
         ListTag var2 = new ListTag();
         Iterator var3 = this.effects.iterator();

         while(var3.hasNext()) {
            MobEffectInstance var4 = (MobEffectInstance)var3.next();
            var2.add(var4.save(new CompoundTag()));
         }

         var1.put("Effects", var2);
      }

   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_RADIUS.equals(var1)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(var1);
   }

   public Potion getPotion() {
      return this.potion;
   }

   public PushReaction getPistonPushReaction() {
      return PushReaction.IGNORE;
   }

   public Packet<?> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }

   public EntityDimensions getDimensions(Pose var1) {
      return EntityDimensions.scalable(this.getRadius() * 2.0F, 0.5F);
   }

   static {
      DATA_RADIUS = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.FLOAT);
      DATA_COLOR = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.INT);
      DATA_WAITING = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.BOOLEAN);
      DATA_PARTICLE = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.PARTICLE);
   }
}

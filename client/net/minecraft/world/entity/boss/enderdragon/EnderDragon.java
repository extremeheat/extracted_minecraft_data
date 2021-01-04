package net.minecraft.world.entity.boss.enderdragon;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.dimension.end.TheEndDimension;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BinaryHeap;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnderDragon extends Mob implements Enemy {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final EntityDataAccessor<Integer> DATA_PHASE;
   private static final TargetingConditions CRYSTAL_DESTROY_TARGETING;
   public final double[][] positions = new double[64][3];
   public int posPointer = -1;
   public final EnderDragonPart[] subEntities;
   public final EnderDragonPart head = new EnderDragonPart(this, "head", 1.0F, 1.0F);
   public final EnderDragonPart neck = new EnderDragonPart(this, "neck", 3.0F, 3.0F);
   public final EnderDragonPart body = new EnderDragonPart(this, "body", 5.0F, 3.0F);
   public final EnderDragonPart tail1 = new EnderDragonPart(this, "tail", 2.0F, 2.0F);
   public final EnderDragonPart tail2 = new EnderDragonPart(this, "tail", 2.0F, 2.0F);
   public final EnderDragonPart tail3 = new EnderDragonPart(this, "tail", 2.0F, 2.0F);
   public final EnderDragonPart wing1 = new EnderDragonPart(this, "wing", 4.0F, 2.0F);
   public final EnderDragonPart wing2 = new EnderDragonPart(this, "wing", 4.0F, 2.0F);
   public float oFlapTime;
   public float flapTime;
   public boolean inWall;
   public int dragonDeathTime;
   public EndCrystal nearestCrystal;
   private final EndDragonFight dragonFight;
   private final EnderDragonPhaseManager phaseManager;
   private int growlTime = 100;
   private int sittingDamageReceived;
   private final Node[] nodes = new Node[24];
   private final int[] nodeAdjacency = new int[24];
   private final BinaryHeap openSet = new BinaryHeap();

   public EnderDragon(EntityType<? extends EnderDragon> var1, Level var2) {
      super(EntityType.ENDER_DRAGON, var2);
      this.subEntities = new EnderDragonPart[]{this.head, this.neck, this.body, this.tail1, this.tail2, this.tail3, this.wing1, this.wing2};
      this.setHealth(this.getMaxHealth());
      this.noPhysics = true;
      this.noCulling = true;
      if (!var2.isClientSide && var2.dimension instanceof TheEndDimension) {
         this.dragonFight = ((TheEndDimension)var2.dimension).getDragonFight();
      } else {
         this.dragonFight = null;
      }

      this.phaseManager = new EnderDragonPhaseManager(this);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(200.0D);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.getEntityData().define(DATA_PHASE, EnderDragonPhase.HOVERING.getId());
   }

   public double[] getLatencyPos(int var1, float var2) {
      if (this.getHealth() <= 0.0F) {
         var2 = 0.0F;
      }

      var2 = 1.0F - var2;
      int var3 = this.posPointer - var1 & 63;
      int var4 = this.posPointer - var1 - 1 & 63;
      double[] var5 = new double[3];
      double var6 = this.positions[var3][0];
      double var8 = Mth.wrapDegrees(this.positions[var4][0] - var6);
      var5[0] = var6 + var8 * (double)var2;
      var6 = this.positions[var3][1];
      var8 = this.positions[var4][1] - var6;
      var5[1] = var6 + var8 * (double)var2;
      var5[2] = Mth.lerp((double)var2, this.positions[var3][2], this.positions[var4][2]);
      return var5;
   }

   public void aiStep() {
      float var1;
      float var2;
      if (this.level.isClientSide) {
         this.setHealth(this.getHealth());
         if (!this.isSilent()) {
            var1 = Mth.cos(this.flapTime * 6.2831855F);
            var2 = Mth.cos(this.oFlapTime * 6.2831855F);
            if (var2 <= -0.3F && var1 >= -0.3F) {
               this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.ENDER_DRAGON_FLAP, this.getSoundSource(), 5.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
            }

            if (!this.phaseManager.getCurrentPhase().isSitting() && --this.growlTime < 0) {
               this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.ENDER_DRAGON_GROWL, this.getSoundSource(), 2.5F, 0.8F + this.random.nextFloat() * 0.3F, false);
               this.growlTime = 200 + this.random.nextInt(200);
            }
         }
      }

      this.oFlapTime = this.flapTime;
      if (this.getHealth() <= 0.0F) {
         var1 = (this.random.nextFloat() - 0.5F) * 8.0F;
         var2 = (this.random.nextFloat() - 0.5F) * 4.0F;
         float var32 = (this.random.nextFloat() - 0.5F) * 8.0F;
         this.level.addParticle(ParticleTypes.EXPLOSION, this.x + (double)var1, this.y + 2.0D + (double)var2, this.z + (double)var32, 0.0D, 0.0D, 0.0D);
      } else {
         this.checkCrystals();
         Vec3 var26 = this.getDeltaMovement();
         var2 = 0.2F / (Mth.sqrt(getHorizontalDistanceSqr(var26)) * 10.0F + 1.0F);
         var2 *= (float)Math.pow(2.0D, var26.y);
         if (this.phaseManager.getCurrentPhase().isSitting()) {
            this.flapTime += 0.1F;
         } else if (this.inWall) {
            this.flapTime += var2 * 0.5F;
         } else {
            this.flapTime += var2;
         }

         this.yRot = Mth.wrapDegrees(this.yRot);
         if (this.isNoAi()) {
            this.flapTime = 0.5F;
         } else {
            if (this.posPointer < 0) {
               for(int var3 = 0; var3 < this.positions.length; ++var3) {
                  this.positions[var3][0] = (double)this.yRot;
                  this.positions[var3][1] = this.y;
               }
            }

            if (++this.posPointer == this.positions.length) {
               this.posPointer = 0;
            }

            this.positions[this.posPointer][0] = (double)this.yRot;
            this.positions[this.posPointer][1] = this.y;
            double var5;
            double var7;
            double var9;
            float var13;
            if (this.level.isClientSide) {
               if (this.lerpSteps > 0) {
                  double var27 = this.x + (this.lerpX - this.x) / (double)this.lerpSteps;
                  var5 = this.y + (this.lerpY - this.y) / (double)this.lerpSteps;
                  var7 = this.z + (this.lerpZ - this.z) / (double)this.lerpSteps;
                  var9 = Mth.wrapDegrees(this.lerpYRot - (double)this.yRot);
                  this.yRot = (float)((double)this.yRot + var9 / (double)this.lerpSteps);
                  this.xRot = (float)((double)this.xRot + (this.lerpXRot - (double)this.xRot) / (double)this.lerpSteps);
                  --this.lerpSteps;
                  this.setPos(var27, var5, var7);
                  this.setRot(this.yRot, this.xRot);
               }

               this.phaseManager.getCurrentPhase().doClientTick();
            } else {
               DragonPhaseInstance var28 = this.phaseManager.getCurrentPhase();
               var28.doServerTick();
               if (this.phaseManager.getCurrentPhase() != var28) {
                  var28 = this.phaseManager.getCurrentPhase();
                  var28.doServerTick();
               }

               Vec3 var4 = var28.getFlyTargetLocation();
               if (var4 != null) {
                  var5 = var4.x - this.x;
                  var7 = var4.y - this.y;
                  var9 = var4.z - this.z;
                  double var11 = var5 * var5 + var7 * var7 + var9 * var9;
                  var13 = var28.getFlySpeed();
                  double var14 = (double)Mth.sqrt(var5 * var5 + var9 * var9);
                  if (var14 > 0.0D) {
                     var7 = Mth.clamp(var7 / var14, (double)(-var13), (double)var13);
                  }

                  this.setDeltaMovement(this.getDeltaMovement().add(0.0D, var7 * 0.01D, 0.0D));
                  this.yRot = Mth.wrapDegrees(this.yRot);
                  double var16 = Mth.clamp(Mth.wrapDegrees(180.0D - Mth.atan2(var5, var9) * 57.2957763671875D - (double)this.yRot), -50.0D, 50.0D);
                  Vec3 var18 = var4.subtract(this.x, this.y, this.z).normalize();
                  Vec3 var19 = (new Vec3((double)Mth.sin(this.yRot * 0.017453292F), this.getDeltaMovement().y, (double)(-Mth.cos(this.yRot * 0.017453292F)))).normalize();
                  float var20 = Math.max(((float)var19.dot(var18) + 0.5F) / 1.5F, 0.0F);
                  this.yRotA *= 0.8F;
                  this.yRotA = (float)((double)this.yRotA + var16 * (double)var28.getTurnSpeed());
                  this.yRot += this.yRotA * 0.1F;
                  float var21 = (float)(2.0D / (var11 + 1.0D));
                  float var22 = 0.06F;
                  this.moveRelative(0.06F * (var20 * var21 + (1.0F - var21)), new Vec3(0.0D, 0.0D, -1.0D));
                  if (this.inWall) {
                     this.move(MoverType.SELF, this.getDeltaMovement().scale(0.800000011920929D));
                  } else {
                     this.move(MoverType.SELF, this.getDeltaMovement());
                  }

                  Vec3 var23 = this.getDeltaMovement().normalize();
                  double var24 = 0.8D + 0.15D * (var23.dot(var19) + 1.0D) / 2.0D;
                  this.setDeltaMovement(this.getDeltaMovement().multiply(var24, 0.9100000262260437D, var24));
               }
            }

            this.yBodyRot = this.yRot;
            Vec3[] var30 = new Vec3[this.subEntities.length];

            for(int var29 = 0; var29 < this.subEntities.length; ++var29) {
               var30[var29] = new Vec3(this.subEntities[var29].x, this.subEntities[var29].y, this.subEntities[var29].z);
            }

            float var31 = (float)(this.getLatencyPos(5, 1.0F)[1] - this.getLatencyPos(10, 1.0F)[1]) * 10.0F * 0.017453292F;
            float var33 = Mth.cos(var31);
            float var6 = Mth.sin(var31);
            float var34 = this.yRot * 0.017453292F;
            float var8 = Mth.sin(var34);
            float var35 = Mth.cos(var34);
            this.body.tick();
            this.body.moveTo(this.x + (double)(var8 * 0.5F), this.y, this.z - (double)(var35 * 0.5F), 0.0F, 0.0F);
            this.wing1.tick();
            this.wing1.moveTo(this.x + (double)(var35 * 4.5F), this.y + 2.0D, this.z + (double)(var8 * 4.5F), 0.0F, 0.0F);
            this.wing2.tick();
            this.wing2.moveTo(this.x - (double)(var35 * 4.5F), this.y + 2.0D, this.z - (double)(var8 * 4.5F), 0.0F, 0.0F);
            if (!this.level.isClientSide && this.hurtTime == 0) {
               this.knockBack(this.level.getEntities((Entity)this, this.wing1.getBoundingBox().inflate(4.0D, 2.0D, 4.0D).move(0.0D, -2.0D, 0.0D), EntitySelector.NO_CREATIVE_OR_SPECTATOR));
               this.knockBack(this.level.getEntities((Entity)this, this.wing2.getBoundingBox().inflate(4.0D, 2.0D, 4.0D).move(0.0D, -2.0D, 0.0D), EntitySelector.NO_CREATIVE_OR_SPECTATOR));
               this.hurt(this.level.getEntities((Entity)this, this.head.getBoundingBox().inflate(1.0D), EntitySelector.NO_CREATIVE_OR_SPECTATOR));
               this.hurt(this.level.getEntities((Entity)this, this.neck.getBoundingBox().inflate(1.0D), EntitySelector.NO_CREATIVE_OR_SPECTATOR));
            }

            double[] var10 = this.getLatencyPos(5, 1.0F);
            float var36 = Mth.sin(this.yRot * 0.017453292F - this.yRotA * 0.01F);
            float var12 = Mth.cos(this.yRot * 0.017453292F - this.yRotA * 0.01F);
            this.head.tick();
            this.neck.tick();
            var13 = this.getHeadYOffset(1.0F);
            this.head.moveTo(this.x + (double)(var36 * 6.5F * var33), this.y + (double)var13 + (double)(var6 * 6.5F), this.z - (double)(var12 * 6.5F * var33), 0.0F, 0.0F);
            this.neck.moveTo(this.x + (double)(var36 * 5.5F * var33), this.y + (double)var13 + (double)(var6 * 5.5F), this.z - (double)(var12 * 5.5F * var33), 0.0F, 0.0F);

            int var37;
            for(var37 = 0; var37 < 3; ++var37) {
               EnderDragonPart var38 = null;
               if (var37 == 0) {
                  var38 = this.tail1;
               }

               if (var37 == 1) {
                  var38 = this.tail2;
               }

               if (var37 == 2) {
                  var38 = this.tail3;
               }

               double[] var39 = this.getLatencyPos(12 + var37 * 2, 1.0F);
               float var40 = this.yRot * 0.017453292F + this.rotWrap(var39[0] - var10[0]) * 0.017453292F;
               float var15 = Mth.sin(var40);
               float var41 = Mth.cos(var40);
               float var17 = 1.5F;
               float var42 = (float)(var37 + 1) * 2.0F;
               var38.tick();
               var38.moveTo(this.x - (double)((var8 * 1.5F + var15 * var42) * var33), this.y + (var39[1] - var10[1]) - (double)((var42 + 1.5F) * var6) + 1.5D, this.z + (double)((var35 * 1.5F + var41 * var42) * var33), 0.0F, 0.0F);
            }

            if (!this.level.isClientSide) {
               this.inWall = this.checkWalls(this.head.getBoundingBox()) | this.checkWalls(this.neck.getBoundingBox()) | this.checkWalls(this.body.getBoundingBox());
               if (this.dragonFight != null) {
                  this.dragonFight.updateDragon(this);
               }
            }

            for(var37 = 0; var37 < this.subEntities.length; ++var37) {
               this.subEntities[var37].xo = var30[var37].x;
               this.subEntities[var37].yo = var30[var37].y;
               this.subEntities[var37].zo = var30[var37].z;
            }

         }
      }
   }

   private float getHeadYOffset(float var1) {
      double var2;
      if (this.phaseManager.getCurrentPhase().isSitting()) {
         var2 = -1.0D;
      } else {
         double[] var4 = this.getLatencyPos(5, 1.0F);
         double[] var5 = this.getLatencyPos(0, 1.0F);
         var2 = var4[1] - var5[1];
      }

      return (float)var2;
   }

   private void checkCrystals() {
      if (this.nearestCrystal != null) {
         if (this.nearestCrystal.removed) {
            this.nearestCrystal = null;
         } else if (this.tickCount % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.setHealth(this.getHealth() + 1.0F);
         }
      }

      if (this.random.nextInt(10) == 0) {
         List var1 = this.level.getEntitiesOfClass(EndCrystal.class, this.getBoundingBox().inflate(32.0D));
         EndCrystal var2 = null;
         double var3 = 1.7976931348623157E308D;
         Iterator var5 = var1.iterator();

         while(var5.hasNext()) {
            EndCrystal var6 = (EndCrystal)var5.next();
            double var7 = var6.distanceToSqr(this);
            if (var7 < var3) {
               var3 = var7;
               var2 = var6;
            }
         }

         this.nearestCrystal = var2;
      }

   }

   private void knockBack(List<Entity> var1) {
      double var2 = (this.body.getBoundingBox().minX + this.body.getBoundingBox().maxX) / 2.0D;
      double var4 = (this.body.getBoundingBox().minZ + this.body.getBoundingBox().maxZ) / 2.0D;
      Iterator var6 = var1.iterator();

      while(var6.hasNext()) {
         Entity var7 = (Entity)var6.next();
         if (var7 instanceof LivingEntity) {
            double var8 = var7.x - var2;
            double var10 = var7.z - var4;
            double var12 = var8 * var8 + var10 * var10;
            var7.push(var8 / var12 * 4.0D, 0.20000000298023224D, var10 / var12 * 4.0D);
            if (!this.phaseManager.getCurrentPhase().isSitting() && ((LivingEntity)var7).getLastHurtByMobTimestamp() < var7.tickCount - 2) {
               var7.hurt(DamageSource.mobAttack(this), 5.0F);
               this.doEnchantDamageEffects(this, var7);
            }
         }
      }

   }

   private void hurt(List<Entity> var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         Entity var3 = (Entity)var1.get(var2);
         if (var3 instanceof LivingEntity) {
            var3.hurt(DamageSource.mobAttack(this), 10.0F);
            this.doEnchantDamageEffects(this, var3);
         }
      }

   }

   private float rotWrap(double var1) {
      return (float)Mth.wrapDegrees(var1);
   }

   private boolean checkWalls(AABB var1) {
      int var2 = Mth.floor(var1.minX);
      int var3 = Mth.floor(var1.minY);
      int var4 = Mth.floor(var1.minZ);
      int var5 = Mth.floor(var1.maxX);
      int var6 = Mth.floor(var1.maxY);
      int var7 = Mth.floor(var1.maxZ);
      boolean var8 = false;
      boolean var9 = false;

      for(int var10 = var2; var10 <= var5; ++var10) {
         for(int var11 = var3; var11 <= var6; ++var11) {
            for(int var12 = var4; var12 <= var7; ++var12) {
               BlockPos var13 = new BlockPos(var10, var11, var12);
               BlockState var14 = this.level.getBlockState(var13);
               Block var15 = var14.getBlock();
               if (!var14.isAir() && var14.getMaterial() != Material.FIRE) {
                  if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && !BlockTags.DRAGON_IMMUNE.contains(var15)) {
                     var9 = this.level.removeBlock(var13, false) || var9;
                  } else {
                     var8 = true;
                  }
               }
            }
         }
      }

      if (var9) {
         BlockPos var16 = new BlockPos(var2 + this.random.nextInt(var5 - var2 + 1), var3 + this.random.nextInt(var6 - var3 + 1), var4 + this.random.nextInt(var7 - var4 + 1));
         this.level.levelEvent(2008, var16, 0);
      }

      return var8;
   }

   public boolean hurt(EnderDragonPart var1, DamageSource var2, float var3) {
      var3 = this.phaseManager.getCurrentPhase().onHurt(var2, var3);
      if (var1 != this.head) {
         var3 = var3 / 4.0F + Math.min(var3, 1.0F);
      }

      if (var3 < 0.01F) {
         return false;
      } else {
         if (var2.getEntity() instanceof Player || var2.isExplosion()) {
            float var4 = this.getHealth();
            this.reallyHurt(var2, var3);
            if (this.getHealth() <= 0.0F && !this.phaseManager.getCurrentPhase().isSitting()) {
               this.setHealth(1.0F);
               this.phaseManager.setPhase(EnderDragonPhase.DYING);
            }

            if (this.phaseManager.getCurrentPhase().isSitting()) {
               this.sittingDamageReceived = (int)((float)this.sittingDamageReceived + (var4 - this.getHealth()));
               if ((float)this.sittingDamageReceived > 0.25F * this.getMaxHealth()) {
                  this.sittingDamageReceived = 0;
                  this.phaseManager.setPhase(EnderDragonPhase.TAKEOFF);
               }
            }
         }

         return true;
      }
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (var1 instanceof EntityDamageSource && ((EntityDamageSource)var1).isThorns()) {
         this.hurt(this.body, var1, var2);
      }

      return false;
   }

   protected boolean reallyHurt(DamageSource var1, float var2) {
      return super.hurt(var1, var2);
   }

   public void kill() {
      this.remove();
      if (this.dragonFight != null) {
         this.dragonFight.updateDragon(this);
         this.dragonFight.setDragonKilled(this);
      }

   }

   protected void tickDeath() {
      if (this.dragonFight != null) {
         this.dragonFight.updateDragon(this);
      }

      ++this.dragonDeathTime;
      if (this.dragonDeathTime >= 180 && this.dragonDeathTime <= 200) {
         float var1 = (this.random.nextFloat() - 0.5F) * 8.0F;
         float var2 = (this.random.nextFloat() - 0.5F) * 4.0F;
         float var3 = (this.random.nextFloat() - 0.5F) * 8.0F;
         this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x + (double)var1, this.y + 2.0D + (double)var2, this.z + (double)var3, 0.0D, 0.0D, 0.0D);
      }

      boolean var4 = this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
      short var5 = 500;
      if (this.dragonFight != null && !this.dragonFight.hasPreviouslyKilledDragon()) {
         var5 = 12000;
      }

      if (!this.level.isClientSide) {
         if (this.dragonDeathTime > 150 && this.dragonDeathTime % 5 == 0 && var4) {
            this.dropExperience(Mth.floor((float)var5 * 0.08F));
         }

         if (this.dragonDeathTime == 1) {
            this.level.globalLevelEvent(1028, new BlockPos(this), 0);
         }
      }

      this.move(MoverType.SELF, new Vec3(0.0D, 0.10000000149011612D, 0.0D));
      this.yRot += 20.0F;
      this.yBodyRot = this.yRot;
      if (this.dragonDeathTime == 200 && !this.level.isClientSide) {
         if (var4) {
            this.dropExperience(Mth.floor((float)var5 * 0.2F));
         }

         if (this.dragonFight != null) {
            this.dragonFight.setDragonKilled(this);
         }

         this.remove();
      }

   }

   private void dropExperience(int var1) {
      while(var1 > 0) {
         int var2 = ExperienceOrb.getExperienceValue(var1);
         var1 -= var2;
         this.level.addFreshEntity(new ExperienceOrb(this.level, this.x, this.y, this.z, var2));
      }

   }

   public int findClosestNode() {
      if (this.nodes[0] == null) {
         for(int var1 = 0; var1 < 24; ++var1) {
            int var2 = 5;
            int var4;
            int var5;
            if (var1 < 12) {
               var4 = Mth.floor(60.0F * Mth.cos(2.0F * (-3.1415927F + 0.2617994F * (float)var1)));
               var5 = Mth.floor(60.0F * Mth.sin(2.0F * (-3.1415927F + 0.2617994F * (float)var1)));
            } else {
               int var3;
               if (var1 < 20) {
                  var3 = var1 - 12;
                  var4 = Mth.floor(40.0F * Mth.cos(2.0F * (-3.1415927F + 0.3926991F * (float)var3)));
                  var5 = Mth.floor(40.0F * Mth.sin(2.0F * (-3.1415927F + 0.3926991F * (float)var3)));
                  var2 += 10;
               } else {
                  var3 = var1 - 20;
                  var4 = Mth.floor(20.0F * Mth.cos(2.0F * (-3.1415927F + 0.7853982F * (float)var3)));
                  var5 = Mth.floor(20.0F * Mth.sin(2.0F * (-3.1415927F + 0.7853982F * (float)var3)));
               }
            }

            int var6 = Math.max(this.level.getSeaLevel() + 10, this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(var4, 0, var5)).getY() + var2);
            this.nodes[var1] = new Node(var4, var6, var5);
         }

         this.nodeAdjacency[0] = 6146;
         this.nodeAdjacency[1] = 8197;
         this.nodeAdjacency[2] = 8202;
         this.nodeAdjacency[3] = 16404;
         this.nodeAdjacency[4] = 32808;
         this.nodeAdjacency[5] = 32848;
         this.nodeAdjacency[6] = 65696;
         this.nodeAdjacency[7] = 131392;
         this.nodeAdjacency[8] = 131712;
         this.nodeAdjacency[9] = 263424;
         this.nodeAdjacency[10] = 526848;
         this.nodeAdjacency[11] = 525313;
         this.nodeAdjacency[12] = 1581057;
         this.nodeAdjacency[13] = 3166214;
         this.nodeAdjacency[14] = 2138120;
         this.nodeAdjacency[15] = 6373424;
         this.nodeAdjacency[16] = 4358208;
         this.nodeAdjacency[17] = 12910976;
         this.nodeAdjacency[18] = 9044480;
         this.nodeAdjacency[19] = 9706496;
         this.nodeAdjacency[20] = 15216640;
         this.nodeAdjacency[21] = 13688832;
         this.nodeAdjacency[22] = 11763712;
         this.nodeAdjacency[23] = 8257536;
      }

      return this.findClosestNode(this.x, this.y, this.z);
   }

   public int findClosestNode(double var1, double var3, double var5) {
      float var7 = 10000.0F;
      int var8 = 0;
      Node var9 = new Node(Mth.floor(var1), Mth.floor(var3), Mth.floor(var5));
      byte var10 = 0;
      if (this.dragonFight == null || this.dragonFight.getCrystalsAlive() == 0) {
         var10 = 12;
      }

      for(int var11 = var10; var11 < 24; ++var11) {
         if (this.nodes[var11] != null) {
            float var12 = this.nodes[var11].distanceToSqr(var9);
            if (var12 < var7) {
               var7 = var12;
               var8 = var11;
            }
         }
      }

      return var8;
   }

   @Nullable
   public Path findPath(int var1, int var2, @Nullable Node var3) {
      Node var5;
      for(int var4 = 0; var4 < 24; ++var4) {
         var5 = this.nodes[var4];
         var5.closed = false;
         var5.f = 0.0F;
         var5.g = 0.0F;
         var5.h = 0.0F;
         var5.cameFrom = null;
         var5.heapIdx = -1;
      }

      Node var13 = this.nodes[var1];
      var5 = this.nodes[var2];
      var13.g = 0.0F;
      var13.h = var13.distanceTo(var5);
      var13.f = var13.h;
      this.openSet.clear();
      this.openSet.insert(var13);
      Node var6 = var13;
      byte var7 = 0;
      if (this.dragonFight == null || this.dragonFight.getCrystalsAlive() == 0) {
         var7 = 12;
      }

      while(!this.openSet.isEmpty()) {
         Node var8 = this.openSet.pop();
         if (var8.equals(var5)) {
            if (var3 != null) {
               var3.cameFrom = var5;
               var5 = var3;
            }

            return this.reconstructPath(var13, var5);
         }

         if (var8.distanceTo(var5) < var6.distanceTo(var5)) {
            var6 = var8;
         }

         var8.closed = true;
         int var9 = 0;

         int var10;
         for(var10 = 0; var10 < 24; ++var10) {
            if (this.nodes[var10] == var8) {
               var9 = var10;
               break;
            }
         }

         for(var10 = var7; var10 < 24; ++var10) {
            if ((this.nodeAdjacency[var9] & 1 << var10) > 0) {
               Node var11 = this.nodes[var10];
               if (!var11.closed) {
                  float var12 = var8.g + var8.distanceTo(var11);
                  if (!var11.inOpenSet() || var12 < var11.g) {
                     var11.cameFrom = var8;
                     var11.g = var12;
                     var11.h = var11.distanceTo(var5);
                     if (var11.inOpenSet()) {
                        this.openSet.changeCost(var11, var11.g + var11.h);
                     } else {
                        var11.f = var11.g + var11.h;
                        this.openSet.insert(var11);
                     }
                  }
               }
            }
         }
      }

      if (var6 == var13) {
         return null;
      } else {
         LOGGER.debug("Failed to find path from {} to {}", var1, var2);
         if (var3 != null) {
            var3.cameFrom = var6;
            var6 = var3;
         }

         return this.reconstructPath(var13, var6);
      }
   }

   private Path reconstructPath(Node var1, Node var2) {
      ArrayList var3 = Lists.newArrayList();
      Node var4 = var2;
      var3.add(0, var2);

      while(var4.cameFrom != null) {
         var4 = var4.cameFrom;
         var3.add(0, var4);
      }

      return new Path(var3, new BlockPos(var2.x, var2.y, var2.z), true);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("DragonPhase", this.phaseManager.getCurrentPhase().getPhase().getId());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("DragonPhase")) {
         this.phaseManager.setPhase(EnderDragonPhase.getById(var1.getInt("DragonPhase")));
      }

   }

   protected void checkDespawn() {
   }

   public EnderDragonPart[] getSubEntities() {
      return this.subEntities;
   }

   public boolean isPickable() {
      return false;
   }

   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENDER_DRAGON_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.ENDER_DRAGON_HURT;
   }

   protected float getSoundVolume() {
      return 5.0F;
   }

   public float getHeadPartYOffset(int var1, double[] var2, double[] var3) {
      DragonPhaseInstance var4 = this.phaseManager.getCurrentPhase();
      EnderDragonPhase var5 = var4.getPhase();
      double var6;
      if (var5 != EnderDragonPhase.LANDING && var5 != EnderDragonPhase.TAKEOFF) {
         if (var4.isSitting()) {
            var6 = (double)var1;
         } else if (var1 == 6) {
            var6 = 0.0D;
         } else {
            var6 = var3[1] - var2[1];
         }
      } else {
         BlockPos var8 = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
         float var9 = Math.max(Mth.sqrt(var8.distSqr(this.position(), true)) / 4.0F, 1.0F);
         var6 = (double)((float)var1 / var9);
      }

      return (float)var6;
   }

   public Vec3 getHeadLookVector(float var1) {
      DragonPhaseInstance var2 = this.phaseManager.getCurrentPhase();
      EnderDragonPhase var3 = var2.getPhase();
      Vec3 var4;
      float var6;
      if (var3 != EnderDragonPhase.LANDING && var3 != EnderDragonPhase.TAKEOFF) {
         if (var2.isSitting()) {
            float var10 = this.xRot;
            var6 = 1.5F;
            this.xRot = -45.0F;
            var4 = this.getViewVector(var1);
            this.xRot = var10;
         } else {
            var4 = this.getViewVector(var1);
         }
      } else {
         BlockPos var5 = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
         var6 = Math.max(Mth.sqrt(var5.distSqr(this.position(), true)) / 4.0F, 1.0F);
         float var7 = 6.0F / var6;
         float var8 = this.xRot;
         float var9 = 1.5F;
         this.xRot = -var7 * 1.5F * 5.0F;
         var4 = this.getViewVector(var1);
         this.xRot = var8;
      }

      return var4;
   }

   public void onCrystalDestroyed(EndCrystal var1, BlockPos var2, DamageSource var3) {
      Player var4;
      if (var3.getEntity() instanceof Player) {
         var4 = (Player)var3.getEntity();
      } else {
         var4 = this.level.getNearestPlayer(CRYSTAL_DESTROY_TARGETING, (double)var2.getX(), (double)var2.getY(), (double)var2.getZ());
      }

      if (var1 == this.nearestCrystal) {
         this.hurt(this.head, DamageSource.explosion((LivingEntity)var4), 10.0F);
      }

      this.phaseManager.getCurrentPhase().onCrystalDestroyed(var1, var2, var3, var4);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_PHASE.equals(var1) && this.level.isClientSide) {
         this.phaseManager.setPhase(EnderDragonPhase.getById((Integer)this.getEntityData().get(DATA_PHASE)));
      }

      super.onSyncedDataUpdated(var1);
   }

   public EnderDragonPhaseManager getPhaseManager() {
      return this.phaseManager;
   }

   @Nullable
   public EndDragonFight getDragonFight() {
      return this.dragonFight;
   }

   public boolean addEffect(MobEffectInstance var1) {
      return false;
   }

   protected boolean canRide(Entity var1) {
      return false;
   }

   public boolean canChangeDimensions() {
      return false;
   }

   static {
      DATA_PHASE = SynchedEntityData.defineId(EnderDragon.class, EntityDataSerializers.INT);
      CRYSTAL_DESTROY_TARGETING = (new TargetingConditions()).range(64.0D);
   }
}

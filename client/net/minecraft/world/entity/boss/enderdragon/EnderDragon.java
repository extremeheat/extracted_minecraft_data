package net.minecraft.world.entity.boss.enderdragon;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.pathfinder.BinaryHeap;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class EnderDragon extends Mob implements Enemy {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final EntityDataAccessor<Integer> DATA_PHASE;
   private static final TargetingConditions CRYSTAL_DESTROY_TARGETING;
   private static final int GROWL_INTERVAL_MIN = 200;
   private static final int GROWL_INTERVAL_MAX = 400;
   private static final float SITTING_ALLOWED_DAMAGE_PERCENTAGE = 0.25F;
   private static final String DRAGON_DEATH_TIME_KEY = "DragonDeathTime";
   private static final String DRAGON_PHASE_KEY = "DragonPhase";
   public final double[][] positions = new double[64][3];
   public int posPointer = -1;
   private final EnderDragonPart[] subEntities;
   public final EnderDragonPart head;
   private final EnderDragonPart neck;
   private final EnderDragonPart body;
   private final EnderDragonPart tail1;
   private final EnderDragonPart tail2;
   private final EnderDragonPart tail3;
   private final EnderDragonPart wing1;
   private final EnderDragonPart wing2;
   public float oFlapTime;
   public float flapTime;
   public boolean inWall;
   public int dragonDeathTime;
   public float yRotA;
   @Nullable
   public EndCrystal nearestCrystal;
   @Nullable
   private EndDragonFight dragonFight;
   private BlockPos fightOrigin;
   private final EnderDragonPhaseManager phaseManager;
   private int growlTime;
   private float sittingDamageReceived;
   private final Node[] nodes;
   private final int[] nodeAdjacency;
   private final BinaryHeap openSet;

   public EnderDragon(EntityType<? extends EnderDragon> var1, Level var2) {
      super(EntityType.ENDER_DRAGON, var2);
      this.fightOrigin = BlockPos.ZERO;
      this.growlTime = 100;
      this.nodes = new Node[24];
      this.nodeAdjacency = new int[24];
      this.openSet = new BinaryHeap();
      this.head = new EnderDragonPart(this, "head", 1.0F, 1.0F);
      this.neck = new EnderDragonPart(this, "neck", 3.0F, 3.0F);
      this.body = new EnderDragonPart(this, "body", 5.0F, 3.0F);
      this.tail1 = new EnderDragonPart(this, "tail", 2.0F, 2.0F);
      this.tail2 = new EnderDragonPart(this, "tail", 2.0F, 2.0F);
      this.tail3 = new EnderDragonPart(this, "tail", 2.0F, 2.0F);
      this.wing1 = new EnderDragonPart(this, "wing", 4.0F, 2.0F);
      this.wing2 = new EnderDragonPart(this, "wing", 4.0F, 2.0F);
      this.subEntities = new EnderDragonPart[]{this.head, this.neck, this.body, this.tail1, this.tail2, this.tail3, this.wing1, this.wing2};
      this.setHealth(this.getMaxHealth());
      this.noPhysics = true;
      this.noCulling = true;
      this.phaseManager = new EnderDragonPhaseManager(this);
   }

   public void setDragonFight(EndDragonFight var1) {
      this.dragonFight = var1;
   }

   public void setFightOrigin(BlockPos var1) {
      this.fightOrigin = var1;
   }

   public BlockPos getFightOrigin() {
      return this.fightOrigin;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 200.0);
   }

   public boolean isFlapping() {
      float var1 = Mth.cos(this.flapTime * 6.2831855F);
      float var2 = Mth.cos(this.oFlapTime * 6.2831855F);
      return var2 <= -0.3F && var1 >= -0.3F;
   }

   public void onFlap() {
      if (this.level().isClientSide && !this.isSilent()) {
         this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_DRAGON_FLAP, this.getSoundSource(), 5.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
      }

   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_PHASE, EnderDragonPhase.HOVERING.getId());
   }

   public double[] getLatencyPos(int var1, float var2) {
      if (this.isDeadOrDying()) {
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
      this.processFlappingMovement();
      if (this.level().isClientSide) {
         this.setHealth(this.getHealth());
         if (!this.isSilent() && !this.phaseManager.getCurrentPhase().isSitting() && --this.growlTime < 0) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_DRAGON_GROWL, this.getSoundSource(), 2.5F, 0.8F + this.random.nextFloat() * 0.3F, false);
            this.growlTime = 200 + this.random.nextInt(200);
         }
      }

      if (this.dragonFight == null) {
         Level var2 = this.level();
         if (var2 instanceof ServerLevel) {
            ServerLevel var1 = (ServerLevel)var2;
            EndDragonFight var26 = var1.getDragonFight();
            if (var26 != null && this.getUUID().equals(var26.getDragonUUID())) {
               this.dragonFight = var26;
            }
         }
      }

      this.oFlapTime = this.flapTime;
      float var27;
      if (this.isDeadOrDying()) {
         float var25 = (this.random.nextFloat() - 0.5F) * 8.0F;
         var27 = (this.random.nextFloat() - 0.5F) * 4.0F;
         float var31 = (this.random.nextFloat() - 0.5F) * 8.0F;
         this.level().addParticle(ParticleTypes.EXPLOSION, this.getX() + (double)var25, this.getY() + 2.0 + (double)var27, this.getZ() + (double)var31, 0.0, 0.0, 0.0);
      } else {
         this.checkCrystals();
         Vec3 var24 = this.getDeltaMovement();
         var27 = 0.2F / ((float)var24.horizontalDistance() * 10.0F + 1.0F);
         var27 *= (float)Math.pow(2.0, var24.y);
         if (this.phaseManager.getCurrentPhase().isSitting()) {
            this.flapTime += 0.1F;
         } else if (this.inWall) {
            this.flapTime += var27 * 0.5F;
         } else {
            this.flapTime += var27;
         }

         this.setYRot(Mth.wrapDegrees(this.getYRot()));
         if (this.isNoAi()) {
            this.flapTime = 0.5F;
         } else {
            if (this.posPointer < 0) {
               for(int var3 = 0; var3 < this.positions.length; ++var3) {
                  this.positions[var3][0] = (double)this.getYRot();
                  this.positions[var3][1] = this.getY();
               }
            }

            if (++this.posPointer == this.positions.length) {
               this.posPointer = 0;
            }

            this.positions[this.posPointer][0] = (double)this.getYRot();
            this.positions[this.posPointer][1] = this.getY();
            float var18;
            float var19;
            float var20;
            if (this.level().isClientSide) {
               if (this.lerpSteps > 0) {
                  this.lerpPositionAndRotationStep(this.lerpSteps, this.lerpX, this.lerpY, this.lerpZ, this.lerpYRot, this.lerpXRot);
                  --this.lerpSteps;
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
                  double var5 = var4.x - this.getX();
                  double var7 = var4.y - this.getY();
                  double var9 = var4.z - this.getZ();
                  double var11 = var5 * var5 + var7 * var7 + var9 * var9;
                  float var13 = var28.getFlySpeed();
                  double var14 = Math.sqrt(var5 * var5 + var9 * var9);
                  if (var14 > 0.0) {
                     var7 = Mth.clamp(var7 / var14, (double)(-var13), (double)var13);
                  }

                  this.setDeltaMovement(this.getDeltaMovement().add(0.0, var7 * 0.01, 0.0));
                  this.setYRot(Mth.wrapDegrees(this.getYRot()));
                  Vec3 var16 = var4.subtract(this.getX(), this.getY(), this.getZ()).normalize();
                  Vec3 var17 = (new Vec3((double)Mth.sin(this.getYRot() * 0.017453292F), this.getDeltaMovement().y, (double)(-Mth.cos(this.getYRot() * 0.017453292F)))).normalize();
                  var18 = Math.max(((float)var17.dot(var16) + 0.5F) / 1.5F, 0.0F);
                  if (Math.abs(var5) > 9.999999747378752E-6 || Math.abs(var9) > 9.999999747378752E-6) {
                     var19 = Mth.clamp(Mth.wrapDegrees(180.0F - (float)Mth.atan2(var5, var9) * 57.295776F - this.getYRot()), -50.0F, 50.0F);
                     this.yRotA *= 0.8F;
                     this.yRotA += var19 * var28.getTurnSpeed();
                     this.setYRot(this.getYRot() + this.yRotA * 0.1F);
                  }

                  var19 = (float)(2.0 / (var11 + 1.0));
                  var20 = 0.06F;
                  this.moveRelative(0.06F * (var18 * var19 + (1.0F - var19)), new Vec3(0.0, 0.0, -1.0));
                  if (this.inWall) {
                     this.move(MoverType.SELF, this.getDeltaMovement().scale(0.800000011920929));
                  } else {
                     this.move(MoverType.SELF, this.getDeltaMovement());
                  }

                  Vec3 var21 = this.getDeltaMovement().normalize();
                  double var22 = 0.8 + 0.15 * (var21.dot(var17) + 1.0) / 2.0;
                  this.setDeltaMovement(this.getDeltaMovement().multiply(var22, 0.9100000262260437, var22));
               }
            }

            this.yBodyRot = this.getYRot();
            Vec3[] var30 = new Vec3[this.subEntities.length];

            for(int var29 = 0; var29 < this.subEntities.length; ++var29) {
               var30[var29] = new Vec3(this.subEntities[var29].getX(), this.subEntities[var29].getY(), this.subEntities[var29].getZ());
            }

            float var32 = (float)(this.getLatencyPos(5, 1.0F)[1] - this.getLatencyPos(10, 1.0F)[1]) * 10.0F * 0.017453292F;
            float var33 = Mth.cos(var32);
            float var6 = Mth.sin(var32);
            float var34 = this.getYRot() * 0.017453292F;
            float var8 = Mth.sin(var34);
            float var35 = Mth.cos(var34);
            this.tickPart(this.body, (double)(var8 * 0.5F), 0.0, (double)(-var35 * 0.5F));
            this.tickPart(this.wing1, (double)(var35 * 4.5F), 2.0, (double)(var8 * 4.5F));
            this.tickPart(this.wing2, (double)(var35 * -4.5F), 2.0, (double)(var8 * -4.5F));
            Level var37 = this.level();
            if (var37 instanceof ServerLevel) {
               ServerLevel var10 = (ServerLevel)var37;
               if (this.hurtTime == 0) {
                  this.knockBack(var10, var10.getEntities(this, this.wing1.getBoundingBox().inflate(4.0, 2.0, 4.0).move(0.0, -2.0, 0.0), EntitySelector.NO_CREATIVE_OR_SPECTATOR));
                  this.knockBack(var10, var10.getEntities(this, this.wing2.getBoundingBox().inflate(4.0, 2.0, 4.0).move(0.0, -2.0, 0.0), EntitySelector.NO_CREATIVE_OR_SPECTATOR));
                  this.hurt(var10.getEntities(this, this.head.getBoundingBox().inflate(1.0), EntitySelector.NO_CREATIVE_OR_SPECTATOR));
                  this.hurt(var10.getEntities(this, this.neck.getBoundingBox().inflate(1.0), EntitySelector.NO_CREATIVE_OR_SPECTATOR));
               }
            }

            float var36 = Mth.sin(this.getYRot() * 0.017453292F - this.yRotA * 0.01F);
            float var38 = Mth.cos(this.getYRot() * 0.017453292F - this.yRotA * 0.01F);
            float var12 = this.getHeadYOffset();
            this.tickPart(this.head, (double)(var36 * 6.5F * var33), (double)(var12 + var6 * 6.5F), (double)(-var38 * 6.5F * var33));
            this.tickPart(this.neck, (double)(var36 * 5.5F * var33), (double)(var12 + var6 * 5.5F), (double)(-var38 * 5.5F * var33));
            double[] var39 = this.getLatencyPos(5, 1.0F);

            int var40;
            for(var40 = 0; var40 < 3; ++var40) {
               EnderDragonPart var15 = null;
               if (var40 == 0) {
                  var15 = this.tail1;
               }

               if (var40 == 1) {
                  var15 = this.tail2;
               }

               if (var40 == 2) {
                  var15 = this.tail3;
               }

               double[] var41 = this.getLatencyPos(12 + var40 * 2, 1.0F);
               float var42 = this.getYRot() * 0.017453292F + this.rotWrap(var41[0] - var39[0]) * 0.017453292F;
               var18 = Mth.sin(var42);
               var19 = Mth.cos(var42);
               var20 = 1.5F;
               float var43 = (float)(var40 + 1) * 2.0F;
               this.tickPart(var15, (double)(-(var8 * 1.5F + var18 * var43) * var33), var41[1] - var39[1] - (double)((var43 + 1.5F) * var6) + 1.5, (double)((var35 * 1.5F + var19 * var43) * var33));
            }

            if (!this.level().isClientSide) {
               this.inWall = this.checkWalls(this.head.getBoundingBox()) | this.checkWalls(this.neck.getBoundingBox()) | this.checkWalls(this.body.getBoundingBox());
               if (this.dragonFight != null) {
                  this.dragonFight.updateDragon(this);
               }
            }

            for(var40 = 0; var40 < this.subEntities.length; ++var40) {
               this.subEntities[var40].xo = var30[var40].x;
               this.subEntities[var40].yo = var30[var40].y;
               this.subEntities[var40].zo = var30[var40].z;
               this.subEntities[var40].xOld = var30[var40].x;
               this.subEntities[var40].yOld = var30[var40].y;
               this.subEntities[var40].zOld = var30[var40].z;
            }

         }
      }
   }

   private void tickPart(EnderDragonPart var1, double var2, double var4, double var6) {
      var1.setPos(this.getX() + var2, this.getY() + var4, this.getZ() + var6);
   }

   private float getHeadYOffset() {
      if (this.phaseManager.getCurrentPhase().isSitting()) {
         return -1.0F;
      } else {
         double[] var1 = this.getLatencyPos(5, 1.0F);
         double[] var2 = this.getLatencyPos(0, 1.0F);
         return (float)(var1[1] - var2[1]);
      }
   }

   private void checkCrystals() {
      if (this.nearestCrystal != null) {
         if (this.nearestCrystal.isRemoved()) {
            this.nearestCrystal = null;
         } else if (this.tickCount % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.setHealth(this.getHealth() + 1.0F);
         }
      }

      if (this.random.nextInt(10) == 0) {
         List var1 = this.level().getEntitiesOfClass(EndCrystal.class, this.getBoundingBox().inflate(32.0));
         EndCrystal var2 = null;
         double var3 = 1.7976931348623157E308;
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

   private void knockBack(ServerLevel var1, List<Entity> var2) {
      double var3 = (this.body.getBoundingBox().minX + this.body.getBoundingBox().maxX) / 2.0;
      double var5 = (this.body.getBoundingBox().minZ + this.body.getBoundingBox().maxZ) / 2.0;
      Iterator var7 = var2.iterator();

      while(var7.hasNext()) {
         Entity var8 = (Entity)var7.next();
         if (var8 instanceof LivingEntity var9) {
            double var10 = var8.getX() - var3;
            double var12 = var8.getZ() - var5;
            double var14 = Math.max(var10 * var10 + var12 * var12, 0.1);
            var8.push(var10 / var14 * 4.0, 0.20000000298023224, var12 / var14 * 4.0);
            if (!this.phaseManager.getCurrentPhase().isSitting() && var9.getLastHurtByMobTimestamp() < var8.tickCount - 2) {
               DamageSource var16 = this.damageSources().mobAttack(this);
               var8.hurt(var16, 5.0F);
               EnchantmentHelper.doPostAttackEffects(var1, var8, var16);
            }
         }
      }

   }

   private void hurt(List<Entity> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Entity var3 = (Entity)var2.next();
         if (var3 instanceof LivingEntity) {
            DamageSource var4 = this.damageSources().mobAttack(this);
            var3.hurt(var4, 10.0F);
            Level var6 = this.level();
            if (var6 instanceof ServerLevel) {
               ServerLevel var5 = (ServerLevel)var6;
               EnchantmentHelper.doPostAttackEffects(var5, var3, var4);
            }
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
               BlockState var14 = this.level().getBlockState(var13);
               if (!var14.isAir() && !var14.is(BlockTags.DRAGON_TRANSPARENT)) {
                  if (this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && !var14.is(BlockTags.DRAGON_IMMUNE)) {
                     var9 = this.level().removeBlock(var13, false) || var9;
                  } else {
                     var8 = true;
                  }
               }
            }
         }
      }

      if (var9) {
         BlockPos var15 = new BlockPos(var2 + this.random.nextInt(var5 - var2 + 1), var3 + this.random.nextInt(var6 - var3 + 1), var4 + this.random.nextInt(var7 - var4 + 1));
         this.level().levelEvent(2008, var15, 0);
      }

      return var8;
   }

   public boolean hurt(EnderDragonPart var1, DamageSource var2, float var3) {
      if (this.phaseManager.getCurrentPhase().getPhase() == EnderDragonPhase.DYING) {
         return false;
      } else {
         var3 = this.phaseManager.getCurrentPhase().onHurt(var2, var3);
         if (var1 != this.head) {
            var3 = var3 / 4.0F + Math.min(var3, 1.0F);
         }

         if (var3 < 0.01F) {
            return false;
         } else {
            if (var2.getEntity() instanceof Player || var2.is(DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS)) {
               float var4 = this.getHealth();
               this.reallyHurt(var2, var3);
               if (this.isDeadOrDying() && !this.phaseManager.getCurrentPhase().isSitting()) {
                  this.setHealth(1.0F);
                  this.phaseManager.setPhase(EnderDragonPhase.DYING);
               }

               if (this.phaseManager.getCurrentPhase().isSitting()) {
                  this.sittingDamageReceived = this.sittingDamageReceived + var4 - this.getHealth();
                  if (this.sittingDamageReceived > 0.25F * this.getMaxHealth()) {
                     this.sittingDamageReceived = 0.0F;
                     this.phaseManager.setPhase(EnderDragonPhase.TAKEOFF);
                  }
               }
            }

            return true;
         }
      }
   }

   public boolean hurt(DamageSource var1, float var2) {
      return !this.level().isClientSide ? this.hurt(this.body, var1, var2) : false;
   }

   protected boolean reallyHurt(DamageSource var1, float var2) {
      return super.hurt(var1, var2);
   }

   public void kill() {
      this.remove(Entity.RemovalReason.KILLED);
      this.gameEvent(GameEvent.ENTITY_DIE);
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
         this.level().addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX() + (double)var1, this.getY() + 2.0 + (double)var2, this.getZ() + (double)var3, 0.0, 0.0, 0.0);
      }

      boolean var4 = this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
      short var5 = 500;
      if (this.dragonFight != null && !this.dragonFight.hasPreviouslyKilledDragon()) {
         var5 = 12000;
      }

      if (this.level() instanceof ServerLevel) {
         if (this.dragonDeathTime > 150 && this.dragonDeathTime % 5 == 0 && var4) {
            ExperienceOrb.award((ServerLevel)this.level(), this.position(), Mth.floor((float)var5 * 0.08F));
         }

         if (this.dragonDeathTime == 1 && !this.isSilent()) {
            this.level().globalLevelEvent(1028, this.blockPosition(), 0);
         }
      }

      this.move(MoverType.SELF, new Vec3(0.0, 0.10000000149011612, 0.0));
      if (this.dragonDeathTime == 200 && this.level() instanceof ServerLevel) {
         if (var4) {
            ExperienceOrb.award((ServerLevel)this.level(), this.position(), Mth.floor((float)var5 * 0.2F));
         }

         if (this.dragonFight != null) {
            this.dragonFight.setDragonKilled(this);
         }

         this.remove(Entity.RemovalReason.KILLED);
         this.gameEvent(GameEvent.ENTITY_DIE);
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

            int var6 = Math.max(this.level().getSeaLevel() + 10, this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(var4, 0, var5)).getY() + var2);
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

      return this.findClosestNode(this.getX(), this.getY(), this.getZ());
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
      var1.putInt("DragonDeathTime", this.dragonDeathTime);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("DragonPhase")) {
         this.phaseManager.setPhase(EnderDragonPhase.getById(var1.getInt("DragonPhase")));
      }

      if (var1.contains("DragonDeathTime")) {
         this.dragonDeathTime = var1.getInt("DragonDeathTime");
      }

   }

   public void checkDespawn() {
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
            var6 = 0.0;
         } else {
            var6 = var3[1] - var2[1];
         }
      } else {
         BlockPos var8 = this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.getLocation(this.fightOrigin));
         double var9 = Math.max(Math.sqrt(var8.distToCenterSqr(this.position())) / 4.0, 1.0);
         var6 = (double)var1 / var9;
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
            float var10 = this.getXRot();
            var6 = 1.5F;
            this.setXRot(-45.0F);
            var4 = this.getViewVector(var1);
            this.setXRot(var10);
         } else {
            var4 = this.getViewVector(var1);
         }
      } else {
         BlockPos var5 = this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.getLocation(this.fightOrigin));
         var6 = Math.max((float)Math.sqrt(var5.distToCenterSqr(this.position())) / 4.0F, 1.0F);
         float var7 = 6.0F / var6;
         float var8 = this.getXRot();
         float var9 = 1.5F;
         this.setXRot(-var7 * 1.5F * 5.0F);
         var4 = this.getViewVector(var1);
         this.setXRot(var8);
      }

      return var4;
   }

   public void onCrystalDestroyed(EndCrystal var1, BlockPos var2, DamageSource var3) {
      Player var4;
      if (var3.getEntity() instanceof Player) {
         var4 = (Player)var3.getEntity();
      } else {
         var4 = this.level().getNearestPlayer(CRYSTAL_DESTROY_TARGETING, (double)var2.getX(), (double)var2.getY(), (double)var2.getZ());
      }

      if (var1 == this.nearestCrystal) {
         this.hurt(this.head, this.damageSources().explosion(var1, var4), 10.0F);
      }

      this.phaseManager.getCurrentPhase().onCrystalDestroyed(var1, var2, var3, var4);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_PHASE.equals(var1) && this.level().isClientSide) {
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

   public boolean addEffect(MobEffectInstance var1, @Nullable Entity var2) {
      return false;
   }

   protected boolean canRide(Entity var1) {
      return false;
   }

   public boolean canUsePortal(boolean var1) {
      return false;
   }

   public void recreateFromPacket(ClientboundAddEntityPacket var1) {
      super.recreateFromPacket(var1);
      EnderDragonPart[] var2 = this.getSubEntities();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var2[var3].setId(var3 + var1.getId());
      }

   }

   public boolean canAttack(LivingEntity var1) {
      return var1.canBeSeenAsEnemy();
   }

   protected float sanitizeScale(float var1) {
      return 1.0F;
   }

   static {
      DATA_PHASE = SynchedEntityData.defineId(EnderDragon.class, EntityDataSerializers.INT);
      CRYSTAL_DESTROY_TARGETING = TargetingConditions.forCombat().range(64.0);
   }
}

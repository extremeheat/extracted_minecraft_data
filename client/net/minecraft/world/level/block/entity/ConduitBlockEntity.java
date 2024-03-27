package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ConduitBlockEntity extends BlockEntity {
   private static final int BLOCK_REFRESH_RATE = 2;
   private static final int EFFECT_DURATION = 13;
   private static final float ROTATION_SPEED = -0.0375F;
   private static final int MIN_ACTIVE_SIZE = 16;
   private static final int MIN_KILL_SIZE = 42;
   private static final int KILL_RANGE = 8;
   private static final Block[] VALID_BLOCKS = new Block[]{Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.SEA_LANTERN, Blocks.DARK_PRISMARINE};
   public int tickCount;
   private float activeRotation;
   private boolean isActive;
   private boolean isHunting;
   private final List<BlockPos> effectBlocks = Lists.newArrayList();
   @Nullable
   private LivingEntity destroyTarget;
   @Nullable
   private UUID destroyTargetUUID;
   private long nextAmbientSoundActivation;

   public ConduitBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.CONDUIT, var1, var2);
   }

   @Override
   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      if (var1.hasUUID("Target")) {
         this.destroyTargetUUID = var1.getUUID("Target");
      } else {
         this.destroyTargetUUID = null;
      }
   }

   @Override
   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      if (this.destroyTarget != null) {
         var1.putUUID("Target", this.destroyTarget.getUUID());
      }
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Override
   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.saveCustomOnly(var1);
   }

   public static void clientTick(Level var0, BlockPos var1, BlockState var2, ConduitBlockEntity var3) {
      ++var3.tickCount;
      long var4 = var0.getGameTime();
      List var6 = var3.effectBlocks;
      if (var4 % 40L == 0L) {
         var3.isActive = updateShape(var0, var1, var6);
         updateHunting(var3, var6);
      }

      updateClientTarget(var0, var1, var3);
      animationTick(var0, var1, var6, var3.destroyTarget, var3.tickCount);
      if (var3.isActive()) {
         ++var3.activeRotation;
      }
   }

   public static void serverTick(Level var0, BlockPos var1, BlockState var2, ConduitBlockEntity var3) {
      ++var3.tickCount;
      long var4 = var0.getGameTime();
      List var6 = var3.effectBlocks;
      if (var4 % 40L == 0L) {
         boolean var7 = updateShape(var0, var1, var6);
         if (var7 != var3.isActive) {
            SoundEvent var8 = var7 ? SoundEvents.CONDUIT_ACTIVATE : SoundEvents.CONDUIT_DEACTIVATE;
            var0.playSound(null, var1, var8, SoundSource.BLOCKS, 1.0F, 1.0F);
         }

         var3.isActive = var7;
         updateHunting(var3, var6);
         if (var7) {
            applyEffects(var0, var1, var6);
            updateDestroyTarget(var0, var1, var2, var6, var3);
         }
      }

      if (var3.isActive()) {
         if (var4 % 80L == 0L) {
            var0.playSound(null, var1, SoundEvents.CONDUIT_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F);
         }

         if (var4 > var3.nextAmbientSoundActivation) {
            var3.nextAmbientSoundActivation = var4 + 60L + (long)var0.getRandom().nextInt(40);
            var0.playSound(null, var1, SoundEvents.CONDUIT_AMBIENT_SHORT, SoundSource.BLOCKS, 1.0F, 1.0F);
         }
      }
   }

   private static void updateHunting(ConduitBlockEntity var0, List<BlockPos> var1) {
      var0.setHunting(var1.size() >= 42);
   }

   private static boolean updateShape(Level var0, BlockPos var1, List<BlockPos> var2) {
      var2.clear();

      for(int var3 = -1; var3 <= 1; ++var3) {
         for(int var4 = -1; var4 <= 1; ++var4) {
            for(int var5 = -1; var5 <= 1; ++var5) {
               BlockPos var6 = var1.offset(var3, var4, var5);
               if (!var0.isWaterAt(var6)) {
                  return false;
               }
            }
         }
      }

      for(int var15 = -2; var15 <= 2; ++var15) {
         for(int var16 = -2; var16 <= 2; ++var16) {
            for(int var17 = -2; var17 <= 2; ++var17) {
               int var18 = Math.abs(var15);
               int var7 = Math.abs(var16);
               int var8 = Math.abs(var17);
               if ((var18 > 1 || var7 > 1 || var8 > 1)
                  && (var15 == 0 && (var7 == 2 || var8 == 2) || var16 == 0 && (var18 == 2 || var8 == 2) || var17 == 0 && (var18 == 2 || var7 == 2))) {
                  BlockPos var9 = var1.offset(var15, var16, var17);
                  BlockState var10 = var0.getBlockState(var9);

                  for(Block var14 : VALID_BLOCKS) {
                     if (var10.is(var14)) {
                        var2.add(var9);
                     }
                  }
               }
            }
         }
      }

      return var2.size() >= 16;
   }

   private static void applyEffects(Level var0, BlockPos var1, List<BlockPos> var2) {
      int var3 = var2.size();
      int var4 = var3 / 7 * 16;
      int var5 = var1.getX();
      int var6 = var1.getY();
      int var7 = var1.getZ();
      AABB var8 = new AABB((double)var5, (double)var6, (double)var7, (double)(var5 + 1), (double)(var6 + 1), (double)(var7 + 1))
         .inflate((double)var4)
         .expandTowards(0.0, (double)var0.getHeight(), 0.0);
      List var9 = var0.getEntitiesOfClass(Player.class, var8);
      if (!var9.isEmpty()) {
         for(Player var11 : var9) {
            if (var1.closerThan(var11.blockPosition(), (double)var4) && var11.isInWaterOrRain()) {
               var11.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 260, 0, true, true));
            }
         }
      }
   }

   private static void updateDestroyTarget(Level var0, BlockPos var1, BlockState var2, List<BlockPos> var3, ConduitBlockEntity var4) {
      LivingEntity var5 = var4.destroyTarget;
      int var6 = var3.size();
      if (var6 < 42) {
         var4.destroyTarget = null;
      } else if (var4.destroyTarget == null && var4.destroyTargetUUID != null) {
         var4.destroyTarget = findDestroyTarget(var0, var1, var4.destroyTargetUUID);
         var4.destroyTargetUUID = null;
      } else if (var4.destroyTarget == null) {
         List var7 = var0.getEntitiesOfClass(LivingEntity.class, getDestroyRangeAABB(var1), var0x -> var0x instanceof Enemy && var0x.isInWaterOrRain());
         if (!var7.isEmpty()) {
            var4.destroyTarget = (LivingEntity)var7.get(var0.random.nextInt(var7.size()));
         }
      } else if (!var4.destroyTarget.isAlive() || !var1.closerThan(var4.destroyTarget.blockPosition(), 8.0)) {
         var4.destroyTarget = null;
      }

      if (var4.destroyTarget != null) {
         var0.playSound(
            null,
            var4.destroyTarget.getX(),
            var4.destroyTarget.getY(),
            var4.destroyTarget.getZ(),
            SoundEvents.CONDUIT_ATTACK_TARGET,
            SoundSource.BLOCKS,
            1.0F,
            1.0F
         );
         var4.destroyTarget.hurt(var0.damageSources().magic(), 4.0F);
      }

      if (var5 != var4.destroyTarget) {
         var0.sendBlockUpdated(var1, var2, var2, 2);
      }
   }

   private static void updateClientTarget(Level var0, BlockPos var1, ConduitBlockEntity var2) {
      if (var2.destroyTargetUUID == null) {
         var2.destroyTarget = null;
      } else if (var2.destroyTarget == null || !var2.destroyTarget.getUUID().equals(var2.destroyTargetUUID)) {
         var2.destroyTarget = findDestroyTarget(var0, var1, var2.destroyTargetUUID);
         if (var2.destroyTarget == null) {
            var2.destroyTargetUUID = null;
         }
      }
   }

   private static AABB getDestroyRangeAABB(BlockPos var0) {
      int var1 = var0.getX();
      int var2 = var0.getY();
      int var3 = var0.getZ();
      return new AABB((double)var1, (double)var2, (double)var3, (double)(var1 + 1), (double)(var2 + 1), (double)(var3 + 1)).inflate(8.0);
   }

   @Nullable
   private static LivingEntity findDestroyTarget(Level var0, BlockPos var1, UUID var2) {
      List var3 = var0.getEntitiesOfClass(LivingEntity.class, getDestroyRangeAABB(var1), var1x -> var1x.getUUID().equals(var2));
      return var3.size() == 1 ? (LivingEntity)var3.get(0) : null;
   }

   private static void animationTick(Level var0, BlockPos var1, List<BlockPos> var2, @Nullable Entity var3, int var4) {
      RandomSource var5 = var0.random;
      double var6 = (double)(Mth.sin((float)(var4 + 35) * 0.1F) / 2.0F + 0.5F);
      var6 = (var6 * var6 + var6) * 0.30000001192092896;
      Vec3 var8 = new Vec3((double)var1.getX() + 0.5, (double)var1.getY() + 1.5 + var6, (double)var1.getZ() + 0.5);

      for(BlockPos var10 : var2) {
         if (var5.nextInt(50) == 0) {
            BlockPos var11 = var10.subtract(var1);
            float var12 = -0.5F + var5.nextFloat() + (float)var11.getX();
            float var13 = -2.0F + var5.nextFloat() + (float)var11.getY();
            float var14 = -0.5F + var5.nextFloat() + (float)var11.getZ();
            var0.addParticle(ParticleTypes.NAUTILUS, var8.x, var8.y, var8.z, (double)var12, (double)var13, (double)var14);
         }
      }

      if (var3 != null) {
         Vec3 var16 = new Vec3(var3.getX(), var3.getEyeY(), var3.getZ());
         float var17 = (-0.5F + var5.nextFloat()) * (3.0F + var3.getBbWidth());
         float var18 = -1.0F + var5.nextFloat() * var3.getBbHeight();
         float var19 = (-0.5F + var5.nextFloat()) * (3.0F + var3.getBbWidth());
         Vec3 var20 = new Vec3((double)var17, (double)var18, (double)var19);
         var0.addParticle(ParticleTypes.NAUTILUS, var16.x, var16.y, var16.z, var20.x, var20.y, var20.z);
      }
   }

   public boolean isActive() {
      return this.isActive;
   }

   public boolean isHunting() {
      return this.isHunting;
   }

   private void setHunting(boolean var1) {
      this.isHunting = var1;
   }

   public float getActiveRotation(float var1) {
      return (this.activeRotation + var1) * -0.0375F;
   }
}

package net.minecraft.world.level.block.entity;

import java.util.Iterator;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.monster.creaking.CreakingTransient;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableObject;

public class CreakingHeartBlockEntity extends BlockEntity {
   private static final int PLAYER_DETECTION_RANGE = 32;
   public static final int CREAKING_ROAMING_RADIUS = 32;
   private static final int DISTANCE_CREAKING_TOO_FAR = 34;
   private static final int SPAWN_RANGE_XZ = 16;
   private static final int SPAWN_RANGE_Y = 8;
   private static final int ATTEMPTS_PER_SPAWN = 5;
   private static final int UPDATE_TICKS = 20;
   private static final int HURT_CALL_TOTAL_TICKS = 100;
   private static final int NUMBER_OF_HURT_CALLS = 10;
   private static final int HURT_CALL_INTERVAL = 10;
   private static final int HURT_CALL_PARTICLE_TICKS = 50;
   private static final int MAX_DEPTH = 2;
   private static final int MAX_COUNT = 64;
   @Nullable
   private CreakingTransient creaking;
   private int ticker;
   private int emitter;
   @Nullable
   private Vec3 emitterTarget;
   private int outputSignal;

   public CreakingHeartBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.CREAKING_HEART, var1, var2);
   }

   public static void serverTick(Level var0, BlockPos var1, BlockState var2, CreakingHeartBlockEntity var3) {
      int var4 = var3.computeAnalogOutputSignal();
      if (var3.outputSignal != var4) {
         var3.outputSignal = var4;
         var0.updateNeighbourForOutputSignal(var1, Blocks.CREAKING_HEART);
      }

      ServerLevel var5;
      if (var3.emitter > 0) {
         if (var3.emitter > 50) {
            var3.emitParticles((ServerLevel)var0, 1, true);
            var3.emitParticles((ServerLevel)var0, 1, false);
         }

         if (var3.emitter % 10 == 0 && var0 instanceof ServerLevel) {
            var5 = (ServerLevel)var0;
            if (var3.emitterTarget != null) {
               if (var3.creaking != null) {
                  var3.emitterTarget = var3.creaking.getBoundingBox().getCenter();
               }

               Vec3 var6 = Vec3.atCenterOf(var1);
               float var7 = 0.2F + 0.8F * (float)(100 - var3.emitter) / 100.0F;
               Vec3 var8 = var6.subtract(var3.emitterTarget).scale((double)var7).add(var3.emitterTarget);
               BlockPos var9 = BlockPos.containing(var8);
               float var10 = (float)var3.emitter / 2.0F / 100.0F + 0.5F;
               var5.playSound((Player)null, var9, SoundEvents.CREAKING_HEART_HURT, SoundSource.BLOCKS, var10, 1.0F);
            }
         }

         --var3.emitter;
      }

      if (var3.ticker-- < 0) {
         var3.ticker = 20;
         if (var3.creaking != null) {
            if (CreakingHeartBlock.isNaturalNight(var0) && !(var3.distanceToCreaking() > 34.0) && !var3.creaking.playerIsStuckInYou()) {
               if (var3.creaking.isRemoved()) {
                  var3.creaking = null;
               }

               if (!CreakingHeartBlock.hasRequiredLogs(var2, var0, var1) && var3.creaking == null) {
                  var0.setBlock(var1, (BlockState)var2.setValue(CreakingHeartBlock.ACTIVE, false), 3);
               }

            } else {
               var3.removeProtector((DamageSource)null);
            }
         } else if (!CreakingHeartBlock.hasRequiredLogs(var2, var0, var1)) {
            var0.setBlock(var1, (BlockState)var2.setValue(CreakingHeartBlock.ACTIVE, false), 3);
         } else if ((Boolean)var2.getValue(CreakingHeartBlock.ACTIVE)) {
            if (CreakingHeartBlock.isNaturalNight(var0)) {
               if (var0.getDifficulty() != Difficulty.PEACEFUL) {
                  if (var0 instanceof ServerLevel) {
                     var5 = (ServerLevel)var0;
                     if (!var5.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                        return;
                     }
                  }

                  Player var11 = var0.getNearestPlayer((double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), 32.0, false);
                  if (var11 != null) {
                     var3.creaking = spawnProtector((ServerLevel)var0, var3);
                     if (var3.creaking != null) {
                        var3.creaking.makeSound(SoundEvents.CREAKING_SPAWN);
                        var0.playSound((Player)null, (BlockPos)var3.getBlockPos(), SoundEvents.CREAKING_HEART_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
                     }
                  }

               }
            }
         }
      }
   }

   private double distanceToCreaking() {
      return this.creaking == null ? 0.0 : Math.sqrt(this.creaking.distanceToSqr(Vec3.atBottomCenterOf(this.getBlockPos())));
   }

   @Nullable
   private static CreakingTransient spawnProtector(ServerLevel var0, CreakingHeartBlockEntity var1) {
      BlockPos var2 = var1.getBlockPos();
      Optional var3 = SpawnUtil.trySpawnMob(EntityType.CREAKING_TRANSIENT, EntitySpawnReason.SPAWNER, var0, var2, 5, 16, 8, SpawnUtil.Strategy.ON_TOP_OF_COLLIDER_NO_LEAVES, true);
      if (var3.isEmpty()) {
         return null;
      } else {
         CreakingTransient var4 = (CreakingTransient)var3.get();
         var0.gameEvent(var4, GameEvent.ENTITY_PLACE, var4.position());
         var0.broadcastEntityEvent(var4, (byte)60);
         var4.bindToCreakingHeart(var2);
         return var4;
      }
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.saveCustomOnly(var1);
   }

   public void creakingHurt() {
      if (this.creaking != null) {
         Level var2 = this.level;
         if (var2 instanceof ServerLevel) {
            ServerLevel var1 = (ServerLevel)var2;
            if (this.emitter <= 0) {
               this.emitParticles(var1, 20, false);
               int var4 = this.level.getRandom().nextIntBetweenInclusive(2, 3);

               for(int var3 = 0; var3 < var4; ++var3) {
                  this.spreadResin().ifPresent((var1x) -> {
                     this.level.playSound((Player)null, (BlockPos)var1x, SoundEvents.RESIN_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                  });
               }

               this.emitter = 100;
               this.emitterTarget = this.creaking.getBoundingBox().getCenter();
            }
         }
      }
   }

   private Optional<BlockPos> spreadResin() {
      BlockPos var1 = this.worldPosition;
      MutableObject var2 = new MutableObject((Object)null);
      BlockPos.breadthFirstTraversal(this.worldPosition, 2, 64, (var1x, var2x) -> {
         Iterator var3 = Util.shuffledCopy((Object[])Direction.values(), this.level.random).iterator();

         while(var3.hasNext()) {
            Direction var4 = (Direction)var3.next();
            BlockPos var5 = var1x.relative(var4);
            BlockState var6 = this.level.getBlockState(var5);
            if (var6.is(BlockTags.PALE_OAK_LOGS)) {
               var2x.accept(var5);
            }
         }

      }, (var2x) -> {
         if (!this.level.getBlockState(var2x).is(BlockTags.PALE_OAK_LOGS)) {
            return BlockPos.TraversalNodeStatus.ACCEPT;
         } else {
            Iterator var3 = Util.shuffledCopy((Object[])Direction.values(), this.level.random).iterator();

            BlockPos var5;
            BlockState var6;
            Direction var7;
            do {
               if (!var3.hasNext()) {
                  return BlockPos.TraversalNodeStatus.ACCEPT;
               }

               Direction var4 = (Direction)var3.next();
               var5 = var2x.relative(var4);
               var6 = this.level.getBlockState(var5);
               var7 = var4.getOpposite();
               if (var6.isAir()) {
                  var6 = Blocks.RESIN_CLUMP.defaultBlockState();
               }
            } while(!var6.is(Blocks.RESIN_CLUMP) || MultifaceBlock.hasFace(var6, var7));

            this.level.setBlock(var5, (BlockState)var6.setValue(MultifaceBlock.getFaceProperty(var7), true), 3);
            var2.setValue(var5);
            return BlockPos.TraversalNodeStatus.STOP;
         }
      });
      return Optional.ofNullable((BlockPos)var2.getValue());
   }

   private void emitParticles(ServerLevel var1, int var2, boolean var3) {
      if (this.creaking != null) {
         int var4 = var3 ? 16545810 : 6250335;
         RandomSource var5 = var1.random;

         for(double var6 = 0.0; var6 < (double)var2; ++var6) {
            Vec3 var8 = this.creaking.getBoundingBox().getMinPosition().add(var5.nextDouble() * this.creaking.getBoundingBox().getXsize(), var5.nextDouble() * this.creaking.getBoundingBox().getYsize(), var5.nextDouble() * this.creaking.getBoundingBox().getZsize());
            Vec3 var9 = Vec3.atLowerCornerOf(this.getBlockPos()).add(var5.nextDouble(), var5.nextDouble(), var5.nextDouble());
            if (var3) {
               Vec3 var10 = var8;
               var8 = var9;
               var9 = var10;
            }

            TrailParticleOption var11 = new TrailParticleOption(var9, var4, var5.nextInt(40) + 10);
            var1.sendParticles(var11, true, true, var8.x, var8.y, var8.z, 1, 0.0, 0.0, 0.0, 0.0);
         }

      }
   }

   public void removeProtector(@Nullable DamageSource var1) {
      if (this.creaking != null) {
         if (var1 == null) {
            this.creaking.tearDown();
         } else {
            this.creaking.creakingDeathEffects(var1);
            this.creaking.setTearingDown();
            this.creaking.setHealth(0.0F);
         }

         this.creaking = null;
      }

   }

   public boolean isProtector(Creaking var1) {
      return this.creaking == var1;
   }

   public int getAnalogOutputSignal() {
      return this.outputSignal;
   }

   public int computeAnalogOutputSignal() {
      if (this.creaking == null) {
         return 0;
      } else {
         double var1 = this.distanceToCreaking();
         double var3 = Math.clamp(var1, 0.0, 32.0) / 32.0;
         return 15 - (int)Math.floor(var3 * 15.0);
      }
   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}

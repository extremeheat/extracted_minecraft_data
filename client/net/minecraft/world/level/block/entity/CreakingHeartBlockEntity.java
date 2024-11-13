package net.minecraft.world.level.block.entity;

import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.UUID;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
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
   private static final int UPDATE_TICKS_VARIANCE = 5;
   private static final int HURT_CALL_TOTAL_TICKS = 100;
   private static final int NUMBER_OF_HURT_CALLS = 10;
   private static final int HURT_CALL_INTERVAL = 10;
   private static final int HURT_CALL_PARTICLE_TICKS = 50;
   private static final int MAX_DEPTH = 2;
   private static final int MAX_COUNT = 64;
   private static final int TICKS_GRACE_PERIOD = 30;
   private static final Optional<Creaking> NO_CREAKING = Optional.empty();
   @Nullable
   private Either<Creaking, UUID> creakingInfo;
   private long ticksExisted;
   private int ticker;
   private int emitter;
   @Nullable
   private Vec3 emitterTarget;
   private int outputSignal;

   public CreakingHeartBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.CREAKING_HEART, var1, var2);
   }

   public static void serverTick(Level var0, BlockPos var1, BlockState var2, CreakingHeartBlockEntity var3) {
      ++var3.ticksExisted;
      if (var0 instanceof ServerLevel var4) {
         int var5 = var3.computeAnalogOutputSignal();
         if (var3.outputSignal != var5) {
            var3.outputSignal = var5;
            var0.updateNeighbourForOutputSignal(var1, Blocks.CREAKING_HEART);
         }

         if (var3.emitter > 0) {
            if (var3.emitter > 50) {
               var3.emitParticles(var4, 1, true);
               var3.emitParticles(var4, 1, false);
            }

            if (var3.emitter % 10 == 0 && var3.emitterTarget != null) {
               var3.getCreakingProtector().ifPresent((var1x) -> var3.emitterTarget = var1x.getBoundingBox().getCenter());
               Vec3 var6 = Vec3.atCenterOf(var1);
               float var7 = 0.2F + 0.8F * (float)(100 - var3.emitter) / 100.0F;
               Vec3 var8 = var6.subtract(var3.emitterTarget).scale((double)var7).add(var3.emitterTarget);
               BlockPos var9 = BlockPos.containing(var8);
               float var10 = (float)var3.emitter / 2.0F / 100.0F + 0.5F;
               var4.playSound((Player)null, var9, SoundEvents.CREAKING_HEART_HURT, SoundSource.BLOCKS, var10, 1.0F);
            }

            --var3.emitter;
         }

         if (var3.ticker-- < 0) {
            var3.ticker = var3.level == null ? 20 : var3.level.random.nextInt(5) + 20;
            if (var3.creakingInfo == null) {
               if (!CreakingHeartBlock.hasRequiredLogs(var2, var0, var1)) {
                  var0.setBlock(var1, (BlockState)var2.setValue(CreakingHeartBlock.ACTIVE, false), 3);
               } else if ((Boolean)var2.getValue(CreakingHeartBlock.ACTIVE)) {
                  if (CreakingHeartBlock.isNaturalNight(var0)) {
                     if (var0.getDifficulty() != Difficulty.PEACEFUL) {
                        if (var4.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                           Player var12 = var0.getNearestPlayer((double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), 32.0, false);
                           if (var12 != null) {
                              Creaking var14 = spawnProtector(var4, var3);
                              if (var14 != null) {
                                 var3.creakingInfo = Either.left(var14);
                                 var14.makeSound(SoundEvents.CREAKING_SPAWN);
                                 var0.playSound((Player)null, (BlockPos)var3.getBlockPos(), SoundEvents.CREAKING_HEART_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
                              }
                           }

                        }
                     }
                  }
               }
            } else {
               Optional var11 = var3.getCreakingProtector();
               if (var11.isPresent()) {
                  Creaking var13 = (Creaking)var11.get();
                  if (!CreakingHeartBlock.isNaturalNight(var0) || var3.distanceToCreaking() > 34.0 || var13.playerIsStuckInYou()) {
                     var3.removeProtector((DamageSource)null);
                     return;
                  }

                  if (var13.isRemoved()) {
                     var3.creakingInfo = null;
                  }

                  if (!CreakingHeartBlock.hasRequiredLogs(var2, var0, var1) && var3.creakingInfo == null) {
                     var0.setBlock(var1, (BlockState)var2.setValue(CreakingHeartBlock.ACTIVE, false), 3);
                  }
               }

            }
         }
      }
   }

   private double distanceToCreaking() {
      return (Double)this.getCreakingProtector().map((var1) -> Math.sqrt(var1.distanceToSqr(Vec3.atBottomCenterOf(this.getBlockPos())))).orElse(0.0);
   }

   private Optional<Creaking> getCreakingProtector() {
      if (this.creakingInfo == null) {
         return NO_CREAKING;
      } else if (this.creakingInfo.left().isPresent()) {
         return Optional.of((Creaking)this.creakingInfo.left().get());
      } else {
         Level var2 = this.level;
         if (var2 instanceof ServerLevel) {
            ServerLevel var1 = (ServerLevel)var2;
            if (this.creakingInfo.right().isPresent()) {
               UUID var5 = (UUID)this.creakingInfo.right().get();
               Entity var3 = var1.getEntity(var5);
               if (var3 instanceof Creaking) {
                  Creaking var4 = (Creaking)var3;
                  this.creakingInfo = Either.left(var4);
                  return Optional.of(var4);
               }

               if (this.ticksExisted >= 30L) {
                  this.creakingInfo = null;
               }

               return NO_CREAKING;
            }
         }

         return NO_CREAKING;
      }
   }

   @Nullable
   private static Creaking spawnProtector(ServerLevel var0, CreakingHeartBlockEntity var1) {
      BlockPos var2 = var1.getBlockPos();
      Optional var3 = SpawnUtil.trySpawnMob(EntityType.CREAKING, EntitySpawnReason.SPAWNER, var0, var2, 5, 16, 8, SpawnUtil.Strategy.ON_TOP_OF_COLLIDER_NO_LEAVES, true);
      if (var3.isEmpty()) {
         return null;
      } else {
         Creaking var4 = (Creaking)var3.get();
         var0.gameEvent(var4, GameEvent.ENTITY_PLACE, var4.position());
         var0.broadcastEntityEvent(var4, (byte)60);
         var4.setTransient(var2);
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
      Object var2 = this.getCreakingProtector().orElse((Object)null);
      if (var2 instanceof Creaking var1) {
         Level var3 = this.level;
         if (var3 instanceof ServerLevel var5) {
            if (this.emitter <= 0) {
               this.emitParticles(var5, 20, false);
               int var6 = this.level.getRandom().nextIntBetweenInclusive(2, 3);

               for(int var4 = 0; var4 < var6; ++var4) {
                  this.spreadResin().ifPresent((var1x) -> {
                     this.level.playSound((Player)null, (BlockPos)var1x, SoundEvents.RESIN_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                     this.level.gameEvent(GameEvent.BLOCK_PLACE, var1x, GameEvent.Context.of(this.level.getBlockState(var1x)));
                  });
               }

               this.emitter = 100;
               this.emitterTarget = var1.getBoundingBox().getCenter();
            }
         }
      }
   }

   private Optional<BlockPos> spreadResin() {
      MutableObject var1 = new MutableObject((Object)null);
      BlockPos.breadthFirstTraversal(this.worldPosition, 2, 64, (var1x, var2) -> {
         for(Direction var4 : Util.shuffledCopy(Direction.values(), this.level.random)) {
            BlockPos var5 = var1x.relative(var4);
            if (this.level.getBlockState(var5).is(BlockTags.PALE_OAK_LOGS)) {
               var2.accept(var5);
            }
         }

      }, (var2) -> {
         if (!this.level.getBlockState(var2).is(BlockTags.PALE_OAK_LOGS)) {
            return BlockPos.TraversalNodeStatus.ACCEPT;
         } else {
            for(Direction var4 : Util.shuffledCopy(Direction.values(), this.level.random)) {
               BlockPos var5 = var2.relative(var4);
               BlockState var6 = this.level.getBlockState(var5);
               Direction var7 = var4.getOpposite();
               if (var6.isAir()) {
                  var6 = Blocks.RESIN_CLUMP.defaultBlockState();
               } else if (var6.is(Blocks.WATER) && var6.getFluidState().isSource()) {
                  var6 = (BlockState)Blocks.RESIN_CLUMP.defaultBlockState().setValue(MultifaceBlock.WATERLOGGED, true);
               }

               if (var6.is(Blocks.RESIN_CLUMP) && !MultifaceBlock.hasFace(var6, var7)) {
                  this.level.setBlock(var5, (BlockState)var6.setValue(MultifaceBlock.getFaceProperty(var7), true), 3);
                  var1.setValue(var5);
                  return BlockPos.TraversalNodeStatus.STOP;
               }
            }

            return BlockPos.TraversalNodeStatus.ACCEPT;
         }
      });
      return Optional.ofNullable((BlockPos)var1.getValue());
   }

   private void emitParticles(ServerLevel var1, int var2, boolean var3) {
      Object var5 = this.getCreakingProtector().orElse((Object)null);
      if (var5 instanceof Creaking var4) {
         int var13 = var3 ? 16545810 : 6250335;
         RandomSource var6 = var1.random;

         for(double var7 = 0.0; var7 < (double)var2; ++var7) {
            AABB var9 = var4.getBoundingBox();
            Vec3 var10 = var9.getMinPosition().add(var6.nextDouble() * var9.getXsize(), var6.nextDouble() * var9.getYsize(), var6.nextDouble() * var9.getZsize());
            Vec3 var11 = Vec3.atLowerCornerOf(this.getBlockPos()).add(var6.nextDouble(), var6.nextDouble(), var6.nextDouble());
            if (var3) {
               Vec3 var12 = var10;
               var10 = var11;
               var11 = var12;
            }

            TrailParticleOption var14 = new TrailParticleOption(var11, var13, var6.nextInt(40) + 10);
            var1.sendParticles(var14, true, true, var10.x, var10.y, var10.z, 1, 0.0, 0.0, 0.0, 0.0);
         }

      }
   }

   public void removeProtector(@Nullable DamageSource var1) {
      Object var3 = this.getCreakingProtector().orElse((Object)null);
      if (var3 instanceof Creaking var2) {
         if (var1 == null) {
            var2.tearDown();
         } else {
            var2.creakingDeathEffects(var1);
            var2.setTearingDown();
            var2.setHealth(0.0F);
         }

         this.creakingInfo = null;
      }

   }

   public boolean isProtector(Creaking var1) {
      return (Boolean)this.getCreakingProtector().map((var1x) -> var1x == var1).orElse(false);
   }

   public int getAnalogOutputSignal() {
      return this.outputSignal;
   }

   public int computeAnalogOutputSignal() {
      if (this.creakingInfo != null && !this.getCreakingProtector().isEmpty()) {
         double var1 = this.distanceToCreaking();
         double var3 = Math.clamp(var1, 0.0, 32.0) / 32.0;
         return 15 - (int)Math.floor(var3 * 15.0);
      } else {
         return 0;
      }
   }

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      if (var1.contains("creaking")) {
         this.creakingInfo = Either.right(var1.getUUID("creaking"));
      } else {
         this.creakingInfo = null;
      }

   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      if (this.creakingInfo != null) {
         var1.putUUID("creaking", (UUID)this.creakingInfo.map(Entity::getUUID, (var0) -> var0));
      }

   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}

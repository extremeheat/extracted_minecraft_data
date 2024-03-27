package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.EndFeatures;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.EndGatewayConfiguration;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class TheEndGatewayBlockEntity extends TheEndPortalBlockEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int SPAWN_TIME = 200;
   private static final int COOLDOWN_TIME = 40;
   private static final int ATTENTION_INTERVAL = 2400;
   private static final int EVENT_COOLDOWN = 1;
   private static final int GATEWAY_HEIGHT_ABOVE_SURFACE = 10;
   private long age;
   private int teleportCooldown;
   @Nullable
   private BlockPos exitPortal;
   private boolean exactTeleport;

   public TheEndGatewayBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.END_GATEWAY, var1, var2);
   }

   @Override
   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      var1.putLong("Age", this.age);
      if (this.exitPortal != null) {
         var1.put("exit_portal", NbtUtils.writeBlockPos(this.exitPortal));
      }

      if (this.exactTeleport) {
         var1.putBoolean("ExactTeleport", true);
      }
   }

   @Override
   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      this.age = var1.getLong("Age");
      NbtUtils.readBlockPos(var1, "exit_portal").filter(Level::isInSpawnableBounds).ifPresent(var1x -> this.exitPortal = var1x);
      this.exactTeleport = var1.getBoolean("ExactTeleport");
   }

   public static void beamAnimationTick(Level var0, BlockPos var1, BlockState var2, TheEndGatewayBlockEntity var3) {
      ++var3.age;
      if (var3.isCoolingDown()) {
         --var3.teleportCooldown;
      }
   }

   public static void teleportTick(Level var0, BlockPos var1, BlockState var2, TheEndGatewayBlockEntity var3) {
      boolean var4 = var3.isSpawning();
      boolean var5 = var3.isCoolingDown();
      ++var3.age;
      if (var5) {
         --var3.teleportCooldown;
      } else {
         List var6 = var0.getEntitiesOfClass(Entity.class, new AABB(var1), TheEndGatewayBlockEntity::canEntityTeleport);
         if (!var6.isEmpty()) {
            teleportEntity(var0, var1, var2, (Entity)var6.get(var0.random.nextInt(var6.size())), var3);
         }

         if (var3.age % 2400L == 0L) {
            triggerCooldown(var0, var1, var2, var3);
         }
      }

      if (var4 != var3.isSpawning() || var5 != var3.isCoolingDown()) {
         setChanged(var0, var1, var2);
      }
   }

   public static boolean canEntityTeleport(Entity var0) {
      return EntitySelector.NO_SPECTATORS.test(var0) && !var0.getRootVehicle().isOnPortalCooldown();
   }

   public boolean isSpawning() {
      return this.age < 200L;
   }

   public boolean isCoolingDown() {
      return this.teleportCooldown > 0;
   }

   public float getSpawnPercent(float var1) {
      return Mth.clamp(((float)this.age + var1) / 200.0F, 0.0F, 1.0F);
   }

   public float getCooldownPercent(float var1) {
      return 1.0F - Mth.clamp(((float)this.teleportCooldown - var1) / 40.0F, 0.0F, 1.0F);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Override
   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.saveCustomOnly(var1);
   }

   private static void triggerCooldown(Level var0, BlockPos var1, BlockState var2, TheEndGatewayBlockEntity var3) {
      if (!var0.isClientSide) {
         var3.teleportCooldown = 40;
         var0.blockEvent(var1, var2.getBlock(), 1, 0);
         setChanged(var0, var1, var2);
      }
   }

   @Override
   public boolean triggerEvent(int var1, int var2) {
      if (var1 == 1) {
         this.teleportCooldown = 40;
         return true;
      } else {
         return super.triggerEvent(var1, var2);
      }
   }

   public static void teleportEntity(Level var0, BlockPos var1, BlockState var2, Entity var3, TheEndGatewayBlockEntity var4) {
      if (var0 instanceof ServerLevel var5 && !var4.isCoolingDown()) {
         var4.teleportCooldown = 100;
         if (var4.exitPortal == null && var0.dimension() == Level.END) {
            BlockPos var6 = findOrCreateValidTeleportPos((ServerLevel)var5, var1);
            var6 = var6.above(10);
            LOGGER.debug("Creating portal at {}", var6);
            spawnGatewayPortal((ServerLevel)var5, var6, EndGatewayConfiguration.knownExit(var1, false));
            var4.exitPortal = var6;
         }

         if (var4.exitPortal != null) {
            BlockPos var10 = var4.exactTeleport ? var4.exitPortal : findExitPosition(var0, var4.exitPortal);
            Entity var7;
            if (var3 instanceof ThrownEnderpearl) {
               Entity var8 = ((ThrownEnderpearl)var3).getOwner();
               if (var8 instanceof ServerPlayer) {
                  CriteriaTriggers.ENTER_BLOCK.trigger((ServerPlayer)var8, var2);
               }

               if (var8 != null) {
                  var7 = var8;
                  var3.discard();
               } else {
                  var7 = var3;
               }
            } else {
               var7 = var3.getRootVehicle();
            }

            var7.setPortalCooldown();
            var7.teleportToWithTicket((double)var10.getX() + 0.5, (double)var10.getY(), (double)var10.getZ() + 0.5);
         }

         triggerCooldown(var0, var1, var2, var4);
      }
   }

   private static BlockPos findExitPosition(Level var0, BlockPos var1) {
      BlockPos var2 = findTallestBlock(var0, var1.offset(0, 2, 0), 5, false);
      LOGGER.debug("Best exit position for portal at {} is {}", var1, var2);
      return var2.above();
   }

   private static BlockPos findOrCreateValidTeleportPos(ServerLevel var0, BlockPos var1) {
      Vec3 var2 = findExitPortalXZPosTentative(var0, var1);
      LevelChunk var3 = getChunk(var0, var2);
      BlockPos var4 = findValidSpawnInChunk(var3);
      if (var4 == null) {
         BlockPos var5 = BlockPos.containing(var2.x + 0.5, 75.0, var2.z + 0.5);
         LOGGER.debug("Failed to find a suitable block to teleport to, spawning an island on {}", var5);
         var0.registryAccess()
            .registry(Registries.CONFIGURED_FEATURE)
            .flatMap(var0x -> var0x.getHolder(EndFeatures.END_ISLAND))
            .ifPresent(var2x -> ((ConfiguredFeature)var2x.value()).place(var0, var0.getChunkSource().getGenerator(), RandomSource.create(var5.asLong()), var5));
         var4 = var5;
      } else {
         LOGGER.debug("Found suitable block to teleport to: {}", var4);
      }

      return findTallestBlock(var0, var4, 16, true);
   }

   private static Vec3 findExitPortalXZPosTentative(ServerLevel var0, BlockPos var1) {
      Vec3 var2 = new Vec3((double)var1.getX(), 0.0, (double)var1.getZ()).normalize();
      boolean var3 = true;
      Vec3 var4 = var2.scale(1024.0);

      for(int var5 = 16; !isChunkEmpty(var0, var4) && var5-- > 0; var4 = var4.add(var2.scale(-16.0))) {
         LOGGER.debug("Skipping backwards past nonempty chunk at {}", var4);
      }

      for(int var6 = 16; isChunkEmpty(var0, var4) && var6-- > 0; var4 = var4.add(var2.scale(16.0))) {
         LOGGER.debug("Skipping forward past empty chunk at {}", var4);
      }

      LOGGER.debug("Found chunk at {}", var4);
      return var4;
   }

   private static boolean isChunkEmpty(ServerLevel var0, Vec3 var1) {
      return getChunk(var0, var1).getHighestFilledSectionIndex() == -1;
   }

   private static BlockPos findTallestBlock(BlockGetter var0, BlockPos var1, int var2, boolean var3) {
      BlockPos var4 = null;

      for(int var5 = -var2; var5 <= var2; ++var5) {
         for(int var6 = -var2; var6 <= var2; ++var6) {
            if (var5 != 0 || var6 != 0 || var3) {
               for(int var7 = var0.getMaxBuildHeight() - 1; var7 > (var4 == null ? var0.getMinBuildHeight() : var4.getY()); --var7) {
                  BlockPos var8 = new BlockPos(var1.getX() + var5, var7, var1.getZ() + var6);
                  BlockState var9 = var0.getBlockState(var8);
                  if (var9.isCollisionShapeFullBlock(var0, var8) && (var3 || !var9.is(Blocks.BEDROCK))) {
                     var4 = var8;
                     break;
                  }
               }
            }
         }
      }

      return var4 == null ? var1 : var4;
   }

   private static LevelChunk getChunk(Level var0, Vec3 var1) {
      return var0.getChunk(Mth.floor(var1.x / 16.0), Mth.floor(var1.z / 16.0));
   }

   @Nullable
   private static BlockPos findValidSpawnInChunk(LevelChunk var0) {
      ChunkPos var1 = var0.getPos();
      BlockPos var2 = new BlockPos(var1.getMinBlockX(), 30, var1.getMinBlockZ());
      int var3 = var0.getHighestSectionPosition() + 16 - 1;
      BlockPos var4 = new BlockPos(var1.getMaxBlockX(), var3, var1.getMaxBlockZ());
      BlockPos var5 = null;
      double var6 = 0.0;

      for(BlockPos var9 : BlockPos.betweenClosed(var2, var4)) {
         BlockState var10 = var0.getBlockState(var9);
         BlockPos var11 = var9.above();
         BlockPos var12 = var9.above(2);
         if (var10.is(Blocks.END_STONE)
            && !var0.getBlockState(var11).isCollisionShapeFullBlock(var0, var11)
            && !var0.getBlockState(var12).isCollisionShapeFullBlock(var0, var12)) {
            double var13 = var9.distToCenterSqr(0.0, 0.0, 0.0);
            if (var5 == null || var13 < var6) {
               var5 = var9;
               var6 = var13;
            }
         }
      }

      return var5;
   }

   private static void spawnGatewayPortal(ServerLevel var0, BlockPos var1, EndGatewayConfiguration var2) {
      Feature.END_GATEWAY.place(var2, var0, var0.getChunkSource().getGenerator(), RandomSource.create(), var1);
   }

   @Override
   public boolean shouldRenderFace(Direction var1) {
      return Block.shouldRenderFace(this.getBlockState(), this.level, this.getBlockPos(), var1, this.getBlockPos().relative(var1));
   }

   public int getParticleAmount() {
      int var1 = 0;

      for(Direction var5 : Direction.values()) {
         var1 += this.shouldRenderFace(var5) ? 1 : 0;
      }

      return var1;
   }

   public void setExitPosition(BlockPos var1, boolean var2) {
      this.exactTeleport = var2;
      this.exitPortal = var1;
   }
}

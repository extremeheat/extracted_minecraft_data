package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.EndFeatures;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      this.age = var1.getLong("Age");
      NbtUtils.readBlockPos(var1, "exit_portal").filter(Level::isInSpawnableBounds).ifPresent((var1x) -> {
         this.exitPortal = var1x;
      });
      this.exactTeleport = var1.getBoolean("ExactTeleport");
   }

   public static void beamAnimationTick(Level var0, BlockPos var1, BlockState var2, TheEndGatewayBlockEntity var3) {
      ++var3.age;
      if (var3.isCoolingDown()) {
         --var3.teleportCooldown;
      }

   }

   public static void portalTick(Level var0, BlockPos var1, BlockState var2, TheEndGatewayBlockEntity var3) {
      boolean var4 = var3.isSpawning();
      boolean var5 = var3.isCoolingDown();
      ++var3.age;
      if (var5) {
         --var3.teleportCooldown;
      } else if (var3.age % 2400L == 0L) {
         triggerCooldown(var0, var1, var2, var3);
      }

      if (var4 != var3.isSpawning() || var5 != var3.isCoolingDown()) {
         setChanged(var0, var1, var2);
      }

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

   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.saveCustomOnly(var1);
   }

   public static void triggerCooldown(Level var0, BlockPos var1, BlockState var2, TheEndGatewayBlockEntity var3) {
      if (!var0.isClientSide) {
         var3.teleportCooldown = 40;
         var0.blockEvent(var1, var2.getBlock(), 1, 0);
         setChanged(var0, var1, var2);
      }

   }

   public boolean triggerEvent(int var1, int var2) {
      if (var1 == 1) {
         this.teleportCooldown = 40;
         return true;
      } else {
         return super.triggerEvent(var1, var2);
      }
   }

   @Nullable
   public Vec3 getPortalPosition(ServerLevel var1, BlockPos var2) {
      BlockPos var3;
      if (this.exitPortal == null && var1.dimension() == Level.END) {
         var3 = findOrCreateValidTeleportPos(var1, var2);
         var3 = var3.above(10);
         LOGGER.debug("Creating portal at {}", var3);
         spawnGatewayPortal(var1, var3, EndGatewayConfiguration.knownExit(var2, false));
         this.setExitPosition(var3, this.exactTeleport);
      }

      if (this.exitPortal != null) {
         var3 = this.exactTeleport ? this.exitPortal : findExitPosition(var1, this.exitPortal);
         return var3.getBottomCenter();
      } else {
         return null;
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
         var0.registryAccess().lookup(Registries.CONFIGURED_FEATURE).flatMap((var0x) -> {
            return var0x.get(EndFeatures.END_ISLAND);
         }).ifPresent((var2x) -> {
            ((ConfiguredFeature)var2x.value()).place(var0, var0.getChunkSource().getGenerator(), RandomSource.create(var5.asLong()), var5);
         });
         var4 = var5;
      } else {
         LOGGER.debug("Found suitable block to teleport to: {}", var4);
      }

      return findTallestBlock(var0, var4, 16, true);
   }

   private static Vec3 findExitPortalXZPosTentative(ServerLevel var0, BlockPos var1) {
      Vec3 var2 = (new Vec3((double)var1.getX(), 0.0, (double)var1.getZ())).normalize();
      boolean var3 = true;
      Vec3 var4 = var2.scale(1024.0);

      int var5;
      for(var5 = 16; !isChunkEmpty(var0, var4) && var5-- > 0; var4 = var4.add(var2.scale(-16.0))) {
         LOGGER.debug("Skipping backwards past nonempty chunk at {}", var4);
      }

      for(var5 = 16; isChunkEmpty(var0, var4) && var5-- > 0; var4 = var4.add(var2.scale(16.0))) {
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
               for(int var7 = var0.getMaxY(); var7 > (var4 == null ? var0.getMinY() : var4.getY()); --var7) {
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
      Iterator var8 = BlockPos.betweenClosed(var2, var4).iterator();

      while(true) {
         BlockPos var9;
         double var13;
         do {
            BlockPos var11;
            BlockPos var12;
            do {
               BlockState var10;
               do {
                  do {
                     if (!var8.hasNext()) {
                        return var5;
                     }

                     var9 = (BlockPos)var8.next();
                     var10 = var0.getBlockState(var9);
                     var11 = var9.above();
                     var12 = var9.above(2);
                  } while(!var10.is(Blocks.END_STONE));
               } while(var0.getBlockState(var11).isCollisionShapeFullBlock(var0, var11));
            } while(var0.getBlockState(var12).isCollisionShapeFullBlock(var0, var12));

            var13 = var9.distToCenterSqr(0.0, 0.0, 0.0);
         } while(var5 != null && !(var13 < var6));

         var5 = var9;
         var6 = var13;
      }
   }

   private static void spawnGatewayPortal(ServerLevel var0, BlockPos var1, EndGatewayConfiguration var2) {
      Feature.END_GATEWAY.place(var2, var0, var0.getChunkSource().getGenerator(), RandomSource.create(), var1);
   }

   public boolean shouldRenderFace(Direction var1) {
      return Block.shouldRenderFace(this.getBlockState(), this.level.getBlockState(this.getBlockPos().relative(var1)), var1);
   }

   public int getParticleAmount() {
      int var1 = 0;
      Direction[] var2 = Direction.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction var5 = var2[var4];
         var1 += this.shouldRenderFace(var5) ? 1 : 0;
      }

      return var1;
   }

   public void setExitPosition(BlockPos var1, boolean var2) {
      this.exactTeleport = var2;
      this.exitPortal = var1;
      this.setChanged();
   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}

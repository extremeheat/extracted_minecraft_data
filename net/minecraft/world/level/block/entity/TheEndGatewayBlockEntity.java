package net.minecraft.world.level.block.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.end.TheEndDimension;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.EndGatewayConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TheEndGatewayBlockEntity extends TheEndPortalBlockEntity implements TickableBlockEntity {
   private static final Logger LOGGER = LogManager.getLogger();
   private long age;
   private int teleportCooldown;
   @Nullable
   private BlockPos exitPortal;
   private boolean exactTeleport;

   public TheEndGatewayBlockEntity() {
      super(BlockEntityType.END_GATEWAY);
   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
      var1.putLong("Age", this.age);
      if (this.exitPortal != null) {
         var1.put("ExitPortal", NbtUtils.writeBlockPos(this.exitPortal));
      }

      if (this.exactTeleport) {
         var1.putBoolean("ExactTeleport", this.exactTeleport);
      }

      return var1;
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      this.age = var1.getLong("Age");
      if (var1.contains("ExitPortal", 10)) {
         this.exitPortal = NbtUtils.readBlockPos(var1.getCompound("ExitPortal"));
      }

      this.exactTeleport = var1.getBoolean("ExactTeleport");
   }

   public double getViewDistance() {
      return 65536.0D;
   }

   public void tick() {
      boolean var1 = this.isSpawning();
      boolean var2 = this.isCoolingDown();
      ++this.age;
      if (var2) {
         --this.teleportCooldown;
      } else if (!this.level.isClientSide) {
         List var3 = this.level.getEntitiesOfClass(Entity.class, new AABB(this.getBlockPos()));
         if (!var3.isEmpty()) {
            this.teleportEntity(((Entity)var3.get(0)).getRootVehicle());
         }

         if (this.age % 2400L == 0L) {
            this.triggerCooldown();
         }
      }

      if (var1 != this.isSpawning() || var2 != this.isCoolingDown()) {
         this.setChanged();
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

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return new ClientboundBlockEntityDataPacket(this.worldPosition, 8, this.getUpdateTag());
   }

   public CompoundTag getUpdateTag() {
      return this.save(new CompoundTag());
   }

   public void triggerCooldown() {
      if (!this.level.isClientSide) {
         this.teleportCooldown = 40;
         this.level.blockEvent(this.getBlockPos(), this.getBlockState().getBlock(), 1, 0);
         this.setChanged();
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

   public void teleportEntity(Entity var1) {
      if (this.level instanceof ServerLevel && !this.isCoolingDown()) {
         this.teleportCooldown = 100;
         if (this.exitPortal == null && this.level.dimension instanceof TheEndDimension) {
            this.findExitPortal((ServerLevel)this.level);
         }

         if (this.exitPortal != null) {
            BlockPos var2 = this.exactTeleport ? this.exitPortal : this.findExitPosition();
            var1.teleportToWithTicket((double)var2.getX() + 0.5D, (double)var2.getY() + 0.5D, (double)var2.getZ() + 0.5D);
         }

         this.triggerCooldown();
      }
   }

   private BlockPos findExitPosition() {
      BlockPos var1 = findTallestBlock(this.level, this.exitPortal, 5, false);
      LOGGER.debug("Best exit position for portal at {} is {}", this.exitPortal, var1);
      return var1.above();
   }

   private void findExitPortal(ServerLevel var1) {
      Vec3 var2 = (new Vec3((double)this.getBlockPos().getX(), 0.0D, (double)this.getBlockPos().getZ())).normalize();
      Vec3 var3 = var2.scale(1024.0D);

      int var4;
      for(var4 = 16; getChunk(var1, var3).getHighestSectionPosition() > 0 && var4-- > 0; var3 = var3.add(var2.scale(-16.0D))) {
         LOGGER.debug("Skipping backwards past nonempty chunk at {}", var3);
      }

      for(var4 = 16; getChunk(var1, var3).getHighestSectionPosition() == 0 && var4-- > 0; var3 = var3.add(var2.scale(16.0D))) {
         LOGGER.debug("Skipping forward past empty chunk at {}", var3);
      }

      LOGGER.debug("Found chunk at {}", var3);
      LevelChunk var5 = getChunk(var1, var3);
      this.exitPortal = findValidSpawnInChunk(var5);
      if (this.exitPortal == null) {
         this.exitPortal = new BlockPos(var3.x + 0.5D, 75.0D, var3.z + 0.5D);
         LOGGER.debug("Failed to find suitable block, settling on {}", this.exitPortal);
         Feature.END_ISLAND.configured(FeatureConfiguration.NONE).place(var1, var1.getChunkSource().getGenerator(), new Random(this.exitPortal.asLong()), this.exitPortal);
      } else {
         LOGGER.debug("Found block at {}", this.exitPortal);
      }

      this.exitPortal = findTallestBlock(var1, this.exitPortal, 16, true);
      LOGGER.debug("Creating portal at {}", this.exitPortal);
      this.exitPortal = this.exitPortal.above(10);
      this.createExitPortal(var1, this.exitPortal);
      this.setChanged();
   }

   private static BlockPos findTallestBlock(BlockGetter var0, BlockPos var1, int var2, boolean var3) {
      BlockPos var4 = null;

      for(int var5 = -var2; var5 <= var2; ++var5) {
         for(int var6 = -var2; var6 <= var2; ++var6) {
            if (var5 != 0 || var6 != 0 || var3) {
               for(int var7 = 255; var7 > (var4 == null ? 0 : var4.getY()); --var7) {
                  BlockPos var8 = new BlockPos(var1.getX() + var5, var7, var1.getZ() + var6);
                  BlockState var9 = var0.getBlockState(var8);
                  if (var9.isCollisionShapeFullBlock(var0, var8) && (var3 || var9.getBlock() != Blocks.BEDROCK)) {
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
      return var0.getChunk(Mth.floor(var1.x / 16.0D), Mth.floor(var1.z / 16.0D));
   }

   @Nullable
   private static BlockPos findValidSpawnInChunk(LevelChunk var0) {
      ChunkPos var1 = var0.getPos();
      BlockPos var2 = new BlockPos(var1.getMinBlockX(), 30, var1.getMinBlockZ());
      int var3 = var0.getHighestSectionPosition() + 16 - 1;
      BlockPos var4 = new BlockPos(var1.getMaxBlockX(), var3, var1.getMaxBlockZ());
      BlockPos var5 = null;
      double var6 = 0.0D;
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
                  } while(var10.getBlock() != Blocks.END_STONE);
               } while(var0.getBlockState(var11).isCollisionShapeFullBlock(var0, var11));
            } while(var0.getBlockState(var12).isCollisionShapeFullBlock(var0, var12));

            var13 = var9.distSqr(0.0D, 0.0D, 0.0D, true);
         } while(var5 != null && var13 >= var6);

         var5 = var9;
         var6 = var13;
      }
   }

   private void createExitPortal(ServerLevel var1, BlockPos var2) {
      Feature.END_GATEWAY.configured(EndGatewayConfiguration.knownExit(this.getBlockPos(), false)).place(var1, var1.getChunkSource().getGenerator(), new Random(), var2);
   }

   public boolean shouldRenderFace(Direction var1) {
      return Block.shouldRenderFace(this.getBlockState(), this.level, this.getBlockPos(), var1);
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
   }
}

package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.LevelStem;
import org.slf4j.Logger;

public class MineTravellingBlockEntity extends BlockEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int SPAWN_TIME = 200;
   private long age = 0L;
   private ResourceKey<LevelStem> dimension;
   private boolean revisit;

   public MineTravellingBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.MINE_TRAVELLING_BLOCK_ENTITY, var1, var2);
      this.dimension = LevelStem.OVERWORLD;
   }

   public long age() {
      return this.age;
   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      var1.putLong("age", this.age);
      var1.putBoolean("revisit", this.revisit);
      var1.store("dimension", ResourceKey.codec(Registries.LEVEL_STEM), this.dimension);
   }

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      this.age = var1.getLongOr("age", 0L);
      this.revisit = var1.getBooleanOr("revisit", false);
      this.dimension = (ResourceKey)var1.read("dimension", ResourceKey.codec(Registries.LEVEL_STEM)).orElse(LevelStem.OVERWORLD);
   }

   public void setDimension(ResourceKey<LevelStem> var1) {
      this.dimension = var1;
   }

   public void setRevisitBlock(boolean var1) {
      this.revisit = var1;
   }

   public static void gatewayAnimationTick(Level var0, BlockPos var1, BlockState var2, MineTravellingBlockEntity var3) {
      ++var3.age;
   }

   public static void portalTick(Level var0, BlockPos var1, BlockState var2, MineTravellingBlockEntity var3) {
      boolean var4 = var3.isSpawning();
      ++var3.age;
      if (var4 != var3.isSpawning()) {
         setChanged(var0, var1, var2);
      }

   }

   public boolean isSpawning() {
      return this.age < 200L;
   }

   public boolean shouldPeriodicallyBeam() {
      return (int)this.age / 200 % 6 == 0;
   }

   public float periodicallyBeam(float var1) {
      float var2 = ((float)this.age + var1) % 200.0F;
      return Mth.clamp(var2 / 200.0F, 0.0F, 1.0F);
   }

   public float getSpawnPercent(float var1) {
      return Mth.clamp(((float)this.age + var1) / 200.0F, 0.0F, 1.0F);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.saveCustomOnly(var1);
   }

   public boolean shouldRenderFace(Direction var1) {
      return Block.shouldRenderFace(this.getBlockState(), this.level.getBlockState(this.getBlockPos().relative(var1)), var1);
   }

   public int getParticleAmount() {
      int var1 = 0;

      for(Direction var5 : Direction.values()) {
         var1 += this.shouldRenderFace(var5) ? 1 : 0;
      }

      return var1;
   }

   public ResourceKey<Level> getTargetDimension() {
      return Registries.levelStemToLevel(this.dimension);
   }

   public boolean isRevisitBlock() {
      return this.revisit;
   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}

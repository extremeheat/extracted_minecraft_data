package net.minecraft.world.grid;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SubGrid implements BlockGetter, CollisionGetter {
   protected final Level level;
   protected final GridCarrier carrier;
   private SubGridBlocks blocks = new SubGridBlocks(0, 0, 0);
   protected Holder<Biome> biome;
   private AABB boundingBox = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

   public SubGrid(Level var1, GridCarrier var2) {
      super();
      this.level = var1;
      this.carrier = var2;
      this.biome = var1.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.PLAINS);
      this.updatePosition(var2.getX(), var2.getY(), var2.getZ());
   }

   public void updatePosition(double var1, double var3, double var5) {
      this.boundingBox = new AABB(
         var1, var3, var5, var1 + (double)this.blocks.sizeX() + 1.0, var3 + (double)this.blocks.sizeY() + 1.0, var5 + (double)this.blocks.sizeZ() + 1.0
      );
   }

   public void setBlocks(SubGridBlocks var1) {
      this.blocks = var1;
      this.updatePosition(this.carrier.getX(), this.carrier.getY(), this.carrier.getZ());
   }

   public void setBiome(Holder<Biome> var1) {
      this.biome = var1;
   }

   public Level level() {
      return this.level;
   }

   @Override
   public BlockState getBlockState(BlockPos var1) {
      return this.blocks.getBlockState(var1);
   }

   @Override
   public FluidState getFluidState(BlockPos var1) {
      return this.getBlockState(var1).getFluidState();
   }

   @Override
   public boolean isPotato() {
      return false;
   }

   @Nullable
   @Override
   public BlockEntity getBlockEntity(BlockPos var1) {
      return null;
   }

   @Override
   public int getHeight() {
      return this.blocks.sizeY();
   }

   @Override
   public int getMinBuildHeight() {
      return 0;
   }

   public UUID id() {
      return this.carrier.getUUID();
   }

   public GridCarrier carrier() {
      return this.carrier;
   }

   public SubGridBlocks getBlocks() {
      return this.blocks;
   }

   public Holder<Biome> getBiome() {
      return this.biome;
   }

   public AABB getNextBoundingBox() {
      return this.boundingBox;
   }

   public AABB getKnownBoundingBox() {
      Vec3 var1 = this.getLastMovement();
      return this.boundingBox.move(-var1.x, -var1.y, -var1.z);
   }

   @Override
   public WorldBorder getWorldBorder() {
      return this.level.getWorldBorder();
   }

   @Nullable
   @Override
   public BlockGetter getChunkForCollisions(int var1, int var2) {
      return this;
   }

   @Override
   public List<VoxelShape> getEntityCollisions(@Nullable Entity var1, AABB var2) {
      return List.of();
   }

   public Vec3 getLastMovement() {
      return new Vec3(this.carrier.getX() - this.carrier.xOld, this.carrier.getY() - this.carrier.yOld, this.carrier.getZ() - this.carrier.zOld);
   }
}

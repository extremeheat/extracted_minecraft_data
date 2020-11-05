package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.material.FluidState;

public abstract class StructurePiece {
   protected static final BlockState CAVE_AIR;
   protected BoundingBox boundingBox;
   @Nullable
   private Direction orientation;
   private Mirror mirror;
   private Rotation rotation;
   protected int genDepth;
   private final StructurePieceType type;
   private static final Set<Block> SHAPE_CHECK_BLOCKS;

   protected StructurePiece(StructurePieceType var1, int var2) {
      super();
      this.type = var1;
      this.genDepth = var2;
   }

   public StructurePiece(StructurePieceType var1, CompoundTag var2) {
      this(var1, var2.getInt("GD"));
      if (var2.contains("BB")) {
         this.boundingBox = new BoundingBox(var2.getIntArray("BB"));
      }

      int var3 = var2.getInt("O");
      this.setOrientation(var3 == -1 ? null : Direction.from2DDataValue(var3));
   }

   public final CompoundTag createTag() {
      CompoundTag var1 = new CompoundTag();
      var1.putString("id", Registry.STRUCTURE_PIECE.getKey(this.getType()).toString());
      var1.put("BB", this.boundingBox.createTag());
      Direction var2 = this.getOrientation();
      var1.putInt("O", var2 == null ? -1 : var2.get2DDataValue());
      var1.putInt("GD", this.genDepth);
      this.addAdditionalSaveData(var1);
      return var1;
   }

   protected abstract void addAdditionalSaveData(CompoundTag var1);

   public void addChildren(StructurePiece var1, List<StructurePiece> var2, Random var3) {
   }

   public abstract boolean postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7);

   public BoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   public int getGenDepth() {
      return this.genDepth;
   }

   public boolean isCloseToChunk(ChunkPos var1, int var2) {
      int var3 = SectionPos.sectionToBlockCoord(var1.x);
      int var4 = SectionPos.sectionToBlockCoord(var1.z);
      return this.boundingBox.intersects(var3 - var2, var4 - var2, var3 + 15 + var2, var4 + 15 + var2);
   }

   public static StructurePiece findCollisionPiece(List<StructurePiece> var0, BoundingBox var1) {
      Iterator var2 = var0.iterator();

      StructurePiece var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (StructurePiece)var2.next();
      } while(var3.getBoundingBox() == null || !var3.getBoundingBox().intersects(var1));

      return var3;
   }

   protected boolean edgesLiquid(BlockGetter var1, BoundingBox var2) {
      int var3 = Math.max(this.boundingBox.x0 - 1, var2.x0);
      int var4 = Math.max(this.boundingBox.y0 - 1, var2.y0);
      int var5 = Math.max(this.boundingBox.z0 - 1, var2.z0);
      int var6 = Math.min(this.boundingBox.x1 + 1, var2.x1);
      int var7 = Math.min(this.boundingBox.y1 + 1, var2.y1);
      int var8 = Math.min(this.boundingBox.z1 + 1, var2.z1);
      BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

      int var10;
      int var11;
      for(var10 = var3; var10 <= var6; ++var10) {
         for(var11 = var5; var11 <= var8; ++var11) {
            if (var1.getBlockState(var9.set(var10, var4, var11)).getMaterial().isLiquid()) {
               return true;
            }

            if (var1.getBlockState(var9.set(var10, var7, var11)).getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      for(var10 = var3; var10 <= var6; ++var10) {
         for(var11 = var4; var11 <= var7; ++var11) {
            if (var1.getBlockState(var9.set(var10, var11, var5)).getMaterial().isLiquid()) {
               return true;
            }

            if (var1.getBlockState(var9.set(var10, var11, var8)).getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      for(var10 = var5; var10 <= var8; ++var10) {
         for(var11 = var4; var11 <= var7; ++var11) {
            if (var1.getBlockState(var9.set(var3, var11, var10)).getMaterial().isLiquid()) {
               return true;
            }

            if (var1.getBlockState(var9.set(var6, var11, var10)).getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      return false;
   }

   protected int getWorldX(int var1, int var2) {
      Direction var3 = this.getOrientation();
      if (var3 == null) {
         return var1;
      } else {
         switch(var3) {
         case NORTH:
         case SOUTH:
            return this.boundingBox.x0 + var1;
         case WEST:
            return this.boundingBox.x1 - var2;
         case EAST:
            return this.boundingBox.x0 + var2;
         default:
            return var1;
         }
      }
   }

   protected int getWorldY(int var1) {
      return this.getOrientation() == null ? var1 : var1 + this.boundingBox.y0;
   }

   protected int getWorldZ(int var1, int var2) {
      Direction var3 = this.getOrientation();
      if (var3 == null) {
         return var2;
      } else {
         switch(var3) {
         case NORTH:
            return this.boundingBox.z1 - var2;
         case SOUTH:
            return this.boundingBox.z0 + var2;
         case WEST:
         case EAST:
            return this.boundingBox.z0 + var1;
         default:
            return var2;
         }
      }
   }

   protected void placeBlock(WorldGenLevel var1, BlockState var2, int var3, int var4, int var5, BoundingBox var6) {
      BlockPos var7 = new BlockPos(this.getWorldX(var3, var5), this.getWorldY(var4), this.getWorldZ(var3, var5));
      if (var6.isInside(var7)) {
         if (this.mirror != Mirror.NONE) {
            var2 = var2.mirror(this.mirror);
         }

         if (this.rotation != Rotation.NONE) {
            var2 = var2.rotate(this.rotation);
         }

         var1.setBlock(var7, var2, 2);
         FluidState var8 = var1.getFluidState(var7);
         if (!var8.isEmpty()) {
            var1.getLiquidTicks().scheduleTick(var7, var8.getType(), 0);
         }

         if (SHAPE_CHECK_BLOCKS.contains(var2.getBlock())) {
            var1.getChunk(var7).markPosForPostprocessing(var7);
         }

      }
   }

   protected BlockState getBlock(BlockGetter var1, int var2, int var3, int var4, BoundingBox var5) {
      int var6 = this.getWorldX(var2, var4);
      int var7 = this.getWorldY(var3);
      int var8 = this.getWorldZ(var2, var4);
      BlockPos var9 = new BlockPos(var6, var7, var8);
      return !var5.isInside(var9) ? Blocks.AIR.defaultBlockState() : var1.getBlockState(var9);
   }

   protected boolean isInterior(LevelReader var1, int var2, int var3, int var4, BoundingBox var5) {
      int var6 = this.getWorldX(var2, var4);
      int var7 = this.getWorldY(var3 + 1);
      int var8 = this.getWorldZ(var2, var4);
      BlockPos var9 = new BlockPos(var6, var7, var8);
      if (!var5.isInside(var9)) {
         return false;
      } else {
         return var7 < var1.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, var6, var8);
      }
   }

   protected void generateAirBox(WorldGenLevel var1, BoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      for(int var9 = var4; var9 <= var7; ++var9) {
         for(int var10 = var3; var10 <= var6; ++var10) {
            for(int var11 = var5; var11 <= var8; ++var11) {
               this.placeBlock(var1, Blocks.AIR.defaultBlockState(), var10, var9, var11, var2);
            }
         }
      }

   }

   protected void generateBox(WorldGenLevel var1, BoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8, BlockState var9, BlockState var10, boolean var11) {
      for(int var12 = var4; var12 <= var7; ++var12) {
         for(int var13 = var3; var13 <= var6; ++var13) {
            for(int var14 = var5; var14 <= var8; ++var14) {
               if (!var11 || !this.getBlock(var1, var13, var12, var14, var2).isAir()) {
                  if (var12 != var4 && var12 != var7 && var13 != var3 && var13 != var6 && var14 != var5 && var14 != var8) {
                     this.placeBlock(var1, var10, var13, var12, var14, var2);
                  } else {
                     this.placeBlock(var1, var9, var13, var12, var14, var2);
                  }
               }
            }
         }
      }

   }

   protected void generateBox(WorldGenLevel var1, BoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, Random var10, StructurePiece.BlockSelector var11) {
      for(int var12 = var4; var12 <= var7; ++var12) {
         for(int var13 = var3; var13 <= var6; ++var13) {
            for(int var14 = var5; var14 <= var8; ++var14) {
               if (!var9 || !this.getBlock(var1, var13, var12, var14, var2).isAir()) {
                  var11.next(var10, var13, var12, var14, var12 == var4 || var12 == var7 || var13 == var3 || var13 == var6 || var14 == var5 || var14 == var8);
                  this.placeBlock(var1, var11.getNext(), var13, var12, var14, var2);
               }
            }
         }
      }

   }

   protected void generateMaybeBox(WorldGenLevel var1, BoundingBox var2, Random var3, float var4, int var5, int var6, int var7, int var8, int var9, int var10, BlockState var11, BlockState var12, boolean var13, boolean var14) {
      for(int var15 = var6; var15 <= var9; ++var15) {
         for(int var16 = var5; var16 <= var8; ++var16) {
            for(int var17 = var7; var17 <= var10; ++var17) {
               if (var3.nextFloat() <= var4 && (!var13 || !this.getBlock(var1, var16, var15, var17, var2).isAir()) && (!var14 || this.isInterior(var1, var16, var15, var17, var2))) {
                  if (var15 != var6 && var15 != var9 && var16 != var5 && var16 != var8 && var17 != var7 && var17 != var10) {
                     this.placeBlock(var1, var12, var16, var15, var17, var2);
                  } else {
                     this.placeBlock(var1, var11, var16, var15, var17, var2);
                  }
               }
            }
         }
      }

   }

   protected void maybeGenerateBlock(WorldGenLevel var1, BoundingBox var2, Random var3, float var4, int var5, int var6, int var7, BlockState var8) {
      if (var3.nextFloat() < var4) {
         this.placeBlock(var1, var8, var5, var6, var7, var2);
      }

   }

   protected void generateUpperHalfSphere(WorldGenLevel var1, BoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8, BlockState var9, boolean var10) {
      float var11 = (float)(var6 - var3 + 1);
      float var12 = (float)(var7 - var4 + 1);
      float var13 = (float)(var8 - var5 + 1);
      float var14 = (float)var3 + var11 / 2.0F;
      float var15 = (float)var5 + var13 / 2.0F;

      for(int var16 = var4; var16 <= var7; ++var16) {
         float var17 = (float)(var16 - var4) / var12;

         for(int var18 = var3; var18 <= var6; ++var18) {
            float var19 = ((float)var18 - var14) / (var11 * 0.5F);

            for(int var20 = var5; var20 <= var8; ++var20) {
               float var21 = ((float)var20 - var15) / (var13 * 0.5F);
               if (!var10 || !this.getBlock(var1, var18, var16, var20, var2).isAir()) {
                  float var22 = var19 * var19 + var17 * var17 + var21 * var21;
                  if (var22 <= 1.05F) {
                     this.placeBlock(var1, var9, var18, var16, var20, var2);
                  }
               }
            }
         }
      }

   }

   protected void fillColumnDown(WorldGenLevel var1, BlockState var2, int var3, int var4, int var5, BoundingBox var6) {
      int var7 = this.getWorldX(var3, var5);
      int var8 = this.getWorldY(var4);
      int var9 = this.getWorldZ(var3, var5);
      if (var6.isInside(new BlockPos(var7, var8, var9))) {
         while((var1.isEmptyBlock(new BlockPos(var7, var8, var9)) || var1.getBlockState(new BlockPos(var7, var8, var9)).getMaterial().isLiquid()) && var8 > var1.getMinBuildHeight() + 1) {
            var1.setBlock(new BlockPos(var7, var8, var9), var2, 2);
            --var8;
         }

      }
   }

   protected boolean createChest(WorldGenLevel var1, BoundingBox var2, Random var3, int var4, int var5, int var6, ResourceLocation var7) {
      BlockPos var8 = new BlockPos(this.getWorldX(var4, var6), this.getWorldY(var5), this.getWorldZ(var4, var6));
      return this.createChest(var1, var2, var3, var8, var7, (BlockState)null);
   }

   public static BlockState reorient(BlockGetter var0, BlockPos var1, BlockState var2) {
      Direction var3 = null;
      Iterator var4 = Direction.Plane.HORIZONTAL.iterator();

      while(var4.hasNext()) {
         Direction var5 = (Direction)var4.next();
         BlockPos var6 = var1.relative(var5);
         BlockState var7 = var0.getBlockState(var6);
         if (var7.is(Blocks.CHEST)) {
            return var2;
         }

         if (var7.isSolidRender(var0, var6)) {
            if (var3 != null) {
               var3 = null;
               break;
            }

            var3 = var5;
         }
      }

      if (var3 != null) {
         return (BlockState)var2.setValue(HorizontalDirectionalBlock.FACING, var3.getOpposite());
      } else {
         Direction var8 = (Direction)var2.getValue(HorizontalDirectionalBlock.FACING);
         BlockPos var9 = var1.relative(var8);
         if (var0.getBlockState(var9).isSolidRender(var0, var9)) {
            var8 = var8.getOpposite();
            var9 = var1.relative(var8);
         }

         if (var0.getBlockState(var9).isSolidRender(var0, var9)) {
            var8 = var8.getClockWise();
            var9 = var1.relative(var8);
         }

         if (var0.getBlockState(var9).isSolidRender(var0, var9)) {
            var8 = var8.getOpposite();
            var1.relative(var8);
         }

         return (BlockState)var2.setValue(HorizontalDirectionalBlock.FACING, var8);
      }
   }

   protected boolean createChest(ServerLevelAccessor var1, BoundingBox var2, Random var3, BlockPos var4, ResourceLocation var5, @Nullable BlockState var6) {
      if (var2.isInside(var4) && !var1.getBlockState(var4).is(Blocks.CHEST)) {
         if (var6 == null) {
            var6 = reorient(var1, var4, Blocks.CHEST.defaultBlockState());
         }

         var1.setBlock(var4, var6, 2);
         BlockEntity var7 = var1.getBlockEntity(var4);
         if (var7 instanceof ChestBlockEntity) {
            ((ChestBlockEntity)var7).setLootTable(var5, var3.nextLong());
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean createDispenser(WorldGenLevel var1, BoundingBox var2, Random var3, int var4, int var5, int var6, Direction var7, ResourceLocation var8) {
      BlockPos var9 = new BlockPos(this.getWorldX(var4, var6), this.getWorldY(var5), this.getWorldZ(var4, var6));
      if (var2.isInside(var9) && !var1.getBlockState(var9).is(Blocks.DISPENSER)) {
         this.placeBlock(var1, (BlockState)Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, var7), var4, var5, var6, var2);
         BlockEntity var10 = var1.getBlockEntity(var9);
         if (var10 instanceof DispenserBlockEntity) {
            ((DispenserBlockEntity)var10).setLootTable(var8, var3.nextLong());
         }

         return true;
      } else {
         return false;
      }
   }

   public void move(int var1, int var2, int var3) {
      this.boundingBox.move(var1, var2, var3);
   }

   @Nullable
   public Direction getOrientation() {
      return this.orientation;
   }

   public void setOrientation(@Nullable Direction var1) {
      this.orientation = var1;
      if (var1 == null) {
         this.rotation = Rotation.NONE;
         this.mirror = Mirror.NONE;
      } else {
         switch(var1) {
         case SOUTH:
            this.mirror = Mirror.LEFT_RIGHT;
            this.rotation = Rotation.NONE;
            break;
         case WEST:
            this.mirror = Mirror.LEFT_RIGHT;
            this.rotation = Rotation.CLOCKWISE_90;
            break;
         case EAST:
            this.mirror = Mirror.NONE;
            this.rotation = Rotation.CLOCKWISE_90;
            break;
         default:
            this.mirror = Mirror.NONE;
            this.rotation = Rotation.NONE;
         }
      }

   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public StructurePieceType getType() {
      return this.type;
   }

   static {
      CAVE_AIR = Blocks.CAVE_AIR.defaultBlockState();
      SHAPE_CHECK_BLOCKS = ImmutableSet.builder().add(Blocks.NETHER_BRICK_FENCE).add(Blocks.TORCH).add(Blocks.WALL_TORCH).add(Blocks.OAK_FENCE).add(Blocks.SPRUCE_FENCE).add(Blocks.DARK_OAK_FENCE).add(Blocks.ACACIA_FENCE).add(Blocks.BIRCH_FENCE).add(Blocks.JUNGLE_FENCE).add(Blocks.LADDER).add(Blocks.IRON_BARS).build();
   }

   public abstract static class BlockSelector {
      protected BlockState next;

      protected BlockSelector() {
         super();
         this.next = Blocks.AIR.defaultBlockState();
      }

      public abstract void next(Random var1, int var2, int var3, int var4, boolean var5);

      public BlockState getNext() {
         return this.next;
      }
   }
}

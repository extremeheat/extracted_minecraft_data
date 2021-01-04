package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class IglooPieces {
   private static final ResourceLocation STRUCTURE_LOCATION_IGLOO = new ResourceLocation("igloo/top");
   private static final ResourceLocation STRUCTURE_LOCATION_LADDER = new ResourceLocation("igloo/middle");
   private static final ResourceLocation STRUCTURE_LOCATION_LABORATORY = new ResourceLocation("igloo/bottom");
   private static final Map<ResourceLocation, BlockPos> PIVOTS;
   private static final Map<ResourceLocation, BlockPos> OFFSETS;

   public static void addPieces(StructureManager var0, BlockPos var1, Rotation var2, List<StructurePiece> var3, Random var4, NoneFeatureConfiguration var5) {
      if (var4.nextDouble() < 0.5D) {
         int var6 = var4.nextInt(8) + 4;
         var3.add(new IglooPieces.IglooPiece(var0, STRUCTURE_LOCATION_LABORATORY, var1, var2, var6 * 3));

         for(int var7 = 0; var7 < var6 - 1; ++var7) {
            var3.add(new IglooPieces.IglooPiece(var0, STRUCTURE_LOCATION_LADDER, var1, var2, var7 * 3));
         }
      }

      var3.add(new IglooPieces.IglooPiece(var0, STRUCTURE_LOCATION_IGLOO, var1, var2, 0));
   }

   static {
      PIVOTS = ImmutableMap.of(STRUCTURE_LOCATION_IGLOO, new BlockPos(3, 5, 5), STRUCTURE_LOCATION_LADDER, new BlockPos(1, 3, 1), STRUCTURE_LOCATION_LABORATORY, new BlockPos(3, 6, 7));
      OFFSETS = ImmutableMap.of(STRUCTURE_LOCATION_IGLOO, BlockPos.ZERO, STRUCTURE_LOCATION_LADDER, new BlockPos(2, -3, 4), STRUCTURE_LOCATION_LABORATORY, new BlockPos(0, -3, -2));
   }

   public static class IglooPiece extends TemplateStructurePiece {
      private final ResourceLocation templateLocation;
      private final Rotation rotation;

      public IglooPiece(StructureManager var1, ResourceLocation var2, BlockPos var3, Rotation var4, int var5) {
         super(StructurePieceType.IGLOO, 0);
         this.templateLocation = var2;
         BlockPos var6 = (BlockPos)IglooPieces.OFFSETS.get(var2);
         this.templatePosition = var3.offset(var6.getX(), var6.getY() - var5, var6.getZ());
         this.rotation = var4;
         this.loadTemplate(var1);
      }

      public IglooPiece(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.IGLOO, var2);
         this.templateLocation = new ResourceLocation(var2.getString("Template"));
         this.rotation = Rotation.valueOf(var2.getString("Rot"));
         this.loadTemplate(var1);
      }

      private void loadTemplate(StructureManager var1) {
         StructureTemplate var2 = var1.getOrCreate(this.templateLocation);
         StructurePlaceSettings var3 = (new StructurePlaceSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).setRotationPivot((BlockPos)IglooPieces.PIVOTS.get(this.templateLocation)).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
         this.setup(var2, this.templatePosition, var3);
      }

      protected void addAdditionalSaveData(CompoundTag var1) {
         super.addAdditionalSaveData(var1);
         var1.putString("Template", this.templateLocation.toString());
         var1.putString("Rot", this.rotation.name());
      }

      protected void handleDataMarker(String var1, BlockPos var2, LevelAccessor var3, Random var4, BoundingBox var5) {
         if ("chest".equals(var1)) {
            var3.setBlock(var2, Blocks.AIR.defaultBlockState(), 3);
            BlockEntity var6 = var3.getBlockEntity(var2.below());
            if (var6 instanceof ChestBlockEntity) {
               ((ChestBlockEntity)var6).setLootTable(BuiltInLootTables.IGLOO_CHEST, var4.nextLong());
            }

         }
      }

      public boolean postProcess(LevelAccessor var1, Random var2, BoundingBox var3, ChunkPos var4) {
         StructurePlaceSettings var5 = (new StructurePlaceSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).setRotationPivot((BlockPos)IglooPieces.PIVOTS.get(this.templateLocation)).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
         BlockPos var6 = (BlockPos)IglooPieces.OFFSETS.get(this.templateLocation);
         BlockPos var7 = this.templatePosition.offset(StructureTemplate.calculateRelativePosition(var5, new BlockPos(3 - var6.getX(), 0, 0 - var6.getZ())));
         int var8 = var1.getHeight(Heightmap.Types.WORLD_SURFACE_WG, var7.getX(), var7.getZ());
         BlockPos var9 = this.templatePosition;
         this.templatePosition = this.templatePosition.offset(0, var8 - 90 - 1, 0);
         boolean var10 = super.postProcess(var1, var2, var3, var4);
         if (this.templateLocation.equals(IglooPieces.STRUCTURE_LOCATION_IGLOO)) {
            BlockPos var11 = this.templatePosition.offset(StructureTemplate.calculateRelativePosition(var5, new BlockPos(3, 0, 5)));
            BlockState var12 = var1.getBlockState(var11.below());
            if (!var12.isAir() && var12.getBlock() != Blocks.LADDER) {
               var1.setBlock(var11, Blocks.SNOW_BLOCK.defaultBlockState(), 3);
            }
         }

         this.templatePosition = var9;
         return var10;
      }
   }
}

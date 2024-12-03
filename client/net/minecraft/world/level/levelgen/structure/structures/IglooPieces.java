package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class IglooPieces {
   public static final int GENERATION_HEIGHT = 90;
   static final ResourceLocation STRUCTURE_LOCATION_IGLOO = ResourceLocation.withDefaultNamespace("igloo/top");
   private static final ResourceLocation STRUCTURE_LOCATION_LADDER = ResourceLocation.withDefaultNamespace("igloo/middle");
   private static final ResourceLocation STRUCTURE_LOCATION_LABORATORY = ResourceLocation.withDefaultNamespace("igloo/bottom");
   static final Map<ResourceLocation, BlockPos> PIVOTS;
   static final Map<ResourceLocation, BlockPos> OFFSETS;

   public IglooPieces() {
      super();
   }

   public static void addPieces(StructureTemplateManager var0, BlockPos var1, Rotation var2, StructurePieceAccessor var3, RandomSource var4) {
      if (var4.nextDouble() < 0.5) {
         int var5 = var4.nextInt(8) + 4;
         var3.addPiece(new IglooPiece(var0, STRUCTURE_LOCATION_LABORATORY, var1, var2, var5 * 3));

         for(int var6 = 0; var6 < var5 - 1; ++var6) {
            var3.addPiece(new IglooPiece(var0, STRUCTURE_LOCATION_LADDER, var1, var2, var6 * 3));
         }
      }

      var3.addPiece(new IglooPiece(var0, STRUCTURE_LOCATION_IGLOO, var1, var2, 0));
   }

   static {
      PIVOTS = ImmutableMap.of(STRUCTURE_LOCATION_IGLOO, new BlockPos(3, 5, 5), STRUCTURE_LOCATION_LADDER, new BlockPos(1, 3, 1), STRUCTURE_LOCATION_LABORATORY, new BlockPos(3, 6, 7));
      OFFSETS = ImmutableMap.of(STRUCTURE_LOCATION_IGLOO, BlockPos.ZERO, STRUCTURE_LOCATION_LADDER, new BlockPos(2, -3, 4), STRUCTURE_LOCATION_LABORATORY, new BlockPos(0, -3, -2));
   }

   public static class IglooPiece extends TemplateStructurePiece {
      public IglooPiece(StructureTemplateManager var1, ResourceLocation var2, BlockPos var3, Rotation var4, int var5) {
         super(StructurePieceType.IGLOO, 0, var1, var2, var2.toString(), makeSettings(var4, var2), makePosition(var2, var3, var5));
      }

      public IglooPiece(StructureTemplateManager var1, CompoundTag var2) {
         super(StructurePieceType.IGLOO, var2, var1, (var1x) -> makeSettings(Rotation.valueOf(var2.getString("Rot")), var1x));
      }

      private static StructurePlaceSettings makeSettings(Rotation var0, ResourceLocation var1) {
         return (new StructurePlaceSettings()).setRotation(var0).setMirror(Mirror.NONE).setRotationPivot((BlockPos)IglooPieces.PIVOTS.get(var1)).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK).setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING);
      }

      private static BlockPos makePosition(ResourceLocation var0, BlockPos var1, int var2) {
         return var1.offset((Vec3i)IglooPieces.OFFSETS.get(var0)).below(var2);
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         super.addAdditionalSaveData(var1, var2);
         var2.putString("Rot", this.placeSettings.getRotation().name());
      }

      protected void handleDataMarker(String var1, BlockPos var2, ServerLevelAccessor var3, RandomSource var4, BoundingBox var5) {
         if ("chest".equals(var1)) {
            var3.setBlock(var2, Blocks.AIR.defaultBlockState(), 3);
            BlockEntity var6 = var3.getBlockEntity(var2.below());
            if (var6 instanceof ChestBlockEntity) {
               ((ChestBlockEntity)var6).setLootTable(BuiltInLootTables.IGLOO_CHEST, var4.nextLong());
            }

         }
      }

      public void postProcess(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         ResourceLocation var8 = ResourceLocation.parse(this.templateName);
         StructurePlaceSettings var9 = makeSettings(this.placeSettings.getRotation(), var8);
         BlockPos var10 = (BlockPos)IglooPieces.OFFSETS.get(var8);
         BlockPos var11 = this.templatePosition.offset(StructureTemplate.calculateRelativePosition(var9, new BlockPos(3 - var10.getX(), 0, -var10.getZ())));
         int var12 = var1.getHeight(Heightmap.Types.WORLD_SURFACE_WG, var11.getX(), var11.getZ());
         BlockPos var13 = this.templatePosition;
         this.templatePosition = this.templatePosition.offset(0, var12 - 90 - 1, 0);
         super.postProcess(var1, var2, var3, var4, var5, var6, var7);
         if (var8.equals(IglooPieces.STRUCTURE_LOCATION_IGLOO)) {
            BlockPos var14 = this.templatePosition.offset(StructureTemplate.calculateRelativePosition(var9, new BlockPos(3, 0, 5)));
            BlockState var15 = var1.getBlockState(var14.below());
            if (!var15.isAir() && !var15.is(Blocks.LADDER)) {
               var1.setBlock(var14, Blocks.SNOW_BLOCK.defaultBlockState(), 3);
            }
         }

         this.templatePosition = var13;
      }
   }
}

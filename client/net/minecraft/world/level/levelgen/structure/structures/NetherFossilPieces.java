package net.minecraft.world.level.levelgen.structure.structures;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class NetherFossilPieces {
   private static final ResourceLocation[] FOSSILS = new ResourceLocation[]{
      new ResourceLocation("nether_fossils/fossil_1"),
      new ResourceLocation("nether_fossils/fossil_2"),
      new ResourceLocation("nether_fossils/fossil_3"),
      new ResourceLocation("nether_fossils/fossil_4"),
      new ResourceLocation("nether_fossils/fossil_5"),
      new ResourceLocation("nether_fossils/fossil_6"),
      new ResourceLocation("nether_fossils/fossil_7"),
      new ResourceLocation("nether_fossils/fossil_8"),
      new ResourceLocation("nether_fossils/fossil_9"),
      new ResourceLocation("nether_fossils/fossil_10"),
      new ResourceLocation("nether_fossils/fossil_11"),
      new ResourceLocation("nether_fossils/fossil_12"),
      new ResourceLocation("nether_fossils/fossil_13"),
      new ResourceLocation("nether_fossils/fossil_14")
   };

   public NetherFossilPieces() {
      super();
   }

   public static void addPieces(StructureTemplateManager var0, StructurePieceAccessor var1, RandomSource var2, BlockPos var3) {
      Rotation var4 = Rotation.getRandom(var2);
      var1.addPiece(new NetherFossilPieces.NetherFossilPiece(var0, Util.getRandom(FOSSILS, var2), var3, var4));
   }

   public static class NetherFossilPiece extends TemplateStructurePiece {
      public NetherFossilPiece(StructureTemplateManager var1, ResourceLocation var2, BlockPos var3, Rotation var4) {
         super(StructurePieceType.NETHER_FOSSIL, 0, var1, var2, var2.toString(), makeSettings(var4), var3);
      }

      public NetherFossilPiece(StructureTemplateManager var1, CompoundTag var2) {
         super(StructurePieceType.NETHER_FOSSIL, var2, var1, var1x -> makeSettings(Rotation.valueOf(var2.getString("Rot"))));
      }

      private static StructurePlaceSettings makeSettings(Rotation var0) {
         return new StructurePlaceSettings().setRotation(var0).setMirror(Mirror.NONE).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
      }

      @Override
      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         super.addAdditionalSaveData(var1, var2);
         var2.putString("Rot", this.placeSettings.getRotation().name());
      }

      @Override
      protected void handleDataMarker(String var1, BlockPos var2, ServerLevelAccessor var3, RandomSource var4, BoundingBox var5) {
      }

      @Override
      public void postProcess(
         WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7
      ) {
         var5.encapsulate(this.template.getBoundingBox(this.placeSettings, this.templatePosition));
         super.postProcess(var1, var2, var3, var4, var5, var6, var7);
      }
   }
}

package net.minecraft.world.level.levelgen.structure.structures;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
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
   private static final Identifier[] FOSSILS = new Identifier[]{Identifier.withDefaultNamespace("nether_fossils/fossil_1"), Identifier.withDefaultNamespace("nether_fossils/fossil_2"), Identifier.withDefaultNamespace("nether_fossils/fossil_3"), Identifier.withDefaultNamespace("nether_fossils/fossil_4"), Identifier.withDefaultNamespace("nether_fossils/fossil_5"), Identifier.withDefaultNamespace("nether_fossils/fossil_6"), Identifier.withDefaultNamespace("nether_fossils/fossil_7"), Identifier.withDefaultNamespace("nether_fossils/fossil_8"), Identifier.withDefaultNamespace("nether_fossils/fossil_9"), Identifier.withDefaultNamespace("nether_fossils/fossil_10"), Identifier.withDefaultNamespace("nether_fossils/fossil_11"), Identifier.withDefaultNamespace("nether_fossils/fossil_12"), Identifier.withDefaultNamespace("nether_fossils/fossil_13"), Identifier.withDefaultNamespace("nether_fossils/fossil_14")};

   public NetherFossilPieces() {
      super();
   }

   public static void addPieces(StructureTemplateManager var0, StructurePieceAccessor var1, RandomSource var2, BlockPos var3) {
      Rotation var4 = Rotation.getRandom(var2);
      var1.addPiece(new NetherFossilPiece(var0, (Identifier)Util.getRandom(FOSSILS, var2), var3, var4));
   }

   public static class NetherFossilPiece extends TemplateStructurePiece {
      public NetherFossilPiece(StructureTemplateManager var1, Identifier var2, BlockPos var3, Rotation var4) {
         super(StructurePieceType.NETHER_FOSSIL, 0, var1, var2, var2.toString(), makeSettings(var4), var3);
      }

      public NetherFossilPiece(StructureTemplateManager var1, CompoundTag var2) {
         super(StructurePieceType.NETHER_FOSSIL, var2, var1, (var1x) -> makeSettings((Rotation)var2.read("Rot", Rotation.LEGACY_CODEC).orElseThrow()));
      }

      private static StructurePlaceSettings makeSettings(Rotation var0) {
         return (new StructurePlaceSettings()).setRotation(var0).setMirror(Mirror.NONE).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         super.addAdditionalSaveData(var1, var2);
         var2.store("Rot", Rotation.LEGACY_CODEC, this.placeSettings.getRotation());
      }

      protected void handleDataMarker(String var1, BlockPos var2, ServerLevelAccessor var3, RandomSource var4, BoundingBox var5) {
      }

      public void postProcess(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         BoundingBox var8 = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
         var5.encapsulate(var8);
         super.postProcess(var1, var2, var3, var4, var5, var6, var7);
         this.placeDriedGhast(var1, var4, var8, var5);
      }

      private void placeDriedGhast(WorldGenLevel var1, RandomSource var2, BoundingBox var3, BoundingBox var4) {
         RandomSource var5 = RandomSource.create(var1.getSeed()).forkPositional().at(var3.getCenter());
         if (var5.nextFloat() < 0.5F) {
            int var6 = var3.minX() + var5.nextInt(var3.getXSpan());
            int var7 = var3.minY();
            int var8 = var3.minZ() + var5.nextInt(var3.getZSpan());
            BlockPos var9 = new BlockPos(var6, var7, var8);
            if (var1.getBlockState(var9).isAir() && var4.isInside(var9)) {
               var1.setBlock(var9, Blocks.DRIED_GHAST.defaultBlockState().rotate(Rotation.getRandom(var5)), 2);
            }
         }

      }
   }
}

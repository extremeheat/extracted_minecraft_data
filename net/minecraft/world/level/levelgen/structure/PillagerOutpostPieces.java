package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.structures.EmptyPoolElement;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import net.minecraft.world.level.levelgen.feature.structures.ListPoolElement;
import net.minecraft.world.level.levelgen.feature.structures.SinglePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class PillagerOutpostPieces {
   public static void addPieces(ChunkGenerator var0, StructureManager var1, BlockPos var2, List var3, WorldgenRandom var4) {
      JigsawPlacement.addPieces(new ResourceLocation("pillager_outpost/base_plates"), 7, PillagerOutpostPieces.PillagerOutpostPiece::new, var0, var1, var2, var3, var4);
   }

   static {
      JigsawPlacement.POOLS.register(new StructureTemplatePool(new ResourceLocation("pillager_outpost/base_plates"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(new SinglePoolElement("pillager_outpost/base_plate"), 1)), StructureTemplatePool.Projection.RIGID));
      JigsawPlacement.POOLS.register(new StructureTemplatePool(new ResourceLocation("pillager_outpost/towers"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(new ListPoolElement(ImmutableList.of(new SinglePoolElement("pillager_outpost/watchtower"), new SinglePoolElement("pillager_outpost/watchtower_overgrown", ImmutableList.of(new BlockRotProcessor(0.05F))))), 1)), StructureTemplatePool.Projection.RIGID));
      JigsawPlacement.POOLS.register(new StructureTemplatePool(new ResourceLocation("pillager_outpost/feature_plates"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(new SinglePoolElement("pillager_outpost/feature_plate"), 1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
      JigsawPlacement.POOLS.register(new StructureTemplatePool(new ResourceLocation("pillager_outpost/features"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(new SinglePoolElement("pillager_outpost/feature_cage1"), 1), Pair.of(new SinglePoolElement("pillager_outpost/feature_cage2"), 1), Pair.of(new SinglePoolElement("pillager_outpost/feature_logs"), 1), Pair.of(new SinglePoolElement("pillager_outpost/feature_tent1"), 1), Pair.of(new SinglePoolElement("pillager_outpost/feature_tent2"), 1), Pair.of(new SinglePoolElement("pillager_outpost/feature_targets"), 1), Pair.of(EmptyPoolElement.INSTANCE, 6)), StructureTemplatePool.Projection.RIGID));
   }

   public static class PillagerOutpostPiece extends PoolElementStructurePiece {
      public PillagerOutpostPiece(StructureManager var1, StructurePoolElement var2, BlockPos var3, int var4, Rotation var5, BoundingBox var6) {
         super(StructurePieceType.PILLAGER_OUTPOST, var1, var2, var3, var4, var5, var6);
      }

      public PillagerOutpostPiece(StructureManager var1, CompoundTag var2) {
         super(var1, var2, StructurePieceType.PILLAGER_OUTPOST);
      }
   }
}

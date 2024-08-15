package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
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
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.CappedProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosAlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.AppendLoot;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public class OceanRuinPieces {
   static final StructureProcessor WARM_SUSPICIOUS_BLOCK_PROCESSOR = archyRuleProcessor(
      Blocks.SAND, Blocks.SUSPICIOUS_SAND, BuiltInLootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY
   );
   static final StructureProcessor COLD_SUSPICIOUS_BLOCK_PROCESSOR = archyRuleProcessor(
      Blocks.GRAVEL, Blocks.SUSPICIOUS_GRAVEL, BuiltInLootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY
   );
   private static final ResourceLocation[] WARM_RUINS = new ResourceLocation[]{
      ResourceLocation.withDefaultNamespace("underwater_ruin/warm_1"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/warm_2"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/warm_3"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/warm_4"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/warm_5"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/warm_6"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/warm_7"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/warm_8")
   };
   private static final ResourceLocation[] RUINS_BRICK = new ResourceLocation[]{
      ResourceLocation.withDefaultNamespace("underwater_ruin/brick_1"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/brick_2"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/brick_3"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/brick_4"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/brick_5"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/brick_6"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/brick_7"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/brick_8")
   };
   private static final ResourceLocation[] RUINS_CRACKED = new ResourceLocation[]{
      ResourceLocation.withDefaultNamespace("underwater_ruin/cracked_1"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/cracked_2"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/cracked_3"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/cracked_4"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/cracked_5"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/cracked_6"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/cracked_7"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/cracked_8")
   };
   private static final ResourceLocation[] RUINS_MOSSY = new ResourceLocation[]{
      ResourceLocation.withDefaultNamespace("underwater_ruin/mossy_1"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/mossy_2"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/mossy_3"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/mossy_4"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/mossy_5"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/mossy_6"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/mossy_7"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/mossy_8")
   };
   private static final ResourceLocation[] BIG_RUINS_BRICK = new ResourceLocation[]{
      ResourceLocation.withDefaultNamespace("underwater_ruin/big_brick_1"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/big_brick_2"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/big_brick_3"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/big_brick_8")
   };
   private static final ResourceLocation[] BIG_RUINS_MOSSY = new ResourceLocation[]{
      ResourceLocation.withDefaultNamespace("underwater_ruin/big_mossy_1"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/big_mossy_2"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/big_mossy_3"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/big_mossy_8")
   };
   private static final ResourceLocation[] BIG_RUINS_CRACKED = new ResourceLocation[]{
      ResourceLocation.withDefaultNamespace("underwater_ruin/big_cracked_1"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/big_cracked_2"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/big_cracked_3"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/big_cracked_8")
   };
   private static final ResourceLocation[] BIG_WARM_RUINS = new ResourceLocation[]{
      ResourceLocation.withDefaultNamespace("underwater_ruin/big_warm_4"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/big_warm_5"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/big_warm_6"),
      ResourceLocation.withDefaultNamespace("underwater_ruin/big_warm_7")
   };

   public OceanRuinPieces() {
      super();
   }

   private static StructureProcessor archyRuleProcessor(Block var0, Block var1, ResourceKey<LootTable> var2) {
      return new CappedProcessor(
         new RuleProcessor(
            List.of(
               new ProcessorRule(new BlockMatchTest(var0), AlwaysTrueTest.INSTANCE, PosAlwaysTrueTest.INSTANCE, var1.defaultBlockState(), new AppendLoot(var2))
            )
         ),
         ConstantInt.of(5)
      );
   }

   private static ResourceLocation getSmallWarmRuin(RandomSource var0) {
      return Util.getRandom(WARM_RUINS, var0);
   }

   private static ResourceLocation getBigWarmRuin(RandomSource var0) {
      return Util.getRandom(BIG_WARM_RUINS, var0);
   }

   public static void addPieces(
      StructureTemplateManager var0, BlockPos var1, Rotation var2, StructurePieceAccessor var3, RandomSource var4, OceanRuinStructure var5
   ) {
      boolean var6 = var4.nextFloat() <= var5.largeProbability;
      float var7 = var6 ? 0.9F : 0.8F;
      addPiece(var0, var1, var2, var3, var4, var5, var6, var7);
      if (var6 && var4.nextFloat() <= var5.clusterProbability) {
         addClusterRuins(var0, var4, var2, var1, var5, var3);
      }
   }

   private static void addClusterRuins(
      StructureTemplateManager var0, RandomSource var1, Rotation var2, BlockPos var3, OceanRuinStructure var4, StructurePieceAccessor var5
   ) {
      BlockPos var6 = new BlockPos(var3.getX(), 90, var3.getZ());
      BlockPos var7 = StructureTemplate.transform(new BlockPos(15, 0, 15), Mirror.NONE, var2, BlockPos.ZERO).offset(var6);
      BoundingBox var8 = BoundingBox.fromCorners(var6, var7);
      BlockPos var9 = new BlockPos(Math.min(var6.getX(), var7.getX()), var6.getY(), Math.min(var6.getZ(), var7.getZ()));
      List var10 = allPositions(var1, var9);
      int var11 = Mth.nextInt(var1, 4, 8);

      for (int var12 = 0; var12 < var11; var12++) {
         if (!var10.isEmpty()) {
            int var13 = var1.nextInt(var10.size());
            BlockPos var14 = (BlockPos)var10.remove(var13);
            Rotation var15 = Rotation.getRandom(var1);
            BlockPos var16 = StructureTemplate.transform(new BlockPos(5, 0, 6), Mirror.NONE, var15, BlockPos.ZERO).offset(var14);
            BoundingBox var17 = BoundingBox.fromCorners(var14, var16);
            if (!var17.intersects(var8)) {
               addPiece(var0, var14, var15, var5, var1, var4, false, 0.8F);
            }
         }
      }
   }

   private static List<BlockPos> allPositions(RandomSource var0, BlockPos var1) {
      ArrayList var2 = Lists.newArrayList();
      var2.add(var1.offset(-16 + Mth.nextInt(var0, 1, 8), 0, 16 + Mth.nextInt(var0, 1, 7)));
      var2.add(var1.offset(-16 + Mth.nextInt(var0, 1, 8), 0, Mth.nextInt(var0, 1, 7)));
      var2.add(var1.offset(-16 + Mth.nextInt(var0, 1, 8), 0, -16 + Mth.nextInt(var0, 4, 8)));
      var2.add(var1.offset(Mth.nextInt(var0, 1, 7), 0, 16 + Mth.nextInt(var0, 1, 7)));
      var2.add(var1.offset(Mth.nextInt(var0, 1, 7), 0, -16 + Mth.nextInt(var0, 4, 6)));
      var2.add(var1.offset(16 + Mth.nextInt(var0, 1, 7), 0, 16 + Mth.nextInt(var0, 3, 8)));
      var2.add(var1.offset(16 + Mth.nextInt(var0, 1, 7), 0, Mth.nextInt(var0, 1, 7)));
      var2.add(var1.offset(16 + Mth.nextInt(var0, 1, 7), 0, -16 + Mth.nextInt(var0, 4, 8)));
      return var2;
   }

   private static void addPiece(
      StructureTemplateManager var0,
      BlockPos var1,
      Rotation var2,
      StructurePieceAccessor var3,
      RandomSource var4,
      OceanRuinStructure var5,
      boolean var6,
      float var7
   ) {
      switch (var5.biomeTemp) {
         case WARM:
         default:
            ResourceLocation var8 = var6 ? getBigWarmRuin(var4) : getSmallWarmRuin(var4);
            var3.addPiece(new OceanRuinPieces.OceanRuinPiece(var0, var8, var1, var2, var7, var5.biomeTemp, var6));
            break;
         case COLD:
            ResourceLocation[] var9 = var6 ? BIG_RUINS_BRICK : RUINS_BRICK;
            ResourceLocation[] var10 = var6 ? BIG_RUINS_CRACKED : RUINS_CRACKED;
            ResourceLocation[] var11 = var6 ? BIG_RUINS_MOSSY : RUINS_MOSSY;
            int var12 = var4.nextInt(var9.length);
            var3.addPiece(new OceanRuinPieces.OceanRuinPiece(var0, var9[var12], var1, var2, var7, var5.biomeTemp, var6));
            var3.addPiece(new OceanRuinPieces.OceanRuinPiece(var0, var10[var12], var1, var2, 0.7F, var5.biomeTemp, var6));
            var3.addPiece(new OceanRuinPieces.OceanRuinPiece(var0, var11[var12], var1, var2, 0.5F, var5.biomeTemp, var6));
      }
   }

   public static class OceanRuinPiece extends TemplateStructurePiece {
      private final OceanRuinStructure.Type biomeType;
      private final float integrity;
      private final boolean isLarge;

      public OceanRuinPiece(
         StructureTemplateManager var1, ResourceLocation var2, BlockPos var3, Rotation var4, float var5, OceanRuinStructure.Type var6, boolean var7
      ) {
         super(StructurePieceType.OCEAN_RUIN, 0, var1, var2, var2.toString(), makeSettings(var4, var5, var6), var3);
         this.integrity = var5;
         this.biomeType = var6;
         this.isLarge = var7;
      }

      private OceanRuinPiece(StructureTemplateManager var1, CompoundTag var2, Rotation var3, float var4, OceanRuinStructure.Type var5, boolean var6) {
         super(StructurePieceType.OCEAN_RUIN, var2, var1, var3x -> makeSettings(var3, var4, var5));
         this.integrity = var4;
         this.biomeType = var5;
         this.isLarge = var6;
      }

      private static StructurePlaceSettings makeSettings(Rotation var0, float var1, OceanRuinStructure.Type var2) {
         StructureProcessor var3 = var2 == OceanRuinStructure.Type.COLD
            ? OceanRuinPieces.COLD_SUSPICIOUS_BLOCK_PROCESSOR
            : OceanRuinPieces.WARM_SUSPICIOUS_BLOCK_PROCESSOR;
         return new StructurePlaceSettings()
            .setRotation(var0)
            .setMirror(Mirror.NONE)
            .addProcessor(new BlockRotProcessor(var1))
            .addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR)
            .addProcessor(var3);
      }

      public static OceanRuinPieces.OceanRuinPiece create(StructureTemplateManager var0, CompoundTag var1) {
         Rotation var2 = Rotation.valueOf(var1.getString("Rot"));
         float var3 = var1.getFloat("Integrity");
         OceanRuinStructure.Type var4 = OceanRuinStructure.Type.valueOf(var1.getString("BiomeType"));
         boolean var5 = var1.getBoolean("IsLarge");
         return new OceanRuinPieces.OceanRuinPiece(var0, var1, var2, var3, var4, var5);
      }

      @Override
      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         super.addAdditionalSaveData(var1, var2);
         var2.putString("Rot", this.placeSettings.getRotation().name());
         var2.putFloat("Integrity", this.integrity);
         var2.putString("BiomeType", this.biomeType.toString());
         var2.putBoolean("IsLarge", this.isLarge);
      }

      @Override
      protected void handleDataMarker(String var1, BlockPos var2, ServerLevelAccessor var3, RandomSource var4, BoundingBox var5) {
         if ("chest".equals(var1)) {
            var3.setBlock(
               var2, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.WATERLOGGED, Boolean.valueOf(var3.getFluidState(var2).is(FluidTags.WATER))), 2
            );
            BlockEntity var6 = var3.getBlockEntity(var2);
            if (var6 instanceof ChestBlockEntity) {
               ((ChestBlockEntity)var6)
                  .setLootTable(this.isLarge ? BuiltInLootTables.UNDERWATER_RUIN_BIG : BuiltInLootTables.UNDERWATER_RUIN_SMALL, var4.nextLong());
            }
         } else if ("drowned".equals(var1)) {
            Drowned var7 = EntityType.DROWNED.create(var3.getLevel(), EntitySpawnReason.STRUCTURE);
            if (var7 != null) {
               var7.setPersistenceRequired();
               var7.moveTo(var2, 0.0F, 0.0F);
               var7.finalizeSpawn(var3, var3.getCurrentDifficultyAt(var2), EntitySpawnReason.STRUCTURE, null);
               var3.addFreshEntityWithPassengers(var7);
               if (var2.getY() > var3.getSeaLevel()) {
                  var3.setBlock(var2, Blocks.AIR.defaultBlockState(), 2);
               } else {
                  var3.setBlock(var2, Blocks.WATER.defaultBlockState(), 2);
               }
            }
         }
      }

      @Override
      public void postProcess(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         int var8 = var1.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, this.templatePosition.getX(), this.templatePosition.getZ());
         this.templatePosition = new BlockPos(this.templatePosition.getX(), var8, this.templatePosition.getZ());
         BlockPos var9 = StructureTemplate.transform(
               new BlockPos(this.template.getSize().getX() - 1, 0, this.template.getSize().getZ() - 1),
               Mirror.NONE,
               this.placeSettings.getRotation(),
               BlockPos.ZERO
            )
            .offset(this.templatePosition);
         this.templatePosition = new BlockPos(this.templatePosition.getX(), this.getHeight(this.templatePosition, var1, var9), this.templatePosition.getZ());
         super.postProcess(var1, var2, var3, var4, var5, var6, var7);
      }

      private int getHeight(BlockPos var1, BlockGetter var2, BlockPos var3) {
         int var4 = var1.getY();
         int var5 = 512;
         int var6 = var4 - 1;
         int var7 = 0;

         for (BlockPos var9 : BlockPos.betweenClosed(var1, var3)) {
            int var10 = var9.getX();
            int var11 = var9.getZ();
            int var12 = var1.getY() - 1;
            BlockPos.MutableBlockPos var13 = new BlockPos.MutableBlockPos(var10, var12, var11);
            BlockState var14 = var2.getBlockState(var13);

            for (FluidState var15 = var2.getFluidState(var13);
               (var14.isAir() || var15.is(FluidTags.WATER) || var14.is(BlockTags.ICE)) && var12 > var2.getMinBuildHeight() + 1;
               var15 = var2.getFluidState(var13)
            ) {
               var13.set(var10, --var12, var11);
               var14 = var2.getBlockState(var13);
            }

            var5 = Math.min(var5, var12);
            if (var12 < var6 - 2) {
               var7++;
            }
         }

         int var16 = Math.abs(var1.getX() - var3.getX());
         if (var6 - var5 > 2 && var7 > var16 - 2) {
            var4 = var5 + 1;
         }

         return var4;
      }
   }
}

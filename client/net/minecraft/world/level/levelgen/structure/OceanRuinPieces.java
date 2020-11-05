package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.configurations.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class OceanRuinPieces {
   private static final ResourceLocation[] WARM_RUINS = new ResourceLocation[]{new ResourceLocation("underwater_ruin/warm_1"), new ResourceLocation("underwater_ruin/warm_2"), new ResourceLocation("underwater_ruin/warm_3"), new ResourceLocation("underwater_ruin/warm_4"), new ResourceLocation("underwater_ruin/warm_5"), new ResourceLocation("underwater_ruin/warm_6"), new ResourceLocation("underwater_ruin/warm_7"), new ResourceLocation("underwater_ruin/warm_8")};
   private static final ResourceLocation[] RUINS_BRICK = new ResourceLocation[]{new ResourceLocation("underwater_ruin/brick_1"), new ResourceLocation("underwater_ruin/brick_2"), new ResourceLocation("underwater_ruin/brick_3"), new ResourceLocation("underwater_ruin/brick_4"), new ResourceLocation("underwater_ruin/brick_5"), new ResourceLocation("underwater_ruin/brick_6"), new ResourceLocation("underwater_ruin/brick_7"), new ResourceLocation("underwater_ruin/brick_8")};
   private static final ResourceLocation[] RUINS_CRACKED = new ResourceLocation[]{new ResourceLocation("underwater_ruin/cracked_1"), new ResourceLocation("underwater_ruin/cracked_2"), new ResourceLocation("underwater_ruin/cracked_3"), new ResourceLocation("underwater_ruin/cracked_4"), new ResourceLocation("underwater_ruin/cracked_5"), new ResourceLocation("underwater_ruin/cracked_6"), new ResourceLocation("underwater_ruin/cracked_7"), new ResourceLocation("underwater_ruin/cracked_8")};
   private static final ResourceLocation[] RUINS_MOSSY = new ResourceLocation[]{new ResourceLocation("underwater_ruin/mossy_1"), new ResourceLocation("underwater_ruin/mossy_2"), new ResourceLocation("underwater_ruin/mossy_3"), new ResourceLocation("underwater_ruin/mossy_4"), new ResourceLocation("underwater_ruin/mossy_5"), new ResourceLocation("underwater_ruin/mossy_6"), new ResourceLocation("underwater_ruin/mossy_7"), new ResourceLocation("underwater_ruin/mossy_8")};
   private static final ResourceLocation[] BIG_RUINS_BRICK = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_brick_1"), new ResourceLocation("underwater_ruin/big_brick_2"), new ResourceLocation("underwater_ruin/big_brick_3"), new ResourceLocation("underwater_ruin/big_brick_8")};
   private static final ResourceLocation[] BIG_RUINS_MOSSY = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_mossy_1"), new ResourceLocation("underwater_ruin/big_mossy_2"), new ResourceLocation("underwater_ruin/big_mossy_3"), new ResourceLocation("underwater_ruin/big_mossy_8")};
   private static final ResourceLocation[] BIG_RUINS_CRACKED = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_cracked_1"), new ResourceLocation("underwater_ruin/big_cracked_2"), new ResourceLocation("underwater_ruin/big_cracked_3"), new ResourceLocation("underwater_ruin/big_cracked_8")};
   private static final ResourceLocation[] BIG_WARM_RUINS = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_warm_4"), new ResourceLocation("underwater_ruin/big_warm_5"), new ResourceLocation("underwater_ruin/big_warm_6"), new ResourceLocation("underwater_ruin/big_warm_7")};

   private static ResourceLocation getSmallWarmRuin(Random var0) {
      return (ResourceLocation)Util.getRandom((Object[])WARM_RUINS, var0);
   }

   private static ResourceLocation getBigWarmRuin(Random var0) {
      return (ResourceLocation)Util.getRandom((Object[])BIG_WARM_RUINS, var0);
   }

   public static void addPieces(StructureManager var0, BlockPos var1, Rotation var2, List<StructurePiece> var3, Random var4, OceanRuinConfiguration var5) {
      boolean var6 = var4.nextFloat() <= var5.largeProbability;
      float var7 = var6 ? 0.9F : 0.8F;
      addPiece(var0, var1, var2, var3, var4, var5, var6, var7);
      if (var6 && var4.nextFloat() <= var5.clusterProbability) {
         addClusterRuins(var0, var4, var2, var1, var5, var3);
      }

   }

   private static void addClusterRuins(StructureManager var0, Random var1, Rotation var2, BlockPos var3, OceanRuinConfiguration var4, List<StructurePiece> var5) {
      int var6 = var3.getX();
      int var7 = var3.getZ();
      BlockPos var8 = StructureTemplate.transform(new BlockPos(15, 0, 15), Mirror.NONE, var2, BlockPos.ZERO).offset(var6, 0, var7);
      BoundingBox var9 = BoundingBox.createProper(var6, 0, var7, var8.getX(), 0, var8.getZ());
      BlockPos var10 = new BlockPos(Math.min(var6, var8.getX()), 0, Math.min(var7, var8.getZ()));
      List var11 = allPositions(var1, var10.getX(), var10.getZ());
      int var12 = Mth.nextInt(var1, 4, 8);

      for(int var13 = 0; var13 < var12; ++var13) {
         if (!var11.isEmpty()) {
            int var14 = var1.nextInt(var11.size());
            BlockPos var15 = (BlockPos)var11.remove(var14);
            int var16 = var15.getX();
            int var17 = var15.getZ();
            Rotation var18 = Rotation.getRandom(var1);
            BlockPos var19 = StructureTemplate.transform(new BlockPos(5, 0, 6), Mirror.NONE, var18, BlockPos.ZERO).offset(var16, 0, var17);
            BoundingBox var20 = BoundingBox.createProper(var16, 0, var17, var19.getX(), 0, var19.getZ());
            if (!var20.intersects(var9)) {
               addPiece(var0, var15, var18, var5, var1, var4, false, 0.8F);
            }
         }
      }

   }

   private static List<BlockPos> allPositions(Random var0, int var1, int var2) {
      ArrayList var3 = Lists.newArrayList();
      var3.add(new BlockPos(var1 - 16 + Mth.nextInt(var0, 1, 8), 90, var2 + 16 + Mth.nextInt(var0, 1, 7)));
      var3.add(new BlockPos(var1 - 16 + Mth.nextInt(var0, 1, 8), 90, var2 + Mth.nextInt(var0, 1, 7)));
      var3.add(new BlockPos(var1 - 16 + Mth.nextInt(var0, 1, 8), 90, var2 - 16 + Mth.nextInt(var0, 4, 8)));
      var3.add(new BlockPos(var1 + Mth.nextInt(var0, 1, 7), 90, var2 + 16 + Mth.nextInt(var0, 1, 7)));
      var3.add(new BlockPos(var1 + Mth.nextInt(var0, 1, 7), 90, var2 - 16 + Mth.nextInt(var0, 4, 6)));
      var3.add(new BlockPos(var1 + 16 + Mth.nextInt(var0, 1, 7), 90, var2 + 16 + Mth.nextInt(var0, 3, 8)));
      var3.add(new BlockPos(var1 + 16 + Mth.nextInt(var0, 1, 7), 90, var2 + Mth.nextInt(var0, 1, 7)));
      var3.add(new BlockPos(var1 + 16 + Mth.nextInt(var0, 1, 7), 90, var2 - 16 + Mth.nextInt(var0, 4, 8)));
      return var3;
   }

   private static void addPiece(StructureManager var0, BlockPos var1, Rotation var2, List<StructurePiece> var3, Random var4, OceanRuinConfiguration var5, boolean var6, float var7) {
      if (var5.biomeTemp == OceanRuinFeature.Type.WARM) {
         ResourceLocation var8 = var6 ? getBigWarmRuin(var4) : getSmallWarmRuin(var4);
         var3.add(new OceanRuinPieces.OceanRuinPiece(var0, var8, var1, var2, var7, var5.biomeTemp, var6));
      } else if (var5.biomeTemp == OceanRuinFeature.Type.COLD) {
         ResourceLocation[] var12 = var6 ? BIG_RUINS_BRICK : RUINS_BRICK;
         ResourceLocation[] var9 = var6 ? BIG_RUINS_CRACKED : RUINS_CRACKED;
         ResourceLocation[] var10 = var6 ? BIG_RUINS_MOSSY : RUINS_MOSSY;
         int var11 = var4.nextInt(var12.length);
         var3.add(new OceanRuinPieces.OceanRuinPiece(var0, var12[var11], var1, var2, var7, var5.biomeTemp, var6));
         var3.add(new OceanRuinPieces.OceanRuinPiece(var0, var9[var11], var1, var2, 0.7F, var5.biomeTemp, var6));
         var3.add(new OceanRuinPieces.OceanRuinPiece(var0, var10[var11], var1, var2, 0.5F, var5.biomeTemp, var6));
      }

   }

   public static class OceanRuinPiece extends TemplateStructurePiece {
      private final OceanRuinFeature.Type biomeType;
      private final float integrity;
      private final ResourceLocation templateLocation;
      private final Rotation rotation;
      private final boolean isLarge;

      public OceanRuinPiece(StructureManager var1, ResourceLocation var2, BlockPos var3, Rotation var4, float var5, OceanRuinFeature.Type var6, boolean var7) {
         super(StructurePieceType.OCEAN_RUIN, 0);
         this.templateLocation = var2;
         this.templatePosition = var3;
         this.rotation = var4;
         this.integrity = var5;
         this.biomeType = var6;
         this.isLarge = var7;
         this.loadTemplate(var1);
      }

      public OceanRuinPiece(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.OCEAN_RUIN, var2);
         this.templateLocation = new ResourceLocation(var2.getString("Template"));
         this.rotation = Rotation.valueOf(var2.getString("Rot"));
         this.integrity = var2.getFloat("Integrity");
         this.biomeType = OceanRuinFeature.Type.valueOf(var2.getString("BiomeType"));
         this.isLarge = var2.getBoolean("IsLarge");
         this.loadTemplate(var1);
      }

      private void loadTemplate(StructureManager var1) {
         StructureTemplate var2 = var1.getOrCreate(this.templateLocation);
         StructurePlaceSettings var3 = (new StructurePlaceSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
         this.setup(var2, this.templatePosition, var3);
      }

      protected void addAdditionalSaveData(CompoundTag var1) {
         super.addAdditionalSaveData(var1);
         var1.putString("Template", this.templateLocation.toString());
         var1.putString("Rot", this.rotation.name());
         var1.putFloat("Integrity", this.integrity);
         var1.putString("BiomeType", this.biomeType.toString());
         var1.putBoolean("IsLarge", this.isLarge);
      }

      protected void handleDataMarker(String var1, BlockPos var2, ServerLevelAccessor var3, Random var4, BoundingBox var5) {
         if ("chest".equals(var1)) {
            var3.setBlock(var2, (BlockState)Blocks.CHEST.defaultBlockState().setValue(ChestBlock.WATERLOGGED, var3.getFluidState(var2).is(FluidTags.WATER)), 2);
            BlockEntity var6 = var3.getBlockEntity(var2);
            if (var6 instanceof ChestBlockEntity) {
               ((ChestBlockEntity)var6).setLootTable(this.isLarge ? BuiltInLootTables.UNDERWATER_RUIN_BIG : BuiltInLootTables.UNDERWATER_RUIN_SMALL, var4.nextLong());
            }
         } else if ("drowned".equals(var1)) {
            Drowned var7 = (Drowned)EntityType.DROWNED.create(var3.getLevel());
            var7.setPersistenceRequired();
            var7.moveTo(var2, 0.0F, 0.0F);
            var7.finalizeSpawn(var3, var3.getCurrentDifficultyAt(var2), MobSpawnType.STRUCTURE, (SpawnGroupData)null, (CompoundTag)null);
            var3.addFreshEntityWithPassengers(var7);
            if (var2.getY() > var3.getSeaLevel()) {
               var3.setBlock(var2, Blocks.AIR.defaultBlockState(), 2);
            } else {
               var3.setBlock(var2, Blocks.WATER.defaultBlockState(), 2);
            }
         }

      }

      public boolean postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.placeSettings.clearProcessors().addProcessor(new BlockRotProcessor(this.integrity)).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
         int var8 = var1.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, this.templatePosition.getX(), this.templatePosition.getZ());
         this.templatePosition = new BlockPos(this.templatePosition.getX(), var8, this.templatePosition.getZ());
         BlockPos var9 = StructureTemplate.transform(new BlockPos(this.template.getSize().getX() - 1, 0, this.template.getSize().getZ() - 1), Mirror.NONE, this.rotation, BlockPos.ZERO).offset(this.templatePosition);
         this.templatePosition = new BlockPos(this.templatePosition.getX(), this.getHeight(this.templatePosition, var1, var9), this.templatePosition.getZ());
         return super.postProcess(var1, var2, var3, var4, var5, var6, var7);
      }

      private int getHeight(BlockPos var1, BlockGetter var2, BlockPos var3) {
         int var4 = var1.getY();
         int var5 = 512;
         int var6 = var4 - 1;
         int var7 = 0;
         Iterator var8 = BlockPos.betweenClosed(var1, var3).iterator();

         while(var8.hasNext()) {
            BlockPos var9 = (BlockPos)var8.next();
            int var10 = var9.getX();
            int var11 = var9.getZ();
            int var12 = var1.getY() - 1;
            BlockPos.MutableBlockPos var13 = new BlockPos.MutableBlockPos(var10, var12, var11);
            BlockState var14 = var2.getBlockState(var13);

            for(FluidState var15 = var2.getFluidState(var13); (var14.isAir() || var15.is(FluidTags.WATER) || var14.is(BlockTags.ICE)) && var12 > var2.getMinBuildHeight() + 1; var15 = var2.getFluidState(var13)) {
               --var12;
               var13.set(var10, var12, var11);
               var14 = var2.getBlockState(var13);
            }

            var5 = Math.min(var5, var12);
            if (var12 < var6 - 2) {
               ++var7;
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

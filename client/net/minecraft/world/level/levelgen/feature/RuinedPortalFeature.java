package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.RuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.RuinedPortalPiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class RuinedPortalFeature extends StructureFeature<RuinedPortalConfiguration> {
   private static final String[] STRUCTURE_LOCATION_PORTALS = new String[]{"ruined_portal/portal_1", "ruined_portal/portal_2", "ruined_portal/portal_3", "ruined_portal/portal_4", "ruined_portal/portal_5", "ruined_portal/portal_6", "ruined_portal/portal_7", "ruined_portal/portal_8", "ruined_portal/portal_9", "ruined_portal/portal_10"};
   private static final String[] STRUCTURE_LOCATION_GIANT_PORTALS = new String[]{"ruined_portal/giant_portal_1", "ruined_portal/giant_portal_2", "ruined_portal/giant_portal_3"};
   private static final float PROBABILITY_OF_GIANT_PORTAL = 0.05F;
   private static final float PROBABILITY_OF_AIR_POCKET = 0.5F;
   private static final float PROBABILITY_OF_UNDERGROUND = 0.5F;
   private static final float UNDERWATER_MOSSINESS = 0.8F;
   private static final float JUNGLE_MOSSINESS = 0.8F;
   private static final float SWAMP_MOSSINESS = 0.5F;
   private static final int MIN_Y_INDEX = 15;

   public RuinedPortalFeature(Codec<RuinedPortalConfiguration> var1) {
      super(var1, RuinedPortalFeature::pieceGeneratorSupplier);
   }

   private static Optional<PieceGenerator<RuinedPortalConfiguration>> pieceGeneratorSupplier(PieceGeneratorSupplier.Context<RuinedPortalConfiguration> var0) {
      RuinedPortalPiece.Properties var2 = new RuinedPortalPiece.Properties();
      RuinedPortalConfiguration var3 = (RuinedPortalConfiguration)var0.config();
      WorldgenRandom var4 = new WorldgenRandom(new LegacyRandomSource(0L));
      var4.setLargeFeatureSeed(var0.seed(), var0.chunkPos().field_504, var0.chunkPos().field_505);
      RuinedPortalPiece.VerticalPlacement var1;
      if (var3.portalType == RuinedPortalFeature.Type.DESERT) {
         var1 = RuinedPortalPiece.VerticalPlacement.PARTLY_BURIED;
         var2.airPocket = false;
         var2.mossiness = 0.0F;
      } else if (var3.portalType == RuinedPortalFeature.Type.JUNGLE) {
         var1 = RuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE;
         var2.airPocket = var4.nextFloat() < 0.5F;
         var2.mossiness = 0.8F;
         var2.overgrown = true;
         var2.vines = true;
      } else if (var3.portalType == RuinedPortalFeature.Type.SWAMP) {
         var1 = RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR;
         var2.airPocket = false;
         var2.mossiness = 0.5F;
         var2.vines = true;
      } else {
         boolean var5;
         if (var3.portalType == RuinedPortalFeature.Type.MOUNTAIN) {
            var5 = var4.nextFloat() < 0.5F;
            var1 = var5 ? RuinedPortalPiece.VerticalPlacement.IN_MOUNTAIN : RuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE;
            var2.airPocket = var5 || var4.nextFloat() < 0.5F;
         } else if (var3.portalType == RuinedPortalFeature.Type.OCEAN) {
            var1 = RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR;
            var2.airPocket = false;
            var2.mossiness = 0.8F;
         } else if (var3.portalType == RuinedPortalFeature.Type.NETHER) {
            var1 = RuinedPortalPiece.VerticalPlacement.IN_NETHER;
            var2.airPocket = var4.nextFloat() < 0.5F;
            var2.mossiness = 0.0F;
            var2.replaceWithBlackstone = true;
         } else {
            var5 = var4.nextFloat() < 0.5F;
            var1 = var5 ? RuinedPortalPiece.VerticalPlacement.UNDERGROUND : RuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE;
            var2.airPocket = var5 || var4.nextFloat() < 0.5F;
         }
      }

      ResourceLocation var16;
      if (var4.nextFloat() < 0.05F) {
         var16 = new ResourceLocation(STRUCTURE_LOCATION_GIANT_PORTALS[var4.nextInt(STRUCTURE_LOCATION_GIANT_PORTALS.length)]);
      } else {
         var16 = new ResourceLocation(STRUCTURE_LOCATION_PORTALS[var4.nextInt(STRUCTURE_LOCATION_PORTALS.length)]);
      }

      StructureTemplate var6 = var0.structureManager().getOrCreate(var16);
      Rotation var7 = (Rotation)Util.getRandom((Object[])Rotation.values(), var4);
      Mirror var8 = var4.nextFloat() < 0.5F ? Mirror.NONE : Mirror.FRONT_BACK;
      BlockPos var9 = new BlockPos(var6.getSize().getX() / 2, 0, var6.getSize().getZ() / 2);
      BlockPos var10 = var0.chunkPos().getWorldPosition();
      BoundingBox var11 = var6.getBoundingBox(var10, var7, var9, var8);
      BlockPos var12 = var11.getCenter();
      int var13 = var0.chunkGenerator().getBaseHeight(var12.getX(), var12.getZ(), RuinedPortalPiece.getHeightMapType(var1), var0.heightAccessor()) - 1;
      int var14 = findSuitableY(var4, var0.chunkGenerator(), var1, var2.airPocket, var13, var11.getYSpan(), var11, var0.heightAccessor());
      BlockPos var15 = new BlockPos(var10.getX(), var14, var10.getZ());
      return !var0.validBiome().test(var0.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(var15.getX()), QuartPos.fromBlock(var15.getY()), QuartPos.fromBlock(var15.getZ()))) ? Optional.empty() : Optional.of((var10x, var11x) -> {
         if (var3.portalType == RuinedPortalFeature.Type.MOUNTAIN || var3.portalType == RuinedPortalFeature.Type.OCEAN || var3.portalType == RuinedPortalFeature.Type.STANDARD) {
            var2.cold = isCold(var15, var0.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(var15.getX()), QuartPos.fromBlock(var15.getY()), QuartPos.fromBlock(var15.getZ())));
         }

         var10x.addPiece(new RuinedPortalPiece(var11x.structureManager(), var15, var1, var2, var16, var6, var7, var8, var9));
      });
   }

   private static boolean isCold(BlockPos var0, Biome var1) {
      return var1.coldEnoughToSnow(var0);
   }

   private static int findSuitableY(Random var0, ChunkGenerator var1, RuinedPortalPiece.VerticalPlacement var2, boolean var3, int var4, int var5, BoundingBox var6, LevelHeightAccessor var7) {
      int var9 = var7.getMinBuildHeight() + 15;
      int var8;
      if (var2 == RuinedPortalPiece.VerticalPlacement.IN_NETHER) {
         if (var3) {
            var8 = Mth.randomBetweenInclusive(var0, 32, 100);
         } else if (var0.nextFloat() < 0.5F) {
            var8 = Mth.randomBetweenInclusive(var0, 27, 29);
         } else {
            var8 = Mth.randomBetweenInclusive(var0, 29, 100);
         }
      } else {
         int var10;
         if (var2 == RuinedPortalPiece.VerticalPlacement.IN_MOUNTAIN) {
            var10 = var4 - var5;
            var8 = getRandomWithinInterval(var0, 70, var10);
         } else if (var2 == RuinedPortalPiece.VerticalPlacement.UNDERGROUND) {
            var10 = var4 - var5;
            var8 = getRandomWithinInterval(var0, var9, var10);
         } else if (var2 == RuinedPortalPiece.VerticalPlacement.PARTLY_BURIED) {
            var8 = var4 - var5 + Mth.randomBetweenInclusive(var0, 2, 8);
         } else {
            var8 = var4;
         }
      }

      ImmutableList var18 = ImmutableList.of(new BlockPos(var6.minX(), 0, var6.minZ()), new BlockPos(var6.maxX(), 0, var6.minZ()), new BlockPos(var6.minX(), 0, var6.maxZ()), new BlockPos(var6.maxX(), 0, var6.maxZ()));
      List var11 = (List)var18.stream().map((var2x) -> {
         return var1.getBaseColumn(var2x.getX(), var2x.getZ(), var7);
      }).collect(Collectors.toList());
      Heightmap.Types var12 = var2 == RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR ? Heightmap.Types.OCEAN_FLOOR_WG : Heightmap.Types.WORLD_SURFACE_WG;

      int var13;
      for(var13 = var8; var13 > var9; --var13) {
         int var14 = 0;
         Iterator var15 = var11.iterator();

         while(var15.hasNext()) {
            NoiseColumn var16 = (NoiseColumn)var15.next();
            BlockState var17 = var16.getBlock(var13);
            if (var12.isOpaque().test(var17)) {
               ++var14;
               if (var14 == 3) {
                  return var13;
               }
            }
         }
      }

      return var13;
   }

   private static int getRandomWithinInterval(Random var0, int var1, int var2) {
      return var1 < var2 ? Mth.randomBetweenInclusive(var0, var1, var2) : var2;
   }

   public static enum Type implements StringRepresentable {
      STANDARD("standard"),
      DESERT("desert"),
      JUNGLE("jungle"),
      SWAMP("swamp"),
      MOUNTAIN("mountain"),
      OCEAN("ocean"),
      NETHER("nether");

      public static final Codec<RuinedPortalFeature.Type> CODEC = StringRepresentable.fromEnum(RuinedPortalFeature.Type::values, RuinedPortalFeature.Type::byName);
      private static final Map<String, RuinedPortalFeature.Type> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(RuinedPortalFeature.Type::getName, (var0) -> {
         return var0;
      }));
      private final String name;

      private Type(String var3) {
         this.name = var3;
      }

      public String getName() {
         return this.name;
      }

      public static RuinedPortalFeature.Type byName(String var0) {
         return (RuinedPortalFeature.Type)BY_NAME.get(var0);
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static RuinedPortalFeature.Type[] $values() {
         return new RuinedPortalFeature.Type[]{STANDARD, DESERT, JUNGLE, SWAMP, MOUNTAIN, OCEAN, NETHER};
      }
   }
}

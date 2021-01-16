package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.RuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.RuinedPortalPiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class RuinedPortalFeature extends StructureFeature<RuinedPortalConfiguration> {
   private static final String[] STRUCTURE_LOCATION_PORTALS = new String[]{"ruined_portal/portal_1", "ruined_portal/portal_2", "ruined_portal/portal_3", "ruined_portal/portal_4", "ruined_portal/portal_5", "ruined_portal/portal_6", "ruined_portal/portal_7", "ruined_portal/portal_8", "ruined_portal/portal_9", "ruined_portal/portal_10"};
   private static final String[] STRUCTURE_LOCATION_GIANT_PORTALS = new String[]{"ruined_portal/giant_portal_1", "ruined_portal/giant_portal_2", "ruined_portal/giant_portal_3"};

   public RuinedPortalFeature(Codec<RuinedPortalConfiguration> var1) {
      super(var1);
   }

   public StructureFeature.StructureStartFactory<RuinedPortalConfiguration> getStartFactory() {
      return RuinedPortalFeature.FeatureStart::new;
   }

   private static boolean isCold(BlockPos var0, Biome var1) {
      return var1.getTemperature(var0) < 0.15F;
   }

   private static int findSuitableY(Random var0, ChunkGenerator var1, RuinedPortalPiece.VerticalPlacement var2, boolean var3, int var4, int var5, BoundingBox var6) {
      int var7;
      if (var2 == RuinedPortalPiece.VerticalPlacement.IN_NETHER) {
         if (var3) {
            var7 = randomIntInclusive(var0, 32, 100);
         } else if (var0.nextFloat() < 0.5F) {
            var7 = randomIntInclusive(var0, 27, 29);
         } else {
            var7 = randomIntInclusive(var0, 29, 100);
         }
      } else {
         int var8;
         if (var2 == RuinedPortalPiece.VerticalPlacement.IN_MOUNTAIN) {
            var8 = var4 - var5;
            var7 = getRandomWithinInterval(var0, 70, var8);
         } else if (var2 == RuinedPortalPiece.VerticalPlacement.UNDERGROUND) {
            var8 = var4 - var5;
            var7 = getRandomWithinInterval(var0, 15, var8);
         } else if (var2 == RuinedPortalPiece.VerticalPlacement.PARTLY_BURIED) {
            var7 = var4 - var5 + randomIntInclusive(var0, 2, 8);
         } else {
            var7 = var4;
         }
      }

      ImmutableList var17 = ImmutableList.of(new BlockPos(var6.x0, 0, var6.z0), new BlockPos(var6.x1, 0, var6.z0), new BlockPos(var6.x0, 0, var6.z1), new BlockPos(var6.x1, 0, var6.z1));
      List var9 = (List)var17.stream().map((var1x) -> {
         return var1.getBaseColumn(var1x.getX(), var1x.getZ());
      }).collect(Collectors.toList());
      Heightmap.Types var10 = var2 == RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR ? Heightmap.Types.OCEAN_FLOOR_WG : Heightmap.Types.WORLD_SURFACE_WG;
      BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();

      int var12;
      for(var12 = var7; var12 > 15; --var12) {
         int var13 = 0;
         var11.set(0, var12, 0);
         Iterator var14 = var9.iterator();

         while(var14.hasNext()) {
            BlockGetter var15 = (BlockGetter)var14.next();
            BlockState var16 = var15.getBlockState(var11);
            if (var16 != null && var10.isOpaque().test(var16)) {
               ++var13;
               if (var13 == 3) {
                  return var12;
               }
            }
         }
      }

      return var12;
   }

   private static int randomIntInclusive(Random var0, int var1, int var2) {
      return var0.nextInt(var2 - var1 + 1) + var1;
   }

   private static int getRandomWithinInterval(Random var0, int var1, int var2) {
      return var1 < var2 ? randomIntInclusive(var0, var1, var2) : var2;
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
   }

   public static class FeatureStart extends StructureStart<RuinedPortalConfiguration> {
      protected FeatureStart(StructureFeature<RuinedPortalConfiguration> var1, int var2, int var3, BoundingBox var4, int var5, long var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      public void generatePieces(RegistryAccess var1, ChunkGenerator var2, StructureManager var3, int var4, int var5, Biome var6, RuinedPortalConfiguration var7) {
         RuinedPortalPiece.Properties var9 = new RuinedPortalPiece.Properties();
         RuinedPortalPiece.VerticalPlacement var8;
         if (var7.portalType == RuinedPortalFeature.Type.DESERT) {
            var8 = RuinedPortalPiece.VerticalPlacement.PARTLY_BURIED;
            var9.airPocket = false;
            var9.mossiness = 0.0F;
         } else if (var7.portalType == RuinedPortalFeature.Type.JUNGLE) {
            var8 = RuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE;
            var9.airPocket = this.random.nextFloat() < 0.5F;
            var9.mossiness = 0.8F;
            var9.overgrown = true;
            var9.vines = true;
         } else if (var7.portalType == RuinedPortalFeature.Type.SWAMP) {
            var8 = RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR;
            var9.airPocket = false;
            var9.mossiness = 0.5F;
            var9.vines = true;
         } else {
            boolean var10;
            if (var7.portalType == RuinedPortalFeature.Type.MOUNTAIN) {
               var10 = this.random.nextFloat() < 0.5F;
               var8 = var10 ? RuinedPortalPiece.VerticalPlacement.IN_MOUNTAIN : RuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE;
               var9.airPocket = var10 || this.random.nextFloat() < 0.5F;
            } else if (var7.portalType == RuinedPortalFeature.Type.OCEAN) {
               var8 = RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR;
               var9.airPocket = false;
               var9.mossiness = 0.8F;
            } else if (var7.portalType == RuinedPortalFeature.Type.NETHER) {
               var8 = RuinedPortalPiece.VerticalPlacement.IN_NETHER;
               var9.airPocket = this.random.nextFloat() < 0.5F;
               var9.mossiness = 0.0F;
               var9.replaceWithBlackstone = true;
            } else {
               var10 = this.random.nextFloat() < 0.5F;
               var8 = var10 ? RuinedPortalPiece.VerticalPlacement.UNDERGROUND : RuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE;
               var9.airPocket = var10 || this.random.nextFloat() < 0.5F;
            }
         }

         ResourceLocation var23;
         if (this.random.nextFloat() < 0.05F) {
            var23 = new ResourceLocation(RuinedPortalFeature.STRUCTURE_LOCATION_GIANT_PORTALS[this.random.nextInt(RuinedPortalFeature.STRUCTURE_LOCATION_GIANT_PORTALS.length)]);
         } else {
            var23 = new ResourceLocation(RuinedPortalFeature.STRUCTURE_LOCATION_PORTALS[this.random.nextInt(RuinedPortalFeature.STRUCTURE_LOCATION_PORTALS.length)]);
         }

         StructureTemplate var11 = var3.getOrCreate(var23);
         Rotation var12 = (Rotation)Util.getRandom((Object[])Rotation.values(), this.random);
         Mirror var13 = this.random.nextFloat() < 0.5F ? Mirror.NONE : Mirror.FRONT_BACK;
         BlockPos var14 = new BlockPos(var11.getSize().getX() / 2, 0, var11.getSize().getZ() / 2);
         BlockPos var15 = (new ChunkPos(var4, var5)).getWorldPosition();
         BoundingBox var16 = var11.getBoundingBox(var15, var12, var14, var13);
         Vec3i var17 = var16.getCenter();
         int var18 = var17.getX();
         int var19 = var17.getZ();
         int var20 = var2.getBaseHeight(var18, var19, RuinedPortalPiece.getHeightMapType(var8)) - 1;
         int var21 = RuinedPortalFeature.findSuitableY(this.random, var2, var8, var9.airPocket, var20, var16.getYSpan(), var16);
         BlockPos var22 = new BlockPos(var15.getX(), var21, var15.getZ());
         if (var7.portalType == RuinedPortalFeature.Type.MOUNTAIN || var7.portalType == RuinedPortalFeature.Type.OCEAN || var7.portalType == RuinedPortalFeature.Type.STANDARD) {
            var9.cold = RuinedPortalFeature.isCold(var22, var6);
         }

         this.pieces.add(new RuinedPortalPiece(var22, var8, var9, var23, var11, var12, var13, var14));
         this.calculateBoundingBox();
      }
   }
}

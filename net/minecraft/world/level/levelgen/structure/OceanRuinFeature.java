package net.minecraft.world.level.levelgen.structure;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.RandomScatteredFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class OceanRuinFeature extends RandomScatteredFeature {
   public OceanRuinFeature(Function var1) {
      super(var1);
   }

   public String getFeatureName() {
      return "Ocean_Ruin";
   }

   public int getLookupRange() {
      return 3;
   }

   protected int getSpacing(ChunkGenerator var1) {
      return var1.getSettings().getOceanRuinSpacing();
   }

   protected int getSeparation(ChunkGenerator var1) {
      return var1.getSettings().getOceanRuinSeparation();
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return OceanRuinFeature.OceanRuinStart::new;
   }

   protected int getRandomSalt() {
      return 14357621;
   }

   public static enum Type {
      WARM("warm"),
      COLD("cold");

      private static final Map BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(OceanRuinFeature.Type::getName, (var0) -> {
         return var0;
      }));
      private final String name;

      private Type(String var3) {
         this.name = var3;
      }

      public String getName() {
         return this.name;
      }

      public static OceanRuinFeature.Type byName(String var0) {
         return (OceanRuinFeature.Type)BY_NAME.get(var0);
      }
   }

   public static class OceanRuinStart extends StructureStart {
      public OceanRuinStart(StructureFeature var1, int var2, int var3, BoundingBox var4, int var5, long var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      public void generatePieces(ChunkGenerator var1, StructureManager var2, int var3, int var4, Biome var5) {
         OceanRuinConfiguration var6 = (OceanRuinConfiguration)var1.getStructureConfiguration(var5, Feature.OCEAN_RUIN);
         int var7 = var3 * 16;
         int var8 = var4 * 16;
         BlockPos var9 = new BlockPos(var7, 90, var8);
         Rotation var10 = Rotation.values()[this.random.nextInt(Rotation.values().length)];
         OceanRuinPieces.addPieces(var2, var9, var10, this.pieces, this.random, var6);
         this.calculateBoundingBox();
      }
   }
}

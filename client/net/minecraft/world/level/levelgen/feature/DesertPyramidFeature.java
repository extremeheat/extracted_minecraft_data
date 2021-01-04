package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.DesertPyramidPiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class DesertPyramidFeature extends RandomScatteredFeature<NoneFeatureConfiguration> {
   public DesertPyramidFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public String getFeatureName() {
      return "Desert_Pyramid";
   }

   public int getLookupRange() {
      return 3;
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return DesertPyramidFeature.FeatureStart::new;
   }

   protected int getRandomSalt() {
      return 14357617;
   }

   public static class FeatureStart extends StructureStart {
      public FeatureStart(StructureFeature<?> var1, int var2, int var3, Biome var4, BoundingBox var5, int var6, long var7) {
         super(var1, var2, var3, var4, var5, var6, var7);
      }

      public void generatePieces(ChunkGenerator<?> var1, StructureManager var2, int var3, int var4, Biome var5) {
         DesertPyramidPiece var6 = new DesertPyramidPiece(this.random, var3 * 16, var4 * 16);
         this.pieces.add(var6);
         this.calculateBoundingBox();
      }
   }
}

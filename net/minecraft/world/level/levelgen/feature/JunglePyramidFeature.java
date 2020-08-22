package net.minecraft.world.level.levelgen.feature;

import java.util.function.Function;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.JunglePyramidPiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class JunglePyramidFeature extends RandomScatteredFeature {
   public JunglePyramidFeature(Function var1) {
      super(var1);
   }

   public String getFeatureName() {
      return "Jungle_Pyramid";
   }

   public int getLookupRange() {
      return 3;
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return JunglePyramidFeature.FeatureStart::new;
   }

   protected int getRandomSalt() {
      return 14357619;
   }

   public static class FeatureStart extends StructureStart {
      public FeatureStart(StructureFeature var1, int var2, int var3, BoundingBox var4, int var5, long var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      public void generatePieces(ChunkGenerator var1, StructureManager var2, int var3, int var4, Biome var5) {
         JunglePyramidPiece var6 = new JunglePyramidPiece(this.random, var3 * 16, var4 * 16);
         this.pieces.add(var6);
         this.calculateBoundingBox();
      }
   }
}

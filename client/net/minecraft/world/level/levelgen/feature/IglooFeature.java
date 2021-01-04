package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.IglooPieces;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class IglooFeature extends RandomScatteredFeature<NoneFeatureConfiguration> {
   public IglooFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public String getFeatureName() {
      return "Igloo";
   }

   public int getLookupRange() {
      return 3;
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return IglooFeature.FeatureStart::new;
   }

   protected int getRandomSalt() {
      return 14357618;
   }

   public static class FeatureStart extends StructureStart {
      public FeatureStart(StructureFeature<?> var1, int var2, int var3, Biome var4, BoundingBox var5, int var6, long var7) {
         super(var1, var2, var3, var4, var5, var6, var7);
      }

      public void generatePieces(ChunkGenerator<?> var1, StructureManager var2, int var3, int var4, Biome var5) {
         NoneFeatureConfiguration var6 = (NoneFeatureConfiguration)var1.getStructureConfiguration(var5, Feature.IGLOO);
         int var7 = var3 * 16;
         int var8 = var4 * 16;
         BlockPos var9 = new BlockPos(var7, 90, var8);
         Rotation var10 = Rotation.values()[this.random.nextInt(Rotation.values().length)];
         IglooPieces.addPieces(var2, var9, var10, this.pieces, this.random, var6);
         this.calculateBoundingBox();
      }
   }
}

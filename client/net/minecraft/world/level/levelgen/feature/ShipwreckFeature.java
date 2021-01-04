package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ShipwreckPieces;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class ShipwreckFeature extends RandomScatteredFeature<ShipwreckConfiguration> {
   public ShipwreckFeature(Function<Dynamic<?>, ? extends ShipwreckConfiguration> var1) {
      super(var1);
   }

   public String getFeatureName() {
      return "Shipwreck";
   }

   public int getLookupRange() {
      return 3;
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return ShipwreckFeature.FeatureStart::new;
   }

   protected int getRandomSalt() {
      return 165745295;
   }

   protected int getSpacing(ChunkGenerator<?> var1) {
      return var1.getSettings().getShipwreckSpacing();
   }

   protected int getSeparation(ChunkGenerator<?> var1) {
      return var1.getSettings().getShipwreckSeparation();
   }

   public static class FeatureStart extends StructureStart {
      public FeatureStart(StructureFeature<?> var1, int var2, int var3, Biome var4, BoundingBox var5, int var6, long var7) {
         super(var1, var2, var3, var4, var5, var6, var7);
      }

      public void generatePieces(ChunkGenerator<?> var1, StructureManager var2, int var3, int var4, Biome var5) {
         ShipwreckConfiguration var6 = (ShipwreckConfiguration)var1.getStructureConfiguration(var5, Feature.SHIPWRECK);
         Rotation var7 = Rotation.values()[this.random.nextInt(Rotation.values().length)];
         BlockPos var8 = new BlockPos(var3 * 16, 90, var4 * 16);
         ShipwreckPieces.addPieces(var2, var8, var7, this.pieces, this.random, var6);
         this.calculateBoundingBox();
      }
   }
}

package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

public abstract class NoiseAffectingStructureFeature<C extends FeatureConfiguration> extends StructureFeature<C> {
   public NoiseAffectingStructureFeature(Codec<C> var1, PieceGeneratorSupplier<C> var2) {
      super(var1, var2);
   }

   public NoiseAffectingStructureFeature(Codec<C> var1, PieceGeneratorSupplier<C> var2, PostPlacementProcessor var3) {
      super(var1, var2, var3);
   }

   public BoundingBox adjustBoundingBox(BoundingBox var1) {
      return super.adjustBoundingBox(var1).inflatedBy(12);
   }
}

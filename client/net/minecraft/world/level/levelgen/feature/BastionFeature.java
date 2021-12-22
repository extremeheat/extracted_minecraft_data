package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

public class BastionFeature extends JigsawFeature {
   private static final int BASTION_SPAWN_HEIGHT = 33;

   public BastionFeature(Codec<JigsawConfiguration> var1) {
      super(var1, 33, false, false, BastionFeature::checkLocation);
   }

   private static boolean checkLocation(PieceGeneratorSupplier.Context<JigsawConfiguration> var0) {
      WorldgenRandom var1 = new WorldgenRandom(new LegacyRandomSource(0L));
      var1.setLargeFeatureSeed(var0.seed(), var0.chunkPos().field_504, var0.chunkPos().field_505);
      return var1.nextInt(5) >= 2;
   }
}

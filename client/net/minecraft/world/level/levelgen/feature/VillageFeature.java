package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;

public class VillageFeature extends JigsawFeature {
   public VillageFeature(Codec<JigsawConfiguration> var1) {
      super(var1, 0, true, true);
   }
}

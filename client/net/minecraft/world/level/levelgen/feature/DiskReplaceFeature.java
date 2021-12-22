package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;

public class DiskReplaceFeature extends BaseDiskFeature {
   public DiskReplaceFeature(Codec<DiskConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<DiskConfiguration> var1) {
      return !var1.level().getFluidState(var1.origin()).method_56(FluidTags.WATER) ? false : super.place(var1);
   }
}

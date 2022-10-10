package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.placement.BasePlacement;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class CompositeFeature<F extends IFeatureConfig, D extends IPlacementConfig> extends Feature<NoFeatureConfig> {
   protected final Feature<F> field_202350_a;
   protected final F field_202351_b;
   protected final BasePlacement<D> field_202352_c;
   protected final D field_202353_d;

   public CompositeFeature(Feature<F> var1, F var2, BasePlacement<D> var3, D var4) {
      super();
      this.field_202351_b = var2;
      this.field_202353_d = var4;
      this.field_202352_c = var3;
      this.field_202350_a = var1;
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      return this.field_202352_c.func_201491_a_(var1, var2, var3, var4, this.field_202353_d, this.field_202350_a, this.field_202351_b);
   }

   public String toString() {
      return String.format("< %s [%s | %s] >", this.getClass().getSimpleName(), this.field_202352_c, this.field_202350_a);
   }

   public Feature<F> func_202349_a() {
      return this.field_202350_a;
   }
}

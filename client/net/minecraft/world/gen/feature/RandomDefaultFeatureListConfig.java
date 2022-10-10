package net.minecraft.world.gen.feature;

public class RandomDefaultFeatureListConfig implements IFeatureConfig {
   public final Feature<?>[] field_202449_a;
   public final IFeatureConfig[] field_202450_b;
   public final float[] field_202451_c;
   public final Feature<?> field_202452_d;
   public final IFeatureConfig field_202453_f;

   public <FC extends IFeatureConfig> RandomDefaultFeatureListConfig(Feature<?>[] var1, IFeatureConfig[] var2, float[] var3, Feature<FC> var4, FC var5) {
      super();
      this.field_202449_a = var1;
      this.field_202450_b = var2;
      this.field_202451_c = var3;
      this.field_202452_d = var4;
      this.field_202453_f = var5;
   }
}

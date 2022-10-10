package net.minecraft.world.gen.feature;

public class TwoFeatureChoiceConfig implements IFeatureConfig {
   public final Feature<?> field_202445_a;
   public final IFeatureConfig field_202446_b;
   public final Feature<?> field_202447_c;
   public final IFeatureConfig field_202448_d;

   public <FC extends IFeatureConfig> TwoFeatureChoiceConfig(Feature<?> var1, IFeatureConfig var2, Feature<?> var3, IFeatureConfig var4) {
      super();
      this.field_202445_a = var1;
      this.field_202446_b = var2;
      this.field_202447_c = var3;
      this.field_202448_d = var4;
   }
}

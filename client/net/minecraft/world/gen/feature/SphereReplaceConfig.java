package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.block.Block;

public class SphereReplaceConfig implements IFeatureConfig {
   public final Block field_202431_a;
   public final int field_202432_b;
   public final int field_202433_c;
   public final List<Block> field_202434_d;

   public SphereReplaceConfig(Block var1, int var2, int var3, List<Block> var4) {
      super();
      this.field_202431_a = var1;
      this.field_202432_b = var2;
      this.field_202433_c = var3;
      this.field_202434_d = var4;
   }
}

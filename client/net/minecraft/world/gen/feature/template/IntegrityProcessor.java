package net.minecraft.world.gen.feature.template;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class IntegrityProcessor implements ITemplateProcessor {
   private final float field_189944_a;
   private final Random field_189945_b;

   public IntegrityProcessor(BlockPos var1, PlacementSettings var2) {
      super();
      this.field_189944_a = var2.func_189948_f();
      this.field_189945_b = var2.func_189947_a(var1);
   }

   @Nullable
   public Template.BlockInfo func_189943_a(IBlockReader var1, BlockPos var2, Template.BlockInfo var3) {
      return this.field_189944_a < 1.0F && this.field_189945_b.nextFloat() > this.field_189944_a ? null : var3;
   }
}

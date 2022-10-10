package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.world.gen.area.IArea;

public abstract class LayerContext<R extends IArea> implements IContextExtended<R> {
   private long field_202701_b;
   private long field_202702_c;
   protected long field_202700_a;
   protected NoiseGeneratorImproved field_205590_b;

   public LayerContext(long var1) {
      super();
      this.field_202700_a = var1;
      this.field_202700_a *= this.field_202700_a * 6364136223846793005L + 1442695040888963407L;
      this.field_202700_a += var1;
      this.field_202700_a *= this.field_202700_a * 6364136223846793005L + 1442695040888963407L;
      this.field_202700_a += var1;
      this.field_202700_a *= this.field_202700_a * 6364136223846793005L + 1442695040888963407L;
      this.field_202700_a += var1;
   }

   public void func_202699_a(long var1) {
      this.field_202701_b = var1;
      this.field_202701_b *= this.field_202701_b * 6364136223846793005L + 1442695040888963407L;
      this.field_202701_b += this.field_202700_a;
      this.field_202701_b *= this.field_202701_b * 6364136223846793005L + 1442695040888963407L;
      this.field_202701_b += this.field_202700_a;
      this.field_202701_b *= this.field_202701_b * 6364136223846793005L + 1442695040888963407L;
      this.field_202701_b += this.field_202700_a;
      this.field_205590_b = new NoiseGeneratorImproved(new Random(var1));
   }

   public void func_202698_a(long var1, long var3) {
      this.field_202702_c = this.field_202701_b;
      this.field_202702_c *= this.field_202702_c * 6364136223846793005L + 1442695040888963407L;
      this.field_202702_c += var1;
      this.field_202702_c *= this.field_202702_c * 6364136223846793005L + 1442695040888963407L;
      this.field_202702_c += var3;
      this.field_202702_c *= this.field_202702_c * 6364136223846793005L + 1442695040888963407L;
      this.field_202702_c += var1;
      this.field_202702_c *= this.field_202702_c * 6364136223846793005L + 1442695040888963407L;
      this.field_202702_c += var3;
   }

   public int func_202696_a(int var1) {
      int var2 = (int)((this.field_202702_c >> 24) % (long)var1);
      if (var2 < 0) {
         var2 += var1;
      }

      this.field_202702_c *= this.field_202702_c * 6364136223846793005L + 1442695040888963407L;
      this.field_202702_c += this.field_202701_b;
      return var2;
   }

   public int func_202697_a(int... var1) {
      return var1[this.func_202696_a(var1.length)];
   }

   public NoiseGeneratorImproved func_205589_a() {
      return this.field_205590_b;
   }
}

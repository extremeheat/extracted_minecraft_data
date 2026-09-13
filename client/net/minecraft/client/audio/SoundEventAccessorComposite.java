package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.util.ResourceLocation;

public class SoundEventAccessorComposite implements ISoundEventAccessor {
   private final List field_148736_a = Lists.newArrayList();
   private final Random field_148734_b = new Random();
   private final ResourceLocation field_148735_c;
   private final SoundCategory field_148732_d;
   private double field_148733_e;
   private double field_148731_f;

   public SoundEventAccessorComposite(ResourceLocation var1, double var2, double var4, SoundCategory var6) {
      super();
      this.field_148735_c = var1;
      this.field_148731_f = var4;
      this.field_148733_e = var2;
      this.field_148732_d = var6;
   }

   @Override
   public int func_148721_a() {
      int var1 = 0;

      for(ISoundEventAccessor var3 : this.field_148736_a) {
         var1 += var3.func_148721_a();
      }

      return var1;
   }

   public SoundPoolEntry func_148720_g() {
      int var1 = this.func_148721_a();
      if (!this.field_148736_a.isEmpty() && var1 != 0) {
         int var2 = this.field_148734_b.nextInt(var1);

         for(ISoundEventAccessor var4 : this.field_148736_a) {
            var2 -= var4.func_148721_a();
            if (var2 < 0) {
               SoundPoolEntry var5 = (SoundPoolEntry)var4.func_148720_g();
               var5.func_148651_a(var5.func_148650_b() * this.field_148733_e);
               var5.func_148647_b(var5.func_148649_c() * this.field_148731_f);
               return var5;
            }
         }

         return SoundHandler.field_147700_a;
      } else {
         return SoundHandler.field_147700_a;
      }
   }

   public void func_148727_a(ISoundEventAccessor var1) {
      this.field_148736_a.add(var1);
   }

   public ResourceLocation func_148729_c() {
      return this.field_148735_c;
   }

   public SoundCategory func_148728_d() {
      return this.field_148732_d;
   }
}

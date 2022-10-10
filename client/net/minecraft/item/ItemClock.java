package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ItemClock extends Item {
   public ItemClock(Item.Properties var1) {
      super(var1);
      this.func_185043_a(new ResourceLocation("time"), new IItemPropertyGetter() {
         private double field_185088_a;
         private double field_185089_b;
         private long field_185090_c;

         public float call(ItemStack var1, @Nullable World var2, @Nullable EntityLivingBase var3) {
            boolean var4 = var3 != null;
            Object var5 = var4 ? var3 : var1.func_82836_z();
            if (var2 == null && var5 != null) {
               var2 = ((Entity)var5).field_70170_p;
            }

            if (var2 == null) {
               return 0.0F;
            } else {
               double var6;
               if (var2.field_73011_w.func_76569_d()) {
                  var6 = (double)var2.func_72826_c(1.0F);
               } else {
                  var6 = Math.random();
               }

               var6 = this.func_185087_a(var2, var6);
               return (float)var6;
            }
         }

         private double func_185087_a(World var1, double var2) {
            if (var1.func_82737_E() != this.field_185090_c) {
               this.field_185090_c = var1.func_82737_E();
               double var4 = var2 - this.field_185088_a;
               var4 = MathHelper.func_191273_b(var4 + 0.5D, 1.0D) - 0.5D;
               this.field_185089_b += var4 * 0.1D;
               this.field_185089_b *= 0.9D;
               this.field_185088_a = MathHelper.func_191273_b(this.field_185088_a + this.field_185089_b, 1.0D);
            }

            return this.field_185088_a;
         }
      });
   }
}

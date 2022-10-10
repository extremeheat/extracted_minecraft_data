package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ItemCompass extends Item {
   public ItemCompass(Item.Properties var1) {
      super(var1);
      this.func_185043_a(new ResourceLocation("angle"), new IItemPropertyGetter() {
         private double field_185095_a;
         private double field_185096_b;
         private long field_185097_c;

         public float call(ItemStack var1, @Nullable World var2, @Nullable EntityLivingBase var3) {
            if (var3 == null && !var1.func_82839_y()) {
               return 0.0F;
            } else {
               boolean var4 = var3 != null;
               Object var5 = var4 ? var3 : var1.func_82836_z();
               if (var2 == null) {
                  var2 = ((Entity)var5).field_70170_p;
               }

               double var6;
               if (var2.field_73011_w.func_76569_d()) {
                  double var8 = var4 ? (double)((Entity)var5).field_70177_z : this.func_185094_a((EntityItemFrame)var5);
                  var8 = MathHelper.func_191273_b(var8 / 360.0D, 1.0D);
                  double var10 = this.func_185092_a(var2, (Entity)var5) / 6.2831854820251465D;
                  var6 = 0.5D - (var8 - 0.25D - var10);
               } else {
                  var6 = Math.random();
               }

               if (var4) {
                  var6 = this.func_185093_a(var2, var6);
               }

               return MathHelper.func_188207_b((float)var6, 1.0F);
            }
         }

         private double func_185093_a(World var1, double var2) {
            if (var1.func_82737_E() != this.field_185097_c) {
               this.field_185097_c = var1.func_82737_E();
               double var4 = var2 - this.field_185095_a;
               var4 = MathHelper.func_191273_b(var4 + 0.5D, 1.0D) - 0.5D;
               this.field_185096_b += var4 * 0.1D;
               this.field_185096_b *= 0.8D;
               this.field_185095_a = MathHelper.func_191273_b(this.field_185095_a + this.field_185096_b, 1.0D);
            }

            return this.field_185095_a;
         }

         private double func_185094_a(EntityItemFrame var1) {
            return (double)MathHelper.func_188209_b(180 + var1.field_174860_b.func_176736_b() * 90);
         }

         private double func_185092_a(IWorld var1, Entity var2) {
            BlockPos var3 = var1.func_175694_M();
            return Math.atan2((double)var3.func_177952_p() - var2.field_70161_v, (double)var3.func_177958_n() - var2.field_70165_t);
         }
      });
   }
}

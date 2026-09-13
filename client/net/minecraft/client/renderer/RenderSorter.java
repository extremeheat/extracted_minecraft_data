package net.minecraft.client.renderer;

import java.util.Comparator;
import net.minecraft.entity.EntityLivingBase;

public class RenderSorter implements Comparator {
   private EntityLivingBase field_78945_a;

   public RenderSorter(EntityLivingBase var1) {
      super();
      this.field_78945_a = var1;
   }

   public int compare(WorldRenderer var1, WorldRenderer var2) {
      if (var1.field_78927_l && !var2.field_78927_l) {
         return 1;
      } else if (var2.field_78927_l && !var1.field_78927_l) {
         return -1;
      } else {
         double var3 = (double)var1.func_78912_a(this.field_78945_a);
         double var5 = (double)var2.func_78912_a(this.field_78945_a);
         if (var3 < var5) {
            return 1;
         } else if (var3 > var5) {
            return -1;
         } else {
            return var1.field_78937_s < var2.field_78937_s ? 1 : -1;
         }
      }
   }
}

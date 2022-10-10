package net.minecraft.entity.ai;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.util.math.Vec3d;

public class EntityAILlamaFollowCaravan extends EntityAIBase {
   public EntityLlama field_190859_a;
   private double field_190860_b;
   private int field_190861_c;

   public EntityAILlamaFollowCaravan(EntityLlama var1, double var2) {
      super();
      this.field_190859_a = var1;
      this.field_190860_b = var2;
      this.func_75248_a(1);
   }

   public boolean func_75250_a() {
      if (!this.field_190859_a.func_110167_bD() && !this.field_190859_a.func_190718_dR()) {
         List var1 = this.field_190859_a.field_70170_p.func_72872_a(this.field_190859_a.getClass(), this.field_190859_a.func_174813_aQ().func_72314_b(9.0D, 4.0D, 9.0D));
         EntityLlama var2 = null;
         double var3 = 1.7976931348623157E308D;
         Iterator var5 = var1.iterator();

         EntityLlama var6;
         double var7;
         while(var5.hasNext()) {
            var6 = (EntityLlama)var5.next();
            if (var6.func_190718_dR() && !var6.func_190712_dQ()) {
               var7 = this.field_190859_a.func_70068_e(var6);
               if (var7 <= var3) {
                  var3 = var7;
                  var2 = var6;
               }
            }
         }

         if (var2 == null) {
            var5 = var1.iterator();

            while(var5.hasNext()) {
               var6 = (EntityLlama)var5.next();
               if (var6.func_110167_bD() && !var6.func_190712_dQ()) {
                  var7 = this.field_190859_a.func_70068_e(var6);
                  if (var7 <= var3) {
                     var3 = var7;
                     var2 = var6;
                  }
               }
            }
         }

         if (var2 == null) {
            return false;
         } else if (var3 < 4.0D) {
            return false;
         } else if (!var2.func_110167_bD() && !this.func_190858_a(var2, 1)) {
            return false;
         } else {
            this.field_190859_a.func_190715_a(var2);
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean func_75253_b() {
      if (this.field_190859_a.func_190718_dR() && this.field_190859_a.func_190716_dS().func_70089_S() && this.func_190858_a(this.field_190859_a, 0)) {
         double var1 = this.field_190859_a.func_70068_e(this.field_190859_a.func_190716_dS());
         if (var1 > 676.0D) {
            if (this.field_190860_b <= 3.0D) {
               this.field_190860_b *= 1.2D;
               this.field_190861_c = 40;
               return true;
            }

            if (this.field_190861_c == 0) {
               return false;
            }
         }

         if (this.field_190861_c > 0) {
            --this.field_190861_c;
         }

         return true;
      } else {
         return false;
      }
   }

   public void func_75251_c() {
      this.field_190859_a.func_190709_dP();
      this.field_190860_b = 2.1D;
   }

   public void func_75246_d() {
      if (this.field_190859_a.func_190718_dR()) {
         EntityLlama var1 = this.field_190859_a.func_190716_dS();
         double var2 = (double)this.field_190859_a.func_70032_d(var1);
         float var4 = 2.0F;
         Vec3d var5 = (new Vec3d(var1.field_70165_t - this.field_190859_a.field_70165_t, var1.field_70163_u - this.field_190859_a.field_70163_u, var1.field_70161_v - this.field_190859_a.field_70161_v)).func_72432_b().func_186678_a(Math.max(var2 - 2.0D, 0.0D));
         this.field_190859_a.func_70661_as().func_75492_a(this.field_190859_a.field_70165_t + var5.field_72450_a, this.field_190859_a.field_70163_u + var5.field_72448_b, this.field_190859_a.field_70161_v + var5.field_72449_c, this.field_190860_b);
      }
   }

   private boolean func_190858_a(EntityLlama var1, int var2) {
      if (var2 > 8) {
         return false;
      } else if (var1.func_190718_dR()) {
         if (var1.func_190716_dS().func_110167_bD()) {
            return true;
         } else {
            EntityLlama var10001 = var1.func_190716_dS();
            ++var2;
            return this.func_190858_a(var10001, var2);
         }
      } else {
         return false;
      }
   }
}

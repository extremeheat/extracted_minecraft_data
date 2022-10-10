package net.minecraft.entity.ai;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAIHurtByTarget extends EntityAITarget {
   private final boolean field_75312_a;
   private int field_142052_b;
   private final Class<?>[] field_179447_c;

   public EntityAIHurtByTarget(EntityCreature var1, boolean var2, Class<?>... var3) {
      super(var1, true);
      this.field_75312_a = var2;
      this.field_179447_c = var3;
      this.func_75248_a(1);
   }

   public boolean func_75250_a() {
      int var1 = this.field_75299_d.func_142015_aE();
      EntityLivingBase var2 = this.field_75299_d.func_70643_av();
      return var1 != this.field_142052_b && var2 != null && this.func_75296_a(var2, false);
   }

   public void func_75249_e() {
      this.field_75299_d.func_70624_b(this.field_75299_d.func_70643_av());
      this.field_188509_g = this.field_75299_d.func_70638_az();
      this.field_142052_b = this.field_75299_d.func_142015_aE();
      this.field_188510_h = 300;
      if (this.field_75312_a) {
         this.func_190105_f();
      }

      super.func_75249_e();
   }

   protected void func_190105_f() {
      double var1 = this.func_111175_f();
      List var3 = this.field_75299_d.field_70170_p.func_72872_a(this.field_75299_d.getClass(), (new AxisAlignedBB(this.field_75299_d.field_70165_t, this.field_75299_d.field_70163_u, this.field_75299_d.field_70161_v, this.field_75299_d.field_70165_t + 1.0D, this.field_75299_d.field_70163_u + 1.0D, this.field_75299_d.field_70161_v + 1.0D)).func_72314_b(var1, 10.0D, var1));
      Iterator var4 = var3.iterator();

      while(true) {
         EntityCreature var5;
         do {
            do {
               do {
                  do {
                     if (!var4.hasNext()) {
                        return;
                     }

                     var5 = (EntityCreature)var4.next();
                  } while(this.field_75299_d == var5);
               } while(var5.func_70638_az() != null);
            } while(this.field_75299_d instanceof EntityTameable && ((EntityTameable)this.field_75299_d).func_70902_q() != ((EntityTameable)var5).func_70902_q());
         } while(var5.func_184191_r(this.field_75299_d.func_70643_av()));

         boolean var6 = false;
         Class[] var7 = this.field_179447_c;
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            Class var10 = var7[var9];
            if (var5.getClass() == var10) {
               var6 = true;
               break;
            }
         }

         if (!var6) {
            this.func_179446_a(var5, this.field_75299_d.func_70643_av());
         }
      }
   }

   protected void func_179446_a(EntityCreature var1, EntityLivingBase var2) {
      var1.func_70624_b(var2);
   }
}

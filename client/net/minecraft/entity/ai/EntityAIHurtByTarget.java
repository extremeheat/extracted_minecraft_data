package net.minecraft.entity.ai;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;

public class EntityAIHurtByTarget extends EntityAITarget {
   private boolean field_75312_a;
   private int field_142052_b;
   private final Class[] field_179447_c;

   public EntityAIHurtByTarget(EntityCreature var1, boolean var2, Class... var3) {
      super(var1, false);
      this.field_75312_a = var2;
      this.field_179447_c = var3;
      this.func_75248_a(1);
   }

   public boolean func_75250_a() {
      int var1 = this.field_75299_d.func_142015_aE();
      return var1 != this.field_142052_b && this.func_75296_a(this.field_75299_d.func_70643_av(), false);
   }

   public void func_75249_e() {
      this.field_75299_d.func_70624_b(this.field_75299_d.func_70643_av());
      this.field_142052_b = this.field_75299_d.func_142015_aE();
      if (this.field_75312_a) {
         double var1 = this.func_111175_f();
         List var3 = this.field_75299_d.field_70170_p.func_72872_a(this.field_75299_d.getClass(), (new AxisAlignedBB(this.field_75299_d.field_70165_t, this.field_75299_d.field_70163_u, this.field_75299_d.field_70161_v, this.field_75299_d.field_70165_t + 1.0D, this.field_75299_d.field_70163_u + 1.0D, this.field_75299_d.field_70161_v + 1.0D)).func_72314_b(var1, 10.0D, var1));
         Iterator var4 = var3.iterator();

         label46:
         while(true) {
            EntityCreature var5;
            do {
               do {
                  do {
                     if (!var4.hasNext()) {
                        break label46;
                     }

                     var5 = (EntityCreature)var4.next();
                  } while(this.field_75299_d == var5);
               } while(var5.func_70638_az() != null);
            } while(var5.func_142014_c(this.field_75299_d.func_70643_av()));

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

      super.func_75249_e();
   }

   protected void func_179446_a(EntityCreature var1, EntityLivingBase var2) {
      var1.func_70624_b(var2);
   }
}

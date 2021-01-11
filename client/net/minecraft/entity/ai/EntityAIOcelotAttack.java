package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntityAIOcelotAttack extends EntityAIBase {
   World field_75411_a;
   EntityLiving field_75409_b;
   EntityLivingBase field_75410_c;
   int field_75408_d;

   public EntityAIOcelotAttack(EntityLiving var1) {
      super();
      this.field_75409_b = var1;
      this.field_75411_a = var1.field_70170_p;
      this.func_75248_a(3);
   }

   public boolean func_75250_a() {
      EntityLivingBase var1 = this.field_75409_b.func_70638_az();
      if (var1 == null) {
         return false;
      } else {
         this.field_75410_c = var1;
         return true;
      }
   }

   public boolean func_75253_b() {
      if (!this.field_75410_c.func_70089_S()) {
         return false;
      } else if (this.field_75409_b.func_70068_e(this.field_75410_c) > 225.0D) {
         return false;
      } else {
         return !this.field_75409_b.func_70661_as().func_75500_f() || this.func_75250_a();
      }
   }

   public void func_75251_c() {
      this.field_75410_c = null;
      this.field_75409_b.func_70661_as().func_75499_g();
   }

   public void func_75246_d() {
      this.field_75409_b.func_70671_ap().func_75651_a(this.field_75410_c, 30.0F, 30.0F);
      double var1 = (double)(this.field_75409_b.field_70130_N * 2.0F * this.field_75409_b.field_70130_N * 2.0F);
      double var3 = this.field_75409_b.func_70092_e(this.field_75410_c.field_70165_t, this.field_75410_c.func_174813_aQ().field_72338_b, this.field_75410_c.field_70161_v);
      double var5 = 0.8D;
      if (var3 > var1 && var3 < 16.0D) {
         var5 = 1.33D;
      } else if (var3 < 225.0D) {
         var5 = 0.6D;
      }

      this.field_75409_b.func_70661_as().func_75497_a(this.field_75410_c, var5);
      this.field_75408_d = Math.max(this.field_75408_d - 1, 0);
      if (var3 <= var1) {
         if (this.field_75408_d <= 0) {
            this.field_75408_d = 20;
            this.field_75409_b.func_70652_k(this.field_75410_c);
         }
      }
   }
}

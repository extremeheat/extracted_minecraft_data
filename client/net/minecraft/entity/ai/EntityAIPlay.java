package net.minecraft.entity.ai;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.Vec3;

public class EntityAIPlay extends EntityAIBase {
   private EntityVillager field_75262_a;
   private EntityLivingBase field_75260_b;
   private double field_75261_c;
   private int field_75259_d;

   public EntityAIPlay(EntityVillager var1, double var2) {
      super();
      this.field_75262_a = var1;
      this.field_75261_c = var2;
      this.func_75248_a(1);
   }

   public boolean func_75250_a() {
      if (this.field_75262_a.func_70874_b() >= 0) {
         return false;
      } else if (this.field_75262_a.func_70681_au().nextInt(400) != 0) {
         return false;
      } else {
         List var1 = this.field_75262_a.field_70170_p.func_72872_a(EntityVillager.class, this.field_75262_a.func_174813_aQ().func_72314_b(6.0D, 3.0D, 6.0D));
         double var2 = 1.7976931348623157E308D;
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            EntityVillager var5 = (EntityVillager)var4.next();
            if (var5 != this.field_75262_a && !var5.func_70945_p() && var5.func_70874_b() < 0) {
               double var6 = var5.func_70068_e(this.field_75262_a);
               if (var6 <= var2) {
                  var2 = var6;
                  this.field_75260_b = var5;
               }
            }
         }

         if (this.field_75260_b == null) {
            Vec3 var8 = RandomPositionGenerator.func_75463_a(this.field_75262_a, 16, 3);
            if (var8 == null) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean func_75253_b() {
      return this.field_75259_d > 0;
   }

   public void func_75249_e() {
      if (this.field_75260_b != null) {
         this.field_75262_a.func_70939_f(true);
      }

      this.field_75259_d = 1000;
   }

   public void func_75251_c() {
      this.field_75262_a.func_70939_f(false);
      this.field_75260_b = null;
   }

   public void func_75246_d() {
      --this.field_75259_d;
      if (this.field_75260_b != null) {
         if (this.field_75262_a.func_70068_e(this.field_75260_b) > 4.0D) {
            this.field_75262_a.func_70661_as().func_75497_a(this.field_75260_b, this.field_75261_c);
         }
      } else if (this.field_75262_a.func_70661_as().func_75500_f()) {
         Vec3 var1 = RandomPositionGenerator.func_75463_a(this.field_75262_a, 16, 3);
         if (var1 == null) {
            return;
         }

         this.field_75262_a.func_70661_as().func_75492_a(var1.field_72450_a, var1.field_72448_b, var1.field_72449_c, this.field_75261_c);
      }

   }
}

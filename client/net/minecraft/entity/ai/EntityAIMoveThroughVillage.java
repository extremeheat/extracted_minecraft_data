package net.minecraft.entity.ai;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIMoveThroughVillage extends EntityAIBase {
   private EntityCreature field_75420_a;
   private double field_75418_b;
   private PathEntity field_75419_c;
   private VillageDoorInfo field_75416_d;
   private boolean field_75417_e;
   private List field_75415_f = new ArrayList();

   public EntityAIMoveThroughVillage(EntityCreature var1, double var2, boolean var4) {
      super();
      this.field_75420_a = var1;
      this.field_75418_b = var2;
      this.field_75417_e = var4;
      this.func_75248_a(1);
   }

   @Override
   public boolean func_75250_a() {
      this.func_75414_f();
      if (this.field_75417_e && this.field_75420_a.field_70170_p.func_72935_r()) {
         return false;
      } else {
         Village var1 = this.field_75420_a
            .field_70170_p
            .field_72982_D
            .func_75550_a(
               MathHelper.func_76128_c(this.field_75420_a.field_70165_t),
               MathHelper.func_76128_c(this.field_75420_a.field_70163_u),
               MathHelper.func_76128_c(this.field_75420_a.field_70161_v),
               0
            );
         if (var1 == null) {
            return false;
         } else {
            this.field_75416_d = this.func_75412_a(var1);
            if (this.field_75416_d == null) {
               return false;
            } else {
               boolean var2 = this.field_75420_a.func_70661_as().func_75507_c();
               this.field_75420_a.func_70661_as().func_75498_b(false);
               this.field_75419_c = this.field_75420_a
                  .func_70661_as()
                  .func_75488_a((double)this.field_75416_d.field_75481_a, (double)this.field_75416_d.field_75479_b, (double)this.field_75416_d.field_75480_c);
               this.field_75420_a.func_70661_as().func_75498_b(var2);
               if (this.field_75419_c != null) {
                  return true;
               } else {
                  Vec3 var3 = RandomPositionGenerator.func_75464_a(
                     this.field_75420_a,
                     10,
                     7,
                     Vec3.func_72443_a(
                        (double)this.field_75416_d.field_75481_a, (double)this.field_75416_d.field_75479_b, (double)this.field_75416_d.field_75480_c
                     )
                  );
                  if (var3 == null) {
                     return false;
                  } else {
                     this.field_75420_a.func_70661_as().func_75498_b(false);
                     this.field_75419_c = this.field_75420_a.func_70661_as().func_75488_a(var3.field_72450_a, var3.field_72448_b, var3.field_72449_c);
                     this.field_75420_a.func_70661_as().func_75498_b(var2);
                     return this.field_75419_c != null;
                  }
               }
            }
         }
      }
   }

   @Override
   public boolean func_75253_b() {
      if (this.field_75420_a.func_70661_as().func_75500_f()) {
         return false;
      } else {
         float var1 = this.field_75420_a.field_70130_N + 4.0F;
         return this.field_75420_a
               .func_70092_e((double)this.field_75416_d.field_75481_a, (double)this.field_75416_d.field_75479_b, (double)this.field_75416_d.field_75480_c)
            > (double)(var1 * var1);
      }
   }

   @Override
   public void func_75249_e() {
      this.field_75420_a.func_70661_as().func_75484_a(this.field_75419_c, this.field_75418_b);
   }

   @Override
   public void func_75251_c() {
      if (this.field_75420_a.func_70661_as().func_75500_f()
         || this.field_75420_a
               .func_70092_e((double)this.field_75416_d.field_75481_a, (double)this.field_75416_d.field_75479_b, (double)this.field_75416_d.field_75480_c)
            < 16.0) {
         this.field_75415_f.add(this.field_75416_d);
      }
   }

   private VillageDoorInfo func_75412_a(Village var1) {
      VillageDoorInfo var2 = null;
      int var3 = 2147483647;

      for(VillageDoorInfo var6 : var1.func_75558_f()) {
         int var7 = var6.func_75474_b(
            MathHelper.func_76128_c(this.field_75420_a.field_70165_t),
            MathHelper.func_76128_c(this.field_75420_a.field_70163_u),
            MathHelper.func_76128_c(this.field_75420_a.field_70161_v)
         );
         if (var7 < var3 && !this.func_75413_a(var6)) {
            var2 = var6;
            var3 = var7;
         }
      }

      return var2;
   }

   private boolean func_75413_a(VillageDoorInfo var1) {
      for(VillageDoorInfo var3 : this.field_75415_f) {
         if (var1.field_75481_a == var3.field_75481_a && var1.field_75479_b == var3.field_75479_b && var1.field_75480_c == var3.field_75480_c) {
            return true;
         }
      }

      return false;
   }

   private void func_75414_f() {
      if (this.field_75415_f.size() > 15) {
         this.field_75415_f.remove(0);
      }
   }
}

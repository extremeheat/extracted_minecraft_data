package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.Vec3;

public class EntityAIWander extends EntityAIBase {
   private EntityCreature field_75457_a;
   private double field_75455_b;
   private double field_75456_c;
   private double field_75453_d;
   private double field_75454_e;

   public EntityAIWander(EntityCreature var1, double var2) {
      super();
      this.field_75457_a = var1;
      this.field_75454_e = var2;
      this.func_75248_a(1);
   }

   @Override
   public boolean func_75250_a() {
      if (this.field_75457_a.func_70654_ax() >= 100) {
         return false;
      } else if (this.field_75457_a.func_70681_au().nextInt(120) != 0) {
         return false;
      } else {
         Vec3 var1 = RandomPositionGenerator.func_75463_a(this.field_75457_a, 10, 7);
         if (var1 == null) {
            return false;
         } else {
            this.field_75455_b = var1.field_72450_a;
            this.field_75456_c = var1.field_72448_b;
            this.field_75453_d = var1.field_72449_c;
            return true;
         }
      }
   }

   @Override
   public boolean func_75253_b() {
      return !this.field_75457_a.func_70661_as().func_75500_f();
   }

   @Override
   public void func_75249_e() {
      this.field_75457_a.func_70661_as().func_75492_a(this.field_75455_b, this.field_75456_c, this.field_75453_d, this.field_75454_e);
   }
}

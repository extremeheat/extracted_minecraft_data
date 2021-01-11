package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.Vec3;

public class EntityAIPanic extends EntityAIBase {
   private EntityCreature field_75267_a;
   protected double field_75265_b;
   private double field_75266_c;
   private double field_75263_d;
   private double field_75264_e;

   public EntityAIPanic(EntityCreature var1, double var2) {
      super();
      this.field_75267_a = var1;
      this.field_75265_b = var2;
      this.func_75248_a(1);
   }

   public boolean func_75250_a() {
      if (this.field_75267_a.func_70643_av() == null && !this.field_75267_a.func_70027_ad()) {
         return false;
      } else {
         Vec3 var1 = RandomPositionGenerator.func_75463_a(this.field_75267_a, 5, 4);
         if (var1 == null) {
            return false;
         } else {
            this.field_75266_c = var1.field_72450_a;
            this.field_75263_d = var1.field_72448_b;
            this.field_75264_e = var1.field_72449_c;
            return true;
         }
      }
   }

   public void func_75249_e() {
      this.field_75267_a.func_70661_as().func_75492_a(this.field_75266_c, this.field_75263_d, this.field_75264_e, this.field_75265_b);
   }

   public boolean func_75253_b() {
      return !this.field_75267_a.func_70661_as().func_75500_f();
   }
}

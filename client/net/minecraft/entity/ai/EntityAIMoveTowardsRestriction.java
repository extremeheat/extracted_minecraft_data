package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;

public class EntityAIMoveTowardsRestriction extends EntityAIBase {
   private EntityCreature field_75436_a;
   private double field_75434_b;
   private double field_75435_c;
   private double field_75432_d;
   private double field_75433_e;

   public EntityAIMoveTowardsRestriction(EntityCreature var1, double var2) {
      super();
      this.field_75436_a = var1;
      this.field_75433_e = var2;
      this.func_75248_a(1);
   }

   @Override
   public boolean func_75250_a() {
      if (this.field_75436_a.func_110173_bK()) {
         return false;
      } else {
         ChunkCoordinates var1 = this.field_75436_a.func_110172_bL();
         Vec3 var2 = RandomPositionGenerator.func_75464_a(
            this.field_75436_a, 16, 7, Vec3.func_72443_a((double)var1.field_71574_a, (double)var1.field_71572_b, (double)var1.field_71573_c)
         );
         if (var2 == null) {
            return false;
         } else {
            this.field_75434_b = var2.field_72450_a;
            this.field_75435_c = var2.field_72448_b;
            this.field_75432_d = var2.field_72449_c;
            return true;
         }
      }
   }

   @Override
   public boolean func_75253_b() {
      return !this.field_75436_a.func_70661_as().func_75500_f();
   }

   @Override
   public void func_75249_e() {
      this.field_75436_a.func_70661_as().func_75492_a(this.field_75434_b, this.field_75435_c, this.field_75432_d, this.field_75433_e);
   }
}

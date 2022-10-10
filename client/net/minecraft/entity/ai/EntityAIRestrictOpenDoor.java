package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIRestrictOpenDoor extends EntityAIBase {
   private final EntityCreature field_75275_a;
   private VillageDoorInfo field_75274_b;

   public EntityAIRestrictOpenDoor(EntityCreature var1) {
      super();
      this.field_75275_a = var1;
      if (!(var1.func_70661_as() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob type for RestrictOpenDoorGoal");
      }
   }

   public boolean func_75250_a() {
      if (this.field_75275_a.field_70170_p.func_72935_r()) {
         return false;
      } else {
         BlockPos var1 = new BlockPos(this.field_75275_a);
         Village var2 = this.field_75275_a.field_70170_p.func_175714_ae().func_176056_a(var1, 16);
         if (var2 == null) {
            return false;
         } else {
            this.field_75274_b = var2.func_179865_b(var1);
            if (this.field_75274_b == null) {
               return false;
            } else {
               return (double)this.field_75274_b.func_179846_b(var1) < 2.25D;
            }
         }
      }
   }

   public boolean func_75253_b() {
      if (this.field_75275_a.field_70170_p.func_72935_r()) {
         return false;
      } else {
         return !this.field_75274_b.func_179851_i() && this.field_75274_b.func_179850_c(new BlockPos(this.field_75275_a));
      }
   }

   public void func_75249_e() {
      ((PathNavigateGround)this.field_75275_a.func_70661_as()).func_179688_b(false);
      ((PathNavigateGround)this.field_75275_a.func_70661_as()).func_179691_c(false);
   }

   public void func_75251_c() {
      ((PathNavigateGround)this.field_75275_a.func_70661_as()).func_179688_b(true);
      ((PathNavigateGround)this.field_75275_a.func_70661_as()).func_179691_c(true);
      this.field_75274_b = null;
   }

   public void func_75246_d() {
      this.field_75274_b.func_75470_e();
   }
}

package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIRestrictOpenDoor extends EntityAIBase {
   private EntityCreature field_75275_a;
   private VillageDoorInfo field_75274_b;

   public EntityAIRestrictOpenDoor(EntityCreature var1) {
      super();
      this.field_75275_a = var1;
   }

   @Override
   public boolean func_75250_a() {
      if (this.field_75275_a.field_70170_p.func_72935_r()) {
         return false;
      } else {
         Village var1 = this.field_75275_a
            .field_70170_p
            .field_72982_D
            .func_75550_a(
               MathHelper.func_76128_c(this.field_75275_a.field_70165_t),
               MathHelper.func_76128_c(this.field_75275_a.field_70163_u),
               MathHelper.func_76128_c(this.field_75275_a.field_70161_v),
               16
            );
         if (var1 == null) {
            return false;
         } else {
            this.field_75274_b = var1.func_75564_b(
               MathHelper.func_76128_c(this.field_75275_a.field_70165_t),
               MathHelper.func_76128_c(this.field_75275_a.field_70163_u),
               MathHelper.func_76128_c(this.field_75275_a.field_70161_v)
            );
            if (this.field_75274_b == null) {
               return false;
            } else {
               return (double)this.field_75274_b
                     .func_75469_c(
                        MathHelper.func_76128_c(this.field_75275_a.field_70165_t),
                        MathHelper.func_76128_c(this.field_75275_a.field_70163_u),
                        MathHelper.func_76128_c(this.field_75275_a.field_70161_v)
                     )
                  < 2.25;
            }
         }
      }
   }

   @Override
   public boolean func_75253_b() {
      if (this.field_75275_a.field_70170_p.func_72935_r()) {
         return false;
      } else {
         return !this.field_75274_b.field_75476_g
            && this.field_75274_b
               .func_75467_a(MathHelper.func_76128_c(this.field_75275_a.field_70165_t), MathHelper.func_76128_c(this.field_75275_a.field_70161_v));
      }
   }

   @Override
   public void func_75249_e() {
      this.field_75275_a.func_70661_as().func_75498_b(false);
      this.field_75275_a.func_70661_as().func_75490_c(false);
   }

   @Override
   public void func_75251_c() {
      this.field_75275_a.func_70661_as().func_75498_b(true);
      this.field_75275_a.func_70661_as().func_75490_c(true);
      this.field_75274_b = null;
   }

   @Override
   public void func_75246_d() {
      this.field_75274_b.func_75470_e();
   }
}

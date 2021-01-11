package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathNavigateGround;

public class EntityAIRestrictSun extends EntityAIBase {
   private EntityCreature field_75273_a;

   public EntityAIRestrictSun(EntityCreature var1) {
      super();
      this.field_75273_a = var1;
   }

   public boolean func_75250_a() {
      return this.field_75273_a.field_70170_p.func_72935_r();
   }

   public void func_75249_e() {
      ((PathNavigateGround)this.field_75273_a.func_70661_as()).func_179685_e(true);
   }

   public void func_75251_c() {
      ((PathNavigateGround)this.field_75273_a.func_70661_as()).func_179685_e(false);
   }
}

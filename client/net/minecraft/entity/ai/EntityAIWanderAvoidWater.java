package net.minecraft.entity.ai;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.math.Vec3d;

public class EntityAIWanderAvoidWater extends EntityAIWander {
   protected final float field_190865_h;

   public EntityAIWanderAvoidWater(EntityCreature var1, double var2) {
      this(var1, var2, 0.001F);
   }

   public EntityAIWanderAvoidWater(EntityCreature var1, double var2, float var4) {
      super(var1, var2);
      this.field_190865_h = var4;
   }

   @Nullable
   protected Vec3d func_190864_f() {
      if (this.field_75457_a.func_203005_aq()) {
         Vec3d var1 = RandomPositionGenerator.func_191377_b(this.field_75457_a, 15, 7);
         return var1 == null ? super.func_190864_f() : var1;
      } else {
         return this.field_75457_a.func_70681_au().nextFloat() >= this.field_190865_h ? RandomPositionGenerator.func_191377_b(this.field_75457_a, 10, 7) : super.func_190864_f();
      }
   }
}

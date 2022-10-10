package net.minecraft.entity.ai;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class EntityAIWanderSwim extends EntityAIWander {
   public EntityAIWanderSwim(EntityCreature var1, double var2, int var4) {
      super(var1, var2, var4);
   }

   @Nullable
   protected Vec3d func_190864_f() {
      Vec3d var1 = RandomPositionGenerator.func_75463_a(this.field_75457_a, 10, 7);

      for(int var2 = 0; var1 != null && !this.field_75457_a.field_70170_p.func_180495_p(new BlockPos(var1)).func_196957_g(this.field_75457_a.field_70170_p, new BlockPos(var1), PathType.WATER) && var2++ < 10; var1 = RandomPositionGenerator.func_75463_a(this.field_75457_a, 10, 7)) {
      }

      return var1;
   }
}

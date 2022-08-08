package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;

public class ClimbOnTopOfPowderSnowGoal extends Goal {
   private final Mob mob;
   private final Level level;

   public ClimbOnTopOfPowderSnowGoal(Mob var1, Level var2) {
      super();
      this.mob = var1;
      this.level = var2;
      this.setFlags(EnumSet.of(Goal.Flag.JUMP));
   }

   public boolean canUse() {
      boolean var1 = this.mob.wasInPowderSnow || this.mob.isInPowderSnow;
      if (var1 && this.mob.getType().is(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS)) {
         BlockPos var2 = this.mob.blockPosition().above();
         BlockState var3 = this.level.getBlockState(var2);
         return var3.is(Blocks.POWDER_SNOW) || var3.getCollisionShape(this.level, var2) == Shapes.empty();
      } else {
         return false;
      }
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }

   public void tick() {
      this.mob.getJumpControl().jump();
   }
}

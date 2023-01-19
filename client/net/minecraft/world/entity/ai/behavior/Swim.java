package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Mob;

public class Swim extends Behavior<Mob> {
   private final float chance;

   public Swim(float var1) {
      super(ImmutableMap.of());
      this.chance = var1;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Mob var2) {
      return var2.isInWater() && var2.getFluidHeight(FluidTags.WATER) > var2.getFluidJumpThreshold() || var2.isInLava();
   }

   protected boolean canStillUse(ServerLevel var1, Mob var2, long var3) {
      return this.checkExtraStartConditions(var1, var2);
   }

   protected void tick(ServerLevel var1, Mob var2, long var3) {
      if (var2.getRandom().nextFloat() < this.chance) {
         var2.getJumpControl().jump();
      }
   }
}

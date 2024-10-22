package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Mob;

public class Swim<T extends Mob> extends Behavior<T> {
   private final float chance;

   public Swim(float var1) {
      super(ImmutableMap.of());
      this.chance = var1;
   }

   public static <T extends Mob> boolean shouldSwim(T var0) {
      return var0.isInWater() && var0.getFluidHeight(FluidTags.WATER) > var0.getFluidJumpThreshold() || var0.isInLava();
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Mob var2) {
      return shouldSwim(var2);
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

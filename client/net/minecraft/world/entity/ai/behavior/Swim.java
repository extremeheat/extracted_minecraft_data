package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class Swim extends Behavior<Mob> {
   private final float height;
   private final float chance;

   public Swim(float var1, float var2) {
      super(ImmutableMap.of());
      this.height = var1;
      this.chance = var2;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Mob var2) {
      return var2.isInWater() && var2.getWaterHeight() > (double)this.height || var2.isInLava();
   }

   protected boolean canStillUse(ServerLevel var1, Mob var2, long var3) {
      return this.checkExtraStartConditions(var1, var2);
   }

   protected void tick(ServerLevel var1, Mob var2, long var3) {
      if (var2.getRandom().nextFloat() < this.chance) {
         var2.getJumpControl().jump();
      }

   }

   // $FF: synthetic method
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return this.canStillUse(var1, (Mob)var2, var3);
   }

   // $FF: synthetic method
   protected void tick(ServerLevel var1, LivingEntity var2, long var3) {
      this.tick(var1, (Mob)var2, var3);
   }
}

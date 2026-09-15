package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.material.Fluid;

public class Swim<T extends Mob> extends Behavior<T> {
   private final float chance;
   private final TagKey<Fluid> fluid;

   public Swim(final float chance) {
      this(chance, FluidTags.ENTITY_FLOATABLE);
   }

   public Swim(final float chance, final TagKey<Fluid> fluid) {
      super(ImmutableMap.of());
      this.chance = chance;
      this.fluid = fluid;
   }

   protected boolean checkExtraStartConditions(final ServerLevel level, final Mob body) {
      return body.isInFluidDeeperThan(body.getFluidJumpThreshold(), this.fluid) || body.isInLava();
   }

   protected boolean canStillUse(final ServerLevel level, final Mob body, final long timestamp) {
      return this.checkExtraStartConditions(level, body);
   }

   protected void tick(final ServerLevel level, final Mob body, final long timestamp) {
      if (body.getRandom().nextFloat() < this.chance) {
         body.getJumpControl().jump();
      }

   }
}

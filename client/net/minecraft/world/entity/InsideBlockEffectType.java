package net.minecraft.world.entity;

import java.util.function.Consumer;
import net.minecraft.world.level.block.BaseFireBlock;

public enum InsideBlockEffectType {
   FREEZE((var0) -> {
      var0.setIsInPowderSnow(true);
      if (var0.canFreeze()) {
         var0.setTicksFrozen(Math.min(var0.getTicksRequiredToFreeze(), var0.getTicksFrozen() + 1));
      }

   }),
   FIRE_IGNITE(BaseFireBlock::fireIgnite),
   LAVA_IGNITE(Entity::lavaIgnite),
   EXTINGUISH(Entity::clearFire);

   private final Consumer<Entity> effect;

   private InsideBlockEffectType(final Consumer<Entity> var3) {
      this.effect = var3;
   }

   public Consumer<Entity> effect() {
      return this.effect;
   }

   // $FF: synthetic method
   private static InsideBlockEffectType[] $values() {
      return new InsideBlockEffectType[]{FREEZE, FIRE_IGNITE, LAVA_IGNITE, EXTINGUISH};
   }
}

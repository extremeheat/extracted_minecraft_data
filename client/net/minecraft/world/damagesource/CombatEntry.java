package net.minecraft.world.damagesource;

import javax.annotation.Nullable;

public record CombatEntry(DamageSource source, float damage, @Nullable FallLocation fallLocation, float fallDistance) {
   public CombatEntry(DamageSource source, float damage, @Nullable FallLocation fallLocation, float fallDistance) {
      super();
      this.source = source;
      this.damage = damage;
      this.fallLocation = fallLocation;
      this.fallDistance = fallDistance;
   }
}

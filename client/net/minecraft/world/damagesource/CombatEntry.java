package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class CombatEntry {
   private final DamageSource source;
   private final int time;
   private final float damage;
   private final float health;
   @Nullable
   private final String location;
   private final float fallDistance;

   public CombatEntry(DamageSource var1, int var2, float var3, float var4, @Nullable String var5, float var6) {
      super();
      this.source = var1;
      this.time = var2;
      this.damage = var4;
      this.health = var3;
      this.location = var5;
      this.fallDistance = var6;
   }

   public DamageSource getSource() {
      return this.source;
   }

   public int getTime() {
      return this.time;
   }

   public float getDamage() {
      return this.damage;
   }

   public float getHealthBeforeDamage() {
      return this.health;
   }

   public float getHealthAfterDamage() {
      return this.health - this.damage;
   }

   public boolean isCombatRelated() {
      return this.source.getEntity() instanceof LivingEntity;
   }

   @Nullable
   public String getLocation() {
      return this.location;
   }

   @Nullable
   public Component getAttackerName() {
      return this.getSource().getEntity() == null ? null : this.getSource().getEntity().getDisplayName();
   }

   @Nullable
   public Entity getAttacker() {
      return this.getSource().getEntity();
   }

   public float getFallDistance() {
      return this.source == DamageSource.OUT_OF_WORLD ? 3.4028235E38F : this.fallDistance;
   }
}

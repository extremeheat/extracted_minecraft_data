package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class EntityDamageSource extends DamageSource {
   protected final Entity entity;
   private boolean isThorns;

   public EntityDamageSource(String var1, Entity var2) {
      super(var1);
      this.entity = var2;
   }

   public EntityDamageSource setThorns() {
      this.isThorns = true;
      return this;
   }

   public boolean isThorns() {
      return this.isThorns;
   }

   @Override
   public Entity getEntity() {
      return this.entity;
   }

   @Override
   public Component getLocalizedDeathMessage(LivingEntity var1) {
      Entity var4 = this.entity;
      ItemStack var2 = var4 instanceof LivingEntity var3 ? var3.getMainHandItem() : ItemStack.EMPTY;
      String var5 = "death.attack." + this.msgId;
      return !var2.isEmpty() && var2.hasCustomHoverName()
         ? Component.translatable(var5 + ".item", var1.getDisplayName(), this.entity.getDisplayName(), var2.getDisplayName())
         : Component.translatable(var5, var1.getDisplayName(), this.entity.getDisplayName());
   }

   @Override
   public boolean scalesWithDifficulty() {
      return this.entity instanceof LivingEntity && !(this.entity instanceof Player);
   }

   @Nullable
   @Override
   public Vec3 getSourcePosition() {
      return this.entity.position();
   }

   @Override
   public String toString() {
      return "EntityDamageSource (" + this.entity + ")";
   }
}

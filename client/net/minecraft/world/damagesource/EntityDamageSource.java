package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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

   public Entity getEntity() {
      return this.entity;
   }

   public Component getLocalizedDeathMessage(LivingEntity var1) {
      ItemStack var2 = this.entity instanceof LivingEntity ? ((LivingEntity)this.entity).getMainHandItem() : ItemStack.EMPTY;
      String var3 = "death.attack." + this.msgId;
      return !var2.isEmpty() && var2.hasCustomHoverName() ? new TranslatableComponent(var3 + ".item", new Object[]{var1.getDisplayName(), this.entity.getDisplayName(), var2.getDisplayName()}) : new TranslatableComponent(var3, new Object[]{var1.getDisplayName(), this.entity.getDisplayName()});
   }

   public boolean scalesWithDifficulty() {
      return this.entity instanceof LivingEntity && !(this.entity instanceof Player);
   }

   @Nullable
   public Vec3 getSourcePosition() {
      return this.entity.position();
   }

   public String toString() {
      return "EntityDamageSource (" + this.entity + ")";
   }
}

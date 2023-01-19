package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class IndirectEntityDamageSource extends EntityDamageSource {
   @Nullable
   private final Entity cause;

   public IndirectEntityDamageSource(String var1, Entity var2, @Nullable Entity var3) {
      super(var1, var2);
      this.cause = var3;
   }

   @Nullable
   @Override
   public Entity getDirectEntity() {
      return this.entity;
   }

   @Nullable
   @Override
   public Entity getEntity() {
      return this.cause;
   }

   @Override
   public Component getLocalizedDeathMessage(LivingEntity var1) {
      Component var2 = this.cause == null ? this.entity.getDisplayName() : this.cause.getDisplayName();
      Entity var5 = this.cause;
      ItemStack var3 = var5 instanceof LivingEntity var4 ? var4.getMainHandItem() : ItemStack.EMPTY;
      String var6 = "death.attack." + this.msgId;
      if (!var3.isEmpty() && var3.hasCustomHoverName()) {
         String var7 = var6 + ".item";
         return Component.translatable(var7, var1.getDisplayName(), var2, var3.getDisplayName());
      } else {
         return Component.translatable(var6, var1.getDisplayName(), var2);
      }
   }
}

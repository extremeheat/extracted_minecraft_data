package net.minecraft.world.item;

import java.util.function.Predicate;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;

public abstract class ProjectileWeaponItem extends Item {
   public static final Predicate ARROW_ONLY = (var0) -> {
      return var0.getItem().is(ItemTags.ARROWS);
   };
   public static final Predicate ARROW_OR_FIREWORK;

   public ProjectileWeaponItem(Item.Properties var1) {
      super(var1);
   }

   public Predicate getSupportedHeldProjectiles() {
      return this.getAllSupportedProjectiles();
   }

   public abstract Predicate getAllSupportedProjectiles();

   public static ItemStack getHeldProjectile(LivingEntity var0, Predicate var1) {
      if (var1.test(var0.getItemInHand(InteractionHand.OFF_HAND))) {
         return var0.getItemInHand(InteractionHand.OFF_HAND);
      } else {
         return var1.test(var0.getItemInHand(InteractionHand.MAIN_HAND)) ? var0.getItemInHand(InteractionHand.MAIN_HAND) : ItemStack.EMPTY;
      }
   }

   public int getEnchantmentValue() {
      return 1;
   }

   static {
      ARROW_OR_FIREWORK = ARROW_ONLY.or((var0) -> {
         return var0.getItem() == Items.FIREWORK_ROCKET;
      });
   }
}

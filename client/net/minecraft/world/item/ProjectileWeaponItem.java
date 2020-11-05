package net.minecraft.world.item;

import java.util.function.Predicate;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;

public abstract class ProjectileWeaponItem extends Item {
   public static final Predicate<ItemStack> ARROW_ONLY = (var0) -> {
      return var0.is((Tag)ItemTags.ARROWS);
   };
   public static final Predicate<ItemStack> ARROW_OR_FIREWORK;

   public ProjectileWeaponItem(Item.Properties var1) {
      super(var1);
   }

   public Predicate<ItemStack> getSupportedHeldProjectiles() {
      return this.getAllSupportedProjectiles();
   }

   public abstract Predicate<ItemStack> getAllSupportedProjectiles();

   public static ItemStack getHeldProjectile(LivingEntity var0, Predicate<ItemStack> var1) {
      if (var1.test(var0.getItemInHand(InteractionHand.OFF_HAND))) {
         return var0.getItemInHand(InteractionHand.OFF_HAND);
      } else {
         return var1.test(var0.getItemInHand(InteractionHand.MAIN_HAND)) ? var0.getItemInHand(InteractionHand.MAIN_HAND) : ItemStack.EMPTY;
      }
   }

   public int getEnchantmentValue() {
      return 1;
   }

   public abstract int getDefaultProjectileRange();

   static {
      ARROW_OR_FIREWORK = ARROW_ONLY.or((var0) -> {
         return var0.is(Items.FIREWORK_ROCKET);
      });
   }
}

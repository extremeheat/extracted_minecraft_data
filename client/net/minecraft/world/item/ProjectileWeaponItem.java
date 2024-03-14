package net.minecraft.world.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public abstract class ProjectileWeaponItem extends Item {
   public static final Predicate<ItemStack> ARROW_ONLY = var0 -> var0.is(ItemTags.ARROWS);
   public static final Predicate<ItemStack> ARROW_OR_FIREWORK = ARROW_ONLY.or(var0 -> var0.is(Items.FIREWORK_ROCKET));

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

   @Override
   public int getEnchantmentValue() {
      return 1;
   }

   public abstract int getDefaultProjectileRange();

   protected void shoot(
      Level var1,
      LivingEntity var2,
      InteractionHand var3,
      ItemStack var4,
      List<ItemStack> var5,
      float var6,
      float var7,
      boolean var8,
      @Nullable LivingEntity var9
   ) {
      float var10 = 10.0F;
      float var11 = var5.size() == 1 ? 0.0F : 20.0F / (float)(var5.size() - 1);
      float var12 = (float)((var5.size() - 1) % 2) * var11 / 2.0F;
      float var13 = 1.0F;

      for(int var14 = 0; var14 < var5.size(); ++var14) {
         ItemStack var15 = (ItemStack)var5.get(var14);
         if (!var15.isEmpty()) {
            float var16 = var12 + var13 * (float)((var14 + 1) / 2) * var11;
            var13 = -var13;
            var4.hurtAndBreak(this.getDurabilityUse(var15), var2, LivingEntity.getSlotForHand(var3));
            Projectile var17 = this.createProjectile(var1, var2, var4, var15, var8);
            this.shootProjectile(var2, var17, var14, var6, var7, var16, var9);
            var1.addFreshEntity(var17);
         }
      }
   }

   protected int getDurabilityUse(ItemStack var1) {
      return 1;
   }

   protected abstract void shootProjectile(LivingEntity var1, Projectile var2, int var3, float var4, float var5, float var6, @Nullable LivingEntity var7);

   protected Projectile createProjectile(Level var1, LivingEntity var2, ItemStack var3, ItemStack var4, boolean var5) {
      Item var8 = var4.getItem();
      ArrowItem var6 = var8 instanceof ArrowItem var7 ? var7 : (ArrowItem)Items.ARROW;
      AbstractArrow var11 = var6.createArrow(var1, var4, var2);
      if (var5) {
         var11.setCritArrow(true);
      }

      int var12 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER, var3);
      if (var12 > 0) {
         var11.setBaseDamage(var11.getBaseDamage() + (double)var12 * 0.5 + 0.5);
      }

      int var9 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH, var3);
      if (var9 > 0) {
         var11.setKnockback(var9);
      }

      if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAME, var3) > 0) {
         var11.igniteForSeconds(100);
      }

      int var10 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, var3);
      if (var10 > 0) {
         var11.setPierceLevel((byte)var10);
      }

      return var11;
   }

   protected static boolean hasInfiniteArrows(ItemStack var0, ItemStack var1, boolean var2) {
      return var2 || var1.is(Items.ARROW) && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY, var0) > 0;
   }

   protected static List<ItemStack> draw(ItemStack var0, ItemStack var1, LivingEntity var2) {
      if (var1.isEmpty()) {
         return List.of();
      } else {
         int var3 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, var0);
         int var4 = var3 == 0 ? 1 : 3;
         ArrayList var5 = new ArrayList(var4);
         ItemStack var6 = var1.copy();

         for(int var7 = 0; var7 < var4; ++var7) {
            var5.add(useAmmo(var0, var7 == 0 ? var1 : var6, var2, var7 > 0));
         }

         return var5;
      }
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected static ItemStack useAmmo(ItemStack var0, ItemStack var1, LivingEntity var2, boolean var3) {
      boolean var4 = !var3 && !hasInfiniteArrows(var0, var1, var2.hasInfiniteMaterials());
      if (!var4) {
         ItemStack var7 = var1.copyWithCount(1);
         var7.set(DataComponents.INTANGIBLE_PROJECTILE, Unit.INSTANCE);
         return var7;
      } else {
         ItemStack var5 = var1.split(1);
         if (var1.isEmpty() && var2 instanceof Player var6) {
            var6.getInventory().removeItem(var1);
         }

         return var5;
      }
   }
}

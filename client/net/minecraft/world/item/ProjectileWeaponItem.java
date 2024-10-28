package net.minecraft.world.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public abstract class ProjectileWeaponItem extends Item {
   public static final Predicate<ItemStack> ARROW_ONLY = (var0) -> {
      return var0.is(ItemTags.ARROWS);
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

   protected void shoot(ServerLevel var1, LivingEntity var2, InteractionHand var3, ItemStack var4, List<ItemStack> var5, float var6, float var7, boolean var8, @Nullable LivingEntity var9) {
      float var10 = EnchantmentHelper.processProjectileSpread(var1, var4, var2, 0.0F);
      float var11 = var5.size() == 1 ? 0.0F : 2.0F * var10 / (float)(var5.size() - 1);
      float var12 = (float)((var5.size() - 1) % 2) * var11 / 2.0F;
      float var13 = 1.0F;

      for(int var14 = 0; var14 < var5.size(); ++var14) {
         ItemStack var15 = (ItemStack)var5.get(var14);
         if (!var15.isEmpty()) {
            float var16 = var12 + var13 * (float)((var14 + 1) / 2) * var11;
            var13 = -var13;
            Projectile var17 = this.createProjectile(var1, var2, var4, var15, var8);
            this.shootProjectile(var2, var17, var14, var6, var7, var16, var9);
            var1.addFreshEntity(var17);
            var4.hurtAndBreak(this.getDurabilityUse(var15), var2, LivingEntity.getSlotForHand(var3));
            if (var4.isEmpty()) {
               break;
            }
         }
      }

   }

   protected int getDurabilityUse(ItemStack var1) {
      return 1;
   }

   protected abstract void shootProjectile(LivingEntity var1, Projectile var2, int var3, float var4, float var5, float var6, @Nullable LivingEntity var7);

   protected Projectile createProjectile(Level var1, LivingEntity var2, ItemStack var3, ItemStack var4, boolean var5) {
      Item var8 = var4.getItem();
      ArrowItem var10000;
      if (var8 instanceof ArrowItem var7) {
         var10000 = var7;
      } else {
         var10000 = (ArrowItem)Items.ARROW;
      }

      ArrowItem var6 = var10000;
      AbstractArrow var9 = var6.createArrow(var1, var4, var2, var3);
      if (var5) {
         var9.setCritArrow(true);
      }

      return var9;
   }

   protected static List<ItemStack> draw(ItemStack var0, ItemStack var1, LivingEntity var2) {
      if (var1.isEmpty()) {
         return List.of();
      } else {
         Level var5 = var2.level();
         int var10000;
         if (var5 instanceof ServerLevel) {
            ServerLevel var4 = (ServerLevel)var5;
            var10000 = EnchantmentHelper.processProjectileCount(var4, var0, var2, 1);
         } else {
            var10000 = 1;
         }

         int var3 = var10000;
         ArrayList var8 = new ArrayList(var3);
         ItemStack var9 = var1.copy();

         for(int var6 = 0; var6 < var3; ++var6) {
            ItemStack var7 = useAmmo(var0, var6 == 0 ? var1 : var9, var2, var6 > 0);
            if (!var7.isEmpty()) {
               var8.add(var7);
            }
         }

         return var8;
      }
   }

   protected static ItemStack useAmmo(ItemStack var0, ItemStack var1, LivingEntity var2, boolean var3) {
      int var10000;
      label28: {
         if (!var3 && !var2.hasInfiniteMaterials()) {
            Level var6 = var2.level();
            if (var6 instanceof ServerLevel) {
               ServerLevel var5 = (ServerLevel)var6;
               var10000 = EnchantmentHelper.processAmmoUse(var5, var0, var1, 1);
               break label28;
            }
         }

         var10000 = 0;
      }

      int var4 = var10000;
      if (var4 > var1.getCount()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack var7;
         if (var4 == 0) {
            var7 = var1.copyWithCount(1);
            var7.set(DataComponents.INTANGIBLE_PROJECTILE, Unit.INSTANCE);
            return var7;
         } else {
            var7 = var1.split(var4);
            if (var1.isEmpty() && var2 instanceof Player) {
               Player var8 = (Player)var2;
               var8.getInventory().removeItem(var1);
            }

            return var7;
         }
      }
   }

   static {
      ARROW_OR_FIREWORK = ARROW_ONLY.or((var0) -> {
         return var0.is(Items.FIREWORK_ROCKET);
      });
   }
}

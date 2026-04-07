package net.minecraft.world.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public abstract class ProjectileWeaponItem extends Item {
   public static final Predicate<ItemStack> ARROW_ONLY = (itemStack) -> itemStack.is(ItemTags.ARROWS);
   public static final Predicate<ItemStack> ARROW_OR_FIREWORK;

   public ProjectileWeaponItem(final Item.Properties properties) {
      super(properties);
   }

   public Predicate<ItemStack> getSupportedHeldProjectiles() {
      return this.getAllSupportedProjectiles();
   }

   public abstract Predicate<ItemStack> getAllSupportedProjectiles();

   public static ItemStack getHeldProjectile(final LivingEntity entity, final Predicate<ItemStack> valid) {
      if (valid.test(entity.getItemInHand(InteractionHand.OFF_HAND))) {
         return entity.getItemInHand(InteractionHand.OFF_HAND);
      } else {
         return valid.test(entity.getItemInHand(InteractionHand.MAIN_HAND)) ? entity.getItemInHand(InteractionHand.MAIN_HAND) : ItemStack.EMPTY;
      }
   }

   public abstract int getDefaultProjectileRange();

   protected void shoot(final ServerLevel level, final LivingEntity shooter, final InteractionHand hand, final ItemStack weapon, final List<ItemStack> projectiles, final float power, final float uncertainty, final boolean isCrit, final @Nullable LivingEntity targetOverride) {
      float maxAngle = EnchantmentHelper.processProjectileSpread(level, weapon, shooter, 0.0F);
      float angleStep = projectiles.size() == 1 ? 0.0F : 2.0F * maxAngle / (float)(projectiles.size() - 1);
      float angleOffset = (float)((projectiles.size() - 1) % 2) * angleStep / 2.0F;
      float direction = 1.0F;

      for(int i = 0; i < projectiles.size(); ++i) {
         ItemStack projectile = (ItemStack)projectiles.get(i);
         if (!projectile.isEmpty()) {
            float angle = angleOffset + direction * (float)((i + 1) / 2) * angleStep;
            direction = -direction;
            Projectile.spawnProjectile(this.createProjectile(level, shooter, weapon, projectile, isCrit), level, projectile, (projectileEntity) -> this.shootProjectile(shooter, projectileEntity, i, power, uncertainty, angle, targetOverride));
            weapon.hurtAndBreak(this.getDurabilityUse(projectile), shooter, hand.asEquipmentSlot());
            if (weapon.isEmpty()) {
               break;
            }
         }
      }

   }

   protected int getDurabilityUse(final ItemStack projectile) {
      return 1;
   }

   protected abstract void shootProjectile(final LivingEntity shooter, final Projectile projectileEntity, final int index, final float power, final float uncertainty, final float angle, final @Nullable LivingEntity targetOverrride);

   protected Projectile createProjectile(final Level level, final LivingEntity shooter, final ItemStack weapon, final ItemStack projectile, final boolean isCrit) {
      Item var8 = projectile.getItem();
      ArrowItem var10000;
      if (var8 instanceof ArrowItem arrow) {
         var10000 = arrow;
      } else {
         var10000 = (ArrowItem)Items.ARROW;
      }

      ArrowItem arrowItem = var10000;
      AbstractArrow arrow = arrowItem.createArrow(level, projectile, shooter, weapon);
      if (isCrit) {
         arrow.setCritArrow(true);
      }

      return arrow;
   }

   protected static List<ItemStack> draw(final ItemStack weapon, final ItemStack projectile, final LivingEntity shooter) {
      if (projectile.isEmpty()) {
         return List.of();
      } else {
         Level var5 = shooter.level();
         int var10000;
         if (var5 instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)var5;
            var10000 = EnchantmentHelper.processProjectileCount(serverLevel, weapon, shooter, 1);
         } else {
            var10000 = 1;
         }

         int numProjectiles = var10000;
         List<ItemStack> drawn = new ArrayList(numProjectiles);
         ItemStack projectileCopy = projectile.copy();

         for(int i = 0; i < numProjectiles; ++i) {
            ItemStack drawnStack = useAmmo(weapon, i == 0 ? projectile : projectileCopy, shooter, i > 0);
            if (!drawnStack.isEmpty()) {
               drawn.add(drawnStack);
            }
         }

         return drawn;
      }
   }

   protected static ItemStack useAmmo(final ItemStack weapon, final ItemStack projectile, final LivingEntity holder, final boolean forceInfinite) {
      int var10000;
      label28: {
         if (!forceInfinite && !holder.hasInfiniteMaterials()) {
            Level var6 = holder.level();
            if (var6 instanceof ServerLevel) {
               ServerLevel serverLevel = (ServerLevel)var6;
               var10000 = EnchantmentHelper.processAmmoUse(serverLevel, weapon, projectile, 1);
               break label28;
            }
         }

         var10000 = 0;
      }

      int ammoToUse = var10000;
      if (ammoToUse > projectile.getCount()) {
         return ItemStack.EMPTY;
      } else if (ammoToUse == 0) {
         ItemStack copy = projectile.copyWithCount(1);
         copy.set(DataComponents.INTANGIBLE_PROJECTILE, Unit.INSTANCE);
         return copy;
      } else {
         ItemStack used = projectile.split(ammoToUse);
         if (projectile.isEmpty() && holder instanceof Player) {
            Player player = (Player)holder;
            player.getInventory().removeItem(projectile);
         }

         return used;
      }
   }

   static {
      ARROW_OR_FIREWORK = ARROW_ONLY.or((itemStack) -> itemStack.is(Items.FIREWORK_ROCKET));
   }
}

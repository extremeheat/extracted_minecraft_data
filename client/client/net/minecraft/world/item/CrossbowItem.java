package net.minecraft.world.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CrossbowItem extends ProjectileWeaponItem {
   private static final int MAX_CHARGE_DURATION = 25;
   public static final int DEFAULT_RANGE = 8;
   private boolean startSoundPlayed = false;
   private boolean midLoadSoundPlayed = false;
   private static final float START_SOUND_PERCENT = 0.2F;
   private static final float MID_SOUND_PERCENT = 0.5F;
   private static final float ARROW_POWER = 3.15F;
   private static final float FIREWORK_POWER = 1.6F;
   public static final float MOB_ARROW_POWER = 1.6F;

   public CrossbowItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public Predicate<ItemStack> getSupportedHeldProjectiles() {
      return ARROW_OR_FIREWORK;
   }

   @Override
   public Predicate<ItemStack> getAllSupportedProjectiles() {
      return ARROW_ONLY;
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      ChargedProjectiles var5 = var4.get(DataComponents.CHARGED_PROJECTILES);
      if (var5 != null && !var5.isEmpty()) {
         this.performShooting(var1, var2, var3, var4, getShootingPower(var5), 1.0F, null);
         return InteractionResultHolder.consume(var4);
      } else if (!var2.getProjectile(var4).isEmpty()) {
         this.startSoundPlayed = false;
         this.midLoadSoundPlayed = false;
         var2.startUsingItem(var3);
         return InteractionResultHolder.consume(var4);
      } else {
         return InteractionResultHolder.fail(var4);
      }
   }

   private static float getShootingPower(ChargedProjectiles var0) {
      return var0.contains(Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
   }

   @Override
   public void releaseUsing(ItemStack var1, Level var2, LivingEntity var3, int var4) {
      int var5 = this.getUseDuration(var1) - var4;
      float var6 = getPowerForTime(var5, var1);
      if (var6 >= 1.0F && !isCharged(var1) && tryLoadProjectiles(var3, var1)) {
         var2.playSound(
            null,
            var3.getX(),
            var3.getY(),
            var3.getZ(),
            SoundEvents.CROSSBOW_LOADING_END,
            var3.getSoundSource(),
            1.0F,
            1.0F / (var2.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F
         );
      }
   }

   private static boolean tryLoadProjectiles(LivingEntity var0, ItemStack var1) {
      List var2 = draw(var1, var0.getProjectile(var1), var0);
      if (!var2.isEmpty()) {
         var1.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(var2));
         return true;
      } else {
         return false;
      }
   }

   public static boolean isCharged(ItemStack var0) {
      ChargedProjectiles var1 = var0.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
      return !var1.isEmpty();
   }

   @Override
   protected void shootProjectile(LivingEntity var1, Projectile var2, int var3, float var4, float var5, float var6, @Nullable LivingEntity var7) {
      Vector3f var8;
      if (var7 != null) {
         double var9 = var7.getX() - var1.getX();
         double var11 = var7.getZ() - var1.getZ();
         double var13 = Math.sqrt(var9 * var9 + var11 * var11);
         double var15 = var7.getY(0.3333333333333333) - var2.getY() + var13 * 0.20000000298023224;
         var8 = getProjectileShotVector(var1, new Vec3(var9, var15, var11), var6);
      } else {
         Vec3 var17 = var1.getUpVector(1.0F);
         Quaternionf var10 = new Quaternionf().setAngleAxis((double)(var6 * 0.017453292F), var17.x, var17.y, var17.z);
         Vec3 var19 = var1.getViewVector(1.0F);
         var8 = var19.toVector3f().rotate(var10);
      }

      var2.shoot((double)var8.x(), (double)var8.y(), (double)var8.z(), var4, var5);
      float var18 = getShotPitch(var1.getRandom(), var3);
      var1.level().playSound(null, var1.getX(), var1.getY(), var1.getZ(), SoundEvents.CROSSBOW_SHOOT, var1.getSoundSource(), 1.0F, var18);
   }

   private static Vector3f getProjectileShotVector(LivingEntity var0, Vec3 var1, float var2) {
      Vector3f var3 = var1.toVector3f().normalize();
      Vector3f var4 = new Vector3f(var3).cross(new Vector3f(0.0F, 1.0F, 0.0F));
      if ((double)var4.lengthSquared() <= 1.0E-7) {
         Vec3 var5 = var0.getUpVector(1.0F);
         var4 = new Vector3f(var3).cross(var5.toVector3f());
      }

      Vector3f var6 = new Vector3f(var3).rotateAxis(1.5707964F, var4.x, var4.y, var4.z);
      return new Vector3f(var3).rotateAxis(var2 * 0.017453292F, var6.x, var6.y, var6.z);
   }

   @Override
   protected Projectile createProjectile(Level var1, LivingEntity var2, ItemStack var3, ItemStack var4, boolean var5) {
      if (var4.is(Items.FIREWORK_ROCKET)) {
         return new FireworkRocketEntity(var1, var4, var2, var2.getX(), var2.getEyeY() - 0.15000000596046448, var2.getZ(), true);
      } else {
         Projectile var6 = super.createProjectile(var1, var2, var3, var4, var5);
         if (var6 instanceof AbstractArrow var7) {
            var7.setShotFromCrossbow(true);
            var7.setSoundEvent(SoundEvents.CROSSBOW_HIT);
         }

         return var6;
      }
   }

   @Override
   protected int getDurabilityUse(ItemStack var1) {
      return var1.is(Items.FIREWORK_ROCKET) ? 3 : 1;
   }

   public void performShooting(Level var1, LivingEntity var2, InteractionHand var3, ItemStack var4, float var5, float var6, @Nullable LivingEntity var7) {
      if (!var1.isClientSide()) {
         ChargedProjectiles var8 = var4.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
         if (var8 != null && !var8.isEmpty()) {
            this.shoot(var1, var2, var3, var4, var8.getItems(), var5, var6, var2 instanceof Player, var7);
            if (var2 instanceof ServerPlayer var9) {
               CriteriaTriggers.SHOT_CROSSBOW.trigger(var9, var4);
               var9.awardStat(Stats.ITEM_USED.get(var4.getItem()));
            }
         }
      }
   }

   private static float getShotPitch(RandomSource var0, int var1) {
      return var1 == 0 ? 1.0F : getRandomShotPitch((var1 & 1) == 1, var0);
   }

   private static float getRandomShotPitch(boolean var0, RandomSource var1) {
      float var2 = var0 ? 0.63F : 0.43F;
      return 1.0F / (var1.nextFloat() * 0.5F + 1.8F) + var2;
   }

   @Override
   public void onUseTick(Level var1, LivingEntity var2, ItemStack var3, int var4) {
      if (!var1.isClientSide) {
         int var5 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, var3);
         SoundEvent var6 = this.getStartSound(var5);
         SoundEvent var7 = var5 == 0 ? SoundEvents.CROSSBOW_LOADING_MIDDLE : null;
         float var8 = (float)(var3.getUseDuration() - var4) / (float)getChargeDuration(var3);
         if (var8 < 0.2F) {
            this.startSoundPlayed = false;
            this.midLoadSoundPlayed = false;
         }

         if (var8 >= 0.2F && !this.startSoundPlayed) {
            this.startSoundPlayed = true;
            var1.playSound(null, var2.getX(), var2.getY(), var2.getZ(), var6, SoundSource.PLAYERS, 0.5F, 1.0F);
         }

         if (var8 >= 0.5F && var7 != null && !this.midLoadSoundPlayed) {
            this.midLoadSoundPlayed = true;
            var1.playSound(null, var2.getX(), var2.getY(), var2.getZ(), var7, SoundSource.PLAYERS, 0.5F, 1.0F);
         }
      }
   }

   @Override
   public int getUseDuration(ItemStack var1) {
      return getChargeDuration(var1) + 3;
   }

   public static int getChargeDuration(ItemStack var0) {
      int var1 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, var0);
      return var1 == 0 ? 25 : 25 - 5 * var1;
   }

   @Override
   public UseAnim getUseAnimation(ItemStack var1) {
      return UseAnim.CROSSBOW;
   }

   private SoundEvent getStartSound(int var1) {
      switch (var1) {
         case 1:
            return SoundEvents.CROSSBOW_QUICK_CHARGE_1;
         case 2:
            return SoundEvents.CROSSBOW_QUICK_CHARGE_2;
         case 3:
            return SoundEvents.CROSSBOW_QUICK_CHARGE_3;
         default:
            return SoundEvents.CROSSBOW_LOADING_START;
      }
   }

   private static float getPowerForTime(int var0, ItemStack var1) {
      float var2 = (float)var0 / (float)getChargeDuration(var1);
      if (var2 > 1.0F) {
         var2 = 1.0F;
      }

      return var2;
   }

   @Override
   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      ChargedProjectiles var5 = var1.get(DataComponents.CHARGED_PROJECTILES);
      if (var5 != null && !var5.isEmpty()) {
         ItemStack var6 = var5.getItems().get(0);
         var3.add(Component.translatable("item.minecraft.crossbow.projectile").append(CommonComponents.SPACE).append(var6.getDisplayName()));
         if (var4.isAdvanced() && var6.is(Items.FIREWORK_ROCKET)) {
            ArrayList var7 = Lists.newArrayList();
            Items.FIREWORK_ROCKET.appendHoverText(var6, var2, var7, var4);
            if (!var7.isEmpty()) {
               for (int var8 = 0; var8 < var7.size(); var8++) {
                  var7.set(var8, Component.literal("  ").append((Component)var7.get(var8)).withStyle(ChatFormatting.GRAY));
               }

               var3.addAll(var7);
            }
         }
      }
   }

   @Override
   public boolean useOnRelease(ItemStack var1) {
      return var1.is(this);
   }

   @Override
   public int getDefaultProjectileRange() {
      return 8;
   }
}
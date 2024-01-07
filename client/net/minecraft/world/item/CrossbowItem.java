package net.minecraft.world.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CrossbowItem extends ProjectileWeaponItem implements Vanishable {
   private static final String TAG_CHARGED = "Charged";
   private static final String TAG_CHARGED_PROJECTILES = "ChargedProjectiles";
   private static final int MAX_CHARGE_DURATION = 25;
   public static final int DEFAULT_RANGE = 8;
   private boolean startSoundPlayed = false;
   private boolean midLoadSoundPlayed = false;
   private static final float START_SOUND_PERCENT = 0.2F;
   private static final float MID_SOUND_PERCENT = 0.5F;
   private static final float ARROW_POWER = 3.15F;
   private static final float FIREWORK_POWER = 1.6F;

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
      if (isCharged(var4)) {
         performShooting(var1, var2, var3, var4, getShootingPower(var4), 1.0F);
         setCharged(var4, false);
         return InteractionResultHolder.consume(var4);
      } else if (!var2.getProjectile(var4).isEmpty()) {
         if (!isCharged(var4)) {
            this.startSoundPlayed = false;
            this.midLoadSoundPlayed = false;
            var2.startUsingItem(var3);
         }

         return InteractionResultHolder.consume(var4);
      } else {
         return InteractionResultHolder.fail(var4);
      }
   }

   private static float getShootingPower(ItemStack var0) {
      return containsChargedProjectile(var0, Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
   }

   @Override
   public void releaseUsing(ItemStack var1, Level var2, LivingEntity var3, int var4) {
      int var5 = this.getUseDuration(var1) - var4;
      float var6 = getPowerForTime(var5, var1);
      if (var6 >= 1.0F && !isCharged(var1) && tryLoadProjectiles(var3, var1)) {
         setCharged(var1, true);
         SoundSource var7 = var3 instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
         var2.playSound(
            null,
            var3.getX(),
            var3.getY(),
            var3.getZ(),
            SoundEvents.CROSSBOW_LOADING_END,
            var7,
            1.0F,
            1.0F / (var2.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F
         );
      }
   }

   private static boolean tryLoadProjectiles(LivingEntity var0, ItemStack var1) {
      int var2 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, var1);
      int var3 = var2 == 0 ? 1 : 3;
      boolean var4 = var0 instanceof Player && ((Player)var0).getAbilities().instabuild;
      ItemStack var5 = var0.getProjectile(var1);
      ItemStack var6 = var5.copy();

      for(int var7 = 0; var7 < var3; ++var7) {
         if (var7 > 0) {
            var5 = var6.copy();
         }

         if (var5.isEmpty() && var4) {
            var5 = new ItemStack(Items.ARROW);
            var6 = var5.copy();
         }

         if (!loadProjectile(var0, var1, var5, var7 > 0, var4)) {
            return false;
         }
      }

      return true;
   }

   private static boolean loadProjectile(LivingEntity var0, ItemStack var1, ItemStack var2, boolean var3, boolean var4) {
      if (var2.isEmpty()) {
         return false;
      } else {
         boolean var5 = var4 && var2.getItem() instanceof ArrowItem;
         ItemStack var6;
         if (!var5 && !var4 && !var3) {
            var6 = var2.split(1);
            if (var2.isEmpty() && var0 instanceof Player) {
               ((Player)var0).getInventory().removeItem(var2);
            }
         } else {
            var6 = var2.copy();
         }

         addChargedProjectile(var1, var6);
         return true;
      }
   }

   public static boolean isCharged(ItemStack var0) {
      CompoundTag var1 = var0.getTag();
      return var1 != null && var1.getBoolean("Charged");
   }

   public static void setCharged(ItemStack var0, boolean var1) {
      CompoundTag var2 = var0.getOrCreateTag();
      var2.putBoolean("Charged", var1);
   }

   private static void addChargedProjectile(ItemStack var0, ItemStack var1) {
      CompoundTag var2 = var0.getOrCreateTag();
      ListTag var3;
      if (var2.contains("ChargedProjectiles", 9)) {
         var3 = var2.getList("ChargedProjectiles", 10);
      } else {
         var3 = new ListTag();
      }

      CompoundTag var4 = new CompoundTag();
      var1.save(var4);
      var3.add(var4);
      var2.put("ChargedProjectiles", var3);
   }

   private static List<ItemStack> getChargedProjectiles(ItemStack var0) {
      ArrayList var1 = Lists.newArrayList();
      CompoundTag var2 = var0.getTag();
      if (var2 != null && var2.contains("ChargedProjectiles", 9)) {
         ListTag var3 = var2.getList("ChargedProjectiles", 10);
         if (var3 != null) {
            for(int var4 = 0; var4 < var3.size(); ++var4) {
               CompoundTag var5 = var3.getCompound(var4);
               var1.add(ItemStack.of(var5));
            }
         }
      }

      return var1;
   }

   private static void clearChargedProjectiles(ItemStack var0) {
      CompoundTag var1 = var0.getTag();
      if (var1 != null) {
         ListTag var2 = var1.getList("ChargedProjectiles", 9);
         var2.clear();
         var1.put("ChargedProjectiles", var2);
      }
   }

   public static boolean containsChargedProjectile(ItemStack var0, Item var1) {
      return getChargedProjectiles(var0).stream().anyMatch(var1x -> var1x.is(var1));
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static void shootProjectile(
      Level var0, LivingEntity var1, InteractionHand var2, ItemStack var3, ItemStack var4, float var5, boolean var6, float var7, float var8, float var9
   ) {
      if (!var0.isClientSide) {
         boolean var10 = var4.is(Items.FIREWORK_ROCKET);
         Object var11;
         if (var10) {
            var11 = new FireworkRocketEntity(var0, var4, var1, var1.getX(), var1.getEyeY() - 0.15000000596046448, var1.getZ(), true);
         } else {
            var11 = getArrow(var0, var1, var3, var4);
            if (var6 || var9 != 0.0F) {
               ((AbstractArrow)var11).pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
         }

         if (var1 instanceof CrossbowAttackMob var12) {
            var12.shootCrossbowProjectile(var12.getTarget(), var3, (Projectile)var11, var9);
         } else {
            Vec3 var13 = var1.getUpVector(1.0F);
            Quaternionf var14 = new Quaternionf().setAngleAxis((double)(var9 * 0.017453292F), var13.x, var13.y, var13.z);
            Vec3 var15 = var1.getViewVector(1.0F);
            Vector3f var16 = var15.toVector3f().rotate(var14);
            ((Projectile)var11).shoot((double)var16.x(), (double)var16.y(), (double)var16.z(), var7, var8);
         }

         var3.hurtAndBreak(var10 ? 3 : 1, var1, var1x -> var1x.broadcastBreakEvent(var2));
         var0.addFreshEntity((Entity)var11);
         var0.playSound(null, var1.getX(), var1.getY(), var1.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, var5);
      }
   }

   private static AbstractArrow getArrow(Level var0, LivingEntity var1, ItemStack var2, ItemStack var3) {
      ArrowItem var4 = (ArrowItem)(var3.getItem() instanceof ArrowItem ? var3.getItem() : Items.ARROW);
      AbstractArrow var5 = var4.createArrow(var0, var3, var1);
      if (var1 instanceof Player) {
         var5.setCritArrow(true);
      }

      var5.setSoundEvent(SoundEvents.CROSSBOW_HIT);
      var5.setShotFromCrossbow(true);
      int var6 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, var2);
      if (var6 > 0) {
         var5.setPierceLevel((byte)var6);
      }

      return var5;
   }

   public static void performShooting(Level var0, LivingEntity var1, InteractionHand var2, ItemStack var3, float var4, float var5) {
      List var6 = getChargedProjectiles(var3);
      float[] var7 = getShotPitches(var1.getRandom());

      for(int var8 = 0; var8 < var6.size(); ++var8) {
         ItemStack var9 = (ItemStack)var6.get(var8);
         boolean var10 = var1 instanceof Player && ((Player)var1).getAbilities().instabuild;
         if (!var9.isEmpty()) {
            if (var8 == 0) {
               shootProjectile(var0, var1, var2, var3, var9, var7[var8], var10, var4, var5, 0.0F);
            } else if (var8 == 1) {
               shootProjectile(var0, var1, var2, var3, var9, var7[var8], var10, var4, var5, -10.0F);
            } else if (var8 == 2) {
               shootProjectile(var0, var1, var2, var3, var9, var7[var8], var10, var4, var5, 10.0F);
            }
         }
      }

      onCrossbowShot(var0, var1, var3);
   }

   private static float[] getShotPitches(RandomSource var0) {
      boolean var1 = var0.nextBoolean();
      return new float[]{1.0F, getRandomShotPitch(var1, var0), getRandomShotPitch(!var1, var0)};
   }

   private static float getRandomShotPitch(boolean var0, RandomSource var1) {
      float var2 = var0 ? 0.63F : 0.43F;
      return 1.0F / (var1.nextFloat() * 0.5F + 1.8F) + var2;
   }

   private static void onCrossbowShot(Level var0, LivingEntity var1, ItemStack var2) {
      if (var1 instanceof ServerPlayer var3) {
         if (!var0.isClientSide) {
            CriteriaTriggers.SHOT_CROSSBOW.trigger((ServerPlayer)var3, var2);
         }

         ((ServerPlayer)var3).awardStat(Stats.ITEM_USED.get(var2.getItem()));
      }

      clearChargedProjectiles(var2);
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
      switch(var1) {
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
   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
      List var5 = getChargedProjectiles(var1);
      if (isCharged(var1) && !var5.isEmpty()) {
         ItemStack var6 = (ItemStack)var5.get(0);
         var3.add(Component.translatable("item.minecraft.crossbow.projectile").append(CommonComponents.SPACE).append(var6.getDisplayName()));
         if (var4.isAdvanced() && var6.is(Items.FIREWORK_ROCKET)) {
            ArrayList var7 = Lists.newArrayList();
            Items.FIREWORK_ROCKET.appendHoverText(var6, var2, var7, var4);
            if (!var7.isEmpty()) {
               for(int var8 = 0; var8 < var7.size(); ++var8) {
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

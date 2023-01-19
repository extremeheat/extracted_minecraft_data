package net.minecraft.world.entity.animal;

import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public interface Bucketable {
   boolean fromBucket();

   void setFromBucket(boolean var1);

   void saveToBucketTag(ItemStack var1);

   void loadFromBucketTag(CompoundTag var1);

   ItemStack getBucketItemStack();

   SoundEvent getPickupSound();

   @Deprecated
   static void saveDefaultDataToBucketTag(Mob var0, ItemStack var1) {
      CompoundTag var2 = var1.getOrCreateTag();
      if (var0.hasCustomName()) {
         var1.setHoverName(var0.getCustomName());
      }

      if (var0.isNoAi()) {
         var2.putBoolean("NoAI", var0.isNoAi());
      }

      if (var0.isSilent()) {
         var2.putBoolean("Silent", var0.isSilent());
      }

      if (var0.isNoGravity()) {
         var2.putBoolean("NoGravity", var0.isNoGravity());
      }

      if (var0.hasGlowingTag()) {
         var2.putBoolean("Glowing", var0.hasGlowingTag());
      }

      if (var0.isInvulnerable()) {
         var2.putBoolean("Invulnerable", var0.isInvulnerable());
      }

      var2.putFloat("Health", var0.getHealth());
   }

   @Deprecated
   static void loadDefaultDataFromBucketTag(Mob var0, CompoundTag var1) {
      if (var1.contains("NoAI")) {
         var0.setNoAi(var1.getBoolean("NoAI"));
      }

      if (var1.contains("Silent")) {
         var0.setSilent(var1.getBoolean("Silent"));
      }

      if (var1.contains("NoGravity")) {
         var0.setNoGravity(var1.getBoolean("NoGravity"));
      }

      if (var1.contains("Glowing")) {
         var0.setGlowingTag(var1.getBoolean("Glowing"));
      }

      if (var1.contains("Invulnerable")) {
         var0.setInvulnerable(var1.getBoolean("Invulnerable"));
      }

      if (var1.contains("Health", 99)) {
         var0.setHealth(var1.getFloat("Health"));
      }
   }

   static <T extends LivingEntity & Bucketable> Optional<InteractionResult> bucketMobPickup(Player var0, InteractionHand var1, T var2) {
      ItemStack var3 = var0.getItemInHand(var1);
      if (var3.getItem() == Items.WATER_BUCKET && var2.isAlive()) {
         var2.playSound(((Bucketable)var2).getPickupSound(), 1.0F, 1.0F);
         ItemStack var4 = ((Bucketable)var2).getBucketItemStack();
         ((Bucketable)var2).saveToBucketTag(var4);
         ItemStack var5 = ItemUtils.createFilledResult(var3, var0, var4, false);
         var0.setItemInHand(var1, var5);
         Level var6 = var2.level;
         if (!var6.isClientSide) {
            CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)var0, var4);
         }

         var2.discard();
         return Optional.of(InteractionResult.sidedSuccess(var6.isClientSide));
      } else {
         return Optional.empty();
      }
   }
}

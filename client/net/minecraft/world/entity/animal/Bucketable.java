package net.minecraft.world.entity.animal;

import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public interface Bucketable {
   boolean fromBucket();

   void setFromBucket(boolean var1);

   void saveToBucketTag(ItemStack var1);

   void loadFromBucketTag(CompoundTag var1);

   ItemStack getBucketItemStack();

   SoundEvent getPickupSound();

   /** @deprecated */
   @Deprecated
   static void saveDefaultDataToBucketTag(Mob var0, ItemStack var1) {
      var1.set(DataComponents.CUSTOM_NAME, var0.getCustomName());
      CustomData.update(DataComponents.BUCKET_ENTITY_DATA, var1, (var1x) -> {
         if (var0.isNoAi()) {
            var1x.putBoolean("NoAI", var0.isNoAi());
         }

         if (var0.isSilent()) {
            var1x.putBoolean("Silent", var0.isSilent());
         }

         if (var0.isNoGravity()) {
            var1x.putBoolean("NoGravity", var0.isNoGravity());
         }

         if (var0.hasGlowingTag()) {
            var1x.putBoolean("Glowing", var0.hasGlowingTag());
         }

         if (var0.isInvulnerable()) {
            var1x.putBoolean("Invulnerable", var0.isInvulnerable());
         }

         var1x.putFloat("Health", var0.getHealth());
      });
   }

   /** @deprecated */
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
         Level var6 = var2.level();
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

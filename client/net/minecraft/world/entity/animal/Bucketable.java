package net.minecraft.world.entity.animal;

import java.util.Objects;
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

   void setFromBucket(final boolean fromBucket);

   void saveToBucketTag(final ItemStack bucket);

   void loadFromBucketTag(final CompoundTag tag);

   ItemStack getBucketItemStack();

   SoundEvent getPickupSound();

   /** @deprecated */
   @Deprecated
   static void saveDefaultDataToBucketTag(final Mob entity, final ItemStack bucket) {
      bucket.copyFrom(DataComponents.CUSTOM_NAME, entity);
      CustomData.update(DataComponents.BUCKET_ENTITY_DATA, bucket, (tag) -> {
         if (entity.isNoAi()) {
            tag.putBoolean("NoAI", entity.isNoAi());
         }

         if (entity.isSilent()) {
            tag.putBoolean("Silent", entity.isSilent());
         }

         if (entity.isNoGravity()) {
            tag.putBoolean("NoGravity", entity.isNoGravity());
         }

         if (entity.hasGlowingTag()) {
            tag.putBoolean("Glowing", entity.hasGlowingTag());
         }

         if (entity.isInvulnerable()) {
            tag.putBoolean("Invulnerable", entity.isInvulnerable());
         }

         tag.putFloat("Health", entity.getHealth());
      });
   }

   /** @deprecated */
   @Deprecated
   static void loadDefaultDataFromBucketTag(final Mob entity, final CompoundTag tag) {
      Optional var10000 = tag.getBoolean("NoAI");
      Objects.requireNonNull(entity);
      var10000.ifPresent(entity::setNoAi);
      var10000 = tag.getBoolean("Silent");
      Objects.requireNonNull(entity);
      var10000.ifPresent(entity::setSilent);
      var10000 = tag.getBoolean("NoGravity");
      Objects.requireNonNull(entity);
      var10000.ifPresent(entity::setNoGravity);
      var10000 = tag.getBoolean("Glowing");
      Objects.requireNonNull(entity);
      var10000.ifPresent(entity::setGlowingTag);
      var10000 = tag.getBoolean("Invulnerable");
      Objects.requireNonNull(entity);
      var10000.ifPresent(entity::setInvulnerable);
      var10000 = tag.getFloat("Health");
      Objects.requireNonNull(entity);
      var10000.ifPresent(entity::setHealth);
   }

   static <T extends LivingEntity & Bucketable> Optional<InteractionResult> bucketMobPickup(final Player player, final InteractionHand hand, final T pickupEntity) {
      ItemStack itemStack = player.getItemInHand(hand);
      if (itemStack.getItem() == Items.WATER_BUCKET && pickupEntity.isAlive()) {
         pickupEntity.playSound(((Bucketable)pickupEntity).getPickupSound(), 1.0F, 1.0F);
         ItemStack bucket = ((Bucketable)pickupEntity).getBucketItemStack();
         ((Bucketable)pickupEntity).saveToBucketTag(bucket);
         ItemStack result = ItemUtils.createFilledResult(itemStack, player, bucket, false);
         player.setItemInHand(hand, result);
         Level level = pickupEntity.level();
         if (!level.isClientSide()) {
            CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)player, bucket);
         }

         pickupEntity.discard();
         return Optional.of(InteractionResult.SUCCESS);
      } else {
         return Optional.empty();
      }
   }
}

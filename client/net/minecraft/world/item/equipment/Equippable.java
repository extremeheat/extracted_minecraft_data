package net.minecraft.world.item.equipment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public record Equippable(EquipmentSlot slot, Holder<SoundEvent> equipSound, Optional<ResourceKey<EquipmentAsset>> assetId, Optional<ResourceLocation> cameraOverlay, Optional<HolderSet<EntityType<?>>> allowedEntities, boolean dispensable, boolean swappable, boolean damageOnHurt, boolean equipOnInteract) {
   public static final Codec<Equippable> CODEC = RecordCodecBuilder.create((var0) -> var0.group(EquipmentSlot.CODEC.fieldOf("slot").forGetter(Equippable::slot), SoundEvent.CODEC.optionalFieldOf("equip_sound", SoundEvents.ARMOR_EQUIP_GENERIC).forGetter(Equippable::equipSound), ResourceKey.codec(EquipmentAssets.ROOT_ID).optionalFieldOf("asset_id").forGetter(Equippable::assetId), ResourceLocation.CODEC.optionalFieldOf("camera_overlay").forGetter(Equippable::cameraOverlay), RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).optionalFieldOf("allowed_entities").forGetter(Equippable::allowedEntities), Codec.BOOL.optionalFieldOf("dispensable", true).forGetter(Equippable::dispensable), Codec.BOOL.optionalFieldOf("swappable", true).forGetter(Equippable::swappable), Codec.BOOL.optionalFieldOf("damage_on_hurt", true).forGetter(Equippable::damageOnHurt), Codec.BOOL.optionalFieldOf("equip_on_interact", false).forGetter(Equippable::equipOnInteract)).apply(var0, Equippable::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, Equippable> STREAM_CODEC;

   public Equippable(EquipmentSlot var1, Holder<SoundEvent> var2, Optional<ResourceKey<EquipmentAsset>> var3, Optional<ResourceLocation> var4, Optional<HolderSet<EntityType<?>>> var5, boolean var6, boolean var7, boolean var8, boolean var9) {
      super();
      this.slot = var1;
      this.equipSound = var2;
      this.assetId = var3;
      this.cameraOverlay = var4;
      this.allowedEntities = var5;
      this.dispensable = var6;
      this.swappable = var7;
      this.damageOnHurt = var8;
      this.equipOnInteract = var9;
   }

   public static Equippable llamaSwag(DyeColor var0) {
      return builder(EquipmentSlot.BODY).setEquipSound(SoundEvents.LLAMA_SWAG).setAsset((ResourceKey)EquipmentAssets.CARPETS.get(var0)).setAllowedEntities(EntityType.LLAMA, EntityType.TRADER_LLAMA).build();
   }

   public static Equippable saddle() {
      HolderGetter var0 = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.ENTITY_TYPE);
      return builder(EquipmentSlot.SADDLE).setEquipSound(SoundEvents.HORSE_SADDLE).setAsset(EquipmentAssets.SADDLE).setAllowedEntities(var0.getOrThrow(EntityTypeTags.CAN_EQUIP_SADDLE)).setEquipOnInteract(true).build();
   }

   public static Builder builder(EquipmentSlot var0) {
      return new Builder(var0);
   }

   public InteractionResult swapWithEquipmentSlot(ItemStack var1, Player var2) {
      if (var2.canUseSlot(this.slot) && this.canBeEquippedBy(var2.getType())) {
         ItemStack var3 = var2.getItemBySlot(this.slot);
         if ((!EnchantmentHelper.has(var3, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE) || var2.isCreative()) && !ItemStack.isSameItemSameComponents(var1, var3)) {
            if (!var2.level().isClientSide()) {
               var2.awardStat(Stats.ITEM_USED.get(var1.getItem()));
            }

            if (var1.getCount() <= 1) {
               ItemStack var6 = var3.isEmpty() ? var1 : var3.copyAndClear();
               ItemStack var7 = var2.isCreative() ? var1.copy() : var1.copyAndClear();
               var2.setItemSlot(this.slot, var7);
               return InteractionResult.SUCCESS.heldItemTransformedTo(var6);
            } else {
               ItemStack var4 = var3.copyAndClear();
               ItemStack var5 = var1.consumeAndReturn(1, var2);
               var2.setItemSlot(this.slot, var5);
               if (!var2.getInventory().add(var4)) {
                  var2.drop(var4, false);
               }

               return InteractionResult.SUCCESS.heldItemTransformedTo(var1);
            }
         } else {
            return InteractionResult.FAIL;
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   public InteractionResult equipOnTarget(Player var1, LivingEntity var2, ItemStack var3) {
      if (var2.isEquippableInSlot(var3, this.slot) && !var2.hasItemInSlot(this.slot) && var2.isAlive()) {
         if (!var1.level().isClientSide()) {
            var2.setItemSlot(this.slot, var3.split(1));
            if (var2 instanceof Mob) {
               Mob var4 = (Mob)var2;
               var4.setGuaranteedDrop(this.slot);
            }
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public boolean canBeEquippedBy(EntityType<?> var1) {
      return this.allowedEntities.isEmpty() || ((HolderSet)this.allowedEntities.get()).contains(var1.builtInRegistryHolder());
   }

   static {
      STREAM_CODEC = StreamCodec.composite(EquipmentSlot.STREAM_CODEC, Equippable::slot, SoundEvent.STREAM_CODEC, Equippable::equipSound, ResourceKey.streamCodec(EquipmentAssets.ROOT_ID).apply(ByteBufCodecs::optional), Equippable::assetId, ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs::optional), Equippable::cameraOverlay, ByteBufCodecs.holderSet(Registries.ENTITY_TYPE).apply(ByteBufCodecs::optional), Equippable::allowedEntities, ByteBufCodecs.BOOL, Equippable::dispensable, ByteBufCodecs.BOOL, Equippable::swappable, ByteBufCodecs.BOOL, Equippable::damageOnHurt, ByteBufCodecs.BOOL, Equippable::equipOnInteract, Equippable::new);
   }

   public static class Builder {
      private final EquipmentSlot slot;
      private Holder<SoundEvent> equipSound;
      private Optional<ResourceKey<EquipmentAsset>> assetId;
      private Optional<ResourceLocation> cameraOverlay;
      private Optional<HolderSet<EntityType<?>>> allowedEntities;
      private boolean dispensable;
      private boolean swappable;
      private boolean damageOnHurt;
      private boolean equipOnInteract;

      Builder(EquipmentSlot var1) {
         super();
         this.equipSound = SoundEvents.ARMOR_EQUIP_GENERIC;
         this.assetId = Optional.empty();
         this.cameraOverlay = Optional.empty();
         this.allowedEntities = Optional.empty();
         this.dispensable = true;
         this.swappable = true;
         this.damageOnHurt = true;
         this.slot = var1;
      }

      public Builder setEquipSound(Holder<SoundEvent> var1) {
         this.equipSound = var1;
         return this;
      }

      public Builder setAsset(ResourceKey<EquipmentAsset> var1) {
         this.assetId = Optional.of(var1);
         return this;
      }

      public Builder setCameraOverlay(ResourceLocation var1) {
         this.cameraOverlay = Optional.of(var1);
         return this;
      }

      public Builder setAllowedEntities(EntityType<?>... var1) {
         return this.setAllowedEntities(HolderSet.direct(EntityType::builtInRegistryHolder, var1));
      }

      public Builder setAllowedEntities(HolderSet<EntityType<?>> var1) {
         this.allowedEntities = Optional.of(var1);
         return this;
      }

      public Builder setDispensable(boolean var1) {
         this.dispensable = var1;
         return this;
      }

      public Builder setSwappable(boolean var1) {
         this.swappable = var1;
         return this;
      }

      public Builder setDamageOnHurt(boolean var1) {
         this.damageOnHurt = var1;
         return this;
      }

      public Builder setEquipOnInteract(boolean var1) {
         this.equipOnInteract = var1;
         return this;
      }

      public Equippable build() {
         return new Equippable(this.slot, this.equipSound, this.assetId, this.cameraOverlay, this.allowedEntities, this.dispensable, this.swappable, this.damageOnHurt, this.equipOnInteract);
      }
   }
}

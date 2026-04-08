package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.jspecify.annotations.Nullable;

public record EntityEquipmentPredicate(Optional<ItemPredicate> head, Optional<ItemPredicate> chest, Optional<ItemPredicate> legs, Optional<ItemPredicate> feet, Optional<ItemPredicate> body, Optional<ItemPredicate> mainhand, Optional<ItemPredicate> offhand) {
   public static final Codec<EntityEquipmentPredicate> CODEC = RecordCodecBuilder.create((i) -> i.group(ItemPredicate.CODEC.optionalFieldOf("head").forGetter(EntityEquipmentPredicate::head), ItemPredicate.CODEC.optionalFieldOf("chest").forGetter(EntityEquipmentPredicate::chest), ItemPredicate.CODEC.optionalFieldOf("legs").forGetter(EntityEquipmentPredicate::legs), ItemPredicate.CODEC.optionalFieldOf("feet").forGetter(EntityEquipmentPredicate::feet), ItemPredicate.CODEC.optionalFieldOf("body").forGetter(EntityEquipmentPredicate::body), ItemPredicate.CODEC.optionalFieldOf("mainhand").forGetter(EntityEquipmentPredicate::mainhand), ItemPredicate.CODEC.optionalFieldOf("offhand").forGetter(EntityEquipmentPredicate::offhand)).apply(i, EntityEquipmentPredicate::new));

   public EntityEquipmentPredicate {
      super();
   }

   public static EntityEquipmentPredicate captainPredicate(final HolderGetter<Item> items, final HolderGetter<BannerPattern> patternGetter) {
      return EntityEquipmentPredicate.Builder.equipment().head(ItemPredicate.Builder.item().of(items, Items.WHITE_BANNER).withComponents(DataComponentMatchers.Builder.components().exact(DataComponentExactPredicate.someOf(Raid.getBannerComponentPatch(patternGetter).split().added(), DataComponents.BANNER_PATTERNS, DataComponents.ITEM_NAME)).build())).build();
   }

   public boolean matches(final @Nullable Entity entity) {
      if (entity instanceof LivingEntity livingEntity) {
         if (this.head.isPresent() && !((ItemPredicate)this.head.get()).test((ItemInstance)livingEntity.getItemBySlot(EquipmentSlot.HEAD))) {
            return false;
         } else if (this.chest.isPresent() && !((ItemPredicate)this.chest.get()).test((ItemInstance)livingEntity.getItemBySlot(EquipmentSlot.CHEST))) {
            return false;
         } else if (this.legs.isPresent() && !((ItemPredicate)this.legs.get()).test((ItemInstance)livingEntity.getItemBySlot(EquipmentSlot.LEGS))) {
            return false;
         } else if (this.feet.isPresent() && !((ItemPredicate)this.feet.get()).test((ItemInstance)livingEntity.getItemBySlot(EquipmentSlot.FEET))) {
            return false;
         } else if (this.body.isPresent() && !((ItemPredicate)this.body.get()).test((ItemInstance)livingEntity.getItemBySlot(EquipmentSlot.BODY))) {
            return false;
         } else if (this.mainhand.isPresent() && !((ItemPredicate)this.mainhand.get()).test((ItemInstance)livingEntity.getItemBySlot(EquipmentSlot.MAINHAND))) {
            return false;
         } else {
            return !this.offhand.isPresent() || ((ItemPredicate)this.offhand.get()).test((ItemInstance)livingEntity.getItemBySlot(EquipmentSlot.OFFHAND));
         }
      } else {
         return false;
      }
   }

   public static class Builder {
      private Optional<ItemPredicate> head = Optional.empty();
      private Optional<ItemPredicate> chest = Optional.empty();
      private Optional<ItemPredicate> legs = Optional.empty();
      private Optional<ItemPredicate> feet = Optional.empty();
      private Optional<ItemPredicate> body = Optional.empty();
      private Optional<ItemPredicate> mainhand = Optional.empty();
      private Optional<ItemPredicate> offhand = Optional.empty();

      public Builder() {
         super();
      }

      public static Builder equipment() {
         return new Builder();
      }

      public Builder head(final ItemPredicate.Builder head) {
         this.head = Optional.of(head.build());
         return this;
      }

      public Builder chest(final ItemPredicate.Builder chest) {
         this.chest = Optional.of(chest.build());
         return this;
      }

      public Builder legs(final ItemPredicate.Builder legs) {
         this.legs = Optional.of(legs.build());
         return this;
      }

      public Builder feet(final ItemPredicate.Builder feet) {
         this.feet = Optional.of(feet.build());
         return this;
      }

      public Builder body(final ItemPredicate.Builder body) {
         this.body = Optional.of(body.build());
         return this;
      }

      public Builder mainhand(final ItemPredicate.Builder mainhand) {
         this.mainhand = Optional.of(mainhand.build());
         return this;
      }

      public Builder offhand(final ItemPredicate.Builder offhand) {
         this.offhand = Optional.of(offhand.build());
         return this;
      }

      public EntityEquipmentPredicate build() {
         return new EntityEquipmentPredicate(this.head, this.chest, this.legs, this.feet, this.body, this.mainhand, this.offhand);
      }
   }
}

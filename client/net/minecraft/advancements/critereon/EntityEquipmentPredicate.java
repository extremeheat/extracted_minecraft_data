package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerPattern;

public record EntityEquipmentPredicate(
   Optional<ItemPredicate> head,
   Optional<ItemPredicate> chest,
   Optional<ItemPredicate> legs,
   Optional<ItemPredicate> feet,
   Optional<ItemPredicate> mainhand,
   Optional<ItemPredicate> offhand
) {
   public static final Codec<EntityEquipmentPredicate> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ItemPredicate.CODEC.optionalFieldOf("head").forGetter(EntityEquipmentPredicate::head),
               ItemPredicate.CODEC.optionalFieldOf("chest").forGetter(EntityEquipmentPredicate::chest),
               ItemPredicate.CODEC.optionalFieldOf("legs").forGetter(EntityEquipmentPredicate::legs),
               ItemPredicate.CODEC.optionalFieldOf("feet").forGetter(EntityEquipmentPredicate::feet),
               ItemPredicate.CODEC.optionalFieldOf("mainhand").forGetter(EntityEquipmentPredicate::mainhand),
               ItemPredicate.CODEC.optionalFieldOf("offhand").forGetter(EntityEquipmentPredicate::offhand)
            )
            .apply(var0, EntityEquipmentPredicate::new)
   );

   public EntityEquipmentPredicate(
      Optional<ItemPredicate> head,
      Optional<ItemPredicate> chest,
      Optional<ItemPredicate> legs,
      Optional<ItemPredicate> feet,
      Optional<ItemPredicate> mainhand,
      Optional<ItemPredicate> offhand
   ) {
      super();
      this.head = head;
      this.chest = chest;
      this.legs = legs;
      this.feet = feet;
      this.mainhand = mainhand;
      this.offhand = offhand;
   }

   public static EntityEquipmentPredicate captainPredicate(HolderGetter<BannerPattern> var0) {
      return EntityEquipmentPredicate.Builder.equipment()
         .head(
            ItemPredicate.Builder.item().of(Items.WHITE_BANNER).hasComponents(DataComponentPredicate.allOf(Raid.getLeaderBannerInstance(var0).getComponents()))
         )
         .build();
   }

   public boolean matches(@Nullable Entity var1) {
      if (var1 instanceof LivingEntity var2) {
         if (this.head.isPresent() && !this.head.get().matches(var2.getItemBySlot(EquipmentSlot.HEAD))) {
            return false;
         } else if (this.chest.isPresent() && !this.chest.get().matches(var2.getItemBySlot(EquipmentSlot.CHEST))) {
            return false;
         } else if (this.legs.isPresent() && !this.legs.get().matches(var2.getItemBySlot(EquipmentSlot.LEGS))) {
            return false;
         } else if (this.feet.isPresent() && !this.feet.get().matches(var2.getItemBySlot(EquipmentSlot.FEET))) {
            return false;
         } else {
            return this.mainhand.isPresent() && !this.mainhand.get().matches(var2.getItemBySlot(EquipmentSlot.MAINHAND))
               ? false
               : !this.offhand.isPresent() || this.offhand.get().matches(var2.getItemBySlot(EquipmentSlot.OFFHAND));
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
      private Optional<ItemPredicate> mainhand = Optional.empty();
      private Optional<ItemPredicate> offhand = Optional.empty();

      public Builder() {
         super();
      }

      public static EntityEquipmentPredicate.Builder equipment() {
         return new EntityEquipmentPredicate.Builder();
      }

      public EntityEquipmentPredicate.Builder head(ItemPredicate.Builder var1) {
         this.head = Optional.of(var1.build());
         return this;
      }

      public EntityEquipmentPredicate.Builder chest(ItemPredicate.Builder var1) {
         this.chest = Optional.of(var1.build());
         return this;
      }

      public EntityEquipmentPredicate.Builder legs(ItemPredicate.Builder var1) {
         this.legs = Optional.of(var1.build());
         return this;
      }

      public EntityEquipmentPredicate.Builder feet(ItemPredicate.Builder var1) {
         this.feet = Optional.of(var1.build());
         return this;
      }

      public EntityEquipmentPredicate.Builder mainhand(ItemPredicate.Builder var1) {
         this.mainhand = Optional.of(var1.build());
         return this;
      }

      public EntityEquipmentPredicate.Builder offhand(ItemPredicate.Builder var1) {
         this.offhand = Optional.of(var1.build());
         return this;
      }

      public EntityEquipmentPredicate build() {
         return new EntityEquipmentPredicate(this.head, this.chest, this.legs, this.feet, this.mainhand, this.offhand);
      }
   }
}

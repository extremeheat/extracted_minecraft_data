package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerPattern;

public record EntityEquipmentPredicate(
   Optional<ItemPredicate> b,
   Optional<ItemPredicate> c,
   Optional<ItemPredicate> d,
   Optional<ItemPredicate> e,
   Optional<ItemPredicate> f,
   Optional<ItemPredicate> g
) {
   private final Optional<ItemPredicate> head;
   private final Optional<ItemPredicate> chest;
   private final Optional<ItemPredicate> legs;
   private final Optional<ItemPredicate> feet;
   private final Optional<ItemPredicate> mainhand;
   private final Optional<ItemPredicate> offhand;
   public static final Codec<EntityEquipmentPredicate> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "head").forGetter(EntityEquipmentPredicate::head),
               ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "chest").forGetter(EntityEquipmentPredicate::chest),
               ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "legs").forGetter(EntityEquipmentPredicate::legs),
               ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "feet").forGetter(EntityEquipmentPredicate::feet),
               ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "mainhand").forGetter(EntityEquipmentPredicate::mainhand),
               ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "offhand").forGetter(EntityEquipmentPredicate::offhand)
            )
            .apply(var0, EntityEquipmentPredicate::new)
   );

   public EntityEquipmentPredicate(
      Optional<ItemPredicate> var1,
      Optional<ItemPredicate> var2,
      Optional<ItemPredicate> var3,
      Optional<ItemPredicate> var4,
      Optional<ItemPredicate> var5,
      Optional<ItemPredicate> var6
   ) {
      super();
      this.head = var1;
      this.chest = var2;
      this.legs = var3;
      this.feet = var4;
      this.mainhand = var5;
      this.offhand = var6;
   }

   public static EntityEquipmentPredicate captainPredicate(HolderGetter<BannerPattern> var0) {
      return EntityEquipmentPredicate.Builder.equipment()
         .head(
            ItemPredicate.Builder.item().of(Items.WHITE_BANNER).hasComponents(DataComponentPredicate.allOf(Raid.getLeaderBannerInstance(var0).getComponents()))
         )
         .build();
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public boolean matches(@Nullable Entity var1) {
      if (var1 instanceof LivingEntity var2) {
         if (this.head.isPresent() && !((ItemPredicate)this.head.get()).matches(var2.getItemBySlot(EquipmentSlot.HEAD))) {
            return false;
         } else if (this.chest.isPresent() && !((ItemPredicate)this.chest.get()).matches(var2.getItemBySlot(EquipmentSlot.CHEST))) {
            return false;
         } else if (this.legs.isPresent() && !((ItemPredicate)this.legs.get()).matches(var2.getItemBySlot(EquipmentSlot.LEGS))) {
            return false;
         } else if (this.feet.isPresent() && !((ItemPredicate)this.feet.get()).matches(var2.getItemBySlot(EquipmentSlot.FEET))) {
            return false;
         } else if (this.mainhand.isPresent() && !((ItemPredicate)this.mainhand.get()).matches(var2.getItemBySlot(EquipmentSlot.MAINHAND))) {
            return false;
         } else {
            return !this.offhand.isPresent() || ((ItemPredicate)this.offhand.get()).matches(var2.getItemBySlot(EquipmentSlot.OFFHAND));
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

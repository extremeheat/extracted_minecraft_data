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

public record EntityEquipmentPredicate(Optional<ItemPredicate> head, Optional<ItemPredicate> chest, Optional<ItemPredicate> legs, Optional<ItemPredicate> feet, Optional<ItemPredicate> mainhand, Optional<ItemPredicate> offhand) {
   public static final Codec<EntityEquipmentPredicate> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ItemPredicate.CODEC.optionalFieldOf("head").forGetter(EntityEquipmentPredicate::head), ItemPredicate.CODEC.optionalFieldOf("chest").forGetter(EntityEquipmentPredicate::chest), ItemPredicate.CODEC.optionalFieldOf("legs").forGetter(EntityEquipmentPredicate::legs), ItemPredicate.CODEC.optionalFieldOf("feet").forGetter(EntityEquipmentPredicate::feet), ItemPredicate.CODEC.optionalFieldOf("mainhand").forGetter(EntityEquipmentPredicate::mainhand), ItemPredicate.CODEC.optionalFieldOf("offhand").forGetter(EntityEquipmentPredicate::offhand)).apply(var0, EntityEquipmentPredicate::new);
   });

   public EntityEquipmentPredicate(Optional<ItemPredicate> var1, Optional<ItemPredicate> var2, Optional<ItemPredicate> var3, Optional<ItemPredicate> var4, Optional<ItemPredicate> var5, Optional<ItemPredicate> var6) {
      super();
      this.head = var1;
      this.chest = var2;
      this.legs = var3;
      this.feet = var4;
      this.mainhand = var5;
      this.offhand = var6;
   }

   public static EntityEquipmentPredicate captainPredicate(HolderGetter<BannerPattern> var0) {
      return EntityEquipmentPredicate.Builder.equipment().head(ItemPredicate.Builder.item().of(Items.WHITE_BANNER).hasComponents(DataComponentPredicate.allOf(Raid.getLeaderBannerInstance(var0).getComponents()))).build();
   }

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

   public Optional<ItemPredicate> head() {
      return this.head;
   }

   public Optional<ItemPredicate> chest() {
      return this.chest;
   }

   public Optional<ItemPredicate> legs() {
      return this.legs;
   }

   public Optional<ItemPredicate> feet() {
      return this.feet;
   }

   public Optional<ItemPredicate> mainhand() {
      return this.mainhand;
   }

   public Optional<ItemPredicate> offhand() {
      return this.offhand;
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

      public static Builder equipment() {
         return new Builder();
      }

      public Builder head(ItemPredicate.Builder var1) {
         this.head = Optional.of(var1.build());
         return this;
      }

      public Builder chest(ItemPredicate.Builder var1) {
         this.chest = Optional.of(var1.build());
         return this;
      }

      public Builder legs(ItemPredicate.Builder var1) {
         this.legs = Optional.of(var1.build());
         return this;
      }

      public Builder feet(ItemPredicate.Builder var1) {
         this.feet = Optional.of(var1.build());
         return this;
      }

      public Builder mainhand(ItemPredicate.Builder var1) {
         this.mainhand = Optional.of(var1.build());
         return this;
      }

      public Builder offhand(ItemPredicate.Builder var1) {
         this.offhand = Optional.of(var1.build());
         return this;
      }

      public EntityEquipmentPredicate build() {
         return new EntityEquipmentPredicate(this.head, this.chest, this.legs, this.feet, this.mainhand, this.offhand);
      }
   }
}

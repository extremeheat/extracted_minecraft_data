package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public record ItemDamagePredicate(MinMaxBounds.Ints durability, MinMaxBounds.Ints damage) implements SingleComponentItemPredicate<Integer> {
   public static final Codec<ItemDamagePredicate> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(MinMaxBounds.Ints.CODEC.optionalFieldOf("durability", MinMaxBounds.Ints.ANY).forGetter(ItemDamagePredicate::durability), MinMaxBounds.Ints.CODEC.optionalFieldOf("damage", MinMaxBounds.Ints.ANY).forGetter(ItemDamagePredicate::damage)).apply(var0, ItemDamagePredicate::new);
   });

   public ItemDamagePredicate(MinMaxBounds.Ints var1, MinMaxBounds.Ints var2) {
      super();
      this.durability = var1;
      this.damage = var2;
   }

   public DataComponentType<Integer> componentType() {
      return DataComponents.DAMAGE;
   }

   public boolean matches(ItemStack var1, Integer var2) {
      if (!this.durability.matches(var1.getMaxDamage() - var2)) {
         return false;
      } else {
         return this.damage.matches(var2);
      }
   }

   public static ItemDamagePredicate durability(MinMaxBounds.Ints var0) {
      return new ItemDamagePredicate(var0, MinMaxBounds.Ints.ANY);
   }

   public MinMaxBounds.Ints durability() {
      return this.durability;
   }

   public MinMaxBounds.Ints damage() {
      return this.damage;
   }
}

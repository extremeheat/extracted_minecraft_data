package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;

public record DamagePredicate(MinMaxBounds.Ints durability, MinMaxBounds.Ints damage) implements DataComponentPredicate {
   public static final Codec<DamagePredicate> CODEC = RecordCodecBuilder.create((var0) -> var0.group(MinMaxBounds.Ints.CODEC.optionalFieldOf("durability", MinMaxBounds.Ints.ANY).forGetter(DamagePredicate::durability), MinMaxBounds.Ints.CODEC.optionalFieldOf("damage", MinMaxBounds.Ints.ANY).forGetter(DamagePredicate::damage)).apply(var0, DamagePredicate::new));

   public DamagePredicate(MinMaxBounds.Ints var1, MinMaxBounds.Ints var2) {
      super();
      this.durability = var1;
      this.damage = var2;
   }

   public boolean matches(DataComponentGetter var1) {
      Integer var2 = (Integer)var1.get(DataComponents.DAMAGE);
      if (var2 == null) {
         return false;
      } else {
         int var3 = (Integer)var1.getOrDefault(DataComponents.MAX_DAMAGE, 0);
         if (!this.durability.matches(var3 - var2)) {
            return false;
         } else {
            return this.damage.matches(var2);
         }
      }
   }

   public static DamagePredicate durability(MinMaxBounds.Ints var0) {
      return new DamagePredicate(var0, MinMaxBounds.Ints.ANY);
   }
}

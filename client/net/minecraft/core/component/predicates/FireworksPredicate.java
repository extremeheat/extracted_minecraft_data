package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.critereon.CollectionPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;

public record FireworksPredicate(Optional<CollectionPredicate<FireworkExplosion, FireworkExplosionPredicate.FireworkPredicate>> explosions, MinMaxBounds.Ints flightDuration) implements SingleComponentItemPredicate<Fireworks> {
   public static final Codec<FireworksPredicate> CODEC = RecordCodecBuilder.create((var0) -> var0.group(CollectionPredicate.codec(FireworkExplosionPredicate.FireworkPredicate.CODEC).optionalFieldOf("explosions").forGetter(FireworksPredicate::explosions), MinMaxBounds.Ints.CODEC.optionalFieldOf("flight_duration", MinMaxBounds.Ints.ANY).forGetter(FireworksPredicate::flightDuration)).apply(var0, FireworksPredicate::new));

   public FireworksPredicate(Optional<CollectionPredicate<FireworkExplosion, FireworkExplosionPredicate.FireworkPredicate>> var1, MinMaxBounds.Ints var2) {
      super();
      this.explosions = var1;
      this.flightDuration = var2;
   }

   public DataComponentType<Fireworks> componentType() {
      return DataComponents.FIREWORKS;
   }

   public boolean matches(Fireworks var1) {
      if (this.explosions.isPresent() && !((CollectionPredicate)this.explosions.get()).test(var1.explosions())) {
         return false;
      } else {
         return this.flightDuration.matches(var1.flightDuration());
      }
   }
}

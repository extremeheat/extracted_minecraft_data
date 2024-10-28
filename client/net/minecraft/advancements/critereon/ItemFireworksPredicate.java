package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;

public record ItemFireworksPredicate(Optional<CollectionPredicate<FireworkExplosion, ItemFireworkExplosionPredicate.FireworkPredicate>> explosions, MinMaxBounds.Ints flightDuration) implements SingleComponentItemPredicate<Fireworks> {
   public static final Codec<ItemFireworksPredicate> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(CollectionPredicate.codec(ItemFireworkExplosionPredicate.FireworkPredicate.CODEC).optionalFieldOf("explosions").forGetter(ItemFireworksPredicate::explosions), MinMaxBounds.Ints.CODEC.optionalFieldOf("flight_duration", MinMaxBounds.Ints.ANY).forGetter(ItemFireworksPredicate::flightDuration)).apply(var0, ItemFireworksPredicate::new);
   });

   public ItemFireworksPredicate(Optional<CollectionPredicate<FireworkExplosion, ItemFireworkExplosionPredicate.FireworkPredicate>> explosions, MinMaxBounds.Ints flightDuration) {
      super();
      this.explosions = explosions;
      this.flightDuration = flightDuration;
   }

   public DataComponentType<Fireworks> componentType() {
      return DataComponents.FIREWORKS;
   }

   public boolean matches(ItemStack var1, Fireworks var2) {
      if (this.explosions.isPresent() && !((CollectionPredicate)this.explosions.get()).test((Iterable)var2.explosions())) {
         return false;
      } else {
         return this.flightDuration.matches(var2.flightDuration());
      }
   }

   public Optional<CollectionPredicate<FireworkExplosion, ItemFireworkExplosionPredicate.FireworkPredicate>> explosions() {
      return this.explosions;
   }

   public MinMaxBounds.Ints flightDuration() {
      return this.flightDuration;
   }
}

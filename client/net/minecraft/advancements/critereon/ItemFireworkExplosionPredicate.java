package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;

public record ItemFireworkExplosionPredicate(ItemFireworkExplosionPredicate.FireworkPredicate predicate)
   implements SingleComponentItemPredicate<FireworkExplosion> {
   public static final Codec<ItemFireworkExplosionPredicate> CODEC = ItemFireworkExplosionPredicate.FireworkPredicate.CODEC
      .xmap(ItemFireworkExplosionPredicate::new, ItemFireworkExplosionPredicate::predicate);

   public ItemFireworkExplosionPredicate(ItemFireworkExplosionPredicate.FireworkPredicate predicate) {
      super();
      this.predicate = predicate;
   }

   @Override
   public DataComponentType<FireworkExplosion> componentType() {
      return DataComponents.FIREWORK_EXPLOSION;
   }

   public boolean matches(ItemStack var1, FireworkExplosion var2) {
      return this.predicate.test(var2);
   }

   public static record FireworkPredicate(Optional<FireworkExplosion.Shape> shape, Optional<Boolean> twinkle, Optional<Boolean> trail)
      implements Predicate<FireworkExplosion> {
      public static final Codec<ItemFireworkExplosionPredicate.FireworkPredicate> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  FireworkExplosion.Shape.CODEC.optionalFieldOf("shape").forGetter(ItemFireworkExplosionPredicate.FireworkPredicate::shape),
                  Codec.BOOL.optionalFieldOf("has_twinkle").forGetter(ItemFireworkExplosionPredicate.FireworkPredicate::twinkle),
                  Codec.BOOL.optionalFieldOf("has_trail").forGetter(ItemFireworkExplosionPredicate.FireworkPredicate::trail)
               )
               .apply(var0, ItemFireworkExplosionPredicate.FireworkPredicate::new)
      );

      public FireworkPredicate(Optional<FireworkExplosion.Shape> shape, Optional<Boolean> twinkle, Optional<Boolean> trail) {
         super();
         this.shape = shape;
         this.twinkle = twinkle;
         this.trail = trail;
      }

      public boolean test(FireworkExplosion var1) {
         if (this.shape.isPresent() && this.shape.get() != var1.shape()) {
            return false;
         } else {
            return this.twinkle.isPresent() && this.twinkle.get() != var1.hasTwinkle() ? false : !this.trail.isPresent() || this.trail.get() == var1.hasTrail();
         }
      }
   }
}

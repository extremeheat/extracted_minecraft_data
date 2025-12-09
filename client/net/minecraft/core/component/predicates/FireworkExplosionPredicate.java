package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.advancements.criterion.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.FireworkExplosion;

public record FireworkExplosionPredicate(FireworkPredicate predicate) implements SingleComponentItemPredicate<FireworkExplosion> {
   public static final Codec<FireworkExplosionPredicate> CODEC;

   public FireworkExplosionPredicate(FireworkPredicate var1) {
      super();
      this.predicate = var1;
   }

   public DataComponentType<FireworkExplosion> componentType() {
      return DataComponents.FIREWORK_EXPLOSION;
   }

   public boolean matches(FireworkExplosion var1) {
      return this.predicate.test(var1);
   }

   static {
      CODEC = FireworkExplosionPredicate.FireworkPredicate.CODEC.xmap(FireworkExplosionPredicate::new, FireworkExplosionPredicate::predicate);
   }

   public static record FireworkPredicate(Optional<FireworkExplosion.Shape> shape, Optional<Boolean> twinkle, Optional<Boolean> trail) implements Predicate<FireworkExplosion> {
      public static final Codec<FireworkPredicate> CODEC = RecordCodecBuilder.create((var0) -> var0.group(FireworkExplosion.Shape.CODEC.optionalFieldOf("shape").forGetter(FireworkPredicate::shape), Codec.BOOL.optionalFieldOf("has_twinkle").forGetter(FireworkPredicate::twinkle), Codec.BOOL.optionalFieldOf("has_trail").forGetter(FireworkPredicate::trail)).apply(var0, FireworkPredicate::new));

      public FireworkPredicate(Optional<FireworkExplosion.Shape> var1, Optional<Boolean> var2, Optional<Boolean> var3) {
         super();
         this.shape = var1;
         this.twinkle = var2;
         this.trail = var3;
      }

      public boolean test(FireworkExplosion var1) {
         if (this.shape.isPresent() && this.shape.get() != var1.shape()) {
            return false;
         } else if (this.twinkle.isPresent() && (Boolean)this.twinkle.get() != var1.hasTwinkle()) {
            return false;
         } else {
            return !this.trail.isPresent() || (Boolean)this.trail.get() == var1.hasTrail();
         }
      }

      // $FF: synthetic method
      public boolean test(final Object var1) {
         return this.test((FireworkExplosion)var1);
      }
   }
}

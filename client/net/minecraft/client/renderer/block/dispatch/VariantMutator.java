package net.minecraft.client.renderer.block.dispatch;

import com.mojang.math.Quadrant;
import java.util.function.UnaryOperator;
import net.minecraft.resources.Identifier;

@FunctionalInterface
public interface VariantMutator extends UnaryOperator<Variant> {
   VariantProperty<Quadrant> X_ROT = Variant::withXRot;
   VariantProperty<Quadrant> Y_ROT = Variant::withYRot;
   VariantProperty<Quadrant> Z_ROT = Variant::withZRot;
   VariantProperty<Identifier> MODEL = Variant::withModel;
   VariantProperty<Boolean> UV_LOCK = Variant::withUvLock;

   default VariantMutator then(final VariantMutator other) {
      return (variant) -> (Variant)other.apply((Variant)this.apply(variant));
   }

   @FunctionalInterface
   public interface VariantProperty<T> {
      Variant apply(Variant input, T value);

      default VariantMutator withValue(final T value) {
         return (variant) -> this.apply(variant, value);
      }
   }
}

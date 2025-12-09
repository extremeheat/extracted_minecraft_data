package net.minecraft.client.renderer.block.model;

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

   default VariantMutator then(VariantMutator var1) {
      return (var2) -> (Variant)var1.apply((Variant)this.apply(var2));
   }

   @FunctionalInterface
   public interface VariantProperty<T> {
      Variant apply(Variant var1, T var2);

      default VariantMutator withValue(T var1) {
         return (var2) -> this.apply(var2, var1);
      }
   }
}

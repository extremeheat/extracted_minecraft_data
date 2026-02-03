package net.minecraft.client.resources.model;

import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.resources.Identifier;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public interface ModelBaker {
   ResolvedModel getModel(Identifier location);

   BlockModelPart missingBlockModelPart();

   SpriteGetter sprites();

   PartCache parts();

   <T> T compute(SharedOperationKey<T> key);

   public interface PartCache {
      default Vector3fc vector(final float x, final float y, final float z) {
         return this.vector(new Vector3f(x, y, z));
      }

      Vector3fc vector(Vector3fc vector);
   }

   @FunctionalInterface
   public interface SharedOperationKey<T> {
      T compute(ModelBaker modelBakery);
   }
}

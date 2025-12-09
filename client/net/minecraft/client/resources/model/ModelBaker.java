package net.minecraft.client.resources.model;

import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.resources.Identifier;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public interface ModelBaker {
   ResolvedModel getModel(Identifier var1);

   BlockModelPart missingBlockModelPart();

   SpriteGetter sprites();

   PartCache parts();

   <T> T compute(SharedOperationKey<T> var1);

   public interface PartCache {
      default Vector3fc vector(float var1, float var2, float var3) {
         return this.vector(new Vector3f(var1, var2, var3));
      }

      Vector3fc vector(Vector3fc var1);
   }

   @FunctionalInterface
   public interface SharedOperationKey<T> {
      T compute(ModelBaker var1);
   }
}

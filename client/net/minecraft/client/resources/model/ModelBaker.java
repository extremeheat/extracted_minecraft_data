package net.minecraft.client.resources.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.resources.Identifier;
import org.joml.Vector3fc;

public interface ModelBaker {
   ResolvedModel getModel(Identifier location);

   BlockModelPart missingBlockModelPart();

   MaterialBaker materials();

   Interner interner();

   <T> T compute(SharedOperationKey<T> key);

   public interface Interner {
      Vector3fc vector(Vector3fc vector);

      BakedQuad.SpriteInfo spriteInfo(BakedQuad.SpriteInfo sprite);
   }

   @FunctionalInterface
   public interface SharedOperationKey<T> {
      T compute(ModelBaker modelBakery);
   }
}

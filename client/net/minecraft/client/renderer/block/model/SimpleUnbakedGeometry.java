package net.minecraft.client.renderer.block.model;

import java.util.List;
import java.util.Map;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.UnbakedGeometry;
import net.minecraft.core.Direction;
import org.joml.Vector3fc;

public record SimpleUnbakedGeometry(List<BlockElement> elements) implements UnbakedGeometry {
   public SimpleUnbakedGeometry {
      super();
   }

   public QuadCollection bake(final TextureSlots textures, final ModelBaker modelBaker, final ModelState modelState, final ModelDebugName name) {
      return bake(this.elements, textures, modelBaker, modelState, name);
   }

   public static QuadCollection bake(final List<BlockElement> elements, final TextureSlots textures, final ModelBaker modelBaker, final ModelState modelState, final ModelDebugName name) {
      QuadCollection.Builder builder = new QuadCollection.Builder();

      for(BlockElement element : elements) {
         boolean drawXFaces = true;
         boolean drawYFaces = true;
         boolean drawZFaces = true;
         Vector3fc from = element.from();
         Vector3fc to = element.to();
         if (from.x() == to.x()) {
            drawYFaces = false;
            drawZFaces = false;
         }

         if (from.y() == to.y()) {
            drawXFaces = false;
            drawZFaces = false;
         }

         if (from.z() == to.z()) {
            drawXFaces = false;
            drawYFaces = false;
         }

         if (drawXFaces || drawYFaces || drawZFaces) {
            for(Map.Entry<Direction, BlockElementFace> entry : element.faces().entrySet()) {
               Direction facing = (Direction)entry.getKey();
               BlockElementFace face = (BlockElementFace)entry.getValue();
               boolean var10000;
               switch (facing.getAxis()) {
                  case X -> var10000 = drawXFaces;
                  case Y -> var10000 = drawYFaces;
                  case Z -> var10000 = drawZFaces;
                  default -> throw new MatchException((String)null, (Throwable)null);
               }

               boolean shouldDrawFace = var10000;
               if (shouldDrawFace) {
                  Material.Baked material = modelBaker.materials().resolveSlot(textures, face.texture(), name);
                  BakedQuad quad = FaceBakery.bakeQuad(modelBaker, from, to, face, material, facing, modelState, element.rotation(), element.shade(), element.lightEmission());
                  if (face.cullForDirection() == null) {
                     builder.addUnculledFace(quad);
                  } else {
                     builder.addCulledFace(Direction.rotate(modelState.transformation().getMatrix(), face.cullForDirection()), quad);
                  }
               }
            }
         }
      }

      return builder.build();
   }
}

package net.minecraft.client.resources.model.geometry;

import java.util.ArrayList;
import java.util.List;

public record ItemQuads(List<BakedQuad> all, List<BakedQuad> solid, List<BakedQuad> translucent) {
   public static final ItemQuads EMPTY = new ItemQuads(List.of(), List.of(), List.of());

   public ItemQuads {
      super();
   }

   public static ItemQuads split(final List<BakedQuad> quads) {
      if (quads.isEmpty()) {
         return EMPTY;
      } else {
         List<BakedQuad> solid = new ArrayList();
         List<BakedQuad> translucent = new ArrayList();

         for(BakedQuad quad : quads) {
            if (quad.materialInfo().itemRenderType().hasBlending()) {
               translucent.add(quad);
            } else {
               solid.add(quad);
            }
         }

         if (translucent.isEmpty()) {
            List<BakedQuad> all = List.copyOf(quads);
            return new ItemQuads(all, all, List.of());
         } else if (solid.isEmpty()) {
            List<BakedQuad> all = List.copyOf(quads);
            return new ItemQuads(all, List.of(), all);
         } else {
            return new ItemQuads(List.copyOf(quads), List.copyOf(solid), List.copyOf(translucent));
         }
      }
   }

   public boolean isEmpty() {
      return this.all.isEmpty();
   }
}

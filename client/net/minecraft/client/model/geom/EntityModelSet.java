package net.minecraft.client.model.geom;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class EntityModelSet {
   public static final EntityModelSet EMPTY = new EntityModelSet(Map.of());
   private final Map<ModelLayerLocation, LayerDefinition> roots;

   public EntityModelSet(Map<ModelLayerLocation, LayerDefinition> var1) {
      super();
      this.roots = var1;
   }

   public ModelPart bakeLayer(ModelLayerLocation var1) {
      LayerDefinition var2 = (LayerDefinition)this.roots.get(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("No model for layer " + String.valueOf(var1));
      } else {
         return var2.bakeRoot();
      }
   }

   public static EntityModelSet vanilla() {
      return new EntityModelSet(ImmutableMap.copyOf(LayerDefinitions.createRoots()));
   }
}

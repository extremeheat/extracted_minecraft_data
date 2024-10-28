package net.minecraft.client.model.geom;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class EntityModelSet implements ResourceManagerReloadListener {
   private Map<ModelLayerLocation, LayerDefinition> roots = ImmutableMap.of();

   public EntityModelSet() {
      super();
   }

   public ModelPart bakeLayer(ModelLayerLocation var1) {
      LayerDefinition var2 = (LayerDefinition)this.roots.get(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("No model for layer " + String.valueOf(var1));
      } else {
         return var2.bakeRoot();
      }
   }

   public void onResourceManagerReload(ResourceManager var1) {
      this.roots = ImmutableMap.copyOf(LayerDefinitions.createRoots());
   }
}

package net.minecraft.client.model.geom;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class EntityModelSet implements ResourceManagerReloadListener {
   private Map<ModelLayerLocation, ModelPart> roots = ImmutableMap.of();

   public EntityModelSet() {
      super();
   }

   public ModelPart getLayer(ModelLayerLocation var1) {
      ModelPart var2 = (ModelPart)this.roots.get(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("No model for layer " + var1);
      } else {
         return var2;
      }
   }

   public void onResourceManagerReload(ResourceManager var1) {
      this.roots = ImmutableMap.copyOf(LayerDefinitions.createRoots());
   }
}

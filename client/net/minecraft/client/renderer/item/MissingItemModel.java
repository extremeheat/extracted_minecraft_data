package net.minecraft.client.renderer.item;

import com.google.common.base.Suppliers;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class MissingItemModel implements ItemModel {
   private final List<BakedQuad> quads;
   private final Supplier<Vector3fc[]> extents;
   private final ModelRenderProperties properties;

   public MissingItemModel(final List<BakedQuad> quads, final ModelRenderProperties properties) {
      super();
      this.quads = quads;
      this.properties = properties;
      this.extents = Suppliers.memoize(() -> BlockModelWrapper.computeExtents(this.quads));
   }

   public void update(final ItemStackRenderState output, final ItemStack item, final ItemModelResolver resolver, final ItemDisplayContext displayContext, final @Nullable ClientLevel level, final @Nullable ItemOwner owner, final int seed) {
      output.appendModelIdentityElement(this);
      ItemStackRenderState.LayerRenderState layer = output.newLayer();
      this.properties.applyToLayer(layer, displayContext);
      layer.setExtents(this.extents);
      layer.prepareQuadList().addAll(this.quads);
   }
}

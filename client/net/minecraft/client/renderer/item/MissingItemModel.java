package net.minecraft.client.renderer.item;

import com.google.common.base.Suppliers;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

public class MissingItemModel implements ItemModel {
   private final List<BakedQuad> quads;
   private final Supplier<Vector3f[]> extents;
   private final ModelRenderProperties properties;

   public MissingItemModel(List<BakedQuad> var1, ModelRenderProperties var2) {
      super();
      this.quads = var1;
      this.properties = var2;
      this.extents = Suppliers.memoize(() -> BlockModelWrapper.computeExtents(this.quads));
   }

   public void update(ItemStackRenderState var1, ItemStack var2, ItemModelResolver var3, ItemDisplayContext var4, @Nullable ClientLevel var5, @Nullable LivingEntity var6, int var7) {
      ItemStackRenderState.LayerRenderState var8 = var1.newLayer();
      var8.setRenderType(Sheets.cutoutBlockSheet());
      this.properties.applyToLayer(var8, var4);
      var8.setExtents(this.extents);
      var8.prepareQuadList().addAll(this.quads);
   }
}

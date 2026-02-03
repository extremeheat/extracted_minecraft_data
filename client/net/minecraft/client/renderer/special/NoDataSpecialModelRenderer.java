package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public interface NoDataSpecialModelRenderer extends SpecialModelRenderer<Void> {
   default @Nullable Void extractArgument(final ItemStack stack) {
      return null;
   }

   default void submit(final @Nullable Void argument, final ItemDisplayContext type, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final boolean hasFoil, final int outlineColor) {
      this.submit(type, poseStack, submitNodeCollector, lightCoords, overlayCoords, hasFoil, outlineColor);
   }

   void submit(ItemDisplayContext type, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, final int outlineColor);
}

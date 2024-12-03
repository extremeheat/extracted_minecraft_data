package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public interface NoDataSpecialModelRenderer extends SpecialModelRenderer<Void> {
   @Nullable
   default Void extractArgument(ItemStack var1) {
      return null;
   }

   default void render(@Nullable Void var1, ItemDisplayContext var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, boolean var7) {
      this.render(var2, var3, var4, var5, var6, var7);
   }

   void render(ItemDisplayContext var1, PoseStack var2, MultiBufferSource var3, int var4, int var5, boolean var6);

   // $FF: synthetic method
   @Nullable
   default Object extractArgument(final ItemStack var1) {
      return this.extractArgument(var1);
   }
}

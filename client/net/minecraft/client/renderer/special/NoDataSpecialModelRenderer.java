package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public interface NoDataSpecialModelRenderer extends SpecialModelRenderer<Void> {
   default @Nullable Void extractArgument(ItemStack var1) {
      return null;
   }

   default void submit(@Nullable Void var1, ItemDisplayContext var2, PoseStack var3, SubmitNodeCollector var4, int var5, int var6, boolean var7, int var8) {
      this.submit(var2, var3, var4, var5, var6, var7, var8);
   }

   void submit(ItemDisplayContext var1, PoseStack var2, SubmitNodeCollector var3, int var4, int var5, boolean var6, int var7);

   // $FF: synthetic method
   default @Nullable Object extractArgument(final ItemStack var1) {
      return this.extractArgument(var1);
   }
}

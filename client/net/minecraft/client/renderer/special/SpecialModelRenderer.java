package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public interface SpecialModelRenderer<T> {
   void render(@Nullable T var1, ItemDisplayContext var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, boolean var7);

   @Nullable
   T extractArgument(ItemStack var1);

   public interface Unbaked {
      @Nullable
      SpecialModelRenderer<?> bake(EntityModelSet var1);

      MapCodec<? extends Unbaked> type();
   }
}

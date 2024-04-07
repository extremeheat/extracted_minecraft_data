package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CrossedArmsItemLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
   private final ItemInHandRenderer itemInHandRenderer;

   public CrossedArmsItemLayer(RenderLayerParent<T, M> var1, ItemInHandRenderer var2) {
      super(var1);
      this.itemInHandRenderer = var2;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      var1.pushPose();
      var1.translate(0.0F, 0.4F, -0.4F);
      var1.mulPose(Axis.XP.rotationDegrees(180.0F));
      ItemStack var11 = var4.getItemBySlot(EquipmentSlot.MAINHAND);
      this.itemInHandRenderer.renderItem(var4, var11, ItemDisplayContext.GROUND, false, var1, var2, var3);
      var1.popPose();
   }
}

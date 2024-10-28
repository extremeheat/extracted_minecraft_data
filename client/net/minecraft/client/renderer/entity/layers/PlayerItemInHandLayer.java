package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PlayerItemInHandLayer<T extends Player, M extends EntityModel<T> & ArmedModel & HeadedModel> extends ItemInHandLayer<T, M> {
   private final ItemInHandRenderer itemInHandRenderer;
   private static final float X_ROT_MIN = -0.5235988F;
   private static final float X_ROT_MAX = 1.5707964F;

   public PlayerItemInHandLayer(RenderLayerParent<T, M> var1, ItemInHandRenderer var2) {
      super(var1, var2);
      this.itemInHandRenderer = var2;
   }

   protected void renderArmWithItem(LivingEntity var1, ItemStack var2, ItemDisplayContext var3, HumanoidArm var4, PoseStack var5, MultiBufferSource var6, int var7) {
      if (var2.is(Items.SPYGLASS) && var1.getUseItem() == var2 && var1.swingTime == 0) {
         this.renderArmWithSpyglass(var1, var2, var4, var5, var6, var7);
      } else {
         super.renderArmWithItem(var1, var2, var3, var4, var5, var6, var7);
      }

   }

   private void renderArmWithSpyglass(LivingEntity var1, ItemStack var2, HumanoidArm var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      ModelPart var7 = ((HeadedModel)this.getParentModel()).getHead();
      float var8 = var7.xRot;
      var7.xRot = Mth.clamp(var7.xRot, -0.5235988F, 1.5707964F);
      var7.translateAndRotate(var4);
      var7.xRot = var8;
      CustomHeadLayer.translateToHead(var4, false);
      boolean var9 = var3 == HumanoidArm.LEFT;
      var4.translate((var9 ? -2.5F : 2.5F) / 16.0F, -0.0625F, 0.0F);
      this.itemInHandRenderer.renderItem(var1, var2, ItemDisplayContext.HEAD, false, var4, var5, var6);
      var4.popPose();
   }
}

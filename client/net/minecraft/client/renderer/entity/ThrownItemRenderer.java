package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ThrownItemRenderer<T extends Entity & ItemSupplier> extends EntityRenderer<T, ThrownItemRenderState> {
   private final ItemRenderer itemRenderer;
   private final float scale;
   private final boolean fullBright;

   public ThrownItemRenderer(EntityRendererProvider.Context var1, float var2, boolean var3) {
      super(var1);
      this.itemRenderer = var1.getItemRenderer();
      this.scale = var2;
      this.fullBright = var3;
   }

   public ThrownItemRenderer(EntityRendererProvider.Context var1) {
      this(var1, 1.0F, false);
   }

   @Override
   protected int getBlockLightLevel(T var1, BlockPos var2) {
      return this.fullBright ? 15 : super.getBlockLightLevel((T)var1, var2);
   }

   public void render(ThrownItemRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      var2.pushPose();
      var2.scale(this.scale, this.scale, this.scale);
      var2.mulPose(this.entityRenderDispatcher.cameraOrientation());
      if (var1.itemModel != null) {
         this.itemRenderer.render(var1.item, ItemDisplayContext.GROUND, false, var2, var3, var4, OverlayTexture.NO_OVERLAY, var1.itemModel);
      }

      var2.popPose();
      super.render(var1, var2, var3, var4);
   }

   public ThrownItemRenderState createRenderState() {
      return new ThrownItemRenderState();
   }

   public void extractRenderState(T var1, ThrownItemRenderState var2, float var3) {
      super.extractRenderState((T)var1, var2, var3);
      ItemStack var4 = ((ItemSupplier)var1).getItem();
      var2.itemModel = !var4.isEmpty() ? this.itemRenderer.getModel(var4, var1.level(), null, var1.getId()) : null;
      var2.item = var4;
   }

   public ResourceLocation getTextureLocation(ThrownItemRenderState var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}

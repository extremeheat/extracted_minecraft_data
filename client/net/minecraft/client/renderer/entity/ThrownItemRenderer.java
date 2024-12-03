package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemDisplayContext;

public class ThrownItemRenderer<T extends Entity & ItemSupplier> extends EntityRenderer<T, ThrownItemRenderState> {
   private final ItemModelResolver itemModelResolver;
   private final float scale;
   private final boolean fullBright;

   public ThrownItemRenderer(EntityRendererProvider.Context var1, float var2, boolean var3) {
      super(var1);
      this.itemModelResolver = var1.getItemModelResolver();
      this.scale = var2;
      this.fullBright = var3;
   }

   public ThrownItemRenderer(EntityRendererProvider.Context var1) {
      this(var1, 1.0F, false);
   }

   protected int getBlockLightLevel(T var1, BlockPos var2) {
      return this.fullBright ? 15 : super.getBlockLightLevel(var1, var2);
   }

   public void render(ThrownItemRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      var2.pushPose();
      var2.scale(this.scale, this.scale, this.scale);
      var2.mulPose(this.entityRenderDispatcher.cameraOrientation());
      var1.item.render(var2, var3, var4, OverlayTexture.NO_OVERLAY);
      var2.popPose();
      super.render(var1, var2, var3, var4);
   }

   public ThrownItemRenderState createRenderState() {
      return new ThrownItemRenderState();
   }

   public void extractRenderState(T var1, ThrownItemRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      this.itemModelResolver.updateForNonLiving(var2.item, ((ItemSupplier)var1).getItem(), ItemDisplayContext.GROUND, var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}

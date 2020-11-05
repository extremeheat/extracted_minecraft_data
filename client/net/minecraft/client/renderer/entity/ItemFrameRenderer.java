package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;

public class ItemFrameRenderer extends EntityRenderer<ItemFrame> {
   private static final ModelResourceLocation FRAME_LOCATION = new ModelResourceLocation("item_frame", "map=false");
   private static final ModelResourceLocation MAP_FRAME_LOCATION = new ModelResourceLocation("item_frame", "map=true");
   private final Minecraft minecraft = Minecraft.getInstance();
   private final ItemRenderer itemRenderer;

   public ItemFrameRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.itemRenderer = var1.getItemRenderer();
   }

   public void render(ItemFrame var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      super.render(var1, var2, var3, var4, var5, var6);
      var4.pushPose();
      Direction var7 = var1.getDirection();
      Vec3 var8 = this.getRenderOffset(var1, var3);
      var4.translate(-var8.x(), -var8.y(), -var8.z());
      double var9 = 0.46875D;
      var4.translate((double)var7.getStepX() * 0.46875D, (double)var7.getStepY() * 0.46875D, (double)var7.getStepZ() * 0.46875D);
      var4.mulPose(Vector3f.XP.rotationDegrees(var1.xRot));
      var4.mulPose(Vector3f.YP.rotationDegrees(180.0F - var1.yRot));
      boolean var11 = var1.isInvisible();
      ItemStack var12 = var1.getItem();
      if (!var11) {
         BlockRenderDispatcher var13 = this.minecraft.getBlockRenderer();
         ModelManager var14 = var13.getBlockModelShaper().getModelManager();
         ModelResourceLocation var15 = var12.is(Items.FILLED_MAP) ? MAP_FRAME_LOCATION : FRAME_LOCATION;
         var4.pushPose();
         var4.translate(-0.5D, -0.5D, -0.5D);
         var13.getModelRenderer().renderModel(var4.last(), var5.getBuffer(Sheets.solidBlockSheet()), (BlockState)null, var14.getModel(var15), 1.0F, 1.0F, 1.0F, var6, OverlayTexture.NO_OVERLAY);
         var4.popPose();
      }

      if (!var12.isEmpty()) {
         boolean var17 = var12.is(Items.FILLED_MAP);
         if (var11) {
            var4.translate(0.0D, 0.0D, 0.5D);
         } else {
            var4.translate(0.0D, 0.0D, 0.4375D);
         }

         int var18 = var17 ? var1.getRotation() % 4 * 2 : var1.getRotation();
         var4.mulPose(Vector3f.ZP.rotationDegrees((float)var18 * 360.0F / 8.0F));
         if (var17) {
            var4.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
            float var19 = 0.0078125F;
            var4.scale(0.0078125F, 0.0078125F, 0.0078125F);
            var4.translate(-64.0D, -64.0D, 0.0D);
            MapItemSavedData var16 = MapItem.getOrCreateSavedData(var12, var1.level);
            var4.translate(0.0D, 0.0D, -1.0D);
            if (var16 != null) {
               this.minecraft.gameRenderer.getMapRenderer().render(var4, var5, var16, true, var6);
            }
         } else {
            var4.scale(0.5F, 0.5F, 0.5F);
            this.itemRenderer.renderStatic(var12, ItemTransforms.TransformType.FIXED, var6, OverlayTexture.NO_OVERLAY, var4, var5);
         }
      }

      var4.popPose();
   }

   public Vec3 getRenderOffset(ItemFrame var1, float var2) {
      return new Vec3((double)((float)var1.getDirection().getStepX() * 0.3F), -0.25D, (double)((float)var1.getDirection().getStepZ() * 0.3F));
   }

   public ResourceLocation getTextureLocation(ItemFrame var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }

   protected boolean shouldShowName(ItemFrame var1) {
      if (Minecraft.renderNames() && !var1.getItem().isEmpty() && var1.getItem().hasCustomHoverName() && this.entityRenderDispatcher.crosshairPickEntity == var1) {
         double var2 = this.entityRenderDispatcher.distanceToSqr(var1);
         float var4 = var1.isDiscrete() ? 32.0F : 64.0F;
         return var2 < (double)(var4 * var4);
      } else {
         return false;
      }
   }

   protected void renderNameTag(ItemFrame var1, Component var2, PoseStack var3, MultiBufferSource var4, int var5) {
      super.renderNameTag(var1, var1.getItem().getHoverName(), var3, var4, var5);
   }
}

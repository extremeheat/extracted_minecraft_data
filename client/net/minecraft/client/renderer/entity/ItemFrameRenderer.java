package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.OptionalInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;

public class ItemFrameRenderer<T extends ItemFrame> extends EntityRenderer<T> {
   private static final ModelResourceLocation FRAME_LOCATION = ModelResourceLocation.vanilla("item_frame", "map=false");
   private static final ModelResourceLocation MAP_FRAME_LOCATION = ModelResourceLocation.vanilla("item_frame", "map=true");
   private static final ModelResourceLocation GLOW_FRAME_LOCATION = ModelResourceLocation.vanilla("glow_item_frame", "map=false");
   private static final ModelResourceLocation GLOW_MAP_FRAME_LOCATION = ModelResourceLocation.vanilla("glow_item_frame", "map=true");
   public static final int GLOW_FRAME_BRIGHTNESS = 5;
   public static final int BRIGHT_MAP_LIGHT_ADJUSTMENT = 30;
   private final ItemRenderer itemRenderer;
   private final BlockRenderDispatcher blockRenderer;

   public ItemFrameRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.itemRenderer = var1.getItemRenderer();
      this.blockRenderer = var1.getBlockRenderDispatcher();
   }

   protected int getBlockLightLevel(T var1, BlockPos var2) {
      return var1.getType() == EntityType.GLOW_ITEM_FRAME ? Math.max(5, super.getBlockLightLevel((T)var1, var2)) : super.getBlockLightLevel((T)var1, var2);
   }

   public void render(T var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      super.render((T)var1, var2, var3, var4, var5, var6);
      var4.pushPose();
      Direction var7 = var1.getDirection();
      Vec3 var8 = this.getRenderOffset((T)var1, var3);
      var4.translate(-var8.x(), -var8.y(), -var8.z());
      double var9 = 0.46875;
      var4.translate((double)var7.getStepX() * 0.46875, (double)var7.getStepY() * 0.46875, (double)var7.getStepZ() * 0.46875);
      var4.mulPose(Axis.XP.rotationDegrees(var1.getXRot()));
      var4.mulPose(Axis.YP.rotationDegrees(180.0F - var1.getYRot()));
      boolean var11 = var1.isInvisible();
      ItemStack var12 = var1.getItem();
      if (!var11) {
         ModelManager var13 = this.blockRenderer.getBlockModelShaper().getModelManager();
         ModelResourceLocation var14 = this.getFrameModelResourceLoc((T)var1, var12);
         var4.pushPose();
         var4.translate(-0.5F, -0.5F, -0.5F);
         this.blockRenderer
            .getModelRenderer()
            .renderModel(var4.last(), var5.getBuffer(Sheets.solidBlockSheet()), null, var13.getModel(var14), 1.0F, 1.0F, 1.0F, var6, OverlayTexture.NO_OVERLAY);
         var4.popPose();
      }

      if (!var12.isEmpty()) {
         OptionalInt var18 = var1.getFramedMapId();
         if (var11) {
            var4.translate(0.0F, 0.0F, 0.5F);
         } else {
            var4.translate(0.0F, 0.0F, 0.4375F);
         }

         int var19 = var18.isPresent() ? var1.getRotation() % 4 * 2 : var1.getRotation();
         var4.mulPose(Axis.ZP.rotationDegrees((float)var19 * 360.0F / 8.0F));
         if (var18.isPresent()) {
            var4.mulPose(Axis.ZP.rotationDegrees(180.0F));
            float var15 = 0.0078125F;
            var4.scale(0.0078125F, 0.0078125F, 0.0078125F);
            var4.translate(-64.0F, -64.0F, 0.0F);
            MapItemSavedData var16 = MapItem.getSavedData(var18.getAsInt(), var1.level());
            var4.translate(0.0F, 0.0F, -1.0F);
            if (var16 != null) {
               int var17 = this.getLightVal((T)var1, 15728850, var6);
               Minecraft.getInstance().gameRenderer.getMapRenderer().render(var4, var5, var18.getAsInt(), var16, true, var17);
            }
         } else {
            int var20 = this.getLightVal((T)var1, 15728880, var6);
            var4.scale(0.5F, 0.5F, 0.5F);
            this.itemRenderer.renderStatic(var12, ItemDisplayContext.FIXED, var20, OverlayTexture.NO_OVERLAY, var4, var5, var1.level(), var1.getId());
         }
      }

      var4.popPose();
   }

   private int getLightVal(T var1, int var2, int var3) {
      return var1.getType() == EntityType.GLOW_ITEM_FRAME ? var2 : var3;
   }

   private ModelResourceLocation getFrameModelResourceLoc(T var1, ItemStack var2) {
      boolean var3 = var1.getType() == EntityType.GLOW_ITEM_FRAME;
      if (var2.is(Items.FILLED_MAP)) {
         return var3 ? GLOW_MAP_FRAME_LOCATION : MAP_FRAME_LOCATION;
      } else {
         return var3 ? GLOW_FRAME_LOCATION : FRAME_LOCATION;
      }
   }

   public Vec3 getRenderOffset(T var1, float var2) {
      return new Vec3((double)((float)var1.getDirection().getStepX() * 0.3F), -0.25, (double)((float)var1.getDirection().getStepZ() * 0.3F));
   }

   public ResourceLocation getTextureLocation(T var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }

   protected boolean shouldShowName(T var1) {
      if (Minecraft.renderNames()
         && !var1.getItem().isEmpty()
         && var1.getItem().hasCustomHoverName()
         && this.entityRenderDispatcher.crosshairPickEntity == var1) {
         double var2 = this.entityRenderDispatcher.distanceToSqr(var1);
         float var4 = var1.isDiscrete() ? 32.0F : 64.0F;
         return var2 < (double)(var4 * var4);
      } else {
         return false;
      }
   }

   protected void renderNameTag(T var1, Component var2, PoseStack var3, MultiBufferSource var4, int var5) {
      super.renderNameTag((T)var1, var1.getItem().getHoverName(), var3, var4, var5);
   }
}

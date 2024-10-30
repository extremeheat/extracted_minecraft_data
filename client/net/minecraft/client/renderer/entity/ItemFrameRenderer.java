package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;

public class ItemFrameRenderer<T extends ItemFrame> extends EntityRenderer<T, ItemFrameRenderState> {
   public static final int GLOW_FRAME_BRIGHTNESS = 5;
   public static final int BRIGHT_MAP_LIGHT_ADJUSTMENT = 30;
   private final ItemRenderer itemRenderer;
   private final MapRenderer mapRenderer;
   private final BlockRenderDispatcher blockRenderer;

   public ItemFrameRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.itemRenderer = var1.getItemRenderer();
      this.mapRenderer = var1.getMapRenderer();
      this.blockRenderer = var1.getBlockRenderDispatcher();
   }

   protected int getBlockLightLevel(T var1, BlockPos var2) {
      return var1.getType() == EntityType.GLOW_ITEM_FRAME ? Math.max(5, super.getBlockLightLevel(var1, var2)) : super.getBlockLightLevel(var1, var2);
   }

   public void render(ItemFrameRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      super.render(var1, var2, var3, var4);
      var2.pushPose();
      Direction var5 = var1.direction;
      Vec3 var6 = this.getRenderOffset(var1);
      var2.translate(-var6.x(), -var6.y(), -var6.z());
      double var7 = 0.46875;
      var2.translate((double)var5.getStepX() * 0.46875, (double)var5.getStepY() * 0.46875, (double)var5.getStepZ() * 0.46875);
      float var9;
      float var10;
      if (var5.getAxis().isHorizontal()) {
         var9 = 0.0F;
         var10 = 180.0F - var5.toYRot();
      } else {
         var9 = (float)(-90 * var5.getAxisDirection().getStep());
         var10 = 180.0F;
      }

      var2.mulPose(Axis.XP.rotationDegrees(var9));
      var2.mulPose(Axis.YP.rotationDegrees(var10));
      ItemStack var11 = var1.itemStack;
      if (!var1.isInvisible) {
         ModelManager var12 = this.blockRenderer.getBlockModelShaper().getModelManager();
         ModelResourceLocation var13 = this.getFrameModelResourceLoc(var1.isGlowFrame, var11);
         var2.pushPose();
         var2.translate(-0.5F, -0.5F, -0.5F);
         this.blockRenderer.getModelRenderer().renderModel(var2.last(), var3.getBuffer(RenderType.entitySolidZOffsetForward(TextureAtlas.LOCATION_BLOCKS)), (BlockState)null, var12.getModel(var13), 1.0F, 1.0F, 1.0F, var4, OverlayTexture.NO_OVERLAY);
         var2.popPose();
      }

      if (!var11.isEmpty()) {
         MapId var16 = var1.mapId;
         if (var1.isInvisible) {
            var2.translate(0.0F, 0.0F, 0.5F);
         } else {
            var2.translate(0.0F, 0.0F, 0.4375F);
         }

         int var17 = var16 != null ? var1.rotation % 4 * 2 : var1.rotation;
         var2.mulPose(Axis.ZP.rotationDegrees((float)var17 * 360.0F / 8.0F));
         if (var16 != null) {
            var2.mulPose(Axis.ZP.rotationDegrees(180.0F));
            float var14 = 0.0078125F;
            var2.scale(0.0078125F, 0.0078125F, 0.0078125F);
            var2.translate(-64.0F, -64.0F, 0.0F);
            var2.translate(0.0F, 0.0F, -1.0F);
            int var15 = this.getLightVal(var1.isGlowFrame, 15728850, var4);
            this.mapRenderer.render(var1.mapRenderState, var2, var3, true, var15);
         } else if (var1.itemModel != null) {
            int var18 = this.getLightVal(var1.isGlowFrame, 15728880, var4);
            var2.scale(0.5F, 0.5F, 0.5F);
            this.itemRenderer.render(var11, ItemDisplayContext.FIXED, false, var2, var3, var18, OverlayTexture.NO_OVERLAY, var1.itemModel);
         }
      }

      var2.popPose();
   }

   private int getLightVal(boolean var1, int var2, int var3) {
      return var1 ? var2 : var3;
   }

   private ModelResourceLocation getFrameModelResourceLoc(boolean var1, ItemStack var2) {
      if (var2.has(DataComponents.MAP_ID)) {
         return var1 ? BlockStateModelLoader.GLOW_MAP_FRAME_LOCATION : BlockStateModelLoader.MAP_FRAME_LOCATION;
      } else {
         return var1 ? BlockStateModelLoader.GLOW_FRAME_LOCATION : BlockStateModelLoader.FRAME_LOCATION;
      }
   }

   public Vec3 getRenderOffset(ItemFrameRenderState var1) {
      return new Vec3((double)((float)var1.direction.getStepX() * 0.3F), -0.25, (double)((float)var1.direction.getStepZ() * 0.3F));
   }

   protected boolean shouldShowName(T var1, double var2) {
      return Minecraft.renderNames() && this.entityRenderDispatcher.crosshairPickEntity == var1 && var1.getItem().getCustomName() != null;
   }

   protected Component getNameTag(T var1) {
      return var1.getItem().getHoverName();
   }

   public ItemFrameRenderState createRenderState() {
      return new ItemFrameRenderState();
   }

   public void extractRenderState(T var1, ItemFrameRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.direction = var1.getDirection();
      ItemStack var4 = var1.getItem();
      var2.itemStack = var4.copy();
      var2.rotation = var1.getRotation();
      var2.isGlowFrame = var1.getType() == EntityType.GLOW_ITEM_FRAME;
      var2.itemModel = null;
      var2.mapId = null;
      if (!var2.itemStack.isEmpty()) {
         MapId var5 = var1.getFramedMapId(var4);
         if (var5 != null) {
            MapItemSavedData var6 = var1.level().getMapData(var5);
            if (var6 != null) {
               this.mapRenderer.extractRenderState(var5, var6, var2.mapRenderState);
               var2.mapId = var5;
            }
         } else {
            var2.itemModel = this.itemRenderer.getModel(var4, var1.level(), (LivingEntity)null, var1.getId());
         }
      }

   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }

   // $FF: synthetic method
   protected Component getNameTag(final Entity var1) {
      return this.getNameTag((ItemFrame)var1);
   }
}

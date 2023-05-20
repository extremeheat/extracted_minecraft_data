package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Transformation;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Display;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public abstract class DisplayRenderer<T extends Display> extends EntityRenderer<T> {
   private static final float MAX_SHADOW_RADIUS = 64.0F;
   private final EntityRenderDispatcher entityRenderDispatcher;

   protected DisplayRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.entityRenderDispatcher = var1.getEntityRenderDispatcher();
   }

   public ResourceLocation getTextureLocation(T var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }

   public void render(T var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      float var7 = var1.calculateInterpolationProgress(var3);
      this.shadowRadius = Math.min(var1.getShadowRadius(var7), 64.0F);
      this.shadowStrength = var1.getShadowStrength(var7);
      int var8 = var1.getPackedBrightnessOverride();
      int var9 = var8 != -1 ? var8 : var6;
      super.render((T)var1, var2, var3, var4, var5, var9);
      var4.pushPose();
      var4.mulPose(this.calculateOrientation((T)var1));
      Transformation var10 = var1.transformation(var7);
      var4.mulPoseMatrix(var10.getMatrix());
      var4.last().normal().rotate(var10.getLeftRotation()).rotate(var10.getRightRotation());
      this.renderInner((T)var1, var4, var5, var9, var7);
      var4.popPose();
   }

   private Quaternionf calculateOrientation(T var1) {
      Camera var2 = this.entityRenderDispatcher.camera;

      return switch(var1.getBillboardConstraints()) {
         case FIXED -> var1.orientation();
         case HORIZONTAL -> new Quaternionf().rotationYXZ(-0.017453292F * var1.getYRot(), -0.017453292F * var2.getXRot(), 0.0F);
         case VERTICAL -> new Quaternionf().rotationYXZ(3.1415927F - 0.017453292F * var2.getYRot(), 0.017453292F * var1.getXRot(), 0.0F);
         case CENTER -> new Quaternionf().rotationYXZ(3.1415927F - 0.017453292F * var2.getYRot(), -0.017453292F * var2.getXRot(), 0.0F);
      };
   }

   protected abstract void renderInner(T var1, PoseStack var2, MultiBufferSource var3, int var4, float var5);

   public static class BlockDisplayRenderer extends DisplayRenderer<Display.BlockDisplay> {
      private final BlockRenderDispatcher blockRenderer;

      protected BlockDisplayRenderer(EntityRendererProvider.Context var1) {
         super(var1);
         this.blockRenderer = var1.getBlockRenderDispatcher();
      }

      public void renderInner(Display.BlockDisplay var1, PoseStack var2, MultiBufferSource var3, int var4, float var5) {
         this.blockRenderer.renderSingleBlock(var1.getBlockState(), var2, var3, var4, OverlayTexture.NO_OVERLAY);
      }
   }

   public static class ItemDisplayRenderer extends DisplayRenderer<Display.ItemDisplay> {
      private final ItemRenderer itemRenderer;

      protected ItemDisplayRenderer(EntityRendererProvider.Context var1) {
         super(var1);
         this.itemRenderer = var1.getItemRenderer();
      }

      public void renderInner(Display.ItemDisplay var1, PoseStack var2, MultiBufferSource var3, int var4, float var5) {
         this.itemRenderer
            .renderStatic(var1.getItemStack(), var1.getItemTransform(), var4, OverlayTexture.NO_OVERLAY, var2, var3, var1.getLevel(), var1.getId());
      }
   }

   public static class TextDisplayRenderer extends DisplayRenderer<Display.TextDisplay> {
      private final Font font;

      protected TextDisplayRenderer(EntityRendererProvider.Context var1) {
         super(var1);
         this.font = var1.getFont();
      }

      private Display.TextDisplay.CachedInfo splitLines(Component var1, int var2) {
         List var3 = this.font.split(var1, var2);
         ArrayList var4 = new ArrayList(var3.size());
         int var5 = 0;

         for(FormattedCharSequence var7 : var3) {
            int var8 = this.font.width(var7);
            var5 = Math.max(var5, var8);
            var4.add(new Display.TextDisplay.CachedLine(var7, var8));
         }

         return new Display.TextDisplay.CachedInfo(var4, var5);
      }

      public void renderInner(Display.TextDisplay var1, PoseStack var2, MultiBufferSource var3, int var4, float var5) {
         byte var6 = var1.getFlags();
         boolean var7 = (var6 & 2) != 0;
         boolean var8 = (var6 & 4) != 0;
         boolean var9 = (var6 & 1) != 0;
         Display.TextDisplay.Align var10 = Display.TextDisplay.getAlign(var6);
         byte var11 = var1.getTextOpacity(var5);
         int var12;
         if (var8) {
            float var13 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            var12 = (int)(var13 * 255.0F) << 24;
         } else {
            var12 = var1.getBackgroundColor(var5);
         }

         float var22 = 0.0F;
         Matrix4f var14 = var2.last().pose();
         var14.rotate(3.1415927F, 0.0F, 1.0F, 0.0F);
         var14.scale(-0.025F, -0.025F, -0.025F);
         Display.TextDisplay.CachedInfo var15 = var1.cacheDisplay(this::splitLines);
         int var16 = 9 + 1;
         int var17 = var15.width();
         int var18 = var15.lines().size() * var16;
         var14.translate(1.0F - (float)var17 / 2.0F, (float)(-var18), 0.0F);
         if (var12 != 0) {
            VertexConsumer var19 = var3.getBuffer(var7 ? RenderType.textBackgroundSeeThrough() : RenderType.textBackground());
            var19.vertex(var14, -1.0F, -1.0F, 0.0F).color(var12).uv2(var4).endVertex();
            var19.vertex(var14, -1.0F, (float)var18, 0.0F).color(var12).uv2(var4).endVertex();
            var19.vertex(var14, (float)var17, (float)var18, 0.0F).color(var12).uv2(var4).endVertex();
            var19.vertex(var14, (float)var17, -1.0F, 0.0F).color(var12).uv2(var4).endVertex();
         }

         for(Display.TextDisplay.CachedLine var20 : var15.lines()) {
            float var21 = switch(var10) {
               case LEFT -> 0.0F;
               case RIGHT -> (float)(var17 - var20.width());
               case CENTER -> (float)var17 / 2.0F - (float)var20.width() / 2.0F;
            };
            this.font
               .drawInBatch(
                  var20.contents(),
                  var21,
                  var22,
                  var11 << 24 | 16777215,
                  var9,
                  var14,
                  var3,
                  var7 ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.POLYGON_OFFSET,
                  0,
                  var4
               );
            var22 += (float)var16;
         }
      }
   }
}

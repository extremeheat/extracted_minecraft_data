package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public abstract class DisplayRenderer<T extends Display, S> extends EntityRenderer<T> {
   private final EntityRenderDispatcher entityRenderDispatcher;

   protected DisplayRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.entityRenderDispatcher = var1.getEntityRenderDispatcher();
   }

   public ResourceLocation getTextureLocation(T var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }

   public void render(T var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      Display.RenderState var7 = var1.renderState();
      if (var7 != null) {
         Object var8 = this.getSubState(var1);
         if (var8 != null) {
            float var9 = var1.calculateInterpolationProgress(var3);
            this.shadowRadius = var7.shadowRadius().get(var9);
            this.shadowStrength = var7.shadowStrength().get(var9);
            int var10 = var7.brightnessOverride();
            int var11 = var10 != -1 ? var10 : var6;
            super.render(var1, var2, var3, var4, var5, var11);
            var4.pushPose();
            var4.mulPose(this.calculateOrientation(var7, var1, var3, new Quaternionf()));
            Transformation var12 = (Transformation)var7.transformation().get(var9);
            var4.mulPose(var12.getMatrix());
            this.renderInner(var1, var8, var4, var5, var11, var9);
            var4.popPose();
         }
      }
   }

   private Quaternionf calculateOrientation(Display.RenderState var1, T var2, float var3, Quaternionf var4) {
      Camera var5 = this.entityRenderDispatcher.camera;
      Quaternionf var10000;
      switch (var1.billboardConstraints()) {
         case FIXED -> var10000 = var4.rotationYXZ(-0.017453292F * entityYRot(var2, var3), 0.017453292F * entityXRot(var2, var3), 0.0F);
         case HORIZONTAL -> var10000 = var4.rotationYXZ(-0.017453292F * entityYRot(var2, var3), 0.017453292F * cameraXRot(var5), 0.0F);
         case VERTICAL -> var10000 = var4.rotationYXZ(-0.017453292F * cameraYrot(var5), 0.017453292F * entityXRot(var2, var3), 0.0F);
         case CENTER -> var10000 = var4.rotationYXZ(-0.017453292F * cameraYrot(var5), 0.017453292F * cameraXRot(var5), 0.0F);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private static float cameraYrot(Camera var0) {
      return var0.getYRot() - 180.0F;
   }

   private static float cameraXRot(Camera var0) {
      return -var0.getXRot();
   }

   private static <T extends Display> float entityYRot(T var0, float var1) {
      return Mth.rotLerp(var1, var0.yRotO, var0.getYRot());
   }

   private static <T extends Display> float entityXRot(T var0, float var1) {
      return Mth.lerp(var1, var0.xRotO, var0.getXRot());
   }

   @Nullable
   protected abstract S getSubState(T var1);

   protected abstract void renderInner(T var1, S var2, PoseStack var3, MultiBufferSource var4, int var5, float var6);

   public static class TextDisplayRenderer extends DisplayRenderer<Display.TextDisplay, Display.TextDisplay.TextRenderState> {
      private final Font font;

      protected TextDisplayRenderer(EntityRendererProvider.Context var1) {
         super(var1);
         this.font = var1.getFont();
      }

      private Display.TextDisplay.CachedInfo splitLines(Component var1, int var2) {
         List var3 = this.font.split(var1, var2);
         ArrayList var4 = new ArrayList(var3.size());
         int var5 = 0;
         Iterator var6 = var3.iterator();

         while(var6.hasNext()) {
            FormattedCharSequence var7 = (FormattedCharSequence)var6.next();
            int var8 = this.font.width(var7);
            var5 = Math.max(var5, var8);
            var4.add(new Display.TextDisplay.CachedLine(var7, var8));
         }

         return new Display.TextDisplay.CachedInfo(var4, var5);
      }

      @Nullable
      protected Display.TextDisplay.TextRenderState getSubState(Display.TextDisplay var1) {
         return var1.textRenderState();
      }

      public void renderInner(Display.TextDisplay var1, Display.TextDisplay.TextRenderState var2, PoseStack var3, MultiBufferSource var4, int var5, float var6) {
         byte var7 = var2.flags();
         boolean var8 = (var7 & 2) != 0;
         boolean var9 = (var7 & 4) != 0;
         boolean var10 = (var7 & 1) != 0;
         Display.TextDisplay.Align var11 = Display.TextDisplay.getAlign(var7);
         byte var12 = (byte)var2.textOpacity().get(var6);
         int var13;
         float var14;
         if (var9) {
            var14 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            var13 = (int)(var14 * 255.0F) << 24;
         } else {
            var13 = var2.backgroundColor().get(var6);
         }

         var14 = 0.0F;
         Matrix4f var15 = var3.last().pose();
         var15.rotate(3.1415927F, 0.0F, 1.0F, 0.0F);
         var15.scale(-0.025F, -0.025F, -0.025F);
         Display.TextDisplay.CachedInfo var16 = var1.cacheDisplay(this::splitLines);
         Objects.requireNonNull(this.font);
         int var17 = 9 + 1;
         int var18 = var16.width();
         int var19 = var16.lines().size() * var17;
         var15.translate(1.0F - (float)var18 / 2.0F, (float)(-var19), 0.0F);
         if (var13 != 0) {
            VertexConsumer var20 = var4.getBuffer(var8 ? RenderType.textBackgroundSeeThrough() : RenderType.textBackground());
            var20.vertex(var15, -1.0F, -1.0F, 0.0F).color(var13).uv2(var5).endVertex();
            var20.vertex(var15, -1.0F, (float)var19, 0.0F).color(var13).uv2(var5).endVertex();
            var20.vertex(var15, (float)var18, (float)var19, 0.0F).color(var13).uv2(var5).endVertex();
            var20.vertex(var15, (float)var18, -1.0F, 0.0F).color(var13).uv2(var5).endVertex();
         }

         for(Iterator var23 = var16.lines().iterator(); var23.hasNext(); var14 += (float)var17) {
            Display.TextDisplay.CachedLine var21 = (Display.TextDisplay.CachedLine)var23.next();
            float var10000;
            switch (var11) {
               case LEFT -> var10000 = 0.0F;
               case RIGHT -> var10000 = (float)(var18 - var21.width());
               case CENTER -> var10000 = (float)var18 / 2.0F - (float)var21.width() / 2.0F;
               default -> throw new MatchException((String)null, (Throwable)null);
            }

            float var22 = var10000;
            this.font.drawInBatch((FormattedCharSequence)var21.contents(), var22, var14, var12 << 24 | 16777215, var10, var15, var4, var8 ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.POLYGON_OFFSET, 0, var5);
         }

      }

      // $FF: synthetic method
      @Nullable
      protected Object getSubState(Display var1) {
         return this.getSubState((Display.TextDisplay)var1);
      }
   }

   public static class ItemDisplayRenderer extends DisplayRenderer<Display.ItemDisplay, Display.ItemDisplay.ItemRenderState> {
      private final ItemRenderer itemRenderer;

      protected ItemDisplayRenderer(EntityRendererProvider.Context var1) {
         super(var1);
         this.itemRenderer = var1.getItemRenderer();
      }

      @Nullable
      protected Display.ItemDisplay.ItemRenderState getSubState(Display.ItemDisplay var1) {
         return var1.itemRenderState();
      }

      public void renderInner(Display.ItemDisplay var1, Display.ItemDisplay.ItemRenderState var2, PoseStack var3, MultiBufferSource var4, int var5, float var6) {
         var3.mulPose(Axis.YP.rotation(3.1415927F));
         this.itemRenderer.renderStatic(var2.itemStack(), var2.itemTransform(), var5, OverlayTexture.NO_OVERLAY, var3, var4, var1.level(), var1.getId());
      }

      // $FF: synthetic method
      @Nullable
      protected Object getSubState(Display var1) {
         return this.getSubState((Display.ItemDisplay)var1);
      }
   }

   public static class BlockDisplayRenderer extends DisplayRenderer<Display.BlockDisplay, Display.BlockDisplay.BlockRenderState> {
      private final BlockRenderDispatcher blockRenderer;

      protected BlockDisplayRenderer(EntityRendererProvider.Context var1) {
         super(var1);
         this.blockRenderer = var1.getBlockRenderDispatcher();
      }

      @Nullable
      protected Display.BlockDisplay.BlockRenderState getSubState(Display.BlockDisplay var1) {
         return var1.blockRenderState();
      }

      public void renderInner(Display.BlockDisplay var1, Display.BlockDisplay.BlockRenderState var2, PoseStack var3, MultiBufferSource var4, int var5, float var6) {
         this.blockRenderer.renderSingleBlock(var2.blockState(), var3, var4, var5, OverlayTexture.NO_OVERLAY);
      }

      // $FF: synthetic method
      @Nullable
      protected Object getSubState(Display var1) {
         return this.getSubState((Display.BlockDisplay)var1);
      }
   }
}

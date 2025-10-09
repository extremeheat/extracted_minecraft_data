package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.BlockDisplayEntityRenderState;
import net.minecraft.client.renderer.entity.state.DisplayEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ItemDisplayEntityRenderState;
import net.minecraft.client.renderer.entity.state.TextDisplayEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public abstract class DisplayRenderer<T extends Display, S, ST extends DisplayEntityRenderState> extends EntityRenderer<T, ST> {
   private final EntityRenderDispatcher entityRenderDispatcher;

   protected DisplayRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.entityRenderDispatcher = var1.getEntityRenderDispatcher();
   }

   protected AABB getBoundingBoxForCulling(T var1) {
      return var1.getBoundingBoxForCulling();
   }

   protected boolean affectedByCulling(T var1) {
      return var1.affectedByCulling();
   }

   private static int getBrightnessOverride(Display var0) {
      Display.RenderState var1 = var0.renderState();
      return var1 != null ? var1.brightnessOverride() : -1;
   }

   protected int getSkyLightLevel(T var1, BlockPos var2) {
      int var3 = getBrightnessOverride(var1);
      return var3 != -1 ? LightTexture.sky(var3) : super.getSkyLightLevel(var1, var2);
   }

   protected int getBlockLightLevel(T var1, BlockPos var2) {
      int var3 = getBrightnessOverride(var1);
      return var3 != -1 ? LightTexture.block(var3) : super.getBlockLightLevel(var1, var2);
   }

   protected float getShadowRadius(ST var1) {
      Display.RenderState var2 = var1.renderState;
      return var2 == null ? 0.0F : var2.shadowRadius().get(var1.interpolationProgress);
   }

   protected float getShadowStrength(ST var1) {
      Display.RenderState var2 = var1.renderState;
      return var2 == null ? 0.0F : var2.shadowStrength().get(var1.interpolationProgress);
   }

   public void submit(ST var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      Display.RenderState var5 = var1.renderState;
      if (var5 != null && var1.hasSubState()) {
         float var6 = var1.interpolationProgress;
         super.submit(var1, var2, var3, var4);
         var2.pushPose();
         var2.mulPose((Quaternionfc)this.calculateOrientation(var5, var1, new Quaternionf()));
         Transformation var7 = (Transformation)var5.transformation().get(var6);
         var2.mulPose(var7.getMatrix());
         this.submitInner(var1, var2, var3, var1.lightCoords, var6);
         var2.popPose();
      }
   }

   private Quaternionf calculateOrientation(Display.RenderState var1, ST var2, Quaternionf var3) {
      Quaternionf var10000;
      switch (var1.billboardConstraints()) {
         case FIXED -> var10000 = var3.rotationYXZ(-0.017453292F * var2.entityYRot, 0.017453292F * var2.entityXRot, 0.0F);
         case HORIZONTAL -> var10000 = var3.rotationYXZ(-0.017453292F * var2.entityYRot, 0.017453292F * transformXRot(var2.cameraXRot), 0.0F);
         case VERTICAL -> var10000 = var3.rotationYXZ(-0.017453292F * transformYRot(var2.cameraYRot), 0.017453292F * var2.entityXRot, 0.0F);
         case CENTER -> var10000 = var3.rotationYXZ(-0.017453292F * transformYRot(var2.cameraYRot), 0.017453292F * transformXRot(var2.cameraXRot), 0.0F);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private static float transformYRot(float var0) {
      return var0 - 180.0F;
   }

   private static float transformXRot(float var0) {
      return -var0;
   }

   private static <T extends Display> float entityYRot(T var0, float var1) {
      return var0.getYRot(var1);
   }

   private static <T extends Display> float entityXRot(T var0, float var1) {
      return var0.getXRot(var1);
   }

   protected abstract void submitInner(ST var1, PoseStack var2, SubmitNodeCollector var3, int var4, float var5);

   public void extractRenderState(T var1, ST var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.renderState = var1.renderState();
      var2.interpolationProgress = var1.calculateInterpolationProgress(var3);
      var2.entityYRot = entityYRot(var1, var3);
      var2.entityXRot = entityXRot(var1, var3);
      Camera var4 = this.entityRenderDispatcher.camera;
      var2.cameraXRot = var4.xRot();
      var2.cameraYRot = var4.yRot();
   }

   // $FF: synthetic method
   protected float getShadowRadius(final EntityRenderState var1) {
      return this.getShadowRadius((DisplayEntityRenderState)var1);
   }

   // $FF: synthetic method
   protected int getBlockLightLevel(final Entity var1, final BlockPos var2) {
      return this.getBlockLightLevel((Display)var1, var2);
   }

   // $FF: synthetic method
   protected int getSkyLightLevel(final Entity var1, final BlockPos var2) {
      return this.getSkyLightLevel((Display)var1, var2);
   }

   public static class BlockDisplayRenderer extends DisplayRenderer<Display.BlockDisplay, Display.BlockDisplay.BlockRenderState, BlockDisplayEntityRenderState> {
      protected BlockDisplayRenderer(EntityRendererProvider.Context var1) {
         super(var1);
      }

      public BlockDisplayEntityRenderState createRenderState() {
         return new BlockDisplayEntityRenderState();
      }

      public void extractRenderState(Display.BlockDisplay var1, BlockDisplayEntityRenderState var2, float var3) {
         super.extractRenderState(var1, var2, var3);
         var2.blockRenderState = var1.blockRenderState();
      }

      public void submitInner(BlockDisplayEntityRenderState var1, PoseStack var2, SubmitNodeCollector var3, int var4, float var5) {
         var3.submitBlock(var2, var1.blockRenderState.blockState(), var4, OverlayTexture.NO_OVERLAY, var1.outlineColor);
      }

      // $FF: synthetic method
      public EntityRenderState createRenderState() {
         return this.createRenderState();
      }

      // $FF: synthetic method
      protected float getShadowRadius(final EntityRenderState var1) {
         return super.getShadowRadius((DisplayEntityRenderState)var1);
      }

      // $FF: synthetic method
      protected int getBlockLightLevel(final Entity var1, final BlockPos var2) {
         return super.getBlockLightLevel((Display)var1, var2);
      }

      // $FF: synthetic method
      protected int getSkyLightLevel(final Entity var1, final BlockPos var2) {
         return super.getSkyLightLevel((Display)var1, var2);
      }
   }

   public static class ItemDisplayRenderer extends DisplayRenderer<Display.ItemDisplay, Display.ItemDisplay.ItemRenderState, ItemDisplayEntityRenderState> {
      private final ItemModelResolver itemModelResolver;

      protected ItemDisplayRenderer(EntityRendererProvider.Context var1) {
         super(var1);
         this.itemModelResolver = var1.getItemModelResolver();
      }

      public ItemDisplayEntityRenderState createRenderState() {
         return new ItemDisplayEntityRenderState();
      }

      public void extractRenderState(Display.ItemDisplay var1, ItemDisplayEntityRenderState var2, float var3) {
         super.extractRenderState(var1, var2, var3);
         Display.ItemDisplay.ItemRenderState var4 = var1.itemRenderState();
         if (var4 != null) {
            this.itemModelResolver.updateForNonLiving(var2.item, var4.itemStack(), var4.itemTransform(), var1);
         } else {
            var2.item.clear();
         }

      }

      public void submitInner(ItemDisplayEntityRenderState var1, PoseStack var2, SubmitNodeCollector var3, int var4, float var5) {
         if (!var1.item.isEmpty()) {
            var2.mulPose((Quaternionfc)Axis.YP.rotation(3.1415927F));
            var1.item.submit(var2, var3, var4, OverlayTexture.NO_OVERLAY, var1.outlineColor);
         }
      }

      // $FF: synthetic method
      public EntityRenderState createRenderState() {
         return this.createRenderState();
      }

      // $FF: synthetic method
      protected float getShadowRadius(final EntityRenderState var1) {
         return super.getShadowRadius((DisplayEntityRenderState)var1);
      }

      // $FF: synthetic method
      protected int getBlockLightLevel(final Entity var1, final BlockPos var2) {
         return super.getBlockLightLevel((Display)var1, var2);
      }

      // $FF: synthetic method
      protected int getSkyLightLevel(final Entity var1, final BlockPos var2) {
         return super.getSkyLightLevel((Display)var1, var2);
      }
   }

   public static class TextDisplayRenderer extends DisplayRenderer<Display.TextDisplay, Display.TextDisplay.TextRenderState, TextDisplayEntityRenderState> {
      private final Font font;

      protected TextDisplayRenderer(EntityRendererProvider.Context var1) {
         super(var1);
         this.font = var1.getFont();
      }

      public TextDisplayEntityRenderState createRenderState() {
         return new TextDisplayEntityRenderState();
      }

      public void extractRenderState(Display.TextDisplay var1, TextDisplayEntityRenderState var2, float var3) {
         super.extractRenderState(var1, var2, var3);
         var2.textRenderState = var1.textRenderState();
         var2.cachedInfo = var1.cacheDisplay(this::splitLines);
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

      public void submitInner(TextDisplayEntityRenderState var1, PoseStack var2, SubmitNodeCollector var3, int var4, float var5) {
         Display.TextDisplay.TextRenderState var6 = var1.textRenderState;
         byte var7 = var6.flags();
         boolean var8 = (var7 & 2) != 0;
         boolean var9 = (var7 & 4) != 0;
         boolean var10 = (var7 & 1) != 0;
         Display.TextDisplay.Align var11 = Display.TextDisplay.getAlign(var7);
         byte var12 = (byte)var6.textOpacity().get(var5);
         int var13;
         if (var9) {
            float var14 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            var13 = (int)(var14 * 255.0F) << 24;
         } else {
            var13 = var6.backgroundColor().get(var5);
         }

         float var25 = 0.0F;
         Matrix4f var15 = var2.last().pose();
         var15.rotate(3.1415927F, 0.0F, 1.0F, 0.0F);
         var15.scale(-0.025F, -0.025F, -0.025F);
         Display.TextDisplay.CachedInfo var16 = var1.cachedInfo;
         boolean var17 = true;
         Objects.requireNonNull(this.font);
         int var18 = 9 + 1;
         int var19 = var16.width();
         int var20 = var16.lines().size() * var18 - 1;
         var15.translate(1.0F - (float)var19 / 2.0F, (float)(-var20), 0.0F);
         if (var13 != 0) {
            var3.submitCustomGeometry(var2, var8 ? RenderType.textBackgroundSeeThrough() : RenderType.textBackground(), (var4x, var5x) -> {
               var5x.addVertex(var4x, -1.0F, -1.0F, 0.0F).setColor(var13).setLight(var4);
               var5x.addVertex(var4x, -1.0F, (float)var20, 0.0F).setColor(var13).setLight(var4);
               var5x.addVertex(var4x, (float)var19, (float)var20, 0.0F).setColor(var13).setLight(var4);
               var5x.addVertex(var4x, (float)var19, -1.0F, 0.0F).setColor(var13).setLight(var4);
            });
         }

         OrderedSubmitNodeCollector var21 = var3.order(var13 != 0 ? 1 : 0);

         for(Display.TextDisplay.CachedLine var23 : var16.lines()) {
            float var10000;
            switch (var11) {
               case LEFT -> var10000 = 0.0F;
               case RIGHT -> var10000 = (float)(var19 - var23.width());
               case CENTER -> var10000 = (float)var19 / 2.0F - (float)var23.width() / 2.0F;
               default -> throw new MatchException((String)null, (Throwable)null);
            }

            float var24 = var10000;
            var21.submitText(var2, var24, var25, var23.contents(), var10, var8 ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.POLYGON_OFFSET, var4, var12 << 24 | 16777215, 0, 0);
            var25 += (float)var18;
         }

      }

      // $FF: synthetic method
      public EntityRenderState createRenderState() {
         return this.createRenderState();
      }

      // $FF: synthetic method
      protected float getShadowRadius(final EntityRenderState var1) {
         return super.getShadowRadius((DisplayEntityRenderState)var1);
      }

      // $FF: synthetic method
      protected int getBlockLightLevel(final Entity var1, final BlockPos var2) {
         return super.getBlockLightLevel((Display)var1, var2);
      }

      // $FF: synthetic method
      protected int getSkyLightLevel(final Entity var1, final BlockPos var2) {
         return super.getSkyLightLevel((Display)var1, var2);
      }
   }
}

package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.WindowRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

public class ScreenEffectRenderer {
   private static final Identifier UNDERWATER_LOCATION = Identifier.withDefaultNamespace("textures/misc/underwater.png");
   private final Minecraft minecraft;
   private final SpriteGetter sprites;
   private final MultiBufferSource bufferSource;
   public static final int ITEM_ACTIVATION_ANIMATION_LENGTH = 40;
   private @Nullable ItemStack itemActivationItem;
   private int itemActivationTicks;
   private float itemActivationOffX;
   private float itemActivationOffY;

   public ScreenEffectRenderer(final Minecraft minecraft, final SpriteGetter sprites, final MultiBufferSource bufferSource) {
      super();
      this.minecraft = minecraft;
      this.sprites = sprites;
      this.bufferSource = bufferSource;
   }

   public void tick() {
      if (this.itemActivationTicks > 0) {
         --this.itemActivationTicks;
         if (this.itemActivationTicks == 0) {
            this.itemActivationItem = null;
         }
      }

   }

   public void renderScreenEffect(final boolean isFirstPerson, final boolean isSleeping, final float partialTicks, final SubmitNodeCollector submitNodeCollector, final boolean hideGui) {
      PoseStack poseStack = new PoseStack();
      Player player = this.minecraft.player;
      if (isFirstPerson && !isSleeping) {
         if (!player.noPhysics) {
            BlockState blockState = getViewBlockingState(player);
            if (blockState != null) {
               renderTex(this.minecraft.getModelManager().getBlockStateModelSet().getParticleMaterial(blockState).sprite(), poseStack, this.bufferSource);
            }
         }

         if (!this.minecraft.player.isSpectator()) {
            if (this.minecraft.player.isEyeInFluid(FluidTags.WATER)) {
               renderWater(this.minecraft, poseStack, this.bufferSource);
            }

            if (this.minecraft.player.isOnFire()) {
               TextureAtlasSprite fireSprite = this.sprites.get(ModelBakery.FIRE_1);
               renderFire(poseStack, this.bufferSource, fireSprite);
            }
         }
      }

      if (!hideGui) {
         this.renderItemActivationAnimation(poseStack, partialTicks, submitNodeCollector);
      }

   }

   private void renderItemActivationAnimation(final PoseStack poseStack, final float partialTicks, final SubmitNodeCollector submitNodeCollector) {
      if (this.itemActivationItem != null && this.itemActivationTicks > 0) {
         int tick = 40 - this.itemActivationTicks;
         float scale = ((float)tick + partialTicks) / 40.0F;
         float ts = scale * scale;
         float tc = scale * ts;
         float smoothScale = 10.25F * tc * ts - 24.95F * ts * ts + 25.5F * tc - 13.8F * ts + 4.0F * scale;
         float piScale = smoothScale * 3.1415927F;
         WindowRenderState windowState = this.minecraft.gameRenderer.getGameRenderState().windowRenderState;
         float aspectRatio = (float)windowState.width / (float)windowState.height;
         float offX = this.itemActivationOffX * 0.3F * aspectRatio;
         float offY = this.itemActivationOffY * 0.3F;
         poseStack.pushPose();
         poseStack.translate(offX * Mth.abs(Mth.sin((double)(piScale * 2.0F))), offY * Mth.abs(Mth.sin((double)(piScale * 2.0F))), -10.0F + 9.0F * Mth.sin((double)piScale));
         float size = 0.8F;
         poseStack.scale(0.8F, 0.8F, 0.8F);
         poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(900.0F * Mth.abs(Mth.sin((double)piScale))));
         poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(6.0F * Mth.cos((double)(scale * 8.0F))));
         poseStack.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(6.0F * Mth.cos((double)(scale * 8.0F))));
         this.minecraft.gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
         ItemStackRenderState itemState = new ItemStackRenderState();
         this.minecraft.getItemModelResolver().updateForTopItem(itemState, this.itemActivationItem, ItemDisplayContext.FIXED, this.minecraft.level, (ItemOwner)null, 0);
         itemState.submit(poseStack, submitNodeCollector, 15728880, OverlayTexture.NO_OVERLAY, 0);
         poseStack.popPose();
      }
   }

   public void resetItemActivation() {
      this.itemActivationItem = null;
   }

   public void displayItemActivation(final ItemStack itemStack, final RandomSource random) {
      this.itemActivationItem = itemStack;
      this.itemActivationTicks = 40;
      this.itemActivationOffX = random.nextFloat() * 2.0F - 1.0F;
      this.itemActivationOffY = random.nextFloat() * 2.0F - 1.0F;
   }

   private static @Nullable BlockState getViewBlockingState(final Player player) {
      BlockPos.MutableBlockPos testPos = new BlockPos.MutableBlockPos();

      for(int i = 0; i < 8; ++i) {
         double xo = player.getX() + (double)(((float)((i >> 0) % 2) - 0.5F) * player.getBbWidth() * 0.8F);
         double yo = player.getEyeY() + (double)(((float)((i >> 1) % 2) - 0.5F) * 0.1F * player.getScale());
         double zo = player.getZ() + (double)(((float)((i >> 2) % 2) - 0.5F) * player.getBbWidth() * 0.8F);
         testPos.set(xo, yo, zo);
         BlockState blockState = player.level().getBlockState(testPos);
         if (blockState.getRenderShape() != RenderShape.INVISIBLE && blockState.isViewBlocking(player.level(), testPos)) {
            return blockState;
         }
      }

      return null;
   }

   private static void renderTex(final TextureAtlasSprite sprite, final PoseStack poseStack, final MultiBufferSource bufferSource) {
      float br = 0.1F;
      int color = ARGB.colorFromFloat(1.0F, 0.1F, 0.1F, 0.1F);
      float x0 = -1.0F;
      float x1 = 1.0F;
      float y0 = -1.0F;
      float y1 = 1.0F;
      float z0 = -0.5F;
      float u0 = sprite.getU0();
      float u1 = sprite.getU1();
      float v0 = sprite.getV0();
      float v1 = sprite.getV1();
      Matrix4f pose = poseStack.last().pose();
      VertexConsumer builder = bufferSource.getBuffer(RenderTypes.blockScreenEffect(sprite.atlasLocation()));
      builder.addVertex((Matrix4fc)pose, -1.0F, -1.0F, -0.5F).setUv(u1, v1).setColor(color);
      builder.addVertex((Matrix4fc)pose, 1.0F, -1.0F, -0.5F).setUv(u0, v1).setColor(color);
      builder.addVertex((Matrix4fc)pose, 1.0F, 1.0F, -0.5F).setUv(u0, v0).setColor(color);
      builder.addVertex((Matrix4fc)pose, -1.0F, 1.0F, -0.5F).setUv(u1, v0).setColor(color);
   }

   private static void renderWater(final Minecraft minecraft, final PoseStack poseStack, final MultiBufferSource bufferSource) {
      BlockPos pos = BlockPos.containing(minecraft.player.getX(), minecraft.player.getEyeY(), minecraft.player.getZ());
      float br = Lightmap.getBrightness(minecraft.player.level().dimensionType(), minecraft.player.level().getMaxLocalRawBrightness(pos));
      int color = ARGB.colorFromFloat(0.1F, br, br, br);
      float size = 4.0F;
      float x0 = -1.0F;
      float x1 = 1.0F;
      float y0 = -1.0F;
      float y1 = 1.0F;
      float z0 = -0.5F;
      float uo = -minecraft.player.getYRot() / 64.0F;
      float vo = minecraft.player.getXRot() / 64.0F;
      Matrix4f pose = poseStack.last().pose();
      VertexConsumer builder = bufferSource.getBuffer(RenderTypes.blockScreenEffect(UNDERWATER_LOCATION));
      builder.addVertex((Matrix4fc)pose, -1.0F, -1.0F, -0.5F).setUv(4.0F + uo, 4.0F + vo).setColor(color);
      builder.addVertex((Matrix4fc)pose, 1.0F, -1.0F, -0.5F).setUv(0.0F + uo, 4.0F + vo).setColor(color);
      builder.addVertex((Matrix4fc)pose, 1.0F, 1.0F, -0.5F).setUv(0.0F + uo, 0.0F + vo).setColor(color);
      builder.addVertex((Matrix4fc)pose, -1.0F, 1.0F, -0.5F).setUv(4.0F + uo, 0.0F + vo).setColor(color);
   }

   private static void renderFire(final PoseStack poseStack, final MultiBufferSource bufferSource, final TextureAtlasSprite sprite) {
      VertexConsumer builder = bufferSource.getBuffer(RenderTypes.fireScreenEffect(sprite.atlasLocation()));
      float u0 = sprite.getU0();
      float u1 = sprite.getU1();
      float v0 = sprite.getV0();
      float v1 = sprite.getV1();
      float size = 1.0F;

      for(int i = 0; i < 2; ++i) {
         poseStack.pushPose();
         float x0 = -0.5F;
         float x1 = 0.5F;
         float y0 = -0.5F;
         float y1 = 0.5F;
         float z0 = -0.5F;
         poseStack.translate((float)(-(i * 2 - 1)) * 0.24F, -0.3F, 0.0F);
         poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees((float)(i * 2 - 1) * 10.0F));
         Matrix4f pose = poseStack.last().pose();
         builder.addVertex((Matrix4fc)pose, -0.5F, -0.5F, -0.5F).setUv(u1, v1).setColor(1.0F, 1.0F, 1.0F, 0.9F);
         builder.addVertex((Matrix4fc)pose, 0.5F, -0.5F, -0.5F).setUv(u0, v1).setColor(1.0F, 1.0F, 1.0F, 0.9F);
         builder.addVertex((Matrix4fc)pose, 0.5F, 0.5F, -0.5F).setUv(u0, v0).setColor(1.0F, 1.0F, 1.0F, 0.9F);
         builder.addVertex((Matrix4fc)pose, -0.5F, 0.5F, -0.5F).setUv(u1, v0).setColor(1.0F, 1.0F, 1.0F, 0.9F);
         poseStack.popPose();
      }

   }
}

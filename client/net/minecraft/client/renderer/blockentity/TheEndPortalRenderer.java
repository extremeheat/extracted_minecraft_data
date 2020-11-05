package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;

public class TheEndPortalRenderer<T extends TheEndPortalBlockEntity> implements BlockEntityRenderer<T> {
   public static final ResourceLocation END_SKY_LOCATION = new ResourceLocation("textures/environment/end_sky.png");
   public static final ResourceLocation END_PORTAL_LOCATION = new ResourceLocation("textures/entity/end_portal.png");
   private static final Random RANDOM = new Random(31100L);
   private static final List<RenderType> RENDER_TYPES = (List)IntStream.range(0, 16).mapToObj((var0) -> {
      return RenderType.endPortal(var0 + 1);
   }).collect(ImmutableList.toImmutableList());
   private final BlockEntityRenderDispatcher renderer;

   public TheEndPortalRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.renderer = var1.getBlockEntityRenderDispatcher();
   }

   public void render(T var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      RANDOM.setSeed(31100L);
      double var7 = var1.getBlockPos().distSqr(this.renderer.camera.getPosition(), true);
      int var9 = this.getPasses(var7);
      float var10 = this.getOffset();
      Matrix4f var11 = var3.last().pose();
      this.renderCube(var1, var10, 0.15F, var11, var4.getBuffer((RenderType)RENDER_TYPES.get(0)));

      for(int var12 = 1; var12 < var9; ++var12) {
         this.renderCube(var1, var10, 2.0F / (float)(18 - var12), var11, var4.getBuffer((RenderType)RENDER_TYPES.get(var12)));
      }

   }

   private void renderCube(T var1, float var2, float var3, Matrix4f var4, VertexConsumer var5) {
      float var6 = (RANDOM.nextFloat() * 0.5F + 0.1F) * var3;
      float var7 = (RANDOM.nextFloat() * 0.5F + 0.4F) * var3;
      float var8 = (RANDOM.nextFloat() * 0.5F + 0.5F) * var3;
      this.renderFace(var1, var4, var5, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, var6, var7, var8, Direction.SOUTH);
      this.renderFace(var1, var4, var5, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, var6, var7, var8, Direction.NORTH);
      this.renderFace(var1, var4, var5, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, var6, var7, var8, Direction.EAST);
      this.renderFace(var1, var4, var5, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, var6, var7, var8, Direction.WEST);
      this.renderFace(var1, var4, var5, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var6, var7, var8, Direction.DOWN);
      this.renderFace(var1, var4, var5, 0.0F, 1.0F, var2, var2, 1.0F, 1.0F, 0.0F, 0.0F, var6, var7, var8, Direction.UP);
   }

   private void renderFace(T var1, Matrix4f var2, VertexConsumer var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, Direction var15) {
      if (var1.shouldRenderFace(var15)) {
         var3.vertex(var2, var4, var6, var8).color(var12, var13, var14, 1.0F).endVertex();
         var3.vertex(var2, var5, var6, var9).color(var12, var13, var14, 1.0F).endVertex();
         var3.vertex(var2, var5, var7, var10).color(var12, var13, var14, 1.0F).endVertex();
         var3.vertex(var2, var4, var7, var11).color(var12, var13, var14, 1.0F).endVertex();
      }

   }

   protected int getPasses(double var1) {
      if (var1 > 36864.0D) {
         return 1;
      } else if (var1 > 25600.0D) {
         return 3;
      } else if (var1 > 16384.0D) {
         return 5;
      } else if (var1 > 9216.0D) {
         return 7;
      } else if (var1 > 4096.0D) {
         return 9;
      } else if (var1 > 1024.0D) {
         return 11;
      } else if (var1 > 576.0D) {
         return 13;
      } else {
         return var1 > 256.0D ? 14 : 15;
      }
   }

   protected float getOffset() {
      return 0.75F;
   }
}

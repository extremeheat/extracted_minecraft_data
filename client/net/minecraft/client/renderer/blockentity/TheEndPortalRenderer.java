package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import org.joml.Matrix4f;

public class TheEndPortalRenderer<T extends TheEndPortalBlockEntity> implements BlockEntityRenderer<T> {
   public static final ResourceLocation END_SKY_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/end_sky.png");
   public static final ResourceLocation END_PORTAL_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/end_portal.png");

   public TheEndPortalRenderer(BlockEntityRendererProvider.Context var1) {
      super();
   }

   public void render(T var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      Matrix4f var7 = var3.last().pose();
      this.renderCube(var1, var7, var4.getBuffer(this.renderType()));
   }

   private void renderCube(T var1, Matrix4f var2, VertexConsumer var3) {
      float var4 = this.getOffsetDown();
      float var5 = this.getOffsetUp();
      this.renderFace(var1, var2, var3, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH);
      this.renderFace(var1, var2, var3, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH);
      this.renderFace(var1, var2, var3, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST);
      this.renderFace(var1, var2, var3, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST);
      this.renderFace(var1, var2, var3, 0.0F, 1.0F, var4, var4, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN);
      this.renderFace(var1, var2, var3, 0.0F, 1.0F, var5, var5, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP);
   }

   private void renderFace(T var1, Matrix4f var2, VertexConsumer var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, Direction var12) {
      if (var1.shouldRenderFace(var12)) {
         var3.addVertex(var2, var4, var6, var8);
         var3.addVertex(var2, var5, var6, var9);
         var3.addVertex(var2, var5, var7, var10);
         var3.addVertex(var2, var4, var7, var11);
      }

   }

   protected float getOffsetUp() {
      return 0.75F;
   }

   protected float getOffsetDown() {
      return 0.375F;
   }

   protected RenderType renderType() {
      return RenderType.endPortal();
   }
}

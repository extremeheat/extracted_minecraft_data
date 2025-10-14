package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.EndPortalRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public abstract class AbstractEndPortalRenderer<T extends TheEndPortalBlockEntity, S extends EndPortalRenderState> implements BlockEntityRenderer<T, S> {
   public static final ResourceLocation END_SKY_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/end_sky.png");
   public static final ResourceLocation END_PORTAL_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/end_portal.png");

   public AbstractEndPortalRenderer() {
      super();
   }

   public void extractRenderState(T var1, S var2, float var3, Vec3 var4, @Nullable ModelFeatureRenderer.CrumblingOverlay var5) {
      BlockEntityRenderer.super.extractRenderState(var1, var2, var3, var4, var5);
      var2.facesToShow.clear();

      for(Direction var9 : Direction.values()) {
         if (var1.shouldRenderFace(var9)) {
            var2.facesToShow.add(var9);
         }
      }

   }

   public void submit(S var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      var3.submitCustomGeometry(var2, this.renderType(), (var2x, var3x) -> this.renderCube(var1.facesToShow, var2x.pose(), var3x));
   }

   private void renderCube(EnumSet<Direction> var1, Matrix4f var2, VertexConsumer var3) {
      float var4 = this.getOffsetDown();
      float var5 = this.getOffsetUp();
      this.renderFace(var1, var2, var3, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH);
      this.renderFace(var1, var2, var3, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH);
      this.renderFace(var1, var2, var3, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST);
      this.renderFace(var1, var2, var3, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST);
      this.renderFace(var1, var2, var3, 0.0F, 1.0F, var4, var4, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN);
      this.renderFace(var1, var2, var3, 0.0F, 1.0F, var5, var5, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP);
   }

   private void renderFace(EnumSet<Direction> var1, Matrix4f var2, VertexConsumer var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, Direction var12) {
      if (var1.contains(var12)) {
         var3.addVertex((Matrix4fc)var2, var4, var6, var8);
         var3.addVertex((Matrix4fc)var2, var5, var6, var9);
         var3.addVertex((Matrix4fc)var2, var5, var7, var10);
         var3.addVertex((Matrix4fc)var2, var4, var7, var11);
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

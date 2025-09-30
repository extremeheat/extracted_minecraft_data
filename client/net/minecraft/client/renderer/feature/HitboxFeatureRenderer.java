package net.minecraft.client.renderer.feature;

import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.state.HitboxRenderState;
import net.minecraft.client.renderer.entity.state.HitboxesRenderState;
import net.minecraft.client.renderer.entity.state.ServerHitboxesRenderState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

public class HitboxFeatureRenderer {
   public HitboxFeatureRenderer() {
      super();
   }

   public void render(SubmitNodeCollection var1, MultiBufferSource.BufferSource var2) {
      for(SubmitNodeStorage.HitboxSubmit var4 : var1.getHitboxSubmits()) {
         VertexConsumer var5 = var2.getBuffer(RenderType.lines());
         PoseStack var6 = new PoseStack();
         var6.mulPose((Matrix4fc)var4.pose());
         renderHitboxesAndViewVector(var6, var4.hitboxesRenderState(), var5, var4.entityRenderState().eyeHeight);
         ServerHitboxesRenderState var7 = var4.entityRenderState().serverHitboxesRenderState;
         if (var7 != null) {
            if (var7.missing()) {
               HitboxRenderState var8 = (HitboxRenderState)var4.hitboxesRenderState().hitboxes().getFirst();
               DebugRenderer.renderFloatingText(var6, var2, "Missing", var4.entityRenderState().x, var8.y1() + 1.5, var4.entityRenderState().z, -65536);
            } else if (var7.hitboxes() != null) {
               var6.translate(var7.serverEntityX() - var4.entityRenderState().x, var7.serverEntityY() - var4.entityRenderState().y, var7.serverEntityZ() - var4.entityRenderState().z);
               renderHitboxesAndViewVector(var6, var7.hitboxes(), var5, var7.eyeHeight());
               Vec3 var9 = new Vec3(var7.deltaMovementX(), var7.deltaMovementY(), var7.deltaMovementZ());
               ShapeRenderer.renderVector(var6, var5, new Vector3f(), var9, -256);
            }
         }
      }

   }

   private static void renderHitboxesAndViewVector(PoseStack var0, HitboxesRenderState var1, VertexConsumer var2, float var3) {
      UnmodifiableIterator var4 = var1.hitboxes().iterator();

      while(var4.hasNext()) {
         HitboxRenderState var5 = (HitboxRenderState)var4.next();
         renderHitbox(var0, var2, var5);
      }

      Vec3 var6 = new Vec3(var1.viewX(), var1.viewY(), var1.viewZ());
      ShapeRenderer.renderVector(var0, var2, new Vector3f(0.0F, var3, 0.0F), var6.scale(2.0), -16776961);
   }

   private static void renderHitbox(PoseStack var0, VertexConsumer var1, HitboxRenderState var2) {
      var0.pushPose();
      var0.translate(var2.offsetX(), var2.offsetY(), var2.offsetZ());
      ShapeRenderer.renderLineBox(var0.last(), var1, var2.x0(), var2.y0(), var2.z0(), var2.x1(), var2.y1(), var2.z1(), var2.red(), var2.green(), var2.blue(), 1.0F);
      var0.popPose();
   }
}

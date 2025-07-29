package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class SpawnerRenderer implements BlockEntityRenderer<SpawnerBlockEntity> {
   private final EntityRenderDispatcher entityRenderer;

   public SpawnerRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.entityRenderer = var1.entityRenderer();
   }

   public void render(SpawnerBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, Vec3 var7) {
      Level var8 = var1.getLevel();
      if (var8 != null) {
         BaseSpawner var9 = var1.getSpawner();
         Entity var10 = var9.getOrCreateDisplayEntity(var8, var1.getBlockPos());
         if (var10 != null) {
            renderEntityInSpawner(var2, var3, var4, var10, this.entityRenderer, var9.getoSpin(), var9.getSpin());
         }

      }
   }

   public static void renderEntityInSpawner(float var0, PoseStack var1, MultiBufferSource var2, Entity var3, EntityRenderDispatcher var4, double var5, double var7) {
      var1.pushPose();
      var1.translate(0.5F, 0.0F, 0.5F);
      float var9 = 0.53125F;
      float var10 = Math.max(var3.getBbWidth(), var3.getBbHeight());
      if ((double)var10 > 1.0) {
         var9 /= var10;
      }

      var1.translate(0.0F, 0.4F, 0.0F);
      var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees((float)Mth.lerp((double)var0, var5, var7) * 10.0F));
      var1.translate(0.0F, -0.2F, 0.0F);
      var1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-30.0F));
      var1.scale(var9, var9, var9);
      EntityRenderState var11 = var4.extractEntity(var3, var0);
      var4.submit(var11, 0.0, 0.0, 0.0, var1, Minecraft.getInstance().gameRenderer.getSubmitNodeStorage());
      var1.popPose();
   }
}

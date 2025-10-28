package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.SpawnerRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

public class SpawnerRenderer implements BlockEntityRenderer<SpawnerBlockEntity, SpawnerRenderState> {
   private final EntityRenderDispatcher entityRenderer;

   public SpawnerRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.entityRenderer = var1.entityRenderer();
   }

   public SpawnerRenderState createRenderState() {
      return new SpawnerRenderState();
   }

   public void extractRenderState(SpawnerBlockEntity var1, SpawnerRenderState var2, float var3, Vec3 var4, ModelFeatureRenderer.@Nullable CrumblingOverlay var5) {
      BlockEntityRenderer.super.extractRenderState(var1, var2, var3, var4, var5);
      if (var1.getLevel() != null) {
         BaseSpawner var6 = var1.getSpawner();
         Entity var7 = var6.getOrCreateDisplayEntity(var1.getLevel(), var1.getBlockPos());
         TrialSpawnerRenderer.extractSpawnerData(var2, var3, var7, this.entityRenderer, var6.getOSpin(), var6.getSpin());
      }
   }

   public void submit(SpawnerRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      if (var1.displayEntity != null) {
         submitEntityInSpawner(var2, var3, var1.displayEntity, this.entityRenderer, var1.spin, var1.scale, var4);
      }

   }

   public static void submitEntityInSpawner(PoseStack var0, SubmitNodeCollector var1, EntityRenderState var2, EntityRenderDispatcher var3, float var4, float var5, CameraRenderState var6) {
      var0.pushPose();
      var0.translate(0.5F, 0.4F, 0.5F);
      var0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var4));
      var0.translate(0.0F, -0.2F, 0.0F);
      var0.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-30.0F));
      var0.scale(var5, var5, var5);
      var3.submit(var2, var6, 0.0, 0.0, 0.0, var0, var1);
      var0.popPose();
   }

   // $FF: synthetic method
   public BlockEntityRenderState createRenderState() {
      return this.createRenderState();
   }
}

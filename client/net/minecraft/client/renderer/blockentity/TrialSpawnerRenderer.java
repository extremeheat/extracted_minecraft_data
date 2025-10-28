package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.SpawnerRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerStateData;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class TrialSpawnerRenderer implements BlockEntityRenderer<TrialSpawnerBlockEntity, SpawnerRenderState> {
   private final EntityRenderDispatcher entityRenderer;

   public TrialSpawnerRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.entityRenderer = var1.entityRenderer();
   }

   public SpawnerRenderState createRenderState() {
      return new SpawnerRenderState();
   }

   public void extractRenderState(TrialSpawnerBlockEntity var1, SpawnerRenderState var2, float var3, Vec3 var4, ModelFeatureRenderer.@Nullable CrumblingOverlay var5) {
      BlockEntityRenderer.super.extractRenderState(var1, var2, var3, var4, var5);
      if (var1.getLevel() != null) {
         TrialSpawner var6 = var1.getTrialSpawner();
         TrialSpawnerStateData var7 = var6.getStateData();
         Entity var8 = var7.getOrCreateDisplayEntity(var6, var1.getLevel(), var6.getState());
         extractSpawnerData(var2, var3, var8, this.entityRenderer, var7.getOSpin(), var7.getSpin());
      }
   }

   static void extractSpawnerData(SpawnerRenderState var0, float var1, @Nullable Entity var2, EntityRenderDispatcher var3, double var4, double var6) {
      if (var2 != null) {
         var0.displayEntity = var3.extractEntity(var2, var1);
         var0.displayEntity.lightCoords = var0.lightCoords;
         var0.spin = (float)Mth.lerp((double)var1, var4, var6) * 10.0F;
         var0.scale = 0.53125F;
         float var8 = Math.max(var2.getBbWidth(), var2.getBbHeight());
         if ((double)var8 > 1.0) {
            var0.scale /= var8;
         }

      }
   }

   public void submit(SpawnerRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      if (var1.displayEntity != null) {
         SpawnerRenderer.submitEntityInSpawner(var2, var3, var1.displayEntity, this.entityRenderer, var1.spin, var1.scale, var4);
      }

   }

   // $FF: synthetic method
   public BlockEntityRenderState createRenderState() {
      return this.createRenderState();
   }
}

package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerStateData;
import net.minecraft.world.phys.Vec3;

public class TrialSpawnerRenderer implements BlockEntityRenderer<TrialSpawnerBlockEntity> {
   private final EntityRenderDispatcher entityRenderer;

   public TrialSpawnerRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.entityRenderer = var1.entityRenderer();
   }

   public void submit(TrialSpawnerBlockEntity var1, float var2, PoseStack var3, int var4, int var5, Vec3 var6, @Nullable ModelFeatureRenderer.CrumblingOverlay var7, SubmitNodeCollector var8) {
      Level var9 = var1.getLevel();
      if (var9 != null) {
         TrialSpawner var10 = var1.getTrialSpawner();
         TrialSpawnerStateData var11 = var10.getStateData();
         Entity var12 = var11.getOrCreateDisplayEntity(var10, var9, var10.getState());
         if (var12 != null) {
            SpawnerRenderer.submitEntityInSpawner(var2, var3, var8, var12, this.entityRenderer, var11.getOSpin(), var11.getSpin());
         }

      }
   }
}

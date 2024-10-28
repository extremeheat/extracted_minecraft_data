package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerData;

public class TrialSpawnerRenderer implements BlockEntityRenderer<TrialSpawnerBlockEntity> {
   private final EntityRenderDispatcher entityRenderer;

   public TrialSpawnerRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.entityRenderer = var1.getEntityRenderer();
   }

   public void render(TrialSpawnerBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      Level var7 = var1.getLevel();
      if (var7 != null) {
         TrialSpawner var8 = var1.getTrialSpawner();
         TrialSpawnerData var9 = var8.getData();
         Entity var10 = var9.getOrCreateDisplayEntity(var8, var7, var8.getState());
         if (var10 != null) {
            SpawnerRenderer.renderEntityInSpawner(var2, var3, var4, var5, var10, this.entityRenderer, var9.getOSpin(), var9.getSpin());
         }

      }
   }
}

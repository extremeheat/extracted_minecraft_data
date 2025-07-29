package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
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

   public void render(TrialSpawnerBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, Vec3 var7) {
      Level var8 = var1.getLevel();
      if (var8 != null) {
         TrialSpawner var9 = var1.getTrialSpawner();
         TrialSpawnerStateData var10 = var9.getStateData();
         Entity var11 = var10.getOrCreateDisplayEntity(var9, var8, var9.getState());
         if (var11 != null) {
            SpawnerRenderer.renderEntityInSpawner(var2, var3, var4, var11, this.entityRenderer, var10.getOSpin(), var10.getSpin());
         }

      }
   }
}

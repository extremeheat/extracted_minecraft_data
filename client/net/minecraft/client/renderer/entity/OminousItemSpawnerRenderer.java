package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.OminousItemSpawner;
import net.minecraft.world.item.ItemStack;

public class OminousItemSpawnerRenderer extends EntityRenderer<OminousItemSpawner, ItemClusterRenderState> {
   private static final float ROTATION_SPEED = 40.0F;
   private static final int TICKS_SCALING = 50;
   private final ItemModelResolver itemModelResolver;
   private final RandomSource random = RandomSource.create();

   protected OminousItemSpawnerRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.itemModelResolver = var1.getItemModelResolver();
   }

   public ItemClusterRenderState createRenderState() {
      return new ItemClusterRenderState();
   }

   public void extractRenderState(OminousItemSpawner var1, ItemClusterRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      ItemStack var4 = var1.getItem();
      var2.extractItemGroupRenderState(var1, var4, this.itemModelResolver);
   }

   public void render(ItemClusterRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      if (!var1.item.isEmpty()) {
         var2.pushPose();
         float var5;
         if (var1.ageInTicks <= 50.0F) {
            var5 = Math.min(var1.ageInTicks, 50.0F) / 50.0F;
            var2.scale(var5, var5, var5);
         }

         var5 = Mth.wrapDegrees(var1.ageInTicks * 40.0F);
         var2.mulPose(Axis.YP.rotationDegrees(var5));
         ItemEntityRenderer.renderMultipleFromCount(var2, var3, 15728880, var1, this.random);
         var2.popPose();
      }
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}

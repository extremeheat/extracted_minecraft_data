package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultClientData;

public class VaultRenderer implements BlockEntityRenderer<VaultBlockEntity> {
   private final ItemModelResolver itemModelResolver;
   private final RandomSource random = RandomSource.create();
   private final ItemClusterRenderState renderState = new ItemClusterRenderState();

   public VaultRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.itemModelResolver = var1.getItemModelResolver();
   }

   public void render(VaultBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      if (VaultBlockEntity.Client.shouldDisplayActiveEffects(var1.getSharedData())) {
         Level var7 = var1.getLevel();
         if (var7 != null) {
            ItemStack var8 = var1.getSharedData().getDisplayItem();
            if (!var8.isEmpty()) {
               this.itemModelResolver.updateForTopItem(this.renderState.item, var8, ItemDisplayContext.GROUND, false, var7, (LivingEntity)null, 0);
               this.renderState.count = ItemClusterRenderState.getRenderedAmount(var8.getCount());
               this.renderState.seed = ItemClusterRenderState.getSeedForItemStack(var8);
               VaultClientData var9 = var1.getClientData();
               var3.pushPose();
               var3.translate(0.5F, 0.4F, 0.5F);
               var3.mulPose(Axis.YP.rotationDegrees(Mth.rotLerp(var2, var9.previousSpin(), var9.currentSpin())));
               ItemEntityRenderer.renderMultipleFromCount(var3, var4, var5, this.renderState, this.random);
               var3.popPose();
            }
         }
      }
   }
}

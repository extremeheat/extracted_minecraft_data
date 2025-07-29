package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultClientData;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class VaultRenderer implements BlockEntityRenderer<VaultBlockEntity> {
   private final ItemModelResolver itemModelResolver;
   private final RandomSource random = RandomSource.create();
   private final ItemClusterRenderState renderState = new ItemClusterRenderState();

   public VaultRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.itemModelResolver = var1.itemModelResolver();
   }

   public void render(VaultBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, Vec3 var7) {
      if (VaultBlockEntity.Client.shouldDisplayActiveEffects(var1.getSharedData())) {
         Level var8 = var1.getLevel();
         if (var8 != null) {
            ItemStack var9 = var1.getSharedData().getDisplayItem();
            if (!var9.isEmpty()) {
               this.itemModelResolver.updateForTopItem(this.renderState.item, var9, ItemDisplayContext.GROUND, var8, (ItemOwner)null, 0);
               this.renderState.count = ItemClusterRenderState.getRenderedAmount(var9.getCount());
               this.renderState.seed = ItemClusterRenderState.getSeedForItemStack(var9);
               VaultClientData var10 = var1.getClientData();
               var3.pushPose();
               var3.translate(0.5F, 0.4F, 0.5F);
               var3.mulPose((Quaternionfc)Axis.YP.rotationDegrees(Mth.rotLerp(var2, var10.previousSpin(), var10.currentSpin())));
               ItemEntityRenderer.renderMultipleFromCount(var3, var4, var5, this.renderState, this.random);
               var3.popPose();
            }
         }
      }
   }
}

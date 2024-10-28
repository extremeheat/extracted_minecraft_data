package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultClientData;

public class VaultRenderer implements BlockEntityRenderer<VaultBlockEntity> {
   private final ItemRenderer itemRenderer;
   private final RandomSource random = RandomSource.create();

   public VaultRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.itemRenderer = var1.getItemRenderer();
   }

   public void render(VaultBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      if (VaultBlockEntity.Client.shouldDisplayActiveEffects(var1.getSharedData())) {
         Level var7 = var1.getLevel();
         if (var7 != null) {
            ItemStack var8 = var1.getSharedData().getDisplayItem();
            if (!var8.isEmpty()) {
               this.random.setSeed((long)ItemEntityRenderer.getSeedForItemStack(var8));
               VaultClientData var9 = var1.getClientData();
               renderItemInside(var2, var7, var3, var4, var5, var8, this.itemRenderer, var9.previousSpin(), var9.currentSpin(), this.random);
            }
         }
      }
   }

   public static void renderItemInside(float var0, Level var1, PoseStack var2, MultiBufferSource var3, int var4, ItemStack var5, ItemRenderer var6, float var7, float var8, RandomSource var9) {
      var2.pushPose();
      var2.translate(0.5F, 0.4F, 0.5F);
      var2.mulPose(Axis.YP.rotationDegrees(Mth.rotLerp(var0, var7, var8)));
      ItemEntityRenderer.renderMultipleFromCount(var6, var2, var3, var4, var5, var9, var1);
      var2.popPose();
   }
}

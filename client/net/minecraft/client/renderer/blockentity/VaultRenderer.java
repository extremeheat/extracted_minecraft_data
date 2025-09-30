package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.VaultRenderState;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultClientData;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class VaultRenderer implements BlockEntityRenderer<VaultBlockEntity, VaultRenderState> {
   private final ItemModelResolver itemModelResolver;
   private final RandomSource random = RandomSource.create();

   public VaultRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.itemModelResolver = var1.itemModelResolver();
   }

   public VaultRenderState createRenderState() {
      return new VaultRenderState();
   }

   public void extractRenderState(VaultBlockEntity var1, VaultRenderState var2, float var3, Vec3 var4, @Nullable ModelFeatureRenderer.CrumblingOverlay var5) {
      BlockEntityRenderer.super.extractRenderState(var1, var2, var3, var4, var5);
      ItemStack var6 = var1.getSharedData().getDisplayItem();
      if (VaultBlockEntity.Client.shouldDisplayActiveEffects(var1.getSharedData()) && !var6.isEmpty() && var1.getLevel() != null) {
         var2.displayItem = new ItemClusterRenderState();
         this.itemModelResolver.updateForTopItem(var2.displayItem.item, var6, ItemDisplayContext.GROUND, var1.getLevel(), (ItemOwner)null, 0);
         var2.displayItem.count = ItemClusterRenderState.getRenderedAmount(var6.getCount());
         var2.displayItem.seed = ItemClusterRenderState.getSeedForItemStack(var6);
         VaultClientData var7 = var1.getClientData();
         var2.spin = Mth.rotLerp(var3, var7.previousSpin(), var7.currentSpin());
      }
   }

   public void submit(VaultRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      if (var1.displayItem != null) {
         var2.pushPose();
         var2.translate(0.5F, 0.4F, 0.5F);
         var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var1.spin));
         ItemEntityRenderer.renderMultipleFromCount(var2, var3, var1.lightCoords, var1.displayItem, this.random);
         var2.popPose();
      }
   }

   // $FF: synthetic method
   public BlockEntityRenderState createRenderState() {
      return this.createRenderState();
   }
}

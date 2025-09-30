package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.CampfireRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class CampfireRenderer implements BlockEntityRenderer<CampfireBlockEntity, CampfireRenderState> {
   private static final float SIZE = 0.375F;
   private final ItemModelResolver itemModelResolver;

   public CampfireRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.itemModelResolver = var1.itemModelResolver();
   }

   public CampfireRenderState createRenderState() {
      return new CampfireRenderState();
   }

   public void extractRenderState(CampfireBlockEntity var1, CampfireRenderState var2, float var3, Vec3 var4, @Nullable ModelFeatureRenderer.CrumblingOverlay var5) {
      BlockEntityRenderer.super.extractRenderState(var1, var2, var3, var4, var5);
      var2.facing = (Direction)var1.getBlockState().getValue(CampfireBlock.FACING);
      int var6 = (int)var1.getBlockPos().asLong();
      var2.items = new ArrayList();

      for(int var7 = 0; var7 < var1.getItems().size(); ++var7) {
         ItemStackRenderState var8 = new ItemStackRenderState();
         this.itemModelResolver.updateForTopItem(var8, (ItemStack)var1.getItems().get(var7), ItemDisplayContext.FIXED, var1.getLevel(), (ItemOwner)null, var6 + var7);
         var2.items.add(var8);
      }

   }

   public void submit(CampfireRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      Direction var5 = var1.facing;
      List var6 = var1.items;

      for(int var7 = 0; var7 < var6.size(); ++var7) {
         ItemStackRenderState var8 = (ItemStackRenderState)var6.get(var7);
         if (!var8.isEmpty()) {
            var2.pushPose();
            var2.translate(0.5F, 0.44921875F, 0.5F);
            Direction var9 = Direction.from2DDataValue((var7 + var5.get2DDataValue()) % 4);
            float var10 = -var9.toYRot();
            var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var10));
            var2.mulPose((Quaternionfc)Axis.XP.rotationDegrees(90.0F));
            var2.translate(-0.3125F, -0.3125F, 0.0F);
            var2.scale(0.375F, 0.375F, 0.375F);
            var8.submit(var2, var3, var1.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            var2.popPose();
         }
      }

   }

   // $FF: synthetic method
   public BlockEntityRenderState createRenderState() {
      return this.createRenderState();
   }
}

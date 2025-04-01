package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.MineTravellingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MineTravellingBlockEntityRenderer implements BlockEntityRenderer<MineTravellingBlockEntity> {
   private static final ResourceLocation BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/end_gateway_beam.png");
   private final BlockRenderDispatcher dispatcher;

   public MineTravellingBlockEntityRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.dispatcher = var1.getBlockRenderDispatcher();
   }

   public void render(MineTravellingBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, Vec3 var7) {
      double var8 = var1.isSpawning() ? (double)var1.getLevel().getMaxY() : 50.0;
      float var10 = var1.periodicallyBeam(var2);
      var10 = Mth.sin(var10 * 3.1415927F);
      int var11 = Mth.floor((double)var10 * var8);
      int var12 = var1.isSpawning() ? DyeColor.LIGHT_BLUE.getTextureDiffuseColor() : DyeColor.BLUE.getTextureDiffuseColor();
      long var13 = var1.getLevel().getGameTime();
      BeaconRenderer.renderBeaconBeam(var3, var4, BEAM_LOCATION, var2, var10, var13, -var11, var11 * 2, var12, 0.15F, 0.175F);
      var3.pushPose();
      double var15 = (double)((float)var1.age() + var2) * 0.1;
      var3.rotateAround(Axis.YP.rotation((float)(var15 % 6.283185307179586)), 0.5F, 0.0F, 0.5F);
      BlockState var17 = Blocks.MINE_TRAVELLING_BLOCK.defaultBlockState();
      List var18 = this.dispatcher.getBlockModel(var17).collectParts(RandomSource.create(var17.getSeed(var1.getBlockPos())));
      this.dispatcher.getModelRenderer().tesselateBlock(var1.getLevel(), var18, var17, var1.getBlockPos(), var3, var4.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(var17)), false, OverlayTexture.NO_OVERLAY);
      var3.popPose();
   }

   protected RenderType renderType() {
      return RenderType.beaconBeam(BEAM_LOCATION, false);
   }

   public int getViewDistance() {
      return 256;
   }
}

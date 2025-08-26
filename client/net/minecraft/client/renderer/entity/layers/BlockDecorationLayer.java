package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.FlowerBedBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionfc;

public class BlockDecorationLayer<S extends EntityRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
   private final Function<S, Optional<BlockState>> blockState;
   private final Consumer<PoseStack> transform;

   public BlockDecorationLayer(RenderLayerParent<S, M> var1, Function<S, Optional<BlockState>> var2, Consumer<PoseStack> var3) {
      super(var1);
      this.blockState = var2;
      this.transform = var3;
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, S var4, float var5, float var6) {
      Optional var7 = (Optional)this.blockState.apply(var4);
      if (!var7.isEmpty()) {
         BlockState var8 = (BlockState)var7.get();
         Block var9 = var8.getBlock();
         boolean var10 = var9 instanceof CopperGolemStatueBlock;
         var1.pushPose();
         this.transform.accept(var1);
         if (!var10) {
            var1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(180.0F));
         }

         if (var10 || var9 instanceof AbstractSkullBlock || var9 instanceof AbstractBannerBlock || var9 instanceof AbstractChestBlock) {
            var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F));
         }

         if (var9 instanceof FlowerBedBlock) {
            var1.translate(-0.25, -1.5, -0.25);
         } else if (!var10) {
            var1.translate(-0.5, -1.5, -0.5);
         } else {
            var1.translate(-0.5, 0.0, -0.5);
         }

         var2.submitBlock(var1, var8, var3, OverlayTexture.NO_OVERLAY, var4.outlineColor);
         var1.popPose();
      }
   }
}

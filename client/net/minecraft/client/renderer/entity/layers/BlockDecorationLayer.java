package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionfc;

public class BlockDecorationLayer<S extends EntityRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
   private final Function<S, Optional<BlockState>> blockState;
   private final BiConsumer<BlockState, PoseStack> transform;

   public BlockDecorationLayer(RenderLayerParent<S, M> var1, Function<S, Optional<BlockState>> var2, BiConsumer<BlockState, PoseStack> var3) {
      super(var1);
      this.blockState = var2;
      this.transform = var3;
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, S var4, float var5, float var6) {
      Optional var7 = (Optional)this.blockState.apply(var4);
      if (!var7.isEmpty()) {
         BlockState var8 = (BlockState)var7.get();
         var1.pushPose();
         this.transform.accept(var8, var1);
         if (!(var8.getBlock() instanceof AbstractSkullBlock) && !(var8.getBlock() instanceof AbstractBannerBlock)) {
            if (var8.getBlock() instanceof CopperGolemStatueBlock) {
               var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F));
            } else {
               var1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(180.0F));
            }
         } else {
            var1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(180.0F));
         }

         var2.submitBlock(var1, var8, var3, OverlayTexture.NO_OVERLAY, var4.outlineColor);
         var1.popPose();
      }
   }
}

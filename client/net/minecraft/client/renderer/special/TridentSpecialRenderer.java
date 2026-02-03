package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.projectile.TridentModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3fc;

public class TridentSpecialRenderer implements NoDataSpecialModelRenderer {
   private final TridentModel model;

   public TridentSpecialRenderer(final TridentModel model) {
      super();
      this.model = model;
   }

   public void submit(final ItemDisplayContext type, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final boolean hasFoil, final int outlineColor) {
      poseStack.pushPose();
      poseStack.scale(1.0F, -1.0F, -1.0F);
      submitNodeCollector.submitModelPart(this.model.root(), poseStack, this.model.renderType(TridentModel.TEXTURE), lightCoords, overlayCoords, (TextureAtlasSprite)null, false, hasFoil, -1, (ModelFeatureRenderer.CrumblingOverlay)null, outlineColor);
      poseStack.popPose();
   }

   public void getExtents(final Consumer<Vector3fc> output) {
      PoseStack poseStack = new PoseStack();
      poseStack.scale(1.0F, -1.0F, -1.0F);
      this.model.root().getExtentsForGui(poseStack, output);
   }

   public static record Unbaked() implements SpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

      public Unbaked() {
         super();
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public SpecialModelRenderer<?> bake(final SpecialModelRenderer.BakingContext context) {
         return new TridentSpecialRenderer(new TridentModel(context.entityModelSet().bakeLayer(ModelLayers.TRIDENT)));
      }
   }
}

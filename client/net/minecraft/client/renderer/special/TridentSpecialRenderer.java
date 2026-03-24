package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.projectile.TridentModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class TridentSpecialRenderer implements NoDataSpecialModelRenderer {
   public static final Transformation DEFAULT_TRANSFORMATION = new Transformation((Vector3fc)null, (Quaternionfc)null, new Vector3f(1.0F, -1.0F, -1.0F), (Quaternionfc)null);
   private final TridentModel model;

   public TridentSpecialRenderer(final TridentModel model) {
      super();
      this.model = model;
   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final boolean hasFoil, final int outlineColor) {
      submitNodeCollector.submitModelPart(this.model.root(), poseStack, this.model.renderType(TridentModel.TEXTURE), lightCoords, overlayCoords, (TextureAtlasSprite)null, false, hasFoil, -1, (ModelFeatureRenderer.CrumblingOverlay)null, outlineColor);
   }

   public void getExtents(final Consumer<Vector3fc> output) {
      PoseStack poseStack = new PoseStack();
      this.model.root().getExtentsForGui(poseStack, output);
   }

   public static record Unbaked() implements NoDataSpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

      public Unbaked() {
         super();
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public TridentSpecialRenderer bake(final SpecialModelRenderer.BakingContext context) {
         return new TridentSpecialRenderer(new TridentModel(context.entityModelSet().bakeLayer(ModelLayers.TRIDENT)));
      }
   }
}

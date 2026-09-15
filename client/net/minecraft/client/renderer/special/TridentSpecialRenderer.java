package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.projectile.TridentModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Unit;
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
      if (hasFoil) {
         submitNodeCollector.submitModel(this.model, Unit.INSTANCE, poseStack, RenderTypes.entitySolidGlint(TridentModel.TEXTURE), lightCoords, overlayCoords, outlineColor);
      } else {
         submitNodeCollector.submitModel(this.model, Unit.INSTANCE, poseStack, TridentModel.TEXTURE, lightCoords, overlayCoords, outlineColor);
      }

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

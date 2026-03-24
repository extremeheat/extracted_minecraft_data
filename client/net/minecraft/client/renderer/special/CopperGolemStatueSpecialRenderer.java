package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.statue.CopperGolemStatueModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.animal.golem.CopperGolemOxidationLevels;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import org.joml.Vector3fc;

public class CopperGolemStatueSpecialRenderer implements NoDataSpecialModelRenderer {
   private final CopperGolemStatueModel model;
   private final Identifier texture;

   public CopperGolemStatueSpecialRenderer(final CopperGolemStatueModel model, final Identifier texture) {
      super();
      this.model = model;
      this.texture = texture;
   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final boolean hasFoil, final int outlineColor) {
      submitNodeCollector.submitModel(this.model, Unit.INSTANCE, poseStack, this.texture, lightCoords, overlayCoords, outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
   }

   public void getExtents(final Consumer<Vector3fc> output) {
      PoseStack poseStack = new PoseStack();
      this.model.setupAnim(Unit.INSTANCE);
      this.model.root().getExtentsForGui(poseStack, output);
   }

   public static record Unbaked(Identifier texture, CopperGolemStatueBlock.Pose pose) implements NoDataSpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture), CopperGolemStatueBlock.Pose.CODEC.fieldOf("pose").forGetter(Unbaked::pose)).apply(i, Unbaked::new));

      public Unbaked(final WeatheringCopper.WeatherState state, final CopperGolemStatueBlock.Pose pose) {
         this(CopperGolemOxidationLevels.getOxidationLevel(state).texture(), pose);
      }

      public Unbaked {
         super();
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public CopperGolemStatueSpecialRenderer bake(final SpecialModelRenderer.BakingContext context) {
         CopperGolemStatueModel model = new CopperGolemStatueModel(context.entityModelSet().bakeLayer(getModel(this.pose)));
         return new CopperGolemStatueSpecialRenderer(model, this.texture);
      }

      private static ModelLayerLocation getModel(final CopperGolemStatueBlock.Pose pose) {
         ModelLayerLocation var10000;
         switch (pose) {
            case STANDING -> var10000 = ModelLayers.COPPER_GOLEM;
            case SITTING -> var10000 = ModelLayers.COPPER_GOLEM_SITTING;
            case STAR -> var10000 = ModelLayers.COPPER_GOLEM_STAR;
            case RUNNING -> var10000 = ModelLayers.COPPER_GOLEM_RUNNING;
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }
   }
}

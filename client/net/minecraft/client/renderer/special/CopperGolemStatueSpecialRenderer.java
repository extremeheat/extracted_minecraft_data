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
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.golem.CopperGolemOxidationLevels;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import org.joml.Vector3fc;

public class CopperGolemStatueSpecialRenderer implements NoDataSpecialModelRenderer {
   private static final Direction MODEL_STATE;
   private final CopperGolemStatueModel model;
   private final Identifier texture;

   public CopperGolemStatueSpecialRenderer(CopperGolemStatueModel var1, Identifier var2) {
      super();
      this.model = var1;
      this.texture = var2;
   }

   public void submit(ItemDisplayContext var1, PoseStack var2, SubmitNodeCollector var3, int var4, int var5, boolean var6, int var7) {
      positionModel(var2);
      var3.submitModel(this.model, Direction.SOUTH, var2, RenderTypes.entityCutoutNoCull(this.texture), var4, var5, -1, (TextureAtlasSprite)null, var7, (ModelFeatureRenderer.CrumblingOverlay)null);
   }

   public void getExtents(Consumer<Vector3fc> var1) {
      PoseStack var2 = new PoseStack();
      positionModel(var2);
      this.model.setupAnim(MODEL_STATE);
      this.model.root().getExtentsForGui(var2, var1);
   }

   private static void positionModel(PoseStack var0) {
      var0.translate(0.5F, 1.5F, 0.5F);
      var0.scale(-1.0F, -1.0F, 1.0F);
   }

   static {
      MODEL_STATE = Direction.SOUTH;
   }

   public static record Unbaked(Identifier texture, CopperGolemStatueBlock.Pose pose) implements SpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture), CopperGolemStatueBlock.Pose.CODEC.fieldOf("pose").forGetter(Unbaked::pose)).apply(var0, Unbaked::new));

      public Unbaked(WeatheringCopper.WeatherState var1, CopperGolemStatueBlock.Pose var2) {
         this(CopperGolemOxidationLevels.getOxidationLevel(var1).texture(), var2);
      }

      public Unbaked(Identifier var1, CopperGolemStatueBlock.Pose var2) {
         super();
         this.texture = var1;
         this.pose = var2;
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext var1) {
         CopperGolemStatueModel var2 = new CopperGolemStatueModel(var1.entityModelSet().bakeLayer(getModel(this.pose)));
         return new CopperGolemStatueSpecialRenderer(var2, this.texture);
      }

      private static ModelLayerLocation getModel(CopperGolemStatueBlock.Pose var0) {
         ModelLayerLocation var10000;
         switch (var0) {
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

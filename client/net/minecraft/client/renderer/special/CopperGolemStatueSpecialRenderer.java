package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.client.model.CopperGolemStatueModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.coppergolem.CopperGolemOxidationLevels;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.WeatheringCopper;
import org.joml.Vector3f;

public class CopperGolemStatueSpecialRenderer implements NoDataSpecialModelRenderer {
   private final CopperGolemStatueModel model;
   private final ResourceLocation texture;

   public CopperGolemStatueSpecialRenderer(CopperGolemStatueModel var1, ResourceLocation var2) {
      super();
      this.model = var1;
      this.texture = var2;
   }

   public void submit(ItemDisplayContext var1, PoseStack var2, SubmitNodeCollector var3, int var4, int var5, boolean var6) {
      this.positionModel(var2);
      var3.submitModel(this.model, Direction.SOUTH, var2, RenderType.entityCutoutNoCull(this.texture), var4, var5, -1, (TextureAtlasSprite)null, 0, (ModelFeatureRenderer.CrumblingOverlay)null);
   }

   public void getExtents(Set<Vector3f> var1) {
      PoseStack var2 = new PoseStack();
      this.positionModel(var2);
      this.model.root().getExtentsForGui(var2, var1);
   }

   private void positionModel(PoseStack var1) {
      var1.translate(0.5F, 1.5F, 0.5F);
      var1.scale(-1.0F, -1.0F, 1.0F);
   }

   public static record Unbaked(ResourceLocation texture) implements SpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("texture").forGetter(Unbaked::texture)).apply(var0, Unbaked::new));

      public Unbaked(WeatheringCopper.WeatherState var1) {
         this(CopperGolemOxidationLevels.getOxidationLevel(var1).texture());
      }

      public Unbaked(ResourceLocation var1) {
         super();
         this.texture = var1;
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext var1) {
         CopperGolemStatueModel var2 = new CopperGolemStatueModel(var1.entityModelSet().bakeLayer(ModelLayers.COPPER_GOLEM));
         return new CopperGolemStatueSpecialRenderer(var2, this.texture);
      }
   }
}

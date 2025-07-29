package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.client.model.CopperGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.coppergolem.CopperGolemOxidationLevels;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.WeatheringCopper;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class CopperGolemStatueSpecialRenderer implements NoDataSpecialModelRenderer {
   private final CopperGolemModel model;
   private final ResourceLocation texture;

   public CopperGolemStatueSpecialRenderer(CopperGolemModel var1, ResourceLocation var2) {
      super();
      this.model = var1;
      this.texture = var2;
   }

   public void render(ItemDisplayContext var1, PoseStack var2, MultiBufferSource var3, int var4, int var5, boolean var6) {
      VertexConsumer var7 = var3.getBuffer(RenderType.entityCutoutNoCull(this.texture));
      this.positionModel(var2);
      this.model.renderToBuffer(var2, var7, var4, OverlayTexture.NO_OVERLAY);
   }

   public void getExtents(Set<Vector3f> var1) {
      PoseStack var2 = new PoseStack();
      this.positionModel(var2);
      this.model.root().getExtentsForGui(var2, var1);
   }

   private void positionModel(PoseStack var1) {
      var1.translate(0.5F, 0.0F, 0.5F);
      var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F));
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
         CopperGolemModel var2 = new CopperGolemModel(var1.entityModelSet().bakeLayer(ModelLayers.COPPER_GOLEM));
         return new CopperGolemStatueSpecialRenderer(var2, this.texture);
      }
   }
}

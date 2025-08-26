package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;

public class TridentSpecialRenderer implements NoDataSpecialModelRenderer {
   private final TridentModel model;

   public TridentSpecialRenderer(TridentModel var1) {
      super();
      this.model = var1;
   }

   public void submit(ItemDisplayContext var1, PoseStack var2, SubmitNodeCollector var3, int var4, int var5, boolean var6) {
      var2.pushPose();
      var2.scale(1.0F, -1.0F, -1.0F);
      var3.submitModelPart(this.model.root(), var2, this.model.renderType(TridentModel.TEXTURE), var4, var5, (TextureAtlasSprite)null, false, var6, -1, (ModelFeatureRenderer.CrumblingOverlay)null);
      var2.popPose();
   }

   public void getExtents(Set<Vector3f> var1) {
      PoseStack var2 = new PoseStack();
      var2.scale(1.0F, -1.0F, -1.0F);
      this.model.root().getExtentsForGui(var2, var1);
   }

   public static record Unbaked() implements SpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

      public Unbaked() {
         super();
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext var1) {
         return new TridentSpecialRenderer(new TridentModel(var1.entityModelSet().bakeLayer(ModelLayers.TRIDENT)));
      }
   }
}

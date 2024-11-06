package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;

public class TridentSpecialRenderer implements NoDataSpecialModelRenderer {
   private final TridentModel model;

   public TridentSpecialRenderer(TridentModel var1) {
      super();
      this.model = var1;
   }

   public void render(ItemDisplayContext var1, PoseStack var2, MultiBufferSource var3, int var4, int var5, boolean var6) {
      var2.pushPose();
      var2.scale(1.0F, -1.0F, -1.0F);
      VertexConsumer var7 = ItemRenderer.getFoilBuffer(var3, this.model.renderType(TridentModel.TEXTURE), false, var6);
      this.model.renderToBuffer(var2, var7, var4, var5);
      var2.popPose();
   }

   public static record Unbaked() implements SpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

      public Unbaked() {
         super();
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public SpecialModelRenderer<?> bake(EntityModelSet var1) {
         return new TridentSpecialRenderer(new TridentModel(var1.bakeLayer(ModelLayers.TRIDENT)));
      }
   }
}

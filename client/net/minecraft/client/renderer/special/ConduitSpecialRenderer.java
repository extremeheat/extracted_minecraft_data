package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.ConduitRenderer;
import net.minecraft.world.item.ItemDisplayContext;

public class ConduitSpecialRenderer implements NoDataSpecialModelRenderer {
   private final ModelPart model;

   public ConduitSpecialRenderer(ModelPart var1) {
      super();
      this.model = var1;
   }

   public void render(ItemDisplayContext var1, PoseStack var2, MultiBufferSource var3, int var4, int var5, boolean var6) {
      VertexConsumer var7 = ConduitRenderer.SHELL_TEXTURE.buffer(var3, RenderType::entitySolid);
      var2.pushPose();
      var2.translate(0.5F, 0.5F, 0.5F);
      this.model.render(var2, var7, var4, var5);
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
         return new ConduitSpecialRenderer(var1.bakeLayer(ModelLayers.CONDUIT_SHELL));
      }
   }
}

package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ConduitRenderer;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;

public class ConduitSpecialRenderer implements NoDataSpecialModelRenderer {
   private final MaterialSet materials;
   private final ModelPart model;

   public ConduitSpecialRenderer(MaterialSet var1, ModelPart var2) {
      super();
      this.materials = var1;
      this.model = var2;
   }

   public void submit(ItemDisplayContext var1, PoseStack var2, SubmitNodeCollector var3, int var4, int var5, boolean var6) {
      var2.pushPose();
      var2.translate(0.5F, 0.5F, 0.5F);
      var3.submitModelPart(this.model, var2, ConduitRenderer.SHELL_TEXTURE.renderType(RenderType::entitySolid), var4, var5, this.materials.get(ConduitRenderer.SHELL_TEXTURE));
      var2.popPose();
   }

   public void getExtents(Set<Vector3f> var1) {
      PoseStack var2 = new PoseStack();
      var2.translate(0.5F, 0.5F, 0.5F);
      this.model.getExtentsForGui(var2, var1);
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
         return new ConduitSpecialRenderer(var1.materials(), var1.entityModelSet().bakeLayer(ModelLayers.CONDUIT_SHELL));
      }
   }
}

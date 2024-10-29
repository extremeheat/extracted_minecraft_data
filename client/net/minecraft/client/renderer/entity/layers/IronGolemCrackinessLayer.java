package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.IronGolemRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Crackiness;

public class IronGolemCrackinessLayer extends RenderLayer<IronGolemRenderState, IronGolemModel> {
   private static final Map<Crackiness.Level, ResourceLocation> resourceLocations;

   public IronGolemCrackinessLayer(RenderLayerParent<IronGolemRenderState, IronGolemModel> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, IronGolemRenderState var4, float var5, float var6) {
      if (!var4.isInvisible) {
         Crackiness.Level var7 = var4.crackiness;
         if (var7 != Crackiness.Level.NONE) {
            ResourceLocation var8 = (ResourceLocation)resourceLocations.get(var7);
            renderColoredCutoutModel(this.getParentModel(), var8, var1, var2, var3, var4, -1);
         }
      }
   }

   static {
      resourceLocations = ImmutableMap.of(Crackiness.Level.LOW, ResourceLocation.withDefaultNamespace("textures/entity/iron_golem/iron_golem_crackiness_low.png"), Crackiness.Level.MEDIUM, ResourceLocation.withDefaultNamespace("textures/entity/iron_golem/iron_golem_crackiness_medium.png"), Crackiness.Level.HIGH, ResourceLocation.withDefaultNamespace("textures/entity/iron_golem/iron_golem_crackiness_high.png"));
   }
}

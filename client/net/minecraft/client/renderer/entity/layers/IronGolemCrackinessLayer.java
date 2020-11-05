package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;

public class IronGolemCrackinessLayer extends RenderLayer<IronGolem, IronGolemModel<IronGolem>> {
   private static final Map<IronGolem.Crackiness, ResourceLocation> resourceLocations;

   public IronGolemCrackinessLayer(RenderLayerParent<IronGolem, IronGolemModel<IronGolem>> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, IronGolem var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (!var4.isInvisible()) {
         IronGolem.Crackiness var11 = var4.getCrackiness();
         if (var11 != IronGolem.Crackiness.NONE) {
            ResourceLocation var12 = (ResourceLocation)resourceLocations.get(var11);
            renderColoredCutoutModel(this.getParentModel(), var12, var1, var2, var3, var4, 1.0F, 1.0F, 1.0F);
         }
      }
   }

   static {
      resourceLocations = ImmutableMap.of(IronGolem.Crackiness.LOW, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_low.png"), IronGolem.Crackiness.MEDIUM, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_medium.png"), IronGolem.Crackiness.HIGH, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_high.png"));
   }
}

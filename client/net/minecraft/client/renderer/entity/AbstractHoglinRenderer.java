package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HoglinModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.state.HoglinRenderState;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.hoglin.HoglinBase;

public abstract class AbstractHoglinRenderer<T extends Mob & HoglinBase> extends AgeableMobRenderer<T, HoglinRenderState, HoglinModel> {
   public AbstractHoglinRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2, ModelLayerLocation var3, float var4) {
      super(var1, new HoglinModel(var1.bakeLayer(var2)), new HoglinModel(var1.bakeLayer(var3)), var4);
   }

   public HoglinRenderState createRenderState() {
      return new HoglinRenderState();
   }

   public void extractRenderState(T var1, HoglinRenderState var2, float var3) {
      super.extractRenderState((T)var1, var2, var3);
      var2.attackAnimationRemainingTicks = ((HoglinBase)var1).getAttackAnimationRemainingTicks();
   }
}

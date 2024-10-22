package net.minecraft.client.model;

import java.util.function.Function;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;

public abstract class EntityModel<T extends EntityRenderState> extends Model {
   public static final float MODEL_Y_OFFSET = -1.501F;

   protected EntityModel(ModelPart var1) {
      this(var1, RenderType::entityCutoutNoCull);
   }

   protected EntityModel(ModelPart var1, Function<ResourceLocation, RenderType> var2) {
      super(var1, var2);
   }

   public void setupAnim(T var1) {
      this.resetPose();
   }
}

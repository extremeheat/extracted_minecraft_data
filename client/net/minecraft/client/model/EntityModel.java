package net.minecraft.client.model;

import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;

public abstract class EntityModel<T extends EntityRenderState> extends Model {
   public static final float MODEL_Y_OFFSET = -1.501F;

   protected EntityModel() {
      this(RenderType::entityCutoutNoCull);
   }

   protected EntityModel(Function<ResourceLocation, RenderType> var1) {
      super(var1);
   }

   public abstract void setupAnim(T var1);
}

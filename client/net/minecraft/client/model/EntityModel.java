package net.minecraft.client.model;

import java.util.function.Function;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public abstract class EntityModel<T extends EntityRenderState> extends Model<T> {
   public static final float MODEL_Y_OFFSET = -1.501F;

   protected EntityModel(ModelPart var1) {
      this(var1, RenderTypes::entityCutoutNoCull);
   }

   protected EntityModel(ModelPart var1, Function<Identifier, RenderType> var2) {
      super(var1, var2);
   }
}

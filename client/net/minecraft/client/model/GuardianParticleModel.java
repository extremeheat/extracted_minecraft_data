package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Unit;

public class GuardianParticleModel extends Model<Unit> {
   public GuardianParticleModel(ModelPart var1) {
      super(var1, RenderType::entityCutoutNoCull);
   }
}

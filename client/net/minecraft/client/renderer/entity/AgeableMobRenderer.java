package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.Mob;

/** @deprecated */
@Deprecated
public abstract class AgeableMobRenderer<T extends Mob, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends MobRenderer<T, S, M> {
   private final M adultModel;
   private final M babyModel;

   public AgeableMobRenderer(EntityRendererProvider.Context var1, M var2, M var3, float var4) {
      super(var1, var2, var4);
      this.adultModel = var2;
      this.babyModel = var3;
   }

   public void submit(S var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      this.model = var1.isBaby ? this.babyModel : this.adultModel;
      super.submit(var1, var2, var3, var4);
   }
}

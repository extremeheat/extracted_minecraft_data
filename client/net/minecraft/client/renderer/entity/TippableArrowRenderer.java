package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.TippableArrowRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Arrow;

public class TippableArrowRenderer extends ArrowRenderer<Arrow, TippableArrowRenderState> {
   public static final ResourceLocation NORMAL_ARROW_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/projectiles/arrow.png");
   public static final ResourceLocation TIPPED_ARROW_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/projectiles/tipped_arrow.png");

   public TippableArrowRenderer(EntityRendererProvider.Context var1) {
      super(var1);
   }

   protected ResourceLocation getTextureLocation(TippableArrowRenderState var1) {
      return var1.isTipped ? TIPPED_ARROW_LOCATION : NORMAL_ARROW_LOCATION;
   }

   public TippableArrowRenderState createRenderState() {
      return new TippableArrowRenderState();
   }

   public void extractRenderState(Arrow var1, TippableArrowRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isTipped = var1.getColor() > 0;
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}

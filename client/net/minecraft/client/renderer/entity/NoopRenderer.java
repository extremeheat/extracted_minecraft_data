package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

public class NoopRenderer<T extends Entity> extends EntityRenderer<T, EntityRenderState> {
   public NoopRenderer(EntityRendererProvider.Context var1) {
      super(var1);
   }

   public EntityRenderState createRenderState() {
      return new EntityRenderState();
   }
}

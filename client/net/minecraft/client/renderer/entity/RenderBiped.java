package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.model.ModelBiped;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public class RenderBiped<T extends EntityLiving> extends RenderLiving<T> {
   private static final ResourceLocation field_177118_j = new ResourceLocation("textures/entity/steve.png");

   public RenderBiped(RenderManager var1, ModelBiped var2, float var3) {
      super(var1, var2, var3);
      this.func_177094_a(new LayerCustomHead(var2.field_78116_c));
      this.func_177094_a(new LayerElytra(this));
      this.func_177094_a(new LayerHeldItem(this));
   }

   protected ResourceLocation func_110775_a(T var1) {
      return field_177118_j;
   }
}

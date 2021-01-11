package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public class RenderBiped<T extends EntityLiving> extends RenderLiving<T> {
   private static final ResourceLocation field_177118_j = new ResourceLocation("textures/entity/steve.png");
   protected ModelBiped field_77071_a;
   protected float field_77070_b;

   public RenderBiped(RenderManager var1, ModelBiped var2, float var3) {
      this(var1, var2, var3, 1.0F);
      this.func_177094_a(new LayerHeldItem(this));
   }

   public RenderBiped(RenderManager var1, ModelBiped var2, float var3, float var4) {
      super(var1, var2, var3);
      this.field_77071_a = var2;
      this.field_77070_b = var4;
      this.func_177094_a(new LayerCustomHead(var2.field_78116_c));
   }

   protected ResourceLocation func_110775_a(T var1) {
      return field_177118_j;
   }

   public void func_82422_c() {
      GlStateManager.func_179109_b(0.0F, 0.1875F, 0.0F);
   }
}

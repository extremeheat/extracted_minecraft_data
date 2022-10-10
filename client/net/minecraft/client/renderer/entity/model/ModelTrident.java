package net.minecraft.client.renderer.entity.model;

import net.minecraft.util.ResourceLocation;

public class ModelTrident extends ModelBase {
   public static final ResourceLocation field_203080_a = new ResourceLocation("textures/entity/trident.png");
   private final ModelRenderer field_203081_b;

   public ModelTrident() {
      super();
      this.field_78090_t = 32;
      this.field_78089_u = 32;
      this.field_203081_b = new ModelRenderer(this, 0, 0);
      this.field_203081_b.func_78790_a(-0.5F, -4.0F, -0.5F, 1, 31, 1, 0.0F);
      ModelRenderer var1 = new ModelRenderer(this, 4, 0);
      var1.func_78789_a(-1.5F, 0.0F, -0.5F, 3, 2, 1);
      this.field_203081_b.func_78792_a(var1);
      ModelRenderer var2 = new ModelRenderer(this, 4, 3);
      var2.func_78789_a(-2.5F, -3.0F, -0.5F, 1, 4, 1);
      this.field_203081_b.func_78792_a(var2);
      ModelRenderer var3 = new ModelRenderer(this, 4, 3);
      var3.field_78809_i = true;
      var3.func_78789_a(1.5F, -3.0F, -0.5F, 1, 4, 1);
      this.field_203081_b.func_78792_a(var3);
   }

   public void func_203079_a() {
      this.field_203081_b.func_78785_a(0.0625F);
   }
}

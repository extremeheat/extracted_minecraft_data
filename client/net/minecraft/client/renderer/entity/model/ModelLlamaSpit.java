package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;

public class ModelLlamaSpit extends ModelBase {
   private final ModelRenderer field_191225_a;

   public ModelLlamaSpit() {
      this(0.0F);
   }

   public ModelLlamaSpit(float var1) {
      super();
      this.field_191225_a = new ModelRenderer(this);
      boolean var2 = true;
      this.field_191225_a.func_78784_a(0, 0).func_78790_a(-4.0F, 0.0F, 0.0F, 2, 2, 2, var1);
      this.field_191225_a.func_78784_a(0, 0).func_78790_a(0.0F, -4.0F, 0.0F, 2, 2, 2, var1);
      this.field_191225_a.func_78784_a(0, 0).func_78790_a(0.0F, 0.0F, -4.0F, 2, 2, 2, var1);
      this.field_191225_a.func_78784_a(0, 0).func_78790_a(0.0F, 0.0F, 0.0F, 2, 2, 2, var1);
      this.field_191225_a.func_78784_a(0, 0).func_78790_a(2.0F, 0.0F, 0.0F, 2, 2, 2, var1);
      this.field_191225_a.func_78784_a(0, 0).func_78790_a(0.0F, 2.0F, 0.0F, 2, 2, 2, var1);
      this.field_191225_a.func_78784_a(0, 0).func_78790_a(0.0F, 0.0F, 2.0F, 2, 2, 2, var1);
      this.field_191225_a.func_78793_a(0.0F, 0.0F, 0.0F);
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      this.field_191225_a.func_78785_a(var7);
   }
}

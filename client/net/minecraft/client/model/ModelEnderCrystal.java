package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class ModelEnderCrystal extends ModelBase {
   private ModelRenderer field_78230_a;
   private ModelRenderer field_78228_b = new ModelRenderer(this, "glass");
   private ModelRenderer field_78229_c;

   public ModelEnderCrystal(float var1, boolean var2) {
      super();
      this.field_78228_b.func_78784_a(0, 0).func_78789_a(-4.0F, -4.0F, -4.0F, 8, 8, 8);
      this.field_78230_a = new ModelRenderer(this, "cube");
      this.field_78230_a.func_78784_a(32, 0).func_78789_a(-4.0F, -4.0F, -4.0F, 8, 8, 8);
      if (var2) {
         this.field_78229_c = new ModelRenderer(this, "base");
         this.field_78229_c.func_78784_a(0, 16).func_78789_a(-6.0F, 0.0F, -6.0F, 12, 4, 12);
      }
   }

   @Override
   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      GL11.glPushMatrix();
      GL11.glScalef(2.0F, 2.0F, 2.0F);
      GL11.glTranslatef(0.0F, -0.5F, 0.0F);
      if (this.field_78229_c != null) {
         this.field_78229_c.func_78785_a(var7);
      }

      GL11.glRotatef(var3, 0.0F, 1.0F, 0.0F);
      GL11.glTranslatef(0.0F, 0.8F + var4, 0.0F);
      GL11.glRotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
      this.field_78228_b.func_78785_a(var7);
      float var8 = 0.875F;
      GL11.glScalef(var8, var8, var8);
      GL11.glRotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
      GL11.glRotatef(var3, 0.0F, 1.0F, 0.0F);
      this.field_78228_b.func_78785_a(var7);
      GL11.glScalef(var8, var8, var8);
      GL11.glRotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
      GL11.glRotatef(var3, 0.0F, 1.0F, 0.0F);
      this.field_78230_a.func_78785_a(var7);
      GL11.glPopMatrix();
   }
}

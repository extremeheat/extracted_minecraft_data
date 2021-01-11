package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelBoat extends ModelBase {
   public ModelRenderer[] field_78103_a = new ModelRenderer[5];

   public ModelBoat() {
      super();
      this.field_78103_a[0] = new ModelRenderer(this, 0, 8);
      this.field_78103_a[1] = new ModelRenderer(this, 0, 0);
      this.field_78103_a[2] = new ModelRenderer(this, 0, 0);
      this.field_78103_a[3] = new ModelRenderer(this, 0, 0);
      this.field_78103_a[4] = new ModelRenderer(this, 0, 0);
      byte var1 = 24;
      byte var2 = 6;
      byte var3 = 20;
      byte var4 = 4;
      this.field_78103_a[0].func_78790_a((float)(-var1 / 2), (float)(-var3 / 2 + 2), -3.0F, var1, var3 - 4, 4, 0.0F);
      this.field_78103_a[0].func_78793_a(0.0F, (float)var4, 0.0F);
      this.field_78103_a[1].func_78790_a((float)(-var1 / 2 + 2), (float)(-var2 - 1), -1.0F, var1 - 4, var2, 2, 0.0F);
      this.field_78103_a[1].func_78793_a((float)(-var1 / 2 + 1), (float)var4, 0.0F);
      this.field_78103_a[2].func_78790_a((float)(-var1 / 2 + 2), (float)(-var2 - 1), -1.0F, var1 - 4, var2, 2, 0.0F);
      this.field_78103_a[2].func_78793_a((float)(var1 / 2 - 1), (float)var4, 0.0F);
      this.field_78103_a[3].func_78790_a((float)(-var1 / 2 + 2), (float)(-var2 - 1), -1.0F, var1 - 4, var2, 2, 0.0F);
      this.field_78103_a[3].func_78793_a(0.0F, (float)var4, (float)(-var3 / 2 + 1));
      this.field_78103_a[4].func_78790_a((float)(-var1 / 2 + 2), (float)(-var2 - 1), -1.0F, var1 - 4, var2, 2, 0.0F);
      this.field_78103_a[4].func_78793_a(0.0F, (float)var4, (float)(var3 / 2 - 1));
      this.field_78103_a[0].field_78795_f = 1.5707964F;
      this.field_78103_a[1].field_78796_g = 4.712389F;
      this.field_78103_a[2].field_78796_g = 1.5707964F;
      this.field_78103_a[3].field_78796_g = 3.1415927F;
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      for(int var8 = 0; var8 < 5; ++var8) {
         this.field_78103_a[var8].func_78785_a(var7);
      }

   }
}

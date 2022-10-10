package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;

public class ModelRenderer {
   public float field_78801_a;
   public float field_78799_b;
   private int field_78803_o;
   private int field_78813_p;
   public float field_78800_c;
   public float field_78797_d;
   public float field_78798_e;
   public float field_78795_f;
   public float field_78796_g;
   public float field_78808_h;
   private boolean field_78812_q;
   private int field_78811_r;
   public boolean field_78809_i;
   public boolean field_78806_j;
   public boolean field_78807_k;
   public List<ModelBox> field_78804_l;
   public List<ModelRenderer> field_78805_m;
   public final String field_78802_n;
   private final ModelBase field_78810_s;
   public float field_82906_o;
   public float field_82908_p;
   public float field_82907_q;

   public ModelRenderer(ModelBase var1, String var2) {
      super();
      this.field_78801_a = 64.0F;
      this.field_78799_b = 32.0F;
      this.field_78806_j = true;
      this.field_78804_l = Lists.newArrayList();
      this.field_78810_s = var1;
      var1.field_78092_r.add(this);
      this.field_78802_n = var2;
      this.func_78787_b(var1.field_78090_t, var1.field_78089_u);
   }

   public ModelRenderer(ModelBase var1) {
      this(var1, (String)null);
   }

   public ModelRenderer(ModelBase var1, int var2, int var3) {
      this(var1);
      this.func_78784_a(var2, var3);
   }

   public void func_78792_a(ModelRenderer var1) {
      if (this.field_78805_m == null) {
         this.field_78805_m = Lists.newArrayList();
      }

      this.field_78805_m.add(var1);
   }

   public ModelRenderer func_78784_a(int var1, int var2) {
      this.field_78803_o = var1;
      this.field_78813_p = var2;
      return this;
   }

   public ModelRenderer func_78786_a(String var1, float var2, float var3, float var4, int var5, int var6, int var7) {
      var1 = this.field_78802_n + "." + var1;
      TextureOffset var8 = this.field_78810_s.func_78084_a(var1);
      this.func_78784_a(var8.field_78783_a, var8.field_78782_b);
      this.field_78804_l.add((new ModelBox(this, this.field_78803_o, this.field_78813_p, var2, var3, var4, var5, var6, var7, 0.0F)).func_78244_a(var1));
      return this;
   }

   public ModelRenderer func_78789_a(float var1, float var2, float var3, int var4, int var5, int var6) {
      this.field_78804_l.add(new ModelBox(this, this.field_78803_o, this.field_78813_p, var1, var2, var3, var4, var5, var6, 0.0F));
      return this;
   }

   public ModelRenderer func_178769_a(float var1, float var2, float var3, int var4, int var5, int var6, boolean var7) {
      this.field_78804_l.add(new ModelBox(this, this.field_78803_o, this.field_78813_p, var1, var2, var3, var4, var5, var6, 0.0F, var7));
      return this;
   }

   public void func_78790_a(float var1, float var2, float var3, int var4, int var5, int var6, float var7) {
      this.field_78804_l.add(new ModelBox(this, this.field_78803_o, this.field_78813_p, var1, var2, var3, var4, var5, var6, var7));
   }

   public void func_205345_a(float var1, float var2, float var3, int var4, int var5, int var6, float var7, boolean var8) {
      this.field_78804_l.add(new ModelBox(this, this.field_78803_o, this.field_78813_p, var1, var2, var3, var4, var5, var6, var7, var8));
   }

   public void func_78793_a(float var1, float var2, float var3) {
      this.field_78800_c = var1;
      this.field_78797_d = var2;
      this.field_78798_e = var3;
   }

   public void func_78785_a(float var1) {
      if (!this.field_78807_k) {
         if (this.field_78806_j) {
            if (!this.field_78812_q) {
               this.func_78788_d(var1);
            }

            GlStateManager.func_179109_b(this.field_82906_o, this.field_82908_p, this.field_82907_q);
            int var2;
            if (this.field_78795_f == 0.0F && this.field_78796_g == 0.0F && this.field_78808_h == 0.0F) {
               if (this.field_78800_c == 0.0F && this.field_78797_d == 0.0F && this.field_78798_e == 0.0F) {
                  GlStateManager.func_179148_o(this.field_78811_r);
                  if (this.field_78805_m != null) {
                     for(var2 = 0; var2 < this.field_78805_m.size(); ++var2) {
                        ((ModelRenderer)this.field_78805_m.get(var2)).func_78785_a(var1);
                     }
                  }
               } else {
                  GlStateManager.func_179109_b(this.field_78800_c * var1, this.field_78797_d * var1, this.field_78798_e * var1);
                  GlStateManager.func_179148_o(this.field_78811_r);
                  if (this.field_78805_m != null) {
                     for(var2 = 0; var2 < this.field_78805_m.size(); ++var2) {
                        ((ModelRenderer)this.field_78805_m.get(var2)).func_78785_a(var1);
                     }
                  }

                  GlStateManager.func_179109_b(-this.field_78800_c * var1, -this.field_78797_d * var1, -this.field_78798_e * var1);
               }
            } else {
               GlStateManager.func_179094_E();
               GlStateManager.func_179109_b(this.field_78800_c * var1, this.field_78797_d * var1, this.field_78798_e * var1);
               if (this.field_78808_h != 0.0F) {
                  GlStateManager.func_179114_b(this.field_78808_h * 57.295776F, 0.0F, 0.0F, 1.0F);
               }

               if (this.field_78796_g != 0.0F) {
                  GlStateManager.func_179114_b(this.field_78796_g * 57.295776F, 0.0F, 1.0F, 0.0F);
               }

               if (this.field_78795_f != 0.0F) {
                  GlStateManager.func_179114_b(this.field_78795_f * 57.295776F, 1.0F, 0.0F, 0.0F);
               }

               GlStateManager.func_179148_o(this.field_78811_r);
               if (this.field_78805_m != null) {
                  for(var2 = 0; var2 < this.field_78805_m.size(); ++var2) {
                     ((ModelRenderer)this.field_78805_m.get(var2)).func_78785_a(var1);
                  }
               }

               GlStateManager.func_179121_F();
            }

            GlStateManager.func_179109_b(-this.field_82906_o, -this.field_82908_p, -this.field_82907_q);
         }
      }
   }

   public void func_78791_b(float var1) {
      if (!this.field_78807_k) {
         if (this.field_78806_j) {
            if (!this.field_78812_q) {
               this.func_78788_d(var1);
            }

            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b(this.field_78800_c * var1, this.field_78797_d * var1, this.field_78798_e * var1);
            if (this.field_78796_g != 0.0F) {
               GlStateManager.func_179114_b(this.field_78796_g * 57.295776F, 0.0F, 1.0F, 0.0F);
            }

            if (this.field_78795_f != 0.0F) {
               GlStateManager.func_179114_b(this.field_78795_f * 57.295776F, 1.0F, 0.0F, 0.0F);
            }

            if (this.field_78808_h != 0.0F) {
               GlStateManager.func_179114_b(this.field_78808_h * 57.295776F, 0.0F, 0.0F, 1.0F);
            }

            GlStateManager.func_179148_o(this.field_78811_r);
            GlStateManager.func_179121_F();
         }
      }
   }

   public void func_78794_c(float var1) {
      if (!this.field_78807_k) {
         if (this.field_78806_j) {
            if (!this.field_78812_q) {
               this.func_78788_d(var1);
            }

            if (this.field_78795_f == 0.0F && this.field_78796_g == 0.0F && this.field_78808_h == 0.0F) {
               if (this.field_78800_c != 0.0F || this.field_78797_d != 0.0F || this.field_78798_e != 0.0F) {
                  GlStateManager.func_179109_b(this.field_78800_c * var1, this.field_78797_d * var1, this.field_78798_e * var1);
               }
            } else {
               GlStateManager.func_179109_b(this.field_78800_c * var1, this.field_78797_d * var1, this.field_78798_e * var1);
               if (this.field_78808_h != 0.0F) {
                  GlStateManager.func_179114_b(this.field_78808_h * 57.295776F, 0.0F, 0.0F, 1.0F);
               }

               if (this.field_78796_g != 0.0F) {
                  GlStateManager.func_179114_b(this.field_78796_g * 57.295776F, 0.0F, 1.0F, 0.0F);
               }

               if (this.field_78795_f != 0.0F) {
                  GlStateManager.func_179114_b(this.field_78795_f * 57.295776F, 1.0F, 0.0F, 0.0F);
               }
            }

         }
      }
   }

   private void func_78788_d(float var1) {
      this.field_78811_r = GLAllocation.func_74526_a(1);
      GlStateManager.func_187423_f(this.field_78811_r, 4864);
      BufferBuilder var2 = Tessellator.func_178181_a().func_178180_c();

      for(int var3 = 0; var3 < this.field_78804_l.size(); ++var3) {
         ((ModelBox)this.field_78804_l.get(var3)).func_178780_a(var2, var1);
      }

      GlStateManager.func_187415_K();
      this.field_78812_q = true;
   }

   public ModelRenderer func_78787_b(int var1, int var2) {
      this.field_78801_a = (float)var1;
      this.field_78799_b = (float)var2;
      return this;
   }
}

package net.minecraft.client.model;

import net.minecraft.client.renderer.WorldRenderer;

public class ModelBox {
   private PositionTextureVertex[] field_78253_h;
   private TexturedQuad[] field_78254_i;
   public final float field_78252_a;
   public final float field_78250_b;
   public final float field_78251_c;
   public final float field_78248_d;
   public final float field_78249_e;
   public final float field_78246_f;
   public String field_78247_g;

   public ModelBox(ModelRenderer var1, int var2, int var3, float var4, float var5, float var6, int var7, int var8, int var9, float var10) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var1.field_78809_i);
   }

   public ModelBox(ModelRenderer var1, int var2, int var3, float var4, float var5, float var6, int var7, int var8, int var9, float var10, boolean var11) {
      super();
      this.field_78252_a = var4;
      this.field_78250_b = var5;
      this.field_78251_c = var6;
      this.field_78248_d = var4 + (float)var7;
      this.field_78249_e = var5 + (float)var8;
      this.field_78246_f = var6 + (float)var9;
      this.field_78253_h = new PositionTextureVertex[8];
      this.field_78254_i = new TexturedQuad[6];
      float var12 = var4 + (float)var7;
      float var13 = var5 + (float)var8;
      float var14 = var6 + (float)var9;
      var4 -= var10;
      var5 -= var10;
      var6 -= var10;
      var12 += var10;
      var13 += var10;
      var14 += var10;
      if (var11) {
         float var15 = var12;
         var12 = var4;
         var4 = var15;
      }

      PositionTextureVertex var24 = new PositionTextureVertex(var4, var5, var6, 0.0F, 0.0F);
      PositionTextureVertex var16 = new PositionTextureVertex(var12, var5, var6, 0.0F, 8.0F);
      PositionTextureVertex var17 = new PositionTextureVertex(var12, var13, var6, 8.0F, 8.0F);
      PositionTextureVertex var18 = new PositionTextureVertex(var4, var13, var6, 8.0F, 0.0F);
      PositionTextureVertex var19 = new PositionTextureVertex(var4, var5, var14, 0.0F, 0.0F);
      PositionTextureVertex var20 = new PositionTextureVertex(var12, var5, var14, 0.0F, 8.0F);
      PositionTextureVertex var21 = new PositionTextureVertex(var12, var13, var14, 8.0F, 8.0F);
      PositionTextureVertex var22 = new PositionTextureVertex(var4, var13, var14, 8.0F, 0.0F);
      this.field_78253_h[0] = var24;
      this.field_78253_h[1] = var16;
      this.field_78253_h[2] = var17;
      this.field_78253_h[3] = var18;
      this.field_78253_h[4] = var19;
      this.field_78253_h[5] = var20;
      this.field_78253_h[6] = var21;
      this.field_78253_h[7] = var22;
      this.field_78254_i[0] = new TexturedQuad(new PositionTextureVertex[]{var20, var16, var17, var21}, var2 + var9 + var7, var3 + var9, var2 + var9 + var7 + var9, var3 + var9 + var8, var1.field_78801_a, var1.field_78799_b);
      this.field_78254_i[1] = new TexturedQuad(new PositionTextureVertex[]{var24, var19, var22, var18}, var2, var3 + var9, var2 + var9, var3 + var9 + var8, var1.field_78801_a, var1.field_78799_b);
      this.field_78254_i[2] = new TexturedQuad(new PositionTextureVertex[]{var20, var19, var24, var16}, var2 + var9, var3, var2 + var9 + var7, var3 + var9, var1.field_78801_a, var1.field_78799_b);
      this.field_78254_i[3] = new TexturedQuad(new PositionTextureVertex[]{var17, var18, var22, var21}, var2 + var9 + var7, var3 + var9, var2 + var9 + var7 + var7, var3, var1.field_78801_a, var1.field_78799_b);
      this.field_78254_i[4] = new TexturedQuad(new PositionTextureVertex[]{var16, var24, var18, var17}, var2 + var9, var3 + var9, var2 + var9 + var7, var3 + var9 + var8, var1.field_78801_a, var1.field_78799_b);
      this.field_78254_i[5] = new TexturedQuad(new PositionTextureVertex[]{var19, var20, var21, var22}, var2 + var9 + var7 + var9, var3 + var9, var2 + var9 + var7 + var9 + var7, var3 + var9 + var8, var1.field_78801_a, var1.field_78799_b);
      if (var11) {
         for(int var23 = 0; var23 < this.field_78254_i.length; ++var23) {
            this.field_78254_i[var23].func_78235_a();
         }
      }

   }

   public void func_178780_a(WorldRenderer var1, float var2) {
      for(int var3 = 0; var3 < this.field_78254_i.length; ++var3) {
         this.field_78254_i[var3].func_178765_a(var1, var2);
      }

   }

   public ModelBox func_78244_a(String var1) {
      this.field_78247_g = var1;
      return this;
   }
}

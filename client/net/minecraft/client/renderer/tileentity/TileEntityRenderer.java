package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class TileEntityRenderer<T extends TileEntity> {
   public static final ResourceLocation[] field_178460_a;
   protected TileEntityRendererDispatcher field_147501_a;

   public TileEntityRenderer() {
      super();
   }

   public void func_199341_a(T var1, double var2, double var4, double var6, float var8, int var9) {
      if (var1 instanceof INameable && this.field_147501_a.field_190057_j != null && var1.func_174877_v().equals(this.field_147501_a.field_190057_j.func_178782_a())) {
         this.func_190053_a(true);
         this.func_190052_a(var1, ((INameable)var1).func_145748_c_().func_150254_d(), var2, var4, var6, 12);
         this.func_190053_a(false);
      }

   }

   protected void func_190053_a(boolean var1) {
      GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
      if (var1) {
         GlStateManager.func_179090_x();
      } else {
         GlStateManager.func_179098_w();
      }

      GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
   }

   protected void func_147499_a(ResourceLocation var1) {
      TextureManager var2 = this.field_147501_a.field_147553_e;
      if (var2 != null) {
         var2.func_110577_a(var1);
      }

   }

   protected World func_178459_a() {
      return this.field_147501_a.field_147550_f;
   }

   public void func_147497_a(TileEntityRendererDispatcher var1) {
      this.field_147501_a = var1;
   }

   public FontRenderer func_147498_b() {
      return this.field_147501_a.func_147548_a();
   }

   public boolean func_188185_a(T var1) {
      return false;
   }

   protected void func_190052_a(T var1, String var2, double var3, double var5, double var7, int var9) {
      Entity var10 = this.field_147501_a.field_147551_g;
      double var11 = var1.func_145835_a(var10.field_70165_t, var10.field_70163_u, var10.field_70161_v);
      if (var11 <= (double)(var9 * var9)) {
         float var13 = this.field_147501_a.field_147562_h;
         float var14 = this.field_147501_a.field_147563_i;
         boolean var15 = false;
         GameRenderer.func_189692_a(this.func_147498_b(), var2, (float)var3 + 0.5F, (float)var5 + 1.5F, (float)var7 + 0.5F, 0, var13, var14, false, false);
      }
   }

   static {
      field_178460_a = new ResourceLocation[]{new ResourceLocation("textures/" + ModelBakery.field_207770_h.func_110623_a() + ".png"), new ResourceLocation("textures/" + ModelBakery.field_207771_i.func_110623_a() + ".png"), new ResourceLocation("textures/" + ModelBakery.field_207772_j.func_110623_a() + ".png"), new ResourceLocation("textures/" + ModelBakery.field_207773_k.func_110623_a() + ".png"), new ResourceLocation("textures/" + ModelBakery.field_207774_l.func_110623_a() + ".png"), new ResourceLocation("textures/" + ModelBakery.field_207775_m.func_110623_a() + ".png"), new ResourceLocation("textures/" + ModelBakery.field_207776_n.func_110623_a() + ".png"), new ResourceLocation("textures/" + ModelBakery.field_207777_o.func_110623_a() + ".png"), new ResourceLocation("textures/" + ModelBakery.field_207778_p.func_110623_a() + ".png"), new ResourceLocation("textures/" + ModelBakery.field_207779_q.func_110623_a() + ".png")};
   }
}

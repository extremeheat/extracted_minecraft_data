package net.minecraft.client.gui;

import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiScreenLoading extends GuiScreen {
   private static final Logger field_195189_a = LogManager.getLogger();
   private static final ResourceLocation field_195190_f = new ResourceLocation("textures/gui/title/mojang.png");
   private ResourceLocation field_195191_g;

   public GuiScreenLoading() {
      super();
   }

   protected void func_73866_w_() {
      try {
         InputStream var1 = this.field_146297_k.func_195541_I().func_195746_a().func_195761_a(ResourcePackType.CLIENT_RESOURCES, field_195190_f);
         this.field_195191_g = this.field_146297_k.func_110434_K().func_110578_a("logo", new DynamicTexture(NativeImage.func_195713_a(var1)));
      } catch (IOException var2) {
         field_195189_a.error("Unable to load logo: {}", field_195190_f, var2);
      }

   }

   public void func_146281_b() {
      this.field_146297_k.func_110434_K().func_147645_c(this.field_195191_g);
      this.field_195191_g = null;
   }

   public void func_73863_a(int var1, int var2, float var3) {
      Framebuffer var4 = new Framebuffer(this.field_146294_l, this.field_146295_m, true);
      var4.func_147610_a(false);
      this.field_146297_k.func_110434_K().func_110577_a(this.field_195191_g);
      GlStateManager.func_179140_f();
      GlStateManager.func_179106_n();
      GlStateManager.func_179097_i();
      GlStateManager.func_179098_w();
      Tessellator var5 = Tessellator.func_178181_a();
      BufferBuilder var6 = var5.func_178180_c();
      var6.func_181668_a(7, DefaultVertexFormats.field_181709_i);
      var6.func_181662_b(0.0D, (double)this.field_146297_k.field_195558_d.func_198091_l(), 0.0D).func_187315_a(0.0D, 0.0D).func_181669_b(255, 255, 255, 255).func_181675_d();
      var6.func_181662_b((double)this.field_146297_k.field_195558_d.func_198109_k(), (double)this.field_146297_k.field_195558_d.func_198091_l(), 0.0D).func_187315_a(0.0D, 0.0D).func_181669_b(255, 255, 255, 255).func_181675_d();
      var6.func_181662_b((double)this.field_146297_k.field_195558_d.func_198109_k(), 0.0D, 0.0D).func_187315_a(0.0D, 0.0D).func_181669_b(255, 255, 255, 255).func_181675_d();
      var6.func_181662_b(0.0D, 0.0D, 0.0D).func_187315_a(0.0D, 0.0D).func_181669_b(255, 255, 255, 255).func_181675_d();
      var5.func_78381_a();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      boolean var7 = true;
      boolean var8 = true;
      this.field_146297_k.func_181536_a((this.field_146297_k.field_195558_d.func_198107_o() - 256) / 2, (this.field_146297_k.field_195558_d.func_198087_p() - 256) / 2, 0, 0, 256, 256, 255, 255, 255, 255);
      GlStateManager.func_179140_f();
      GlStateManager.func_179106_n();
      var4.func_147609_e();
      var4.func_147615_c(this.field_146297_k.field_195558_d.func_198109_k(), this.field_146297_k.field_195558_d.func_198091_l());
      GlStateManager.func_179141_d();
      GlStateManager.func_179092_a(516, 0.1F);
   }
}

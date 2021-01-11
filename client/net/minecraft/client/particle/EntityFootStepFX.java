package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityFootStepFX extends EntityFX {
   private static final ResourceLocation field_110126_a = new ResourceLocation("textures/particle/footprint.png");
   private int field_70576_a;
   private int field_70578_aq;
   private TextureManager field_70577_ar;

   protected EntityFootStepFX(TextureManager var1, World var2, double var3, double var5, double var7) {
      super(var2, var3, var5, var7, 0.0D, 0.0D, 0.0D);
      this.field_70577_ar = var1;
      this.field_70159_w = this.field_70181_x = this.field_70179_y = 0.0D;
      this.field_70578_aq = 200;
   }

   public void func_180434_a(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = ((float)this.field_70576_a + var3) / (float)this.field_70578_aq;
      var9 *= var9;
      float var10 = 2.0F - var9 * 2.0F;
      if (var10 > 1.0F) {
         var10 = 1.0F;
      }

      var10 *= 0.2F;
      GlStateManager.func_179140_f();
      float var11 = 0.125F;
      float var12 = (float)(this.field_70165_t - field_70556_an);
      float var13 = (float)(this.field_70163_u - field_70554_ao);
      float var14 = (float)(this.field_70161_v - field_70555_ap);
      float var15 = this.field_70170_p.func_175724_o(new BlockPos(this));
      this.field_70577_ar.func_110577_a(field_110126_a);
      GlStateManager.func_179147_l();
      GlStateManager.func_179112_b(770, 771);
      var1.func_181668_a(7, DefaultVertexFormats.field_181709_i);
      var1.func_181662_b((double)(var12 - 0.125F), (double)var13, (double)(var14 + 0.125F)).func_181673_a(0.0D, 1.0D).func_181666_a(var15, var15, var15, var10).func_181675_d();
      var1.func_181662_b((double)(var12 + 0.125F), (double)var13, (double)(var14 + 0.125F)).func_181673_a(1.0D, 1.0D).func_181666_a(var15, var15, var15, var10).func_181675_d();
      var1.func_181662_b((double)(var12 + 0.125F), (double)var13, (double)(var14 - 0.125F)).func_181673_a(1.0D, 0.0D).func_181666_a(var15, var15, var15, var10).func_181675_d();
      var1.func_181662_b((double)(var12 - 0.125F), (double)var13, (double)(var14 - 0.125F)).func_181673_a(0.0D, 0.0D).func_181666_a(var15, var15, var15, var10).func_181675_d();
      Tessellator.func_178181_a().func_78381_a();
      GlStateManager.func_179084_k();
      GlStateManager.func_179145_e();
   }

   public void func_70071_h_() {
      ++this.field_70576_a;
      if (this.field_70576_a == this.field_70578_aq) {
         this.func_70106_y();
      }

   }

   public int func_70537_b() {
      return 3;
   }

   public static class Factory implements IParticleFactory {
      public Factory() {
         super();
      }

      public EntityFX func_178902_a(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityFootStepFX(Minecraft.func_71410_x().func_110434_K(), var2, var3, var5, var7);
      }
   }
}

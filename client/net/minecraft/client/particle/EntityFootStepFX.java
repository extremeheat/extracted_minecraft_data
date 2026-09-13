package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class EntityFootStepFX extends EntityFX {
   private static final ResourceLocation field_110126_a = new ResourceLocation("textures/particle/footprint.png");
   private int field_70576_a;
   private int field_70578_aq;
   private TextureManager field_70577_ar;

   public EntityFootStepFX(TextureManager var1, World var2, double var3, double var5, double var7) {
      super(var2, var3, var5, var7, 0.0, 0.0, 0.0);
      this.field_70577_ar = var1;
      this.field_70159_w = this.field_70181_x = this.field_70179_y = 0.0;
      this.field_70578_aq = 200;
   }

   @Override
   public void func_70539_a(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = ((float)this.field_70576_a + var2) / (float)this.field_70578_aq;
      var8 *= var8;
      float var9 = 2.0F - var8 * 2.0F;
      if (var9 > 1.0F) {
         var9 = 1.0F;
      }

      var9 *= 0.2F;
      GL11.glDisable(2896);
      float var10 = 0.125F;
      float var11 = (float)(this.field_70165_t - field_70556_an);
      float var12 = (float)(this.field_70163_u - field_70554_ao);
      float var13 = (float)(this.field_70161_v - field_70555_ap);
      float var14 = this.field_70170_p
         .func_72801_o(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v));
      this.field_70577_ar.func_110577_a(field_110126_a);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      var1.func_78382_b();
      var1.func_78369_a(var14, var14, var14, var9);
      var1.func_78374_a((double)(var11 - var10), (double)var12, (double)(var13 + var10), 0.0, 1.0);
      var1.func_78374_a((double)(var11 + var10), (double)var12, (double)(var13 + var10), 1.0, 1.0);
      var1.func_78374_a((double)(var11 + var10), (double)var12, (double)(var13 - var10), 1.0, 0.0);
      var1.func_78374_a((double)(var11 - var10), (double)var12, (double)(var13 - var10), 0.0, 0.0);
      var1.func_78381_a();
      GL11.glDisable(3042);
      GL11.glEnable(2896);
   }

   @Override
   public void func_70071_h_() {
      ++this.field_70576_a;
      if (this.field_70576_a == this.field_70578_aq) {
         this.func_70106_y();
      }
   }

   @Override
   public int func_70537_b() {
      return 3;
   }
}

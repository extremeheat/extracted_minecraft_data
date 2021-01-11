package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityPickupFX extends EntityFX {
   private Entity field_174840_a;
   private Entity field_174843_ax;
   private int field_70594_ar;
   private int field_70593_as;
   private float field_174841_aA;
   private RenderManager field_174842_aB = Minecraft.func_71410_x().func_175598_ae();

   public EntityPickupFX(World var1, Entity var2, Entity var3, float var4) {
      super(var1, var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, var2.field_70159_w, var2.field_70181_x, var2.field_70179_y);
      this.field_174840_a = var2;
      this.field_174843_ax = var3;
      this.field_70593_as = 3;
      this.field_174841_aA = var4;
   }

   public void func_180434_a(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = ((float)this.field_70594_ar + var3) / (float)this.field_70593_as;
      var9 *= var9;
      double var10 = this.field_174840_a.field_70165_t;
      double var12 = this.field_174840_a.field_70163_u;
      double var14 = this.field_174840_a.field_70161_v;
      double var16 = this.field_174843_ax.field_70142_S + (this.field_174843_ax.field_70165_t - this.field_174843_ax.field_70142_S) * (double)var3;
      double var18 = this.field_174843_ax.field_70137_T + (this.field_174843_ax.field_70163_u - this.field_174843_ax.field_70137_T) * (double)var3 + (double)this.field_174841_aA;
      double var20 = this.field_174843_ax.field_70136_U + (this.field_174843_ax.field_70161_v - this.field_174843_ax.field_70136_U) * (double)var3;
      double var22 = var10 + (var16 - var10) * (double)var9;
      double var24 = var12 + (var18 - var12) * (double)var9;
      double var26 = var14 + (var20 - var14) * (double)var9;
      int var28 = this.func_70070_b(var3);
      int var29 = var28 % 65536;
      int var30 = var28 / 65536;
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)var29 / 1.0F, (float)var30 / 1.0F);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      var22 -= field_70556_an;
      var24 -= field_70554_ao;
      var26 -= field_70555_ap;
      this.field_174842_aB.func_147940_a(this.field_174840_a, (double)((float)var22), (double)((float)var24), (double)((float)var26), this.field_174840_a.field_70177_z, var3);
   }

   public void func_70071_h_() {
      ++this.field_70594_ar;
      if (this.field_70594_ar == this.field_70593_as) {
         this.func_70106_y();
      }

   }

   public int func_70537_b() {
      return 3;
   }
}

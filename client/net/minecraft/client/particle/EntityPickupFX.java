package net.minecraft.client.particle;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class EntityPickupFX extends EntityFX {
   private Entity field_70591_a;
   private Entity field_70595_aq;
   private int field_70594_ar;
   private int field_70593_as;
   private float field_70592_at;

   public EntityPickupFX(World var1, Entity var2, Entity var3, float var4) {
      super(var1, var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, var2.field_70159_w, var2.field_70181_x, var2.field_70179_y);
      this.field_70591_a = var2;
      this.field_70595_aq = var3;
      this.field_70593_as = 3;
      this.field_70592_at = var4;
   }

   @Override
   public void func_70539_a(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = ((float)this.field_70594_ar + var2) / (float)this.field_70593_as;
      var8 *= var8;
      double var9 = this.field_70591_a.field_70165_t;
      double var11 = this.field_70591_a.field_70163_u;
      double var13 = this.field_70591_a.field_70161_v;
      double var15 = this.field_70595_aq.field_70142_S + (this.field_70595_aq.field_70165_t - this.field_70595_aq.field_70142_S) * (double)var2;
      double var17 = this.field_70595_aq.field_70137_T
         + (this.field_70595_aq.field_70163_u - this.field_70595_aq.field_70137_T) * (double)var2
         + (double)this.field_70592_at;
      double var19 = this.field_70595_aq.field_70136_U + (this.field_70595_aq.field_70161_v - this.field_70595_aq.field_70136_U) * (double)var2;
      double var21 = var9 + (var15 - var9) * (double)var8;
      double var23 = var11 + (var17 - var11) * (double)var8;
      double var25 = var13 + (var19 - var13) * (double)var8;
      int var27 = this.func_70070_b(var2);
      int var28 = var27 % 65536;
      int var29 = var27 / 65536;
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)var28 / 1.0F, (float)var29 / 1.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      var21 -= field_70556_an;
      var23 -= field_70554_ao;
      var25 -= field_70555_ap;
      RenderManager.field_78727_a
         .func_147940_a(this.field_70591_a, (double)((float)var21), (double)((float)var23), (double)((float)var25), this.field_70591_a.field_70177_z, var2);
   }

   @Override
   public void func_70071_h_() {
      ++this.field_70594_ar;
      if (this.field_70594_ar == this.field_70593_as) {
         this.func_70106_y();
      }
   }

   @Override
   public int func_70537_b() {
      return 3;
   }
}

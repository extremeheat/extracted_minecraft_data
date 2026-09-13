package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelVillager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderVillager extends RenderLiving {
   private static final ResourceLocation field_110903_f = new ResourceLocation("textures/entity/villager/villager.png");
   private static final ResourceLocation field_110904_g = new ResourceLocation("textures/entity/villager/farmer.png");
   private static final ResourceLocation field_110908_h = new ResourceLocation("textures/entity/villager/librarian.png");
   private static final ResourceLocation field_110907_k = new ResourceLocation("textures/entity/villager/priest.png");
   private static final ResourceLocation field_110905_l = new ResourceLocation("textures/entity/villager/smith.png");
   private static final ResourceLocation field_110906_m = new ResourceLocation("textures/entity/villager/butcher.png");
   protected ModelVillager field_77056_a = (ModelVillager)this.field_77045_g;

   public RenderVillager() {
      super(new ModelVillager(0.0F), 0.5F);
   }

   protected int func_77032_a(EntityVillager var1, int var2, float var3) {
      return -1;
   }

   public void func_76986_a(EntityVillager var1, double var2, double var4, double var6, float var8, float var9) {
      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityVillager var1) {
      switch(var1.func_70946_n()) {
         case 0:
            return field_110904_g;
         case 1:
            return field_110908_h;
         case 2:
            return field_110907_k;
         case 3:
            return field_110905_l;
         case 4:
            return field_110906_m;
         default:
            return field_110903_f;
      }
   }

   protected void func_77029_c(EntityVillager var1, float var2) {
      super.func_77029_c(var1, var2);
   }

   protected void func_77041_b(EntityVillager var1, float var2) {
      float var3 = 0.9375F;
      if (var1.func_70874_b() < 0) {
         var3 = (float)((double)var3 * 0.5);
         this.field_76989_e = 0.25F;
      } else {
         this.field_76989_e = 0.5F;
      }

      GL11.glScalef(var3, var3, var3);
   }
}

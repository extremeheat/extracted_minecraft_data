package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderWolf extends RenderLiving {
   private static final ResourceLocation field_110917_a = new ResourceLocation("textures/entity/wolf/wolf.png");
   private static final ResourceLocation field_110915_f = new ResourceLocation("textures/entity/wolf/wolf_tame.png");
   private static final ResourceLocation field_110916_g = new ResourceLocation("textures/entity/wolf/wolf_angry.png");
   private static final ResourceLocation field_110918_h = new ResourceLocation("textures/entity/wolf/wolf_collar.png");

   public RenderWolf(ModelBase var1, ModelBase var2, float var3) {
      super(var1, var3);
      this.func_77042_a(var2);
   }

   protected float func_77044_a(EntityWolf var1, float var2) {
      return var1.func_70920_v();
   }

   protected int func_77032_a(EntityWolf var1, int var2, float var3) {
      if (var2 == 0 && var1.func_70921_u()) {
         float var5 = var1.func_70013_c(var3) * var1.func_70915_j(var3);
         this.func_110776_a(field_110917_a);
         GL11.glColor3f(var5, var5, var5);
         return 1;
      } else if (var2 == 1 && var1.func_70909_n()) {
         this.func_110776_a(field_110918_h);
         int var4 = var1.func_82186_bH();
         GL11.glColor3f(EntitySheep.field_70898_d[var4][0], EntitySheep.field_70898_d[var4][1], EntitySheep.field_70898_d[var4][2]);
         return 1;
      } else {
         return -1;
      }
   }

   protected ResourceLocation func_110775_a(EntityWolf var1) {
      if (var1.func_70909_n()) {
         return field_110915_f;
      } else {
         return var1.func_70919_bu() ? field_110916_g : field_110917_a;
      }
   }
}

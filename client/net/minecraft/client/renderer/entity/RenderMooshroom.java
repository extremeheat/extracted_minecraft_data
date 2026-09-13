package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderMooshroom extends RenderLiving {
   private static final ResourceLocation field_110880_a = new ResourceLocation("textures/entity/cow/mooshroom.png");

   public RenderMooshroom(ModelBase var1, float var2) {
      super(var1, var2);
   }

   public void func_76986_a(EntityMooshroom var1, double var2, double var4, double var6, float var8, float var9) {
      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityMooshroom var1) {
      return field_110880_a;
   }

   protected void func_77029_c(EntityMooshroom var1, float var2) {
      super.func_77029_c(var1, var2);
      if (!var1.func_70631_g_()) {
         this.func_110776_a(TextureMap.field_110575_b);
         GL11.glEnable(2884);
         GL11.glPushMatrix();
         GL11.glScalef(1.0F, -1.0F, 1.0F);
         GL11.glTranslatef(0.2F, 0.4F, 0.5F);
         GL11.glRotatef(42.0F, 0.0F, 1.0F, 0.0F);
         this.field_147909_c.func_147800_a(Blocks.field_150337_Q, 0, 1.0F);
         GL11.glTranslatef(0.1F, 0.0F, -0.6F);
         GL11.glRotatef(42.0F, 0.0F, 1.0F, 0.0F);
         this.field_147909_c.func_147800_a(Blocks.field_150337_Q, 0, 1.0F);
         GL11.glPopMatrix();
         GL11.glPushMatrix();
         ((ModelQuadruped)this.field_77045_g).field_78150_a.func_78794_c(0.0625F);
         GL11.glScalef(1.0F, -1.0F, 1.0F);
         GL11.glTranslatef(0.0F, 0.75F, -0.2F);
         GL11.glRotatef(12.0F, 0.0F, 1.0F, 0.0F);
         this.field_147909_c.func_147800_a(Blocks.field_150337_Q, 0, 1.0F);
         GL11.glPopMatrix();
         GL11.glDisable(2884);
      }
   }
}

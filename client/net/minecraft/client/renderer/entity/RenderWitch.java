package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderWitch extends RenderLiving {
   private static final ResourceLocation field_110910_a = new ResourceLocation("textures/entity/witch.png");
   private final ModelWitch field_82414_a = (ModelWitch)this.field_77045_g;

   public RenderWitch() {
      super(new ModelWitch(0.0F), 0.5F);
   }

   public void func_76986_a(EntityWitch var1, double var2, double var4, double var6, float var8, float var9) {
      ItemStack var10 = var1.func_70694_bm();
      this.field_82414_a.field_82900_g = var10 != null;
      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityWitch var1) {
      return field_110910_a;
   }

   protected void func_77029_c(EntityWitch var1, float var2) {
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      super.func_77029_c(var1, var2);
      ItemStack var3 = var1.func_70694_bm();
      if (var3 != null) {
         GL11.glPushMatrix();
         if (this.field_77045_g.field_78091_s) {
            float var4 = 0.5F;
            GL11.glTranslatef(0.0F, 0.625F, 0.0F);
            GL11.glRotatef(-20.0F, -1.0F, 0.0F, 0.0F);
            GL11.glScalef(var4, var4, var4);
         }

         this.field_82414_a.field_82898_f.func_78794_c(0.0625F);
         GL11.glTranslatef(-0.0625F, 0.53125F, 0.21875F);
         if (var3.func_77973_b() instanceof ItemBlock && RenderBlocks.func_147739_a(Block.func_149634_a(var3.func_77973_b()).func_149645_b())) {
            float var8 = 0.5F;
            GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
            var8 *= 0.75F;
            GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(var8, -var8, var8);
         } else if (var3.func_77973_b() == Items.field_151031_f) {
            float var5 = 0.625F;
            GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
            GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(var5, -var5, var5);
            GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
         } else if (var3.func_77973_b().func_77662_d()) {
            float var6 = 0.625F;
            if (var3.func_77973_b().func_77629_n_()) {
               GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
               GL11.glTranslatef(0.0F, -0.125F, 0.0F);
            }

            this.func_82410_b();
            GL11.glScalef(var6, -var6, var6);
            GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
         } else {
            float var7 = 0.375F;
            GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
            GL11.glScalef(var7, var7, var7);
            GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
         }

         GL11.glRotatef(-15.0F, 1.0F, 0.0F, 0.0F);
         GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
         this.field_76990_c.field_78721_f.func_78443_a(var1, var3, 0);
         if (var3.func_77973_b().func_77623_v()) {
            this.field_76990_c.field_78721_f.func_78443_a(var1, var3, 1);
         }

         GL11.glPopMatrix();
      }
   }

   protected void func_82410_b() {
      GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
   }

   protected void func_77041_b(EntityWitch var1, float var2) {
      float var3 = 0.9375F;
      GL11.glScalef(var3, var3, var3);
   }
}

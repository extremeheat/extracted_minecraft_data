package net.minecraft.client.gui;

import java.util.Random;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnchantmentNameParts;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

public class GuiEnchantment extends GuiContainer {
   private static final ResourceLocation field_147078_C = new ResourceLocation("textures/gui/container/enchanting_table.png");
   private static final ResourceLocation field_147070_D = new ResourceLocation("textures/entity/enchanting_table_book.png");
   private static final ModelBook field_147072_E = new ModelBook();
   private Random field_147074_F = new Random();
   private ContainerEnchantment field_147075_G = (ContainerEnchantment)this.field_147002_h;
   public int field_147073_u;
   public float field_147071_v;
   public float field_147069_w;
   public float field_147082_x;
   public float field_147081_y;
   public float field_147080_z;
   public float field_147076_A;
   ItemStack field_147077_B;
   private String field_147079_H;

   public GuiEnchantment(InventoryPlayer var1, World var2, int var3, int var4, int var5, String var6) {
      super(new ContainerEnchantment(var1, var2, var3, var4, var5));
      this.field_147079_H = var6;
   }

   @Override
   protected void func_146979_b(int var1, int var2) {
      this.field_146289_q.func_78276_b(this.field_147079_H == null ? I18n.func_135052_a("container.enchant") : this.field_147079_H, 12, 5, 4210752);
      this.field_146289_q.func_78276_b(I18n.func_135052_a("container.inventory"), 8, this.field_147000_g - 96 + 2, 4210752);
   }

   @Override
   public void func_73876_c() {
      super.func_73876_c();
      this.func_147068_g();
   }

   @Override
   protected void func_73864_a(int var1, int var2, int var3) {
      super.func_73864_a(var1, var2, var3);
      int var4 = (this.field_146294_l - this.field_146999_f) / 2;
      int var5 = (this.field_146295_m - this.field_147000_g) / 2;

      for(int var6 = 0; var6 < 3; ++var6) {
         int var7 = var1 - (var4 + 60);
         int var8 = var2 - (var5 + 14 + 19 * var6);
         if (var7 >= 0 && var8 >= 0 && var7 < 108 && var8 < 19 && this.field_147075_G.func_75140_a(this.field_146297_k.field_71439_g, var6)) {
            this.field_146297_k.field_71442_b.func_78756_a(this.field_147075_G.field_75152_c, var6);
         }
      }
   }

   @Override
   protected void func_146976_a(float var1, int var2, int var3) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_147078_C);
      int var4 = (this.field_146294_l - this.field_146999_f) / 2;
      int var5 = (this.field_146295_m - this.field_147000_g) / 2;
      this.func_73729_b(var4, var5, 0, 0, this.field_146999_f, this.field_147000_g);
      GL11.glPushMatrix();
      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();
      ScaledResolution var6 = new ScaledResolution(this.field_146297_k, this.field_146297_k.field_71443_c, this.field_146297_k.field_71440_d);
      GL11.glViewport(
         (var6.func_78326_a() - 320) / 2 * var6.func_78325_e(),
         (var6.func_78328_b() - 240) / 2 * var6.func_78325_e(),
         320 * var6.func_78325_e(),
         240 * var6.func_78325_e()
      );
      GL11.glTranslatef(-0.34F, 0.23F, 0.0F);
      Project.gluPerspective(90.0F, 1.3333334F, 9.0F, 80.0F);
      float var7 = 1.0F;
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      RenderHelper.func_74519_b();
      GL11.glTranslatef(0.0F, 3.3F, -16.0F);
      GL11.glScalef(var7, var7, var7);
      float var8 = 5.0F;
      GL11.glScalef(var8, var8, var8);
      GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_147070_D);
      GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
      float var9 = this.field_147076_A + (this.field_147080_z - this.field_147076_A) * var1;
      GL11.glTranslatef((1.0F - var9) * 0.2F, (1.0F - var9) * 0.1F, (1.0F - var9) * 0.25F);
      GL11.glRotatef(-(1.0F - var9) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
      float var10 = this.field_147069_w + (this.field_147071_v - this.field_147069_w) * var1 + 0.25F;
      float var11 = this.field_147069_w + (this.field_147071_v - this.field_147069_w) * var1 + 0.75F;
      var10 = (var10 - (float)MathHelper.func_76140_b((double)var10)) * 1.6F - 0.3F;
      var11 = (var11 - (float)MathHelper.func_76140_b((double)var11)) * 1.6F - 0.3F;
      if (var10 < 0.0F) {
         var10 = 0.0F;
      }

      if (var11 < 0.0F) {
         var11 = 0.0F;
      }

      if (var10 > 1.0F) {
         var10 = 1.0F;
      }

      if (var11 > 1.0F) {
         var11 = 1.0F;
      }

      GL11.glEnable(32826);
      field_147072_E.func_78088_a(null, 0.0F, var10, var11, var9, 0.0F, 0.0625F);
      GL11.glDisable(32826);
      RenderHelper.func_74518_a();
      GL11.glMatrixMode(5889);
      GL11.glViewport(0, 0, this.field_146297_k.field_71443_c, this.field_146297_k.field_71440_d);
      GL11.glPopMatrix();
      GL11.glMatrixMode(5888);
      GL11.glPopMatrix();
      RenderHelper.func_74518_a();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      EnchantmentNameParts.field_148338_a.func_148335_a(this.field_147075_G.field_75166_f);

      for(int var12 = 0; var12 < 3; ++var12) {
         String var13 = EnchantmentNameParts.field_148338_a.func_148334_a();
         this.field_73735_i = 0.0F;
         this.field_146297_k.func_110434_K().func_110577_a(field_147078_C);
         int var14 = this.field_147075_G.field_75167_g[var12];
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         if (var14 == 0) {
            this.func_73729_b(var4 + 60, var5 + 14 + 19 * var12, 0, 185, 108, 19);
         } else {
            String var15 = "" + var14;
            FontRenderer var16 = this.field_146297_k.field_71464_q;
            int var17 = 6839882;
            if (this.field_146297_k.field_71439_g.field_71068_ca < var14 && !this.field_146297_k.field_71439_g.field_71075_bZ.field_75098_d) {
               this.func_73729_b(var4 + 60, var5 + 14 + 19 * var12, 0, 185, 108, 19);
               var16.func_78279_b(var13, var4 + 62, var5 + 16 + 19 * var12, 104, (var17 & 16711422) >> 1);
               var16 = this.field_146297_k.field_71466_p;
               var17 = 4226832;
               var16.func_78261_a(var15, var4 + 62 + 104 - var16.func_78256_a(var15), var5 + 16 + 19 * var12 + 7, var17);
            } else {
               int var18 = var2 - (var4 + 60);
               int var19 = var3 - (var5 + 14 + 19 * var12);
               if (var18 >= 0 && var19 >= 0 && var18 < 108 && var19 < 19) {
                  this.func_73729_b(var4 + 60, var5 + 14 + 19 * var12, 0, 204, 108, 19);
                  var17 = 16777088;
               } else {
                  this.func_73729_b(var4 + 60, var5 + 14 + 19 * var12, 0, 166, 108, 19);
               }

               var16.func_78279_b(var13, var4 + 62, var5 + 16 + 19 * var12, 104, var17);
               var16 = this.field_146297_k.field_71466_p;
               var17 = 8453920;
               var16.func_78261_a(var15, var4 + 62 + 104 - var16.func_78256_a(var15), var5 + 16 + 19 * var12 + 7, var17);
            }
         }
      }
   }

   public void func_147068_g() {
      ItemStack var1 = this.field_147002_h.func_75139_a(0).func_75211_c();
      if (!ItemStack.func_77989_b(var1, this.field_147077_B)) {
         this.field_147077_B = var1;

         do {
            this.field_147082_x += (float)(this.field_147074_F.nextInt(4) - this.field_147074_F.nextInt(4));
         } while(this.field_147071_v <= this.field_147082_x + 1.0F && this.field_147071_v >= this.field_147082_x - 1.0F);
      }

      ++this.field_147073_u;
      this.field_147069_w = this.field_147071_v;
      this.field_147076_A = this.field_147080_z;
      boolean var2 = false;

      for(int var3 = 0; var3 < 3; ++var3) {
         if (this.field_147075_G.field_75167_g[var3] != 0) {
            var2 = true;
         }
      }

      if (var2) {
         this.field_147080_z += 0.2F;
      } else {
         this.field_147080_z -= 0.2F;
      }

      if (this.field_147080_z < 0.0F) {
         this.field_147080_z = 0.0F;
      }

      if (this.field_147080_z > 1.0F) {
         this.field_147080_z = 1.0F;
      }

      float var5 = (this.field_147082_x - this.field_147071_v) * 0.4F;
      float var4 = 0.2F;
      if (var5 < -var4) {
         var5 = -var4;
      }

      if (var5 > var4) {
         var5 = var4;
      }

      this.field_147081_y += (var5 - this.field_147081_y) * 0.9F;
      this.field_147071_v += this.field_147081_y;
   }
}

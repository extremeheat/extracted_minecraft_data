package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnchantmentNameParts;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import org.lwjgl.util.glu.Project;

public class GuiEnchantment extends GuiContainer {
   private static final ResourceLocation field_147078_C = new ResourceLocation("textures/gui/container/enchanting_table.png");
   private static final ResourceLocation field_147070_D = new ResourceLocation("textures/entity/enchanting_table_book.png");
   private static final ModelBook field_147072_E = new ModelBook();
   private final InventoryPlayer field_175379_F;
   private Random field_147074_F = new Random();
   private ContainerEnchantment field_147075_G;
   public int field_147073_u;
   public float field_147071_v;
   public float field_147069_w;
   public float field_147082_x;
   public float field_147081_y;
   public float field_147080_z;
   public float field_147076_A;
   ItemStack field_147077_B;
   private final IWorldNameable field_175380_I;

   public GuiEnchantment(InventoryPlayer var1, World var2, IWorldNameable var3) {
      super(new ContainerEnchantment(var1, var2));
      this.field_175379_F = var1;
      this.field_147075_G = (ContainerEnchantment)this.field_147002_h;
      this.field_175380_I = var3;
   }

   protected void func_146979_b(int var1, int var2) {
      this.field_146289_q.func_78276_b(this.field_175380_I.func_145748_c_().func_150260_c(), 12, 5, 4210752);
      this.field_146289_q.func_78276_b(this.field_175379_F.func_145748_c_().func_150260_c(), 8, this.field_147000_g - 96 + 2, 4210752);
   }

   public void func_73876_c() {
      super.func_73876_c();
      this.func_147068_g();
   }

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

   protected void func_146976_a(float var1, int var2, int var3) {
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_147078_C);
      int var4 = (this.field_146294_l - this.field_146999_f) / 2;
      int var5 = (this.field_146295_m - this.field_147000_g) / 2;
      this.func_73729_b(var4, var5, 0, 0, this.field_146999_f, this.field_147000_g);
      GlStateManager.func_179094_E();
      GlStateManager.func_179128_n(5889);
      GlStateManager.func_179094_E();
      GlStateManager.func_179096_D();
      ScaledResolution var6 = new ScaledResolution(this.field_146297_k);
      GlStateManager.func_179083_b((var6.func_78326_a() - 320) / 2 * var6.func_78325_e(), (var6.func_78328_b() - 240) / 2 * var6.func_78325_e(), 320 * var6.func_78325_e(), 240 * var6.func_78325_e());
      GlStateManager.func_179109_b(-0.34F, 0.23F, 0.0F);
      Project.gluPerspective(90.0F, 1.3333334F, 9.0F, 80.0F);
      float var7 = 1.0F;
      GlStateManager.func_179128_n(5888);
      GlStateManager.func_179096_D();
      RenderHelper.func_74519_b();
      GlStateManager.func_179109_b(0.0F, 3.3F, -16.0F);
      GlStateManager.func_179152_a(var7, var7, var7);
      float var8 = 5.0F;
      GlStateManager.func_179152_a(var8, var8, var8);
      GlStateManager.func_179114_b(180.0F, 0.0F, 0.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_147070_D);
      GlStateManager.func_179114_b(20.0F, 1.0F, 0.0F, 0.0F);
      float var9 = this.field_147076_A + (this.field_147080_z - this.field_147076_A) * var1;
      GlStateManager.func_179109_b((1.0F - var9) * 0.2F, (1.0F - var9) * 0.1F, (1.0F - var9) * 0.25F);
      GlStateManager.func_179114_b(-(1.0F - var9) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(180.0F, 1.0F, 0.0F, 0.0F);
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

      GlStateManager.func_179091_B();
      field_147072_E.func_78088_a((Entity)null, 0.0F, var10, var11, var9, 0.0F, 0.0625F);
      GlStateManager.func_179101_C();
      RenderHelper.func_74518_a();
      GlStateManager.func_179128_n(5889);
      GlStateManager.func_179083_b(0, 0, this.field_146297_k.field_71443_c, this.field_146297_k.field_71440_d);
      GlStateManager.func_179121_F();
      GlStateManager.func_179128_n(5888);
      GlStateManager.func_179121_F();
      RenderHelper.func_74518_a();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      EnchantmentNameParts.func_178176_a().func_148335_a((long)this.field_147075_G.field_178149_f);
      int var12 = this.field_147075_G.func_178147_e();

      for(int var13 = 0; var13 < 3; ++var13) {
         int var14 = var4 + 60;
         int var15 = var14 + 20;
         byte var16 = 86;
         String var17 = EnchantmentNameParts.func_178176_a().func_148334_a();
         this.field_73735_i = 0.0F;
         this.field_146297_k.func_110434_K().func_110577_a(field_147078_C);
         int var18 = this.field_147075_G.field_75167_g[var13];
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         if (var18 == 0) {
            this.func_73729_b(var14, var5 + 14 + 19 * var13, 0, 185, 108, 19);
         } else {
            String var19 = "" + var18;
            FontRenderer var20 = this.field_146297_k.field_71464_q;
            int var21 = 6839882;
            if ((var12 < var13 + 1 || this.field_146297_k.field_71439_g.field_71068_ca < var18) && !this.field_146297_k.field_71439_g.field_71075_bZ.field_75098_d) {
               this.func_73729_b(var14, var5 + 14 + 19 * var13, 0, 185, 108, 19);
               this.func_73729_b(var14 + 1, var5 + 15 + 19 * var13, 16 * var13, 239, 16, 16);
               var20.func_78279_b(var17, var15, var5 + 16 + 19 * var13, var16, (var21 & 16711422) >> 1);
               var21 = 4226832;
            } else {
               int var22 = var2 - (var4 + 60);
               int var23 = var3 - (var5 + 14 + 19 * var13);
               if (var22 >= 0 && var23 >= 0 && var22 < 108 && var23 < 19) {
                  this.func_73729_b(var14, var5 + 14 + 19 * var13, 0, 204, 108, 19);
                  var21 = 16777088;
               } else {
                  this.func_73729_b(var14, var5 + 14 + 19 * var13, 0, 166, 108, 19);
               }

               this.func_73729_b(var14 + 1, var5 + 15 + 19 * var13, 16 * var13, 223, 16, 16);
               var20.func_78279_b(var17, var15, var5 + 16 + 19 * var13, var16, var21);
               var21 = 8453920;
            }

            var20 = this.field_146297_k.field_71466_p;
            var20.func_175063_a(var19, (float)(var15 + 86 - var20.func_78256_a(var19)), (float)(var5 + 16 + 19 * var13 + 7), var21);
         }
      }

   }

   public void func_73863_a(int var1, int var2, float var3) {
      super.func_73863_a(var1, var2, var3);
      boolean var4 = this.field_146297_k.field_71439_g.field_71075_bZ.field_75098_d;
      int var5 = this.field_147075_G.func_178147_e();

      for(int var6 = 0; var6 < 3; ++var6) {
         int var7 = this.field_147075_G.field_75167_g[var6];
         int var8 = this.field_147075_G.field_178151_h[var6];
         int var9 = var6 + 1;
         if (this.func_146978_c(60, 14 + 19 * var6, 108, 17, var1, var2) && var7 > 0 && var8 >= 0) {
            ArrayList var10 = Lists.newArrayList();
            String var11;
            if (var8 >= 0 && Enchantment.func_180306_c(var8 & 255) != null) {
               var11 = Enchantment.func_180306_c(var8 & 255).func_77316_c((var8 & '\uff00') >> 8);
               var10.add(EnumChatFormatting.WHITE.toString() + EnumChatFormatting.ITALIC.toString() + I18n.func_135052_a("container.enchant.clue", var11));
            }

            if (!var4) {
               if (var8 >= 0) {
                  var10.add("");
               }

               if (this.field_146297_k.field_71439_g.field_71068_ca < var7) {
                  var10.add(EnumChatFormatting.RED.toString() + "Level Requirement: " + this.field_147075_G.field_75167_g[var6]);
               } else {
                  var11 = "";
                  if (var9 == 1) {
                     var11 = I18n.func_135052_a("container.enchant.lapis.one");
                  } else {
                     var11 = I18n.func_135052_a("container.enchant.lapis.many", var9);
                  }

                  if (var5 >= var9) {
                     var10.add(EnumChatFormatting.GRAY.toString() + "" + var11);
                  } else {
                     var10.add(EnumChatFormatting.RED.toString() + "" + var11);
                  }

                  if (var9 == 1) {
                     var11 = I18n.func_135052_a("container.enchant.level.one");
                  } else {
                     var11 = I18n.func_135052_a("container.enchant.level.many", var9);
                  }

                  var10.add(EnumChatFormatting.GRAY.toString() + "" + var11);
               }
            }

            this.func_146283_a(var10, var1, var2);
            break;
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

      this.field_147080_z = MathHelper.func_76131_a(this.field_147080_z, 0.0F, 1.0F);
      float var5 = (this.field_147082_x - this.field_147071_v) * 0.4F;
      float var4 = 0.2F;
      var5 = MathHelper.func_76131_a(var5, -var4, var4);
      this.field_147081_y += (var5 - this.field_147081_y) * 0.9F;
      this.field_147071_v += this.field_147081_y;
   }
}

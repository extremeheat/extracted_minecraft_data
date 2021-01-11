package net.minecraft.client.gui.achievement;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class GuiAchievements extends GuiScreen implements IProgressMeter {
   private static final int field_146572_y;
   private static final int field_146571_z;
   private static final int field_146559_A;
   private static final int field_146560_B;
   private static final ResourceLocation field_146561_C;
   protected GuiScreen field_146562_a;
   protected int field_146555_f = 256;
   protected int field_146557_g = 202;
   protected int field_146563_h;
   protected int field_146564_i;
   protected float field_146570_r = 1.0F;
   protected double field_146569_s;
   protected double field_146568_t;
   protected double field_146567_u;
   protected double field_146566_v;
   protected double field_146565_w;
   protected double field_146573_x;
   private int field_146554_D;
   private StatFileWriter field_146556_E;
   private boolean field_146558_F = true;

   public GuiAchievements(GuiScreen var1, StatFileWriter var2) {
      super();
      this.field_146562_a = var1;
      this.field_146556_E = var2;
      short var3 = 141;
      short var4 = 141;
      this.field_146569_s = this.field_146567_u = this.field_146565_w = (double)(AchievementList.field_76004_f.field_75993_a * 24 - var3 / 2 - 12);
      this.field_146568_t = this.field_146566_v = this.field_146573_x = (double)(AchievementList.field_76004_f.field_75991_b * 24 - var4 / 2);
   }

   public void func_73866_w_() {
      this.field_146297_k.func_147114_u().func_147297_a(new C16PacketClientStatus(C16PacketClientStatus.EnumState.REQUEST_STATS));
      this.field_146292_n.clear();
      this.field_146292_n.add(new GuiOptionButton(1, this.field_146294_l / 2 + 24, this.field_146295_m / 2 + 74, 80, 20, I18n.func_135052_a("gui.done")));
   }

   protected void func_146284_a(GuiButton var1) {
      if (!this.field_146558_F) {
         if (var1.field_146127_k == 1) {
            this.field_146297_k.func_147108_a(this.field_146562_a);
         }

      }
   }

   protected void func_73869_a(char var1, int var2) {
      if (var2 == this.field_146297_k.field_71474_y.field_151445_Q.func_151463_i()) {
         this.field_146297_k.func_147108_a((GuiScreen)null);
         this.field_146297_k.func_71381_h();
      } else {
         super.func_73869_a(var1, var2);
      }

   }

   public void func_73863_a(int var1, int var2, float var3) {
      if (this.field_146558_F) {
         this.func_146276_q_();
         this.func_73732_a(this.field_146289_q, I18n.func_135052_a("multiplayer.downloadingStats"), this.field_146294_l / 2, this.field_146295_m / 2, 16777215);
         this.func_73732_a(this.field_146289_q, field_146510_b_[(int)(Minecraft.func_71386_F() / 150L % (long)field_146510_b_.length)], this.field_146294_l / 2, this.field_146295_m / 2 + this.field_146289_q.field_78288_b * 2, 16777215);
      } else {
         int var4;
         if (Mouse.isButtonDown(0)) {
            var4 = (this.field_146294_l - this.field_146555_f) / 2;
            int var5 = (this.field_146295_m - this.field_146557_g) / 2;
            int var6 = var4 + 8;
            int var7 = var5 + 17;
            if ((this.field_146554_D == 0 || this.field_146554_D == 1) && var1 >= var6 && var1 < var6 + 224 && var2 >= var7 && var2 < var7 + 155) {
               if (this.field_146554_D == 0) {
                  this.field_146554_D = 1;
               } else {
                  this.field_146567_u -= (double)((float)(var1 - this.field_146563_h) * this.field_146570_r);
                  this.field_146566_v -= (double)((float)(var2 - this.field_146564_i) * this.field_146570_r);
                  this.field_146565_w = this.field_146569_s = this.field_146567_u;
                  this.field_146573_x = this.field_146568_t = this.field_146566_v;
               }

               this.field_146563_h = var1;
               this.field_146564_i = var2;
            }
         } else {
            this.field_146554_D = 0;
         }

         var4 = Mouse.getDWheel();
         float var11 = this.field_146570_r;
         if (var4 < 0) {
            this.field_146570_r += 0.25F;
         } else if (var4 > 0) {
            this.field_146570_r -= 0.25F;
         }

         this.field_146570_r = MathHelper.func_76131_a(this.field_146570_r, 1.0F, 2.0F);
         if (this.field_146570_r != var11) {
            float var10000 = var11 - this.field_146570_r;
            float var12 = var11 * (float)this.field_146555_f;
            float var8 = var11 * (float)this.field_146557_g;
            float var9 = this.field_146570_r * (float)this.field_146555_f;
            float var10 = this.field_146570_r * (float)this.field_146557_g;
            this.field_146567_u -= (double)((var9 - var12) * 0.5F);
            this.field_146566_v -= (double)((var10 - var8) * 0.5F);
            this.field_146565_w = this.field_146569_s = this.field_146567_u;
            this.field_146573_x = this.field_146568_t = this.field_146566_v;
         }

         if (this.field_146565_w < (double)field_146572_y) {
            this.field_146565_w = (double)field_146572_y;
         }

         if (this.field_146573_x < (double)field_146571_z) {
            this.field_146573_x = (double)field_146571_z;
         }

         if (this.field_146565_w >= (double)field_146559_A) {
            this.field_146565_w = (double)(field_146559_A - 1);
         }

         if (this.field_146573_x >= (double)field_146560_B) {
            this.field_146573_x = (double)(field_146560_B - 1);
         }

         this.func_146276_q_();
         this.func_146552_b(var1, var2, var3);
         GlStateManager.func_179140_f();
         GlStateManager.func_179097_i();
         this.func_146553_h();
         GlStateManager.func_179145_e();
         GlStateManager.func_179126_j();
      }

   }

   public void func_146509_g() {
      if (this.field_146558_F) {
         this.field_146558_F = false;
      }

   }

   public void func_73876_c() {
      if (!this.field_146558_F) {
         this.field_146569_s = this.field_146567_u;
         this.field_146568_t = this.field_146566_v;
         double var1 = this.field_146565_w - this.field_146567_u;
         double var3 = this.field_146573_x - this.field_146566_v;
         if (var1 * var1 + var3 * var3 < 4.0D) {
            this.field_146567_u += var1;
            this.field_146566_v += var3;
         } else {
            this.field_146567_u += var1 * 0.85D;
            this.field_146566_v += var3 * 0.85D;
         }

      }
   }

   protected void func_146553_h() {
      int var1 = (this.field_146294_l - this.field_146555_f) / 2;
      int var2 = (this.field_146295_m - this.field_146557_g) / 2;
      this.field_146289_q.func_78276_b(I18n.func_135052_a("gui.achievements"), var1 + 15, var2 + 5, 4210752);
   }

   protected void func_146552_b(int var1, int var2, float var3) {
      int var4 = MathHelper.func_76128_c(this.field_146569_s + (this.field_146567_u - this.field_146569_s) * (double)var3);
      int var5 = MathHelper.func_76128_c(this.field_146568_t + (this.field_146566_v - this.field_146568_t) * (double)var3);
      if (var4 < field_146572_y) {
         var4 = field_146572_y;
      }

      if (var5 < field_146571_z) {
         var5 = field_146571_z;
      }

      if (var4 >= field_146559_A) {
         var4 = field_146559_A - 1;
      }

      if (var5 >= field_146560_B) {
         var5 = field_146560_B - 1;
      }

      int var6 = (this.field_146294_l - this.field_146555_f) / 2;
      int var7 = (this.field_146295_m - this.field_146557_g) / 2;
      int var8 = var6 + 16;
      int var9 = var7 + 17;
      this.field_73735_i = 0.0F;
      GlStateManager.func_179143_c(518);
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)var8, (float)var9, -200.0F);
      GlStateManager.func_179152_a(1.0F / this.field_146570_r, 1.0F / this.field_146570_r, 0.0F);
      GlStateManager.func_179098_w();
      GlStateManager.func_179140_f();
      GlStateManager.func_179091_B();
      GlStateManager.func_179142_g();
      int var10 = var4 + 288 >> 4;
      int var11 = var5 + 288 >> 4;
      int var12 = (var4 + 288) % 16;
      int var13 = (var5 + 288) % 16;
      boolean var14 = true;
      boolean var15 = true;
      boolean var16 = true;
      boolean var17 = true;
      boolean var18 = true;
      Random var19 = new Random();
      float var20 = 16.0F / this.field_146570_r;
      float var21 = 16.0F / this.field_146570_r;

      int var22;
      float var23;
      int var24;
      int var25;
      for(var22 = 0; (float)var22 * var20 - (float)var13 < 155.0F; ++var22) {
         var23 = 0.6F - (float)(var11 + var22) / 25.0F * 0.3F;
         GlStateManager.func_179131_c(var23, var23, var23, 1.0F);

         for(var24 = 0; (float)var24 * var21 - (float)var12 < 224.0F; ++var24) {
            var19.setSeed((long)(this.field_146297_k.func_110432_I().func_148255_b().hashCode() + var10 + var24 + (var11 + var22) * 16));
            var25 = var19.nextInt(1 + var11 + var22) + (var11 + var22) / 2;
            TextureAtlasSprite var26 = this.func_175371_a(Blocks.field_150354_m);
            if (var25 <= 37 && var11 + var22 != 35) {
               if (var25 == 22) {
                  if (var19.nextInt(2) == 0) {
                     var26 = this.func_175371_a(Blocks.field_150482_ag);
                  } else {
                     var26 = this.func_175371_a(Blocks.field_150450_ax);
                  }
               } else if (var25 == 10) {
                  var26 = this.func_175371_a(Blocks.field_150366_p);
               } else if (var25 == 8) {
                  var26 = this.func_175371_a(Blocks.field_150365_q);
               } else if (var25 > 4) {
                  var26 = this.func_175371_a(Blocks.field_150348_b);
               } else if (var25 > 0) {
                  var26 = this.func_175371_a(Blocks.field_150346_d);
               }
            } else {
               Block var27 = Blocks.field_150357_h;
               var26 = this.func_175371_a(var27);
            }

            this.field_146297_k.func_110434_K().func_110577_a(TextureMap.field_110575_b);
            this.func_175175_a(var24 * 16 - var12, var22 * 16 - var13, var26, 16, 16);
         }
      }

      GlStateManager.func_179126_j();
      GlStateManager.func_179143_c(515);
      this.field_146297_k.func_110434_K().func_110577_a(field_146561_C);

      int var30;
      int var31;
      int var37;
      for(var22 = 0; var22 < AchievementList.field_76007_e.size(); ++var22) {
         Achievement var34 = (Achievement)AchievementList.field_76007_e.get(var22);
         if (var34.field_75992_c != null) {
            var24 = var34.field_75993_a * 24 - var4 + 11;
            var25 = var34.field_75991_b * 24 - var5 + 11;
            int var39 = var34.field_75992_c.field_75993_a * 24 - var4 + 11;
            var37 = var34.field_75992_c.field_75991_b * 24 - var5 + 11;
            boolean var28 = this.field_146556_E.func_77443_a(var34);
            boolean var29 = this.field_146556_E.func_77442_b(var34);
            var30 = this.field_146556_E.func_150874_c(var34);
            if (var30 <= 4) {
               var31 = -16777216;
               if (var28) {
                  var31 = -6250336;
               } else if (var29) {
                  var31 = -16711936;
               }

               this.func_73730_a(var24, var39, var25, var31);
               this.func_73728_b(var39, var25, var37, var31);
               if (var24 > var39) {
                  this.func_73729_b(var24 - 11 - 7, var25 - 5, 114, 234, 7, 11);
               } else if (var24 < var39) {
                  this.func_73729_b(var24 + 11, var25 - 5, 107, 234, 7, 11);
               } else if (var25 > var37) {
                  this.func_73729_b(var24 - 5, var25 - 11 - 7, 96, 234, 11, 7);
               } else if (var25 < var37) {
                  this.func_73729_b(var24 - 5, var25 + 11, 96, 241, 11, 7);
               }
            }
         }
      }

      Achievement var33 = null;
      var23 = (float)(var1 - var8) * this.field_146570_r;
      float var35 = (float)(var2 - var9) * this.field_146570_r;
      RenderHelper.func_74520_c();
      GlStateManager.func_179140_f();
      GlStateManager.func_179091_B();
      GlStateManager.func_179142_g();

      int var38;
      int var41;
      for(var25 = 0; var25 < AchievementList.field_76007_e.size(); ++var25) {
         Achievement var40 = (Achievement)AchievementList.field_76007_e.get(var25);
         var37 = var40.field_75993_a * 24 - var4;
         var38 = var40.field_75991_b * 24 - var5;
         if (var37 >= -24 && var38 >= -24 && (float)var37 <= 224.0F * this.field_146570_r && (float)var38 <= 155.0F * this.field_146570_r) {
            var41 = this.field_146556_E.func_150874_c(var40);
            float var43;
            if (this.field_146556_E.func_77443_a(var40)) {
               var43 = 0.75F;
               GlStateManager.func_179131_c(var43, var43, var43, 1.0F);
            } else if (this.field_146556_E.func_77442_b(var40)) {
               var43 = 1.0F;
               GlStateManager.func_179131_c(var43, var43, var43, 1.0F);
            } else if (var41 < 3) {
               var43 = 0.3F;
               GlStateManager.func_179131_c(var43, var43, var43, 1.0F);
            } else if (var41 == 3) {
               var43 = 0.2F;
               GlStateManager.func_179131_c(var43, var43, var43, 1.0F);
            } else {
               if (var41 != 4) {
                  continue;
               }

               var43 = 0.1F;
               GlStateManager.func_179131_c(var43, var43, var43, 1.0F);
            }

            this.field_146297_k.func_110434_K().func_110577_a(field_146561_C);
            if (var40.func_75984_f()) {
               this.func_73729_b(var37 - 2, var38 - 2, 26, 202, 26, 26);
            } else {
               this.func_73729_b(var37 - 2, var38 - 2, 0, 202, 26, 26);
            }

            if (!this.field_146556_E.func_77442_b(var40)) {
               var43 = 0.1F;
               GlStateManager.func_179131_c(var43, var43, var43, 1.0F);
               this.field_146296_j.func_175039_a(false);
            }

            GlStateManager.func_179145_e();
            GlStateManager.func_179089_o();
            this.field_146296_j.func_180450_b(var40.field_75990_d, var37 + 3, var38 + 3);
            GlStateManager.func_179112_b(770, 771);
            GlStateManager.func_179140_f();
            if (!this.field_146556_E.func_77442_b(var40)) {
               this.field_146296_j.func_175039_a(true);
            }

            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
            if (var23 >= (float)var37 && var23 <= (float)(var37 + 22) && var35 >= (float)var38 && var35 <= (float)(var38 + 22)) {
               var33 = var40;
            }
         }
      }

      GlStateManager.func_179097_i();
      GlStateManager.func_179147_l();
      GlStateManager.func_179121_F();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_146561_C);
      this.func_73729_b(var6, var7, 0, 0, this.field_146555_f, this.field_146557_g);
      this.field_73735_i = 0.0F;
      GlStateManager.func_179143_c(515);
      GlStateManager.func_179097_i();
      GlStateManager.func_179098_w();
      super.func_73863_a(var1, var2, var3);
      if (var33 != null) {
         String var36 = var33.func_150951_e().func_150260_c();
         String var42 = var33.func_75989_e();
         var37 = var1 + 12;
         var38 = var2 - 4;
         var41 = this.field_146556_E.func_150874_c(var33);
         if (this.field_146556_E.func_77442_b(var33)) {
            var30 = Math.max(this.field_146289_q.func_78256_a(var36), 120);
            var31 = this.field_146289_q.func_78267_b(var42, var30);
            if (this.field_146556_E.func_77443_a(var33)) {
               var31 += 12;
            }

            this.func_73733_a(var37 - 3, var38 - 3, var37 + var30 + 3, var38 + var31 + 3 + 12, -1073741824, -1073741824);
            this.field_146289_q.func_78279_b(var42, var37, var38 + 12, var30, -6250336);
            if (this.field_146556_E.func_77443_a(var33)) {
               this.field_146289_q.func_175063_a(I18n.func_135052_a("achievement.taken"), (float)var37, (float)(var38 + var31 + 4), -7302913);
            }
         } else {
            int var32;
            String var44;
            if (var41 == 3) {
               var36 = I18n.func_135052_a("achievement.unknown");
               var30 = Math.max(this.field_146289_q.func_78256_a(var36), 120);
               var44 = (new ChatComponentTranslation("achievement.requires", new Object[]{var33.field_75992_c.func_150951_e()})).func_150260_c();
               var32 = this.field_146289_q.func_78267_b(var44, var30);
               this.func_73733_a(var37 - 3, var38 - 3, var37 + var30 + 3, var38 + var32 + 12 + 3, -1073741824, -1073741824);
               this.field_146289_q.func_78279_b(var44, var37, var38 + 12, var30, -9416624);
            } else if (var41 < 3) {
               var30 = Math.max(this.field_146289_q.func_78256_a(var36), 120);
               var44 = (new ChatComponentTranslation("achievement.requires", new Object[]{var33.field_75992_c.func_150951_e()})).func_150260_c();
               var32 = this.field_146289_q.func_78267_b(var44, var30);
               this.func_73733_a(var37 - 3, var38 - 3, var37 + var30 + 3, var38 + var32 + 12 + 3, -1073741824, -1073741824);
               this.field_146289_q.func_78279_b(var44, var37, var38 + 12, var30, -9416624);
            } else {
               var36 = null;
            }
         }

         if (var36 != null) {
            this.field_146289_q.func_175063_a(var36, (float)var37, (float)var38, this.field_146556_E.func_77442_b(var33) ? (var33.func_75984_f() ? -128 : -1) : (var33.func_75984_f() ? -8355776 : -8355712));
         }
      }

      GlStateManager.func_179126_j();
      GlStateManager.func_179145_e();
      RenderHelper.func_74518_a();
   }

   private TextureAtlasSprite func_175371_a(Block var1) {
      return Minecraft.func_71410_x().func_175602_ab().func_175023_a().func_178122_a(var1.func_176223_P());
   }

   public boolean func_73868_f() {
      return !this.field_146558_F;
   }

   static {
      field_146572_y = AchievementList.field_76010_a * 24 - 112;
      field_146571_z = AchievementList.field_76008_b * 24 - 112;
      field_146559_A = AchievementList.field_76009_c * 24 - 77;
      field_146560_B = AchievementList.field_76006_d * 24 - 77;
      field_146561_C = new ResourceLocation("textures/gui/achievement/achievement_background.png");
   }
}

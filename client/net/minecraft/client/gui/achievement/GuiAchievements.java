package net.minecraft.client.gui.achievement;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus$EnumState;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiAchievements extends GuiScreen implements IProgressMeter {
   private static final int field_146572_y = AchievementList.field_76010_a * 24 - 112;
   private static final int field_146571_z = AchievementList.field_76008_b * 24 - 112;
   private static final int field_146559_A = AchievementList.field_76009_c * 24 - 77;
   private static final int field_146560_B = AchievementList.field_76006_d * 24 - 77;
   private static final ResourceLocation field_146561_C = new ResourceLocation("textures/gui/achievement/achievement_background.png");
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

   @Override
   public void func_73866_w_() {
      this.field_146297_k.func_147114_u().func_147297_a(new C16PacketClientStatus(C16PacketClientStatus$EnumState.REQUEST_STATS));
      this.field_146292_n.clear();
      this.field_146292_n.add(new GuiOptionButton(1, this.field_146294_l / 2 + 24, this.field_146295_m / 2 + 74, 80, 20, I18n.func_135052_a("gui.done")));
   }

   @Override
   protected void func_146284_a(GuiButton var1) {
      if (!this.field_146558_F) {
         if (var1.field_146127_k == 1) {
            this.field_146297_k.func_147108_a(this.field_146562_a);
         }
      }
   }

   @Override
   protected void func_73869_a(char var1, int var2) {
      if (var2 == this.field_146297_k.field_71474_y.field_151445_Q.func_151463_i()) {
         this.field_146297_k.func_147108_a(null);
         this.field_146297_k.func_71381_h();
      } else {
         super.func_73869_a(var1, var2);
      }
   }

   @Override
   public void func_73863_a(int var1, int var2, float var3) {
      if (this.field_146558_F) {
         this.func_146276_q_();
         this.func_73732_a(this.field_146289_q, I18n.func_135052_a("multiplayer.downloadingStats"), this.field_146294_l / 2, this.field_146295_m / 2, 16777215);
         this.func_73732_a(
            this.field_146289_q,
            field_146510_b_[(int)(Minecraft.func_71386_F() / 150L % (long)field_146510_b_.length)],
            this.field_146294_l / 2,
            this.field_146295_m / 2 + this.field_146289_q.field_78288_b * 2,
            16777215
         );
      } else {
         if (Mouse.isButtonDown(0)) {
            int var4 = (this.field_146294_l - this.field_146555_f) / 2;
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

         int var11 = Mouse.getDWheel();
         float var12 = this.field_146570_r;
         if (var11 < 0) {
            this.field_146570_r += 0.25F;
         } else if (var11 > 0) {
            this.field_146570_r -= 0.25F;
         }

         this.field_146570_r = MathHelper.func_76131_a(this.field_146570_r, 1.0F, 2.0F);
         if (this.field_146570_r != var12) {
            float var13 = var12 - this.field_146570_r;
            float var14 = var12 * (float)this.field_146555_f;
            float var8 = var12 * (float)this.field_146557_g;
            float var9 = this.field_146570_r * (float)this.field_146555_f;
            float var10 = this.field_146570_r * (float)this.field_146557_g;
            this.field_146567_u -= (double)((var9 - var14) * 0.5F);
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
         GL11.glDisable(2896);
         GL11.glDisable(2929);
         this.func_146553_h();
         GL11.glEnable(2896);
         GL11.glEnable(2929);
      }
   }

   @Override
   public void func_146509_g() {
      if (this.field_146558_F) {
         this.field_146558_F = false;
      }
   }

   @Override
   public void func_73876_c() {
      if (!this.field_146558_F) {
         this.field_146569_s = this.field_146567_u;
         this.field_146568_t = this.field_146566_v;
         double var1 = this.field_146565_w - this.field_146567_u;
         double var3 = this.field_146573_x - this.field_146566_v;
         if (var1 * var1 + var3 * var3 < 4.0) {
            this.field_146567_u += var1;
            this.field_146566_v += var3;
         } else {
            this.field_146567_u += var1 * 0.85;
            this.field_146566_v += var3 * 0.85;
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
      GL11.glDepthFunc(518);
      GL11.glPushMatrix();
      GL11.glTranslatef((float)var8, (float)var9, -200.0F);
      GL11.glScalef(1.0F / this.field_146570_r, 1.0F / this.field_146570_r, 0.0F);
      GL11.glEnable(3553);
      GL11.glDisable(2896);
      GL11.glEnable(32826);
      GL11.glEnable(2903);
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

      for(int var22 = 0; (float)var22 * var20 - (float)var13 < 155.0F; ++var22) {
         float var23 = 0.6F - (float)(var11 + var22) / 25.0F * 0.3F;
         GL11.glColor4f(var23, var23, var23, 1.0F);

         for(int var24 = 0; (float)var24 * var21 - (float)var12 < 224.0F; ++var24) {
            var19.setSeed((long)(this.field_146297_k.func_110432_I().func_148255_b().hashCode() + var10 + var24 + (var11 + var22) * 16));
            int var25 = var19.nextInt(1 + var11 + var22) + (var11 + var22) / 2;
            IIcon var26 = Blocks.field_150354_m.func_149691_a(0, 0);
            if (var25 > 37 || var11 + var22 == 35) {
               var26 = Blocks.field_150357_h.func_149691_a(0, 0);
            } else if (var25 == 22) {
               if (var19.nextInt(2) == 0) {
                  var26 = Blocks.field_150482_ag.func_149691_a(0, 0);
               } else {
                  var26 = Blocks.field_150450_ax.func_149691_a(0, 0);
               }
            } else if (var25 == 10) {
               var26 = Blocks.field_150366_p.func_149691_a(0, 0);
            } else if (var25 == 8) {
               var26 = Blocks.field_150365_q.func_149691_a(0, 0);
            } else if (var25 > 4) {
               var26 = Blocks.field_150348_b.func_149691_a(0, 0);
            } else if (var25 > 0) {
               var26 = Blocks.field_150346_d.func_149691_a(0, 0);
            }

            this.field_146297_k.func_110434_K().func_110577_a(TextureMap.field_110575_b);
            this.func_94065_a(var24 * 16 - var12, var22 * 16 - var13, var26, 16, 16);
         }
      }

      GL11.glEnable(2929);
      GL11.glDepthFunc(515);
      this.field_146297_k.func_110434_K().func_110577_a(field_146561_C);

      for(int var34 = 0; var34 < AchievementList.field_76007_e.size(); ++var34) {
         Achievement var36 = (Achievement)AchievementList.field_76007_e.get(var34);
         if (var36.field_75992_c != null) {
            int var38 = var36.field_75993_a * 24 - var4 + 11;
            int var40 = var36.field_75991_b * 24 - var5 + 11;
            int var42 = var36.field_75992_c.field_75993_a * 24 - var4 + 11;
            int var27 = var36.field_75992_c.field_75991_b * 24 - var5 + 11;
            boolean var28 = this.field_146556_E.func_77443_a(var36);
            boolean var29 = this.field_146556_E.func_77442_b(var36);
            int var30 = this.field_146556_E.func_150874_c(var36);
            if (var30 <= 4) {
               int var31 = -16777216;
               if (var28) {
                  var31 = -6250336;
               } else if (var29) {
                  var31 = -16711936;
               }

               this.func_73730_a(var38, var42, var40, var31);
               this.func_73728_b(var42, var40, var27, var31);
               if (var38 > var42) {
                  this.func_73729_b(var38 - 11 - 7, var40 - 5, 114, 234, 7, 11);
               } else if (var38 < var42) {
                  this.func_73729_b(var38 + 11, var40 - 5, 107, 234, 7, 11);
               } else if (var40 > var27) {
                  this.func_73729_b(var38 - 5, var40 - 11 - 7, 96, 234, 11, 7);
               } else if (var40 < var27) {
                  this.func_73729_b(var38 - 5, var40 + 11, 96, 241, 11, 7);
               }
            }
         }
      }

      Achievement var35 = null;
      RenderItem var37 = new RenderItem();
      float var39 = (float)(var1 - var8) * this.field_146570_r;
      float var41 = (float)(var2 - var9) * this.field_146570_r;
      RenderHelper.func_74520_c();
      GL11.glDisable(2896);
      GL11.glEnable(32826);
      GL11.glEnable(2903);

      for(int var43 = 0; var43 < AchievementList.field_76007_e.size(); ++var43) {
         Achievement var45 = (Achievement)AchievementList.field_76007_e.get(var43);
         int var47 = var45.field_75993_a * 24 - var4;
         int var49 = var45.field_75991_b * 24 - var5;
         if (var47 >= -24 && var49 >= -24 && (float)var47 <= 224.0F * this.field_146570_r && (float)var49 <= 155.0F * this.field_146570_r) {
            int var51 = this.field_146556_E.func_150874_c(var45);
            if (this.field_146556_E.func_77443_a(var45)) {
               float var53 = 0.75F;
               GL11.glColor4f(var53, var53, var53, 1.0F);
            } else if (this.field_146556_E.func_77442_b(var45)) {
               float var54 = 1.0F;
               GL11.glColor4f(var54, var54, var54, 1.0F);
            } else if (var51 < 3) {
               float var55 = 0.3F;
               GL11.glColor4f(var55, var55, var55, 1.0F);
            } else if (var51 == 3) {
               float var56 = 0.2F;
               GL11.glColor4f(var56, var56, var56, 1.0F);
            } else {
               if (var51 != 4) {
                  continue;
               }

               float var57 = 0.1F;
               GL11.glColor4f(var57, var57, var57, 1.0F);
            }

            this.field_146297_k.func_110434_K().func_110577_a(field_146561_C);
            if (var45.func_75984_f()) {
               this.func_73729_b(var47 - 2, var49 - 2, 26, 202, 26, 26);
            } else {
               this.func_73729_b(var47 - 2, var49 - 2, 0, 202, 26, 26);
            }

            if (!this.field_146556_E.func_77442_b(var45)) {
               float var58 = 0.1F;
               GL11.glColor4f(var58, var58, var58, 1.0F);
               var37.field_77024_a = false;
            }

            GL11.glEnable(2896);
            GL11.glEnable(2884);
            var37.func_82406_b(this.field_146297_k.field_71466_p, this.field_146297_k.func_110434_K(), var45.field_75990_d, var47 + 3, var49 + 3);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(2896);
            if (!this.field_146556_E.func_77442_b(var45)) {
               var37.field_77024_a = true;
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (var39 >= (float)var47 && var39 <= (float)(var47 + 22) && var41 >= (float)var49 && var41 <= (float)(var49 + 22)) {
               var35 = var45;
            }
         }
      }

      GL11.glDisable(2929);
      GL11.glEnable(3042);
      GL11.glPopMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_146561_C);
      this.func_73729_b(var6, var7, 0, 0, this.field_146555_f, this.field_146557_g);
      this.field_73735_i = 0.0F;
      GL11.glDepthFunc(515);
      GL11.glDisable(2929);
      GL11.glEnable(3553);
      super.func_73863_a(var1, var2, var3);
      if (var35 != null) {
         String var44 = var35.func_150951_e().func_150260_c();
         String var46 = var35.func_75989_e();
         int var48 = var1 + 12;
         int var50 = var2 - 4;
         int var52 = this.field_146556_E.func_150874_c(var35);
         if (!this.field_146556_E.func_77442_b(var35)) {
            if (var52 == 3) {
               var44 = I18n.func_135052_a("achievement.unknown");
               int var59 = Math.max(this.field_146289_q.func_78256_a(var44), 120);
               String var32 = new ChatComponentTranslation("achievement.requires", var35.field_75992_c.func_150951_e()).func_150260_c();
               int var33 = this.field_146289_q.func_78267_b(var32, var59);
               this.func_73733_a(var48 - 3, var50 - 3, var48 + var59 + 3, var50 + var33 + 12 + 3, -1073741824, -1073741824);
               this.field_146289_q.func_78279_b(var32, var48, var50 + 12, var59, -9416624);
            } else if (var52 < 3) {
               int var60 = Math.max(this.field_146289_q.func_78256_a(var44), 120);
               String var62 = new ChatComponentTranslation("achievement.requires", var35.field_75992_c.func_150951_e()).func_150260_c();
               int var64 = this.field_146289_q.func_78267_b(var62, var60);
               this.func_73733_a(var48 - 3, var50 - 3, var48 + var60 + 3, var50 + var64 + 12 + 3, -1073741824, -1073741824);
               this.field_146289_q.func_78279_b(var62, var48, var50 + 12, var60, -9416624);
            } else {
               var44 = null;
            }
         } else {
            int var61 = Math.max(this.field_146289_q.func_78256_a(var44), 120);
            int var63 = this.field_146289_q.func_78267_b(var46, var61);
            if (this.field_146556_E.func_77443_a(var35)) {
               var63 += 12;
            }

            this.func_73733_a(var48 - 3, var50 - 3, var48 + var61 + 3, var50 + var63 + 3 + 12, -1073741824, -1073741824);
            this.field_146289_q.func_78279_b(var46, var48, var50 + 12, var61, -6250336);
            if (this.field_146556_E.func_77443_a(var35)) {
               this.field_146289_q.func_78261_a(I18n.func_135052_a("achievement.taken"), var48, var50 + var63 + 4, -7302913);
            }
         }

         if (var44 != null) {
            this.field_146289_q
               .func_78261_a(
                  var44,
                  var48,
                  var50,
                  this.field_146556_E.func_77442_b(var35) ? (var35.func_75984_f() ? -128 : -1) : (var35.func_75984_f() ? -8355776 : -8355712)
               );
         }
      }

      GL11.glEnable(2929);
      GL11.glEnable(2896);
      RenderHelper.func_74518_a();
   }

   @Override
   public boolean func_73868_f() {
      return !this.field_146558_F;
   }
}

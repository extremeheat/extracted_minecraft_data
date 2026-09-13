package net.minecraft.client.gui;

import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.GL11;

public class GuiIngame extends Gui {
   private static final ResourceLocation field_110329_b = new ResourceLocation("textures/misc/vignette.png");
   private static final ResourceLocation field_110330_c = new ResourceLocation("textures/gui/widgets.png");
   private static final ResourceLocation field_110328_d = new ResourceLocation("textures/misc/pumpkinblur.png");
   private static final RenderItem field_73841_b = new RenderItem();
   private final Random field_73842_c = new Random();
   private final Minecraft field_73839_d;
   private final GuiNewChat field_73840_e;
   private final GuiStreamIndicator field_152127_m;
   private int field_73837_f;
   private String field_73838_g = "";
   private int field_73845_h;
   private boolean field_73844_j;
   public float field_73843_a = 1.0F;
   private int field_92017_k;
   private ItemStack field_92016_l;

   public GuiIngame(Minecraft var1) {
      super();
      this.field_73839_d = var1;
      this.field_73840_e = new GuiNewChat(var1);
      this.field_152127_m = new GuiStreamIndicator(this.field_73839_d);
   }

   public void func_73830_a(float var1, boolean var2, int var3, int var4) {
      ScaledResolution var5 = new ScaledResolution(this.field_73839_d, this.field_73839_d.field_71443_c, this.field_73839_d.field_71440_d);
      int var6 = var5.func_78326_a();
      int var7 = var5.func_78328_b();
      FontRenderer var8 = this.field_73839_d.field_71466_p;
      this.field_73839_d.field_71460_t.func_78478_c();
      GL11.glEnable(3042);
      if (Minecraft.func_71375_t()) {
         this.func_73829_a(this.field_73839_d.field_71439_g.func_70013_c(var1), var6, var7);
      } else {
         OpenGlHelper.func_148821_a(770, 771, 1, 0);
      }

      ItemStack var9 = this.field_73839_d.field_71439_g.field_71071_by.func_70440_f(3);
      if (this.field_73839_d.field_71474_y.field_74320_O == 0 && var9 != null && var9.func_77973_b() == Item.func_150898_a(Blocks.field_150423_aK)) {
         this.func_73836_a(var6, var7);
      }

      if (!this.field_73839_d.field_71439_g.func_70644_a(Potion.field_76431_k)) {
         float var10 = this.field_73839_d.field_71439_g.field_71080_cy
            + (this.field_73839_d.field_71439_g.field_71086_bY - this.field_73839_d.field_71439_g.field_71080_cy) * var1;
         if (var10 > 0.0F) {
            this.func_130015_b(var10, var6, var7);
         }
      }

      if (!this.field_73839_d.field_71442_b.func_78747_a()) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_73839_d.func_110434_K().func_110577_a(field_110330_c);
         InventoryPlayer var31 = this.field_73839_d.field_71439_g.field_71071_by;
         this.field_73735_i = -90.0F;
         this.func_73729_b(var6 / 2 - 91, var7 - 22, 0, 0, 182, 22);
         this.func_73729_b(var6 / 2 - 91 - 1 + var31.field_70461_c * 20, var7 - 22 - 1, 0, 22, 24, 22);
         this.field_73839_d.func_110434_K().func_110577_a(field_110324_m);
         GL11.glEnable(3042);
         OpenGlHelper.func_148821_a(775, 769, 1, 0);
         this.func_73729_b(var6 / 2 - 7, var7 / 2 - 7, 0, 0, 16, 16);
         OpenGlHelper.func_148821_a(770, 771, 1, 0);
         this.field_73839_d.field_71424_I.func_76320_a("bossHealth");
         this.func_73828_d();
         this.field_73839_d.field_71424_I.func_76319_b();
         if (this.field_73839_d.field_71442_b.func_78755_b()) {
            this.func_110327_a(var6, var7);
         }

         this.field_73839_d.field_71424_I.func_76320_a("actionBar");
         GL11.glEnable(32826);
         RenderHelper.func_74520_c();

         for(int var11 = 0; var11 < 9; ++var11) {
            int var12 = var6 / 2 - 90 + var11 * 20 + 2;
            int var13 = var7 - 16 - 3;
            this.func_73832_a(var11, var12, var13, var1);
         }

         RenderHelper.func_74518_a();
         GL11.glDisable(32826);
         this.field_73839_d.field_71424_I.func_76319_b();
         GL11.glDisable(3042);
      }

      if (this.field_73839_d.field_71439_g.func_71060_bI() > 0) {
         this.field_73839_d.field_71424_I.func_76320_a("sleep");
         GL11.glDisable(2929);
         GL11.glDisable(3008);
         int var32 = this.field_73839_d.field_71439_g.func_71060_bI();
         float var34 = (float)var32 / 100.0F;
         if (var34 > 1.0F) {
            var34 = 1.0F - (float)(var32 - 100) / 10.0F;
         }

         int var36 = (int)(220.0F * var34) << 24 | 1052704;
         func_73734_a(0, 0, var6, var7, var36);
         GL11.glEnable(3008);
         GL11.glEnable(2929);
         this.field_73839_d.field_71424_I.func_76319_b();
      }

      int var33 = 16777215;
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      int var35 = var6 / 2 - 91;
      if (this.field_73839_d.field_71439_g.func_110317_t()) {
         this.field_73839_d.field_71424_I.func_76320_a("jumpBar");
         this.field_73839_d.func_110434_K().func_110577_a(Gui.field_110324_m);
         float var37 = this.field_73839_d.field_71439_g.func_110319_bJ();
         short var46 = 182;
         int var14 = (int)(var37 * (float)(var46 + 1));
         int var15 = var7 - 32 + 3;
         this.func_73729_b(var35, var15, 0, 84, var46, 5);
         if (var14 > 0) {
            this.func_73729_b(var35, var15, 0, 89, var14, 5);
         }

         this.field_73839_d.field_71424_I.func_76319_b();
      } else if (this.field_73839_d.field_71442_b.func_78763_f()) {
         this.field_73839_d.field_71424_I.func_76320_a("expBar");
         this.field_73839_d.func_110434_K().func_110577_a(Gui.field_110324_m);
         int var38 = this.field_73839_d.field_71439_g.func_71050_bK();
         if (var38 > 0) {
            short var47 = 182;
            int var53 = (int)(this.field_73839_d.field_71439_g.field_71106_cc * (float)(var47 + 1));
            int var59 = var7 - 32 + 3;
            this.func_73729_b(var35, var59, 0, 64, var47, 5);
            if (var53 > 0) {
               this.func_73729_b(var35, var59, 0, 69, var53, 5);
            }
         }

         this.field_73839_d.field_71424_I.func_76319_b();
         if (this.field_73839_d.field_71439_g.field_71068_ca > 0) {
            this.field_73839_d.field_71424_I.func_76320_a("expLevel");
            boolean var48 = false;
            int var54 = var48 ? 16777215 : 8453920;
            String var60 = "" + this.field_73839_d.field_71439_g.field_71068_ca;
            int var16 = (var6 - var8.func_78256_a(var60)) / 2;
            int var17 = var7 - 31 - 4;
            boolean var18 = false;
            var8.func_78276_b(var60, var16 + 1, var17, 0);
            var8.func_78276_b(var60, var16 - 1, var17, 0);
            var8.func_78276_b(var60, var16, var17 + 1, 0);
            var8.func_78276_b(var60, var16, var17 - 1, 0);
            var8.func_78276_b(var60, var16, var17, var54);
            this.field_73839_d.field_71424_I.func_76319_b();
         }
      }

      if (this.field_73839_d.field_71474_y.field_92117_D) {
         this.field_73839_d.field_71424_I.func_76320_a("toolHighlight");
         if (this.field_92017_k > 0 && this.field_92016_l != null) {
            String var39 = this.field_92016_l.func_82833_r();
            int var49 = (var6 - var8.func_78256_a(var39)) / 2;
            int var55 = var7 - 59;
            if (!this.field_73839_d.field_71442_b.func_78755_b()) {
               var55 += 14;
            }

            int var61 = (int)((float)this.field_92017_k * 256.0F / 10.0F);
            if (var61 > 255) {
               var61 = 255;
            }

            if (var61 > 0) {
               GL11.glPushMatrix();
               GL11.glEnable(3042);
               OpenGlHelper.func_148821_a(770, 771, 1, 0);
               var8.func_78261_a(var39, var49, var55, 16777215 + (var61 << 24));
               GL11.glDisable(3042);
               GL11.glPopMatrix();
            }
         }

         this.field_73839_d.field_71424_I.func_76319_b();
      }

      if (this.field_73839_d.func_71355_q()) {
         this.field_73839_d.field_71424_I.func_76320_a("demo");
         String var40 = "";
         if (this.field_73839_d.field_71441_e.func_82737_E() >= 120500L) {
            var40 = I18n.func_135052_a("demo.demoExpired");
         } else {
            var40 = I18n.func_135052_a("demo.remainingTime", StringUtils.func_76337_a((int)(120500L - this.field_73839_d.field_71441_e.func_82737_E())));
         }

         int var50 = var8.func_78256_a(var40);
         var8.func_78261_a(var40, var6 - var50 - 10, 5, 16777215);
         this.field_73839_d.field_71424_I.func_76319_b();
      }

      if (this.field_73839_d.field_71474_y.field_74330_P) {
         this.field_73839_d.field_71424_I.func_76320_a("debug");
         GL11.glPushMatrix();
         var8.func_78261_a("Minecraft 1.7.10 (" + this.field_73839_d.field_71426_K + ")", 2, 2, 16777215);
         var8.func_78261_a(this.field_73839_d.func_71393_m(), 2, 12, 16777215);
         var8.func_78261_a(this.field_73839_d.func_71408_n(), 2, 22, 16777215);
         var8.func_78261_a(this.field_73839_d.func_71374_p(), 2, 32, 16777215);
         var8.func_78261_a(this.field_73839_d.func_71388_o(), 2, 42, 16777215);
         long var42 = Runtime.getRuntime().maxMemory();
         long var56 = Runtime.getRuntime().totalMemory();
         long var63 = Runtime.getRuntime().freeMemory();
         long var66 = var56 - var63;
         String var20 = "Used memory: " + var66 * 100L / var42 + "% (" + var66 / 1024L / 1024L + "MB) of " + var42 / 1024L / 1024L + "MB";
         int var21 = 14737632;
         this.func_73731_b(var8, var20, var6 - var8.func_78256_a(var20) - 2, 2, 14737632);
         var20 = "Allocated memory: " + var56 * 100L / var42 + "% (" + var56 / 1024L / 1024L + "MB)";
         this.func_73731_b(var8, var20, var6 - var8.func_78256_a(var20) - 2, 12, 14737632);
         int var22 = MathHelper.func_76128_c(this.field_73839_d.field_71439_g.field_70165_t);
         int var23 = MathHelper.func_76128_c(this.field_73839_d.field_71439_g.field_70163_u);
         int var24 = MathHelper.func_76128_c(this.field_73839_d.field_71439_g.field_70161_v);
         this.func_73731_b(
            var8, String.format("x: %.5f (%d) // c: %d (%d)", this.field_73839_d.field_71439_g.field_70165_t, var22, var22 >> 4, var22 & 15), 2, 64, 14737632
         );
         this.func_73731_b(
            var8,
            String.format(
               "y: %.3f (feet pos, %.3f eyes pos)",
               this.field_73839_d.field_71439_g.field_70121_D.field_72338_b,
               this.field_73839_d.field_71439_g.field_70163_u
            ),
            2,
            72,
            14737632
         );
         this.func_73731_b(
            var8, String.format("z: %.5f (%d) // c: %d (%d)", this.field_73839_d.field_71439_g.field_70161_v, var24, var24 >> 4, var24 & 15), 2, 80, 14737632
         );
         int var25 = MathHelper.func_76128_c((double)(this.field_73839_d.field_71439_g.field_70177_z * 4.0F / 360.0F) + 0.5) & 3;
         this.func_73731_b(
            var8,
            "f: " + var25 + " (" + Direction.field_82373_c[var25] + ") / " + MathHelper.func_76142_g(this.field_73839_d.field_71439_g.field_70177_z),
            2,
            88,
            14737632
         );
         if (this.field_73839_d.field_71441_e != null && this.field_73839_d.field_71441_e.func_72899_e(var22, var23, var24)) {
            Chunk var26 = this.field_73839_d.field_71441_e.func_72938_d(var22, var24);
            this.func_73731_b(
               var8,
               "lc: "
                  + (var26.func_76625_h() + 15)
                  + " b: "
                  + var26.func_76591_a(var22 & 15, var24 & 15, this.field_73839_d.field_71441_e.func_72959_q()).field_76791_y
                  + " bl: "
                  + var26.func_76614_a(EnumSkyBlock.Block, var22 & 15, var23, var24 & 15)
                  + " sl: "
                  + var26.func_76614_a(EnumSkyBlock.Sky, var22 & 15, var23, var24 & 15)
                  + " rl: "
                  + var26.func_76629_c(var22 & 15, var23, var24 & 15, 0),
               2,
               96,
               14737632
            );
         }

         this.func_73731_b(
            var8,
            String.format(
               "ws: %.3f, fs: %.3f, g: %b, fl: %d",
               this.field_73839_d.field_71439_g.field_71075_bZ.func_75094_b(),
               this.field_73839_d.field_71439_g.field_71075_bZ.func_75093_a(),
               this.field_73839_d.field_71439_g.field_70122_E,
               this.field_73839_d.field_71441_e.func_72976_f(var22, var24)
            ),
            2,
            104,
            14737632
         );
         if (this.field_73839_d.field_71460_t != null && this.field_73839_d.field_71460_t.func_147702_a()) {
            this.func_73731_b(var8, String.format("shader: %s", this.field_73839_d.field_71460_t.func_147706_e().func_148022_b()), 2, 112, 14737632);
         }

         GL11.glPopMatrix();
         this.field_73839_d.field_71424_I.func_76319_b();
      }

      if (this.field_73845_h > 0) {
         this.field_73839_d.field_71424_I.func_76320_a("overlayMessage");
         float var43 = (float)this.field_73845_h - var1;
         int var51 = (int)(var43 * 255.0F / 20.0F);
         if (var51 > 255) {
            var51 = 255;
         }

         if (var51 > 8) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)(var6 / 2), (float)(var7 - 68), 0.0F);
            GL11.glEnable(3042);
            OpenGlHelper.func_148821_a(770, 771, 1, 0);
            int var57 = 16777215;
            if (this.field_73844_j) {
               var57 = Color.HSBtoRGB(var43 / 50.0F, 0.7F, 0.6F) & 16777215;
            }

            var8.func_78276_b(this.field_73838_g, -var8.func_78256_a(this.field_73838_g) / 2, -4, var57 + (var51 << 24 & 0xFF000000));
            GL11.glDisable(3042);
            GL11.glPopMatrix();
         }

         this.field_73839_d.field_71424_I.func_76319_b();
      }

      ScoreObjective var44 = this.field_73839_d.field_71441_e.func_96441_U().func_96539_a(1);
      if (var44 != null) {
         this.func_96136_a(var44, var7, var6, var8);
      }

      GL11.glEnable(3042);
      OpenGlHelper.func_148821_a(770, 771, 1, 0);
      GL11.glDisable(3008);
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, (float)(var7 - 48), 0.0F);
      this.field_73839_d.field_71424_I.func_76320_a("chat");
      this.field_73840_e.func_146230_a(this.field_73837_f);
      this.field_73839_d.field_71424_I.func_76319_b();
      GL11.glPopMatrix();
      var44 = this.field_73839_d.field_71441_e.func_96441_U().func_96539_a(0);
      if (this.field_73839_d.field_71474_y.field_74321_H.func_151470_d()
         && (!this.field_73839_d.func_71387_A() || this.field_73839_d.field_71439_g.field_71174_a.field_147303_b.size() > 1 || var44 != null)) {
         this.field_73839_d.field_71424_I.func_76320_a("playerList");
         NetHandlerPlayClient var52 = this.field_73839_d.field_71439_g.field_71174_a;
         List var58 = var52.field_147303_b;
         int var62 = var52.field_147304_c;
         int var64 = var62;

         int var65;
         for(var65 = 1; var64 > 20; var64 = (var62 + var65 - 1) / var65) {
            ++var65;
         }

         int var67 = 300 / var65;
         if (var67 > 150) {
            var67 = 150;
         }

         int var19 = (var6 - var65 * var67) / 2;
         byte var69 = 10;
         func_73734_a(var19 - 1, var69 - 1, var19 + var67 * var65, var69 + 9 * var64, -2147483648);

         for(int var70 = 0; var70 < var62; ++var70) {
            int var71 = var19 + var70 % var65 * var67;
            int var72 = var69 + var70 / var65 * 9;
            func_73734_a(var71, var72, var71 + var67 - 1, var72 + 8, 553648127);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(3008);
            if (var70 < var58.size()) {
               GuiPlayerInfo var73 = (GuiPlayerInfo)var58.get(var70);
               ScorePlayerTeam var74 = this.field_73839_d.field_71441_e.func_96441_U().func_96509_i(var73.field_78831_a);
               String var75 = ScorePlayerTeam.func_96667_a(var74, var73.field_78831_a);
               var8.func_78261_a(var75, var71, var72, 16777215);
               if (var44 != null) {
                  int var27 = var71 + var8.func_78256_a(var75) + 5;
                  int var28 = var71 + var67 - 12 - 5;
                  if (var28 - var27 > 5) {
                     Score var29 = var44.func_96682_a().func_96529_a(var73.field_78831_a, var44);
                     String var30 = EnumChatFormatting.YELLOW + "" + var29.func_96652_c();
                     var8.func_78261_a(var30, var28 - var8.func_78256_a(var30), var72, 16777215);
                  }
               }

               GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
               this.field_73839_d.func_110434_K().func_110577_a(field_110324_m);
               byte var76 = 0;
               byte var77 = 0;
               if (var73.field_78829_b < 0) {
                  var77 = 5;
               } else if (var73.field_78829_b < 150) {
                  var77 = 0;
               } else if (var73.field_78829_b < 300) {
                  var77 = 1;
               } else if (var73.field_78829_b < 600) {
                  var77 = 2;
               } else if (var73.field_78829_b < 1000) {
                  var77 = 3;
               } else {
                  var77 = 4;
               }

               this.field_73735_i += 100.0F;
               this.func_73729_b(var71 + var67 - 12, var72, 0 + var76 * 10, 176 + var77 * 8, 10, 8);
               this.field_73735_i -= 100.0F;
            }
         }
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(2896);
      GL11.glEnable(3008);
   }

   public void func_152126_a(float var1, float var2) {
      this.field_152127_m.func_152437_a((int)(var1 - 10.0F), 10);
   }

   private void func_96136_a(ScoreObjective var1, int var2, int var3, FontRenderer var4) {
      Scoreboard var5 = var1.func_96682_a();
      Collection var6 = var5.func_96534_i(var1);
      if (var6.size() <= 15) {
         int var7 = var4.func_78256_a(var1.func_96678_d());

         for(Score var9 : var6) {
            ScorePlayerTeam var10 = var5.func_96509_i(var9.func_96653_e());
            String var11 = ScorePlayerTeam.func_96667_a(var10, var9.func_96653_e()) + ": " + EnumChatFormatting.RED + var9.func_96652_c();
            var7 = Math.max(var7, var4.func_78256_a(var11));
         }

         int var22 = var6.size() * var4.field_78288_b;
         int var23 = var2 / 2 + var22 / 3;
         byte var24 = 3;
         int var25 = var3 - var7 - var24;
         int var12 = 0;

         for(Score var14 : var6) {
            ++var12;
            ScorePlayerTeam var15 = var5.func_96509_i(var14.func_96653_e());
            String var16 = ScorePlayerTeam.func_96667_a(var15, var14.func_96653_e());
            String var17 = EnumChatFormatting.RED + "" + var14.func_96652_c();
            int var19 = var23 - var12 * var4.field_78288_b;
            int var20 = var3 - var24 + 2;
            func_73734_a(var25 - 2, var19, var20, var19 + var4.field_78288_b, 1342177280);
            var4.func_78276_b(var16, var25, var19, 553648127);
            var4.func_78276_b(var17, var20 - var4.func_78256_a(var17), var19, 553648127);
            if (var12 == var6.size()) {
               String var21 = var1.func_96678_d();
               func_73734_a(var25 - 2, var19 - var4.field_78288_b - 1, var20, var19 - 1, 1610612736);
               func_73734_a(var25 - 2, var19 - 1, var20, var19, 1342177280);
               var4.func_78276_b(var21, var25 + var7 / 2 - var4.func_78256_a(var21) / 2, var19 - var4.field_78288_b, 553648127);
            }
         }
      }
   }

   private void func_110327_a(int var1, int var2) {
      boolean var3 = this.field_73839_d.field_71439_g.field_70172_ad / 3 % 2 == 1;
      if (this.field_73839_d.field_71439_g.field_70172_ad < 10) {
         var3 = false;
      }

      int var4 = MathHelper.func_76123_f(this.field_73839_d.field_71439_g.func_110143_aJ());
      int var5 = MathHelper.func_76123_f(this.field_73839_d.field_71439_g.field_70735_aL);
      this.field_73842_c.setSeed((long)(this.field_73837_f * 312871));
      boolean var6 = false;
      FoodStats var7 = this.field_73839_d.field_71439_g.func_71024_bL();
      int var8 = var7.func_75116_a();
      int var9 = var7.func_75120_b();
      IAttributeInstance var10 = this.field_73839_d.field_71439_g.func_110148_a(SharedMonsterAttributes.field_111267_a);
      int var11 = var1 / 2 - 91;
      int var12 = var1 / 2 + 91;
      int var13 = var2 - 39;
      float var14 = (float)var10.func_111126_e();
      float var15 = this.field_73839_d.field_71439_g.func_110139_bj();
      int var16 = MathHelper.func_76123_f((var14 + var15) / 2.0F / 10.0F);
      int var17 = Math.max(10 - (var16 - 2), 3);
      int var18 = var13 - (var16 - 1) * var17 - 10;
      float var19 = var15;
      int var20 = this.field_73839_d.field_71439_g.func_70658_aO();
      int var21 = -1;
      if (this.field_73839_d.field_71439_g.func_70644_a(Potion.field_76428_l)) {
         var21 = this.field_73837_f % MathHelper.func_76123_f(var14 + 5.0F);
      }

      this.field_73839_d.field_71424_I.func_76320_a("armor");

      for(int var22 = 0; var22 < 10; ++var22) {
         if (var20 > 0) {
            int var23 = var11 + var22 * 8;
            if (var22 * 2 + 1 < var20) {
               this.func_73729_b(var23, var18, 34, 9, 9, 9);
            }

            if (var22 * 2 + 1 == var20) {
               this.func_73729_b(var23, var18, 25, 9, 9, 9);
            }

            if (var22 * 2 + 1 > var20) {
               this.func_73729_b(var23, var18, 16, 9, 9, 9);
            }
         }
      }

      this.field_73839_d.field_71424_I.func_76318_c("health");

      for(int var34 = MathHelper.func_76123_f((var14 + var15) / 2.0F) - 1; var34 >= 0; --var34) {
         int var36 = 16;
         if (this.field_73839_d.field_71439_g.func_70644_a(Potion.field_76436_u)) {
            var36 += 36;
         } else if (this.field_73839_d.field_71439_g.func_70644_a(Potion.field_82731_v)) {
            var36 += 72;
         }

         byte var24 = 0;
         if (var3) {
            var24 = 1;
         }

         int var25 = MathHelper.func_76123_f((float)(var34 + 1) / 10.0F) - 1;
         int var26 = var11 + var34 % 10 * 8;
         int var27 = var13 - var25 * var17;
         if (var4 <= 4) {
            var27 += this.field_73842_c.nextInt(2);
         }

         if (var34 == var21) {
            var27 -= 2;
         }

         byte var28 = 0;
         if (this.field_73839_d.field_71441_e.func_72912_H().func_76093_s()) {
            var28 = 5;
         }

         this.func_73729_b(var26, var27, 16 + var24 * 9, 9 * var28, 9, 9);
         if (var3) {
            if (var34 * 2 + 1 < var5) {
               this.func_73729_b(var26, var27, var36 + 54, 9 * var28, 9, 9);
            }

            if (var34 * 2 + 1 == var5) {
               this.func_73729_b(var26, var27, var36 + 63, 9 * var28, 9, 9);
            }
         }

         if (var19 > 0.0F) {
            if (var19 == var15 && var15 % 2.0F == 1.0F) {
               this.func_73729_b(var26, var27, var36 + 153, 9 * var28, 9, 9);
            } else {
               this.func_73729_b(var26, var27, var36 + 144, 9 * var28, 9, 9);
            }

            var19 -= 2.0F;
         } else {
            if (var34 * 2 + 1 < var4) {
               this.func_73729_b(var26, var27, var36 + 36, 9 * var28, 9, 9);
            }

            if (var34 * 2 + 1 == var4) {
               this.func_73729_b(var26, var27, var36 + 45, 9 * var28, 9, 9);
            }
         }
      }

      Entity var35 = this.field_73839_d.field_71439_g.field_70154_o;
      if (var35 == null) {
         this.field_73839_d.field_71424_I.func_76318_c("food");

         for(int var37 = 0; var37 < 10; ++var37) {
            int var40 = var13;
            int var43 = 16;
            byte var46 = 0;
            if (this.field_73839_d.field_71439_g.func_70644_a(Potion.field_76438_s)) {
               var43 += 36;
               var46 = 13;
            }

            if (this.field_73839_d.field_71439_g.func_71024_bL().func_75115_e() <= 0.0F && this.field_73837_f % (var8 * 3 + 1) == 0) {
               var40 = var13 + (this.field_73842_c.nextInt(3) - 1);
            }

            if (var6) {
               var46 = 1;
            }

            int var49 = var12 - var37 * 8 - 9;
            this.func_73729_b(var49, var40, 16 + var46 * 9, 27, 9, 9);
            if (var6) {
               if (var37 * 2 + 1 < var9) {
                  this.func_73729_b(var49, var40, var43 + 54, 27, 9, 9);
               }

               if (var37 * 2 + 1 == var9) {
                  this.func_73729_b(var49, var40, var43 + 63, 27, 9, 9);
               }
            }

            if (var37 * 2 + 1 < var8) {
               this.func_73729_b(var49, var40, var43 + 36, 27, 9, 9);
            }

            if (var37 * 2 + 1 == var8) {
               this.func_73729_b(var49, var40, var43 + 45, 27, 9, 9);
            }
         }
      } else if (var35 instanceof EntityLivingBase) {
         this.field_73839_d.field_71424_I.func_76318_c("mountHealth");
         EntityLivingBase var38 = (EntityLivingBase)var35;
         int var41 = (int)Math.ceil((double)var38.func_110143_aJ());
         float var44 = var38.func_110138_aP();
         int var47 = (int)(var44 + 0.5F) / 2;
         if (var47 > 30) {
            var47 = 30;
         }

         int var50 = var13;

         for(int var51 = 0; var47 > 0; var51 += 20) {
            int var29 = Math.min(var47, 10);
            var47 -= var29;

            for(int var30 = 0; var30 < var29; ++var30) {
               byte var31 = 52;
               byte var32 = 0;
               if (var6) {
                  var32 = 1;
               }

               int var33 = var12 - var30 * 8 - 9;
               this.func_73729_b(var33, var50, var31 + var32 * 9, 9, 9, 9);
               if (var30 * 2 + 1 + var51 < var41) {
                  this.func_73729_b(var33, var50, var31 + 36, 9, 9, 9);
               }

               if (var30 * 2 + 1 + var51 == var41) {
                  this.func_73729_b(var33, var50, var31 + 45, 9, 9, 9);
               }
            }

            var50 -= 10;
         }
      }

      this.field_73839_d.field_71424_I.func_76318_c("air");
      if (this.field_73839_d.field_71439_g.func_70055_a(Material.field_151586_h)) {
         int var39 = this.field_73839_d.field_71439_g.func_70086_ai();
         int var42 = MathHelper.func_76143_f((double)(var39 - 2) * 10.0 / 300.0);
         int var45 = MathHelper.func_76143_f((double)var39 * 10.0 / 300.0) - var42;

         for(int var48 = 0; var48 < var42 + var45; ++var48) {
            if (var48 < var42) {
               this.func_73729_b(var12 - var48 * 8 - 9, var18, 16, 18, 9, 9);
            } else {
               this.func_73729_b(var12 - var48 * 8 - 9, var18, 25, 18, 9, 9);
            }
         }
      }

      this.field_73839_d.field_71424_I.func_76319_b();
   }

   private void func_73828_d() {
      if (BossStatus.field_82827_c != null && BossStatus.field_82826_b > 0) {
         --BossStatus.field_82826_b;
         FontRenderer var1 = this.field_73839_d.field_71466_p;
         ScaledResolution var2 = new ScaledResolution(this.field_73839_d, this.field_73839_d.field_71443_c, this.field_73839_d.field_71440_d);
         int var3 = var2.func_78326_a();
         short var4 = 182;
         int var5 = var3 / 2 - var4 / 2;
         int var6 = (int)(BossStatus.field_82828_a * (float)(var4 + 1));
         byte var7 = 12;
         this.func_73729_b(var5, var7, 0, 74, var4, 5);
         this.func_73729_b(var5, var7, 0, 74, var4, 5);
         if (var6 > 0) {
            this.func_73729_b(var5, var7, 0, 79, var6, 5);
         }

         String var8 = BossStatus.field_82827_c;
         var1.func_78261_a(var8, var3 / 2 - var1.func_78256_a(var8) / 2, var7 - 10, 16777215);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_73839_d.func_110434_K().func_110577_a(field_110324_m);
      }
   }

   private void func_73836_a(int var1, int var2) {
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      OpenGlHelper.func_148821_a(770, 771, 1, 0);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(3008);
      this.field_73839_d.func_110434_K().func_110577_a(field_110328_d);
      Tessellator var3 = Tessellator.field_78398_a;
      var3.func_78382_b();
      var3.func_78374_a(0.0, (double)var2, -90.0, 0.0, 1.0);
      var3.func_78374_a((double)var1, (double)var2, -90.0, 1.0, 1.0);
      var3.func_78374_a((double)var1, 0.0, -90.0, 1.0, 0.0);
      var3.func_78374_a(0.0, 0.0, -90.0, 0.0, 0.0);
      var3.func_78381_a();
      GL11.glDepthMask(true);
      GL11.glEnable(2929);
      GL11.glEnable(3008);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void func_73829_a(float var1, int var2, int var3) {
      var1 = 1.0F - var1;
      if (var1 < 0.0F) {
         var1 = 0.0F;
      }

      if (var1 > 1.0F) {
         var1 = 1.0F;
      }

      this.field_73843_a = (float)((double)this.field_73843_a + (double)(var1 - this.field_73843_a) * 0.01);
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      OpenGlHelper.func_148821_a(0, 769, 1, 0);
      GL11.glColor4f(this.field_73843_a, this.field_73843_a, this.field_73843_a, 1.0F);
      this.field_73839_d.func_110434_K().func_110577_a(field_110329_b);
      Tessellator var4 = Tessellator.field_78398_a;
      var4.func_78382_b();
      var4.func_78374_a(0.0, (double)var3, -90.0, 0.0, 1.0);
      var4.func_78374_a((double)var2, (double)var3, -90.0, 1.0, 1.0);
      var4.func_78374_a((double)var2, 0.0, -90.0, 1.0, 0.0);
      var4.func_78374_a(0.0, 0.0, -90.0, 0.0, 0.0);
      var4.func_78381_a();
      GL11.glDepthMask(true);
      GL11.glEnable(2929);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      OpenGlHelper.func_148821_a(770, 771, 1, 0);
   }

   private void func_130015_b(float var1, int var2, int var3) {
      if (var1 < 1.0F) {
         var1 *= var1;
         var1 *= var1;
         var1 = var1 * 0.8F + 0.2F;
      }

      GL11.glDisable(3008);
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      OpenGlHelper.func_148821_a(770, 771, 1, 0);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, var1);
      IIcon var4 = Blocks.field_150427_aO.func_149733_h(1);
      this.field_73839_d.func_110434_K().func_110577_a(TextureMap.field_110575_b);
      float var5 = var4.func_94209_e();
      float var6 = var4.func_94206_g();
      float var7 = var4.func_94212_f();
      float var8 = var4.func_94210_h();
      Tessellator var9 = Tessellator.field_78398_a;
      var9.func_78382_b();
      var9.func_78374_a(0.0, (double)var3, -90.0, (double)var5, (double)var8);
      var9.func_78374_a((double)var2, (double)var3, -90.0, (double)var7, (double)var8);
      var9.func_78374_a((double)var2, 0.0, -90.0, (double)var7, (double)var6);
      var9.func_78374_a(0.0, 0.0, -90.0, (double)var5, (double)var6);
      var9.func_78381_a();
      GL11.glDepthMask(true);
      GL11.glEnable(2929);
      GL11.glEnable(3008);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void func_73832_a(int var1, int var2, int var3, float var4) {
      ItemStack var5 = this.field_73839_d.field_71439_g.field_71071_by.field_70462_a[var1];
      if (var5 != null) {
         float var6 = (float)var5.field_77992_b - var4;
         if (var6 > 0.0F) {
            GL11.glPushMatrix();
            float var7 = 1.0F + var6 / 5.0F;
            GL11.glTranslatef((float)(var2 + 8), (float)(var3 + 12), 0.0F);
            GL11.glScalef(1.0F / var7, (var7 + 1.0F) / 2.0F, 1.0F);
            GL11.glTranslatef((float)(-(var2 + 8)), (float)(-(var3 + 12)), 0.0F);
         }

         field_73841_b.func_82406_b(this.field_73839_d.field_71466_p, this.field_73839_d.func_110434_K(), var5, var2, var3);
         if (var6 > 0.0F) {
            GL11.glPopMatrix();
         }

         field_73841_b.func_77021_b(this.field_73839_d.field_71466_p, this.field_73839_d.func_110434_K(), var5, var2, var3);
      }
   }

   public void func_73831_a() {
      if (this.field_73845_h > 0) {
         --this.field_73845_h;
      }

      ++this.field_73837_f;
      this.field_152127_m.func_152439_a();
      if (this.field_73839_d.field_71439_g != null) {
         ItemStack var1 = this.field_73839_d.field_71439_g.field_71071_by.func_70448_g();
         if (var1 == null) {
            this.field_92017_k = 0;
         } else if (this.field_92016_l != null
            && var1.func_77973_b() == this.field_92016_l.func_77973_b()
            && ItemStack.func_77970_a(var1, this.field_92016_l)
            && (var1.func_77984_f() || var1.func_77960_j() == this.field_92016_l.func_77960_j())) {
            if (this.field_92017_k > 0) {
               --this.field_92017_k;
            }
         } else {
            this.field_92017_k = 40;
         }

         this.field_92016_l = var1;
      }
   }

   public void func_73833_a(String var1) {
      this.func_110326_a(I18n.func_135052_a("record.nowPlaying", var1), true);
   }

   public void func_110326_a(String var1, boolean var2) {
      this.field_73838_g = var1;
      this.field_73845_h = 60;
      this.field_73844_j = var2;
   }

   public GuiNewChat func_146158_b() {
      return this.field_73840_e;
   }

   public int func_73834_c() {
      return this.field_73837_f;
   }
}

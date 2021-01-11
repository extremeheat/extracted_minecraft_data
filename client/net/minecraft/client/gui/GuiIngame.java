package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.border.WorldBorder;

public class GuiIngame extends Gui {
   private static final ResourceLocation field_110329_b = new ResourceLocation("textures/misc/vignette.png");
   private static final ResourceLocation field_110330_c = new ResourceLocation("textures/gui/widgets.png");
   private static final ResourceLocation field_110328_d = new ResourceLocation("textures/misc/pumpkinblur.png");
   private final Random field_73842_c = new Random();
   private final Minecraft field_73839_d;
   private final RenderItem field_73841_b;
   private final GuiNewChat field_73840_e;
   private final GuiStreamIndicator field_152127_m;
   private int field_73837_f;
   private String field_73838_g = "";
   private int field_73845_h;
   private boolean field_73844_j;
   public float field_73843_a = 1.0F;
   private int field_92017_k;
   private ItemStack field_92016_l;
   private final GuiOverlayDebug field_175198_t;
   private final GuiSpectator field_175197_u;
   private final GuiPlayerTabOverlay field_175196_v;
   private int field_175195_w;
   private String field_175201_x = "";
   private String field_175200_y = "";
   private int field_175199_z;
   private int field_175192_A;
   private int field_175193_B;
   private int field_175194_C = 0;
   private int field_175189_D = 0;
   private long field_175190_E = 0L;
   private long field_175191_F = 0L;

   public GuiIngame(Minecraft var1) {
      super();
      this.field_73839_d = var1;
      this.field_73841_b = var1.func_175599_af();
      this.field_175198_t = new GuiOverlayDebug(var1);
      this.field_175197_u = new GuiSpectator(var1);
      this.field_73840_e = new GuiNewChat(var1);
      this.field_152127_m = new GuiStreamIndicator(var1);
      this.field_175196_v = new GuiPlayerTabOverlay(var1, this);
      this.func_175177_a();
   }

   public void func_175177_a() {
      this.field_175199_z = 10;
      this.field_175192_A = 70;
      this.field_175193_B = 20;
   }

   public void func_175180_a(float var1) {
      ScaledResolution var2 = new ScaledResolution(this.field_73839_d);
      int var3 = var2.func_78326_a();
      int var4 = var2.func_78328_b();
      this.field_73839_d.field_71460_t.func_78478_c();
      GlStateManager.func_179147_l();
      if (Minecraft.func_71375_t()) {
         this.func_180480_a(this.field_73839_d.field_71439_g.func_70013_c(var1), var2);
      } else {
         GlStateManager.func_179120_a(770, 771, 1, 0);
      }

      ItemStack var5 = this.field_73839_d.field_71439_g.field_71071_by.func_70440_f(3);
      if (this.field_73839_d.field_71474_y.field_74320_O == 0 && var5 != null && var5.func_77973_b() == Item.func_150898_a(Blocks.field_150423_aK)) {
         this.func_180476_e(var2);
      }

      if (!this.field_73839_d.field_71439_g.func_70644_a(Potion.field_76431_k)) {
         float var6 = this.field_73839_d.field_71439_g.field_71080_cy + (this.field_73839_d.field_71439_g.field_71086_bY - this.field_73839_d.field_71439_g.field_71080_cy) * var1;
         if (var6 > 0.0F) {
            this.func_180474_b(var6, var2);
         }
      }

      if (this.field_73839_d.field_71442_b.func_78747_a()) {
         this.field_175197_u.func_175264_a(var2, var1);
      } else {
         this.func_180479_a(var2, var1);
      }

      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_73839_d.func_110434_K().func_110577_a(field_110324_m);
      GlStateManager.func_179147_l();
      if (this.func_175183_b()) {
         GlStateManager.func_179120_a(775, 769, 1, 0);
         GlStateManager.func_179141_d();
         this.func_73729_b(var3 / 2 - 7, var4 / 2 - 7, 0, 0, 16, 16);
      }

      GlStateManager.func_179120_a(770, 771, 1, 0);
      this.field_73839_d.field_71424_I.func_76320_a("bossHealth");
      this.func_73828_d();
      this.field_73839_d.field_71424_I.func_76319_b();
      if (this.field_73839_d.field_71442_b.func_78755_b()) {
         this.func_180477_d(var2);
      }

      GlStateManager.func_179084_k();
      float var7;
      int var8;
      int var11;
      if (this.field_73839_d.field_71439_g.func_71060_bI() > 0) {
         this.field_73839_d.field_71424_I.func_76320_a("sleep");
         GlStateManager.func_179097_i();
         GlStateManager.func_179118_c();
         var11 = this.field_73839_d.field_71439_g.func_71060_bI();
         var7 = (float)var11 / 100.0F;
         if (var7 > 1.0F) {
            var7 = 1.0F - (float)(var11 - 100) / 10.0F;
         }

         var8 = (int)(220.0F * var7) << 24 | 1052704;
         func_73734_a(0, 0, var3, var4, var8);
         GlStateManager.func_179141_d();
         GlStateManager.func_179126_j();
         this.field_73839_d.field_71424_I.func_76319_b();
      }

      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      var11 = var3 / 2 - 91;
      if (this.field_73839_d.field_71439_g.func_110317_t()) {
         this.func_175186_a(var2, var11);
      } else if (this.field_73839_d.field_71442_b.func_78763_f()) {
         this.func_175176_b(var2, var11);
      }

      if (this.field_73839_d.field_71474_y.field_92117_D && !this.field_73839_d.field_71442_b.func_78747_a()) {
         this.func_181551_a(var2);
      } else if (this.field_73839_d.field_71439_g.func_175149_v()) {
         this.field_175197_u.func_175263_a(var2);
      }

      if (this.field_73839_d.func_71355_q()) {
         this.func_175185_b(var2);
      }

      if (this.field_73839_d.field_71474_y.field_74330_P) {
         this.field_175198_t.func_175237_a(var2);
      }

      int var9;
      if (this.field_73845_h > 0) {
         this.field_73839_d.field_71424_I.func_76320_a("overlayMessage");
         var7 = (float)this.field_73845_h - var1;
         var8 = (int)(var7 * 255.0F / 20.0F);
         if (var8 > 255) {
            var8 = 255;
         }

         if (var8 > 8) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b((float)(var3 / 2), (float)(var4 - 68), 0.0F);
            GlStateManager.func_179147_l();
            GlStateManager.func_179120_a(770, 771, 1, 0);
            var9 = 16777215;
            if (this.field_73844_j) {
               var9 = MathHelper.func_181758_c(var7 / 50.0F, 0.7F, 0.6F) & 16777215;
            }

            this.func_175179_f().func_78276_b(this.field_73838_g, -this.func_175179_f().func_78256_a(this.field_73838_g) / 2, -4, var9 + (var8 << 24 & -16777216));
            GlStateManager.func_179084_k();
            GlStateManager.func_179121_F();
         }

         this.field_73839_d.field_71424_I.func_76319_b();
      }

      if (this.field_175195_w > 0) {
         this.field_73839_d.field_71424_I.func_76320_a("titleAndSubtitle");
         var7 = (float)this.field_175195_w - var1;
         var8 = 255;
         if (this.field_175195_w > this.field_175193_B + this.field_175192_A) {
            float var13 = (float)(this.field_175199_z + this.field_175192_A + this.field_175193_B) - var7;
            var8 = (int)(var13 * 255.0F / (float)this.field_175199_z);
         }

         if (this.field_175195_w <= this.field_175193_B) {
            var8 = (int)(var7 * 255.0F / (float)this.field_175193_B);
         }

         var8 = MathHelper.func_76125_a(var8, 0, 255);
         if (var8 > 8) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b((float)(var3 / 2), (float)(var4 / 2), 0.0F);
            GlStateManager.func_179147_l();
            GlStateManager.func_179120_a(770, 771, 1, 0);
            GlStateManager.func_179094_E();
            GlStateManager.func_179152_a(4.0F, 4.0F, 4.0F);
            var9 = var8 << 24 & -16777216;
            this.func_175179_f().func_175065_a(this.field_175201_x, (float)(-this.func_175179_f().func_78256_a(this.field_175201_x) / 2), -10.0F, 16777215 | var9, true);
            GlStateManager.func_179121_F();
            GlStateManager.func_179094_E();
            GlStateManager.func_179152_a(2.0F, 2.0F, 2.0F);
            this.func_175179_f().func_175065_a(this.field_175200_y, (float)(-this.func_175179_f().func_78256_a(this.field_175200_y) / 2), 5.0F, 16777215 | var9, true);
            GlStateManager.func_179121_F();
            GlStateManager.func_179084_k();
            GlStateManager.func_179121_F();
         }

         this.field_73839_d.field_71424_I.func_76319_b();
      }

      Scoreboard var12 = this.field_73839_d.field_71441_e.func_96441_U();
      ScoreObjective var16 = null;
      ScorePlayerTeam var15 = var12.func_96509_i(this.field_73839_d.field_71439_g.func_70005_c_());
      if (var15 != null) {
         int var10 = var15.func_178775_l().func_175746_b();
         if (var10 >= 0) {
            var16 = var12.func_96539_a(3 + var10);
         }
      }

      ScoreObjective var14 = var16 != null ? var16 : var12.func_96539_a(1);
      if (var14 != null) {
         this.func_180475_a(var14, var2);
      }

      GlStateManager.func_179147_l();
      GlStateManager.func_179120_a(770, 771, 1, 0);
      GlStateManager.func_179118_c();
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b(0.0F, (float)(var4 - 48), 0.0F);
      this.field_73839_d.field_71424_I.func_76320_a("chat");
      this.field_73840_e.func_146230_a(this.field_73837_f);
      this.field_73839_d.field_71424_I.func_76319_b();
      GlStateManager.func_179121_F();
      var14 = var12.func_96539_a(0);
      if (this.field_73839_d.field_71474_y.field_74321_H.func_151470_d() && (!this.field_73839_d.func_71387_A() || this.field_73839_d.field_71439_g.field_71174_a.func_175106_d().size() > 1 || var14 != null)) {
         this.field_175196_v.func_175246_a(true);
         this.field_175196_v.func_175249_a(var3, var12, var14);
      } else {
         this.field_175196_v.func_175246_a(false);
      }

      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179140_f();
      GlStateManager.func_179141_d();
   }

   protected void func_180479_a(ScaledResolution var1, float var2) {
      if (this.field_73839_d.func_175606_aa() instanceof EntityPlayer) {
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_73839_d.func_110434_K().func_110577_a(field_110330_c);
         EntityPlayer var3 = (EntityPlayer)this.field_73839_d.func_175606_aa();
         int var4 = var1.func_78326_a() / 2;
         float var5 = this.field_73735_i;
         this.field_73735_i = -90.0F;
         this.func_73729_b(var4 - 91, var1.func_78328_b() - 22, 0, 0, 182, 22);
         this.func_73729_b(var4 - 91 - 1 + var3.field_71071_by.field_70461_c * 20, var1.func_78328_b() - 22 - 1, 0, 22, 24, 22);
         this.field_73735_i = var5;
         GlStateManager.func_179091_B();
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 771, 1, 0);
         RenderHelper.func_74520_c();

         for(int var6 = 0; var6 < 9; ++var6) {
            int var7 = var1.func_78326_a() / 2 - 90 + var6 * 20 + 2;
            int var8 = var1.func_78328_b() - 16 - 3;
            this.func_175184_a(var6, var7, var8, var2, var3);
         }

         RenderHelper.func_74518_a();
         GlStateManager.func_179101_C();
         GlStateManager.func_179084_k();
      }
   }

   public void func_175186_a(ScaledResolution var1, int var2) {
      this.field_73839_d.field_71424_I.func_76320_a("jumpBar");
      this.field_73839_d.func_110434_K().func_110577_a(Gui.field_110324_m);
      float var3 = this.field_73839_d.field_71439_g.func_110319_bJ();
      short var4 = 182;
      int var5 = (int)(var3 * (float)(var4 + 1));
      int var6 = var1.func_78328_b() - 32 + 3;
      this.func_73729_b(var2, var6, 0, 84, var4, 5);
      if (var5 > 0) {
         this.func_73729_b(var2, var6, 0, 89, var5, 5);
      }

      this.field_73839_d.field_71424_I.func_76319_b();
   }

   public void func_175176_b(ScaledResolution var1, int var2) {
      this.field_73839_d.field_71424_I.func_76320_a("expBar");
      this.field_73839_d.func_110434_K().func_110577_a(Gui.field_110324_m);
      int var3 = this.field_73839_d.field_71439_g.func_71050_bK();
      int var6;
      if (var3 > 0) {
         short var4 = 182;
         int var5 = (int)(this.field_73839_d.field_71439_g.field_71106_cc * (float)(var4 + 1));
         var6 = var1.func_78328_b() - 32 + 3;
         this.func_73729_b(var2, var6, 0, 64, var4, 5);
         if (var5 > 0) {
            this.func_73729_b(var2, var6, 0, 69, var5, 5);
         }
      }

      this.field_73839_d.field_71424_I.func_76319_b();
      if (this.field_73839_d.field_71439_g.field_71068_ca > 0) {
         this.field_73839_d.field_71424_I.func_76320_a("expLevel");
         int var9 = 8453920;
         String var10 = "" + this.field_73839_d.field_71439_g.field_71068_ca;
         var6 = (var1.func_78326_a() - this.func_175179_f().func_78256_a(var10)) / 2;
         int var7 = var1.func_78328_b() - 31 - 4;
         boolean var8 = false;
         this.func_175179_f().func_78276_b(var10, var6 + 1, var7, 0);
         this.func_175179_f().func_78276_b(var10, var6 - 1, var7, 0);
         this.func_175179_f().func_78276_b(var10, var6, var7 + 1, 0);
         this.func_175179_f().func_78276_b(var10, var6, var7 - 1, 0);
         this.func_175179_f().func_78276_b(var10, var6, var7, var9);
         this.field_73839_d.field_71424_I.func_76319_b();
      }

   }

   public void func_181551_a(ScaledResolution var1) {
      this.field_73839_d.field_71424_I.func_76320_a("selectedItemName");
      if (this.field_92017_k > 0 && this.field_92016_l != null) {
         String var2 = this.field_92016_l.func_82833_r();
         if (this.field_92016_l.func_82837_s()) {
            var2 = EnumChatFormatting.ITALIC + var2;
         }

         int var3 = (var1.func_78326_a() - this.func_175179_f().func_78256_a(var2)) / 2;
         int var4 = var1.func_78328_b() - 59;
         if (!this.field_73839_d.field_71442_b.func_78755_b()) {
            var4 += 14;
         }

         int var5 = (int)((float)this.field_92017_k * 256.0F / 10.0F);
         if (var5 > 255) {
            var5 = 255;
         }

         if (var5 > 0) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179147_l();
            GlStateManager.func_179120_a(770, 771, 1, 0);
            this.func_175179_f().func_175063_a(var2, (float)var3, (float)var4, 16777215 + (var5 << 24));
            GlStateManager.func_179084_k();
            GlStateManager.func_179121_F();
         }
      }

      this.field_73839_d.field_71424_I.func_76319_b();
   }

   public void func_175185_b(ScaledResolution var1) {
      this.field_73839_d.field_71424_I.func_76320_a("demo");
      String var2 = "";
      if (this.field_73839_d.field_71441_e.func_82737_E() >= 120500L) {
         var2 = I18n.func_135052_a("demo.demoExpired");
      } else {
         var2 = I18n.func_135052_a("demo.remainingTime", StringUtils.func_76337_a((int)(120500L - this.field_73839_d.field_71441_e.func_82737_E())));
      }

      int var3 = this.func_175179_f().func_78256_a(var2);
      this.func_175179_f().func_175063_a(var2, (float)(var1.func_78326_a() - var3 - 10), 5.0F, 16777215);
      this.field_73839_d.field_71424_I.func_76319_b();
   }

   protected boolean func_175183_b() {
      if (this.field_73839_d.field_71474_y.field_74330_P && !this.field_73839_d.field_71439_g.func_175140_cp() && !this.field_73839_d.field_71474_y.field_178879_v) {
         return false;
      } else if (this.field_73839_d.field_71442_b.func_78747_a()) {
         if (this.field_73839_d.field_147125_j != null) {
            return true;
         } else {
            if (this.field_73839_d.field_71476_x != null && this.field_73839_d.field_71476_x.field_72313_a == MovingObjectPosition.MovingObjectType.BLOCK) {
               BlockPos var1 = this.field_73839_d.field_71476_x.func_178782_a();
               if (this.field_73839_d.field_71441_e.func_175625_s(var1) instanceof IInventory) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return true;
      }
   }

   public void func_180478_c(ScaledResolution var1) {
      this.field_152127_m.func_152437_a(var1.func_78326_a() - 10, 10);
   }

   private void func_180475_a(ScoreObjective var1, ScaledResolution var2) {
      Scoreboard var3 = var1.func_96682_a();
      Collection var4 = var3.func_96534_i(var1);
      ArrayList var5 = Lists.newArrayList(Iterables.filter(var4, new Predicate<Score>() {
         public boolean apply(Score var1) {
            return var1.func_96653_e() != null && !var1.func_96653_e().startsWith("#");
         }

         // $FF: synthetic method
         public boolean apply(Object var1) {
            return this.apply((Score)var1);
         }
      }));
      ArrayList var21;
      if (var5.size() > 15) {
         var21 = Lists.newArrayList(Iterables.skip(var5, var4.size() - 15));
      } else {
         var21 = var5;
      }

      int var6 = this.func_175179_f().func_78256_a(var1.func_96678_d());

      String var10;
      for(Iterator var7 = var21.iterator(); var7.hasNext(); var6 = Math.max(var6, this.func_175179_f().func_78256_a(var10))) {
         Score var8 = (Score)var7.next();
         ScorePlayerTeam var9 = var3.func_96509_i(var8.func_96653_e());
         var10 = ScorePlayerTeam.func_96667_a(var9, var8.func_96653_e()) + ": " + EnumChatFormatting.RED + var8.func_96652_c();
      }

      int var22 = var21.size() * this.func_175179_f().field_78288_b;
      int var23 = var2.func_78328_b() / 2 + var22 / 3;
      byte var24 = 3;
      int var25 = var2.func_78326_a() - var6 - var24;
      int var11 = 0;
      Iterator var12 = var21.iterator();

      while(var12.hasNext()) {
         Score var13 = (Score)var12.next();
         ++var11;
         ScorePlayerTeam var14 = var3.func_96509_i(var13.func_96653_e());
         String var15 = ScorePlayerTeam.func_96667_a(var14, var13.func_96653_e());
         String var16 = EnumChatFormatting.RED + "" + var13.func_96652_c();
         int var18 = var23 - var11 * this.func_175179_f().field_78288_b;
         int var19 = var2.func_78326_a() - var24 + 2;
         func_73734_a(var25 - 2, var18, var19, var18 + this.func_175179_f().field_78288_b, 1342177280);
         this.func_175179_f().func_78276_b(var15, var25, var18, 553648127);
         this.func_175179_f().func_78276_b(var16, var19 - this.func_175179_f().func_78256_a(var16), var18, 553648127);
         if (var11 == var21.size()) {
            String var20 = var1.func_96678_d();
            func_73734_a(var25 - 2, var18 - this.func_175179_f().field_78288_b - 1, var19, var18 - 1, 1610612736);
            func_73734_a(var25 - 2, var18 - 1, var19, var18, 1342177280);
            this.func_175179_f().func_78276_b(var20, var25 + var6 / 2 - this.func_175179_f().func_78256_a(var20) / 2, var18 - this.func_175179_f().field_78288_b, 553648127);
         }
      }

   }

   private void func_180477_d(ScaledResolution var1) {
      if (this.field_73839_d.func_175606_aa() instanceof EntityPlayer) {
         EntityPlayer var2 = (EntityPlayer)this.field_73839_d.func_175606_aa();
         int var3 = MathHelper.func_76123_f(var2.func_110143_aJ());
         boolean var4 = this.field_175191_F > (long)this.field_73837_f && (this.field_175191_F - (long)this.field_73837_f) / 3L % 2L == 1L;
         if (var3 < this.field_175194_C && var2.field_70172_ad > 0) {
            this.field_175190_E = Minecraft.func_71386_F();
            this.field_175191_F = (long)(this.field_73837_f + 20);
         } else if (var3 > this.field_175194_C && var2.field_70172_ad > 0) {
            this.field_175190_E = Minecraft.func_71386_F();
            this.field_175191_F = (long)(this.field_73837_f + 10);
         }

         if (Minecraft.func_71386_F() - this.field_175190_E > 1000L) {
            this.field_175194_C = var3;
            this.field_175189_D = var3;
            this.field_175190_E = Minecraft.func_71386_F();
         }

         this.field_175194_C = var3;
         int var5 = this.field_175189_D;
         this.field_73842_c.setSeed((long)(this.field_73837_f * 312871));
         boolean var6 = false;
         FoodStats var7 = var2.func_71024_bL();
         int var8 = var7.func_75116_a();
         int var9 = var7.func_75120_b();
         IAttributeInstance var10 = var2.func_110148_a(SharedMonsterAttributes.field_111267_a);
         int var11 = var1.func_78326_a() / 2 - 91;
         int var12 = var1.func_78326_a() / 2 + 91;
         int var13 = var1.func_78328_b() - 39;
         float var14 = (float)var10.func_111126_e();
         float var15 = var2.func_110139_bj();
         int var16 = MathHelper.func_76123_f((var14 + var15) / 2.0F / 10.0F);
         int var17 = Math.max(10 - (var16 - 2), 3);
         int var18 = var13 - (var16 - 1) * var17 - 10;
         float var19 = var15;
         int var20 = var2.func_70658_aO();
         int var21 = -1;
         if (var2.func_70644_a(Potion.field_76428_l)) {
            var21 = this.field_73837_f % MathHelper.func_76123_f(var14 + 5.0F);
         }

         this.field_73839_d.field_71424_I.func_76320_a("armor");

         int var22;
         int var23;
         for(var22 = 0; var22 < 10; ++var22) {
            if (var20 > 0) {
               var23 = var11 + var22 * 8;
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

         int var25;
         int var26;
         int var27;
         for(var22 = MathHelper.func_76123_f((var14 + var15) / 2.0F) - 1; var22 >= 0; --var22) {
            var23 = 16;
            if (var2.func_70644_a(Potion.field_76436_u)) {
               var23 += 36;
            } else if (var2.func_70644_a(Potion.field_82731_v)) {
               var23 += 72;
            }

            byte var24 = 0;
            if (var4) {
               var24 = 1;
            }

            var25 = MathHelper.func_76123_f((float)(var22 + 1) / 10.0F) - 1;
            var26 = var11 + var22 % 10 * 8;
            var27 = var13 - var25 * var17;
            if (var3 <= 4) {
               var27 += this.field_73842_c.nextInt(2);
            }

            if (var22 == var21) {
               var27 -= 2;
            }

            byte var28 = 0;
            if (var2.field_70170_p.func_72912_H().func_76093_s()) {
               var28 = 5;
            }

            this.func_73729_b(var26, var27, 16 + var24 * 9, 9 * var28, 9, 9);
            if (var4) {
               if (var22 * 2 + 1 < var5) {
                  this.func_73729_b(var26, var27, var23 + 54, 9 * var28, 9, 9);
               }

               if (var22 * 2 + 1 == var5) {
                  this.func_73729_b(var26, var27, var23 + 63, 9 * var28, 9, 9);
               }
            }

            if (var19 > 0.0F) {
               if (var19 == var15 && var15 % 2.0F == 1.0F) {
                  this.func_73729_b(var26, var27, var23 + 153, 9 * var28, 9, 9);
               } else {
                  this.func_73729_b(var26, var27, var23 + 144, 9 * var28, 9, 9);
               }

               var19 -= 2.0F;
            } else {
               if (var22 * 2 + 1 < var3) {
                  this.func_73729_b(var26, var27, var23 + 36, 9 * var28, 9, 9);
               }

               if (var22 * 2 + 1 == var3) {
                  this.func_73729_b(var26, var27, var23 + 45, 9 * var28, 9, 9);
               }
            }
         }

         Entity var34 = var2.field_70154_o;
         int var35;
         if (var34 == null) {
            this.field_73839_d.field_71424_I.func_76318_c("food");

            for(var23 = 0; var23 < 10; ++var23) {
               var35 = var13;
               var25 = 16;
               byte var36 = 0;
               if (var2.func_70644_a(Potion.field_76438_s)) {
                  var25 += 36;
                  var36 = 13;
               }

               if (var2.func_71024_bL().func_75115_e() <= 0.0F && this.field_73837_f % (var8 * 3 + 1) == 0) {
                  var35 = var13 + (this.field_73842_c.nextInt(3) - 1);
               }

               if (var6) {
                  var36 = 1;
               }

               var27 = var12 - var23 * 8 - 9;
               this.func_73729_b(var27, var35, 16 + var36 * 9, 27, 9, 9);
               if (var6) {
                  if (var23 * 2 + 1 < var9) {
                     this.func_73729_b(var27, var35, var25 + 54, 27, 9, 9);
                  }

                  if (var23 * 2 + 1 == var9) {
                     this.func_73729_b(var27, var35, var25 + 63, 27, 9, 9);
                  }
               }

               if (var23 * 2 + 1 < var8) {
                  this.func_73729_b(var27, var35, var25 + 36, 27, 9, 9);
               }

               if (var23 * 2 + 1 == var8) {
                  this.func_73729_b(var27, var35, var25 + 45, 27, 9, 9);
               }
            }
         } else if (var34 instanceof EntityLivingBase) {
            this.field_73839_d.field_71424_I.func_76318_c("mountHealth");
            EntityLivingBase var37 = (EntityLivingBase)var34;
            var35 = (int)Math.ceil((double)var37.func_110143_aJ());
            float var38 = var37.func_110138_aP();
            var26 = (int)(var38 + 0.5F) / 2;
            if (var26 > 30) {
               var26 = 30;
            }

            var27 = var13;

            for(int var39 = 0; var26 > 0; var39 += 20) {
               int var29 = Math.min(var26, 10);
               var26 -= var29;

               for(int var30 = 0; var30 < var29; ++var30) {
                  byte var31 = 52;
                  byte var32 = 0;
                  if (var6) {
                     var32 = 1;
                  }

                  int var33 = var12 - var30 * 8 - 9;
                  this.func_73729_b(var33, var27, var31 + var32 * 9, 9, 9, 9);
                  if (var30 * 2 + 1 + var39 < var35) {
                     this.func_73729_b(var33, var27, var31 + 36, 9, 9, 9);
                  }

                  if (var30 * 2 + 1 + var39 == var35) {
                     this.func_73729_b(var33, var27, var31 + 45, 9, 9, 9);
                  }
               }

               var27 -= 10;
            }
         }

         this.field_73839_d.field_71424_I.func_76318_c("air");
         if (var2.func_70055_a(Material.field_151586_h)) {
            var23 = this.field_73839_d.field_71439_g.func_70086_ai();
            var35 = MathHelper.func_76143_f((double)(var23 - 2) * 10.0D / 300.0D);
            var25 = MathHelper.func_76143_f((double)var23 * 10.0D / 300.0D) - var35;

            for(var26 = 0; var26 < var35 + var25; ++var26) {
               if (var26 < var35) {
                  this.func_73729_b(var12 - var26 * 8 - 9, var18, 16, 18, 9, 9);
               } else {
                  this.func_73729_b(var12 - var26 * 8 - 9, var18, 25, 18, 9, 9);
               }
            }
         }

         this.field_73839_d.field_71424_I.func_76319_b();
      }
   }

   private void func_73828_d() {
      if (BossStatus.field_82827_c != null && BossStatus.field_82826_b > 0) {
         --BossStatus.field_82826_b;
         FontRenderer var1 = this.field_73839_d.field_71466_p;
         ScaledResolution var2 = new ScaledResolution(this.field_73839_d);
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
         this.func_175179_f().func_175063_a(var8, (float)(var3 / 2 - this.func_175179_f().func_78256_a(var8) / 2), (float)(var7 - 10), 16777215);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_73839_d.func_110434_K().func_110577_a(field_110324_m);
      }
   }

   private void func_180476_e(ScaledResolution var1) {
      GlStateManager.func_179097_i();
      GlStateManager.func_179132_a(false);
      GlStateManager.func_179120_a(770, 771, 1, 0);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179118_c();
      this.field_73839_d.func_110434_K().func_110577_a(field_110328_d);
      Tessellator var2 = Tessellator.func_178181_a();
      WorldRenderer var3 = var2.func_178180_c();
      var3.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      var3.func_181662_b(0.0D, (double)var1.func_78328_b(), -90.0D).func_181673_a(0.0D, 1.0D).func_181675_d();
      var3.func_181662_b((double)var1.func_78326_a(), (double)var1.func_78328_b(), -90.0D).func_181673_a(1.0D, 1.0D).func_181675_d();
      var3.func_181662_b((double)var1.func_78326_a(), 0.0D, -90.0D).func_181673_a(1.0D, 0.0D).func_181675_d();
      var3.func_181662_b(0.0D, 0.0D, -90.0D).func_181673_a(0.0D, 0.0D).func_181675_d();
      var2.func_78381_a();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179126_j();
      GlStateManager.func_179141_d();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void func_180480_a(float var1, ScaledResolution var2) {
      var1 = 1.0F - var1;
      var1 = MathHelper.func_76131_a(var1, 0.0F, 1.0F);
      WorldBorder var3 = this.field_73839_d.field_71441_e.func_175723_af();
      float var4 = (float)var3.func_177745_a(this.field_73839_d.field_71439_g);
      double var5 = Math.min(var3.func_177749_o() * (double)var3.func_177740_p() * 1000.0D, Math.abs(var3.func_177751_j() - var3.func_177741_h()));
      double var7 = Math.max((double)var3.func_177748_q(), var5);
      if ((double)var4 < var7) {
         var4 = 1.0F - (float)((double)var4 / var7);
      } else {
         var4 = 0.0F;
      }

      this.field_73843_a = (float)((double)this.field_73843_a + (double)(var1 - this.field_73843_a) * 0.01D);
      GlStateManager.func_179097_i();
      GlStateManager.func_179132_a(false);
      GlStateManager.func_179120_a(0, 769, 1, 0);
      if (var4 > 0.0F) {
         GlStateManager.func_179131_c(0.0F, var4, var4, 1.0F);
      } else {
         GlStateManager.func_179131_c(this.field_73843_a, this.field_73843_a, this.field_73843_a, 1.0F);
      }

      this.field_73839_d.func_110434_K().func_110577_a(field_110329_b);
      Tessellator var9 = Tessellator.func_178181_a();
      WorldRenderer var10 = var9.func_178180_c();
      var10.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      var10.func_181662_b(0.0D, (double)var2.func_78328_b(), -90.0D).func_181673_a(0.0D, 1.0D).func_181675_d();
      var10.func_181662_b((double)var2.func_78326_a(), (double)var2.func_78328_b(), -90.0D).func_181673_a(1.0D, 1.0D).func_181675_d();
      var10.func_181662_b((double)var2.func_78326_a(), 0.0D, -90.0D).func_181673_a(1.0D, 0.0D).func_181675_d();
      var10.func_181662_b(0.0D, 0.0D, -90.0D).func_181673_a(0.0D, 0.0D).func_181675_d();
      var9.func_78381_a();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179126_j();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179120_a(770, 771, 1, 0);
   }

   private void func_180474_b(float var1, ScaledResolution var2) {
      if (var1 < 1.0F) {
         var1 *= var1;
         var1 *= var1;
         var1 = var1 * 0.8F + 0.2F;
      }

      GlStateManager.func_179118_c();
      GlStateManager.func_179097_i();
      GlStateManager.func_179132_a(false);
      GlStateManager.func_179120_a(770, 771, 1, 0);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, var1);
      this.field_73839_d.func_110434_K().func_110577_a(TextureMap.field_110575_b);
      TextureAtlasSprite var3 = this.field_73839_d.func_175602_ab().func_175023_a().func_178122_a(Blocks.field_150427_aO.func_176223_P());
      float var4 = var3.func_94209_e();
      float var5 = var3.func_94206_g();
      float var6 = var3.func_94212_f();
      float var7 = var3.func_94210_h();
      Tessellator var8 = Tessellator.func_178181_a();
      WorldRenderer var9 = var8.func_178180_c();
      var9.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      var9.func_181662_b(0.0D, (double)var2.func_78328_b(), -90.0D).func_181673_a((double)var4, (double)var7).func_181675_d();
      var9.func_181662_b((double)var2.func_78326_a(), (double)var2.func_78328_b(), -90.0D).func_181673_a((double)var6, (double)var7).func_181675_d();
      var9.func_181662_b((double)var2.func_78326_a(), 0.0D, -90.0D).func_181673_a((double)var6, (double)var5).func_181675_d();
      var9.func_181662_b(0.0D, 0.0D, -90.0D).func_181673_a((double)var4, (double)var5).func_181675_d();
      var8.func_78381_a();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179126_j();
      GlStateManager.func_179141_d();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void func_175184_a(int var1, int var2, int var3, float var4, EntityPlayer var5) {
      ItemStack var6 = var5.field_71071_by.field_70462_a[var1];
      if (var6 != null) {
         float var7 = (float)var6.field_77992_b - var4;
         if (var7 > 0.0F) {
            GlStateManager.func_179094_E();
            float var8 = 1.0F + var7 / 5.0F;
            GlStateManager.func_179109_b((float)(var2 + 8), (float)(var3 + 12), 0.0F);
            GlStateManager.func_179152_a(1.0F / var8, (var8 + 1.0F) / 2.0F, 1.0F);
            GlStateManager.func_179109_b((float)(-(var2 + 8)), (float)(-(var3 + 12)), 0.0F);
         }

         this.field_73841_b.func_180450_b(var6, var2, var3);
         if (var7 > 0.0F) {
            GlStateManager.func_179121_F();
         }

         this.field_73841_b.func_175030_a(this.field_73839_d.field_71466_p, var6, var2, var3);
      }
   }

   public void func_73831_a() {
      if (this.field_73845_h > 0) {
         --this.field_73845_h;
      }

      if (this.field_175195_w > 0) {
         --this.field_175195_w;
         if (this.field_175195_w <= 0) {
            this.field_175201_x = "";
            this.field_175200_y = "";
         }
      }

      ++this.field_73837_f;
      this.field_152127_m.func_152439_a();
      if (this.field_73839_d.field_71439_g != null) {
         ItemStack var1 = this.field_73839_d.field_71439_g.field_71071_by.func_70448_g();
         if (var1 == null) {
            this.field_92017_k = 0;
         } else if (this.field_92016_l == null || var1.func_77973_b() != this.field_92016_l.func_77973_b() || !ItemStack.func_77970_a(var1, this.field_92016_l) || !var1.func_77984_f() && var1.func_77960_j() != this.field_92016_l.func_77960_j()) {
            this.field_92017_k = 40;
         } else if (this.field_92017_k > 0) {
            --this.field_92017_k;
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

   public void func_175178_a(String var1, String var2, int var3, int var4, int var5) {
      if (var1 == null && var2 == null && var3 < 0 && var4 < 0 && var5 < 0) {
         this.field_175201_x = "";
         this.field_175200_y = "";
         this.field_175195_w = 0;
      } else if (var1 != null) {
         this.field_175201_x = var1;
         this.field_175195_w = this.field_175199_z + this.field_175192_A + this.field_175193_B;
      } else if (var2 != null) {
         this.field_175200_y = var2;
      } else {
         if (var3 >= 0) {
            this.field_175199_z = var3;
         }

         if (var4 >= 0) {
            this.field_175192_A = var4;
         }

         if (var5 >= 0) {
            this.field_175193_B = var5;
         }

         if (this.field_175195_w > 0) {
            this.field_175195_w = this.field_175199_z + this.field_175192_A + this.field_175193_B;
         }

      }
   }

   public void func_175188_a(IChatComponent var1, boolean var2) {
      this.func_110326_a(var1.func_150260_c(), var2);
   }

   public GuiNewChat func_146158_b() {
      return this.field_73840_e;
   }

   public int func_73834_c() {
      return this.field_73837_f;
   }

   public FontRenderer func_175179_f() {
      return this.field_73839_d.field_71466_p;
   }

   public GuiSpectator func_175187_g() {
      return this.field_175197_u;
   }

   public GuiPlayerTabOverlay func_175181_h() {
      return this.field_175196_v;
   }

   public void func_181029_i() {
      this.field_175196_v.func_181030_a();
   }
}

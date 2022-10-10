package net.minecraft.client.gui;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.IChatListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.chat.NormalChatListener;
import net.minecraft.client.gui.chat.OverlayChatListener;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.border.WorldBorder;

public class GuiIngame extends Gui {
   private static final ResourceLocation field_110329_b = new ResourceLocation("textures/misc/vignette.png");
   private static final ResourceLocation field_110330_c = new ResourceLocation("textures/gui/widgets.png");
   private static final ResourceLocation field_110328_d = new ResourceLocation("textures/misc/pumpkinblur.png");
   private final Random field_73842_c = new Random();
   private final Minecraft field_73839_d;
   private final ItemRenderer field_73841_b;
   private final GuiNewChat field_73840_e;
   private int field_73837_f;
   private String field_73838_g = "";
   private int field_73845_h;
   private boolean field_73844_j;
   public float field_73843_a = 1.0F;
   private int field_92017_k;
   private ItemStack field_92016_l;
   private final GuiOverlayDebug field_175198_t;
   private final GuiSubtitleOverlay field_184049_t;
   private final GuiSpectator field_175197_u;
   private final GuiPlayerTabOverlay field_175196_v;
   private final GuiBossOverlay field_184050_w;
   private int field_175195_w;
   private String field_175201_x;
   private String field_175200_y;
   private int field_175199_z;
   private int field_175192_A;
   private int field_175193_B;
   private int field_175194_C;
   private int field_175189_D;
   private long field_175190_E;
   private long field_175191_F;
   private int field_194811_H;
   private int field_194812_I;
   private final Map<ChatType, List<IChatListener>> field_191743_I;

   public GuiIngame(Minecraft var1) {
      super();
      this.field_92016_l = ItemStack.field_190927_a;
      this.field_175201_x = "";
      this.field_175200_y = "";
      this.field_191743_I = Maps.newHashMap();
      this.field_73839_d = var1;
      this.field_73841_b = var1.func_175599_af();
      this.field_175198_t = new GuiOverlayDebug(var1);
      this.field_175197_u = new GuiSpectator(var1);
      this.field_73840_e = new GuiNewChat(var1);
      this.field_175196_v = new GuiPlayerTabOverlay(var1, this);
      this.field_184050_w = new GuiBossOverlay(var1);
      this.field_184049_t = new GuiSubtitleOverlay(var1);
      ChatType[] var2 = ChatType.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ChatType var5 = var2[var4];
         this.field_191743_I.put(var5, Lists.newArrayList());
      }

      NarratorChatListener var6 = NarratorChatListener.field_193643_a;
      ((List)this.field_191743_I.get(ChatType.CHAT)).add(new NormalChatListener(var1));
      ((List)this.field_191743_I.get(ChatType.CHAT)).add(var6);
      ((List)this.field_191743_I.get(ChatType.SYSTEM)).add(new NormalChatListener(var1));
      ((List)this.field_191743_I.get(ChatType.SYSTEM)).add(var6);
      ((List)this.field_191743_I.get(ChatType.GAME_INFO)).add(new OverlayChatListener(var1));
      this.func_175177_a();
   }

   public void func_175177_a() {
      this.field_175199_z = 10;
      this.field_175192_A = 70;
      this.field_175193_B = 20;
   }

   public void func_175180_a(float var1) {
      this.field_194811_H = this.field_73839_d.field_195558_d.func_198107_o();
      this.field_194812_I = this.field_73839_d.field_195558_d.func_198087_p();
      FontRenderer var2 = this.func_175179_f();
      GlStateManager.func_179147_l();
      if (Minecraft.func_71375_t()) {
         this.func_212303_b(this.field_73839_d.func_175606_aa());
      } else {
         GlStateManager.func_179126_j();
         GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      }

      ItemStack var3 = this.field_73839_d.field_71439_g.field_71071_by.func_70440_f(3);
      if (this.field_73839_d.field_71474_y.field_74320_O == 0 && var3.func_77973_b() == Blocks.field_196625_cS.func_199767_j()) {
         this.func_194808_p();
      }

      float var4;
      if (!this.field_73839_d.field_71439_g.func_70644_a(MobEffects.field_76431_k)) {
         var4 = this.field_73839_d.field_71439_g.field_71080_cy + (this.field_73839_d.field_71439_g.field_71086_bY - this.field_73839_d.field_71439_g.field_71080_cy) * var1;
         if (var4 > 0.0F) {
            this.func_194805_e(var4);
         }
      }

      if (this.field_73839_d.field_71442_b.func_178889_l() == GameType.SPECTATOR) {
         this.field_175197_u.func_195622_a(var1);
      } else if (!this.field_73839_d.field_71474_y.field_74319_N) {
         this.func_194806_b(var1);
      }

      if (!this.field_73839_d.field_71474_y.field_74319_N) {
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_73839_d.func_110434_K().func_110577_a(field_110324_m);
         GlStateManager.func_179147_l();
         GlStateManager.func_179141_d();
         this.func_194798_c(var1);
         GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         this.field_73839_d.field_71424_I.func_76320_a("bossHealth");
         this.field_184050_w.func_184051_a();
         this.field_73839_d.field_71424_I.func_76319_b();
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_73839_d.func_110434_K().func_110577_a(field_110324_m);
         if (this.field_73839_d.field_71442_b.func_78755_b()) {
            this.func_194807_n();
         }

         this.func_194799_o();
         GlStateManager.func_179084_k();
         int var8 = this.field_194811_H / 2 - 91;
         if (this.field_73839_d.field_71439_g.func_110317_t()) {
            this.func_194803_a(var8);
         } else if (this.field_73839_d.field_71442_b.func_78763_f()) {
            this.func_194804_b(var8);
         }

         if (this.field_73839_d.field_71474_y.field_92117_D && this.field_73839_d.field_71442_b.func_178889_l() != GameType.SPECTATOR) {
            this.func_194801_c();
         } else if (this.field_73839_d.field_71439_g.func_175149_v()) {
            this.field_175197_u.func_195623_a();
         }
      }

      int var6;
      if (this.field_73839_d.field_71439_g.func_71060_bI() > 0) {
         this.field_73839_d.field_71424_I.func_76320_a("sleep");
         GlStateManager.func_179097_i();
         GlStateManager.func_179118_c();
         var4 = (float)this.field_73839_d.field_71439_g.func_71060_bI();
         float var5 = var4 / 100.0F;
         if (var5 > 1.0F) {
            var5 = 1.0F - (var4 - 100.0F) / 10.0F;
         }

         var6 = (int)(220.0F * var5) << 24 | 1052704;
         func_73734_a(0, 0, this.field_194811_H, this.field_194812_I, var6);
         GlStateManager.func_179141_d();
         GlStateManager.func_179126_j();
         this.field_73839_d.field_71424_I.func_76319_b();
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      }

      if (this.field_73839_d.func_71355_q()) {
         this.func_194810_d();
      }

      this.func_194809_b();
      if (this.field_73839_d.field_71474_y.field_74330_P) {
         this.field_175198_t.func_194818_a();
      }

      if (!this.field_73839_d.field_71474_y.field_74319_N) {
         int var10;
         if (this.field_73845_h > 0) {
            this.field_73839_d.field_71424_I.func_76320_a("overlayMessage");
            var4 = (float)this.field_73845_h - var1;
            var10 = (int)(var4 * 255.0F / 20.0F);
            if (var10 > 255) {
               var10 = 255;
            }

            if (var10 > 8) {
               GlStateManager.func_179094_E();
               GlStateManager.func_179109_b((float)(this.field_194811_H / 2), (float)(this.field_194812_I - 68), 0.0F);
               GlStateManager.func_179147_l();
               GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
               var6 = 16777215;
               if (this.field_73844_j) {
                  var6 = MathHelper.func_181758_c(var4 / 50.0F, 0.7F, 0.6F) & 16777215;
               }

               var2.func_211126_b(this.field_73838_g, (float)(-var2.func_78256_a(this.field_73838_g) / 2), -4.0F, var6 + (var10 << 24 & -16777216));
               GlStateManager.func_179084_k();
               GlStateManager.func_179121_F();
            }

            this.field_73839_d.field_71424_I.func_76319_b();
         }

         if (this.field_175195_w > 0) {
            this.field_73839_d.field_71424_I.func_76320_a("titleAndSubtitle");
            var4 = (float)this.field_175195_w - var1;
            var10 = 255;
            if (this.field_175195_w > this.field_175193_B + this.field_175192_A) {
               float var11 = (float)(this.field_175199_z + this.field_175192_A + this.field_175193_B) - var4;
               var10 = (int)(var11 * 255.0F / (float)this.field_175199_z);
            }

            if (this.field_175195_w <= this.field_175193_B) {
               var10 = (int)(var4 * 255.0F / (float)this.field_175193_B);
            }

            var10 = MathHelper.func_76125_a(var10, 0, 255);
            if (var10 > 8) {
               GlStateManager.func_179094_E();
               GlStateManager.func_179109_b((float)(this.field_194811_H / 2), (float)(this.field_194812_I / 2), 0.0F);
               GlStateManager.func_179147_l();
               GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
               GlStateManager.func_179094_E();
               GlStateManager.func_179152_a(4.0F, 4.0F, 4.0F);
               var6 = var10 << 24 & -16777216;
               var2.func_175063_a(this.field_175201_x, (float)(-var2.func_78256_a(this.field_175201_x) / 2), -10.0F, 16777215 | var6);
               GlStateManager.func_179121_F();
               GlStateManager.func_179094_E();
               GlStateManager.func_179152_a(2.0F, 2.0F, 2.0F);
               var2.func_175063_a(this.field_175200_y, (float)(-var2.func_78256_a(this.field_175200_y) / 2), 5.0F, 16777215 | var6);
               GlStateManager.func_179121_F();
               GlStateManager.func_179084_k();
               GlStateManager.func_179121_F();
            }

            this.field_73839_d.field_71424_I.func_76319_b();
         }

         this.field_184049_t.func_195620_a();
         Scoreboard var9 = this.field_73839_d.field_71441_e.func_96441_U();
         ScoreObjective var14 = null;
         ScorePlayerTeam var13 = var9.func_96509_i(this.field_73839_d.field_71439_g.func_195047_I_());
         if (var13 != null) {
            int var7 = var13.func_178775_l().func_175746_b();
            if (var7 >= 0) {
               var14 = var9.func_96539_a(3 + var7);
            }
         }

         ScoreObjective var12 = var14 != null ? var14 : var9.func_96539_a(1);
         if (var12 != null) {
            this.func_194802_a(var12);
         }

         GlStateManager.func_179147_l();
         GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.func_179118_c();
         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b(0.0F, (float)(this.field_194812_I - 48), 0.0F);
         this.field_73839_d.field_71424_I.func_76320_a("chat");
         this.field_73840_e.func_146230_a(this.field_73837_f);
         this.field_73839_d.field_71424_I.func_76319_b();
         GlStateManager.func_179121_F();
         var12 = var9.func_96539_a(0);
         if (this.field_73839_d.field_71474_y.field_74321_H.func_151470_d() && (!this.field_73839_d.func_71387_A() || this.field_73839_d.field_71439_g.field_71174_a.func_175106_d().size() > 1 || var12 != null)) {
            this.field_175196_v.func_175246_a(true);
            this.field_175196_v.func_175249_a(this.field_194811_H, var9, var12);
         } else {
            this.field_175196_v.func_175246_a(false);
         }
      }

      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179140_f();
      GlStateManager.func_179141_d();
   }

   private void func_194798_c(float var1) {
      GameSettings var2 = this.field_73839_d.field_71474_y;
      if (var2.field_74320_O == 0) {
         if (this.field_73839_d.field_71442_b.func_178889_l() == GameType.SPECTATOR && this.field_73839_d.field_147125_j == null) {
            label67: {
               RayTraceResult var3 = this.field_73839_d.field_71476_x;
               if (var3 != null && var3.field_72313_a == RayTraceResult.Type.BLOCK) {
                  BlockPos var4 = var3.func_178782_a();
                  if (this.field_73839_d.field_71441_e.func_180495_p(var4).func_177230_c().func_149716_u() && this.field_73839_d.field_71441_e.func_175625_s(var4) instanceof IInventory) {
                     break label67;
                  }

                  return;
               }

               return;
            }
         }

         if (var2.field_74330_P && !var2.field_74319_N && !this.field_73839_d.field_71439_g.func_175140_cp() && !var2.field_178879_v) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b((float)(this.field_194811_H / 2), (float)(this.field_194812_I / 2), this.field_73735_i);
            Entity var10 = this.field_73839_d.func_175606_aa();
            GlStateManager.func_179114_b(var10.field_70127_C + (var10.field_70125_A - var10.field_70127_C) * var1, -1.0F, 0.0F, 0.0F);
            GlStateManager.func_179114_b(var10.field_70126_B + (var10.field_70177_z - var10.field_70126_B) * var1, 0.0F, 1.0F, 0.0F);
            GlStateManager.func_179152_a(-1.0F, -1.0F, -1.0F);
            OpenGlHelper.func_188785_m(10);
            GlStateManager.func_179121_F();
         } else {
            GlStateManager.func_187428_a(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            boolean var9 = true;
            this.func_175174_a((float)this.field_194811_H / 2.0F - 7.5F, (float)this.field_194812_I / 2.0F - 7.5F, 0, 0, 15, 15);
            if (this.field_73839_d.field_71474_y.field_186716_M == 1) {
               float var11 = this.field_73839_d.field_71439_g.func_184825_o(0.0F);
               boolean var5 = false;
               if (this.field_73839_d.field_147125_j != null && this.field_73839_d.field_147125_j instanceof EntityLivingBase && var11 >= 1.0F) {
                  var5 = this.field_73839_d.field_71439_g.func_184818_cX() > 5.0F;
                  var5 &= this.field_73839_d.field_147125_j.func_70089_S();
               }

               int var6 = this.field_194812_I / 2 - 7 + 16;
               int var7 = this.field_194811_H / 2 - 8;
               if (var5) {
                  this.func_73729_b(var7, var6, 68, 94, 16, 16);
               } else if (var11 < 1.0F) {
                  int var8 = (int)(var11 * 17.0F);
                  this.func_73729_b(var7, var6, 36, 94, 16, 4);
                  this.func_73729_b(var7, var6, 52, 94, var8, 4);
               }
            }
         }

      }
   }

   protected void func_194809_b() {
      Collection var1 = this.field_73839_d.field_71439_g.func_70651_bq();
      if (!var1.isEmpty()) {
         this.field_73839_d.func_110434_K().func_110577_a(GuiContainer.field_147001_a);
         GlStateManager.func_179147_l();
         int var2 = 0;
         int var3 = 0;
         Iterator var4 = Ordering.natural().reverse().sortedCopy(var1).iterator();

         while(var4.hasNext()) {
            PotionEffect var5 = (PotionEffect)var4.next();
            Potion var6 = var5.func_188419_a();
            if (var6.func_76400_d() && var5.func_205348_f()) {
               int var7 = this.field_194811_H;
               int var8 = 1;
               if (this.field_73839_d.func_71355_q()) {
                  var8 += 15;
               }

               int var9 = var6.func_76392_e();
               if (var6.func_188408_i()) {
                  ++var2;
                  var7 -= 25 * var2;
               } else {
                  ++var3;
                  var7 -= 25 * var3;
                  var8 += 26;
               }

               GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
               float var10 = 1.0F;
               int var11;
               if (var5.func_82720_e()) {
                  this.func_73729_b(var7, var8, 165, 166, 24, 24);
               } else {
                  this.func_73729_b(var7, var8, 141, 166, 24, 24);
                  if (var5.func_76459_b() <= 200) {
                     var11 = 10 - var5.func_76459_b() / 20;
                     var10 = MathHelper.func_76131_a((float)var5.func_76459_b() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + MathHelper.func_76134_b((float)var5.func_76459_b() * 3.1415927F / 5.0F) * MathHelper.func_76131_a((float)var11 / 10.0F * 0.25F, 0.0F, 0.25F);
                  }
               }

               GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, var10);
               var11 = var9 % 12;
               int var12 = var9 / 12;
               this.func_73729_b(var7 + 3, var8 + 3, var11 * 18, 198 + var12 * 18, 18, 18);
            }
         }

      }
   }

   protected void func_194806_b(float var1) {
      EntityPlayer var2 = this.func_212304_m();
      if (var2 != null) {
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_73839_d.func_110434_K().func_110577_a(field_110330_c);
         ItemStack var3 = var2.func_184592_cb();
         EnumHandSide var4 = var2.func_184591_cq().func_188468_a();
         int var5 = this.field_194811_H / 2;
         float var6 = this.field_73735_i;
         boolean var7 = true;
         boolean var8 = true;
         this.field_73735_i = -90.0F;
         this.func_73729_b(var5 - 91, this.field_194812_I - 22, 0, 0, 182, 22);
         this.func_73729_b(var5 - 91 - 1 + var2.field_71071_by.field_70461_c * 20, this.field_194812_I - 22 - 1, 0, 22, 24, 22);
         if (!var3.func_190926_b()) {
            if (var4 == EnumHandSide.LEFT) {
               this.func_73729_b(var5 - 91 - 29, this.field_194812_I - 23, 24, 22, 29, 24);
            } else {
               this.func_73729_b(var5 + 91, this.field_194812_I - 23, 53, 22, 29, 24);
            }
         }

         this.field_73735_i = var6;
         GlStateManager.func_179091_B();
         GlStateManager.func_179147_l();
         GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         RenderHelper.func_74520_c();

         int var9;
         int var10;
         int var11;
         for(var9 = 0; var9 < 9; ++var9) {
            var10 = var5 - 90 + var9 * 20 + 2;
            var11 = this.field_194812_I - 16 - 3;
            this.func_184044_a(var10, var11, var1, var2, (ItemStack)var2.field_71071_by.field_70462_a.get(var9));
         }

         if (!var3.func_190926_b()) {
            var9 = this.field_194812_I - 16 - 3;
            if (var4 == EnumHandSide.LEFT) {
               this.func_184044_a(var5 - 91 - 26, var9, var1, var2, var3);
            } else {
               this.func_184044_a(var5 + 91 + 10, var9, var1, var2, var3);
            }
         }

         if (this.field_73839_d.field_71474_y.field_186716_M == 2) {
            float var13 = this.field_73839_d.field_71439_g.func_184825_o(0.0F);
            if (var13 < 1.0F) {
               var10 = this.field_194812_I - 20;
               var11 = var5 + 91 + 6;
               if (var4 == EnumHandSide.RIGHT) {
                  var11 = var5 - 91 - 22;
               }

               this.field_73839_d.func_110434_K().func_110577_a(Gui.field_110324_m);
               int var12 = (int)(var13 * 19.0F);
               GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
               this.func_73729_b(var11, var10, 0, 94, 18, 18);
               this.func_73729_b(var11, var10 + 18 - var12, 18, 112 - var12, 18, var12);
            }
         }

         RenderHelper.func_74518_a();
         GlStateManager.func_179101_C();
         GlStateManager.func_179084_k();
      }
   }

   public void func_194803_a(int var1) {
      this.field_73839_d.field_71424_I.func_76320_a("jumpBar");
      this.field_73839_d.func_110434_K().func_110577_a(Gui.field_110324_m);
      float var2 = this.field_73839_d.field_71439_g.func_110319_bJ();
      boolean var3 = true;
      int var4 = (int)(var2 * 183.0F);
      int var5 = this.field_194812_I - 32 + 3;
      this.func_73729_b(var1, var5, 0, 84, 182, 5);
      if (var4 > 0) {
         this.func_73729_b(var1, var5, 0, 89, var4, 5);
      }

      this.field_73839_d.field_71424_I.func_76319_b();
   }

   public void func_194804_b(int var1) {
      this.field_73839_d.field_71424_I.func_76320_a("expBar");
      this.field_73839_d.func_110434_K().func_110577_a(Gui.field_110324_m);
      int var2 = this.field_73839_d.field_71439_g.func_71050_bK();
      int var4;
      int var5;
      if (var2 > 0) {
         boolean var3 = true;
         var4 = (int)(this.field_73839_d.field_71439_g.field_71106_cc * 183.0F);
         var5 = this.field_194812_I - 32 + 3;
         this.func_73729_b(var1, var5, 0, 64, 182, 5);
         if (var4 > 0) {
            this.func_73729_b(var1, var5, 0, 69, var4, 5);
         }
      }

      this.field_73839_d.field_71424_I.func_76319_b();
      if (this.field_73839_d.field_71439_g.field_71068_ca > 0) {
         this.field_73839_d.field_71424_I.func_76320_a("expLevel");
         String var6 = "" + this.field_73839_d.field_71439_g.field_71068_ca;
         var4 = (this.field_194811_H - this.func_175179_f().func_78256_a(var6)) / 2;
         var5 = this.field_194812_I - 31 - 4;
         this.func_175179_f().func_211126_b(var6, (float)(var4 + 1), (float)var5, 0);
         this.func_175179_f().func_211126_b(var6, (float)(var4 - 1), (float)var5, 0);
         this.func_175179_f().func_211126_b(var6, (float)var4, (float)(var5 + 1), 0);
         this.func_175179_f().func_211126_b(var6, (float)var4, (float)(var5 - 1), 0);
         this.func_175179_f().func_211126_b(var6, (float)var4, (float)var5, 8453920);
         this.field_73839_d.field_71424_I.func_76319_b();
      }

   }

   public void func_194801_c() {
      this.field_73839_d.field_71424_I.func_76320_a("selectedItemName");
      if (this.field_92017_k > 0 && !this.field_92016_l.func_190926_b()) {
         ITextComponent var1 = (new TextComponentString("")).func_150257_a(this.field_92016_l.func_200301_q()).func_211708_a(this.field_92016_l.func_77953_t().field_77937_e);
         if (this.field_92016_l.func_82837_s()) {
            var1.func_211708_a(TextFormatting.ITALIC);
         }

         String var2 = var1.func_150254_d();
         int var3 = (this.field_194811_H - this.func_175179_f().func_78256_a(var2)) / 2;
         int var4 = this.field_194812_I - 59;
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
            GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            this.func_175179_f().func_175063_a(var2, (float)var3, (float)var4, 16777215 + (var5 << 24));
            GlStateManager.func_179084_k();
            GlStateManager.func_179121_F();
         }
      }

      this.field_73839_d.field_71424_I.func_76319_b();
   }

   public void func_194810_d() {
      this.field_73839_d.field_71424_I.func_76320_a("demo");
      String var1;
      if (this.field_73839_d.field_71441_e.func_82737_E() >= 120500L) {
         var1 = I18n.func_135052_a("demo.demoExpired");
      } else {
         var1 = I18n.func_135052_a("demo.remainingTime", StringUtils.func_76337_a((int)(120500L - this.field_73839_d.field_71441_e.func_82737_E())));
      }

      int var2 = this.func_175179_f().func_78256_a(var1);
      this.func_175179_f().func_175063_a(var1, (float)(this.field_194811_H - var2 - 10), 5.0F, 16777215);
      this.field_73839_d.field_71424_I.func_76319_b();
   }

   private void func_194802_a(ScoreObjective var1) {
      Scoreboard var2 = var1.func_96682_a();
      Collection var3 = var2.func_96534_i(var1);
      List var4 = (List)var3.stream().filter((var0) -> {
         return var0.func_96653_e() != null && !var0.func_96653_e().startsWith("#");
      }).collect(Collectors.toList());
      Object var21;
      if (var4.size() > 15) {
         var21 = Lists.newArrayList(Iterables.skip(var4, var3.size() - 15));
      } else {
         var21 = var4;
      }

      String var5 = var1.func_96678_d().func_150254_d();
      int var6 = this.func_175179_f().func_78256_a(var5);
      int var7 = var6;

      String var11;
      for(Iterator var8 = ((Collection)var21).iterator(); var8.hasNext(); var7 = Math.max(var7, this.func_175179_f().func_78256_a(var11))) {
         Score var9 = (Score)var8.next();
         ScorePlayerTeam var10 = var2.func_96509_i(var9.func_96653_e());
         var11 = ScorePlayerTeam.func_200541_a(var10, new TextComponentString(var9.func_96653_e())).func_150254_d() + ": " + TextFormatting.RED + var9.func_96652_c();
      }

      int var22 = ((Collection)var21).size() * this.func_175179_f().field_78288_b;
      int var23 = this.field_194812_I / 2 + var22 / 3;
      boolean var24 = true;
      int var25 = this.field_194811_H - var7 - 3;
      int var12 = 0;
      Iterator var13 = ((Collection)var21).iterator();

      while(var13.hasNext()) {
         Score var14 = (Score)var13.next();
         ++var12;
         ScorePlayerTeam var15 = var2.func_96509_i(var14.func_96653_e());
         String var16 = ScorePlayerTeam.func_200541_a(var15, new TextComponentString(var14.func_96653_e())).func_150254_d();
         String var17 = TextFormatting.RED + "" + var14.func_96652_c();
         int var19 = var23 - var12 * this.func_175179_f().field_78288_b;
         int var20 = this.field_194811_H - 3 + 2;
         func_73734_a(var25 - 2, var19, var20, var19 + this.func_175179_f().field_78288_b, 1342177280);
         this.func_175179_f().func_211126_b(var16, (float)var25, (float)var19, 553648127);
         this.func_175179_f().func_211126_b(var17, (float)(var20 - this.func_175179_f().func_78256_a(var17)), (float)var19, 553648127);
         if (var12 == ((Collection)var21).size()) {
            func_73734_a(var25 - 2, var19 - this.func_175179_f().field_78288_b - 1, var20, var19 - 1, 1610612736);
            func_73734_a(var25 - 2, var19 - 1, var20, var19, 1342177280);
            this.func_175179_f().func_211126_b(var5, (float)(var25 + var7 / 2 - var6 / 2), (float)(var19 - this.func_175179_f().field_78288_b), 553648127);
         }
      }

   }

   private EntityPlayer func_212304_m() {
      return !(this.field_73839_d.func_175606_aa() instanceof EntityPlayer) ? null : (EntityPlayer)this.field_73839_d.func_175606_aa();
   }

   private EntityLivingBase func_212305_n() {
      EntityPlayer var1 = this.func_212304_m();
      if (var1 != null) {
         Entity var2 = var1.func_184187_bx();
         if (var2 == null) {
            return null;
         }

         if (var2 instanceof EntityLivingBase) {
            return (EntityLivingBase)var2;
         }
      }

      return null;
   }

   private int func_212306_a(EntityLivingBase var1) {
      if (var1 != null && var1.func_203003_aK()) {
         float var2 = var1.func_110138_aP();
         int var3 = (int)(var2 + 0.5F) / 2;
         if (var3 > 30) {
            var3 = 30;
         }

         return var3;
      } else {
         return 0;
      }
   }

   private int func_212302_c(int var1) {
      return (int)Math.ceil((double)var1 / 10.0D);
   }

   private void func_194807_n() {
      EntityPlayer var1 = this.func_212304_m();
      if (var1 != null) {
         int var2 = MathHelper.func_76123_f(var1.func_110143_aJ());
         boolean var3 = this.field_175191_F > (long)this.field_73837_f && (this.field_175191_F - (long)this.field_73837_f) / 3L % 2L == 1L;
         long var4 = Util.func_211177_b();
         if (var2 < this.field_175194_C && var1.field_70172_ad > 0) {
            this.field_175190_E = var4;
            this.field_175191_F = (long)(this.field_73837_f + 20);
         } else if (var2 > this.field_175194_C && var1.field_70172_ad > 0) {
            this.field_175190_E = var4;
            this.field_175191_F = (long)(this.field_73837_f + 10);
         }

         if (var4 - this.field_175190_E > 1000L) {
            this.field_175194_C = var2;
            this.field_175189_D = var2;
            this.field_175190_E = var4;
         }

         this.field_175194_C = var2;
         int var6 = this.field_175189_D;
         this.field_73842_c.setSeed((long)(this.field_73837_f * 312871));
         FoodStats var7 = var1.func_71024_bL();
         int var8 = var7.func_75116_a();
         IAttributeInstance var9 = var1.func_110148_a(SharedMonsterAttributes.field_111267_a);
         int var10 = this.field_194811_H / 2 - 91;
         int var11 = this.field_194811_H / 2 + 91;
         int var12 = this.field_194812_I - 39;
         float var13 = (float)var9.func_111126_e();
         int var14 = MathHelper.func_76123_f(var1.func_110139_bj());
         int var15 = MathHelper.func_76123_f((var13 + (float)var14) / 2.0F / 10.0F);
         int var16 = Math.max(10 - (var15 - 2), 3);
         int var17 = var12 - (var15 - 1) * var16 - 10;
         int var18 = var12 - 10;
         int var19 = var14;
         int var20 = var1.func_70658_aO();
         int var21 = -1;
         if (var1.func_70644_a(MobEffects.field_76428_l)) {
            var21 = this.field_73837_f % MathHelper.func_76123_f(var13 + 5.0F);
         }

         this.field_73839_d.field_71424_I.func_76320_a("armor");

         int var22;
         int var23;
         for(var22 = 0; var22 < 10; ++var22) {
            if (var20 > 0) {
               var23 = var10 + var22 * 8;
               if (var22 * 2 + 1 < var20) {
                  this.func_73729_b(var23, var17, 34, 9, 9, 9);
               }

               if (var22 * 2 + 1 == var20) {
                  this.func_73729_b(var23, var17, 25, 9, 9, 9);
               }

               if (var22 * 2 + 1 > var20) {
                  this.func_73729_b(var23, var17, 16, 9, 9, 9);
               }
            }
         }

         this.field_73839_d.field_71424_I.func_76318_c("health");

         int var25;
         int var26;
         int var27;
         for(var22 = MathHelper.func_76123_f((var13 + (float)var14) / 2.0F) - 1; var22 >= 0; --var22) {
            var23 = 16;
            if (var1.func_70644_a(MobEffects.field_76436_u)) {
               var23 += 36;
            } else if (var1.func_70644_a(MobEffects.field_82731_v)) {
               var23 += 72;
            }

            byte var24 = 0;
            if (var3) {
               var24 = 1;
            }

            var25 = MathHelper.func_76123_f((float)(var22 + 1) / 10.0F) - 1;
            var26 = var10 + var22 % 10 * 8;
            var27 = var12 - var25 * var16;
            if (var2 <= 4) {
               var27 += this.field_73842_c.nextInt(2);
            }

            if (var19 <= 0 && var22 == var21) {
               var27 -= 2;
            }

            byte var28 = 0;
            if (var1.field_70170_p.func_72912_H().func_76093_s()) {
               var28 = 5;
            }

            this.func_73729_b(var26, var27, 16 + var24 * 9, 9 * var28, 9, 9);
            if (var3) {
               if (var22 * 2 + 1 < var6) {
                  this.func_73729_b(var26, var27, var23 + 54, 9 * var28, 9, 9);
               }

               if (var22 * 2 + 1 == var6) {
                  this.func_73729_b(var26, var27, var23 + 63, 9 * var28, 9, 9);
               }
            }

            if (var19 > 0) {
               if (var19 == var14 && var14 % 2 == 1) {
                  this.func_73729_b(var26, var27, var23 + 153, 9 * var28, 9, 9);
                  --var19;
               } else {
                  this.func_73729_b(var26, var27, var23 + 144, 9 * var28, 9, 9);
                  var19 -= 2;
               }
            } else {
               if (var22 * 2 + 1 < var2) {
                  this.func_73729_b(var26, var27, var23 + 36, 9 * var28, 9, 9);
               }

               if (var22 * 2 + 1 == var2) {
                  this.func_73729_b(var26, var27, var23 + 45, 9 * var28, 9, 9);
               }
            }
         }

         EntityLivingBase var30 = this.func_212305_n();
         var23 = this.func_212306_a(var30);
         int var31;
         int var33;
         if (var23 == 0) {
            this.field_73839_d.field_71424_I.func_76318_c("food");

            for(var31 = 0; var31 < 10; ++var31) {
               var25 = var12;
               var26 = 16;
               byte var32 = 0;
               if (var1.func_70644_a(MobEffects.field_76438_s)) {
                  var26 += 36;
                  var32 = 13;
               }

               if (var1.func_71024_bL().func_75115_e() <= 0.0F && this.field_73837_f % (var8 * 3 + 1) == 0) {
                  var25 = var12 + (this.field_73842_c.nextInt(3) - 1);
               }

               var33 = var11 - var31 * 8 - 9;
               this.func_73729_b(var33, var25, 16 + var32 * 9, 27, 9, 9);
               if (var31 * 2 + 1 < var8) {
                  this.func_73729_b(var33, var25, var26 + 36, 27, 9, 9);
               }

               if (var31 * 2 + 1 == var8) {
                  this.func_73729_b(var33, var25, var26 + 45, 27, 9, 9);
               }
            }

            var18 -= 10;
         }

         this.field_73839_d.field_71424_I.func_76318_c("air");
         var31 = var1.func_70086_ai();
         var25 = var1.func_205010_bg();
         if (var1.func_208600_a(FluidTags.field_206959_a) || var31 < var25) {
            var26 = this.func_212302_c(var23) - 1;
            var18 -= var26 * 10;
            var27 = MathHelper.func_76143_f((double)(var31 - 2) * 10.0D / (double)var25);
            var33 = MathHelper.func_76143_f((double)var31 * 10.0D / (double)var25) - var27;

            for(int var29 = 0; var29 < var27 + var33; ++var29) {
               if (var29 < var27) {
                  this.func_73729_b(var11 - var29 * 8 - 9, var18, 16, 18, 9, 9);
               } else {
                  this.func_73729_b(var11 - var29 * 8 - 9, var18, 25, 18, 9, 9);
               }
            }
         }

         this.field_73839_d.field_71424_I.func_76319_b();
      }
   }

   private void func_194799_o() {
      EntityLivingBase var1 = this.func_212305_n();
      if (var1 != null) {
         int var2 = this.func_212306_a(var1);
         if (var2 != 0) {
            int var3 = (int)Math.ceil((double)var1.func_110143_aJ());
            this.field_73839_d.field_71424_I.func_76318_c("mountHealth");
            int var4 = this.field_194812_I - 39;
            int var5 = this.field_194811_H / 2 + 91;
            int var6 = var4;
            int var7 = 0;

            for(boolean var8 = false; var2 > 0; var7 += 20) {
               int var9 = Math.min(var2, 10);
               var2 -= var9;

               for(int var10 = 0; var10 < var9; ++var10) {
                  boolean var11 = true;
                  byte var12 = 0;
                  int var13 = var5 - var10 * 8 - 9;
                  this.func_73729_b(var13, var6, 52 + var12 * 9, 9, 9, 9);
                  if (var10 * 2 + 1 + var7 < var3) {
                     this.func_73729_b(var13, var6, 88, 9, 9, 9);
                  }

                  if (var10 * 2 + 1 + var7 == var3) {
                     this.func_73729_b(var13, var6, 97, 9, 9, 9);
                  }
               }

               var6 -= 10;
            }

         }
      }
   }

   private void func_194808_p() {
      GlStateManager.func_179097_i();
      GlStateManager.func_179132_a(false);
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179118_c();
      this.field_73839_d.func_110434_K().func_110577_a(field_110328_d);
      Tessellator var1 = Tessellator.func_178181_a();
      BufferBuilder var2 = var1.func_178180_c();
      var2.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      var2.func_181662_b(0.0D, (double)this.field_194812_I, -90.0D).func_187315_a(0.0D, 1.0D).func_181675_d();
      var2.func_181662_b((double)this.field_194811_H, (double)this.field_194812_I, -90.0D).func_187315_a(1.0D, 1.0D).func_181675_d();
      var2.func_181662_b((double)this.field_194811_H, 0.0D, -90.0D).func_187315_a(1.0D, 0.0D).func_181675_d();
      var2.func_181662_b(0.0D, 0.0D, -90.0D).func_187315_a(0.0D, 0.0D).func_181675_d();
      var1.func_78381_a();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179126_j();
      GlStateManager.func_179141_d();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void func_212307_a(Entity var1) {
      if (var1 != null) {
         float var2 = MathHelper.func_76131_a(1.0F - var1.func_70013_c(), 0.0F, 1.0F);
         this.field_73843_a = (float)((double)this.field_73843_a + (double)(var2 - this.field_73843_a) * 0.01D);
      }
   }

   private void func_212303_b(Entity var1) {
      WorldBorder var2 = this.field_73839_d.field_71441_e.func_175723_af();
      float var3 = (float)var2.func_177745_a(var1);
      double var4 = Math.min(var2.func_177749_o() * (double)var2.func_177740_p() * 1000.0D, Math.abs(var2.func_177751_j() - var2.func_177741_h()));
      double var6 = Math.max((double)var2.func_177748_q(), var4);
      if ((double)var3 < var6) {
         var3 = 1.0F - (float)((double)var3 / var6);
      } else {
         var3 = 0.0F;
      }

      GlStateManager.func_179097_i();
      GlStateManager.func_179132_a(false);
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      if (var3 > 0.0F) {
         GlStateManager.func_179131_c(0.0F, var3, var3, 1.0F);
      } else {
         GlStateManager.func_179131_c(this.field_73843_a, this.field_73843_a, this.field_73843_a, 1.0F);
      }

      this.field_73839_d.func_110434_K().func_110577_a(field_110329_b);
      Tessellator var8 = Tessellator.func_178181_a();
      BufferBuilder var9 = var8.func_178180_c();
      var9.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      var9.func_181662_b(0.0D, (double)this.field_194812_I, -90.0D).func_187315_a(0.0D, 1.0D).func_181675_d();
      var9.func_181662_b((double)this.field_194811_H, (double)this.field_194812_I, -90.0D).func_187315_a(1.0D, 1.0D).func_181675_d();
      var9.func_181662_b((double)this.field_194811_H, 0.0D, -90.0D).func_187315_a(1.0D, 0.0D).func_181675_d();
      var9.func_181662_b(0.0D, 0.0D, -90.0D).func_187315_a(0.0D, 0.0D).func_181675_d();
      var8.func_78381_a();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179126_j();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
   }

   private void func_194805_e(float var1) {
      if (var1 < 1.0F) {
         var1 *= var1;
         var1 *= var1;
         var1 = var1 * 0.8F + 0.2F;
      }

      GlStateManager.func_179118_c();
      GlStateManager.func_179097_i();
      GlStateManager.func_179132_a(false);
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, var1);
      this.field_73839_d.func_110434_K().func_110577_a(TextureMap.field_110575_b);
      TextureAtlasSprite var2 = this.field_73839_d.func_175602_ab().func_175023_a().func_178122_a(Blocks.field_150427_aO.func_176223_P());
      float var3 = var2.func_94209_e();
      float var4 = var2.func_94206_g();
      float var5 = var2.func_94212_f();
      float var6 = var2.func_94210_h();
      Tessellator var7 = Tessellator.func_178181_a();
      BufferBuilder var8 = var7.func_178180_c();
      var8.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      var8.func_181662_b(0.0D, (double)this.field_194812_I, -90.0D).func_187315_a((double)var3, (double)var6).func_181675_d();
      var8.func_181662_b((double)this.field_194811_H, (double)this.field_194812_I, -90.0D).func_187315_a((double)var5, (double)var6).func_181675_d();
      var8.func_181662_b((double)this.field_194811_H, 0.0D, -90.0D).func_187315_a((double)var5, (double)var4).func_181675_d();
      var8.func_181662_b(0.0D, 0.0D, -90.0D).func_187315_a((double)var3, (double)var4).func_181675_d();
      var7.func_78381_a();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179126_j();
      GlStateManager.func_179141_d();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void func_184044_a(int var1, int var2, float var3, EntityPlayer var4, ItemStack var5) {
      if (!var5.func_190926_b()) {
         float var6 = (float)var5.func_190921_D() - var3;
         if (var6 > 0.0F) {
            GlStateManager.func_179094_E();
            float var7 = 1.0F + var6 / 5.0F;
            GlStateManager.func_179109_b((float)(var1 + 8), (float)(var2 + 12), 0.0F);
            GlStateManager.func_179152_a(1.0F / var7, (var7 + 1.0F) / 2.0F, 1.0F);
            GlStateManager.func_179109_b((float)(-(var1 + 8)), (float)(-(var2 + 12)), 0.0F);
         }

         this.field_73841_b.func_184391_a(var4, var5, var1, var2);
         if (var6 > 0.0F) {
            GlStateManager.func_179121_F();
         }

         this.field_73841_b.func_175030_a(this.field_73839_d.field_71466_p, var5, var1, var2);
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
      Entity var1 = this.field_73839_d.func_175606_aa();
      if (var1 != null) {
         this.func_212307_a(var1);
      }

      if (this.field_73839_d.field_71439_g != null) {
         ItemStack var2 = this.field_73839_d.field_71439_g.field_71071_by.func_70448_g();
         if (var2.func_190926_b()) {
            this.field_92017_k = 0;
         } else if (!this.field_92016_l.func_190926_b() && var2.func_77973_b() == this.field_92016_l.func_77973_b() && var2.func_200301_q().equals(this.field_92016_l.func_200301_q())) {
            if (this.field_92017_k > 0) {
               --this.field_92017_k;
            }
         } else {
            this.field_92017_k = 40;
         }

         this.field_92016_l = var2;
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

   public void func_175188_a(ITextComponent var1, boolean var2) {
      this.func_110326_a(var1.getString(), var2);
   }

   public void func_191742_a(ChatType var1, ITextComponent var2) {
      Iterator var3 = ((List)this.field_191743_I.get(var1)).iterator();

      while(var3.hasNext()) {
         IChatListener var4 = (IChatListener)var3.next();
         var4.func_192576_a(var1, var2);
      }

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
      this.field_184050_w.func_184057_b();
      this.field_73839_d.func_193033_an().func_191788_b();
   }

   public GuiBossOverlay func_184046_j() {
      return this.field_184050_w;
   }
}

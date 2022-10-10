package net.minecraft.client.gui;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;

public class GuiPlayerTabOverlay extends Gui {
   private static final Ordering<NetworkPlayerInfo> field_175252_a = Ordering.from(new GuiPlayerTabOverlay.PlayerComparator());
   private final Minecraft field_175250_f;
   private final GuiIngame field_175251_g;
   private ITextComponent field_175255_h;
   private ITextComponent field_175256_i;
   private long field_175253_j;
   private boolean field_175254_k;

   public GuiPlayerTabOverlay(Minecraft var1, GuiIngame var2) {
      super();
      this.field_175250_f = var1;
      this.field_175251_g = var2;
   }

   public ITextComponent func_200262_a(NetworkPlayerInfo var1) {
      return var1.func_178854_k() != null ? var1.func_178854_k() : ScorePlayerTeam.func_200541_a(var1.func_178850_i(), new TextComponentString(var1.func_178845_a().getName()));
   }

   public void func_175246_a(boolean var1) {
      if (var1 && !this.field_175254_k) {
         this.field_175253_j = Util.func_211177_b();
      }

      this.field_175254_k = var1;
   }

   public void func_175249_a(int var1, Scoreboard var2, @Nullable ScoreObjective var3) {
      NetHandlerPlayClient var4 = this.field_175250_f.field_71439_g.field_71174_a;
      List var5 = field_175252_a.sortedCopy(var4.func_175106_d());
      int var6 = 0;
      int var7 = 0;
      Iterator var8 = var5.iterator();

      int var10;
      while(var8.hasNext()) {
         NetworkPlayerInfo var9 = (NetworkPlayerInfo)var8.next();
         var10 = this.field_175250_f.field_71466_p.func_78256_a(this.func_200262_a(var9).func_150254_d());
         var6 = Math.max(var6, var10);
         if (var3 != null && var3.func_199865_f() != ScoreCriteria.RenderType.HEARTS) {
            var10 = this.field_175250_f.field_71466_p.func_78256_a(" " + var2.func_96529_a(var9.func_178845_a().getName(), var3).func_96652_c());
            var7 = Math.max(var7, var10);
         }
      }

      var5 = var5.subList(0, Math.min(var5.size(), 80));
      int var32 = var5.size();
      int var33 = var32;

      for(var10 = 1; var33 > 20; var33 = (var32 + var10 - 1) / var10) {
         ++var10;
      }

      boolean var11 = this.field_175250_f.func_71387_A() || this.field_175250_f.func_147114_u().func_147298_b().func_179292_f();
      int var12;
      if (var3 != null) {
         if (var3.func_199865_f() == ScoreCriteria.RenderType.HEARTS) {
            var12 = 90;
         } else {
            var12 = var7;
         }
      } else {
         var12 = 0;
      }

      int var13 = Math.min(var10 * ((var11 ? 9 : 0) + var6 + var12 + 13), var1 - 50) / var10;
      int var14 = var1 / 2 - (var13 * var10 + (var10 - 1) * 5) / 2;
      int var15 = 10;
      int var16 = var13 * var10 + (var10 - 1) * 5;
      List var17 = null;
      if (this.field_175256_i != null) {
         var17 = this.field_175250_f.field_71466_p.func_78271_c(this.field_175256_i.func_150254_d(), var1 - 50);

         String var19;
         for(Iterator var18 = var17.iterator(); var18.hasNext(); var16 = Math.max(var16, this.field_175250_f.field_71466_p.func_78256_a(var19))) {
            var19 = (String)var18.next();
         }
      }

      List var34 = null;
      String var20;
      Iterator var35;
      if (this.field_175255_h != null) {
         var34 = this.field_175250_f.field_71466_p.func_78271_c(this.field_175255_h.func_150254_d(), var1 - 50);

         for(var35 = var34.iterator(); var35.hasNext(); var16 = Math.max(var16, this.field_175250_f.field_71466_p.func_78256_a(var20))) {
            var20 = (String)var35.next();
         }
      }

      int var21;
      if (var17 != null) {
         func_73734_a(var1 / 2 - var16 / 2 - 1, var15 - 1, var1 / 2 + var16 / 2 + 1, var15 + var17.size() * this.field_175250_f.field_71466_p.field_78288_b, -2147483648);

         for(var35 = var17.iterator(); var35.hasNext(); var15 += this.field_175250_f.field_71466_p.field_78288_b) {
            var20 = (String)var35.next();
            var21 = this.field_175250_f.field_71466_p.func_78256_a(var20);
            this.field_175250_f.field_71466_p.func_175063_a(var20, (float)(var1 / 2 - var21 / 2), (float)var15, -1);
         }

         ++var15;
      }

      func_73734_a(var1 / 2 - var16 / 2 - 1, var15 - 1, var1 / 2 + var16 / 2 + 1, var15 + var33 * 9, -2147483648);

      for(int var36 = 0; var36 < var32; ++var36) {
         int var37 = var36 / var33;
         var21 = var36 % var33;
         int var22 = var14 + var37 * var13 + var37 * 5;
         int var23 = var15 + var21 * 9;
         func_73734_a(var22, var23, var22 + var13, var23 + 8, 553648127);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179141_d();
         GlStateManager.func_179147_l();
         GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         if (var36 < var5.size()) {
            NetworkPlayerInfo var24 = (NetworkPlayerInfo)var5.get(var36);
            GameProfile var25 = var24.func_178845_a();
            int var28;
            if (var11) {
               EntityPlayer var26 = this.field_175250_f.field_71441_e.func_152378_a(var25.getId());
               boolean var27 = var26 != null && var26.func_175148_a(EnumPlayerModelParts.CAPE) && ("Dinnerbone".equals(var25.getName()) || "Grumm".equals(var25.getName()));
               this.field_175250_f.func_110434_K().func_110577_a(var24.func_178837_g());
               var28 = 8 + (var27 ? 8 : 0);
               int var29 = 8 * (var27 ? -1 : 1);
               Gui.func_152125_a(var22, var23, 8.0F, (float)var28, 8, var29, 8, 8, 64.0F, 64.0F);
               if (var26 != null && var26.func_175148_a(EnumPlayerModelParts.HAT)) {
                  int var30 = 8 + (var27 ? 8 : 0);
                  int var31 = 8 * (var27 ? -1 : 1);
                  Gui.func_152125_a(var22, var23, 40.0F, (float)var30, 8, var31, 8, 8, 64.0F, 64.0F);
               }

               var22 += 9;
            }

            String var38 = this.func_200262_a(var24).func_150254_d();
            if (var24.func_178848_b() == GameType.SPECTATOR) {
               this.field_175250_f.field_71466_p.func_175063_a(TextFormatting.ITALIC + var38, (float)var22, (float)var23, -1862270977);
            } else {
               this.field_175250_f.field_71466_p.func_175063_a(var38, (float)var22, (float)var23, -1);
            }

            if (var3 != null && var24.func_178848_b() != GameType.SPECTATOR) {
               int var39 = var22 + var6 + 1;
               var28 = var39 + var12;
               if (var28 - var39 > 5) {
                  this.func_175247_a(var3, var23, var25.getName(), var39, var28, var24);
               }
            }

            this.func_175245_a(var13, var22 - (var11 ? 9 : 0), var23, var24);
         }
      }

      if (var34 != null) {
         var15 += var33 * 9 + 1;
         func_73734_a(var1 / 2 - var16 / 2 - 1, var15 - 1, var1 / 2 + var16 / 2 + 1, var15 + var34.size() * this.field_175250_f.field_71466_p.field_78288_b, -2147483648);

         for(var35 = var34.iterator(); var35.hasNext(); var15 += this.field_175250_f.field_71466_p.field_78288_b) {
            var20 = (String)var35.next();
            var21 = this.field_175250_f.field_71466_p.func_78256_a(var20);
            this.field_175250_f.field_71466_p.func_175063_a(var20, (float)(var1 / 2 - var21 / 2), (float)var15, -1);
         }
      }

   }

   protected void func_175245_a(int var1, int var2, int var3, NetworkPlayerInfo var4) {
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_175250_f.func_110434_K().func_110577_a(field_110324_m);
      boolean var5 = false;
      byte var6;
      if (var4.func_178853_c() < 0) {
         var6 = 5;
      } else if (var4.func_178853_c() < 150) {
         var6 = 0;
      } else if (var4.func_178853_c() < 300) {
         var6 = 1;
      } else if (var4.func_178853_c() < 600) {
         var6 = 2;
      } else if (var4.func_178853_c() < 1000) {
         var6 = 3;
      } else {
         var6 = 4;
      }

      this.field_73735_i += 100.0F;
      this.func_73729_b(var2 + var1 - 11, var3, 0, 176 + var6 * 8, 10, 8);
      this.field_73735_i -= 100.0F;
   }

   private void func_175247_a(ScoreObjective var1, int var2, String var3, int var4, int var5, NetworkPlayerInfo var6) {
      int var7 = var1.func_96682_a().func_96529_a(var3, var1).func_96652_c();
      if (var1.func_199865_f() == ScoreCriteria.RenderType.HEARTS) {
         this.field_175250_f.func_110434_K().func_110577_a(field_110324_m);
         long var8 = Util.func_211177_b();
         if (this.field_175253_j == var6.func_178855_p()) {
            if (var7 < var6.func_178835_l()) {
               var6.func_178846_a(var8);
               var6.func_178844_b((long)(this.field_175251_g.func_73834_c() + 20));
            } else if (var7 > var6.func_178835_l()) {
               var6.func_178846_a(var8);
               var6.func_178844_b((long)(this.field_175251_g.func_73834_c() + 10));
            }
         }

         if (var8 - var6.func_178847_n() > 1000L || this.field_175253_j != var6.func_178855_p()) {
            var6.func_178836_b(var7);
            var6.func_178857_c(var7);
            var6.func_178846_a(var8);
         }

         var6.func_178843_c(this.field_175253_j);
         var6.func_178836_b(var7);
         int var10 = MathHelper.func_76123_f((float)Math.max(var7, var6.func_178860_m()) / 2.0F);
         int var11 = Math.max(MathHelper.func_76123_f((float)(var7 / 2)), Math.max(MathHelper.func_76123_f((float)(var6.func_178860_m() / 2)), 10));
         boolean var12 = var6.func_178858_o() > (long)this.field_175251_g.func_73834_c() && (var6.func_178858_o() - (long)this.field_175251_g.func_73834_c()) / 3L % 2L == 1L;
         if (var10 > 0) {
            float var13 = Math.min((float)(var5 - var4 - 4) / (float)var11, 9.0F);
            if (var13 > 3.0F) {
               int var14;
               for(var14 = var10; var14 < var11; ++var14) {
                  this.func_175174_a((float)var4 + (float)var14 * var13, (float)var2, var12 ? 25 : 16, 0, 9, 9);
               }

               for(var14 = 0; var14 < var10; ++var14) {
                  this.func_175174_a((float)var4 + (float)var14 * var13, (float)var2, var12 ? 25 : 16, 0, 9, 9);
                  if (var12) {
                     if (var14 * 2 + 1 < var6.func_178860_m()) {
                        this.func_175174_a((float)var4 + (float)var14 * var13, (float)var2, 70, 0, 9, 9);
                     }

                     if (var14 * 2 + 1 == var6.func_178860_m()) {
                        this.func_175174_a((float)var4 + (float)var14 * var13, (float)var2, 79, 0, 9, 9);
                     }
                  }

                  if (var14 * 2 + 1 < var7) {
                     this.func_175174_a((float)var4 + (float)var14 * var13, (float)var2, var14 >= 10 ? 160 : 52, 0, 9, 9);
                  }

                  if (var14 * 2 + 1 == var7) {
                     this.func_175174_a((float)var4 + (float)var14 * var13, (float)var2, var14 >= 10 ? 169 : 61, 0, 9, 9);
                  }
               }
            } else {
               float var18 = MathHelper.func_76131_a((float)var7 / 20.0F, 0.0F, 1.0F);
               int var15 = (int)((1.0F - var18) * 255.0F) << 16 | (int)(var18 * 255.0F) << 8;
               String var16 = "" + (float)var7 / 2.0F;
               if (var5 - this.field_175250_f.field_71466_p.func_78256_a(var16 + "hp") >= var4) {
                  var16 = var16 + "hp";
               }

               this.field_175250_f.field_71466_p.func_175063_a(var16, (float)((var5 + var4) / 2 - this.field_175250_f.field_71466_p.func_78256_a(var16) / 2), (float)var2, var15);
            }
         }
      } else {
         String var17 = TextFormatting.YELLOW + "" + var7;
         this.field_175250_f.field_71466_p.func_175063_a(var17, (float)(var5 - this.field_175250_f.field_71466_p.func_78256_a(var17)), (float)var2, 16777215);
      }

   }

   public void func_175248_a(@Nullable ITextComponent var1) {
      this.field_175255_h = var1;
   }

   public void func_175244_b(@Nullable ITextComponent var1) {
      this.field_175256_i = var1;
   }

   public void func_181030_a() {
      this.field_175256_i = null;
      this.field_175255_h = null;
   }

   static class PlayerComparator implements Comparator<NetworkPlayerInfo> {
      private PlayerComparator() {
         super();
      }

      public int compare(NetworkPlayerInfo var1, NetworkPlayerInfo var2) {
         ScorePlayerTeam var3 = var1.func_178850_i();
         ScorePlayerTeam var4 = var2.func_178850_i();
         return ComparisonChain.start().compareTrueFirst(var1.func_178848_b() != GameType.SPECTATOR, var2.func_178848_b() != GameType.SPECTATOR).compare(var3 != null ? var3.func_96661_b() : "", var4 != null ? var4.func_96661_b() : "").compare(var1.func_178845_a().getName(), var2.func_178845_a().getName(), String::compareToIgnoreCase).result();
      }

      // $FF: synthetic method
      public int compare(Object var1, Object var2) {
         return this.compare((NetworkPlayerInfo)var1, (NetworkPlayerInfo)var2);
      }

      // $FF: synthetic method
      PlayerComparator(Object var1) {
         this();
      }
   }
}

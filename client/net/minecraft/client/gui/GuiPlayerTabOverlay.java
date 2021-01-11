package net.minecraft.client.gui;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings;

public class GuiPlayerTabOverlay extends Gui {
   private static final Ordering<NetworkPlayerInfo> field_175252_a = Ordering.from(new GuiPlayerTabOverlay.PlayerComparator());
   private final Minecraft field_175250_f;
   private final GuiIngame field_175251_g;
   private IChatComponent field_175255_h;
   private IChatComponent field_175256_i;
   private long field_175253_j;
   private boolean field_175254_k;

   public GuiPlayerTabOverlay(Minecraft var1, GuiIngame var2) {
      super();
      this.field_175250_f = var1;
      this.field_175251_g = var2;
   }

   public String func_175243_a(NetworkPlayerInfo var1) {
      return var1.func_178854_k() != null ? var1.func_178854_k().func_150254_d() : ScorePlayerTeam.func_96667_a(var1.func_178850_i(), var1.func_178845_a().getName());
   }

   public void func_175246_a(boolean var1) {
      if (var1 && !this.field_175254_k) {
         this.field_175253_j = Minecraft.func_71386_F();
      }

      this.field_175254_k = var1;
   }

   public void func_175249_a(int var1, Scoreboard var2, ScoreObjective var3) {
      NetHandlerPlayClient var4 = this.field_175250_f.field_71439_g.field_71174_a;
      List var5 = field_175252_a.sortedCopy(var4.func_175106_d());
      int var6 = 0;
      int var7 = 0;
      Iterator var8 = var5.iterator();

      int var10;
      while(var8.hasNext()) {
         NetworkPlayerInfo var9 = (NetworkPlayerInfo)var8.next();
         var10 = this.field_175250_f.field_71466_p.func_78256_a(this.func_175243_a(var9));
         var6 = Math.max(var6, var10);
         if (var3 != null && var3.func_178766_e() != IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
            var10 = this.field_175250_f.field_71466_p.func_78256_a(" " + var2.func_96529_a(var9.func_178845_a().getName(), var3).func_96652_c());
            var7 = Math.max(var7, var10);
         }
      }

      var5 = var5.subList(0, Math.min(var5.size(), 80));
      int var33 = var5.size();
      int var34 = var33;

      for(var10 = 1; var34 > 20; var34 = (var33 + var10 - 1) / var10) {
         ++var10;
      }

      boolean var11 = this.field_175250_f.func_71387_A() || this.field_175250_f.func_147114_u().func_147298_b().func_179292_f();
      int var12;
      if (var3 != null) {
         if (var3.func_178766_e() == IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
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
      List var18 = null;
      Iterator var19;
      String var20;
      if (this.field_175256_i != null) {
         var17 = this.field_175250_f.field_71466_p.func_78271_c(this.field_175256_i.func_150254_d(), var1 - 50);

         for(var19 = var17.iterator(); var19.hasNext(); var16 = Math.max(var16, this.field_175250_f.field_71466_p.func_78256_a(var20))) {
            var20 = (String)var19.next();
         }
      }

      if (this.field_175255_h != null) {
         var18 = this.field_175250_f.field_71466_p.func_78271_c(this.field_175255_h.func_150254_d(), var1 - 50);

         for(var19 = var18.iterator(); var19.hasNext(); var16 = Math.max(var16, this.field_175250_f.field_71466_p.func_78256_a(var20))) {
            var20 = (String)var19.next();
         }
      }

      int var21;
      if (var17 != null) {
         func_73734_a(var1 / 2 - var16 / 2 - 1, var15 - 1, var1 / 2 + var16 / 2 + 1, var15 + var17.size() * this.field_175250_f.field_71466_p.field_78288_b, -2147483648);

         for(var19 = var17.iterator(); var19.hasNext(); var15 += this.field_175250_f.field_71466_p.field_78288_b) {
            var20 = (String)var19.next();
            var21 = this.field_175250_f.field_71466_p.func_78256_a(var20);
            this.field_175250_f.field_71466_p.func_175063_a(var20, (float)(var1 / 2 - var21 / 2), (float)var15, -1);
         }

         ++var15;
      }

      func_73734_a(var1 / 2 - var16 / 2 - 1, var15 - 1, var1 / 2 + var16 / 2 + 1, var15 + var34 * 9, -2147483648);

      for(int var35 = 0; var35 < var33; ++var35) {
         int var36 = var35 / var34;
         var21 = var35 % var34;
         int var22 = var14 + var36 * var13 + var36 * 5;
         int var23 = var15 + var21 * 9;
         func_73734_a(var22, var23, var22 + var13, var23 + 8, 553648127);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179141_d();
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 771, 1, 0);
         if (var35 < var5.size()) {
            NetworkPlayerInfo var24 = (NetworkPlayerInfo)var5.get(var35);
            String var25 = this.func_175243_a(var24);
            GameProfile var26 = var24.func_178845_a();
            if (var11) {
               EntityPlayer var27 = this.field_175250_f.field_71441_e.func_152378_a(var26.getId());
               boolean var28 = var27 != null && var27.func_175148_a(EnumPlayerModelParts.CAPE) && (var26.getName().equals("Dinnerbone") || var26.getName().equals("Grumm"));
               this.field_175250_f.func_110434_K().func_110577_a(var24.func_178837_g());
               int var29 = 8 + (var28 ? 8 : 0);
               int var30 = 8 * (var28 ? -1 : 1);
               Gui.func_152125_a(var22, var23, 8.0F, (float)var29, 8, var30, 8, 8, 64.0F, 64.0F);
               if (var27 != null && var27.func_175148_a(EnumPlayerModelParts.HAT)) {
                  int var31 = 8 + (var28 ? 8 : 0);
                  int var32 = 8 * (var28 ? -1 : 1);
                  Gui.func_152125_a(var22, var23, 40.0F, (float)var31, 8, var32, 8, 8, 64.0F, 64.0F);
               }

               var22 += 9;
            }

            if (var24.func_178848_b() == WorldSettings.GameType.SPECTATOR) {
               var25 = EnumChatFormatting.ITALIC + var25;
               this.field_175250_f.field_71466_p.func_175063_a(var25, (float)var22, (float)var23, -1862270977);
            } else {
               this.field_175250_f.field_71466_p.func_175063_a(var25, (float)var22, (float)var23, -1);
            }

            if (var3 != null && var24.func_178848_b() != WorldSettings.GameType.SPECTATOR) {
               int var37 = var22 + var6 + 1;
               int var38 = var37 + var12;
               if (var38 - var37 > 5) {
                  this.func_175247_a(var3, var23, var26.getName(), var37, var38, var24);
               }
            }

            this.func_175245_a(var13, var22 - (var11 ? 9 : 0), var23, var24);
         }
      }

      if (var18 != null) {
         var15 += var34 * 9 + 1;
         func_73734_a(var1 / 2 - var16 / 2 - 1, var15 - 1, var1 / 2 + var16 / 2 + 1, var15 + var18.size() * this.field_175250_f.field_71466_p.field_78288_b, -2147483648);

         for(var19 = var18.iterator(); var19.hasNext(); var15 += this.field_175250_f.field_71466_p.field_78288_b) {
            var20 = (String)var19.next();
            var21 = this.field_175250_f.field_71466_p.func_78256_a(var20);
            this.field_175250_f.field_71466_p.func_175063_a(var20, (float)(var1 / 2 - var21 / 2), (float)var15, -1);
         }
      }

   }

   protected void func_175245_a(int var1, int var2, int var3, NetworkPlayerInfo var4) {
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_175250_f.func_110434_K().func_110577_a(field_110324_m);
      byte var5 = 0;
      boolean var6 = false;
      byte var7;
      if (var4.func_178853_c() < 0) {
         var7 = 5;
      } else if (var4.func_178853_c() < 150) {
         var7 = 0;
      } else if (var4.func_178853_c() < 300) {
         var7 = 1;
      } else if (var4.func_178853_c() < 600) {
         var7 = 2;
      } else if (var4.func_178853_c() < 1000) {
         var7 = 3;
      } else {
         var7 = 4;
      }

      this.field_73735_i += 100.0F;
      this.func_73729_b(var2 + var1 - 11, var3, 0 + var5 * 10, 176 + var7 * 8, 10, 8);
      this.field_73735_i -= 100.0F;
   }

   private void func_175247_a(ScoreObjective var1, int var2, String var3, int var4, int var5, NetworkPlayerInfo var6) {
      int var7 = var1.func_96682_a().func_96529_a(var3, var1).func_96652_c();
      if (var1.func_178766_e() == IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
         this.field_175250_f.func_110434_K().func_110577_a(field_110324_m);
         if (this.field_175253_j == var6.func_178855_p()) {
            if (var7 < var6.func_178835_l()) {
               var6.func_178846_a(Minecraft.func_71386_F());
               var6.func_178844_b((long)(this.field_175251_g.func_73834_c() + 20));
            } else if (var7 > var6.func_178835_l()) {
               var6.func_178846_a(Minecraft.func_71386_F());
               var6.func_178844_b((long)(this.field_175251_g.func_73834_c() + 10));
            }
         }

         if (Minecraft.func_71386_F() - var6.func_178847_n() > 1000L || this.field_175253_j != var6.func_178855_p()) {
            var6.func_178836_b(var7);
            var6.func_178857_c(var7);
            var6.func_178846_a(Minecraft.func_71386_F());
         }

         var6.func_178843_c(this.field_175253_j);
         var6.func_178836_b(var7);
         int var8 = MathHelper.func_76123_f((float)Math.max(var7, var6.func_178860_m()) / 2.0F);
         int var9 = Math.max(MathHelper.func_76123_f((float)(var7 / 2)), Math.max(MathHelper.func_76123_f((float)(var6.func_178860_m() / 2)), 10));
         boolean var10 = var6.func_178858_o() > (long)this.field_175251_g.func_73834_c() && (var6.func_178858_o() - (long)this.field_175251_g.func_73834_c()) / 3L % 2L == 1L;
         if (var8 > 0) {
            float var11 = Math.min((float)(var5 - var4 - 4) / (float)var9, 9.0F);
            if (var11 > 3.0F) {
               int var12;
               for(var12 = var8; var12 < var9; ++var12) {
                  this.func_175174_a((float)var4 + (float)var12 * var11, (float)var2, var10 ? 25 : 16, 0, 9, 9);
               }

               for(var12 = 0; var12 < var8; ++var12) {
                  this.func_175174_a((float)var4 + (float)var12 * var11, (float)var2, var10 ? 25 : 16, 0, 9, 9);
                  if (var10) {
                     if (var12 * 2 + 1 < var6.func_178860_m()) {
                        this.func_175174_a((float)var4 + (float)var12 * var11, (float)var2, 70, 0, 9, 9);
                     }

                     if (var12 * 2 + 1 == var6.func_178860_m()) {
                        this.func_175174_a((float)var4 + (float)var12 * var11, (float)var2, 79, 0, 9, 9);
                     }
                  }

                  if (var12 * 2 + 1 < var7) {
                     this.func_175174_a((float)var4 + (float)var12 * var11, (float)var2, var12 >= 10 ? 160 : 52, 0, 9, 9);
                  }

                  if (var12 * 2 + 1 == var7) {
                     this.func_175174_a((float)var4 + (float)var12 * var11, (float)var2, var12 >= 10 ? 169 : 61, 0, 9, 9);
                  }
               }
            } else {
               float var16 = MathHelper.func_76131_a((float)var7 / 20.0F, 0.0F, 1.0F);
               int var13 = (int)((1.0F - var16) * 255.0F) << 16 | (int)(var16 * 255.0F) << 8;
               String var14 = "" + (float)var7 / 2.0F;
               if (var5 - this.field_175250_f.field_71466_p.func_78256_a(var14 + "hp") >= var4) {
                  var14 = var14 + "hp";
               }

               this.field_175250_f.field_71466_p.func_175063_a(var14, (float)((var5 + var4) / 2 - this.field_175250_f.field_71466_p.func_78256_a(var14) / 2), (float)var2, var13);
            }
         }
      } else {
         String var15 = EnumChatFormatting.YELLOW + "" + var7;
         this.field_175250_f.field_71466_p.func_175063_a(var15, (float)(var5 - this.field_175250_f.field_71466_p.func_78256_a(var15)), (float)var2, 16777215);
      }

   }

   public void func_175248_a(IChatComponent var1) {
      this.field_175255_h = var1;
   }

   public void func_175244_b(IChatComponent var1) {
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
         return ComparisonChain.start().compareTrueFirst(var1.func_178848_b() != WorldSettings.GameType.SPECTATOR, var2.func_178848_b() != WorldSettings.GameType.SPECTATOR).compare(var3 != null ? var3.func_96661_b() : "", var4 != null ? var4.func_96661_b() : "").compare(var1.func_178845_a().getName(), var2.func_178845_a().getName()).result();
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

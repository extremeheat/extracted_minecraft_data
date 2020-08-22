package net.minecraft.client.gui.components;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class PlayerTabOverlay extends GuiComponent {
   private static final Ordering PLAYER_ORDERING = Ordering.from(new PlayerTabOverlay.PlayerInfoComparator());
   private final Minecraft minecraft;
   private final Gui gui;
   private Component footer;
   private Component header;
   private long visibilityId;
   private boolean visible;

   public PlayerTabOverlay(Minecraft var1, Gui var2) {
      this.minecraft = var1;
      this.gui = var2;
   }

   public Component getNameForDisplay(PlayerInfo var1) {
      return var1.getTabListDisplayName() != null ? var1.getTabListDisplayName() : PlayerTeam.formatNameForTeam(var1.getTeam(), new TextComponent(var1.getProfile().getName()));
   }

   public void setVisible(boolean var1) {
      if (var1 && !this.visible) {
         this.visibilityId = Util.getMillis();
      }

      this.visible = var1;
   }

   public void render(int var1, Scoreboard var2, @Nullable Objective var3) {
      ClientPacketListener var4 = this.minecraft.player.connection;
      List var5 = PLAYER_ORDERING.sortedCopy(var4.getOnlinePlayers());
      int var6 = 0;
      int var7 = 0;
      Iterator var8 = var5.iterator();

      int var10;
      while(var8.hasNext()) {
         PlayerInfo var9 = (PlayerInfo)var8.next();
         var10 = this.minecraft.font.width(this.getNameForDisplay(var9).getColoredString());
         var6 = Math.max(var6, var10);
         if (var3 != null && var3.getRenderType() != ObjectiveCriteria.RenderType.HEARTS) {
            var10 = this.minecraft.font.width(" " + var2.getOrCreatePlayerScore(var9.getProfile().getName(), var3).getScore());
            var7 = Math.max(var7, var10);
         }
      }

      var5 = var5.subList(0, Math.min(var5.size(), 80));
      int var33 = var5.size();
      int var34 = var33;

      for(var10 = 1; var34 > 20; var34 = (var33 + var10 - 1) / var10) {
         ++var10;
      }

      boolean var11 = this.minecraft.isLocalServer() || this.minecraft.getConnection().getConnection().isEncrypted();
      int var12;
      if (var3 != null) {
         if (var3.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
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
      if (this.header != null) {
         var17 = this.minecraft.font.split(this.header.getColoredString(), var1 - 50);

         String var19;
         for(Iterator var18 = var17.iterator(); var18.hasNext(); var16 = Math.max(var16, this.minecraft.font.width(var19))) {
            var19 = (String)var18.next();
         }
      }

      List var35 = null;
      String var20;
      Iterator var36;
      if (this.footer != null) {
         var35 = this.minecraft.font.split(this.footer.getColoredString(), var1 - 50);

         for(var36 = var35.iterator(); var36.hasNext(); var16 = Math.max(var16, this.minecraft.font.width(var20))) {
            var20 = (String)var36.next();
         }
      }

      int var10000;
      int var10001;
      int var10002;
      int var10004;
      int var21;
      if (var17 != null) {
         var10000 = var1 / 2 - var16 / 2 - 1;
         var10001 = var15 - 1;
         var10002 = var1 / 2 + var16 / 2 + 1;
         var10004 = var17.size();
         this.minecraft.font.getClass();
         fill(var10000, var10001, var10002, var15 + var10004 * 9, Integer.MIN_VALUE);

         for(var36 = var17.iterator(); var36.hasNext(); var15 += 9) {
            var20 = (String)var36.next();
            var21 = this.minecraft.font.width(var20);
            this.minecraft.font.drawShadow(var20, (float)(var1 / 2 - var21 / 2), (float)var15, -1);
            this.minecraft.font.getClass();
         }

         ++var15;
      }

      fill(var1 / 2 - var16 / 2 - 1, var15 - 1, var1 / 2 + var16 / 2 + 1, var15 + var34 * 9, Integer.MIN_VALUE);
      int var37 = this.minecraft.options.getBackgroundColor(553648127);

      int var22;
      for(int var38 = 0; var38 < var33; ++var38) {
         var21 = var38 / var34;
         var22 = var38 % var34;
         int var23 = var14 + var21 * var13 + var21 * 5;
         int var24 = var15 + var22 * 9;
         fill(var23, var24, var23 + var13, var24 + 8, var37);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.enableAlphaTest();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         if (var38 < var5.size()) {
            PlayerInfo var25 = (PlayerInfo)var5.get(var38);
            GameProfile var26 = var25.getProfile();
            int var29;
            if (var11) {
               Player var27 = this.minecraft.level.getPlayerByUUID(var26.getId());
               boolean var28 = var27 != null && var27.isModelPartShown(PlayerModelPart.CAPE) && ("Dinnerbone".equals(var26.getName()) || "Grumm".equals(var26.getName()));
               this.minecraft.getTextureManager().bind(var25.getSkinLocation());
               var29 = 8 + (var28 ? 8 : 0);
               int var30 = 8 * (var28 ? -1 : 1);
               GuiComponent.blit(var23, var24, 8, 8, 8.0F, (float)var29, 8, var30, 64, 64);
               if (var27 != null && var27.isModelPartShown(PlayerModelPart.HAT)) {
                  int var31 = 8 + (var28 ? 8 : 0);
                  int var32 = 8 * (var28 ? -1 : 1);
                  GuiComponent.blit(var23, var24, 8, 8, 40.0F, (float)var31, 8, var32, 64, 64);
               }

               var23 += 9;
            }

            String var41 = this.getNameForDisplay(var25).getColoredString();
            if (var25.getGameMode() == GameType.SPECTATOR) {
               this.minecraft.font.drawShadow(ChatFormatting.ITALIC + var41, (float)var23, (float)var24, -1862270977);
            } else {
               this.minecraft.font.drawShadow(var41, (float)var23, (float)var24, -1);
            }

            if (var3 != null && var25.getGameMode() != GameType.SPECTATOR) {
               int var42 = var23 + var6 + 1;
               var29 = var42 + var12;
               if (var29 - var42 > 5) {
                  this.renderTablistScore(var3, var24, var26.getName(), var42, var29, var25);
               }
            }

            this.renderPingIcon(var13, var23 - (var11 ? 9 : 0), var24, var25);
         }
      }

      if (var35 != null) {
         var15 += var34 * 9 + 1;
         var10000 = var1 / 2 - var16 / 2 - 1;
         var10001 = var15 - 1;
         var10002 = var1 / 2 + var16 / 2 + 1;
         var10004 = var35.size();
         this.minecraft.font.getClass();
         fill(var10000, var10001, var10002, var15 + var10004 * 9, Integer.MIN_VALUE);

         for(Iterator var39 = var35.iterator(); var39.hasNext(); var15 += 9) {
            String var40 = (String)var39.next();
            var22 = this.minecraft.font.width(var40);
            this.minecraft.font.drawShadow(var40, (float)(var1 / 2 - var22 / 2), (float)var15, -1);
            this.minecraft.font.getClass();
         }
      }

   }

   protected void renderPingIcon(int var1, int var2, int var3, PlayerInfo var4) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(GUI_ICONS_LOCATION);
      boolean var5 = false;
      byte var6;
      if (var4.getLatency() < 0) {
         var6 = 5;
      } else if (var4.getLatency() < 150) {
         var6 = 0;
      } else if (var4.getLatency() < 300) {
         var6 = 1;
      } else if (var4.getLatency() < 600) {
         var6 = 2;
      } else if (var4.getLatency() < 1000) {
         var6 = 3;
      } else {
         var6 = 4;
      }

      this.setBlitOffset(this.getBlitOffset() + 100);
      this.blit(var2 + var1 - 11, var3, 0, 176 + var6 * 8, 10, 8);
      this.setBlitOffset(this.getBlitOffset() - 100);
   }

   private void renderTablistScore(Objective var1, int var2, String var3, int var4, int var5, PlayerInfo var6) {
      int var7 = var1.getScoreboard().getOrCreatePlayerScore(var3, var1).getScore();
      if (var1.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
         this.minecraft.getTextureManager().bind(GUI_ICONS_LOCATION);
         long var8 = Util.getMillis();
         if (this.visibilityId == var6.getRenderVisibilityId()) {
            if (var7 < var6.getLastHealth()) {
               var6.setLastHealthTime(var8);
               var6.setHealthBlinkTime((long)(this.gui.getGuiTicks() + 20));
            } else if (var7 > var6.getLastHealth()) {
               var6.setLastHealthTime(var8);
               var6.setHealthBlinkTime((long)(this.gui.getGuiTicks() + 10));
            }
         }

         if (var8 - var6.getLastHealthTime() > 1000L || this.visibilityId != var6.getRenderVisibilityId()) {
            var6.setLastHealth(var7);
            var6.setDisplayHealth(var7);
            var6.setLastHealthTime(var8);
         }

         var6.setRenderVisibilityId(this.visibilityId);
         var6.setLastHealth(var7);
         int var10 = Mth.ceil((float)Math.max(var7, var6.getDisplayHealth()) / 2.0F);
         int var11 = Math.max(Mth.ceil((float)(var7 / 2)), Math.max(Mth.ceil((float)(var6.getDisplayHealth() / 2)), 10));
         boolean var12 = var6.getHealthBlinkTime() > (long)this.gui.getGuiTicks() && (var6.getHealthBlinkTime() - (long)this.gui.getGuiTicks()) / 3L % 2L == 1L;
         if (var10 > 0) {
            int var13 = Mth.floor(Math.min((float)(var5 - var4 - 4) / (float)var11, 9.0F));
            if (var13 > 3) {
               int var14;
               for(var14 = var10; var14 < var11; ++var14) {
                  this.blit(var4 + var14 * var13, var2, var12 ? 25 : 16, 0, 9, 9);
               }

               for(var14 = 0; var14 < var10; ++var14) {
                  this.blit(var4 + var14 * var13, var2, var12 ? 25 : 16, 0, 9, 9);
                  if (var12) {
                     if (var14 * 2 + 1 < var6.getDisplayHealth()) {
                        this.blit(var4 + var14 * var13, var2, 70, 0, 9, 9);
                     }

                     if (var14 * 2 + 1 == var6.getDisplayHealth()) {
                        this.blit(var4 + var14 * var13, var2, 79, 0, 9, 9);
                     }
                  }

                  if (var14 * 2 + 1 < var7) {
                     this.blit(var4 + var14 * var13, var2, var14 >= 10 ? 160 : 52, 0, 9, 9);
                  }

                  if (var14 * 2 + 1 == var7) {
                     this.blit(var4 + var14 * var13, var2, var14 >= 10 ? 169 : 61, 0, 9, 9);
                  }
               }
            } else {
               float var18 = Mth.clamp((float)var7 / 20.0F, 0.0F, 1.0F);
               int var15 = (int)((1.0F - var18) * 255.0F) << 16 | (int)(var18 * 255.0F) << 8;
               String var16 = "" + (float)var7 / 2.0F;
               if (var5 - this.minecraft.font.width(var16 + "hp") >= var4) {
                  var16 = var16 + "hp";
               }

               this.minecraft.font.drawShadow(var16, (float)((var5 + var4) / 2 - this.minecraft.font.width(var16) / 2), (float)var2, var15);
            }
         }
      } else {
         String var17 = ChatFormatting.YELLOW + "" + var7;
         this.minecraft.font.drawShadow(var17, (float)(var5 - this.minecraft.font.width(var17)), (float)var2, 16777215);
      }

   }

   public void setFooter(@Nullable Component var1) {
      this.footer = var1;
   }

   public void setHeader(@Nullable Component var1) {
      this.header = var1;
   }

   public void reset() {
      this.header = null;
      this.footer = null;
   }

   static class PlayerInfoComparator implements Comparator {
      private PlayerInfoComparator() {
      }

      public int compare(PlayerInfo var1, PlayerInfo var2) {
         PlayerTeam var3 = var1.getTeam();
         PlayerTeam var4 = var2.getTeam();
         return ComparisonChain.start().compareTrueFirst(var1.getGameMode() != GameType.SPECTATOR, var2.getGameMode() != GameType.SPECTATOR).compare(var3 != null ? var3.getName() : "", var4 != null ? var4.getName() : "").compare(var1.getProfile().getName(), var2.getProfile().getName(), String::compareToIgnoreCase).result();
      }

      // $FF: synthetic method
      public int compare(Object var1, Object var2) {
         return this.compare((PlayerInfo)var1, (PlayerInfo)var2);
      }

      // $FF: synthetic method
      PlayerInfoComparator(Object var1) {
         this();
      }
   }
}

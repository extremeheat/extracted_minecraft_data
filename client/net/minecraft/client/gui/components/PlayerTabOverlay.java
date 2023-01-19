package net.minecraft.client.gui.components;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class PlayerTabOverlay extends GuiComponent {
   private static final Ordering<PlayerInfo> PLAYER_ORDERING = Ordering.from(new PlayerTabOverlay.PlayerInfoComparator());
   public static final int MAX_ROWS_PER_COL = 20;
   public static final int HEART_EMPTY_CONTAINER = 16;
   public static final int HEART_EMPTY_CONTAINER_BLINKING = 25;
   public static final int HEART_FULL = 52;
   public static final int HEART_HALF_FULL = 61;
   public static final int HEART_GOLDEN_FULL = 160;
   public static final int HEART_GOLDEN_HALF_FULL = 169;
   public static final int HEART_GHOST_FULL = 70;
   public static final int HEART_GHOST_HALF_FULL = 79;
   private final Minecraft minecraft;
   private final Gui gui;
   @Nullable
   private Component footer;
   @Nullable
   private Component header;
   private long visibilityId;
   private boolean visible;

   public PlayerTabOverlay(Minecraft var1, Gui var2) {
      super();
      this.minecraft = var1;
      this.gui = var2;
   }

   public Component getNameForDisplay(PlayerInfo var1) {
      return var1.getTabListDisplayName() != null
         ? this.decorateName(var1, var1.getTabListDisplayName().copy())
         : this.decorateName(var1, PlayerTeam.formatNameForTeam(var1.getTeam(), Component.literal(var1.getProfile().getName())));
   }

   private Component decorateName(PlayerInfo var1, MutableComponent var2) {
      return var1.getGameMode() == GameType.SPECTATOR ? var2.withStyle(ChatFormatting.ITALIC) : var2;
   }

   public void setVisible(boolean var1) {
      if (var1 && !this.visible) {
         this.visibilityId = Util.getMillis();
      }

      this.visible = var1;
   }

   public void render(PoseStack var1, int var2, Scoreboard var3, @Nullable Objective var4) {
      ClientPacketListener var5 = this.minecraft.player.connection;
      List var6 = PLAYER_ORDERING.sortedCopy(var5.getOnlinePlayers());
      int var7 = 0;
      int var8 = 0;

      for(PlayerInfo var10 : var6) {
         int var11 = this.minecraft.font.width(this.getNameForDisplay(var10));
         var7 = Math.max(var7, var11);
         if (var4 != null && var4.getRenderType() != ObjectiveCriteria.RenderType.HEARTS) {
            var11 = this.minecraft.font.width(" " + var3.getOrCreatePlayerScore(var10.getProfile().getName(), var4).getScore());
            var8 = Math.max(var8, var11);
         }
      }

      var6 = var6.subList(0, Math.min(var6.size(), 80));
      int var32 = var6.size();
      int var33 = var32;

      int var35;
      for(var35 = 1; var33 > 20; var33 = (var32 + var35 - 1) / var35) {
         ++var35;
      }

      boolean var12 = this.minecraft.isLocalServer() || this.minecraft.getConnection().getConnection().isEncrypted();
      int var13;
      if (var4 != null) {
         if (var4.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
            var13 = 90;
         } else {
            var13 = var8;
         }
      } else {
         var13 = 0;
      }

      int var14 = Math.min(var35 * ((var12 ? 9 : 0) + var7 + var13 + 13), var2 - 50) / var35;
      int var15 = var2 / 2 - (var14 * var35 + (var35 - 1) * 5) / 2;
      int var16 = 10;
      int var17 = var14 * var35 + (var35 - 1) * 5;
      List var18 = null;
      if (this.header != null) {
         var18 = this.minecraft.font.split(this.header, var2 - 50);

         for(FormattedCharSequence var20 : var18) {
            var17 = Math.max(var17, this.minecraft.font.width(var20));
         }
      }

      List var37 = null;
      if (this.footer != null) {
         var37 = this.minecraft.font.split(this.footer, var2 - 50);

         for(FormattedCharSequence var21 : var37) {
            var17 = Math.max(var17, this.minecraft.font.width(var21));
         }
      }

      if (var18 != null) {
         fill(var1, var2 / 2 - var17 / 2 - 1, var16 - 1, var2 / 2 + var17 / 2 + 1, var16 + var18.size() * 9, -2147483648);

         for(FormattedCharSequence var41 : var18) {
            int var22 = this.minecraft.font.width(var41);
            this.minecraft.font.drawShadow(var1, var41, (float)(var2 / 2 - var22 / 2), (float)var16, -1);
            var16 += 9;
         }

         ++var16;
      }

      fill(var1, var2 / 2 - var17 / 2 - 1, var16 - 1, var2 / 2 + var17 / 2 + 1, var16 + var33 * 9, -2147483648);
      int var40 = this.minecraft.options.getBackgroundColor(553648127);

      for(int var42 = 0; var42 < var32; ++var42) {
         int var44 = var42 / var33;
         int var23 = var42 % var33;
         int var24 = var15 + var44 * var14 + var44 * 5;
         int var25 = var16 + var23 * 9;
         fill(var1, var24, var25, var24 + var14, var25 + 8, var40);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         if (var42 < var6.size()) {
            PlayerInfo var26 = (PlayerInfo)var6.get(var42);
            GameProfile var27 = var26.getProfile();
            if (var12) {
               Player var28 = this.minecraft.level.getPlayerByUUID(var27.getId());
               boolean var29 = var28 != null && LivingEntityRenderer.isEntityUpsideDown(var28);
               boolean var30 = var28 != null && var28.isModelPartShown(PlayerModelPart.HAT);
               RenderSystem.setShaderTexture(0, var26.getSkinLocation());
               PlayerFaceRenderer.draw(var1, var24, var25, 8, var30, var29);
               var24 += 9;
            }

            this.minecraft
               .font
               .drawShadow(var1, this.getNameForDisplay(var26), (float)var24, (float)var25, var26.getGameMode() == GameType.SPECTATOR ? -1862270977 : -1);
            if (var4 != null && var26.getGameMode() != GameType.SPECTATOR) {
               int var47 = var24 + var7 + 1;
               int var48 = var47 + var13;
               if (var48 - var47 > 5) {
                  this.renderTablistScore(var4, var25, var27.getName(), var47, var48, var26, var1);
               }
            }

            this.renderPingIcon(var1, var14, var24 - (var12 ? 9 : 0), var25, var26);
         }
      }

      if (var37 != null) {
         var16 += var33 * 9 + 1;
         fill(var1, var2 / 2 - var17 / 2 - 1, var16 - 1, var2 / 2 + var17 / 2 + 1, var16 + var37.size() * 9, -2147483648);

         for(FormattedCharSequence var45 : var37) {
            int var46 = this.minecraft.font.width(var45);
            this.minecraft.font.drawShadow(var1, var45, (float)(var2 / 2 - var46 / 2), (float)var16, -1);
            var16 += 9;
         }
      }
   }

   protected void renderPingIcon(PoseStack var1, int var2, int var3, int var4, PlayerInfo var5) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
      boolean var6 = false;
      byte var7;
      if (var5.getLatency() < 0) {
         var7 = 5;
      } else if (var5.getLatency() < 150) {
         var7 = 0;
      } else if (var5.getLatency() < 300) {
         var7 = 1;
      } else if (var5.getLatency() < 600) {
         var7 = 2;
      } else if (var5.getLatency() < 1000) {
         var7 = 3;
      } else {
         var7 = 4;
      }

      this.setBlitOffset(this.getBlitOffset() + 100);
      this.blit(var1, var3 + var2 - 11, var4, 0, 176 + var7 * 8, 10, 8);
      this.setBlitOffset(this.getBlitOffset() - 100);
   }

   private void renderTablistScore(Objective var1, int var2, String var3, int var4, int var5, PlayerInfo var6, PoseStack var7) {
      int var8 = var1.getScoreboard().getOrCreatePlayerScore(var3, var1).getScore();
      if (var1.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
         RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
         long var9 = Util.getMillis();
         if (this.visibilityId == var6.getRenderVisibilityId()) {
            if (var8 < var6.getLastHealth()) {
               var6.setLastHealthTime(var9);
               var6.setHealthBlinkTime((long)(this.gui.getGuiTicks() + 20));
            } else if (var8 > var6.getLastHealth()) {
               var6.setLastHealthTime(var9);
               var6.setHealthBlinkTime((long)(this.gui.getGuiTicks() + 10));
            }
         }

         if (var9 - var6.getLastHealthTime() > 1000L || this.visibilityId != var6.getRenderVisibilityId()) {
            var6.setLastHealth(var8);
            var6.setDisplayHealth(var8);
            var6.setLastHealthTime(var9);
         }

         var6.setRenderVisibilityId(this.visibilityId);
         var6.setLastHealth(var8);
         int var11 = Mth.ceil((float)Math.max(var8, var6.getDisplayHealth()) / 2.0F);
         int var12 = Math.max(Mth.ceil((float)(var8 / 2)), Math.max(Mth.ceil((float)(var6.getDisplayHealth() / 2)), 10));
         boolean var13 = var6.getHealthBlinkTime() > (long)this.gui.getGuiTicks()
            && (var6.getHealthBlinkTime() - (long)this.gui.getGuiTicks()) / 3L % 2L == 1L;
         if (var11 > 0) {
            int var14 = Mth.floor(Math.min((float)(var5 - var4 - 4) / (float)var12, 9.0F));
            if (var14 > 3) {
               for(int var15 = var11; var15 < var12; ++var15) {
                  this.blit(var7, var4 + var15 * var14, var2, var13 ? 25 : 16, 0, 9, 9);
               }

               for(int var19 = 0; var19 < var11; ++var19) {
                  this.blit(var7, var4 + var19 * var14, var2, var13 ? 25 : 16, 0, 9, 9);
                  if (var13) {
                     if (var19 * 2 + 1 < var6.getDisplayHealth()) {
                        this.blit(var7, var4 + var19 * var14, var2, 70, 0, 9, 9);
                     }

                     if (var19 * 2 + 1 == var6.getDisplayHealth()) {
                        this.blit(var7, var4 + var19 * var14, var2, 79, 0, 9, 9);
                     }
                  }

                  if (var19 * 2 + 1 < var8) {
                     this.blit(var7, var4 + var19 * var14, var2, var19 >= 10 ? 160 : 52, 0, 9, 9);
                  }

                  if (var19 * 2 + 1 == var8) {
                     this.blit(var7, var4 + var19 * var14, var2, var19 >= 10 ? 169 : 61, 0, 9, 9);
                  }
               }
            } else {
               float var20 = Mth.clamp((float)var8 / 20.0F, 0.0F, 1.0F);
               int var16 = (int)((1.0F - var20) * 255.0F) << 16 | (int)(var20 * 255.0F) << 8;
               String var17 = (float)var8 / 2.0F + "";
               if (var5 - this.minecraft.font.width(var17 + "hp") >= var4) {
                  var17 = var17 + "hp";
               }

               this.minecraft.font.drawShadow(var7, var17, (float)((var5 + var4) / 2 - this.minecraft.font.width(var17) / 2), (float)var2, var16);
            }
         }
      } else {
         String var18 = "" + ChatFormatting.YELLOW + var8;
         this.minecraft.font.drawShadow(var7, var18, (float)(var5 - this.minecraft.font.width(var18)), (float)var2, 16777215);
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

   static class PlayerInfoComparator implements Comparator<PlayerInfo> {
      PlayerInfoComparator() {
         super();
      }

      public int compare(PlayerInfo var1, PlayerInfo var2) {
         PlayerTeam var3 = var1.getTeam();
         PlayerTeam var4 = var2.getTeam();
         return ComparisonChain.start()
            .compareTrueFirst(var1.getGameMode() != GameType.SPECTATOR, var2.getGameMode() != GameType.SPECTATOR)
            .compare(var3 != null ? var3.getName() : "", var4 != null ? var4.getName() : "")
            .compare(var1.getProfile().getName(), var2.getProfile().getName(), String::compareToIgnoreCase)
            .result();
      }
   }
}
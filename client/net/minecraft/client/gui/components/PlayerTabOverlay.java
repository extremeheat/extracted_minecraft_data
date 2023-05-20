package net.minecraft.client.gui.components;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
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
   private static final Comparator<PlayerInfo> PLAYER_COMPARATOR = Comparator.<PlayerInfo>comparingInt(
         var0 -> var0.getGameMode() == GameType.SPECTATOR ? 1 : 0
      )
      .thenComparing(var0 -> Optionull.mapOrDefault(var0.getTeam(), PlayerTeam::getName, ""))
      .thenComparing(var0 -> var0.getProfile().getName(), String::compareToIgnoreCase);
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
   private boolean visible;
   private final Map<UUID, PlayerTabOverlay.HealthState> healthStates = new Object2ObjectOpenHashMap();

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
      if (this.visible != var1) {
         this.healthStates.clear();
         this.visible = var1;
         if (var1) {
            MutableComponent var2 = ComponentUtils.formatList(this.getPlayerInfos(), Component.literal(", "), this::getNameForDisplay);
            this.minecraft.getNarrator().sayNow(Component.translatable("multiplayer.player.list.narration", var2));
         }
      }
   }

   private List<PlayerInfo> getPlayerInfos() {
      return this.minecraft.player.connection.getListedOnlinePlayers().stream().sorted(PLAYER_COMPARATOR).limit(80L).toList();
   }

   public void render(PoseStack var1, int var2, Scoreboard var3, @Nullable Objective var4) {
      List var5 = this.getPlayerInfos();
      int var6 = 0;
      int var7 = 0;

      for(PlayerInfo var9 : var5) {
         int var10 = this.minecraft.font.width(this.getNameForDisplay(var9));
         var6 = Math.max(var6, var10);
         if (var4 != null && var4.getRenderType() != ObjectiveCriteria.RenderType.HEARTS) {
            var10 = this.minecraft.font.width(" " + var3.getOrCreatePlayerScore(var9.getProfile().getName(), var4).getScore());
            var7 = Math.max(var7, var10);
         }
      }

      if (!this.healthStates.isEmpty()) {
         Set var30 = var5.stream().map(var0 -> var0.getProfile().getId()).collect(Collectors.toSet());
         this.healthStates.keySet().removeIf(var1x -> !var30.contains(var1x));
      }

      int var31 = var5.size();
      int var32 = var31;

      int var34;
      for(var34 = 1; var32 > 20; var32 = (var31 + var34 - 1) / var34) {
         ++var34;
      }

      boolean var11 = this.minecraft.isLocalServer() || this.minecraft.getConnection().getConnection().isEncrypted();
      int var12;
      if (var4 != null) {
         if (var4.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
            var12 = 90;
         } else {
            var12 = var7;
         }
      } else {
         var12 = 0;
      }

      int var13 = Math.min(var34 * ((var11 ? 9 : 0) + var6 + var12 + 13), var2 - 50) / var34;
      int var14 = var2 / 2 - (var13 * var34 + (var34 - 1) * 5) / 2;
      int var15 = 10;
      int var16 = var13 * var34 + (var34 - 1) * 5;
      List var17 = null;
      if (this.header != null) {
         var17 = this.minecraft.font.split(this.header, var2 - 50);

         for(FormattedCharSequence var19 : var17) {
            var16 = Math.max(var16, this.minecraft.font.width(var19));
         }
      }

      List var36 = null;
      if (this.footer != null) {
         var36 = this.minecraft.font.split(this.footer, var2 - 50);

         for(FormattedCharSequence var20 : var36) {
            var16 = Math.max(var16, this.minecraft.font.width(var20));
         }
      }

      if (var17 != null) {
         fill(var1, var2 / 2 - var16 / 2 - 1, var15 - 1, var2 / 2 + var16 / 2 + 1, var15 + var17.size() * 9, -2147483648);

         for(FormattedCharSequence var40 : var17) {
            int var21 = this.minecraft.font.width(var40);
            this.minecraft.font.drawShadow(var1, var40, (float)(var2 / 2 - var21 / 2), (float)var15, -1);
            var15 += 9;
         }

         ++var15;
      }

      fill(var1, var2 / 2 - var16 / 2 - 1, var15 - 1, var2 / 2 + var16 / 2 + 1, var15 + var32 * 9, -2147483648);
      int var39 = this.minecraft.options.getBackgroundColor(553648127);

      for(int var41 = 0; var41 < var31; ++var41) {
         int var43 = var41 / var32;
         int var22 = var41 % var32;
         int var23 = var14 + var43 * var13 + var43 * 5;
         int var24 = var15 + var22 * 9;
         fill(var1, var23, var24, var23 + var13, var24 + 8, var39);
         RenderSystem.enableBlend();
         if (var41 < var5.size()) {
            PlayerInfo var25 = (PlayerInfo)var5.get(var41);
            GameProfile var26 = var25.getProfile();
            if (var11) {
               Player var27 = this.minecraft.level.getPlayerByUUID(var26.getId());
               boolean var28 = var27 != null && LivingEntityRenderer.isEntityUpsideDown(var27);
               boolean var29 = var27 != null && var27.isModelPartShown(PlayerModelPart.HAT);
               RenderSystem.setShaderTexture(0, var25.getSkinLocation());
               PlayerFaceRenderer.draw(var1, var23, var24, 8, var29, var28);
               var23 += 9;
            }

            this.minecraft
               .font
               .drawShadow(var1, this.getNameForDisplay(var25), (float)var23, (float)var24, var25.getGameMode() == GameType.SPECTATOR ? -1862270977 : -1);
            if (var4 != null && var25.getGameMode() != GameType.SPECTATOR) {
               int var46 = var23 + var6 + 1;
               int var47 = var46 + var12;
               if (var47 - var46 > 5) {
                  this.renderTablistScore(var4, var24, var26.getName(), var46, var47, var26.getId(), var1);
               }
            }

            this.renderPingIcon(var1, var13, var23 - (var11 ? 9 : 0), var24, var25);
         }
      }

      if (var36 != null) {
         var15 += var32 * 9 + 1;
         fill(var1, var2 / 2 - var16 / 2 - 1, var15 - 1, var2 / 2 + var16 / 2 + 1, var15 + var36.size() * 9, -2147483648);

         for(FormattedCharSequence var44 : var36) {
            int var45 = this.minecraft.font.width(var44);
            this.minecraft.font.drawShadow(var1, var44, (float)(var2 / 2 - var45 / 2), (float)var15, -1);
            var15 += 9;
         }
      }
   }

   protected void renderPingIcon(PoseStack var1, int var2, int var3, int var4, PlayerInfo var5) {
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

      var1.pushPose();
      var1.translate(0.0F, 0.0F, 100.0F);
      blit(var1, var3 + var2 - 11, var4, 0, 176 + var7 * 8, 10, 8);
      var1.popPose();
   }

   private void renderTablistScore(Objective var1, int var2, String var3, int var4, int var5, UUID var6, PoseStack var7) {
      int var8 = var1.getScoreboard().getOrCreatePlayerScore(var3, var1).getScore();
      if (var1.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
         this.renderTablistHearts(var2, var4, var5, var6, var7, var8);
      } else {
         String var9 = "" + ChatFormatting.YELLOW + var8;
         this.minecraft.font.drawShadow(var7, var9, (float)(var5 - this.minecraft.font.width(var9)), (float)var2, 16777215);
      }
   }

   private void renderTablistHearts(int var1, int var2, int var3, UUID var4, PoseStack var5, int var6) {
      PlayerTabOverlay.HealthState var7 = this.healthStates.computeIfAbsent(var4, var1x -> new PlayerTabOverlay.HealthState(var6));
      var7.update(var6, (long)this.gui.getGuiTicks());
      int var8 = Mth.positiveCeilDiv(Math.max(var6, var7.displayedValue()), 2);
      int var9 = Math.max(var6, Math.max(var7.displayedValue(), 20)) / 2;
      boolean var10 = var7.isBlinking((long)this.gui.getGuiTicks());
      if (var8 > 0) {
         RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
         int var11 = Mth.floor(Math.min((float)(var3 - var2 - 4) / (float)var9, 9.0F));
         if (var11 <= 3) {
            float var16 = Mth.clamp((float)var6 / 20.0F, 0.0F, 1.0F);
            int var13 = (int)((1.0F - var16) * 255.0F) << 16 | (int)(var16 * 255.0F) << 8;
            String var14 = (float)var6 / 2.0F + "";
            if (var3 - this.minecraft.font.width(var14 + "hp") >= var2) {
               var14 = var14 + "hp";
            }

            this.minecraft.font.drawShadow(var5, var14, (float)((var3 + var2 - this.minecraft.font.width(var14)) / 2), (float)var1, var13);
         } else {
            for(int var12 = var8; var12 < var9; ++var12) {
               blit(var5, var2 + var12 * var11, var1, var10 ? 25 : 16, 0, 9, 9);
            }

            for(int var15 = 0; var15 < var8; ++var15) {
               blit(var5, var2 + var15 * var11, var1, var10 ? 25 : 16, 0, 9, 9);
               if (var10) {
                  if (var15 * 2 + 1 < var7.displayedValue()) {
                     blit(var5, var2 + var15 * var11, var1, 70, 0, 9, 9);
                  }

                  if (var15 * 2 + 1 == var7.displayedValue()) {
                     blit(var5, var2 + var15 * var11, var1, 79, 0, 9, 9);
                  }
               }

               if (var15 * 2 + 1 < var6) {
                  blit(var5, var2 + var15 * var11, var1, var15 >= 10 ? 160 : 52, 0, 9, 9);
               }

               if (var15 * 2 + 1 == var6) {
                  blit(var5, var2 + var15 * var11, var1, var15 >= 10 ? 169 : 61, 0, 9, 9);
               }
            }
         }
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

   static class HealthState {
      private static final long DISPLAY_UPDATE_DELAY = 20L;
      private static final long DECREASE_BLINK_DURATION = 20L;
      private static final long INCREASE_BLINK_DURATION = 10L;
      private int lastValue;
      private int displayedValue;
      private long lastUpdateTick;
      private long blinkUntilTick;

      public HealthState(int var1) {
         super();
         this.displayedValue = var1;
         this.lastValue = var1;
      }

      public void update(int var1, long var2) {
         if (var1 != this.lastValue) {
            long var4 = var1 < this.lastValue ? 20L : 10L;
            this.blinkUntilTick = var2 + var4;
            this.lastValue = var1;
            this.lastUpdateTick = var2;
         }

         if (var2 - this.lastUpdateTick > 20L) {
            this.displayedValue = var1;
         }
      }

      public int displayedValue() {
         return this.displayedValue;
      }

      public boolean isBlinking(long var1) {
         return this.blinkUntilTick > var1 && (this.blinkUntilTick - var1) % 6L >= 3L;
      }
   }
}

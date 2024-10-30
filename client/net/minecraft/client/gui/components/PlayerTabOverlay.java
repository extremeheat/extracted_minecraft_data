package net.minecraft.client.gui.components;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class PlayerTabOverlay {
   private static final ResourceLocation PING_UNKNOWN_SPRITE = ResourceLocation.withDefaultNamespace("icon/ping_unknown");
   private static final ResourceLocation PING_1_SPRITE = ResourceLocation.withDefaultNamespace("icon/ping_1");
   private static final ResourceLocation PING_2_SPRITE = ResourceLocation.withDefaultNamespace("icon/ping_2");
   private static final ResourceLocation PING_3_SPRITE = ResourceLocation.withDefaultNamespace("icon/ping_3");
   private static final ResourceLocation PING_4_SPRITE = ResourceLocation.withDefaultNamespace("icon/ping_4");
   private static final ResourceLocation PING_5_SPRITE = ResourceLocation.withDefaultNamespace("icon/ping_5");
   private static final ResourceLocation HEART_CONTAINER_BLINKING_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/container_blinking");
   private static final ResourceLocation HEART_CONTAINER_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/container");
   private static final ResourceLocation HEART_FULL_BLINKING_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/full_blinking");
   private static final ResourceLocation HEART_HALF_BLINKING_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/half_blinking");
   private static final ResourceLocation HEART_ABSORBING_FULL_BLINKING_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/absorbing_full_blinking");
   private static final ResourceLocation HEART_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/full");
   private static final ResourceLocation HEART_ABSORBING_HALF_BLINKING_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/absorbing_half_blinking");
   private static final ResourceLocation HEART_HALF_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/half");
   private static final Comparator<PlayerInfo> PLAYER_COMPARATOR = Comparator.comparingInt((var0) -> {
      return -var0.getTabListOrder();
   }).thenComparingInt((var0) -> {
      return var0.getGameMode() == GameType.SPECTATOR ? 1 : 0;
   }).thenComparing((var0) -> {
      return (String)Optionull.mapOrDefault(var0.getTeam(), PlayerTeam::getName, "");
   }).thenComparing((var0) -> {
      return var0.getProfile().getName();
   }, String::compareToIgnoreCase);
   public static final int MAX_ROWS_PER_COL = 20;
   private final Minecraft minecraft;
   private final Gui gui;
   @Nullable
   private Component footer;
   @Nullable
   private Component header;
   private boolean visible;
   private final Map<UUID, HealthState> healthStates = new Object2ObjectOpenHashMap();

   public PlayerTabOverlay(Minecraft var1, Gui var2) {
      super();
      this.minecraft = var1;
      this.gui = var2;
   }

   public Component getNameForDisplay(PlayerInfo var1) {
      return var1.getTabListDisplayName() != null ? this.decorateName(var1, var1.getTabListDisplayName().copy()) : this.decorateName(var1, PlayerTeam.formatNameForTeam(var1.getTeam(), Component.literal(var1.getProfile().getName())));
   }

   private Component decorateName(PlayerInfo var1, MutableComponent var2) {
      return var1.getGameMode() == GameType.SPECTATOR ? var2.withStyle(ChatFormatting.ITALIC) : var2;
   }

   public void setVisible(boolean var1) {
      if (this.visible != var1) {
         this.healthStates.clear();
         this.visible = var1;
         if (var1) {
            MutableComponent var2 = ComponentUtils.formatList(this.getPlayerInfos(), (Component)Component.literal(", "), this::getNameForDisplay);
            this.minecraft.getNarrator().sayNow((Component)Component.translatable("multiplayer.player.list.narration", var2));
         }
      }

   }

   private List<PlayerInfo> getPlayerInfos() {
      return this.minecraft.player.connection.getListedOnlinePlayers().stream().sorted(PLAYER_COMPARATOR).limit(80L).toList();
   }

   public void render(GuiGraphics var1, int var2, Scoreboard var3, @Nullable Objective var4) {
      List var5 = this.getPlayerInfos();
      ArrayList var6 = new ArrayList(var5.size());
      int var7 = this.minecraft.font.width(" ");
      int var8 = 0;
      int var9 = 0;

      Component var12;
      int var13;
      MutableComponent var14;
      int var15;
      for(Iterator var10 = var5.iterator(); var10.hasNext(); var6.add(new ScoreDisplayEntry(var12, var13, var14, var15))) {
         PlayerInfo var11 = (PlayerInfo)var10.next();
         var12 = this.getNameForDisplay(var11);
         var8 = Math.max(var8, this.minecraft.font.width((FormattedText)var12));
         var13 = 0;
         var14 = null;
         var15 = 0;
         if (var4 != null) {
            ScoreHolder var16 = ScoreHolder.fromGameProfile(var11.getProfile());
            ReadOnlyScoreInfo var17 = var3.getPlayerScoreInfo(var16, var4);
            if (var17 != null) {
               var13 = var17.value();
            }

            if (var4.getRenderType() != ObjectiveCriteria.RenderType.HEARTS) {
               NumberFormat var18 = var4.numberFormatOrDefault(StyledFormat.PLAYER_LIST_DEFAULT);
               var14 = ReadOnlyScoreInfo.safeFormatValue(var17, var18);
               var15 = this.minecraft.font.width((FormattedText)var14);
               var9 = Math.max(var9, var15 > 0 ? var7 + var15 : 0);
            }
         }
      }

      if (!this.healthStates.isEmpty()) {
         Set var32 = (Set)var5.stream().map((var0) -> {
            return var0.getProfile().getId();
         }).collect(Collectors.toSet());
         this.healthStates.keySet().removeIf((var1x) -> {
            return !var32.contains(var1x);
         });
      }

      int var33 = var5.size();
      int var34 = var33;

      int var35;
      for(var35 = 1; var34 > 20; var34 = (var33 + var35 - 1) / var35) {
         ++var35;
      }

      boolean var36 = this.minecraft.isLocalServer() || this.minecraft.getConnection().getConnection().isEncrypted();
      int var37;
      if (var4 != null) {
         if (var4.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
            var37 = 90;
         } else {
            var37 = var9;
         }
      } else {
         var37 = 0;
      }

      var15 = Math.min(var35 * ((var36 ? 9 : 0) + var8 + var37 + 13), var2 - 50) / var35;
      int var38 = var2 / 2 - (var15 * var35 + (var35 - 1) * 5) / 2;
      int var39 = 10;
      int var40 = var15 * var35 + (var35 - 1) * 5;
      List var19 = null;
      if (this.header != null) {
         var19 = this.minecraft.font.split(this.header, var2 - 50);

         FormattedCharSequence var21;
         for(Iterator var20 = var19.iterator(); var20.hasNext(); var40 = Math.max(var40, this.minecraft.font.width(var21))) {
            var21 = (FormattedCharSequence)var20.next();
         }
      }

      List var41 = null;
      FormattedCharSequence var22;
      Iterator var42;
      if (this.footer != null) {
         var41 = this.minecraft.font.split(this.footer, var2 - 50);

         for(var42 = var41.iterator(); var42.hasNext(); var40 = Math.max(var40, this.minecraft.font.width(var22))) {
            var22 = (FormattedCharSequence)var42.next();
         }
      }

      int var10001;
      int var10002;
      int var10003;
      int var10005;
      int var23;
      if (var19 != null) {
         var10001 = var2 / 2 - var40 / 2 - 1;
         var10002 = var39 - 1;
         var10003 = var2 / 2 + var40 / 2 + 1;
         var10005 = var19.size();
         Objects.requireNonNull(this.minecraft.font);
         var1.fill(var10001, var10002, var10003, var39 + var10005 * 9, -2147483648);

         for(var42 = var19.iterator(); var42.hasNext(); var39 += 9) {
            var22 = (FormattedCharSequence)var42.next();
            var23 = this.minecraft.font.width(var22);
            var1.drawString(this.minecraft.font, (FormattedCharSequence)var22, var2 / 2 - var23 / 2, var39, -1);
            Objects.requireNonNull(this.minecraft.font);
         }

         ++var39;
      }

      var1.fill(var2 / 2 - var40 / 2 - 1, var39 - 1, var2 / 2 + var40 / 2 + 1, var39 + var34 * 9, -2147483648);
      int var43 = this.minecraft.options.getBackgroundColor(553648127);

      int var24;
      for(int var44 = 0; var44 < var33; ++var44) {
         var23 = var44 / var34;
         var24 = var44 % var34;
         int var25 = var38 + var23 * var15 + var23 * 5;
         int var26 = var39 + var24 * 9;
         var1.fill(var25, var26, var25 + var15, var26 + 8, var43);
         if (var44 < var5.size()) {
            PlayerInfo var27 = (PlayerInfo)var5.get(var44);
            ScoreDisplayEntry var28 = (ScoreDisplayEntry)var6.get(var44);
            GameProfile var29 = var27.getProfile();
            if (var36) {
               Player var30 = this.minecraft.level.getPlayerByUUID(var29.getId());
               boolean var31 = var30 != null && LivingEntityRenderer.isEntityUpsideDown(var30);
               PlayerFaceRenderer.draw(var1, var27.getSkin().texture(), var25, var26, 8, var27.showHat(), var31, -1);
               var25 += 9;
            }

            var1.drawString(this.minecraft.font, var28.name, var25, var26, var27.getGameMode() == GameType.SPECTATOR ? -1862270977 : -1);
            if (var4 != null && var27.getGameMode() != GameType.SPECTATOR) {
               int var47 = var25 + var8 + 1;
               int var48 = var47 + var37;
               if (var48 - var47 > 5) {
                  this.renderTablistScore(var4, var26, var28, var47, var48, var29.getId(), var1);
               }
            }

            this.renderPingIcon(var1, var15, var25 - (var36 ? 9 : 0), var26, var27);
         }
      }

      if (var41 != null) {
         var39 += var34 * 9 + 1;
         var10001 = var2 / 2 - var40 / 2 - 1;
         var10002 = var39 - 1;
         var10003 = var2 / 2 + var40 / 2 + 1;
         var10005 = var41.size();
         Objects.requireNonNull(this.minecraft.font);
         var1.fill(var10001, var10002, var10003, var39 + var10005 * 9, -2147483648);

         for(Iterator var45 = var41.iterator(); var45.hasNext(); var39 += 9) {
            FormattedCharSequence var46 = (FormattedCharSequence)var45.next();
            var24 = this.minecraft.font.width(var46);
            var1.drawString(this.minecraft.font, (FormattedCharSequence)var46, var2 / 2 - var24 / 2, var39, -1);
            Objects.requireNonNull(this.minecraft.font);
         }
      }

   }

   protected void renderPingIcon(GuiGraphics var1, int var2, int var3, int var4, PlayerInfo var5) {
      ResourceLocation var6;
      if (var5.getLatency() < 0) {
         var6 = PING_UNKNOWN_SPRITE;
      } else if (var5.getLatency() < 150) {
         var6 = PING_5_SPRITE;
      } else if (var5.getLatency() < 300) {
         var6 = PING_4_SPRITE;
      } else if (var5.getLatency() < 600) {
         var6 = PING_3_SPRITE;
      } else if (var5.getLatency() < 1000) {
         var6 = PING_2_SPRITE;
      } else {
         var6 = PING_1_SPRITE;
      }

      var1.pose().pushPose();
      var1.pose().translate(0.0F, 0.0F, 100.0F);
      var1.blitSprite(RenderType::guiTextured, (ResourceLocation)var6, var3 + var2 - 11, var4, 10, 8);
      var1.pose().popPose();
   }

   private void renderTablistScore(Objective var1, int var2, ScoreDisplayEntry var3, int var4, int var5, UUID var6, GuiGraphics var7) {
      if (var1.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
         this.renderTablistHearts(var2, var4, var5, var6, var7, var3.score);
      } else if (var3.formattedScore != null) {
         var7.drawString(this.minecraft.font, var3.formattedScore, var5 - var3.scoreWidth, var2, 16777215);
      }

   }

   private void renderTablistHearts(int var1, int var2, int var3, UUID var4, GuiGraphics var5, int var6) {
      HealthState var7 = (HealthState)this.healthStates.computeIfAbsent(var4, (var1x) -> {
         return new HealthState(var6);
      });
      var7.update(var6, (long)this.gui.getGuiTicks());
      int var8 = Mth.positiveCeilDiv(Math.max(var6, var7.displayedValue()), 2);
      int var9 = Math.max(var6, Math.max(var7.displayedValue(), 20)) / 2;
      boolean var10 = var7.isBlinking((long)this.gui.getGuiTicks());
      if (var8 > 0) {
         int var11 = Mth.floor(Math.min((float)(var3 - var2 - 4) / (float)var9, 9.0F));
         int var13;
         if (var11 <= 3) {
            float var17 = Mth.clamp((float)var6 / 20.0F, 0.0F, 1.0F);
            var13 = (int)((1.0F - var17) * 255.0F) << 16 | (int)(var17 * 255.0F) << 8;
            float var14 = (float)var6 / 2.0F;
            MutableComponent var15 = Component.translatable("multiplayer.player.list.hp", var14);
            MutableComponent var16;
            if (var3 - this.minecraft.font.width((FormattedText)var15) >= var2) {
               var16 = var15;
            } else {
               var16 = Component.literal(Float.toString(var14));
            }

            var5.drawString(this.minecraft.font, (Component)var16, (var3 + var2 - this.minecraft.font.width((FormattedText)var16)) / 2, var1, var13);
         } else {
            ResourceLocation var12 = var10 ? HEART_CONTAINER_BLINKING_SPRITE : HEART_CONTAINER_SPRITE;

            for(var13 = var8; var13 < var9; ++var13) {
               var5.blitSprite(RenderType::guiTextured, (ResourceLocation)var12, var2 + var13 * var11, var1, 9, 9);
            }

            for(var13 = 0; var13 < var8; ++var13) {
               var5.blitSprite(RenderType::guiTextured, (ResourceLocation)var12, var2 + var13 * var11, var1, 9, 9);
               if (var10) {
                  if (var13 * 2 + 1 < var7.displayedValue()) {
                     var5.blitSprite(RenderType::guiTextured, (ResourceLocation)HEART_FULL_BLINKING_SPRITE, var2 + var13 * var11, var1, 9, 9);
                  }

                  if (var13 * 2 + 1 == var7.displayedValue()) {
                     var5.blitSprite(RenderType::guiTextured, (ResourceLocation)HEART_HALF_BLINKING_SPRITE, var2 + var13 * var11, var1, 9, 9);
                  }
               }

               if (var13 * 2 + 1 < var6) {
                  var5.blitSprite(RenderType::guiTextured, (ResourceLocation)(var13 >= 10 ? HEART_ABSORBING_FULL_BLINKING_SPRITE : HEART_FULL_SPRITE), var2 + var13 * var11, var1, 9, 9);
               }

               if (var13 * 2 + 1 == var6) {
                  var5.blitSprite(RenderType::guiTextured, (ResourceLocation)(var13 >= 10 ? HEART_ABSORBING_HALF_BLINKING_SPRITE : HEART_HALF_SPRITE), var2 + var13 * var11, var1, 9, 9);
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

   private static record ScoreDisplayEntry(Component name, int score, @Nullable Component formattedScore, int scoreWidth) {
      final Component name;
      final int score;
      @Nullable
      final Component formattedScore;
      final int scoreWidth;

      ScoreDisplayEntry(Component var1, int var2, @Nullable Component var3, int var4) {
         super();
         this.name = var1;
         this.score = var2;
         this.formattedScore = var3;
         this.scoreWidth = var4;
      }

      public Component name() {
         return this.name;
      }

      public int score() {
         return this.score;
      }

      @Nullable
      public Component formattedScore() {
         return this.formattedScore;
      }

      public int scoreWidth() {
         return this.scoreWidth;
      }
   }

   private static class HealthState {
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

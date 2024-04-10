package net.minecraft.world;

import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public abstract class BossEvent {
   private final UUID id;
   protected Component name;
   protected float progress;
   protected BossEvent.BossBarColor color;
   protected BossEvent.BossBarOverlay overlay;
   protected boolean darkenScreen;
   protected boolean playBossMusic;
   protected boolean createWorldFog;

   public BossEvent(UUID var1, Component var2, BossEvent.BossBarColor var3, BossEvent.BossBarOverlay var4) {
      super();
      this.id = var1;
      this.name = var2;
      this.color = var3;
      this.overlay = var4;
      this.progress = 1.0F;
   }

   public UUID getId() {
      return this.id;
   }

   public Component getName() {
      return this.name;
   }

   public void setName(Component var1) {
      this.name = var1;
   }

   public float getProgress() {
      return this.progress;
   }

   public void setProgress(float var1) {
      this.progress = var1;
   }

   public BossEvent.BossBarColor getColor() {
      return this.color;
   }

   public void setColor(BossEvent.BossBarColor var1) {
      this.color = var1;
   }

   public BossEvent.BossBarOverlay getOverlay() {
      return this.overlay;
   }

   public void setOverlay(BossEvent.BossBarOverlay var1) {
      this.overlay = var1;
   }

   public boolean shouldDarkenScreen() {
      return this.darkenScreen;
   }

   public BossEvent setDarkenScreen(boolean var1) {
      this.darkenScreen = var1;
      return this;
   }

   public boolean shouldPlayBossMusic() {
      return this.playBossMusic;
   }

   public BossEvent setPlayBossMusic(boolean var1) {
      this.playBossMusic = var1;
      return this;
   }

   public BossEvent setCreateWorldFog(boolean var1) {
      this.createWorldFog = var1;
      return this;
   }

   public boolean shouldCreateWorldFog() {
      return this.createWorldFog;
   }

   public static enum BossBarColor {
      PINK("pink", ChatFormatting.RED),
      BLUE("blue", ChatFormatting.BLUE),
      RED("red", ChatFormatting.DARK_RED),
      GREEN("green", ChatFormatting.GREEN),
      YELLOW("yellow", ChatFormatting.YELLOW),
      PURPLE("purple", ChatFormatting.DARK_BLUE),
      WHITE("white", ChatFormatting.WHITE);

      private final String name;
      private final ChatFormatting formatting;

      private BossBarColor(final String param3, final ChatFormatting param4) {
         this.name = nullxx;
         this.formatting = nullxxx;
      }

      public ChatFormatting getFormatting() {
         return this.formatting;
      }

      public String getName() {
         return this.name;
      }

      public static BossEvent.BossBarColor byName(String var0) {
         for (BossEvent.BossBarColor var4 : values()) {
            if (var4.name.equals(var0)) {
               return var4;
            }
         }

         return WHITE;
      }
   }

   public static enum BossBarOverlay {
      PROGRESS("progress"),
      NOTCHED_6("notched_6"),
      NOTCHED_10("notched_10"),
      NOTCHED_12("notched_12"),
      NOTCHED_20("notched_20");

      private final String name;

      private BossBarOverlay(final String param3) {
         this.name = nullxx;
      }

      public String getName() {
         return this.name;
      }

      public static BossEvent.BossBarOverlay byName(String var0) {
         for (BossEvent.BossBarOverlay var4 : values()) {
            if (var4.name.equals(var0)) {
               return var4;
            }
         }

         return PROGRESS;
      }
   }
}

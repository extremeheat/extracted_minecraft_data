package net.minecraft.client.gui.components;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.phys.Vec3;

public class BossHealthOverlay {
   private static final int BAR_WIDTH = 182;
   private static final int BAR_HEIGHT = 5;
   private static final ResourceLocation[] BAR_BACKGROUND_SPRITES = new ResourceLocation[]{
      new ResourceLocation("boss_bar/pink_background"),
      new ResourceLocation("boss_bar/blue_background"),
      new ResourceLocation("boss_bar/red_background"),
      new ResourceLocation("boss_bar/green_background"),
      new ResourceLocation("boss_bar/yellow_background"),
      new ResourceLocation("boss_bar/purple_background"),
      new ResourceLocation("boss_bar/white_background")
   };
   private static final ResourceLocation[] BAR_PROGRESS_SPRITES = new ResourceLocation[]{
      new ResourceLocation("boss_bar/pink_progress"),
      new ResourceLocation("boss_bar/blue_progress"),
      new ResourceLocation("boss_bar/red_progress"),
      new ResourceLocation("boss_bar/green_progress"),
      new ResourceLocation("boss_bar/yellow_progress"),
      new ResourceLocation("boss_bar/purple_progress"),
      new ResourceLocation("boss_bar/white_progress")
   };
   private static final ResourceLocation[] OVERLAY_BACKGROUND_SPRITES = new ResourceLocation[]{
      new ResourceLocation("boss_bar/notched_6_background"),
      new ResourceLocation("boss_bar/notched_10_background"),
      new ResourceLocation("boss_bar/notched_12_background"),
      new ResourceLocation("boss_bar/notched_20_background")
   };
   private static final ResourceLocation[] OVERLAY_PROGRESS_SPRITES = new ResourceLocation[]{
      new ResourceLocation("boss_bar/notched_6_progress"),
      new ResourceLocation("boss_bar/notched_10_progress"),
      new ResourceLocation("boss_bar/notched_12_progress"),
      new ResourceLocation("boss_bar/notched_20_progress")
   };
   private final Minecraft minecraft;
   final Map<UUID, LerpingBossEvent> events = Maps.newLinkedHashMap();

   public BossHealthOverlay(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(GuiGraphics var1) {
      if (!this.events.isEmpty()) {
         this.minecraft.getProfiler().push("bossHealth");
         int var2 = var1.guiWidth();
         int var3 = 12;

         for(LerpingBossEvent var5 : this.events.values()) {
            if (var5.isActiveFor(this.minecraft.cameraEntity.position())) {
               int var6 = var2 / 2 - 91;
               this.drawBar(var1, var6, var3, var5);
               Component var8 = var5.getName();
               int var9 = this.minecraft.font.width(var8);
               int var10 = var2 / 2 - var9 / 2;
               int var11 = var3 - 9;
               var1.drawString(this.minecraft.font, var8, var10, var11, 16777215);
               var3 += 10 + 9;
               if (var3 >= var1.guiHeight() / 3) {
                  break;
               }
            }
         }

         this.minecraft.getProfiler().pop();
      }
   }

   private void drawBar(GuiGraphics var1, int var2, int var3, BossEvent var4) {
      this.drawBar(var1, var2, var3, var4, 182, BAR_BACKGROUND_SPRITES, OVERLAY_BACKGROUND_SPRITES);
      int var5 = Mth.lerpDiscrete(var4.getProgress(), 0, 182);
      if (var5 > 0) {
         this.drawBar(var1, var2, var3, var4, var5, BAR_PROGRESS_SPRITES, OVERLAY_PROGRESS_SPRITES);
      }
   }

   private void drawBar(GuiGraphics var1, int var2, int var3, BossEvent var4, int var5, ResourceLocation[] var6, ResourceLocation[] var7) {
      RenderSystem.enableBlend();
      var1.blitSprite(var6[var4.getColor().ordinal()], 182, 5, 0, 0, var2, var3, var5, 5);
      if (var4.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
         var1.blitSprite(var7[var4.getOverlay().ordinal() - 1], 182, 5, 0, 0, var2, var3, var5, 5);
      }

      RenderSystem.disableBlend();
   }

   public void update(ClientboundBossEventPacket var1) {
      var1.dispatch(
         new ClientboundBossEventPacket.Handler() {
            @Override
            public void add(
               UUID var1,
               Component var2,
               float var3,
               BossEvent.BossBarColor var4,
               BossEvent.BossBarOverlay var5,
               boolean var6,
               boolean var7,
               boolean var8,
               Vec3 var9,
               int var10
            ) {
               BossHealthOverlay.this.events.put(var1, new LerpingBossEvent(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10));
            }
   
            @Override
            public void remove(UUID var1) {
               BossHealthOverlay.this.events.remove(var1);
            }
   
            @Override
            public void updateProgress(UUID var1, float var2) {
               BossHealthOverlay.this.events.get(var1).setProgress(var2);
            }
   
            @Override
            public void updateName(UUID var1, Component var2) {
               BossHealthOverlay.this.events.get(var1).setName(var2);
            }
   
            @Override
            public void updateLocation(UUID var1, Vec3 var2, int var3) {
               LerpingBossEvent var4 = BossHealthOverlay.this.events.get(var1);
               var4.setLocation(var2, var3);
            }
   
            @Override
            public void updateStyle(UUID var1, BossEvent.BossBarColor var2, BossEvent.BossBarOverlay var3) {
               LerpingBossEvent var4 = BossHealthOverlay.this.events.get(var1);
               var4.setColor(var2);
               var4.setOverlay(var3);
            }
   
            @Override
            public void updateProperties(UUID var1, boolean var2, boolean var3, boolean var4) {
               LerpingBossEvent var5 = BossHealthOverlay.this.events.get(var1);
               var5.setDarkenScreen(var2);
               var5.setPlayBossMusic(var3);
               var5.setCreateWorldFog(var4);
            }
         }
      );
   }

   public void reset() {
      this.events.clear();
   }

   public boolean shouldPlayMusic() {
      if (!this.events.isEmpty()) {
         for(BossEvent var2 : this.events.values()) {
            if (var2.shouldPlayBossMusic()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean shouldDarkenScreen() {
      if (!this.events.isEmpty()) {
         for(BossEvent var2 : this.events.values()) {
            if (var2.shouldDarkenScreen()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean shouldCreateWorldFog() {
      if (!this.events.isEmpty()) {
         for(BossEvent var2 : this.events.values()) {
            if (var2.shouldCreateWorldFog()) {
               return true;
            }
         }
      }

      return false;
   }
}

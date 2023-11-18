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
import net.minecraft.world.BossEvent;

public class BossHealthOverlay {
   private static final ResourceLocation GUI_BARS_LOCATION = new ResourceLocation("textures/gui/bars.png");
   private static final int BAR_WIDTH = 182;
   private static final int BAR_HEIGHT = 5;
   private static final int OVERLAY_OFFSET = 80;
   private final Minecraft minecraft;
   final Map<UUID, LerpingBossEvent> events = Maps.newLinkedHashMap();

   public BossHealthOverlay(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(GuiGraphics var1) {
      if (!this.events.isEmpty()) {
         int var2 = var1.guiWidth();
         int var3 = 12;

         for(LerpingBossEvent var5 : this.events.values()) {
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
   }

   private void drawBar(GuiGraphics var1, int var2, int var3, BossEvent var4) {
      this.drawBar(var1, var2, var3, var4, 182, 0);
      int var5 = (int)(var4.getProgress() * 183.0F);
      if (var5 > 0) {
         this.drawBar(var1, var2, var3, var4, var5, 5);
      }
   }

   private void drawBar(GuiGraphics var1, int var2, int var3, BossEvent var4, int var5, int var6) {
      var1.blit(GUI_BARS_LOCATION, var2, var3, 0, var4.getColor().ordinal() * 5 * 2 + var6, var5, 5);
      if (var4.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
         RenderSystem.enableBlend();
         var1.blit(GUI_BARS_LOCATION, var2, var3, 0, 80 + (var4.getOverlay().ordinal() - 1) * 5 * 2 + var6, var5, 5);
         RenderSystem.disableBlend();
      }
   }

   public void update(ClientboundBossEventPacket var1) {
      var1.dispatch(
         new ClientboundBossEventPacket.Handler() {
            @Override
            public void add(
               UUID var1, Component var2, float var3, BossEvent.BossBarColor var4, BossEvent.BossBarOverlay var5, boolean var6, boolean var7, boolean var8
            ) {
               BossHealthOverlay.this.events.put(var1, new LerpingBossEvent(var1, var2, var3, var4, var5, var6, var7, var8));
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

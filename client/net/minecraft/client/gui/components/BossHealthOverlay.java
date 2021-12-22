package net.minecraft.client.gui.components;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;

public class BossHealthOverlay extends GuiComponent {
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

   public void render(PoseStack var1) {
      if (!this.events.isEmpty()) {
         int var2 = this.minecraft.getWindow().getGuiScaledWidth();
         int var3 = 12;
         Iterator var4 = this.events.values().iterator();

         while(var4.hasNext()) {
            LerpingBossEvent var5 = (LerpingBossEvent)var4.next();
            int var6 = var2 / 2 - 91;
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, GUI_BARS_LOCATION);
            this.drawBar(var1, var6, var3, var5);
            Component var8 = var5.getName();
            int var9 = this.minecraft.font.width((FormattedText)var8);
            int var10 = var2 / 2 - var9 / 2;
            int var11 = var3 - 9;
            this.minecraft.font.drawShadow(var1, var8, (float)var10, (float)var11, 16777215);
            Objects.requireNonNull(this.minecraft.font);
            var3 += 10 + 9;
            if (var3 >= this.minecraft.getWindow().getGuiScaledHeight() / 3) {
               break;
            }
         }

      }
   }

   private void drawBar(PoseStack var1, int var2, int var3, BossEvent var4) {
      this.blit(var1, var2, var3, 0, var4.getColor().ordinal() * 5 * 2, 182, 5);
      if (var4.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
         this.blit(var1, var2, var3, 0, 80 + (var4.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
      }

      int var5 = (int)(var4.getProgress() * 183.0F);
      if (var5 > 0) {
         this.blit(var1, var2, var3, 0, var4.getColor().ordinal() * 5 * 2 + 5, var5, 5);
         if (var4.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
            this.blit(var1, var2, var3, 0, 80 + (var4.getOverlay().ordinal() - 1) * 5 * 2 + 5, var5, 5);
         }
      }

   }

   public void update(ClientboundBossEventPacket var1) {
      var1.dispatch(new ClientboundBossEventPacket.Handler() {
         public void add(UUID var1, Component var2, float var3, BossEvent.BossBarColor var4, BossEvent.BossBarOverlay var5, boolean var6, boolean var7, boolean var8) {
            BossHealthOverlay.this.events.put(var1, new LerpingBossEvent(var1, var2, var3, var4, var5, var6, var7, var8));
         }

         public void remove(UUID var1) {
            BossHealthOverlay.this.events.remove(var1);
         }

         public void updateProgress(UUID var1, float var2) {
            ((LerpingBossEvent)BossHealthOverlay.this.events.get(var1)).setProgress(var2);
         }

         public void updateName(UUID var1, Component var2) {
            ((LerpingBossEvent)BossHealthOverlay.this.events.get(var1)).setName(var2);
         }

         public void updateStyle(UUID var1, BossEvent.BossBarColor var2, BossEvent.BossBarOverlay var3) {
            LerpingBossEvent var4 = (LerpingBossEvent)BossHealthOverlay.this.events.get(var1);
            var4.setColor(var2);
            var4.setOverlay(var3);
         }

         public void updateProperties(UUID var1, boolean var2, boolean var3, boolean var4) {
            LerpingBossEvent var5 = (LerpingBossEvent)BossHealthOverlay.this.events.get(var1);
            var5.setDarkenScreen(var2);
            var5.setPlayBossMusic(var3);
            var5.setCreateWorldFog(var4);
         }
      });
   }

   public void reset() {
      this.events.clear();
   }

   public boolean shouldPlayMusic() {
      if (!this.events.isEmpty()) {
         Iterator var1 = this.events.values().iterator();

         while(var1.hasNext()) {
            BossEvent var2 = (BossEvent)var1.next();
            if (var2.shouldPlayBossMusic()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean shouldDarkenScreen() {
      if (!this.events.isEmpty()) {
         Iterator var1 = this.events.values().iterator();

         while(var1.hasNext()) {
            BossEvent var2 = (BossEvent)var1.next();
            if (var2.shouldDarkenScreen()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean shouldCreateWorldFog() {
      if (!this.events.isEmpty()) {
         Iterator var1 = this.events.values().iterator();

         while(var1.hasNext()) {
            BossEvent var2 = (BossEvent)var1.next();
            if (var2.shouldCreateWorldFog()) {
               return true;
            }
         }
      }

      return false;
   }
}

package net.minecraft.client.gui.components;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import java.util.Map;
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
   private final Minecraft minecraft;
   private final Map<UUID, LerpingBossEvent> events = Maps.newLinkedHashMap();

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
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.minecraft.getTextureManager().bind(GUI_BARS_LOCATION);
            this.drawBar(var1, var6, var3, var5);
            Component var8 = var5.getName();
            int var9 = this.minecraft.font.width((FormattedText)var8);
            int var10 = var2 / 2 - var9 / 2;
            int var11 = var3 - 9;
            this.minecraft.font.drawShadow(var1, var8, (float)var10, (float)var11, 16777215);
            this.minecraft.font.getClass();
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

      int var5 = (int)(var4.getPercent() * 183.0F);
      if (var5 > 0) {
         this.blit(var1, var2, var3, 0, var4.getColor().ordinal() * 5 * 2 + 5, var5, 5);
         if (var4.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
            this.blit(var1, var2, var3, 0, 80 + (var4.getOverlay().ordinal() - 1) * 5 * 2 + 5, var5, 5);
         }
      }

   }

   public void update(ClientboundBossEventPacket var1) {
      if (var1.getOperation() == ClientboundBossEventPacket.Operation.ADD) {
         this.events.put(var1.getId(), new LerpingBossEvent(var1));
      } else if (var1.getOperation() == ClientboundBossEventPacket.Operation.REMOVE) {
         this.events.remove(var1.getId());
      } else {
         ((LerpingBossEvent)this.events.get(var1.getId())).update(var1);
      }

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

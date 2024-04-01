package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.sounds.SoundEvents;

public class SproutRespawnScreen extends Screen {
   private static final Component TITLE = Component.literal("potato");
   private int transparency;
   private final int delay = 20;
   private int counter;

   public SproutRespawnScreen() {
      super(TITLE);
   }

   @Override
   protected void init() {
      if (this.minecraft.player != null) {
         this.minecraft.player.makeSound(SoundEvents.PLAYER_SPROUT_RESPAWN_1);
      }
   }

   @Override
   public void tick() {
      ++this.counter;
      if (this.counter >= 20) {
         this.onClose();
      }
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.fill(0, 0, this.width, this.height, -16777216);
      var1.setColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      super.renderBackground(var1, var2, var3, var4);
   }

   @Override
   public void onClose() {
      this.sproutRespawn();
   }

   private void sproutRespawn() {
      this.minecraft.setScreen(null);
      if (this.minecraft.player != null) {
         this.minecraft.player.connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.SPROUT_RESPAWN));
      }
   }
}

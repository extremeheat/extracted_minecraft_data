package net.minecraft.client.gui.screens.inventory;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSetCommandMinecartPacket;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.level.BaseCommandBlock;

public class MinecartCommandBlockEditScreen extends AbstractCommandBlockEditScreen {
   private final BaseCommandBlock commandBlock;

   public MinecartCommandBlockEditScreen(BaseCommandBlock var1) {
      super();
      this.commandBlock = var1;
   }

   public BaseCommandBlock getCommandBlock() {
      return this.commandBlock;
   }

   int getPreviousY() {
      return 150;
   }

   protected void init() {
      super.init();
      this.commandEdit.setValue(this.getCommandBlock().getCommand());
   }

   protected void populateAndSendPacket(BaseCommandBlock var1) {
      if (var1 instanceof MinecartCommandBlock.MinecartCommandBase var2) {
         this.minecraft.getConnection().send((Packet)(new ServerboundSetCommandMinecartPacket(var2.getMinecart().getId(), this.commandEdit.getValue(), var1.isTrackOutput())));
      }

   }
}

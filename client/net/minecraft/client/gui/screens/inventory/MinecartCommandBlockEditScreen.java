package net.minecraft.client.gui.screens.inventory;

import net.minecraft.network.protocol.game.ServerboundSetCommandMinecartPacket;
import net.minecraft.world.entity.vehicle.minecart.MinecartCommandBlock;
import net.minecraft.world.level.BaseCommandBlock;

public class MinecartCommandBlockEditScreen extends AbstractCommandBlockEditScreen {
   private final MinecartCommandBlock minecart;

   public MinecartCommandBlockEditScreen(MinecartCommandBlock var1) {
      super();
      this.minecart = var1;
   }

   public BaseCommandBlock getCommandBlock() {
      return this.minecart.getCommandBlock();
   }

   int getPreviousY() {
      return 150;
   }

   protected void init() {
      super.init();
      this.commandEdit.setValue(this.getCommandBlock().getCommand());
   }

   protected void populateAndSendPacket() {
      this.minecraft.getConnection().send(new ServerboundSetCommandMinecartPacket(this.minecart.getId(), this.commandEdit.getValue(), this.minecart.getCommandBlock().isTrackOutput()));
   }
}

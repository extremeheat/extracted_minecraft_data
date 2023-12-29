package net.minecraft.client.gui.screens.inventory;

import net.minecraft.network.protocol.game.ServerboundSetCommandMinecartPacket;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.level.BaseCommandBlock;

public class MinecartCommandBlockEditScreen extends AbstractCommandBlockEditScreen {
   private final BaseCommandBlock commandBlock;

   public MinecartCommandBlockEditScreen(BaseCommandBlock var1) {
      super();
      this.commandBlock = var1;
   }

   @Override
   public BaseCommandBlock getCommandBlock() {
      return this.commandBlock;
   }

   @Override
   int getPreviousY() {
      return 150;
   }

   @Override
   protected void init() {
      super.init();
      this.commandEdit.setValue(this.getCommandBlock().getCommand());
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected void populateAndSendPacket(BaseCommandBlock var1) {
      if (var1 instanceof MinecartCommandBlock.MinecartCommandBase var2) {
         this.minecraft
            .getConnection()
            .send(new ServerboundSetCommandMinecartPacket(var2.getMinecart().getId(), this.commandEdit.getValue(), var1.isTrackOutput()));
      }
   }
}

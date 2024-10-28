package net.minecraft.server.gui;

import java.util.Vector;
import javax.swing.JList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class PlayerListComponent extends JList<String> {
   private final MinecraftServer server;
   private int tickCount;

   public PlayerListComponent(MinecraftServer var1) {
      super();
      this.server = var1;
      var1.addTickable(this::tick);
   }

   public void tick() {
      if (this.tickCount++ % 20 == 0) {
         Vector var1 = new Vector();

         for(int var2 = 0; var2 < this.server.getPlayerList().getPlayers().size(); ++var2) {
            var1.add(((ServerPlayer)this.server.getPlayerList().getPlayers().get(var2)).getGameProfile().getName());
         }

         this.setListData(var1);
      }

   }
}

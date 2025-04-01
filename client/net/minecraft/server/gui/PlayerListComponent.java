package net.minecraft.server.gui;

import java.util.Vector;
import javax.swing.JList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TheGame;
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
         TheGame var2 = this.server.theGame();
         if (var2 != null) {
            for(ServerPlayer var4 : var2.playerList().getPlayers()) {
               var1.add(var4.getGameProfile().getName());
            }
         }

         this.setListData(var1);
      }

   }
}

package net.minecraft.client.grid;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.grid.GridCarrier;
import net.minecraft.world.grid.SubGrid;

public class ClientSubGrid extends SubGrid implements AutoCloseable {
   private final SubGridRenderer renderer = new SubGridRenderer(this);

   public ClientSubGrid(ClientLevel var1, GridCarrier var2) {
      super(var1, var2);
   }

   public SubGridRenderer getRenderer() {
      return this.renderer;
   }

   @Override
   public void close() {
      this.renderer.close();
   }
}

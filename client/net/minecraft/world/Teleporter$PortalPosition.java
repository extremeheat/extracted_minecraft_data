package net.minecraft.world;

import net.minecraft.util.ChunkCoordinates;

public class Teleporter$PortalPosition extends ChunkCoordinates {
   public long field_85087_d;

   public Teleporter$PortalPosition(Teleporter var1, int var2, int var3, int var4, long var5) {
      super(var2, var3, var4);
      this.field_85088_e = var1;
      this.field_85087_d = var5;
   }
}

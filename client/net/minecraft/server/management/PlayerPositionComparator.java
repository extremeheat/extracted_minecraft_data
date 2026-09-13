package net.minecraft.server.management;

import java.util.Comparator;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

public class PlayerPositionComparator implements Comparator {
   private final ChunkCoordinates field_82548_a;

   public PlayerPositionComparator(ChunkCoordinates var1) {
      super();
      this.field_82548_a = var1;
   }

   public int compare(EntityPlayerMP var1, EntityPlayerMP var2) {
      double var3 = var1.func_70092_e(
         (double)this.field_82548_a.field_71574_a, (double)this.field_82548_a.field_71572_b, (double)this.field_82548_a.field_71573_c
      );
      double var5 = var2.func_70092_e(
         (double)this.field_82548_a.field_71574_a, (double)this.field_82548_a.field_71572_b, (double)this.field_82548_a.field_71573_c
      );
      if (var3 < var5) {
         return -1;
      } else {
         return var3 > var5 ? 1 : 0;
      }
   }
}

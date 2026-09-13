package net.minecraft.network.play.server;

import java.util.Collection;

public class S20PacketEntityProperties$Snapshot {
   private final String field_151412_b;
   private final double field_151413_c;
   private final Collection field_151411_d;

   public S20PacketEntityProperties$Snapshot(S20PacketEntityProperties var1, String var2, double var3, Collection var5) {
      super();
      this.field_151414_a = var1;
      this.field_151412_b = var2;
      this.field_151413_c = var3;
      this.field_151411_d = var5;
   }

   public String func_151409_a() {
      return this.field_151412_b;
   }

   public double func_151410_b() {
      return this.field_151413_c;
   }

   public Collection func_151408_c() {
      return this.field_151411_d;
   }
}

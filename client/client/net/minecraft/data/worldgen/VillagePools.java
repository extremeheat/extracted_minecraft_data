package net.minecraft.data.worldgen;

import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class VillagePools {
   public VillagePools() {
      super();
   }

   public static void bootstrap(BootstrapContext<StructureTemplatePool> var0) {
      PlainVillagePools.bootstrap(var0);
      SnowyVillagePools.bootstrap(var0);
      SavannaVillagePools.bootstrap(var0);
      DesertVillagePools.bootstrap(var0);
      TaigaVillagePools.bootstrap(var0);
   }
}

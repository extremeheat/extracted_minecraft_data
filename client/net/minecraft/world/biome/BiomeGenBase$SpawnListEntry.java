package net.minecraft.world.biome;

import net.minecraft.util.WeightedRandom$Item;

public class BiomeGenBase$SpawnListEntry extends WeightedRandom$Item {
   public Class field_76300_b;
   public int field_76301_c;
   public int field_76299_d;

   public BiomeGenBase$SpawnListEntry(Class var1, int var2, int var3, int var4) {
      super(var2);
      this.field_76300_b = var1;
      this.field_76301_c = var3;
      this.field_76299_d = var4;
   }

   @Override
   public String toString() {
      return this.field_76300_b.getSimpleName() + "*(" + this.field_76301_c + "-" + this.field_76299_d + "):" + this.field_76292_a;
   }
}

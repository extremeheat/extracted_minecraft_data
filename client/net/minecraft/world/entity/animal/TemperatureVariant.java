package net.minecraft.world.entity.animal;

public enum TemperatureVariant {
   TEMPERATE("temperate"),
   WARM("warm"),
   COLD("cold");

   private final String id;

   private TemperatureVariant(final String var3) {
      this.id = var3;
   }

   public String getId() {
      return this.id;
   }

   // $FF: synthetic method
   private static TemperatureVariant[] $values() {
      return new TemperatureVariant[]{TEMPERATE, WARM, COLD};
   }
}

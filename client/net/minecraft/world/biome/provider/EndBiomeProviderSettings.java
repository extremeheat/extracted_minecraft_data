package net.minecraft.world.biome.provider;

public class EndBiomeProviderSettings implements IBiomeProviderSettings {
   private long field_205447_a;

   public EndBiomeProviderSettings() {
      super();
   }

   public EndBiomeProviderSettings func_205446_a(long var1) {
      this.field_205447_a = var1;
      return this;
   }

   public long func_205445_a() {
      return this.field_205447_a;
   }
}

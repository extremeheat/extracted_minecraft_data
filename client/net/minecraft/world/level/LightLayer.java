package net.minecraft.world.level;

public enum LightLayer {
   SKY(15),
   BLOCK(0);

   public final int surrounding;

   private LightLayer(int var3) {
      this.surrounding = var3;
   }

   // $FF: synthetic method
   private static LightLayer[] $values() {
      return new LightLayer[]{SKY, BLOCK};
   }
}

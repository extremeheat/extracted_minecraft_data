package net.minecraft.world.level.material;

public enum FogType {
   LAVA,
   WATER,
   POWDER_SNOW,
   DIMENSION_OR_BOSS,
   ATMOSPHERIC,
   NONE;

   private FogType() {
   }

   // $FF: synthetic method
   private static FogType[] $values() {
      return new FogType[]{LAVA, WATER, POWDER_SNOW, DIMENSION_OR_BOSS, ATMOSPHERIC, NONE};
   }
}

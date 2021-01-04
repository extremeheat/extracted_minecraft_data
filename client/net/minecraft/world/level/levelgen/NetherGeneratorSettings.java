package net.minecraft.world.level.levelgen;

public class NetherGeneratorSettings extends ChunkGeneratorSettings {
   public NetherGeneratorSettings() {
      super();
   }

   public int getBedrockFloorPosition() {
      return 0;
   }

   public int getBedrockRoofPosition() {
      return 127;
   }
}

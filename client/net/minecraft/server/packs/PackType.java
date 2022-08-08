package net.minecraft.server.packs;

import com.mojang.bridge.game.GameVersion;

public enum PackType {
   CLIENT_RESOURCES("assets", com.mojang.bridge.game.PackType.RESOURCE),
   SERVER_DATA("data", com.mojang.bridge.game.PackType.DATA);

   private final String directory;
   private final com.mojang.bridge.game.PackType bridgeType;

   private PackType(String var3, com.mojang.bridge.game.PackType var4) {
      this.directory = var3;
      this.bridgeType = var4;
   }

   public String getDirectory() {
      return this.directory;
   }

   public int getVersion(GameVersion var1) {
      return var1.getPackVersion(this.bridgeType);
   }

   // $FF: synthetic method
   private static PackType[] $values() {
      return new PackType[]{CLIENT_RESOURCES, SERVER_DATA};
   }
}

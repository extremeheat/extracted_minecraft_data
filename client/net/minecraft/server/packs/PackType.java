package net.minecraft.server.packs;

public enum PackType {
   CLIENT_RESOURCES("assets"),
   SERVER_DATA("data");

   private final String directory;

   private PackType(String var3) {
      this.directory = var3;
   }

   public String getDirectory() {
      return this.directory;
   }

   // $FF: synthetic method
   private static PackType[] $values() {
      return new PackType[]{CLIENT_RESOURCES, SERVER_DATA};
   }
}

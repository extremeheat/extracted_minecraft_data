package net.minecraft.server.packs.metadata.pack;

import net.minecraft.network.chat.Component;

public class PackMetadataSection {
   public static final PackMetadataSectionSerializer SERIALIZER = new PackMetadataSectionSerializer();
   private final Component description;
   private final int packFormat;

   public PackMetadataSection(Component var1, int var2) {
      super();
      this.description = var1;
      this.packFormat = var2;
   }

   public Component getDescription() {
      return this.description;
   }

   public int getPackFormat() {
      return this.packFormat;
   }
}

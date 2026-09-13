package net.minecraft.client.resources.data;

import net.minecraft.util.IChatComponent;

public class PackMetadataSection implements IMetadataSection {
   private final IChatComponent field_110464_a;
   private final int field_110463_b;

   public PackMetadataSection(IChatComponent var1, int var2) {
      super();
      this.field_110464_a = var1;
      this.field_110463_b = var2;
   }

   public IChatComponent func_152805_a() {
      return this.field_110464_a;
   }

   public int func_110462_b() {
      return this.field_110463_b;
   }
}

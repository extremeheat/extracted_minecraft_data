package net.minecraft.resources.data;

import net.minecraft.util.text.ITextComponent;

public class PackMetadataSection {
   public static final PackMetadataSectionSerializer field_198964_a = new PackMetadataSectionSerializer();
   private final ITextComponent field_198965_b;
   private final int field_198966_c;

   public PackMetadataSection(ITextComponent var1, int var2) {
      super();
      this.field_198965_b = var1;
      this.field_198966_c = var2;
   }

   public ITextComponent func_198963_a() {
      return this.field_198965_b;
   }

   public int func_198962_b() {
      return this.field_198966_c;
   }
}

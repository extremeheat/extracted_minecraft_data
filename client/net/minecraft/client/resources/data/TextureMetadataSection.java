package net.minecraft.client.resources.data;

public class TextureMetadataSection {
   public static final TextureMetadataSectionSerializer field_195819_a = new TextureMetadataSectionSerializer();
   private final boolean field_110482_a;
   private final boolean field_110481_b;

   public TextureMetadataSection(boolean var1, boolean var2) {
      super();
      this.field_110482_a = var1;
      this.field_110481_b = var2;
   }

   public boolean func_110479_a() {
      return this.field_110482_a;
   }

   public boolean func_110480_b() {
      return this.field_110481_b;
   }
}

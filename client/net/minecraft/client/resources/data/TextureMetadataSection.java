package net.minecraft.client.resources.data;

import java.util.Collections;
import java.util.List;

public class TextureMetadataSection implements IMetadataSection {
   private final boolean field_110482_a;
   private final boolean field_110481_b;
   private final List<Integer> field_148536_c;

   public TextureMetadataSection(boolean var1, boolean var2, List<Integer> var3) {
      super();
      this.field_110482_a = var1;
      this.field_110481_b = var2;
      this.field_148536_c = var3;
   }

   public boolean func_110479_a() {
      return this.field_110482_a;
   }

   public boolean func_110480_b() {
      return this.field_110481_b;
   }

   public List<Integer> func_148535_c() {
      return Collections.unmodifiableList(this.field_148536_c);
   }
}

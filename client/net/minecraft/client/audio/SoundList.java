package net.minecraft.client.audio;

import java.util.List;
import javax.annotation.Nullable;

public class SoundList {
   private final List<Sound> field_188702_a;
   private final boolean field_148575_b;
   private final String field_188703_c;

   public SoundList(List<Sound> var1, boolean var2, String var3) {
      super();
      this.field_188702_a = var1;
      this.field_148575_b = var2;
      this.field_188703_c = var3;
   }

   public List<Sound> func_188700_a() {
      return this.field_188702_a;
   }

   public boolean func_148574_b() {
      return this.field_148575_b;
   }

   @Nullable
   public String func_188701_c() {
      return this.field_188703_c;
   }
}

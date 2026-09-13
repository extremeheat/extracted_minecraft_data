package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class JsonException$Entry {
   private String field_151376_a = null;
   private final List field_151375_b = Lists.newArrayList();

   private JsonException$Entry() {
      super();
   }

   private void func_151373_a(String var1) {
      this.field_151375_b.add(0, var1);
   }

   public String func_151372_b() {
      return StringUtils.join(this.field_151375_b, "->");
   }

   @Override
   public String toString() {
      if (this.field_151376_a != null) {
         return !this.field_151375_b.isEmpty() ? this.field_151376_a + " " + this.func_151372_b() : this.field_151376_a;
      } else {
         return !this.field_151375_b.isEmpty() ? "(Unknown file) " + this.func_151372_b() : "(Unknown file)";
      }
   }
}

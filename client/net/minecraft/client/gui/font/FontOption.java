package net.minecraft.client.gui.font;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.StringRepresentable;

public enum FontOption implements StringRepresentable {
   UNIFORM("uniform"),
   JAPANESE_VARIANTS("jp");

   public static final Codec<FontOption> CODEC = StringRepresentable.<FontOption>fromEnum(FontOption::values);
   private final String name;

   private FontOption(final String var3) {
      this.name = var3;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static FontOption[] $values() {
      return new FontOption[]{UNIFORM, JAPANESE_VARIANTS};
   }

   public static class Filter {
      private final Map<FontOption, Boolean> values;
      public static final Codec<Filter> CODEC;
      public static final Filter ALWAYS_PASS;

      public Filter(Map<FontOption, Boolean> var1) {
         super();
         this.values = var1;
      }

      public boolean apply(Set<FontOption> var1) {
         for(Map.Entry var3 : this.values.entrySet()) {
            if (var1.contains(var3.getKey()) != (Boolean)var3.getValue()) {
               return false;
            }
         }

         return true;
      }

      public Filter merge(Filter var1) {
         HashMap var2 = new HashMap(var1.values);
         var2.putAll(this.values);
         return new Filter(Map.copyOf(var2));
      }

      static {
         CODEC = Codec.unboundedMap(FontOption.CODEC, Codec.BOOL).xmap(Filter::new, (var0) -> var0.values);
         ALWAYS_PASS = new Filter(Map.of());
      }
   }
}

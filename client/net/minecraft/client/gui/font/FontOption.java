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

   private FontOption(final String name) {
      this.name = name;
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

      public Filter(final Map<FontOption, Boolean> values) {
         super();
         this.values = values;
      }

      public boolean apply(final Set<FontOption> options) {
         for(Map.Entry<FontOption, Boolean> e : this.values.entrySet()) {
            if (options.contains(e.getKey()) != (Boolean)e.getValue()) {
               return false;
            }
         }

         return true;
      }

      public Filter merge(final Filter other) {
         Map<FontOption, Boolean> options = new HashMap(other.values);
         options.putAll(this.values);
         return new Filter(Map.copyOf(options));
      }

      static {
         CODEC = Codec.unboundedMap(FontOption.CODEC, Codec.BOOL).xmap(Filter::new, (p) -> p.values);
         ALWAYS_PASS = new Filter(Map.of());
      }
   }
}

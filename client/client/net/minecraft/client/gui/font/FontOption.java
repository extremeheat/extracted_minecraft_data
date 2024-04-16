package net.minecraft.client.gui.font;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.util.StringRepresentable;

public enum FontOption implements StringRepresentable {
   UNIFORM("uniform"),
   JAPANESE_VARIANTS("jp");

   public static final Codec<FontOption> CODEC = StringRepresentable.fromEnum(FontOption::values);
   private final String name;

   private FontOption(final String nullxx) {
      this.name = nullxx;
   }

   @Override
   public String getSerializedName() {
      return this.name;
   }

   public static class Filter {
      private final Map<FontOption, Boolean> values;
      public static final Codec<FontOption.Filter> CODEC = Codec.unboundedMap(FontOption.CODEC, Codec.BOOL).xmap(FontOption.Filter::new, var0 -> var0.values);
      public static final FontOption.Filter ALWAYS_PASS = new FontOption.Filter(Map.of());

      public Filter(Map<FontOption, Boolean> var1) {
         super();
         this.values = var1;
      }

      public boolean apply(Set<FontOption> var1) {
         for (Entry var3 : this.values.entrySet()) {
            if (var1.contains(var3.getKey()) != (Boolean)var3.getValue()) {
               return false;
            }
         }

         return true;
      }

      public FontOption.Filter merge(FontOption.Filter var1) {
         HashMap var2 = new HashMap<>(var1.values);
         var2.putAll(this.values);
         return new FontOption.Filter(Map.copyOf(var2));
      }
   }
}

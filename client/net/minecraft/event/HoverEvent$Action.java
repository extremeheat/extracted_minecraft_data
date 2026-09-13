package net.minecraft.event;

import com.google.common.collect.Maps;
import java.util.Map;

public enum HoverEvent$Action {
   SHOW_TEXT("show_text", true),
   SHOW_ACHIEVEMENT("show_achievement", true),
   SHOW_ITEM("show_item", true);

   private static final Map field_150690_d = Maps.newHashMap();
   private final boolean field_150691_e;
   private final String field_150688_f;

   private HoverEvent$Action(String var3, boolean var4) {
      this.field_150688_f = var3;
      this.field_150691_e = var4;
   }

   public boolean func_150686_a() {
      return this.field_150691_e;
   }

   public String func_150685_b() {
      return this.field_150688_f;
   }

   public static HoverEvent$Action func_150684_a(String var0) {
      return (HoverEvent$Action)field_150690_d.get(var0);
   }

   static {
      for(HoverEvent$Action var3 : values()) {
         field_150690_d.put(var3.func_150685_b(), var3);
      }
   }
}

package net.minecraft.event;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.IChatComponent;

public class HoverEvent {
   private final HoverEvent.Action field_150704_a;
   private final IChatComponent field_150703_b;

   public HoverEvent(HoverEvent.Action var1, IChatComponent var2) {
      super();
      this.field_150704_a = var1;
      this.field_150703_b = var2;
   }

   public HoverEvent.Action func_150701_a() {
      return this.field_150704_a;
   }

   public IChatComponent func_150702_b() {
      return this.field_150703_b;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         HoverEvent var2 = (HoverEvent)var1;
         if (this.field_150704_a != var2.field_150704_a) {
            return false;
         } else {
            if (this.field_150703_b != null) {
               if (!this.field_150703_b.equals(var2.field_150703_b)) {
                  return false;
               }
            } else if (var2.field_150703_b != null) {
               return false;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public String toString() {
      return "HoverEvent{action=" + this.field_150704_a + ", value='" + this.field_150703_b + '\'' + '}';
   }

   public int hashCode() {
      int var1 = this.field_150704_a.hashCode();
      var1 = 31 * var1 + (this.field_150703_b != null ? this.field_150703_b.hashCode() : 0);
      return var1;
   }

   public static enum Action {
      SHOW_TEXT("show_text", true),
      SHOW_ACHIEVEMENT("show_achievement", true),
      SHOW_ITEM("show_item", true),
      SHOW_ENTITY("show_entity", true);

      private static final Map<String, HoverEvent.Action> field_150690_d = Maps.newHashMap();
      private final boolean field_150691_e;
      private final String field_150688_f;

      private Action(String var3, boolean var4) {
         this.field_150688_f = var3;
         this.field_150691_e = var4;
      }

      public boolean func_150686_a() {
         return this.field_150691_e;
      }

      public String func_150685_b() {
         return this.field_150688_f;
      }

      public static HoverEvent.Action func_150684_a(String var0) {
         return (HoverEvent.Action)field_150690_d.get(var0);
      }

      static {
         HoverEvent.Action[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            HoverEvent.Action var3 = var0[var2];
            field_150690_d.put(var3.func_150685_b(), var3);
         }

      }
   }
}

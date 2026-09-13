package net.minecraft.event;

import com.google.common.collect.Maps;
import java.util.Map;

public enum ClickEvent$Action {
   OPEN_URL("open_url", true),
   OPEN_FILE("open_file", false),
   RUN_COMMAND("run_command", true),
   TWITCH_USER_INFO("twitch_user_info", false),
   SUGGEST_COMMAND("suggest_command", true);

   private static final Map field_150679_e = Maps.newHashMap();
   private final boolean field_150676_f;
   private final String field_150677_g;

   private ClickEvent$Action(String var3, boolean var4) {
      this.field_150677_g = var3;
      this.field_150676_f = var4;
   }

   public boolean func_150674_a() {
      return this.field_150676_f;
   }

   public String func_150673_b() {
      return this.field_150677_g;
   }

   public static ClickEvent$Action func_150672_a(String var0) {
      return (ClickEvent$Action)field_150679_e.get(var0);
   }

   static {
      for(ClickEvent$Action var3 : values()) {
         field_150679_e.put(var3.func_150673_b(), var3);
      }
   }
}

package net.minecraft.world.entity.player;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;

public enum ChatVisiblity implements OptionEnum {
   FULL(0, "options.chat.visibility.full"),
   SYSTEM(1, "options.chat.visibility.system"),
   HIDDEN(2, "options.chat.visibility.hidden");

   private static final IntFunction<ChatVisiblity> BY_ID = ByIdMap.continuous(ChatVisiblity::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
   private final int id;
   private final String key;

   private ChatVisiblity(int var3, String var4) {
      this.id = var3;
      this.key = var4;
   }

   @Override
   public int getId() {
      return this.id;
   }

   @Override
   public String getKey() {
      return this.key;
   }

   public static ChatVisiblity byId(int var0) {
      return BY_ID.apply(var0);
   }
}

package net.minecraft.util.text;

public enum ChatType {
   CHAT((byte)0),
   SYSTEM((byte)1),
   GAME_INFO((byte)2);

   private final byte field_192588_d;

   private ChatType(byte var3) {
      this.field_192588_d = var3;
   }

   public byte func_192583_a() {
      return this.field_192588_d;
   }

   public static ChatType func_192582_a(byte var0) {
      ChatType[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ChatType var4 = var1[var3];
         if (var0 == var4.field_192588_d) {
            return var4;
         }
      }

      return CHAT;
   }
}

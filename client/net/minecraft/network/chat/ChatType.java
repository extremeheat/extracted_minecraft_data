package net.minecraft.network.chat;

public enum ChatType {
   CHAT((byte)0, false),
   SYSTEM((byte)1, true),
   GAME_INFO((byte)2, true);

   private final byte index;
   private final boolean interrupt;

   private ChatType(byte var3, boolean var4) {
      this.index = var3;
      this.interrupt = var4;
   }

   public byte getIndex() {
      return this.index;
   }

   public static ChatType getForIndex(byte var0) {
      ChatType[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ChatType var4 = var1[var3];
         if (var0 == var4.index) {
            return var4;
         }
      }

      return CHAT;
   }

   public boolean shouldInterrupt() {
      return this.interrupt;
   }

   // $FF: synthetic method
   private static ChatType[] $values() {
      return new ChatType[]{CHAT, SYSTEM, GAME_INFO};
   }
}

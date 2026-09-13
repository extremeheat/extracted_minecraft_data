package net.minecraft.entity.player;

public enum EntityPlayer$EnumChatVisibility {
   FULL(0, "options.chat.visibility.full"),
   SYSTEM(1, "options.chat.visibility.system"),
   HIDDEN(2, "options.chat.visibility.hidden");

   private static final EntityPlayer$EnumChatVisibility[] field_151432_d = new EntityPlayer$EnumChatVisibility[values().length];
   private final int field_151433_e;
   private final String field_151430_f;

   private EntityPlayer$EnumChatVisibility(int var3, String var4) {
      this.field_151433_e = var3;
      this.field_151430_f = var4;
   }

   public int func_151428_a() {
      return this.field_151433_e;
   }

   public static EntityPlayer$EnumChatVisibility func_151426_a(int var0) {
      return field_151432_d[var0 % field_151432_d.length];
   }

   public String func_151429_b() {
      return this.field_151430_f;
   }

   static {
      for(EntityPlayer$EnumChatVisibility var3 : values()) {
         field_151432_d[var3.field_151433_e] = var3;
      }
   }
}

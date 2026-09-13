package net.minecraft.network;

import com.mojang.authlib.GameProfile;

public class ServerStatusResponse$PlayerCountData {
   private final int field_151336_a;
   private final int field_151334_b;
   private GameProfile[] field_151335_c;

   public ServerStatusResponse$PlayerCountData(int var1, int var2) {
      super();
      this.field_151336_a = var1;
      this.field_151334_b = var2;
   }

   public int func_151332_a() {
      return this.field_151336_a;
   }

   public int func_151333_b() {
      return this.field_151334_b;
   }

   public GameProfile[] func_151331_c() {
      return this.field_151335_c;
   }

   public void func_151330_a(GameProfile[] var1) {
      this.field_151335_c = var1;
   }
}

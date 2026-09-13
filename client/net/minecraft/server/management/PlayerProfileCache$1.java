package net.minecraft.server.management;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;

final class PlayerProfileCache$1 implements ProfileLookupCallback {
   PlayerProfileCache$1(GameProfile[] var1) {
      super();
      this.field_152667_a = var1;
   }

   public void onProfileLookupSucceeded(GameProfile var1) {
      this.field_152667_a[0] = var1;
   }

   public void onProfileLookupFailed(GameProfile var1, Exception var2) {
      this.field_152667_a[0] = null;
   }
}

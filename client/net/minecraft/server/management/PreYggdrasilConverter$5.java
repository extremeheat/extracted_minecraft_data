package net.minecraft.server.management;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import java.util.List;
import net.minecraft.server.MinecraftServer;

final class PreYggdrasilConverter$5 implements ProfileLookupCallback {
   PreYggdrasilConverter$5(MinecraftServer var1, List var2) {
      super();
      this.field_152741_a = var1;
      this.field_152742_b = var2;
   }

   public void onProfileLookupSucceeded(GameProfile var1) {
      this.field_152741_a.func_152358_ax().func_152649_a(var1);
      this.field_152742_b.add(var1);
   }

   public void onProfileLookupFailed(GameProfile var1, Exception var2) {
      PreYggdrasilConverter.access$000().warn("Could not lookup user whitelist entry for " + var1.getName(), var2);
   }
}

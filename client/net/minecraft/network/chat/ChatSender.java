package net.minecraft.network.chat;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.world.entity.player.ProfilePublicKey;

public record ChatSender(UUID b, @Nullable ProfilePublicKey c) {
   private final UUID profileId;
   @Nullable
   private final ProfilePublicKey profilePublicKey;
   public static final ChatSender SYSTEM;

   public ChatSender(UUID var1, @Nullable ProfilePublicKey var2) {
      super();
      this.profileId = var1;
      this.profilePublicKey = var2;
   }

   public boolean isSystem() {
      return SYSTEM.equals(this);
   }

   public UUID profileId() {
      return this.profileId;
   }

   @Nullable
   public ProfilePublicKey profilePublicKey() {
      return this.profilePublicKey;
   }

   static {
      SYSTEM = new ChatSender(Util.NIL_UUID, (ProfilePublicKey)null);
   }
}

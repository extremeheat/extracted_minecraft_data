package net.minecraft.server.level;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.Player;

public record ClientInformation(String language, int viewDistance, ChatVisiblity chatVisibility, boolean chatColors, int modelCustomisation, HumanoidArm mainHand, boolean textFilteringEnabled, boolean allowsListing, ParticleStatus particleStatus) {
   public static final int MAX_LANGUAGE_LENGTH = 16;

   public ClientInformation(FriendlyByteBuf var1) {
      this(var1.readUtf(16), var1.readByte(), (ChatVisiblity)var1.readEnum(ChatVisiblity.class), var1.readBoolean(), var1.readUnsignedByte(), (HumanoidArm)var1.readEnum(HumanoidArm.class), var1.readBoolean(), var1.readBoolean(), (ParticleStatus)var1.readEnum(ParticleStatus.class));
   }

   public ClientInformation(String var1, int var2, ChatVisiblity var3, boolean var4, int var5, HumanoidArm var6, boolean var7, boolean var8, ParticleStatus var9) {
      super();
      this.language = var1;
      this.viewDistance = var2;
      this.chatVisibility = var3;
      this.chatColors = var4;
      this.modelCustomisation = var5;
      this.mainHand = var6;
      this.textFilteringEnabled = var7;
      this.allowsListing = var8;
      this.particleStatus = var9;
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.language);
      var1.writeByte(this.viewDistance);
      var1.writeEnum(this.chatVisibility);
      var1.writeBoolean(this.chatColors);
      var1.writeByte(this.modelCustomisation);
      var1.writeEnum(this.mainHand);
      var1.writeBoolean(this.textFilteringEnabled);
      var1.writeBoolean(this.allowsListing);
      var1.writeEnum(this.particleStatus);
   }

   public static ClientInformation createDefault() {
      return new ClientInformation("en_us", 2, ChatVisiblity.FULL, true, 0, Player.DEFAULT_MAIN_HAND, false, false, ParticleStatus.ALL);
   }
}

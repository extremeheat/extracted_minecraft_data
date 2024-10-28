package net.minecraft.server.level;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.Player;

public record ClientInformation(String language, int viewDistance, ChatVisiblity chatVisibility, boolean chatColors, int modelCustomisation, HumanoidArm mainHand, boolean textFilteringEnabled, boolean allowsListing) {
   public static final int MAX_LANGUAGE_LENGTH = 16;

   public ClientInformation(FriendlyByteBuf var1) {
      this(var1.readUtf(16), var1.readByte(), (ChatVisiblity)var1.readEnum(ChatVisiblity.class), var1.readBoolean(), var1.readUnsignedByte(), (HumanoidArm)var1.readEnum(HumanoidArm.class), var1.readBoolean(), var1.readBoolean());
   }

   public ClientInformation(String language, int viewDistance, ChatVisiblity chatVisibility, boolean chatColors, int modelCustomisation, HumanoidArm mainHand, boolean textFilteringEnabled, boolean allowsListing) {
      super();
      this.language = language;
      this.viewDistance = viewDistance;
      this.chatVisibility = chatVisibility;
      this.chatColors = chatColors;
      this.modelCustomisation = modelCustomisation;
      this.mainHand = mainHand;
      this.textFilteringEnabled = textFilteringEnabled;
      this.allowsListing = allowsListing;
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
   }

   public static ClientInformation createDefault() {
      return new ClientInformation("en_us", 2, ChatVisiblity.FULL, true, 0, Player.DEFAULT_MAIN_HAND, false, false);
   }

   public String language() {
      return this.language;
   }

   public int viewDistance() {
      return this.viewDistance;
   }

   public ChatVisiblity chatVisibility() {
      return this.chatVisibility;
   }

   public boolean chatColors() {
      return this.chatColors;
   }

   public int modelCustomisation() {
      return this.modelCustomisation;
   }

   public HumanoidArm mainHand() {
      return this.mainHand;
   }

   public boolean textFilteringEnabled() {
      return this.textFilteringEnabled;
   }

   public boolean allowsListing() {
      return this.allowsListing;
   }
}

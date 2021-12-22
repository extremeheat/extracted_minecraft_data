package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;

public record ServerboundClientInformationPacket(String b, int c, ChatVisiblity d, boolean e, int f, HumanoidArm g, boolean h, boolean i) implements Packet<ServerGamePacketListener> {
   private final String language;
   private final int viewDistance;
   private final ChatVisiblity chatVisibility;
   private final boolean chatColors;
   private final int modelCustomisation;
   private final HumanoidArm mainHand;
   private final boolean textFilteringEnabled;
   private final boolean allowsListing;
   public static final int MAX_LANGUAGE_LENGTH = 16;

   public ServerboundClientInformationPacket(FriendlyByteBuf var1) {
      this(var1.readUtf(16), var1.readByte(), (ChatVisiblity)var1.readEnum(ChatVisiblity.class), var1.readBoolean(), var1.readUnsignedByte(), (HumanoidArm)var1.readEnum(HumanoidArm.class), var1.readBoolean(), var1.readBoolean());
   }

   public ServerboundClientInformationPacket(String var1, int var2, ChatVisiblity var3, boolean var4, int var5, HumanoidArm var6, boolean var7, boolean var8) {
      super();
      this.language = var1;
      this.viewDistance = var2;
      this.chatVisibility = var3;
      this.chatColors = var4;
      this.modelCustomisation = var5;
      this.mainHand = var6;
      this.textFilteringEnabled = var7;
      this.allowsListing = var8;
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

   public void handle(ServerGamePacketListener var1) {
      var1.handleClientInformation(this);
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

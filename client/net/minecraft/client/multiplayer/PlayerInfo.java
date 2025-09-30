package net.minecraft.client.multiplayer;

import com.mojang.authlib.GameProfile;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.chat.SignedMessageValidator;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;

public class PlayerInfo {
   private final GameProfile profile;
   @Nullable
   private Supplier<PlayerSkin> skinLookup;
   private GameType gameMode;
   private int latency;
   @Nullable
   private Component tabListDisplayName;
   private boolean showHat;
   @Nullable
   private RemoteChatSession chatSession;
   private SignedMessageValidator messageValidator;
   private int tabListOrder;

   public PlayerInfo(GameProfile var1, boolean var2) {
      super();
      this.gameMode = GameType.DEFAULT_MODE;
      this.showHat = true;
      this.profile = var1;
      this.messageValidator = fallbackMessageValidator(var2);
   }

   private static Supplier<PlayerSkin> createSkinLookup(GameProfile var0) {
      Minecraft var1 = Minecraft.getInstance();
      boolean var2 = !var1.isLocalPlayer(var0.id());
      return var1.getSkinManager().createLookup(var0, var2);
   }

   public GameProfile getProfile() {
      return this.profile;
   }

   @Nullable
   public RemoteChatSession getChatSession() {
      return this.chatSession;
   }

   public SignedMessageValidator getMessageValidator() {
      return this.messageValidator;
   }

   public boolean hasVerifiableChat() {
      return this.chatSession != null;
   }

   protected void setChatSession(RemoteChatSession var1) {
      this.chatSession = var1;
      this.messageValidator = var1.createMessageValidator(ProfilePublicKey.EXPIRY_GRACE_PERIOD);
   }

   protected void clearChatSession(boolean var1) {
      this.chatSession = null;
      this.messageValidator = fallbackMessageValidator(var1);
   }

   private static SignedMessageValidator fallbackMessageValidator(boolean var0) {
      return var0 ? SignedMessageValidator.REJECT_ALL : SignedMessageValidator.ACCEPT_UNSIGNED;
   }

   public GameType getGameMode() {
      return this.gameMode;
   }

   protected void setGameMode(GameType var1) {
      this.gameMode = var1;
   }

   public int getLatency() {
      return this.latency;
   }

   protected void setLatency(int var1) {
      this.latency = var1;
   }

   public PlayerSkin getSkin() {
      if (this.skinLookup == null) {
         this.skinLookup = createSkinLookup(this.profile);
      }

      return (PlayerSkin)this.skinLookup.get();
   }

   @Nullable
   public PlayerTeam getTeam() {
      return Minecraft.getInstance().level.getScoreboard().getPlayersTeam(this.getProfile().name());
   }

   public void setTabListDisplayName(@Nullable Component var1) {
      this.tabListDisplayName = var1;
   }

   @Nullable
   public Component getTabListDisplayName() {
      return this.tabListDisplayName;
   }

   public void setShowHat(boolean var1) {
      this.showHat = var1;
   }

   public boolean showHat() {
      return this.showHat;
   }

   public void setTabListOrder(int var1) {
      this.tabListOrder = var1;
   }

   public int getTabListOrder() {
      return this.tabListOrder;
   }
}

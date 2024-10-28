package net.minecraft.client.multiplayer;

import com.google.common.base.Suppliers;
import com.mojang.authlib.GameProfile;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.chat.SignedMessageValidator;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;

public class PlayerInfo {
   private final GameProfile profile;
   private final Supplier<PlayerSkin> skinLookup;
   private GameType gameMode;
   private int latency;
   @Nullable
   private Component tabListDisplayName;
   @Nullable
   private RemoteChatSession chatSession;
   private SignedMessageValidator messageValidator;

   public PlayerInfo(GameProfile var1, boolean var2) {
      super();
      this.gameMode = GameType.DEFAULT_MODE;
      this.profile = var1;
      this.messageValidator = fallbackMessageValidator(var2);
      com.google.common.base.Supplier var3 = Suppliers.memoize(() -> {
         return createSkinLookup(var1);
      });
      this.skinLookup = () -> {
         return (PlayerSkin)((Supplier)var3.get()).get();
      };
   }

   private static Supplier<PlayerSkin> createSkinLookup(GameProfile var0) {
      Minecraft var1 = Minecraft.getInstance();
      SkinManager var2 = var1.getSkinManager();
      CompletableFuture var3 = var2.getOrLoad(var0);
      boolean var4 = !var1.isLocalPlayer(var0.getId());
      PlayerSkin var5 = DefaultPlayerSkin.get(var0);
      return () -> {
         PlayerSkin var3x = (PlayerSkin)var3.getNow(var5);
         return var4 && !var3x.secure() ? var5 : var3x;
      };
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
      return (PlayerSkin)this.skinLookup.get();
   }

   @Nullable
   public PlayerTeam getTeam() {
      return Minecraft.getInstance().level.getScoreboard().getPlayersTeam(this.getProfile().getName());
   }

   public void setTabListDisplayName(@Nullable Component var1) {
      this.tabListDisplayName = var1;
   }

   @Nullable
   public Component getTabListDisplayName() {
      return this.tabListDisplayName;
   }
}

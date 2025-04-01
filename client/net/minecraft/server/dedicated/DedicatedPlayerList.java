package net.minecraft.server.dedicated;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import net.minecraft.server.TheGame;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.slf4j.Logger;

public class DedicatedPlayerList extends PlayerList {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final DedicatedServer dedicatedServer;

   public DedicatedPlayerList(DedicatedServer var1, TheGame var2, PlayerDataStorage var3) {
      super(var2, var3, var1.getProperties().maxPlayers);
      this.dedicatedServer = var1;
      DedicatedServerProperties var4 = var1.getProperties();
      this.setViewDistance(var4.viewDistance);
      this.setSimulationDistance(var4.simulationDistance);
      super.setUsingWhiteList((Boolean)var4.whiteList.get());
      this.loadUserBanList();
      this.saveUserBanList();
      this.loadIpBanList();
      this.saveIpBanList();
      this.loadOps();
      this.loadWhiteList();
      this.saveOps();
      if (!this.getWhiteList().getFile().exists()) {
         this.saveWhiteList();
      }

   }

   public void setUsingWhiteList(boolean var1) {
      super.setUsingWhiteList(var1);
      this.dedicatedServer.storeUsingWhiteList(this.theGame(), var1);
   }

   public void op(GameProfile var1) {
      super.op(var1);
      this.saveOps();
   }

   public void deop(GameProfile var1) {
      super.deop(var1);
      this.saveOps();
   }

   public void reloadWhiteList() {
      this.loadWhiteList();
   }

   private void saveIpBanList() {
      try {
         this.getIpBans().save();
      } catch (IOException var2) {
         LOGGER.warn("Failed to save ip banlist: ", var2);
      }

   }

   private void saveUserBanList() {
      try {
         this.getBans().save();
      } catch (IOException var2) {
         LOGGER.warn("Failed to save user banlist: ", var2);
      }

   }

   private void loadIpBanList() {
      try {
         this.getIpBans().load();
      } catch (IOException var2) {
         LOGGER.warn("Failed to load ip banlist: ", var2);
      }

   }

   private void loadUserBanList() {
      try {
         this.getBans().load();
      } catch (IOException var2) {
         LOGGER.warn("Failed to load user banlist: ", var2);
      }

   }

   private void loadOps() {
      try {
         this.getOps().load();
      } catch (Exception var2) {
         LOGGER.warn("Failed to load operators list: ", var2);
      }

   }

   private void saveOps() {
      try {
         this.getOps().save();
      } catch (Exception var2) {
         LOGGER.warn("Failed to save operators list: ", var2);
      }

   }

   private void loadWhiteList() {
      try {
         this.getWhiteList().load();
      } catch (Exception var2) {
         LOGGER.warn("Failed to load white-list: ", var2);
      }

   }

   private void saveWhiteList() {
      try {
         this.getWhiteList().save();
      } catch (Exception var2) {
         LOGGER.warn("Failed to save white-list: ", var2);
      }

   }

   public boolean isWhiteListed(GameProfile var1) {
      return !this.isUsingWhitelist() || this.isOp(var1) || this.getWhiteList().isWhiteListed(var1);
   }

   public boolean canBypassPlayerLimit(GameProfile var1) {
      return this.getOps().canBypassPlayerLimit(var1);
   }
}

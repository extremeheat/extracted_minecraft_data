package net.minecraft.client.gui.screens.social;

import com.mojang.authlib.services.response.PresenceStatus;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.friends.FriendEntry;
import net.minecraft.client.gui.screens.friends.FriendsListActions;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.PlayerSkin;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class PlayerEntry extends FriendEntry {
   private final MutableBoolean hasRecentMessages;
   private boolean isRemoved;
   public static final int SKIN_SHADE = ARGB.color(190, 0, 0, 0);

   private PlayerEntry(final Minecraft minecraft, final SocialInteractionsScreen socialInteractionsScreen, final UUID id, final String playerName, final PlayerSocialManager.Visibility visibility, final Supplier<PlayerSkin> skinGetter, final boolean chatReportable, final MutableBoolean hasRecentMessages) {
      super(minecraft, socialInteractionsScreen.getListContentWidth(), id, playerName, new PlayerStatus(PresenceStatus.ONLINE, visibility), () -> minecraft.gui.setScreen(new PlayerOptionsScreen(socialInteractionsScreen, id, playerName, new FriendsListActions(minecraft), skinGetter, true, chatReportable, hasRecentMessages.isTrue())));
      this.hasRecentMessages = hasRecentMessages;
   }

   public PlayerEntry(final Minecraft minecraft, final SocialInteractionsScreen socialInteractionsScreen, final UUID id, final String playerName, final PlayerSocialManager.Visibility visibility, final Supplier<PlayerSkin> skinGetter, final boolean chatReportable) {
      this(minecraft, socialInteractionsScreen, id, playerName, visibility, skinGetter, chatReportable, new MutableBoolean());
   }

   protected void extractPlayerFace(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractPlayerFace(graphics, mouseX, mouseY, a);
      if (this.isRemoved) {
         int skinX = this.playerFaceWidget.getX() + 1;
         int skinY = this.playerFaceWidget.getY() + 1;
         graphics.fill(skinX, skinY, skinX + 24, skinY + 24, SKIN_SHADE);
      }

   }

   public void setRemoved(final boolean isRemoved) {
      this.isRemoved = isRemoved;
      this.statusWidget.applyPresence(isRemoved ? PresenceStatus.OFFLINE : PresenceStatus.ONLINE);
   }

   public boolean isRemoved() {
      return this.isRemoved;
   }

   public void setHasRecentMessages(final boolean hasRecentMessages) {
      this.hasRecentMessages.setValue(hasRecentMessages);
   }

   public int prioritySortOrder() {
      if (this.minecraft.getReportingContext().hasDraftReportFor(this.playerId)) {
         return 0;
      } else {
         return this.playerId.version() == 2 ? 2 : 1;
      }
   }
}

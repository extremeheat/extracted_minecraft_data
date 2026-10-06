package net.minecraft.client.gui.screens.friends;

import com.google.common.util.concurrent.Runnables;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class FriendsListActions {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component ERROR_TOAST_GENERIC = Component.translatable("gui.friends.toast.generic_error");
   private final Map<UUID, Action> pendingFriendActions = new HashMap();
   private final Minecraft minecraft;
   private Runnable startFriendAction = Runnables.doNothing();
   private Runnable refreshFriendsList = Runnables.doNothing();
   private final Runnable updateListener = () -> this.refreshFriendsList.run();

   public FriendsListActions(final Minecraft minecraft) {
      super();
      this.minecraft = minecraft;
   }

   public void addFriendListUpdateListener(final Runnable startFriendAction, final Runnable refreshFriendsList) {
      this.startFriendAction = startFriendAction;
      this.refreshFriendsList = refreshFriendsList;
      this.minecraft.getPlayerSocialManager().addFriendListUpdateListener(this.updateListener);
   }

   public void removeFriendListUpdateListener() {
      this.startFriendAction = Runnables.doNothing();
      this.refreshFriendsList = Runnables.doNothing();
      this.minecraft.getPlayerSocialManager().removeFriendListUpdateListener(this.updateListener);
   }

   public Action getPendingAction(final UUID friendId) {
      return (Action)this.pendingFriendActions.getOrDefault(friendId, FriendsListActions.Action.NONE);
   }

   public void sendFriendRequest(final UUID friendId) {
      this.performFriendAction(friendId, FriendsListActions.Action.ADD_FRIEND, PlayerSocialManager::sendFriendRequest);
   }

   public void removeFriend(final UUID friendId) {
      this.performFriendAction(friendId, FriendsListActions.Action.REMOVE_FRIEND, PlayerSocialManager::removeFriend);
   }

   public void acceptIncomingFriendRequest(final UUID profileId) {
      this.performFriendAction(profileId, FriendsListActions.Action.ACCEPT_INCOMING, PlayerSocialManager::acceptIncomingFriendRequest);
   }

   public void declineIncomingFriendRequest(final UUID profileId) {
      this.performFriendAction(profileId, FriendsListActions.Action.DECLINE_INCOMING, PlayerSocialManager::declineIncomingFriendRequest);
   }

   public void revokeOutgoingFriendRequest(final UUID profileId) {
      this.performFriendAction(profileId, FriendsListActions.Action.REVOKE_OUTGOING, PlayerSocialManager::revokeOutgoingFriendRequest);
   }

   private void performFriendAction(final UUID profileId, final Action action, final BiFunction<PlayerSocialManager, UUID, CompletableFuture<?>> operation) {
      this.startFriendAction.run();
      this.pendingFriendActions.put(profileId, action);
      ((CompletableFuture)operation.apply(this.minecraft.getPlayerSocialManager(), profileId)).whenCompleteAsync((var2, var3) -> this.pendingFriendActions.remove(profileId), this.minecraft).thenRunAsync(this.updateListener, this.minecraft).exceptionally(this::onActionFailed);
   }

   private @Nullable Void onActionFailed(final Throwable ex) {
      LOGGER.error("Friend action failed", ex);
      this.minecraft.execute(() -> {
         SystemToast.addOrUpdate(this.minecraft.gui.toastManager(), SystemToast.SystemToastId.FRIEND_SYSTEM_NOTIFICATION, ERROR_TOAST_GENERIC, (Component)null);
         this.refreshFriendsList.run();
      });
      return null;
   }

   public static enum Action {
      NONE,
      ADD_FRIEND,
      REMOVE_FRIEND,
      ACCEPT_INCOMING,
      DECLINE_INCOMING,
      REVOKE_OUTGOING;

      private Action() {
      }

      // $FF: synthetic method
      private static Action[] $values() {
         return new Action[]{NONE, ADD_FRIEND, REMOVE_FRIEND, ACCEPT_INCOMING, DECLINE_INCOMING, REVOKE_OUTGOING};
      }
   }
}

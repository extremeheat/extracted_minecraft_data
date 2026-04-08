package net.minecraft.client.gui.screens.options;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.gui.components.LoadingDotsWidget;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.AbstractGameRulesScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundSetGameRulePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleMap;
import net.minecraft.world.level.gamerules.GameRules;
import org.jspecify.annotations.Nullable;

public class InWorldGameRulesScreen extends AbstractGameRulesScreen implements HasGamemasterPermissionReaction {
   private static final Component PENDING_TEXT = Component.translatable("editGamerule.inGame.downloadingGamerules");
   private final GameRuleMap initialValues = GameRuleMap.of();
   private final List<GameRule<?>> serverProvidedRules = new ArrayList();
   private final ClientPacketListener connection;
   private final Screen lastScreen;
   private @Nullable LoadingDotsWidget loadingDotsWidget;
   private boolean receivedServerValues = false;

   public InWorldGameRulesScreen(final ClientPacketListener connection, final Consumer<Optional<GameRules>> exitCallback, final Screen lastScreen) {
      super(new GameRules(connection.enabledFeatures()), exitCallback);
      this.connection = connection;
      this.lastScreen = lastScreen;
   }

   protected void initContent() {
      this.loadingDotsWidget = new LoadingDotsWidget(this.font, PENDING_TEXT);
      this.layout.addToContents(this.loadingDotsWidget);
      this.connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_GAMERULE_VALUES));
   }

   protected void onDone() {
      List<ServerboundSetGameRulePacket.Entry> changedEntries = new ArrayList();
      this.initialValues.keySet().forEach((rule) -> this.collectChangedGameRule(rule, changedEntries));
      if (!changedEntries.isEmpty()) {
         this.connection.send(new ServerboundSetGameRulePacket(changedEntries));
      }

      this.closeAndApplyChanges();
   }

   private <T> void collectChangedGameRule(final GameRule<T> rule, final List<ServerboundSetGameRulePacket.Entry> entries) {
      if (this.hasGameRuleChanged(rule)) {
         T currentValue = (T)this.gameRules.get(rule);
         BuiltInRegistries.GAME_RULE.getResourceKey(rule).ifPresent((key) -> entries.add(new ServerboundSetGameRulePacket.Entry(key, rule.serialize(currentValue))));
      }

   }

   public void onClose() {
      if (this.hasPendingChanges()) {
         this.minecraft.setScreen(new ConfirmScreen((confirmed) -> {
            if (confirmed) {
               this.closeAndDiscardChanges();
            } else {
               this.minecraft.setScreen(this);
            }

         }, Component.translatable("editGamerule.inGame.discardChanges.title"), Component.translatable("editGamerule.inGame.discardChanges.message")));
      } else {
         this.closeAndDiscardChanges();
      }

   }

   private boolean hasPendingChanges() {
      return this.initialValues.keySet().stream().anyMatch(this::hasGameRuleChanged);
   }

   private <T> boolean hasGameRuleChanged(final GameRule<T> rule) {
      return !this.gameRules.get(rule).equals(this.initialValues.get(rule));
   }

   public void onGameRuleValuesUpdated(final Map<ResourceKey<GameRule<?>>, String> values) {
      if (!this.receivedServerValues) {
         this.receivedServerValues = true;
         values.forEach((key, valueStr) -> {
            GameRule<?> rule = (GameRule)BuiltInRegistries.GAME_RULE.getValue(key);
            if (rule != null) {
               this.serverProvidedRules.add(rule);
               this.initializeGameRuleValue(rule, valueStr);
            }

         });
         if (this.loadingDotsWidget != null) {
            this.removeWidget(this.loadingDotsWidget);
         }

         GameRules serverGameRules = new GameRules(this.serverProvidedRules);
         this.ruleList = (AbstractGameRulesScreen.RuleList)this.layout.addToContents(new AbstractGameRulesScreen.RuleList(serverGameRules));
         this.addRenderableWidget(this.ruleList);
         this.repositionElements();
      }
   }

   private <T> void initializeGameRuleValue(final GameRule<T> rule, final String valueStr) {
      rule.deserialize(valueStr).result().ifPresent((value) -> {
         this.initialValues.set(rule, value);
         this.gameRules.set(rule, value, (MinecraftServer)null);
      });
   }

   public void onGamemasterPermissionChanged(final boolean hasGamemasterPermission) {
      if (!hasGamemasterPermission) {
         this.minecraft.setScreen(this.lastScreen);
         Screen var3 = this.minecraft.screen;
         if (var3 instanceof HasGamemasterPermissionReaction) {
            HasGamemasterPermissionReaction screen = (HasGamemasterPermissionReaction)var3;
            screen.onGamemasterPermissionChanged(hasGamemasterPermission);
         }
      }

   }
}

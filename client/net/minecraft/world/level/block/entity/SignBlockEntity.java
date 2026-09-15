package net.minecraft.world.level.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.ResolutionContext;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class SignBlockEntity extends BlockEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_TEXT_LINE_WIDTH = 90;
   private static final int TEXT_LINE_HEIGHT = 10;
   private static final boolean DEFAULT_IS_WAXED = false;
   private static final boolean DEFAULT_ALLOW_OP_FEATURES = false;
   private static final Component CLICK_ACTIONS_DISABLED;
   private @Nullable UUID playerWhoMayEdit;
   private SignText frontText;
   private SignText backText;
   private boolean isWaxed;
   private boolean allowOpFeatures;

   public SignBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      this(BlockEntityTypes.SIGN, worldPosition, blockState);
   }

   public SignBlockEntity(final BlockEntityType<? extends SignBlockEntity> type, final BlockPos worldPosition, final BlockState blockState) {
      super(type, worldPosition, blockState);
      this.frontText = SignText.EMPTY;
      this.backText = SignText.EMPTY;
      this.isWaxed = false;
      this.allowOpFeatures = false;
   }

   public SignTextSlot getSlotPlayerIsFacing(final Player player) {
      Block var3 = this.getBlockState().getBlock();
      if (var3 instanceof SignBlock sign) {
         Vec3 signPositionOffset = sign.getSignHitboxCenterPosition(this.getBlockState());
         double xd = player.getX() - ((double)this.getBlockPos().getX() + signPositionOffset.x);
         double zd = player.getZ() - ((double)this.getBlockPos().getZ() + signPositionOffset.z);
         float signYRot = sign.getYRotationDegrees(this.getBlockState());
         float playerYRot = (float)(Mth.atan2(zd, xd) * 57.2957763671875) - 90.0F;
         return Mth.degreesDifferenceAbs(signYRot, playerYRot) <= 90.0F ? SignTextSlot.FRONT : SignTextSlot.BACK;
      } else {
         return SignTextSlot.FRONT;
      }
   }

   public SignText getText(final SignTextSlot slot) {
      SignText var10000;
      switch (slot) {
         case FRONT -> var10000 = this.frontText;
         case BACK -> var10000 = this.backText;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public int getTextLineHeight() {
      return 10;
   }

   public int getMaxTextLineWidth() {
      return 90;
   }

   protected void saveAdditional(final ValueOutput output) {
      super.saveAdditional(output);
      if (this.allowOpFeatures) {
         output.putBoolean("allow_op_features", this.allowOpFeatures);
      }

      output.store("front_text", SignText.CODEC, this.frontText);
      output.store("back_text", SignText.CODEC, this.backText);
      output.putBoolean("is_waxed", this.isWaxed);
   }

   protected void loadAdditional(final ValueInput input) {
      super.loadAdditional(input);
      this.allowOpFeatures = input.getBooleanOr("allow_op_features", false);
      this.frontText = (SignText)input.read("front_text", SignText.CODEC).map(this::resolveLines).orElse(SignText.EMPTY);
      this.backText = (SignText)input.read("back_text", SignText.CODEC).map(this::resolveLines).orElse(SignText.EMPTY);
      this.isWaxed = input.getBooleanOr("is_waxed", false);
   }

   private SignText resolveLines(final SignText data) {
      return !this.allowOpFeatures ? data : data.asMutable().modifyLines(this::resolveLine).asImmutable();
   }

   private Component resolveLine(final Component component) {
      Level var3 = this.level;
      if (var3 instanceof ServerLevel serverLevel) {
         try {
            return ComponentUtils.resolve(ResolutionContext.create(createCommandSourceStack((Player)null, serverLevel, this.worldPosition)), component);
         } catch (CommandSyntaxException var4) {
         }
      }

      return component;
   }

   public void updateSignText(final Player player, final SignTextSlot slot, final List<FilteredText> lines) {
      if (!this.isWaxed() && player.getUUID().equals(this.getPlayerWhoMayEdit()) && this.level != null) {
         this.updateText((text) -> updateMessages(lines, text, player.isTextFilteringEnabled()), slot);
         this.setAllowedPlayerEditor((UUID)null);
         this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
      } else {
         LOGGER.warn("Player {} just tried to change non-editable sign", player.getPlainTextName());
      }
   }

   public boolean updateText(final UnaryOperator<SignText> function, final SignTextSlot slot) {
      SignText currentText = this.getText(slot);
      SignText newText = (SignText)function.apply(currentText);
      if (currentText != newText) {
         this.setText(newText, slot);
         return true;
      } else {
         return false;
      }
   }

   private static SignText updateMessages(final List<FilteredText> newLines, final SignText originalText, final boolean shouldFilter) {
      SignText.Mutable result = originalText.asMutable();
      List<Component> originalLines = originalText.getMessages(shouldFilter);

      for(int i = 0; i < newLines.size(); ++i) {
         FilteredText line = (FilteredText)newLines.get(i);
         Style currentTextStyle = ((Component)originalLines.get(i)).getStyle();
         if (shouldFilter) {
            result.setLine(i, Component.literal(line.filteredOrEmpty()).setStyle(currentTextStyle));
         } else {
            result.setLine(i, Component.literal(line.raw()).setStyle(currentTextStyle), Component.literal(line.filteredOrEmpty()).setStyle(currentTextStyle));
         }
      }

      return result.asImmutable();
   }

   public void setText(final SignText text, final SignTextSlot slot) {
      switch (slot) {
         case FRONT -> this.frontText = text;
         case BACK -> this.backText = text;
      }

      this.markUpdated();
   }

   public boolean canExecuteClickCommands(final SignTextSlot slot, final Player player) {
      return this.isWaxed() && this.getText(slot).hasAnyClickCommands(player.isTextFilteringEnabled());
   }

   public boolean executeClickCommandsIfPresent(final ServerLevel level, final Player player, final BlockPos pos, final SignTextSlot slot) {
      boolean hasAnyClickCommand = false;

      for(Component message : this.getText(slot).getMessages(player.isTextFilteringEnabled())) {
         Style style = message.getStyle();
         ClickEvent event = style.getClickEvent();
         byte var11 = 0;
         //$FF: var11->value
         //0->net/minecraft/network/chat/ClickEvent$RunCommand
         //1->net/minecraft/network/chat/ClickEvent$ShowDialog
         //2->net/minecraft/network/chat/ClickEvent$Custom
         switch (event.typeSwitch<invokedynamic>(event, var11)) {
            case -1:
            default:
               break;
            case 0:
               ClickEvent.RunCommand command = (ClickEvent.RunCommand)event;
               if (this.allowOpFeatures) {
                  level.getServer().getCommands().performPrefixedCommand(createCommandSourceStack(player, level, pos), command.command());
               }

               hasAnyClickCommand = true;
               break;
            case 1:
               ClickEvent.ShowDialog dialog = (ClickEvent.ShowDialog)event;
               if (this.allowOpFeatures) {
                  player.openDialog(dialog.dialog());
               }

               hasAnyClickCommand = true;
               break;
            case 2:
               ClickEvent.Custom custom = (ClickEvent.Custom)event;
               if (this.allowOpFeatures) {
                  level.getServer().handleCustomClickAction(custom.id(), custom.payload());
               }

               hasAnyClickCommand = true;
         }
      }

      if (!this.allowOpFeatures && hasAnyClickCommand) {
         player.sendOverlayMessage(CLICK_ACTIONS_DISABLED);
      }

      return hasAnyClickCommand;
   }

   private static CommandSourceStack createCommandSourceStack(final @Nullable Player player, final ServerLevel level, final BlockPos pos) {
      return player != null ? new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf(pos), Vec2.ZERO, level, LevelBasedPermissionSet.GAMEMASTER, level.getServer(), player) : new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf(pos), Vec2.ZERO, level, LevelBasedPermissionSet.GAMEMASTER, Component.literal("Sign"), level.getServer());
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(final HolderLookup.Provider registries) {
      return this.saveCustomOnly(registries);
   }

   public void setAllowedPlayerEditor(final @Nullable UUID playerUUID) {
      this.playerWhoMayEdit = playerUUID;
   }

   public @Nullable UUID getPlayerWhoMayEdit() {
      return this.playerWhoMayEdit;
   }

   private void markUpdated() {
      this.setChanged();
      this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
   }

   public boolean isWaxed() {
      return this.isWaxed;
   }

   public boolean setWaxed(final boolean isWaxed) {
      if (this.isWaxed != isWaxed) {
         this.isWaxed = isWaxed;
         this.markUpdated();
         return true;
      } else {
         return false;
      }
   }

   public boolean playerIsTooFarAwayToEdit(final UUID player) {
      Player editingPlayer = this.level.getPlayerByUUID(player);
      return editingPlayer == null || !editingPlayer.isWithinBlockInteractionRange(this.getBlockPos(), 4.0);
   }

   public static void tick(final Level level, final BlockPos blockPos, final BlockState blockState, final SignBlockEntity signBlockEntity) {
      UUID playerWhoMayEdit = signBlockEntity.getPlayerWhoMayEdit();
      if (playerWhoMayEdit != null) {
         signBlockEntity.clearInvalidPlayerWhoMayEdit(signBlockEntity, level, playerWhoMayEdit);
      }

   }

   private void clearInvalidPlayerWhoMayEdit(final SignBlockEntity signBlockEntity, final Level level, final UUID playerWhoMayEdit) {
      if (signBlockEntity.playerIsTooFarAwayToEdit(playerWhoMayEdit)) {
         signBlockEntity.setAllowedPlayerEditor((UUID)null);
      }

   }

   public SoundEvent getSignInteractionFailedSoundEvent() {
      return SoundEvents.WAXED_SIGN_INTERACT_FAIL;
   }

   protected void applyImplicitComponents(final DataComponentGetter components) {
      super.applyImplicitComponents(components);
      this.frontText = (SignText)components.getOrDefault(DataComponents.SIGN_TEXT_FRONT, SignText.EMPTY);
      this.backText = (SignText)components.getOrDefault(DataComponents.SIGN_TEXT_BACK, SignText.EMPTY);
      this.isWaxed = components.get(DataComponents.WAXED) != null;
   }

   protected void collectImplicitComponents(final DataComponentMap.Builder components) {
      super.collectImplicitComponents(components);
      components.set(DataComponents.SIGN_TEXT_FRONT, this.frontText);
      components.set(DataComponents.SIGN_TEXT_BACK, this.backText);
      if (this.isWaxed) {
         components.set(DataComponents.WAXED, Unit.INSTANCE);
      }

   }

   public void removeComponentsFromTag(final ValueOutput output) {
      super.removeComponentsFromTag(output);
      output.discard("front_text");
      output.discard("back_text");
      output.discard("is_waxed");
   }

   static {
      CLICK_ACTIONS_DISABLED = Component.translatable("sign.click_actions_disabled").withStyle(ChatFormatting.RED);
   }
}

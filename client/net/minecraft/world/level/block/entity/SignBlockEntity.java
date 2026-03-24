package net.minecraft.world.level.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
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
   private @Nullable UUID playerWhoMayEdit;
   private SignText frontText;
   private SignText backText;
   private boolean isWaxed;

   public SignBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      this(BlockEntityType.SIGN, worldPosition, blockState);
   }

   public SignBlockEntity(final BlockEntityType<? extends SignBlockEntity> type, final BlockPos worldPosition, final BlockState blockState) {
      super(type, worldPosition, blockState);
      this.isWaxed = false;
      this.frontText = this.createDefaultSignText();
      this.backText = this.createDefaultSignText();
   }

   protected SignText createDefaultSignText() {
      return new SignText();
   }

   public boolean isFacingFrontText(final Player player) {
      Block var3 = this.getBlockState().getBlock();
      if (var3 instanceof SignBlock sign) {
         Vec3 signPositionOffset = sign.getSignHitboxCenterPosition(this.getBlockState());
         double xd = player.getX() - ((double)this.getBlockPos().getX() + signPositionOffset.x);
         double zd = player.getZ() - ((double)this.getBlockPos().getZ() + signPositionOffset.z);
         float signYRot = sign.getYRotationDegrees(this.getBlockState());
         float playerYRot = (float)(Mth.atan2(zd, xd) * 57.2957763671875) - 90.0F;
         return Mth.degreesDifferenceAbs(signYRot, playerYRot) <= 90.0F;
      } else {
         return false;
      }
   }

   public SignText getText(final boolean isFrontText) {
      return isFrontText ? this.frontText : this.backText;
   }

   public SignText getFrontText() {
      return this.frontText;
   }

   public SignText getBackText() {
      return this.backText;
   }

   public int getTextLineHeight() {
      return 10;
   }

   public int getMaxTextLineWidth() {
      return 90;
   }

   protected void saveAdditional(final ValueOutput output) {
      super.saveAdditional(output);
      output.store("front_text", SignText.DIRECT_CODEC, this.frontText);
      output.store("back_text", SignText.DIRECT_CODEC, this.backText);
      output.putBoolean("is_waxed", this.isWaxed);
   }

   protected void loadAdditional(final ValueInput input) {
      super.loadAdditional(input);
      this.frontText = (SignText)input.read("front_text", SignText.DIRECT_CODEC).map(this::loadLines).orElseGet(SignText::new);
      this.backText = (SignText)input.read("back_text", SignText.DIRECT_CODEC).map(this::loadLines).orElseGet(SignText::new);
      this.isWaxed = input.getBooleanOr("is_waxed", false);
   }

   private SignText loadLines(SignText data) {
      for(int i = 0; i < 4; ++i) {
         Component unfilteredMessage = this.loadLine(data.getMessage(i, false));
         Component filteredMessage = this.loadLine(data.getMessage(i, true));
         data = data.setMessage(i, unfilteredMessage, filteredMessage);
      }

      return data;
   }

   private Component loadLine(final Component component) {
      Level var3 = this.level;
      if (var3 instanceof ServerLevel serverLevel) {
         try {
            return ComponentUtils.resolve(ResolutionContext.create(createCommandSourceStack((Player)null, serverLevel, this.worldPosition)), component);
         } catch (CommandSyntaxException var4) {
         }
      }

      return component;
   }

   public void updateSignText(final Player player, final boolean frontText, final List<FilteredText> lines) {
      if (!this.isWaxed() && player.getUUID().equals(this.getPlayerWhoMayEdit()) && this.level != null) {
         this.updateText((text) -> this.setMessages(player, lines, text), frontText);
         this.setAllowedPlayerEditor((UUID)null);
         this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
      } else {
         LOGGER.warn("Player {} just tried to change non-editable sign", player.getPlainTextName());
      }
   }

   public boolean updateText(final UnaryOperator<SignText> function, final boolean isFrontText) {
      SignText text = this.getText(isFrontText);
      return this.setText((SignText)function.apply(text), isFrontText);
   }

   private SignText setMessages(final Player player, final List<FilteredText> lines, SignText text) {
      for(int i = 0; i < lines.size(); ++i) {
         FilteredText line = (FilteredText)lines.get(i);
         Style currentTextStyle = text.getMessage(i, player.isTextFilteringEnabled()).getStyle();
         if (player.isTextFilteringEnabled()) {
            text = text.setMessage(i, Component.literal(line.filteredOrEmpty()).setStyle(currentTextStyle));
         } else {
            text = text.setMessage(i, Component.literal(line.raw()).setStyle(currentTextStyle), Component.literal(line.filteredOrEmpty()).setStyle(currentTextStyle));
         }
      }

      return text;
   }

   public boolean setText(final SignText text, final boolean isFrontText) {
      return isFrontText ? this.setFrontText(text) : this.setBackText(text);
   }

   private boolean setBackText(final SignText text) {
      if (text != this.backText) {
         this.backText = text;
         this.markUpdated();
         return true;
      } else {
         return false;
      }
   }

   private boolean setFrontText(final SignText text) {
      if (text != this.frontText) {
         this.frontText = text;
         this.markUpdated();
         return true;
      } else {
         return false;
      }
   }

   public boolean canExecuteClickCommands(final boolean isFrontText, final Player player) {
      return this.isWaxed() && this.getText(isFrontText).hasAnyClickCommands(player);
   }

   public boolean executeClickCommandsIfPresent(final ServerLevel level, final Player player, final BlockPos pos, final boolean isFrontText) {
      boolean hasAnyClickCommand = false;

      for(Component message : this.getText(isFrontText).getMessages(player.isTextFilteringEnabled())) {
         Style style = message.getStyle();
         ClickEvent event = style.getClickEvent();
         byte var13 = 0;
         //$FF: var13->value
         //0->net/minecraft/network/chat/ClickEvent$RunCommand
         //1->net/minecraft/network/chat/ClickEvent$ShowDialog
         //2->net/minecraft/network/chat/ClickEvent$Custom
         switch (event.typeSwitch<invokedynamic>(event, var13)) {
            case -1:
            default:
               break;
            case 0:
               ClickEvent.RunCommand command = (ClickEvent.RunCommand)event;
               level.getServer().getCommands().performPrefixedCommand(createCommandSourceStack(player, level, pos), command.command());
               hasAnyClickCommand = true;
               break;
            case 1:
               ClickEvent.ShowDialog dialog = (ClickEvent.ShowDialog)event;
               player.openDialog(dialog.dialog());
               hasAnyClickCommand = true;
               break;
            case 2:
               ClickEvent.Custom custom = (ClickEvent.Custom)event;
               level.getServer().handleCustomClickAction(custom.id(), custom.payload());
               hasAnyClickCommand = true;
         }
      }

      return hasAnyClickCommand;
   }

   private static CommandSourceStack createCommandSourceStack(final @Nullable Player player, final ServerLevel level, final BlockPos pos) {
      String textName = player == null ? "Sign" : player.getPlainTextName();
      Component displayName = (Component)(player == null ? Component.literal("Sign") : player.getDisplayName());
      return new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf(pos), Vec2.ZERO, level, LevelBasedPermissionSet.GAMEMASTER, textName, displayName, level.getServer(), player);
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
}

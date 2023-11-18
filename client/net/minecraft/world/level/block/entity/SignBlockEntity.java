package net.minecraft.world.level.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.FilteredText;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class SignBlockEntity extends BlockEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_TEXT_LINE_WIDTH = 90;
   private static final int TEXT_LINE_HEIGHT = 10;
   @Nullable
   private UUID playerWhoMayEdit;
   private SignText frontText = this.createDefaultSignText();
   private SignText backText = this.createDefaultSignText();
   private boolean isWaxed;

   public SignBlockEntity(BlockPos var1, BlockState var2) {
      this(BlockEntityType.SIGN, var1, var2);
   }

   public SignBlockEntity(BlockEntityType var1, BlockPos var2, BlockState var3) {
      super(var1, var2, var3);
   }

   protected SignText createDefaultSignText() {
      return new SignText();
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public boolean isFacingFrontText(Player var1) {
      Block var3 = this.getBlockState().getBlock();
      if (var3 instanceof SignBlock var2) {
         Vec3 var10 = var2.getSignHitboxCenterPosition(this.getBlockState());
         double var4 = var1.getX() - ((double)this.getBlockPos().getX() + var10.x);
         double var6 = var1.getZ() - ((double)this.getBlockPos().getZ() + var10.z);
         float var8 = var2.getYRotationDegrees(this.getBlockState());
         float var9 = (float)(Mth.atan2(var6, var4) * 57.2957763671875) - 90.0F;
         return Mth.degreesDifferenceAbs(var8, var9) <= 90.0F;
      } else {
         return false;
      }
   }

   public SignText getTextFacingPlayer(Player var1) {
      return this.getText(this.isFacingFrontText(var1));
   }

   public SignText getText(boolean var1) {
      return var1 ? this.frontText : this.backText;
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

   @Override
   protected void saveAdditional(CompoundTag var1) {
      super.saveAdditional(var1);
      SignText.DIRECT_CODEC.encodeStart(NbtOps.INSTANCE, this.frontText).resultOrPartial(LOGGER::error).ifPresent(var1x -> var1.put("front_text", var1x));
      SignText.DIRECT_CODEC.encodeStart(NbtOps.INSTANCE, this.backText).resultOrPartial(LOGGER::error).ifPresent(var1x -> var1.put("back_text", var1x));
      var1.putBoolean("is_waxed", this.isWaxed);
   }

   @Override
   public void load(CompoundTag var1) {
      super.load(var1);
      if (var1.contains("front_text")) {
         SignText.DIRECT_CODEC
            .parse(NbtOps.INSTANCE, var1.getCompound("front_text"))
            .resultOrPartial(LOGGER::error)
            .ifPresent(var1x -> this.frontText = this.loadLines(var1x));
      }

      if (var1.contains("back_text")) {
         SignText.DIRECT_CODEC
            .parse(NbtOps.INSTANCE, var1.getCompound("back_text"))
            .resultOrPartial(LOGGER::error)
            .ifPresent(var1x -> this.backText = this.loadLines(var1x));
      }

      this.isWaxed = var1.getBoolean("is_waxed");
   }

   private SignText loadLines(SignText var1) {
      for(int var2 = 0; var2 < 4; ++var2) {
         Component var3 = this.loadLine(var1.getMessage(var2, false));
         Component var4 = this.loadLine(var1.getMessage(var2, true));
         var1 = var1.setMessage(var2, var3, var4);
      }

      return var1;
   }

   private Component loadLine(Component var1) {
      Level var3 = this.level;
      if (var3 instanceof ServerLevel var2) {
         try {
            return ComponentUtils.updateForEntity(createCommandSourceStack(null, (Level)var2, this.worldPosition), var1, null, 0);
         } catch (CommandSyntaxException var4) {
         }
      }

      return var1;
   }

   public void updateSignText(Player var1, boolean var2, List<FilteredText> var3) {
      if (!this.isWaxed() && var1.getUUID().equals(this.getPlayerWhoMayEdit()) && this.level != null) {
         this.updateText(var3x -> this.setMessages(var1, var3, var3x), var2);
         this.setAllowedPlayerEditor(null);
         this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
      } else {
         LOGGER.warn("Player {} just tried to change non-editable sign", var1.getName().getString());
      }
   }

   public boolean updateText(UnaryOperator<SignText> var1, boolean var2) {
      SignText var3 = this.getText(var2);
      return this.setText(var1.apply(var3), var2);
   }

   private SignText setMessages(Player var1, List<FilteredText> var2, SignText var3) {
      for(int var4 = 0; var4 < var2.size(); ++var4) {
         FilteredText var5 = (FilteredText)var2.get(var4);
         Style var6 = var3.getMessage(var4, var1.isTextFilteringEnabled()).getStyle();
         if (var1.isTextFilteringEnabled()) {
            var3 = var3.setMessage(var4, Component.literal(var5.filteredOrEmpty()).setStyle(var6));
         } else {
            var3 = var3.setMessage(var4, Component.literal(var5.raw()).setStyle(var6), Component.literal(var5.filteredOrEmpty()).setStyle(var6));
         }
      }

      return var3;
   }

   public boolean setText(SignText var1, boolean var2) {
      return var2 ? this.setFrontText(var1) : this.setBackText(var1);
   }

   private boolean setBackText(SignText var1) {
      if (var1 != this.backText) {
         this.backText = var1;
         this.markUpdated();
         return true;
      } else {
         return false;
      }
   }

   private boolean setFrontText(SignText var1) {
      if (var1 != this.frontText) {
         this.frontText = var1;
         this.markUpdated();
         return true;
      } else {
         return false;
      }
   }

   public boolean canExecuteClickCommands(boolean var1, Player var2) {
      return this.isWaxed() && this.getText(var1).hasAnyClickCommands(var2);
   }

   public boolean executeClickCommandsIfPresent(Player var1, Level var2, BlockPos var3, boolean var4) {
      boolean var5 = false;

      for(Component var9 : this.getText(var4).getMessages(var1.isTextFilteringEnabled())) {
         Style var10 = var9.getStyle();
         ClickEvent var11 = var10.getClickEvent();
         if (var11 != null && var11.getAction() == ClickEvent.Action.RUN_COMMAND) {
            var1.getServer().getCommands().performPrefixedCommand(createCommandSourceStack(var1, var2, var3), var11.getValue());
            var5 = true;
         }
      }

      return var5;
   }

   private static CommandSourceStack createCommandSourceStack(@Nullable Player var0, Level var1, BlockPos var2) {
      String var3 = var0 == null ? "Sign" : var0.getName().getString();
      Object var4 = var0 == null ? Component.literal("Sign") : var0.getDisplayName();
      return new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf(var2), Vec2.ZERO, (ServerLevel)var1, 2, var3, (Component)var4, var1.getServer(), var0);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Override
   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Override
   public boolean onlyOpCanSetNbt() {
      return true;
   }

   public void setAllowedPlayerEditor(@Nullable UUID var1) {
      this.playerWhoMayEdit = var1;
   }

   @Nullable
   public UUID getPlayerWhoMayEdit() {
      return this.playerWhoMayEdit;
   }

   private void markUpdated() {
      this.setChanged();
      this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
   }

   public boolean isWaxed() {
      return this.isWaxed;
   }

   public boolean setWaxed(boolean var1) {
      if (this.isWaxed != var1) {
         this.isWaxed = var1;
         this.markUpdated();
         return true;
      } else {
         return false;
      }
   }

   public boolean playerIsTooFarAwayToEdit(UUID var1) {
      Player var2 = this.level.getPlayerByUUID(var1);
      return var2 == null
         || var2.distanceToSqr((double)this.getBlockPos().getX(), (double)this.getBlockPos().getY(), (double)this.getBlockPos().getZ()) > 64.0;
   }

   public static void tick(Level var0, BlockPos var1, BlockState var2, SignBlockEntity var3) {
      UUID var4 = var3.getPlayerWhoMayEdit();
      if (var4 != null) {
         var3.clearInvalidPlayerWhoMayEdit(var3, var0, var4);
      }
   }

   private void clearInvalidPlayerWhoMayEdit(SignBlockEntity var1, Level var2, UUID var3) {
      if (var1.playerIsTooFarAwayToEdit(var3)) {
         var1.setAllowedPlayerEditor(null);
      }
   }
}

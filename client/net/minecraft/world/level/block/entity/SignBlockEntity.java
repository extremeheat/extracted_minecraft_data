package net.minecraft.world.level.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class SignBlockEntity extends BlockEntity {
   public static final int LINES = 4;
   private static final int MAX_TEXT_LINE_WIDTH = 90;
   private static final int TEXT_LINE_HEIGHT = 10;
   private static final String[] RAW_TEXT_FIELD_NAMES = new String[]{"Text1", "Text2", "Text3", "Text4"};
   private static final String[] FILTERED_TEXT_FIELD_NAMES = new String[]{"FilteredText1", "FilteredText2", "FilteredText3", "FilteredText4"};
   private final Component[] messages = new Component[]{CommonComponents.EMPTY, CommonComponents.EMPTY, CommonComponents.EMPTY, CommonComponents.EMPTY};
   private final Component[] filteredMessages = new Component[]{
      CommonComponents.EMPTY, CommonComponents.EMPTY, CommonComponents.EMPTY, CommonComponents.EMPTY
   };
   private boolean isEditable = true;
   @Nullable
   private UUID playerWhoMayEdit;
   @Nullable
   private FormattedCharSequence[] renderMessages;
   private boolean renderMessagedFiltered;
   private DyeColor color = DyeColor.BLACK;
   private boolean hasGlowingText;

   public SignBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.SIGN, var1, var2);
   }

   public SignBlockEntity(BlockEntityType var1, BlockPos var2, BlockState var3) {
      super(var1, var2, var3);
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

      for(int var2 = 0; var2 < 4; ++var2) {
         Component var3 = this.messages[var2];
         String var4 = Component.Serializer.toJson(var3);
         var1.putString(RAW_TEXT_FIELD_NAMES[var2], var4);
         Component var5 = this.filteredMessages[var2];
         if (!var5.equals(var3)) {
            var1.putString(FILTERED_TEXT_FIELD_NAMES[var2], Component.Serializer.toJson(var5));
         }
      }

      var1.putString("Color", this.color.getName());
      var1.putBoolean("GlowingText", this.hasGlowingText);
   }

   @Override
   public void load(CompoundTag var1) {
      this.isEditable = false;
      super.load(var1);
      this.color = DyeColor.byName(var1.getString("Color"), DyeColor.BLACK);

      for(int var2 = 0; var2 < 4; ++var2) {
         String var3 = var1.getString(RAW_TEXT_FIELD_NAMES[var2]);
         Component var4 = this.loadLine(var3);
         this.messages[var2] = var4;
         String var5 = FILTERED_TEXT_FIELD_NAMES[var2];
         if (var1.contains(var5, 8)) {
            this.filteredMessages[var2] = this.loadLine(var1.getString(var5));
         } else {
            this.filteredMessages[var2] = var4;
         }
      }

      this.renderMessages = null;
      this.hasGlowingText = var1.getBoolean("GlowingText");
   }

   private Component loadLine(String var1) {
      Component var2 = this.deserializeTextSafe(var1);
      if (this.level instanceof ServerLevel) {
         try {
            return ComponentUtils.updateForEntity(this.createCommandSourceStack(null), var2, null, 0);
         } catch (CommandSyntaxException var4) {
         }
      }

      return var2;
   }

   private Component deserializeTextSafe(String var1) {
      try {
         MutableComponent var2 = Component.Serializer.fromJson(var1);
         if (var2 != null) {
            return var2;
         }
      } catch (Exception var3) {
      }

      return CommonComponents.EMPTY;
   }

   public Component getMessage(int var1, boolean var2) {
      return this.getMessages(var2)[var1];
   }

   public void setMessage(int var1, Component var2) {
      this.setMessage(var1, var2, var2);
   }

   public void setMessage(int var1, Component var2, Component var3) {
      this.messages[var1] = var2;
      this.filteredMessages[var1] = var3;
      this.renderMessages = null;
   }

   public FormattedCharSequence[] getRenderMessages(boolean var1, Function<Component, FormattedCharSequence> var2) {
      if (this.renderMessages == null || this.renderMessagedFiltered != var1) {
         this.renderMessagedFiltered = var1;
         this.renderMessages = new FormattedCharSequence[4];

         for(int var3 = 0; var3 < 4; ++var3) {
            this.renderMessages[var3] = (FormattedCharSequence)var2.apply(this.getMessage(var3, var1));
         }
      }

      return this.renderMessages;
   }

   private Component[] getMessages(boolean var1) {
      return var1 ? this.filteredMessages : this.messages;
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

   public boolean isEditable() {
      return this.isEditable;
   }

   public void setEditable(boolean var1) {
      this.isEditable = var1;
      if (!var1) {
         this.playerWhoMayEdit = null;
      }
   }

   public void setAllowedPlayerEditor(UUID var1) {
      this.playerWhoMayEdit = var1;
   }

   @Nullable
   public UUID getPlayerWhoMayEdit() {
      return this.playerWhoMayEdit;
   }

   public boolean hasAnyClickCommands(Player var1) {
      for(Component var5 : this.getMessages(var1.isTextFilteringEnabled())) {
         Style var6 = var5.getStyle();
         ClickEvent var7 = var6.getClickEvent();
         if (var7 != null && var7.getAction() == ClickEvent.Action.RUN_COMMAND) {
            return true;
         }
      }

      return false;
   }

   public boolean executeClickCommands(ServerPlayer var1) {
      for(Component var5 : this.getMessages(var1.isTextFilteringEnabled())) {
         Style var6 = var5.getStyle();
         ClickEvent var7 = var6.getClickEvent();
         if (var7 != null && var7.getAction() == ClickEvent.Action.RUN_COMMAND) {
            var1.getServer().getCommands().performPrefixedCommand(this.createCommandSourceStack(var1), var7.getValue());
         }
      }

      return true;
   }

   public CommandSourceStack createCommandSourceStack(@Nullable ServerPlayer var1) {
      String var2 = var1 == null ? "Sign" : var1.getName().getString();
      Object var3 = var1 == null ? Component.literal("Sign") : var1.getDisplayName();
      return new CommandSourceStack(
         CommandSource.NULL, Vec3.atCenterOf(this.worldPosition), Vec2.ZERO, (ServerLevel)this.level, 2, var2, (Component)var3, this.level.getServer(), var1
      );
   }

   public DyeColor getColor() {
      return this.color;
   }

   public boolean setColor(DyeColor var1) {
      if (var1 != this.getColor()) {
         this.color = var1;
         this.markUpdated();
         return true;
      } else {
         return false;
      }
   }

   public boolean hasGlowingText() {
      return this.hasGlowingText;
   }

   public boolean setHasGlowingText(boolean var1) {
      if (this.hasGlowingText != var1) {
         this.hasGlowingText = var1;
         this.markUpdated();
         return true;
      } else {
         return false;
      }
   }

   private void markUpdated() {
      this.setChanged();
      this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
   }
}

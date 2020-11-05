package net.minecraft.world.level.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class SignBlockEntity extends BlockEntity {
   private final Component[] messages;
   private boolean isEditable;
   private Player playerWhoMayEdit;
   private final FormattedCharSequence[] renderMessages;
   private DyeColor color;

   public SignBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.SIGN, var1, var2);
      this.messages = new Component[]{TextComponent.EMPTY, TextComponent.EMPTY, TextComponent.EMPTY, TextComponent.EMPTY};
      this.isEditable = true;
      this.renderMessages = new FormattedCharSequence[4];
      this.color = DyeColor.BLACK;
   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);

      for(int var2 = 0; var2 < 4; ++var2) {
         String var3 = Component.Serializer.toJson(this.messages[var2]);
         var1.putString("Text" + (var2 + 1), var3);
      }

      var1.putString("Color", this.color.getName());
      return var1;
   }

   public void load(CompoundTag var1) {
      this.isEditable = false;
      super.load(var1);
      this.color = DyeColor.byName(var1.getString("Color"), DyeColor.BLACK);

      for(int var2 = 0; var2 < 4; ++var2) {
         String var3 = var1.getString("Text" + (var2 + 1));
         MutableComponent var4 = Component.Serializer.fromJson(var3.isEmpty() ? "\"\"" : var3);
         if (this.level instanceof ServerLevel) {
            try {
               this.messages[var2] = ComponentUtils.updateForEntity(this.createCommandSourceStack((ServerPlayer)null), var4, (Entity)null, 0);
            } catch (CommandSyntaxException var6) {
               this.messages[var2] = var4;
            }
         } else {
            this.messages[var2] = var4;
         }

         this.renderMessages[var2] = null;
      }

   }

   public Component getMessage(int var1) {
      return this.messages[var1];
   }

   public void setMessage(int var1, Component var2) {
      this.messages[var1] = var2;
      this.renderMessages[var1] = null;
   }

   @Nullable
   public FormattedCharSequence getRenderMessage(int var1, Function<Component, FormattedCharSequence> var2) {
      if (this.renderMessages[var1] == null && this.messages[var1] != null) {
         this.renderMessages[var1] = (FormattedCharSequence)var2.apply(this.messages[var1]);
      }

      return this.renderMessages[var1];
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return new ClientboundBlockEntityDataPacket(this.worldPosition, 9, this.getUpdateTag());
   }

   public CompoundTag getUpdateTag() {
      return this.save(new CompoundTag());
   }

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

   public void setAllowedPlayerEditor(Player var1) {
      this.playerWhoMayEdit = var1;
   }

   public Player getPlayerWhoMayEdit() {
      return this.playerWhoMayEdit;
   }

   public boolean executeClickCommands(Player var1) {
      Component[] var2 = this.messages;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Component var5 = var2[var4];
         Style var6 = var5 == null ? null : var5.getStyle();
         if (var6 != null && var6.getClickEvent() != null) {
            ClickEvent var7 = var6.getClickEvent();
            if (var7.getAction() == ClickEvent.Action.RUN_COMMAND) {
               var1.getServer().getCommands().performCommand(this.createCommandSourceStack((ServerPlayer)var1), var7.getValue());
            }
         }
      }

      return true;
   }

   public CommandSourceStack createCommandSourceStack(@Nullable ServerPlayer var1) {
      String var2 = var1 == null ? "Sign" : var1.getName().getString();
      Object var3 = var1 == null ? new TextComponent("Sign") : var1.getDisplayName();
      return new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf(this.worldPosition), Vec2.ZERO, (ServerLevel)this.level, 2, var2, (Component)var3, this.level.getServer(), var1);
   }

   public DyeColor getColor() {
      return this.color;
   }

   public boolean setColor(DyeColor var1) {
      if (var1 != this.getColor()) {
         this.color = var1;
         this.setChanged();
         this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
         return true;
      } else {
         return false;
      }
   }
}

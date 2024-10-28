package net.minecraft.server.commands.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockDataAccessor implements DataAccessor {
   static final SimpleCommandExceptionType ERROR_NOT_A_BLOCK_ENTITY = new SimpleCommandExceptionType(Component.translatable("commands.data.block.invalid"));
   public static final Function<String, DataCommands.DataProvider> PROVIDER = (var0) -> {
      return new DataCommands.DataProvider() {
         public DataAccessor access(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException {
            BlockPos var2 = BlockPosArgument.getLoadedBlockPos(var1, var0 + "Pos");
            BlockEntity var3 = ((CommandSourceStack)var1.getSource()).getLevel().getBlockEntity(var2);
            if (var3 == null) {
               throw BlockDataAccessor.ERROR_NOT_A_BLOCK_ENTITY.create();
            } else {
               return new BlockDataAccessor(var3, var2);
            }
         }

         public ArgumentBuilder<CommandSourceStack, ?> wrap(ArgumentBuilder<CommandSourceStack, ?> var1, Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> var2) {
            return var1.then(Commands.literal("block").then((ArgumentBuilder)var2.apply(Commands.argument(var0 + "Pos", BlockPosArgument.blockPos()))));
         }
      };
   };
   private final BlockEntity entity;
   private final BlockPos pos;

   public BlockDataAccessor(BlockEntity var1, BlockPos var2) {
      super();
      this.entity = var1;
      this.pos = var2;
   }

   public void setData(CompoundTag var1) {
      BlockState var2 = this.entity.getLevel().getBlockState(this.pos);
      this.entity.loadWithComponents(var1, this.entity.getLevel().registryAccess());
      this.entity.setChanged();
      this.entity.getLevel().sendBlockUpdated(this.pos, var2, var2, 3);
   }

   public CompoundTag getData() {
      return this.entity.saveWithFullMetadata(this.entity.getLevel().registryAccess());
   }

   public Component getModifiedSuccess() {
      return Component.translatable("commands.data.block.modified", this.pos.getX(), this.pos.getY(), this.pos.getZ());
   }

   public Component getPrintSuccess(Tag var1) {
      return Component.translatable("commands.data.block.query", this.pos.getX(), this.pos.getY(), this.pos.getZ(), NbtUtils.toPrettyComponent(var1));
   }

   public Component getPrintSuccess(NbtPathArgument.NbtPath var1, double var2, int var4) {
      return Component.translatable("commands.data.block.get", var1.asString(), this.pos.getX(), this.pos.getY(), this.pos.getZ(), String.format(Locale.ROOT, "%.2f", var2), var4);
   }
}

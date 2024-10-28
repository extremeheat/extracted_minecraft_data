package net.minecraft.server.commands.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.CommandStorage;

public class StorageDataAccessor implements DataAccessor {
   static final SuggestionProvider<CommandSourceStack> SUGGEST_STORAGE = (var0, var1) -> {
      return SharedSuggestionProvider.suggestResource(getGlobalTags(var0).keys(), var1);
   };
   public static final Function<String, DataCommands.DataProvider> PROVIDER = (var0) -> {
      return new DataCommands.DataProvider() {
         public DataAccessor access(CommandContext<CommandSourceStack> var1) {
            return new StorageDataAccessor(StorageDataAccessor.getGlobalTags(var1), ResourceLocationArgument.getId(var1, var0));
         }

         public ArgumentBuilder<CommandSourceStack, ?> wrap(ArgumentBuilder<CommandSourceStack, ?> var1, Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> var2) {
            return var1.then(Commands.literal("storage").then((ArgumentBuilder)var2.apply(Commands.argument(var0, ResourceLocationArgument.id()).suggests(StorageDataAccessor.SUGGEST_STORAGE))));
         }
      };
   };
   private final CommandStorage storage;
   private final ResourceLocation id;

   static CommandStorage getGlobalTags(CommandContext<CommandSourceStack> var0) {
      return ((CommandSourceStack)var0.getSource()).getServer().getCommandStorage();
   }

   StorageDataAccessor(CommandStorage var1, ResourceLocation var2) {
      super();
      this.storage = var1;
      this.id = var2;
   }

   public void setData(CompoundTag var1) {
      this.storage.set(this.id, var1);
   }

   public CompoundTag getData() {
      return this.storage.get(this.id);
   }

   public Component getModifiedSuccess() {
      return Component.translatable("commands.data.storage.modified", Component.translationArg(this.id));
   }

   public Component getPrintSuccess(Tag var1) {
      return Component.translatable("commands.data.storage.query", Component.translationArg(this.id), NbtUtils.toPrettyComponent(var1));
   }

   public Component getPrintSuccess(NbtPathArgument.NbtPath var1, double var2, int var4) {
      return Component.translatable("commands.data.storage.get", var1.asString(), Component.translationArg(this.id), String.format(Locale.ROOT, "%.2f", var2), var4);
   }
}

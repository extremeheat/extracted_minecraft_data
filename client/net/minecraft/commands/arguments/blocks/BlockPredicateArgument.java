package net.minecraft.commands.arguments.blocks;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockPredicateArgument implements ArgumentType<Result> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}");
   private final HolderLookup<Block> blocks;

   public BlockPredicateArgument(CommandBuildContext var1) {
      super();
      this.blocks = var1.lookupOrThrow(Registries.BLOCK);
   }

   public static BlockPredicateArgument blockPredicate(CommandBuildContext var0) {
      return new BlockPredicateArgument(var0);
   }

   public Result parse(StringReader var1) throws CommandSyntaxException {
      return parse(this.blocks, var1);
   }

   public static Result parse(HolderLookup<Block> var0, StringReader var1) throws CommandSyntaxException {
      return (Result)BlockStateParser.parseForTesting(var0, var1, true).map((var0x) -> {
         return new BlockPredicate(var0x.blockState(), var0x.properties().keySet(), var0x.nbt());
      }, (var0x) -> {
         return new TagPredicate(var0x.tag(), var0x.vagueProperties(), var0x.nbt());
      });
   }

   public static Predicate<BlockInWorld> getBlockPredicate(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return (Predicate)var0.getArgument(var1, Result.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return BlockStateParser.fillSuggestions(this.blocks, var2, true, true);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public interface Result extends Predicate<BlockInWorld> {
      boolean requiresNbt();
   }

   private static class TagPredicate implements Result {
      private final HolderSet<Block> tag;
      @Nullable
      private final CompoundTag nbt;
      private final Map<String, String> vagueProperties;

      TagPredicate(HolderSet<Block> var1, Map<String, String> var2, @Nullable CompoundTag var3) {
         super();
         this.tag = var1;
         this.vagueProperties = var2;
         this.nbt = var3;
      }

      public boolean test(BlockInWorld var1) {
         BlockState var2 = var1.getState();
         if (!var2.is(this.tag)) {
            return false;
         } else {
            Iterator var3 = this.vagueProperties.entrySet().iterator();

            while(var3.hasNext()) {
               Map.Entry var4 = (Map.Entry)var3.next();
               Property var5 = var2.getBlock().getStateDefinition().getProperty((String)var4.getKey());
               if (var5 == null) {
                  return false;
               }

               Comparable var6 = (Comparable)var5.getValue((String)var4.getValue()).orElse((Object)null);
               if (var6 == null) {
                  return false;
               }

               if (var2.getValue(var5) != var6) {
                  return false;
               }
            }

            if (this.nbt == null) {
               return true;
            } else {
               BlockEntity var7 = var1.getEntity();
               return var7 != null && NbtUtils.compareNbt(this.nbt, var7.saveWithFullMetadata(var1.getLevel().registryAccess()), true);
            }
         }
      }

      public boolean requiresNbt() {
         return this.nbt != null;
      }

      // $FF: synthetic method
      public boolean test(final Object var1) {
         return this.test((BlockInWorld)var1);
      }
   }

   static class BlockPredicate implements Result {
      private final BlockState state;
      private final Set<Property<?>> properties;
      @Nullable
      private final CompoundTag nbt;

      public BlockPredicate(BlockState var1, Set<Property<?>> var2, @Nullable CompoundTag var3) {
         super();
         this.state = var1;
         this.properties = var2;
         this.nbt = var3;
      }

      public boolean test(BlockInWorld var1) {
         BlockState var2 = var1.getState();
         if (!var2.is(this.state.getBlock())) {
            return false;
         } else {
            Iterator var3 = this.properties.iterator();

            while(var3.hasNext()) {
               Property var4 = (Property)var3.next();
               if (var2.getValue(var4) != this.state.getValue(var4)) {
                  return false;
               }
            }

            if (this.nbt == null) {
               return true;
            } else {
               BlockEntity var5 = var1.getEntity();
               return var5 != null && NbtUtils.compareNbt(this.nbt, var5.saveWithFullMetadata(var1.getLevel().registryAccess()), true);
            }
         }
      }

      public boolean requiresNbt() {
         return this.nbt != null;
      }

      // $FF: synthetic method
      public boolean test(final Object var1) {
         return this.test((BlockInWorld)var1);
      }
   }
}

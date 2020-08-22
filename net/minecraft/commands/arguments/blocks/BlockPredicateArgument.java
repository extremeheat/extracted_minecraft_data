package net.minecraft.commands.arguments.blocks;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockPredicateArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}");
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("arguments.block.tag.unknown", new Object[]{var0});
   });

   public static BlockPredicateArgument blockPredicate() {
      return new BlockPredicateArgument();
   }

   public BlockPredicateArgument.Result parse(StringReader var1) throws CommandSyntaxException {
      BlockStateParser var2 = (new BlockStateParser(var1, true)).parse(true);
      if (var2.getState() != null) {
         BlockPredicateArgument.BlockPredicate var4 = new BlockPredicateArgument.BlockPredicate(var2.getState(), var2.getProperties().keySet(), var2.getNbt());
         return (var1x) -> {
            return var4;
         };
      } else {
         ResourceLocation var3 = var2.getTag();
         return (var2x) -> {
            Tag var3x = var2x.getBlocks().getTag(var3);
            if (var3x == null) {
               throw ERROR_UNKNOWN_TAG.create(var3.toString());
            } else {
               return new BlockPredicateArgument.TagPredicate(var3x, var2.getVagueProperties(), var2.getNbt());
            }
         };
      }
   }

   public static Predicate getBlockPredicate(CommandContext var0, String var1) throws CommandSyntaxException {
      return ((BlockPredicateArgument.Result)var0.getArgument(var1, BlockPredicateArgument.Result.class)).create(((CommandSourceStack)var0.getSource()).getServer().getTags());
   }

   public CompletableFuture listSuggestions(CommandContext var1, SuggestionsBuilder var2) {
      StringReader var3 = new StringReader(var2.getInput());
      var3.setCursor(var2.getStart());
      BlockStateParser var4 = new BlockStateParser(var3, true);

      try {
         var4.parse(true);
      } catch (CommandSyntaxException var6) {
      }

      return var4.fillSuggestions(var2);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   static class TagPredicate implements Predicate {
      private final Tag tag;
      @Nullable
      private final CompoundTag nbt;
      private final Map vagueProperties;

      private TagPredicate(Tag var1, Map var2, @Nullable CompoundTag var3) {
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
               Entry var4 = (Entry)var3.next();
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
               return var7 != null && NbtUtils.compareNbt(this.nbt, var7.save(new CompoundTag()), true);
            }
         }
      }

      // $FF: synthetic method
      public boolean test(Object var1) {
         return this.test((BlockInWorld)var1);
      }

      // $FF: synthetic method
      TagPredicate(Tag var1, Map var2, CompoundTag var3, Object var4) {
         this(var1, var2, var3);
      }
   }

   static class BlockPredicate implements Predicate {
      private final BlockState state;
      private final Set properties;
      @Nullable
      private final CompoundTag nbt;

      public BlockPredicate(BlockState var1, Set var2, @Nullable CompoundTag var3) {
         this.state = var1;
         this.properties = var2;
         this.nbt = var3;
      }

      public boolean test(BlockInWorld var1) {
         BlockState var2 = var1.getState();
         if (var2.getBlock() != this.state.getBlock()) {
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
               return var5 != null && NbtUtils.compareNbt(this.nbt, var5.save(new CompoundTag()), true);
            }
         }
      }

      // $FF: synthetic method
      public boolean test(Object var1) {
         return this.test((BlockInWorld)var1);
      }
   }

   public interface Result {
      Predicate create(TagManager var1) throws CommandSyntaxException;
   }
}

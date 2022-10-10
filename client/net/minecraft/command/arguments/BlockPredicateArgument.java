package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
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
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.IProperty;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class BlockPredicateArgument implements ArgumentType<BlockPredicateArgument.IResult> {
   private static final Collection<String> field_201331_a = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}");
   private static final DynamicCommandExceptionType field_199826_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("arguments.block.tag.unknown", new Object[]{var0});
   });

   public BlockPredicateArgument() {
      super();
   }

   public static BlockPredicateArgument func_199824_a() {
      return new BlockPredicateArgument();
   }

   public BlockPredicateArgument.IResult parse(StringReader var1) throws CommandSyntaxException {
      BlockStateParser var2 = (new BlockStateParser(var1, true)).func_197243_a(true);
      if (var2.func_197249_b() != null) {
         BlockPredicateArgument.BlockPredicate var4 = new BlockPredicateArgument.BlockPredicate(var2.func_197249_b(), var2.func_197254_a().keySet(), var2.func_197241_c());
         return (var1x) -> {
            return var4;
         };
      } else {
         ResourceLocation var3 = var2.func_199829_d();
         return (var2x) -> {
            Tag var3x = var2x.func_199717_a().func_199910_a(var3);
            if (var3x == null) {
               throw field_199826_a.create(var3.toString());
            } else {
               return new BlockPredicateArgument.TagPredicate(var3x, var2.func_200139_j(), var2.func_197241_c());
            }
         };
      }
   }

   public static Predicate<BlockWorldState> func_199825_a(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      return ((BlockPredicateArgument.IResult)var0.getArgument(var1, BlockPredicateArgument.IResult.class)).create(((CommandSource)var0.getSource()).func_197028_i().func_199731_aO());
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      StringReader var3 = new StringReader(var2.getInput());
      var3.setCursor(var2.getStart());
      BlockStateParser var4 = new BlockStateParser(var3, true);

      try {
         var4.func_197243_a(true);
      } catch (CommandSyntaxException var6) {
      }

      return var4.func_197245_a(var2);
   }

   public Collection<String> getExamples() {
      return field_201331_a;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   static class TagPredicate implements Predicate<BlockWorldState> {
      private final Tag<Block> field_199820_a;
      @Nullable
      private final NBTTagCompound field_199821_b;
      private final Map<String, String> field_200133_c;

      private TagPredicate(Tag<Block> var1, Map<String, String> var2, @Nullable NBTTagCompound var3) {
         super();
         this.field_199820_a = var1;
         this.field_200133_c = var2;
         this.field_199821_b = var3;
      }

      public boolean test(BlockWorldState var1) {
         IBlockState var2 = var1.func_177509_a();
         if (!var2.func_203425_a(this.field_199820_a)) {
            return false;
         } else {
            Iterator var3 = this.field_200133_c.entrySet().iterator();

            while(var3.hasNext()) {
               Entry var4 = (Entry)var3.next();
               IProperty var5 = var2.func_177230_c().func_176194_O().func_185920_a((String)var4.getKey());
               if (var5 == null) {
                  return false;
               }

               Comparable var6 = (Comparable)var5.func_185929_b((String)var4.getValue()).orElse((Object)null);
               if (var6 == null) {
                  return false;
               }

               if (var2.func_177229_b(var5) != var6) {
                  return false;
               }
            }

            if (this.field_199821_b == null) {
               return true;
            } else {
               TileEntity var7 = var1.func_177507_b();
               return var7 != null && NBTUtil.func_181123_a(this.field_199821_b, var7.func_189515_b(new NBTTagCompound()), true);
            }
         }
      }

      // $FF: synthetic method
      public boolean test(Object var1) {
         return this.test((BlockWorldState)var1);
      }

      // $FF: synthetic method
      TagPredicate(Tag var1, Map var2, NBTTagCompound var3, Object var4) {
         this(var1, var2, var3);
      }
   }

   static class BlockPredicate implements Predicate<BlockWorldState> {
      private final IBlockState field_199817_a;
      private final Set<IProperty<?>> field_199818_b;
      @Nullable
      private final NBTTagCompound field_199819_c;

      public BlockPredicate(IBlockState var1, Set<IProperty<?>> var2, @Nullable NBTTagCompound var3) {
         super();
         this.field_199817_a = var1;
         this.field_199818_b = var2;
         this.field_199819_c = var3;
      }

      public boolean test(BlockWorldState var1) {
         IBlockState var2 = var1.func_177509_a();
         if (var2.func_177230_c() != this.field_199817_a.func_177230_c()) {
            return false;
         } else {
            Iterator var3 = this.field_199818_b.iterator();

            while(var3.hasNext()) {
               IProperty var4 = (IProperty)var3.next();
               if (var2.func_177229_b(var4) != this.field_199817_a.func_177229_b(var4)) {
                  return false;
               }
            }

            if (this.field_199819_c == null) {
               return true;
            } else {
               TileEntity var5 = var1.func_177507_b();
               return var5 != null && NBTUtil.func_181123_a(this.field_199819_c, var5.func_189515_b(new NBTTagCompound()), true);
            }
         }
      }

      // $FF: synthetic method
      public boolean test(Object var1) {
         return this.test((BlockWorldState)var1);
      }
   }

   public interface IResult {
      Predicate<BlockWorldState> create(NetworkTagManager var1) throws CommandSyntaxException;
   }
}

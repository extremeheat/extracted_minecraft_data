package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NbtPathArgument implements ArgumentType<NbtPath> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}");
   public static final SimpleCommandExceptionType ERROR_INVALID_NODE = new SimpleCommandExceptionType(Component.translatable("arguments.nbtpath.node.invalid"));
   public static final SimpleCommandExceptionType ERROR_DATA_TOO_DEEP = new SimpleCommandExceptionType(Component.translatable("arguments.nbtpath.too_deep"));
   public static final DynamicCommandExceptionType ERROR_NOTHING_FOUND = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("arguments.nbtpath.nothing_found", var0);
   });
   static final DynamicCommandExceptionType ERROR_EXPECTED_LIST = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("commands.data.modify.expected_list", var0);
   });
   static final DynamicCommandExceptionType ERROR_INVALID_INDEX = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("commands.data.modify.invalid_index", var0);
   });
   private static final char INDEX_MATCH_START = '[';
   private static final char INDEX_MATCH_END = ']';
   private static final char KEY_MATCH_START = '{';
   private static final char KEY_MATCH_END = '}';
   private static final char QUOTED_KEY_START = '"';
   private static final char SINGLE_QUOTED_KEY_START = '\'';

   public NbtPathArgument() {
      super();
   }

   public static NbtPathArgument nbtPath() {
      return new NbtPathArgument();
   }

   public static NbtPath getPath(CommandContext<CommandSourceStack> var0, String var1) {
      return (NbtPath)var0.getArgument(var1, NbtPath.class);
   }

   public NbtPath parse(StringReader var1) throws CommandSyntaxException {
      ArrayList var2 = Lists.newArrayList();
      int var3 = var1.getCursor();
      Object2IntOpenHashMap var4 = new Object2IntOpenHashMap();
      boolean var5 = true;

      while(var1.canRead() && var1.peek() != ' ') {
         Node var6 = parseNode(var1, var5);
         var2.add(var6);
         var4.put(var6, var1.getCursor() - var3);
         var5 = false;
         if (var1.canRead()) {
            char var7 = var1.peek();
            if (var7 != ' ' && var7 != '[' && var7 != '{') {
               var1.expect('.');
            }
         }
      }

      return new NbtPath(var1.getString().substring(var3, var1.getCursor()), (Node[])var2.toArray(new Node[0]), var4);
   }

   private static Node parseNode(StringReader var0, boolean var1) throws CommandSyntaxException {
      Object var10000;
      switch (var0.peek()) {
         case '"':
         case '\'':
            var10000 = readObjectNode(var0, var0.readString());
            break;
         case '[':
            var0.skip();
            char var4 = var0.peek();
            if (var4 == '{') {
               CompoundTag var3 = (new TagParser(var0)).readStruct();
               var0.expect(']');
               var10000 = new MatchElementNode(var3);
            } else if (var4 == ']') {
               var0.skip();
               var10000 = NbtPathArgument.AllElementsNode.INSTANCE;
            } else {
               int var5 = var0.readInt();
               var0.expect(']');
               var10000 = new IndexedElementNode(var5);
            }
            break;
         case '{':
            if (!var1) {
               throw ERROR_INVALID_NODE.createWithContext(var0);
            }

            CompoundTag var2 = (new TagParser(var0)).readStruct();
            var10000 = new MatchRootObjectNode(var2);
            break;
         default:
            var10000 = readObjectNode(var0, readUnquotedName(var0));
      }

      return (Node)var10000;
   }

   private static Node readObjectNode(StringReader var0, String var1) throws CommandSyntaxException {
      if (var0.canRead() && var0.peek() == '{') {
         CompoundTag var2 = (new TagParser(var0)).readStruct();
         return new MatchObjectNode(var1, var2);
      } else {
         return new CompoundChildNode(var1);
      }
   }

   private static String readUnquotedName(StringReader var0) throws CommandSyntaxException {
      int var1 = var0.getCursor();

      while(var0.canRead() && isAllowedInUnquotedName(var0.peek())) {
         var0.skip();
      }

      if (var0.getCursor() == var1) {
         throw ERROR_INVALID_NODE.createWithContext(var0);
      } else {
         return var0.getString().substring(var1, var0.getCursor());
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   private static boolean isAllowedInUnquotedName(char var0) {
      return var0 != ' ' && var0 != '"' && var0 != '\'' && var0 != '[' && var0 != ']' && var0 != '.' && var0 != '{' && var0 != '}';
   }

   static Predicate<Tag> createTagPredicate(CompoundTag var0) {
      return (var1) -> {
         return NbtUtils.compareNbt(var0, var1, true);
      };
   }

   // $FF: synthetic method
   public Object parse(final StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public static class NbtPath {
      private final String original;
      private final Object2IntMap<Node> nodeToOriginalPosition;
      private final Node[] nodes;
      public static final Codec<NbtPath> CODEC;

      public static NbtPath of(String var0) throws CommandSyntaxException {
         return (new NbtPathArgument()).parse(new StringReader(var0));
      }

      public NbtPath(String var1, Node[] var2, Object2IntMap<Node> var3) {
         super();
         this.original = var1;
         this.nodes = var2;
         this.nodeToOriginalPosition = var3;
      }

      public List<Tag> get(Tag var1) throws CommandSyntaxException {
         List var2 = Collections.singletonList(var1);
         Node[] var3 = this.nodes;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Node var6 = var3[var5];
            var2 = var6.get(var2);
            if (var2.isEmpty()) {
               throw this.createNotFoundException(var6);
            }
         }

         return var2;
      }

      public int countMatching(Tag var1) {
         List var2 = Collections.singletonList(var1);
         Node[] var3 = this.nodes;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Node var6 = var3[var5];
            var2 = var6.get(var2);
            if (var2.isEmpty()) {
               return 0;
            }
         }

         return var2.size();
      }

      private List<Tag> getOrCreateParents(Tag var1) throws CommandSyntaxException {
         List var2 = Collections.singletonList(var1);

         for(int var3 = 0; var3 < this.nodes.length - 1; ++var3) {
            Node var4 = this.nodes[var3];
            int var5 = var3 + 1;
            Node var10002 = this.nodes[var5];
            Objects.requireNonNull(var10002);
            var2 = var4.getOrCreate(var2, var10002::createPreferredParentTag);
            if (var2.isEmpty()) {
               throw this.createNotFoundException(var4);
            }
         }

         return var2;
      }

      public List<Tag> getOrCreate(Tag var1, Supplier<Tag> var2) throws CommandSyntaxException {
         List var3 = this.getOrCreateParents(var1);
         Node var4 = this.nodes[this.nodes.length - 1];
         return var4.getOrCreate(var3, var2);
      }

      private static int apply(List<Tag> var0, Function<Tag, Integer> var1) {
         return (Integer)var0.stream().map(var1).reduce(0, (var0x, var1x) -> {
            return var0x + var1x;
         });
      }

      public static boolean isTooDeep(Tag var0, int var1) {
         if (var1 >= 512) {
            return true;
         } else {
            Iterator var4;
            if (var0 instanceof CompoundTag) {
               CompoundTag var2 = (CompoundTag)var0;
               var4 = var2.getAllKeys().iterator();

               while(var4.hasNext()) {
                  String var5 = (String)var4.next();
                  Tag var6 = var2.get(var5);
                  if (var6 != null && isTooDeep(var6, var1 + 1)) {
                     return true;
                  }
               }
            } else if (var0 instanceof ListTag) {
               ListTag var3 = (ListTag)var0;
               var4 = var3.iterator();

               while(var4.hasNext()) {
                  Tag var7 = (Tag)var4.next();
                  if (isTooDeep(var7, var1 + 1)) {
                     return true;
                  }
               }
            }

            return false;
         }
      }

      public int set(Tag var1, Tag var2) throws CommandSyntaxException {
         if (isTooDeep(var2, this.estimatePathDepth())) {
            throw NbtPathArgument.ERROR_DATA_TOO_DEEP.create();
         } else {
            Tag var3 = var2.copy();
            List var4 = this.getOrCreateParents(var1);
            if (var4.isEmpty()) {
               return 0;
            } else {
               Node var5 = this.nodes[this.nodes.length - 1];
               MutableBoolean var6 = new MutableBoolean(false);
               return apply(var4, (var3x) -> {
                  return var5.setTag(var3x, () -> {
                     if (var6.isFalse()) {
                        var6.setTrue();
                        return var3;
                     } else {
                        return var3.copy();
                     }
                  });
               });
            }
         }
      }

      private int estimatePathDepth() {
         return this.nodes.length;
      }

      public int insert(int var1, CompoundTag var2, List<Tag> var3) throws CommandSyntaxException {
         ArrayList var4 = new ArrayList(var3.size());
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            Tag var6 = (Tag)var5.next();
            Tag var7 = var6.copy();
            var4.add(var7);
            if (isTooDeep(var7, this.estimatePathDepth())) {
               throw NbtPathArgument.ERROR_DATA_TOO_DEEP.create();
            }
         }

         List var17 = this.getOrCreate(var2, ListTag::new);
         int var18 = 0;
         boolean var19 = false;

         boolean var11;
         for(Iterator var8 = var17.iterator(); var8.hasNext(); var18 += var11 ? 1 : 0) {
            Tag var9 = (Tag)var8.next();
            if (!(var9 instanceof CollectionTag)) {
               throw NbtPathArgument.ERROR_EXPECTED_LIST.create(var9);
            }

            CollectionTag var10 = (CollectionTag)var9;
            var11 = false;
            int var12 = var1 < 0 ? var10.size() + var1 + 1 : var1;
            Iterator var13 = var4.iterator();

            while(var13.hasNext()) {
               Tag var14 = (Tag)var13.next();

               try {
                  if (var10.addTag(var12, var19 ? var14.copy() : var14)) {
                     ++var12;
                     var11 = true;
                  }
               } catch (IndexOutOfBoundsException var16) {
                  throw NbtPathArgument.ERROR_INVALID_INDEX.create(var12);
               }
            }

            var19 = true;
         }

         return var18;
      }

      public int remove(Tag var1) {
         List var2 = Collections.singletonList(var1);

         for(int var3 = 0; var3 < this.nodes.length - 1; ++var3) {
            var2 = this.nodes[var3].get(var2);
         }

         Node var4 = this.nodes[this.nodes.length - 1];
         Objects.requireNonNull(var4);
         return apply(var2, var4::removeTag);
      }

      private CommandSyntaxException createNotFoundException(Node var1) {
         int var2 = this.nodeToOriginalPosition.getInt(var1);
         return NbtPathArgument.ERROR_NOTHING_FOUND.create(this.original.substring(0, var2));
      }

      public String toString() {
         return this.original;
      }

      public String asString() {
         return this.original;
      }

      static {
         CODEC = Codec.STRING.comapFlatMap((var0) -> {
            try {
               NbtPath var1 = (new NbtPathArgument()).parse(new StringReader(var0));
               return DataResult.success(var1);
            } catch (CommandSyntaxException var2) {
               return DataResult.error(() -> {
                  return "Failed to parse path " + var0 + ": " + var2.getMessage();
               });
            }
         }, NbtPath::asString);
      }
   }

   private interface Node {
      void getTag(Tag var1, List<Tag> var2);

      void getOrCreateTag(Tag var1, Supplier<Tag> var2, List<Tag> var3);

      Tag createPreferredParentTag();

      int setTag(Tag var1, Supplier<Tag> var2);

      int removeTag(Tag var1);

      default List<Tag> get(List<Tag> var1) {
         return this.collect(var1, this::getTag);
      }

      default List<Tag> getOrCreate(List<Tag> var1, Supplier<Tag> var2) {
         return this.collect(var1, (var2x, var3) -> {
            this.getOrCreateTag(var2x, var2, var3);
         });
      }

      default List<Tag> collect(List<Tag> var1, BiConsumer<Tag, List<Tag>> var2) {
         ArrayList var3 = Lists.newArrayList();
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            Tag var5 = (Tag)var4.next();
            var2.accept(var5, var3);
         }

         return var3;
      }
   }

   static class MatchRootObjectNode implements Node {
      private final Predicate<Tag> predicate;

      public MatchRootObjectNode(CompoundTag var1) {
         super();
         this.predicate = NbtPathArgument.createTagPredicate(var1);
      }

      public void getTag(Tag var1, List<Tag> var2) {
         if (var1 instanceof CompoundTag && this.predicate.test(var1)) {
            var2.add(var1);
         }

      }

      public void getOrCreateTag(Tag var1, Supplier<Tag> var2, List<Tag> var3) {
         this.getTag(var1, var3);
      }

      public Tag createPreferredParentTag() {
         return new CompoundTag();
      }

      public int setTag(Tag var1, Supplier<Tag> var2) {
         return 0;
      }

      public int removeTag(Tag var1) {
         return 0;
      }
   }

   static class MatchElementNode implements Node {
      private final CompoundTag pattern;
      private final Predicate<Tag> predicate;

      public MatchElementNode(CompoundTag var1) {
         super();
         this.pattern = var1;
         this.predicate = NbtPathArgument.createTagPredicate(var1);
      }

      public void getTag(Tag var1, List<Tag> var2) {
         if (var1 instanceof ListTag var3) {
            Stream var10000 = var3.stream().filter(this.predicate);
            Objects.requireNonNull(var2);
            var10000.forEach(var2::add);
         }

      }

      public void getOrCreateTag(Tag var1, Supplier<Tag> var2, List<Tag> var3) {
         MutableBoolean var4 = new MutableBoolean();
         if (var1 instanceof ListTag var5) {
            var5.stream().filter(this.predicate).forEach((var2x) -> {
               var3.add(var2x);
               var4.setTrue();
            });
            if (var4.isFalse()) {
               CompoundTag var6 = this.pattern.copy();
               var5.add(var6);
               var3.add(var6);
            }
         }

      }

      public Tag createPreferredParentTag() {
         return new ListTag();
      }

      public int setTag(Tag var1, Supplier<Tag> var2) {
         int var3 = 0;
         if (var1 instanceof ListTag var4) {
            int var5 = var4.size();
            if (var5 == 0) {
               var4.add((Tag)var2.get());
               ++var3;
            } else {
               for(int var6 = 0; var6 < var5; ++var6) {
                  Tag var7 = var4.get(var6);
                  if (this.predicate.test(var7)) {
                     Tag var8 = (Tag)var2.get();
                     if (!var8.equals(var7) && var4.setTag(var6, var8)) {
                        ++var3;
                     }
                  }
               }
            }
         }

         return var3;
      }

      public int removeTag(Tag var1) {
         int var2 = 0;
         if (var1 instanceof ListTag var3) {
            for(int var4 = var3.size() - 1; var4 >= 0; --var4) {
               if (this.predicate.test(var3.get(var4))) {
                  var3.remove(var4);
                  ++var2;
               }
            }
         }

         return var2;
      }
   }

   static class AllElementsNode implements Node {
      public static final AllElementsNode INSTANCE = new AllElementsNode();

      private AllElementsNode() {
         super();
      }

      public void getTag(Tag var1, List<Tag> var2) {
         if (var1 instanceof CollectionTag) {
            var2.addAll((CollectionTag)var1);
         }

      }

      public void getOrCreateTag(Tag var1, Supplier<Tag> var2, List<Tag> var3) {
         if (var1 instanceof CollectionTag var4) {
            if (var4.isEmpty()) {
               Tag var5 = (Tag)var2.get();
               if (var4.addTag(0, var5)) {
                  var3.add(var5);
               }
            } else {
               var3.addAll(var4);
            }
         }

      }

      public Tag createPreferredParentTag() {
         return new ListTag();
      }

      public int setTag(Tag var1, Supplier<Tag> var2) {
         if (!(var1 instanceof CollectionTag var3)) {
            return 0;
         } else {
            int var4 = var3.size();
            if (var4 == 0) {
               var3.addTag(0, (Tag)var2.get());
               return 1;
            } else {
               Tag var5 = (Tag)var2.get();
               Stream var10001 = var3.stream();
               Objects.requireNonNull(var5);
               int var6 = var4 - (int)var10001.filter(var5::equals).count();
               if (var6 == 0) {
                  return 0;
               } else {
                  var3.clear();
                  if (!var3.addTag(0, var5)) {
                     return 0;
                  } else {
                     for(int var7 = 1; var7 < var4; ++var7) {
                        var3.addTag(var7, (Tag)var2.get());
                     }

                     return var6;
                  }
               }
            }
         }
      }

      public int removeTag(Tag var1) {
         if (var1 instanceof CollectionTag var2) {
            int var3 = var2.size();
            if (var3 > 0) {
               var2.clear();
               return var3;
            }
         }

         return 0;
      }
   }

   static class IndexedElementNode implements Node {
      private final int index;

      public IndexedElementNode(int var1) {
         super();
         this.index = var1;
      }

      public void getTag(Tag var1, List<Tag> var2) {
         if (var1 instanceof CollectionTag var3) {
            int var4 = var3.size();
            int var5 = this.index < 0 ? var4 + this.index : this.index;
            if (0 <= var5 && var5 < var4) {
               var2.add((Tag)var3.get(var5));
            }
         }

      }

      public void getOrCreateTag(Tag var1, Supplier<Tag> var2, List<Tag> var3) {
         this.getTag(var1, var3);
      }

      public Tag createPreferredParentTag() {
         return new ListTag();
      }

      public int setTag(Tag var1, Supplier<Tag> var2) {
         if (var1 instanceof CollectionTag var3) {
            int var4 = var3.size();
            int var5 = this.index < 0 ? var4 + this.index : this.index;
            if (0 <= var5 && var5 < var4) {
               Tag var6 = (Tag)var3.get(var5);
               Tag var7 = (Tag)var2.get();
               if (!var7.equals(var6) && var3.setTag(var5, var7)) {
                  return 1;
               }
            }
         }

         return 0;
      }

      public int removeTag(Tag var1) {
         if (var1 instanceof CollectionTag var2) {
            int var3 = var2.size();
            int var4 = this.index < 0 ? var3 + this.index : this.index;
            if (0 <= var4 && var4 < var3) {
               var2.remove(var4);
               return 1;
            }
         }

         return 0;
      }
   }

   static class MatchObjectNode implements Node {
      private final String name;
      private final CompoundTag pattern;
      private final Predicate<Tag> predicate;

      public MatchObjectNode(String var1, CompoundTag var2) {
         super();
         this.name = var1;
         this.pattern = var2;
         this.predicate = NbtPathArgument.createTagPredicate(var2);
      }

      public void getTag(Tag var1, List<Tag> var2) {
         if (var1 instanceof CompoundTag) {
            Tag var3 = ((CompoundTag)var1).get(this.name);
            if (this.predicate.test(var3)) {
               var2.add(var3);
            }
         }

      }

      public void getOrCreateTag(Tag var1, Supplier<Tag> var2, List<Tag> var3) {
         if (var1 instanceof CompoundTag var4) {
            Tag var5 = var4.get(this.name);
            if (var5 == null) {
               CompoundTag var6 = this.pattern.copy();
               var4.put(this.name, var6);
               var3.add(var6);
            } else if (this.predicate.test(var5)) {
               var3.add(var5);
            }
         }

      }

      public Tag createPreferredParentTag() {
         return new CompoundTag();
      }

      public int setTag(Tag var1, Supplier<Tag> var2) {
         if (var1 instanceof CompoundTag var3) {
            Tag var4 = var3.get(this.name);
            if (this.predicate.test(var4)) {
               Tag var5 = (Tag)var2.get();
               if (!var5.equals(var4)) {
                  var3.put(this.name, var5);
                  return 1;
               }
            }
         }

         return 0;
      }

      public int removeTag(Tag var1) {
         if (var1 instanceof CompoundTag var2) {
            Tag var3 = var2.get(this.name);
            if (this.predicate.test(var3)) {
               var2.remove(this.name);
               return 1;
            }
         }

         return 0;
      }
   }

   static class CompoundChildNode implements Node {
      private final String name;

      public CompoundChildNode(String var1) {
         super();
         this.name = var1;
      }

      public void getTag(Tag var1, List<Tag> var2) {
         if (var1 instanceof CompoundTag) {
            Tag var3 = ((CompoundTag)var1).get(this.name);
            if (var3 != null) {
               var2.add(var3);
            }
         }

      }

      public void getOrCreateTag(Tag var1, Supplier<Tag> var2, List<Tag> var3) {
         if (var1 instanceof CompoundTag var4) {
            Tag var5;
            if (var4.contains(this.name)) {
               var5 = var4.get(this.name);
            } else {
               var5 = (Tag)var2.get();
               var4.put(this.name, var5);
            }

            var3.add(var5);
         }

      }

      public Tag createPreferredParentTag() {
         return new CompoundTag();
      }

      public int setTag(Tag var1, Supplier<Tag> var2) {
         if (var1 instanceof CompoundTag var3) {
            Tag var4 = (Tag)var2.get();
            Tag var5 = var3.put(this.name, var4);
            if (!var4.equals(var5)) {
               return 1;
            }
         }

         return 0;
      }

      public int removeTag(Tag var1) {
         if (var1 instanceof CompoundTag var2) {
            if (var2.contains(this.name)) {
               var2.remove(this.name);
               return 1;
            }
         }

         return 0;
      }
   }
}

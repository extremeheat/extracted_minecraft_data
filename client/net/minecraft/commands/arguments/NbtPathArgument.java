package net.minecraft.commands.arguments;

import com.google.common.collect.Iterables;
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
   public static final DynamicCommandExceptionType ERROR_NOTHING_FOUND = new DynamicCommandExceptionType((path) -> Component.translatableEscape("arguments.nbtpath.nothing_found", path));
   private static final DynamicCommandExceptionType ERROR_EXPECTED_LIST = new DynamicCommandExceptionType((node) -> Component.translatableEscape("commands.data.modify.expected_list", node));
   private static final DynamicCommandExceptionType ERROR_INVALID_INDEX = new DynamicCommandExceptionType((node) -> Component.translatableEscape("commands.data.modify.invalid_index", node));
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

   public static NbtPath getPath(final CommandContext<CommandSourceStack> context, final String name) {
      return (NbtPath)context.getArgument(name, NbtPath.class);
   }

   public NbtPath parse(final StringReader reader) throws CommandSyntaxException {
      List<Node> nodes = Lists.newArrayList();
      int start = reader.getCursor();
      Object2IntMap<Node> nodeToOriginalPosition = new Object2IntOpenHashMap();
      boolean firstNode = true;

      while(reader.canRead() && reader.peek() != ' ') {
         Node node = parseNode(reader, firstNode);
         nodes.add(node);
         nodeToOriginalPosition.put(node, reader.getCursor() - start);
         firstNode = false;
         if (reader.canRead()) {
            char next = reader.peek();
            if (next != ' ' && next != '[' && next != '{') {
               reader.expect('.');
            }
         }
      }

      return new NbtPath(reader.getString().substring(start, reader.getCursor()), (Node[])nodes.toArray(new Node[0]), nodeToOriginalPosition);
   }

   private static Node parseNode(final StringReader reader, final boolean firstNode) throws CommandSyntaxException {
      Object var10000;
      switch (reader.peek()) {
         case '"':
         case '\'':
            var10000 = readObjectNode(reader, reader.readString());
            break;
         case '[':
            reader.skip();
            int next = reader.peek();
            if (next == 123) {
               CompoundTag pattern = TagParser.parseCompoundAsArgument(reader);
               reader.expect(']');
               var10000 = new MatchElementNode(pattern);
            } else if (next == 93) {
               reader.skip();
               var10000 = NbtPathArgument.AllElementsNode.INSTANCE;
            } else {
               int index = reader.readInt();
               reader.expect(']');
               var10000 = new IndexedElementNode(index);
            }
            break;
         case '{':
            if (!firstNode) {
               throw ERROR_INVALID_NODE.createWithContext(reader);
            }

            CompoundTag pattern = TagParser.parseCompoundAsArgument(reader);
            var10000 = new MatchRootObjectNode(pattern);
            break;
         default:
            var10000 = readObjectNode(reader, readUnquotedName(reader));
      }

      return (Node)var10000;
   }

   private static Node readObjectNode(final StringReader reader, final String name) throws CommandSyntaxException {
      if (name.isEmpty()) {
         throw ERROR_INVALID_NODE.createWithContext(reader);
      } else if (reader.canRead() && reader.peek() == '{') {
         CompoundTag pattern = TagParser.parseCompoundAsArgument(reader);
         return new MatchObjectNode(name, pattern);
      } else {
         return new CompoundChildNode(name);
      }
   }

   private static String readUnquotedName(final StringReader reader) throws CommandSyntaxException {
      int start = reader.getCursor();

      while(reader.canRead() && isAllowedInUnquotedName(reader.peek())) {
         reader.skip();
      }

      if (reader.getCursor() == start) {
         throw ERROR_INVALID_NODE.createWithContext(reader);
      } else {
         return reader.getString().substring(start, reader.getCursor());
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   private static boolean isAllowedInUnquotedName(final char c) {
      return c != ' ' && c != '"' && c != '\'' && c != '[' && c != ']' && c != '.' && c != '{' && c != '}';
   }

   private static Predicate<Tag> createTagPredicate(final CompoundTag pattern) {
      return (tag) -> NbtUtils.compareNbt(pattern, tag, true);
   }

   public static class NbtPath {
      private final String original;
      private final Object2IntMap<Node> nodeToOriginalPosition;
      private final Node[] nodes;
      public static final Codec<NbtPath> CODEC;

      public static NbtPath of(final String string) throws CommandSyntaxException {
         return (new NbtPathArgument()).parse(new StringReader(string));
      }

      public NbtPath(final String original, final Node[] nodes, final Object2IntMap<Node> nodeToOriginalPosition) {
         super();
         this.original = original;
         this.nodes = nodes;
         this.nodeToOriginalPosition = nodeToOriginalPosition;
      }

      public List<Tag> get(final Tag tag) throws CommandSyntaxException {
         List<Tag> result = Collections.singletonList(tag);

         for(Node node : this.nodes) {
            result = node.get(result);
            if (result.isEmpty()) {
               throw this.createNotFoundException(node);
            }
         }

         return result;
      }

      public int countMatching(final Tag tag) {
         List<Tag> result = Collections.singletonList(tag);

         for(Node node : this.nodes) {
            result = node.get(result);
            if (result.isEmpty()) {
               return 0;
            }
         }

         return result.size();
      }

      private List<Tag> getOrCreateParents(final Tag tag) throws CommandSyntaxException {
         List<Tag> result = Collections.singletonList(tag);

         for(int i = 0; i < this.nodes.length - 1; ++i) {
            Node node = this.nodes[i];
            int next = i + 1;
            Node var10002 = this.nodes[next];
            Objects.requireNonNull(var10002);
            result = node.getOrCreate(result, var10002::createPreferredParentTag);
            if (result.isEmpty()) {
               throw this.createNotFoundException(node);
            }
         }

         return result;
      }

      public List<Tag> getOrCreate(final Tag tag, final Supplier<Tag> newTagValue) throws CommandSyntaxException {
         List<Tag> result = this.getOrCreateParents(tag);
         Node lastNode = this.nodes[this.nodes.length - 1];
         return lastNode.getOrCreate(result, newTagValue);
      }

      private static int apply(final List<Tag> targets, final Function<Tag, Integer> operation) {
         return (Integer)targets.stream().map(operation).reduce(0, (a, b) -> a + b);
      }

      public static boolean isTooDeep(final Tag tag, final int depth) {
         if (depth >= 512) {
            return true;
         } else {
            if (tag instanceof CompoundTag) {
               CompoundTag compound = (CompoundTag)tag;

               for(Tag child : compound.values()) {
                  if (isTooDeep(child, depth + 1)) {
                     return true;
                  }
               }
            } else if (tag instanceof ListTag) {
               for(Tag listEntry : (ListTag)tag) {
                  if (isTooDeep(listEntry, depth + 1)) {
                     return true;
                  }
               }
            }

            return false;
         }
      }

      public int set(final Tag tag, final Tag toAdd) throws CommandSyntaxException {
         if (isTooDeep(toAdd, this.estimatePathDepth())) {
            throw NbtPathArgument.ERROR_DATA_TOO_DEEP.create();
         } else {
            Tag firstCopy = toAdd.copy();
            List<Tag> result = this.getOrCreateParents(tag);
            if (result.isEmpty()) {
               return 0;
            } else {
               Node lastNode = this.nodes[this.nodes.length - 1];
               MutableBoolean usedFirstCopy = new MutableBoolean(false);
               return apply(result, (t) -> lastNode.setTag(t, () -> {
                     if (usedFirstCopy.isFalse()) {
                        usedFirstCopy.setTrue();
                        return firstCopy;
                     } else {
                        return firstCopy.copy();
                     }
                  }));
            }
         }
      }

      private int estimatePathDepth() {
         return this.nodes.length;
      }

      public int insert(final int index, final CompoundTag target, final List<Tag> toInsert) throws CommandSyntaxException {
         List<Tag> toInsertCopy = new ArrayList(toInsert.size());

         for(Tag tag : toInsert) {
            Tag copy = tag.copy();
            toInsertCopy.add(copy);
            if (isTooDeep(copy, this.estimatePathDepth())) {
               throw NbtPathArgument.ERROR_DATA_TOO_DEEP.create();
            }
         }

         Collection<Tag> targets = this.getOrCreate(target, ListTag::new);
         int modifiedCount = 0;
         boolean usedFirst = false;

         for(Tag targetTag : targets) {
            if (!(targetTag instanceof CollectionTag)) {
               throw NbtPathArgument.ERROR_EXPECTED_LIST.create(targetTag);
            }

            CollectionTag targetList = (CollectionTag)targetTag;
            boolean modified = false;
            int actualIndex = index < 0 ? targetList.size() + index + 1 : index;

            for(Tag sourceTag : toInsertCopy) {
               try {
                  if (targetList.addTag(actualIndex, usedFirst ? sourceTag.copy() : sourceTag)) {
                     ++actualIndex;
                     modified = true;
                  }
               } catch (IndexOutOfBoundsException var16) {
                  throw NbtPathArgument.ERROR_INVALID_INDEX.create(actualIndex);
               }
            }

            usedFirst = true;
            modifiedCount += modified ? 1 : 0;
         }

         return modifiedCount;
      }

      public int remove(final Tag tag) {
         List<Tag> result = Collections.singletonList(tag);

         for(int i = 0; i < this.nodes.length - 1; ++i) {
            result = this.nodes[i].get(result);
         }

         Node lastNode = this.nodes[this.nodes.length - 1];
         Objects.requireNonNull(lastNode);
         return apply(result, lastNode::removeTag);
      }

      private CommandSyntaxException createNotFoundException(final Node node) {
         int index = this.nodeToOriginalPosition.getInt(node);
         return NbtPathArgument.ERROR_NOTHING_FOUND.create(this.original.substring(0, index));
      }

      public String toString() {
         return this.original;
      }

      public String asString() {
         return this.original;
      }

      static {
         CODEC = Codec.STRING.comapFlatMap((string) -> {
            try {
               NbtPath parsed = (new NbtPathArgument()).parse(new StringReader(string));
               return DataResult.success(parsed);
            } catch (CommandSyntaxException e) {
               return DataResult.error(() -> "Failed to parse path " + string + ": " + e.getMessage());
            }
         }, NbtPath::asString);
      }
   }

   private interface Node {
      void getTag(Tag parent, final List<Tag> output);

      void getOrCreateTag(Tag parent, Supplier<Tag> child, final List<Tag> output);

      Tag createPreferredParentTag();

      int setTag(Tag parent, Supplier<Tag> toAdd);

      int removeTag(Tag parent);

      default List<Tag> get(final List<Tag> tags) {
         return this.collect(tags, this::getTag);
      }

      default List<Tag> getOrCreate(final List<Tag> tags, final Supplier<Tag> child) {
         return this.collect(tags, (tag, output) -> this.getOrCreateTag(tag, child, output));
      }

      default List<Tag> collect(final List<Tag> tags, final BiConsumer<Tag, List<Tag>> collector) {
         List<Tag> result = Lists.newArrayList();

         for(Tag tag : tags) {
            collector.accept(tag, result);
         }

         return result;
      }
   }

   private static class CompoundChildNode implements Node {
      private final String name;

      public CompoundChildNode(final String name) {
         super();
         this.name = name;
      }

      public void getTag(final Tag parent, final List<Tag> output) {
         if (parent instanceof CompoundTag) {
            Tag result = ((CompoundTag)parent).get(this.name);
            if (result != null) {
               output.add(result);
            }
         }

      }

      public void getOrCreateTag(final Tag parent, final Supplier<Tag> child, final List<Tag> output) {
         if (parent instanceof CompoundTag compound) {
            Tag result;
            if (compound.contains(this.name)) {
               result = compound.get(this.name);
            } else {
               result = (Tag)child.get();
               compound.put(this.name, result);
            }

            output.add(result);
         }

      }

      public Tag createPreferredParentTag() {
         return new CompoundTag();
      }

      public int setTag(final Tag parent, final Supplier<Tag> toAdd) {
         if (parent instanceof CompoundTag compound) {
            Tag newValue = (Tag)toAdd.get();
            Tag previousValue = compound.put(this.name, newValue);
            if (!newValue.equals(previousValue)) {
               return 1;
            }
         }

         return 0;
      }

      public int removeTag(final Tag parent) {
         if (parent instanceof CompoundTag compound) {
            if (compound.contains(this.name)) {
               compound.remove(this.name);
               return 1;
            }
         }

         return 0;
      }
   }

   private static class IndexedElementNode implements Node {
      private final int index;

      public IndexedElementNode(final int index) {
         super();
         this.index = index;
      }

      public void getTag(final Tag parent, final List<Tag> output) {
         if (parent instanceof CollectionTag list) {
            int size = list.size();
            int actualIndex = this.index < 0 ? size + this.index : this.index;
            if (0 <= actualIndex && actualIndex < size) {
               output.add(list.get(actualIndex));
            }
         }

      }

      public void getOrCreateTag(final Tag parent, final Supplier<Tag> child, final List<Tag> output) {
         this.getTag(parent, output);
      }

      public Tag createPreferredParentTag() {
         return new ListTag();
      }

      public int setTag(final Tag parent, final Supplier<Tag> toAdd) {
         if (parent instanceof CollectionTag list) {
            int size = list.size();
            int actualIndex = this.index < 0 ? size + this.index : this.index;
            if (0 <= actualIndex && actualIndex < size) {
               Tag previousValue = list.get(actualIndex);
               Tag newValue = (Tag)toAdd.get();
               if (!newValue.equals(previousValue) && list.setTag(actualIndex, newValue)) {
                  return 1;
               }
            }
         }

         return 0;
      }

      public int removeTag(final Tag parent) {
         if (parent instanceof CollectionTag list) {
            int size = list.size();
            int actualIndex = this.index < 0 ? size + this.index : this.index;
            if (0 <= actualIndex && actualIndex < size) {
               list.remove(actualIndex);
               return 1;
            }
         }

         return 0;
      }
   }

   private static class MatchElementNode implements Node {
      private final CompoundTag pattern;
      private final Predicate<Tag> predicate;

      public MatchElementNode(final CompoundTag pattern) {
         super();
         this.pattern = pattern;
         this.predicate = NbtPathArgument.createTagPredicate(pattern);
      }

      public void getTag(final Tag parent, final List<Tag> output) {
         if (parent instanceof ListTag list) {
            Stream var10000 = list.stream().filter(this.predicate);
            Objects.requireNonNull(output);
            var10000.forEach(output::add);
         }

      }

      public void getOrCreateTag(final Tag parent, final Supplier<Tag> child, final List<Tag> output) {
         MutableBoolean foundAnything = new MutableBoolean();
         if (parent instanceof ListTag list) {
            list.stream().filter(this.predicate).forEach((t) -> {
               output.add(t);
               foundAnything.setTrue();
            });
            if (foundAnything.isFalse()) {
               CompoundTag newTag = this.pattern.copy();
               list.add(newTag);
               output.add(newTag);
            }
         }

      }

      public Tag createPreferredParentTag() {
         return new ListTag();
      }

      public int setTag(final Tag parent, final Supplier<Tag> toAdd) {
         int changedCount = 0;
         if (parent instanceof ListTag list) {
            int size = list.size();
            if (size == 0) {
               list.add((Tag)toAdd.get());
               ++changedCount;
            } else {
               for(int i = 0; i < size; ++i) {
                  Tag currentValue = list.get(i);
                  if (this.predicate.test(currentValue)) {
                     Tag newValue = (Tag)toAdd.get();
                     if (!newValue.equals(currentValue) && list.setTag(i, newValue)) {
                        ++changedCount;
                     }
                  }
               }
            }
         }

         return changedCount;
      }

      public int removeTag(final Tag parent) {
         int changedCount = 0;
         if (parent instanceof ListTag list) {
            for(int i = list.size() - 1; i >= 0; --i) {
               if (this.predicate.test(list.get(i))) {
                  list.remove(i);
                  ++changedCount;
               }
            }
         }

         return changedCount;
      }
   }

   private static class AllElementsNode implements Node {
      public static final AllElementsNode INSTANCE = new AllElementsNode();

      private AllElementsNode() {
         super();
      }

      public void getTag(final Tag parent, final List<Tag> output) {
         if (parent instanceof CollectionTag collection) {
            Iterables.addAll(output, collection);
         }

      }

      public void getOrCreateTag(final Tag parent, final Supplier<Tag> child, final List<Tag> output) {
         if (parent instanceof CollectionTag list) {
            if (list.isEmpty()) {
               Tag result = (Tag)child.get();
               if (list.addTag(0, result)) {
                  output.add(result);
               }
            } else {
               Iterables.addAll(output, list);
            }
         }

      }

      public Tag createPreferredParentTag() {
         return new ListTag();
      }

      public int setTag(final Tag parent, final Supplier<Tag> toAdd) {
         if (!(parent instanceof CollectionTag list)) {
            return 0;
         } else {
            int size = list.size();
            if (size == 0) {
               list.addTag(0, (Tag)toAdd.get());
               return 1;
            } else {
               Tag newValue = (Tag)toAdd.get();
               Stream var10001 = list.stream();
               Objects.requireNonNull(newValue);
               int changedCount = size - (int)var10001.filter(newValue::equals).count();
               if (changedCount == 0) {
                  return 0;
               } else {
                  list.clear();
                  if (!list.addTag(0, newValue)) {
                     return 0;
                  } else {
                     for(int i = 1; i < size; ++i) {
                        list.addTag(i, (Tag)toAdd.get());
                     }

                     return changedCount;
                  }
               }
            }
         }
      }

      public int removeTag(final Tag parent) {
         if (parent instanceof CollectionTag list) {
            int size = list.size();
            if (size > 0) {
               list.clear();
               return size;
            }
         }

         return 0;
      }
   }

   private static class MatchObjectNode implements Node {
      private final String name;
      private final CompoundTag pattern;
      private final Predicate<Tag> predicate;

      public MatchObjectNode(final String name, final CompoundTag pattern) {
         super();
         this.name = name;
         this.pattern = pattern;
         this.predicate = NbtPathArgument.createTagPredicate(pattern);
      }

      public void getTag(final Tag parent, final List<Tag> output) {
         if (parent instanceof CompoundTag) {
            Tag result = ((CompoundTag)parent).get(this.name);
            if (this.predicate.test(result)) {
               output.add(result);
            }
         }

      }

      public void getOrCreateTag(final Tag parent, final Supplier<Tag> child, final List<Tag> output) {
         if (parent instanceof CompoundTag compound) {
            Tag result = compound.get(this.name);
            if (result == null) {
               Tag var6 = this.pattern.copy();
               compound.put(this.name, var6);
               output.add(var6);
            } else if (this.predicate.test(result)) {
               output.add(result);
            }
         }

      }

      public Tag createPreferredParentTag() {
         return new CompoundTag();
      }

      public int setTag(final Tag parent, final Supplier<Tag> toAdd) {
         if (parent instanceof CompoundTag compound) {
            Tag currentValue = compound.get(this.name);
            if (this.predicate.test(currentValue)) {
               Tag newValue = (Tag)toAdd.get();
               if (!newValue.equals(currentValue)) {
                  compound.put(this.name, newValue);
                  return 1;
               }
            }
         }

         return 0;
      }

      public int removeTag(final Tag parent) {
         if (parent instanceof CompoundTag compound) {
            Tag current = compound.get(this.name);
            if (this.predicate.test(current)) {
               compound.remove(this.name);
               return 1;
            }
         }

         return 0;
      }
   }

   private static class MatchRootObjectNode implements Node {
      private final Predicate<Tag> predicate;

      public MatchRootObjectNode(final CompoundTag pattern) {
         super();
         this.predicate = NbtPathArgument.createTagPredicate(pattern);
      }

      public void getTag(final Tag self, final List<Tag> output) {
         if (self instanceof CompoundTag && this.predicate.test(self)) {
            output.add(self);
         }

      }

      public void getOrCreateTag(final Tag self, final Supplier<Tag> child, final List<Tag> output) {
         this.getTag(self, output);
      }

      public Tag createPreferredParentTag() {
         return new CompoundTag();
      }

      public int setTag(final Tag parent, final Supplier<Tag> toAdd) {
         return 0;
      }

      public int removeTag(final Tag parent) {
         return 0;
      }
   }
}

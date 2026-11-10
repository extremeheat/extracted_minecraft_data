package net.minecraft.network.protocol.game;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import net.minecraft.commands.arguments.SerializableArgument;
import net.minecraft.commands.arguments.SerializableArgumentTypes;
import net.minecraft.commands.suggestions.CustomSuggestion;
import net.minecraft.commands.suggestions.SerializableCustomSuggestion;
import net.minecraft.commands.suggestions.SerializableSuggestionTypes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.CommonPacketTypes;
import org.jspecify.annotations.Nullable;

public class ClientboundCommandsPacket implements Packet<ClientCommonPacketListener> {
   private static final StreamCodec<RegistryFriendlyByteBuf, Entry> ENTRY_STREAM_CODEC = StreamCodec.<RegistryFriendlyByteBuf, Entry>of((output, entry) -> entry.write(output), ClientboundCommandsPacket::readNode);
   private static final StreamCodec<RegistryFriendlyByteBuf, ClientboundCommandsPacket> RAW_STREAM_CODEC;
   public static final StreamCodec<ByteBuf, ClientboundCommandsPacket> STREAM_CODEC;
   private static final byte MASK_TYPE = 3;
   private static final byte FLAG_EXECUTABLE = 4;
   private static final byte FLAG_REDIRECT = 8;
   private static final byte FLAG_CUSTOM_SUGGESTIONS = 16;
   private static final byte FLAG_RESTRICTED = 32;
   private static final byte TYPE_ROOT = 0;
   private static final byte TYPE_LITERAL = 1;
   private static final byte TYPE_ARGUMENT = 2;
   private final int rootIndex;
   private final List<Entry> entries;

   public <S, T extends S> ClientboundCommandsPacket(final RootCommandNode<T> root, final NodeInspector<S> inspector) {
      super();
      Object2IntMap<CommandNode<T>> nodeToId = enumerateNodes(root);
      this.entries = createEntries(nodeToId, inspector);
      this.rootIndex = nodeToId.getInt(root);
   }

   private ClientboundCommandsPacket(final List<Entry> entries, final int rootIndex) {
      super();
      this.entries = entries;
      this.rootIndex = rootIndex;
      validateEntries(this.entries);
   }

   private static void validateEntries(final List<Entry> entries, final BiPredicate<Entry, IntSet> validator) {
      IntSet elementsToCheck = new IntOpenHashSet(IntSets.fromTo(0, entries.size()));

      while(!elementsToCheck.isEmpty()) {
         boolean worked = elementsToCheck.removeIf((index) -> validator.test((Entry)entries.get(index), elementsToCheck));
         if (!worked) {
            throw new IllegalStateException("Server sent an impossible command tree");
         }
      }

   }

   private static void validateEntries(final List<Entry> entries) {
      validateEntries(entries, Entry::canBuild);
      validateEntries(entries, Entry::canResolve);
   }

   private static <S> Object2IntMap<CommandNode<S>> enumerateNodes(final RootCommandNode<S> root) {
      Object2IntMap<CommandNode<S>> nodeToId = new Object2IntOpenHashMap();
      Queue<CommandNode<S>> queue = new ArrayDeque();
      queue.add(root);

      CommandNode<S> node;
      while((node = (CommandNode)queue.poll()) != null) {
         if (!nodeToId.containsKey(node)) {
            int id = nodeToId.size();
            nodeToId.put(node, id);
            queue.addAll(node.getChildren());
            if (node.getRedirect() != null) {
               queue.add(node.getRedirect());
            }
         }
      }

      return nodeToId;
   }

   private static <S> List<Entry> createEntries(final Object2IntMap<? extends CommandNode<? extends S>> nodeToId, final NodeInspector<S> inspector) {
      ObjectArrayList<Entry> result = new ObjectArrayList(nodeToId.size());
      result.size(nodeToId.size());
      ObjectIterator var3 = Object2IntMaps.fastIterable(nodeToId).iterator();

      while(var3.hasNext()) {
         Object2IntMap.Entry<? extends CommandNode<? extends S>> entry = (Object2IntMap.Entry)var3.next();
         result.set(entry.getIntValue(), createEntry((CommandNode)entry.getKey(), inspector, nodeToId));
      }

      return result;
   }

   private static Entry readNode(final RegistryFriendlyByteBuf input) {
      byte flags = input.readByte();
      int[] children = input.readVarIntArray();
      int redirect = (flags & 8) != 0 ? input.readVarInt() : 0;
      NodeStub stub = read(input, flags);
      return new Entry(stub, flags, redirect, children);
   }

   private static @Nullable NodeStub read(final RegistryFriendlyByteBuf input, final byte flags) {
      int type = flags & 3;
      if (type == 2) {
         StreamCodec<RegistryFriendlyByteBuf, ? extends NodeStub> argumentCodec = (flags & 16) != 0 ? ClientboundCommandsPacket.ArgumentNodeStubWithSuggestion.STREAM_CODEC : ClientboundCommandsPacket.ArgumentNodeStubWithoutSuggestion.STREAM_CODEC;
         return (NodeStub)argumentCodec.decode(input);
      } else {
         return type == 1 ? (NodeStub)ClientboundCommandsPacket.LiteralNodeStub.STREAM_CODEC.decode(input) : null;
      }
   }

   private static <S> Entry createEntry(final CommandNode<? extends S> node, final NodeInspector<S> inspector, final Object2IntMap<? extends CommandNode<? extends S>> ids) {
      int flags = 0;
      int redirect;
      if (node.getRedirect() != null) {
         flags |= 8;
         redirect = ids.getInt(node.getRedirect());
      } else {
         redirect = 0;
      }

      if (inspector.isExecutable(node)) {
         flags |= 4;
      }

      if (inspector.isRestricted(node)) {
         flags |= 32;
      }

      Objects.requireNonNull(node);
      byte var7 = 0;
      NodeStub nodeStub;
      //$FF: var7->value
      //0->com/mojang/brigadier/tree/RootCommandNode
      //1->com/mojang/brigadier/tree/ArgumentCommandNode
      //2->com/mojang/brigadier/tree/LiteralCommandNode
      switch (node.typeSwitch<invokedynamic>(node, var7)) {
         case 0:
            RootCommandNode<? extends S> ignored = (RootCommandNode)node;
            flags |= 0;
            nodeStub = null;
            break;
         case 1:
            ArgumentCommandNode<? extends S, ?> arg = (ArgumentCommandNode)node;
            String id = arg.getName();
            SerializableArgument.BrigadierWrapper<?> type = SerializableArgument.unwrapOrThrow(arg.getType());
            flags |= 2;
            SuggestionProvider<? extends S> customSuggestions = arg.getCustomSuggestions();
            if (customSuggestions != null) {
               nodeStub = new ArgumentNodeStubWithSuggestion(id, type.argument(), CustomSuggestion.unwrapOrThrow(customSuggestions).suggestion().serializable());
               flags |= 16;
            } else {
               nodeStub = new ArgumentNodeStubWithoutSuggestion(id, type.argument());
            }
            break;
         case 2:
            LiteralCommandNode<? extends S> literal = (LiteralCommandNode)node;
            nodeStub = new LiteralNodeStub(literal.getLiteral());
            flags |= 1;
            break;
         default:
            throw new UnsupportedOperationException("Unknown node type " + String.valueOf(node));
      }

      Stream var10000 = node.getChildren().stream();
      Objects.requireNonNull(ids);
      int[] childrenIds = var10000.mapToInt(ids::getInt).toArray();
      return new Entry(nodeStub, flags, redirect, childrenIds);
   }

   public PacketType<ClientboundCommandsPacket> type() {
      return CommonPacketTypes.CLIENTBOUND_COMMANDS;
   }

   public void handle(final ClientCommonPacketListener listener) {
      listener.handleCommands(this);
   }

   public <S> RootCommandNode<S> getRoot(final NodeBuilder<S> builder) {
      return (RootCommandNode)(new NodeResolver<S>(builder, this.entries)).resolve(this.rootIndex);
   }

   static {
      RAW_STREAM_CODEC = StreamCodec.composite(ENTRY_STREAM_CODEC.apply(ByteBufCodecs.list()), (p) -> p.entries, ByteBufCodecs.VAR_INT, (p) -> p.rootIndex, ClientboundCommandsPacket::new);
      STREAM_CODEC = RAW_STREAM_CODEC.mapStream((s) -> new RegistryFriendlyByteBuf(s.unwrap(), RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)));
   }

   private static record LiteralNodeStub(String id) implements NodeStub {
      public static final StreamCodec<ByteBuf, LiteralNodeStub> STREAM_CODEC;

      private LiteralNodeStub {
         super();
      }

      public <S> ArgumentBuilder<S, ?> build(final NodeBuilder<S> builder) {
         return builder.createLiteral(this.id);
      }

      public void write(final RegistryFriendlyByteBuf output) {
         STREAM_CODEC.encode(output, this);
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, LiteralNodeStub::id, LiteralNodeStub::new);
      }
   }

   private static record ArgumentNodeStubWithoutSuggestion(String id, SerializableArgument<?> argument) implements NodeStub {
      public static final StreamCodec<RegistryFriendlyByteBuf, ArgumentNodeStubWithoutSuggestion> STREAM_CODEC;

      private ArgumentNodeStubWithoutSuggestion {
         super();
      }

      public <S> ArgumentBuilder<S, ?> build(final NodeBuilder<S> builder) {
         return builder.createArgument(this.id, this.argument.toBrigadier(), (CustomSuggestion)null);
      }

      public void write(final RegistryFriendlyByteBuf output) {
         STREAM_CODEC.encode(output, this);
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, ArgumentNodeStubWithoutSuggestion::id, SerializableArgumentTypes.STREAM_CODEC, ArgumentNodeStubWithoutSuggestion::argument, ArgumentNodeStubWithoutSuggestion::new);
      }
   }

   private static record ArgumentNodeStubWithSuggestion(String id, SerializableArgument<?> argument, SerializableCustomSuggestion customSuggestion) implements NodeStub {
      public static final StreamCodec<RegistryFriendlyByteBuf, ArgumentNodeStubWithSuggestion> STREAM_CODEC;

      private ArgumentNodeStubWithSuggestion {
         super();
      }

      public <S> ArgumentBuilder<S, ?> build(final NodeBuilder<S> builder) {
         return builder.createArgument(this.id, this.argument.toBrigadier(), this.customSuggestion);
      }

      public void write(final RegistryFriendlyByteBuf output) {
         STREAM_CODEC.encode(output, this);
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, ArgumentNodeStubWithSuggestion::id, SerializableArgumentTypes.STREAM_CODEC, ArgumentNodeStubWithSuggestion::argument, SerializableSuggestionTypes.STREAM_CODEC, ArgumentNodeStubWithSuggestion::customSuggestion, ArgumentNodeStubWithSuggestion::new);
      }
   }

   private static record Entry(@Nullable NodeStub stub, int flags, int redirect, int[] children) {
      private Entry {
         super();
      }

      public void write(final RegistryFriendlyByteBuf output) {
         output.writeByte(this.flags);
         output.writeVarIntArray(this.children);
         if ((this.flags & 8) != 0) {
            output.writeVarInt(this.redirect);
         }

         if (this.stub != null) {
            this.stub.write(output);
         }

      }

      public boolean canBuild(final IntSet unbuiltNodes) {
         if ((this.flags & 8) != 0) {
            return !unbuiltNodes.contains(this.redirect);
         } else {
            return true;
         }
      }

      public boolean canResolve(final IntSet unresolvedNodes) {
         for(int child : this.children) {
            if (unresolvedNodes.contains(child)) {
               return false;
            }
         }

         return true;
      }
   }

   private static class NodeResolver<S> {
      private final NodeBuilder<S> builder;
      private final List<Entry> entries;
      private final List<CommandNode<S>> nodes;

      private NodeResolver(final NodeBuilder<S> builder, final List<Entry> entries) {
         super();
         this.builder = builder;
         this.entries = entries;
         ObjectArrayList<CommandNode<S>> nodes = new ObjectArrayList();
         nodes.size(entries.size());
         this.nodes = nodes;
      }

      public CommandNode<S> resolve(final int index) {
         CommandNode<S> currentNode = (CommandNode)this.nodes.get(index);
         if (currentNode != null) {
            return currentNode;
         } else {
            Entry entry = (Entry)this.entries.get(index);
            CommandNode<S> result;
            if (entry.stub == null) {
               result = new RootCommandNode();
            } else {
               ArgumentBuilder<S, ?> resultBuilder = entry.stub.build(this.builder);
               if ((entry.flags & 8) != 0) {
                  resultBuilder.redirect(this.resolve(entry.redirect));
               }

               boolean isExecutable = (entry.flags & 4) != 0;
               boolean isRestricted = (entry.flags & 32) != 0;
               result = this.builder.configure(resultBuilder, isExecutable, isRestricted).build();
            }

            this.nodes.set(index, result);

            for(int childId : entry.children) {
               CommandNode<S> child = this.resolve(childId);
               if (!(child instanceof RootCommandNode)) {
                  result.addChild(child);
               }
            }

            return result;
         }
      }
   }

   public interface NodeBuilder<S> {
      ArgumentBuilder<S, ?> createLiteral(String id);

      ArgumentBuilder<S, ?> createArgument(String id, ArgumentType<?> argumentType, @Nullable CustomSuggestion customSuggestion);

      ArgumentBuilder<S, ?> configure(ArgumentBuilder<S, ?> input, boolean executable, boolean restricted);
   }

   public interface NodeInspector<S> {
      boolean isExecutable(CommandNode<? extends S> node);

      boolean isRestricted(CommandNode<? extends S> node);
   }

   private interface NodeStub {
      <S> ArgumentBuilder<S, ?> build(NodeBuilder<S> builder);

      void write(RegistryFriendlyByteBuf output);
   }
}

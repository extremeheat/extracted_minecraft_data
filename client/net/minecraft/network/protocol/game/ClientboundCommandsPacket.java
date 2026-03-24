package net.minecraft.network.protocol.game;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
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
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class ClientboundCommandsPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundCommandsPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientboundCommandsPacket>codec(ClientboundCommandsPacket::write, ClientboundCommandsPacket::new);
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

   public <S> ClientboundCommandsPacket(final RootCommandNode<S> root, final NodeInspector<S> inspector) {
      super();
      Object2IntMap<CommandNode<S>> nodeToId = enumerateNodes(root);
      this.entries = createEntries(nodeToId, inspector);
      this.rootIndex = nodeToId.getInt(root);
   }

   private ClientboundCommandsPacket(final FriendlyByteBuf input) {
      super();
      this.entries = input.<Entry>readList(ClientboundCommandsPacket::readNode);
      this.rootIndex = input.readVarInt();
      validateEntries(this.entries);
   }

   private void write(final FriendlyByteBuf output) {
      output.writeCollection(this.entries, (buffer, entry) -> entry.write(buffer));
      output.writeVarInt(this.rootIndex);
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

   private static <S> List<Entry> createEntries(final Object2IntMap<CommandNode<S>> nodeToId, final NodeInspector<S> inspector) {
      ObjectArrayList<Entry> result = new ObjectArrayList(nodeToId.size());
      result.size(nodeToId.size());
      ObjectIterator var3 = Object2IntMaps.fastIterable(nodeToId).iterator();

      while(var3.hasNext()) {
         Object2IntMap.Entry<CommandNode<S>> entry = (Object2IntMap.Entry)var3.next();
         result.set(entry.getIntValue(), createEntry((CommandNode)entry.getKey(), inspector, nodeToId));
      }

      return result;
   }

   private static Entry readNode(final FriendlyByteBuf input) {
      byte flags = input.readByte();
      int[] children = input.readVarIntArray();
      int redirect = (flags & 8) != 0 ? input.readVarInt() : 0;
      NodeStub stub = read(input, flags);
      return new Entry(stub, flags, redirect, children);
   }

   private static @Nullable NodeStub read(final FriendlyByteBuf input, final byte flags) {
      int type = flags & 3;
      if (type == 2) {
         String name = input.readUtf();
         int id = input.readVarInt();
         ArgumentTypeInfo<?, ?> argumentType = (ArgumentTypeInfo)BuiltInRegistries.COMMAND_ARGUMENT_TYPE.byId(id);
         if (argumentType == null) {
            return null;
         } else {
            ArgumentTypeInfo.Template<?> argument = argumentType.deserializeFromNetwork(input);
            Identifier suggestionId = (flags & 16) != 0 ? input.readIdentifier() : null;
            return new ArgumentNodeStub(name, argument, suggestionId);
         }
      } else if (type == 1) {
         String id = input.readUtf();
         return new LiteralNodeStub(id);
      } else {
         return null;
      }
   }

   private static <S> Entry createEntry(final CommandNode<S> node, final NodeInspector<S> inspector, final Object2IntMap<CommandNode<S>> ids) {
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
            RootCommandNode<S> ignored = (RootCommandNode)node;
            flags |= 0;
            nodeStub = null;
            break;
         case 1:
            ArgumentCommandNode<S, ?> arg = (ArgumentCommandNode)node;
            Identifier suggestionId = inspector.suggestionId(arg);
            nodeStub = new ArgumentNodeStub(arg.getName(), ArgumentTypeInfos.unpack(arg.getType()), suggestionId);
            flags |= 2;
            if (suggestionId != null) {
               flags |= 16;
            }
            break;
         case 2:
            LiteralCommandNode<S> literal = (LiteralCommandNode)node;
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
      return GamePacketTypes.CLIENTBOUND_COMMANDS;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleCommands(this);
   }

   public <S> RootCommandNode<S> getRoot(final CommandBuildContext context, final NodeBuilder<S> builder) {
      return (RootCommandNode)(new NodeResolver<S>(context, builder, this.entries)).resolve(this.rootIndex);
   }

   private static record LiteralNodeStub(String id) implements NodeStub {
      private LiteralNodeStub {
         super();
      }

      public <S> ArgumentBuilder<S, ?> build(final CommandBuildContext context, final NodeBuilder<S> builder) {
         return builder.createLiteral(this.id);
      }

      public void write(final FriendlyByteBuf output) {
         output.writeUtf(this.id);
      }
   }

   private static record ArgumentNodeStub(String id, ArgumentTypeInfo.Template<?> argumentType, @Nullable Identifier suggestionId) implements NodeStub {
      private ArgumentNodeStub {
         super();
      }

      public <S> ArgumentBuilder<S, ?> build(final CommandBuildContext context, final NodeBuilder<S> builder) {
         ArgumentType<?> type = this.argumentType.instantiate(context);
         return builder.createArgument(this.id, type, this.suggestionId);
      }

      public void write(final FriendlyByteBuf output) {
         output.writeUtf(this.id);
         serializeCap(output, this.argumentType);
         if (this.suggestionId != null) {
            output.writeIdentifier(this.suggestionId);
         }

      }

      private static <A extends ArgumentType<?>> void serializeCap(final FriendlyByteBuf output, final ArgumentTypeInfo.Template<A> argumentType) {
         serializeCap(output, argumentType.type(), argumentType);
      }

      private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void serializeCap(final FriendlyByteBuf output, final ArgumentTypeInfo<A, T> info, final ArgumentTypeInfo.Template<A> argumentType) {
         output.writeVarInt(BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getId(info));
         info.serializeToNetwork(argumentType, output);
      }
   }

   private static record Entry(@Nullable NodeStub stub, int flags, int redirect, int[] children) {
      private Entry {
         super();
      }

      public void write(final FriendlyByteBuf output) {
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
      private final CommandBuildContext context;
      private final NodeBuilder<S> builder;
      private final List<Entry> entries;
      private final List<CommandNode<S>> nodes;

      private NodeResolver(final CommandBuildContext context, final NodeBuilder<S> builder, final List<Entry> entries) {
         super();
         this.context = context;
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
               ArgumentBuilder<S, ?> resultBuilder = entry.stub.build(this.context, this.builder);
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

      ArgumentBuilder<S, ?> createArgument(String id, ArgumentType<?> argumentType, @Nullable Identifier suggestionId);

      ArgumentBuilder<S, ?> configure(ArgumentBuilder<S, ?> input, boolean executable, boolean restricted);
   }

   public interface NodeInspector<S> {
      @Nullable Identifier suggestionId(ArgumentCommandNode<S, ?> node);

      boolean isExecutable(CommandNode<S> node);

      boolean isRestricted(CommandNode<S> node);
   }

   private interface NodeStub {
      <S> ArgumentBuilder<S, ?> build(CommandBuildContext context, NodeBuilder<S> builder);

      void write(FriendlyByteBuf output);
   }
}

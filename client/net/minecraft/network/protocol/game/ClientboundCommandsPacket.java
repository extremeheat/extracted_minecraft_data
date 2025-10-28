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
import net.minecraft.resources.ResourceLocation;
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

   public <S> ClientboundCommandsPacket(RootCommandNode<S> var1, NodeInspector<S> var2) {
      super();
      Object2IntMap var3 = enumerateNodes(var1);
      this.entries = createEntries(var3, var2);
      this.rootIndex = var3.getInt(var1);
   }

   private ClientboundCommandsPacket(FriendlyByteBuf var1) {
      super();
      this.entries = var1.<Entry>readList(ClientboundCommandsPacket::readNode);
      this.rootIndex = var1.readVarInt();
      validateEntries(this.entries);
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeCollection(this.entries, (var0, var1x) -> var1x.write(var0));
      var1.writeVarInt(this.rootIndex);
   }

   private static void validateEntries(List<Entry> var0, BiPredicate<Entry, IntSet> var1) {
      IntOpenHashSet var2 = new IntOpenHashSet(IntSets.fromTo(0, var0.size()));

      while(!var2.isEmpty()) {
         boolean var3 = var2.removeIf((var3x) -> var1.test((Entry)var0.get(var3x), var2));
         if (!var3) {
            throw new IllegalStateException("Server sent an impossible command tree");
         }
      }

   }

   private static void validateEntries(List<Entry> var0) {
      validateEntries(var0, Entry::canBuild);
      validateEntries(var0, Entry::canResolve);
   }

   private static <S> Object2IntMap<CommandNode<S>> enumerateNodes(RootCommandNode<S> var0) {
      Object2IntOpenHashMap var1 = new Object2IntOpenHashMap();
      ArrayDeque var2 = new ArrayDeque();
      var2.add(var0);

      CommandNode var3;
      while((var3 = (CommandNode)var2.poll()) != null) {
         if (!var1.containsKey(var3)) {
            int var4 = var1.size();
            var1.put(var3, var4);
            var2.addAll(var3.getChildren());
            if (var3.getRedirect() != null) {
               var2.add(var3.getRedirect());
            }
         }
      }

      return var1;
   }

   private static <S> List<Entry> createEntries(Object2IntMap<CommandNode<S>> var0, NodeInspector<S> var1) {
      ObjectArrayList var2 = new ObjectArrayList(var0.size());
      var2.size(var0.size());
      ObjectIterator var3 = Object2IntMaps.fastIterable(var0).iterator();

      while(var3.hasNext()) {
         Object2IntMap.Entry var4 = (Object2IntMap.Entry)var3.next();
         var2.set(var4.getIntValue(), createEntry((CommandNode)var4.getKey(), var1, var0));
      }

      return var2;
   }

   private static Entry readNode(FriendlyByteBuf var0) {
      byte var1 = var0.readByte();
      int[] var2 = var0.readVarIntArray();
      int var3 = (var1 & 8) != 0 ? var0.readVarInt() : 0;
      NodeStub var4 = read(var0, var1);
      return new Entry(var4, var1, var3, var2);
   }

   private static @Nullable NodeStub read(FriendlyByteBuf var0, byte var1) {
      int var2 = var1 & 3;
      if (var2 == 2) {
         String var8 = var0.readUtf();
         int var4 = var0.readVarInt();
         ArgumentTypeInfo var5 = (ArgumentTypeInfo)BuiltInRegistries.COMMAND_ARGUMENT_TYPE.byId(var4);
         if (var5 == null) {
            return null;
         } else {
            ArgumentTypeInfo.Template var6 = var5.deserializeFromNetwork(var0);
            ResourceLocation var7 = (var1 & 16) != 0 ? var0.readResourceLocation() : null;
            return new ArgumentNodeStub(var8, var6, var7);
         }
      } else if (var2 == 1) {
         String var3 = var0.readUtf();
         return new LiteralNodeStub(var3);
      } else {
         return null;
      }
   }

   private static <S> Entry createEntry(CommandNode<S> var0, NodeInspector<S> var1, Object2IntMap<CommandNode<S>> var2) {
      int var3 = 0;
      int var4;
      if (var0.getRedirect() != null) {
         var3 |= 8;
         var4 = var2.getInt(var0.getRedirect());
      } else {
         var4 = 0;
      }

      if (var1.isExecutable(var0)) {
         var3 |= 4;
      }

      if (var1.isRestricted(var0)) {
         var3 |= 32;
      }

      Objects.requireNonNull(var0);
      byte var7 = 0;
      Object var5;
      //$FF: var7->value
      //0->com/mojang/brigadier/tree/RootCommandNode
      //1->com/mojang/brigadier/tree/ArgumentCommandNode
      //2->com/mojang/brigadier/tree/LiteralCommandNode
      switch (var0.typeSwitch<invokedynamic>(var0, var7)) {
         case 0:
            RootCommandNode var8 = (RootCommandNode)var0;
            var3 |= 0;
            var5 = null;
            break;
         case 1:
            ArgumentCommandNode var9 = (ArgumentCommandNode)var0;
            ResourceLocation var12 = var1.suggestionId(var9);
            var5 = new ArgumentNodeStub(var9.getName(), ArgumentTypeInfos.unpack(var9.getType()), var12);
            var3 |= 2;
            if (var12 != null) {
               var3 |= 16;
            }
            break;
         case 2:
            LiteralCommandNode var10 = (LiteralCommandNode)var0;
            var5 = new LiteralNodeStub(var10.getLiteral());
            var3 |= 1;
            break;
         default:
            throw new UnsupportedOperationException("Unknown node type " + String.valueOf(var0));
      }

      Stream var10000 = var0.getChildren().stream();
      Objects.requireNonNull(var2);
      int[] var6 = var10000.mapToInt(var2::getInt).toArray();
      return new Entry((NodeStub)var5, var3, var4, var6);
   }

   public PacketType<ClientboundCommandsPacket> type() {
      return GamePacketTypes.CLIENTBOUND_COMMANDS;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleCommands(this);
   }

   public <S> RootCommandNode<S> getRoot(CommandBuildContext var1, NodeBuilder<S> var2) {
      return (RootCommandNode)(new NodeResolver<S>(var1, var2, this.entries)).resolve(this.rootIndex);
   }

   static record LiteralNodeStub(String id) implements NodeStub {
      LiteralNodeStub(String var1) {
         super();
         this.id = var1;
      }

      public <S> ArgumentBuilder<S, ?> build(CommandBuildContext var1, NodeBuilder<S> var2) {
         return var2.createLiteral(this.id);
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeUtf(this.id);
      }
   }

   static record ArgumentNodeStub(String id, ArgumentTypeInfo.Template<?> argumentType, @Nullable ResourceLocation suggestionId) implements NodeStub {
      ArgumentNodeStub(String var1, ArgumentTypeInfo.Template<?> var2, @Nullable ResourceLocation var3) {
         super();
         this.id = var1;
         this.argumentType = var2;
         this.suggestionId = var3;
      }

      public <S> ArgumentBuilder<S, ?> build(CommandBuildContext var1, NodeBuilder<S> var2) {
         ArgumentType var3 = this.argumentType.instantiate(var1);
         return var2.createArgument(this.id, var3, this.suggestionId);
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeUtf(this.id);
         serializeCap(var1, this.argumentType);
         if (this.suggestionId != null) {
            var1.writeResourceLocation(this.suggestionId);
         }

      }

      private static <A extends ArgumentType<?>> void serializeCap(FriendlyByteBuf var0, ArgumentTypeInfo.Template<A> var1) {
         serializeCap(var0, var1.type(), var1);
      }

      private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void serializeCap(FriendlyByteBuf var0, ArgumentTypeInfo<A, T> var1, ArgumentTypeInfo.Template<A> var2) {
         var0.writeVarInt(BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getId(var1));
         var1.serializeToNetwork(var2, var0);
      }
   }

   static record Entry(@Nullable NodeStub stub, int flags, int redirect, int[] children) {
      final @Nullable NodeStub stub;
      final int flags;
      final int redirect;
      final int[] children;

      Entry(@Nullable NodeStub var1, int var2, int var3, int[] var4) {
         super();
         this.stub = var1;
         this.flags = var2;
         this.redirect = var3;
         this.children = var4;
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeByte(this.flags);
         var1.writeVarIntArray(this.children);
         if ((this.flags & 8) != 0) {
            var1.writeVarInt(this.redirect);
         }

         if (this.stub != null) {
            this.stub.write(var1);
         }

      }

      public boolean canBuild(IntSet var1) {
         if ((this.flags & 8) != 0) {
            return !var1.contains(this.redirect);
         } else {
            return true;
         }
      }

      public boolean canResolve(IntSet var1) {
         for(int var5 : this.children) {
            if (var1.contains(var5)) {
               return false;
            }
         }

         return true;
      }
   }

   static class NodeResolver<S> {
      private final CommandBuildContext context;
      private final NodeBuilder<S> builder;
      private final List<Entry> entries;
      private final List<CommandNode<S>> nodes;

      NodeResolver(CommandBuildContext var1, NodeBuilder<S> var2, List<Entry> var3) {
         super();
         this.context = var1;
         this.builder = var2;
         this.entries = var3;
         ObjectArrayList var4 = new ObjectArrayList();
         var4.size(var3.size());
         this.nodes = var4;
      }

      public CommandNode<S> resolve(int var1) {
         CommandNode var2 = (CommandNode)this.nodes.get(var1);
         if (var2 != null) {
            return var2;
         } else {
            Entry var3 = (Entry)this.entries.get(var1);
            Object var4;
            if (var3.stub == null) {
               var4 = new RootCommandNode();
            } else {
               ArgumentBuilder var5 = var3.stub.build(this.context, this.builder);
               if ((var3.flags & 8) != 0) {
                  var5.redirect(this.resolve(var3.redirect));
               }

               boolean var6 = (var3.flags & 4) != 0;
               boolean var7 = (var3.flags & 32) != 0;
               var4 = this.builder.configure(var5, var6, var7).build();
            }

            this.nodes.set(var1, var4);

            for(int var8 : var3.children) {
               CommandNode var9 = this.resolve(var8);
               if (!(var9 instanceof RootCommandNode)) {
                  ((CommandNode)var4).addChild(var9);
               }
            }

            return (CommandNode<S>)var4;
         }
      }
   }

   public interface NodeBuilder<S> {
      ArgumentBuilder<S, ?> createLiteral(String var1);

      ArgumentBuilder<S, ?> createArgument(String var1, ArgumentType<?> var2, @Nullable ResourceLocation var3);

      ArgumentBuilder<S, ?> configure(ArgumentBuilder<S, ?> var1, boolean var2, boolean var3);
   }

   public interface NodeInspector<S> {
      @Nullable ResourceLocation suggestionId(ArgumentCommandNode<S, ?> var1);

      boolean isExecutable(CommandNode<S> var1);

      boolean isRestricted(CommandNode<S> var1);
   }

   interface NodeStub {
      <S> ArgumentBuilder<S, ?> build(CommandBuildContext var1, NodeBuilder<S> var2);

      void write(FriendlyByteBuf var1);
   }
}

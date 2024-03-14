package net.minecraft.network.protocol.game;

import com.google.common.collect.Queues;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
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
import java.util.function.BiPredicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;

public class ClientboundCommandsPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundCommandsPacket> STREAM_CODEC = Packet.codec(
      ClientboundCommandsPacket::write, ClientboundCommandsPacket::new
   );
   private static final byte MASK_TYPE = 3;
   private static final byte FLAG_EXECUTABLE = 4;
   private static final byte FLAG_REDIRECT = 8;
   private static final byte FLAG_CUSTOM_SUGGESTIONS = 16;
   private static final byte TYPE_ROOT = 0;
   private static final byte TYPE_LITERAL = 1;
   private static final byte TYPE_ARGUMENT = 2;
   private final int rootIndex;
   private final List<ClientboundCommandsPacket.Entry> entries;

   public ClientboundCommandsPacket(RootCommandNode<SharedSuggestionProvider> var1) {
      super();
      Object2IntMap var2 = enumerateNodes(var1);
      this.entries = createEntries(var2);
      this.rootIndex = var2.getInt(var1);
   }

   private ClientboundCommandsPacket(FriendlyByteBuf var1) {
      super();
      this.entries = var1.readList(ClientboundCommandsPacket::readNode);
      this.rootIndex = var1.readVarInt();
      validateEntries(this.entries);
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeCollection(this.entries, (var0, var1x) -> var1x.write(var0));
      var1.writeVarInt(this.rootIndex);
   }

   private static void validateEntries(List<ClientboundCommandsPacket.Entry> var0, BiPredicate<ClientboundCommandsPacket.Entry, IntSet> var1) {
      IntOpenHashSet var2 = new IntOpenHashSet(IntSets.fromTo(0, var0.size()));

      while(!var2.isEmpty()) {
         boolean var3 = var2.removeIf(var3x -> var1.test((ClientboundCommandsPacket.Entry)var0.get(var3x), var2));
         if (!var3) {
            throw new IllegalStateException("Server sent an impossible command tree");
         }
      }
   }

   private static void validateEntries(List<ClientboundCommandsPacket.Entry> var0) {
      validateEntries(var0, ClientboundCommandsPacket.Entry::canBuild);
      validateEntries(var0, ClientboundCommandsPacket.Entry::canResolve);
   }

   private static Object2IntMap<CommandNode<SharedSuggestionProvider>> enumerateNodes(RootCommandNode<SharedSuggestionProvider> var0) {
      Object2IntOpenHashMap var1 = new Object2IntOpenHashMap();
      ArrayDeque var2 = Queues.newArrayDeque();
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

   private static List<ClientboundCommandsPacket.Entry> createEntries(Object2IntMap<CommandNode<SharedSuggestionProvider>> var0) {
      ObjectArrayList var1 = new ObjectArrayList(var0.size());
      var1.size(var0.size());
      ObjectIterator var2 = Object2IntMaps.fastIterable(var0).iterator();

      while(var2.hasNext()) {
         it.unimi.dsi.fastutil.objects.Object2IntMap.Entry var3 = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry)var2.next();
         var1.set(var3.getIntValue(), createEntry((CommandNode<SharedSuggestionProvider>)var3.getKey(), var0));
      }

      return var1;
   }

   private static ClientboundCommandsPacket.Entry readNode(FriendlyByteBuf var0) {
      byte var1 = var0.readByte();
      int[] var2 = var0.readVarIntArray();
      int var3 = (var1 & 8) != 0 ? var0.readVarInt() : 0;
      ClientboundCommandsPacket.NodeStub var4 = read(var0, var1);
      return new ClientboundCommandsPacket.Entry(var4, var1, var3, var2);
   }

   @Nullable
   private static ClientboundCommandsPacket.NodeStub read(FriendlyByteBuf var0, byte var1) {
      int var2 = var1 & 3;
      if (var2 == 2) {
         String var8 = var0.readUtf();
         int var4 = var0.readVarInt();
         ArgumentTypeInfo var5 = BuiltInRegistries.COMMAND_ARGUMENT_TYPE.byId(var4);
         if (var5 == null) {
            return null;
         } else {
            ArgumentTypeInfo.Template var6 = var5.deserializeFromNetwork(var0);
            ResourceLocation var7 = (var1 & 16) != 0 ? var0.readResourceLocation() : null;
            return new ClientboundCommandsPacket.ArgumentNodeStub(var8, var6, var7);
         }
      } else if (var2 == 1) {
         String var3 = var0.readUtf();
         return new ClientboundCommandsPacket.LiteralNodeStub(var3);
      } else {
         return null;
      }
   }

   private static ClientboundCommandsPacket.Entry createEntry(
      CommandNode<SharedSuggestionProvider> var0, Object2IntMap<CommandNode<SharedSuggestionProvider>> var1
   ) {
      int var2 = 0;
      int var3;
      if (var0.getRedirect() != null) {
         var2 |= 8;
         var3 = var1.getInt(var0.getRedirect());
      } else {
         var3 = 0;
      }

      if (var0.getCommand() != null) {
         var2 |= 4;
      }

      Object var4;
      if (var0 instanceof RootCommandNode) {
         var2 |= 0;
         var4 = null;
      } else if (var0 instanceof ArgumentCommandNode var6) {
         var4 = new ClientboundCommandsPacket.ArgumentNodeStub((ArgumentCommandNode<SharedSuggestionProvider, ?>)var6);
         var2 |= 2;
         if (var6.getCustomSuggestions() != null) {
            var2 |= 16;
         }
      } else {
         if (!(var0 instanceof LiteralCommandNode)) {
            throw new UnsupportedOperationException("Unknown node type " + var0);
         }

         LiteralCommandNode var5 = (LiteralCommandNode)var0;
         var4 = new ClientboundCommandsPacket.LiteralNodeStub(var5.getLiteral());
         var2 |= 1;
      }

      int[] var8 = var0.getChildren().stream().mapToInt(var1::getInt).toArray();
      return new ClientboundCommandsPacket.Entry((ClientboundCommandsPacket.NodeStub)var4, var2, var3, var8);
   }

   @Override
   public PacketType<ClientboundCommandsPacket> type() {
      return GamePacketTypes.CLIENTBOUND_COMMANDS;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleCommands(this);
   }

   public RootCommandNode<SharedSuggestionProvider> getRoot(CommandBuildContext var1) {
      return (RootCommandNode<SharedSuggestionProvider>)new ClientboundCommandsPacket.NodeResolver(var1, this.entries).resolve(this.rootIndex);
   }

   static class ArgumentNodeStub implements ClientboundCommandsPacket.NodeStub {
      private final String id;
      private final ArgumentTypeInfo.Template<?> argumentType;
      @Nullable
      private final ResourceLocation suggestionId;

      @Nullable
      private static ResourceLocation getSuggestionId(@Nullable SuggestionProvider<SharedSuggestionProvider> var0) {
         return var0 != null ? SuggestionProviders.getName(var0) : null;
      }

      ArgumentNodeStub(String var1, ArgumentTypeInfo.Template<?> var2, @Nullable ResourceLocation var3) {
         super();
         this.id = var1;
         this.argumentType = var2;
         this.suggestionId = var3;
      }

      public ArgumentNodeStub(ArgumentCommandNode<SharedSuggestionProvider, ?> var1) {
         this(var1.getName(), ArgumentTypeInfos.unpack(var1.getType()), getSuggestionId(var1.getCustomSuggestions()));
      }

      @Override
      public ArgumentBuilder<SharedSuggestionProvider, ?> build(CommandBuildContext var1) {
         ArgumentType var2 = this.argumentType.instantiate(var1);
         RequiredArgumentBuilder var3 = RequiredArgumentBuilder.argument(this.id, var2);
         if (this.suggestionId != null) {
            var3.suggests(SuggestionProviders.getProvider(this.suggestionId));
         }

         return var3;
      }

      @Override
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

      private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void serializeCap(
         FriendlyByteBuf var0, ArgumentTypeInfo<A, T> var1, ArgumentTypeInfo.Template<A> var2
      ) {
         var0.writeVarInt(BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getId(var1));
         var1.serializeToNetwork((T)var2, var0);
      }
   }

   static class Entry {
      @Nullable
      final ClientboundCommandsPacket.NodeStub stub;
      final int flags;
      final int redirect;
      final int[] children;

      Entry(@Nullable ClientboundCommandsPacket.NodeStub var1, int var2, int var3, int[] var4) {
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

   static class LiteralNodeStub implements ClientboundCommandsPacket.NodeStub {
      private final String id;

      LiteralNodeStub(String var1) {
         super();
         this.id = var1;
      }

      @Override
      public ArgumentBuilder<SharedSuggestionProvider, ?> build(CommandBuildContext var1) {
         return LiteralArgumentBuilder.literal(this.id);
      }

      @Override
      public void write(FriendlyByteBuf var1) {
         var1.writeUtf(this.id);
      }
   }

   static class NodeResolver {
      private final CommandBuildContext context;
      private final List<ClientboundCommandsPacket.Entry> entries;
      private final List<CommandNode<SharedSuggestionProvider>> nodes;

      NodeResolver(CommandBuildContext var1, List<ClientboundCommandsPacket.Entry> var2) {
         super();
         this.context = var1;
         this.entries = var2;
         ObjectArrayList var3 = new ObjectArrayList();
         var3.size(var2.size());
         this.nodes = var3;
      }

      public CommandNode<SharedSuggestionProvider> resolve(int var1) {
         CommandNode var2 = (CommandNode)this.nodes.get(var1);
         if (var2 != null) {
            return var2;
         } else {
            ClientboundCommandsPacket.Entry var3 = this.entries.get(var1);
            Object var4;
            if (var3.stub == null) {
               var4 = new RootCommandNode();
            } else {
               ArgumentBuilder var5 = var3.stub.build(this.context);
               if ((var3.flags & 8) != 0) {
                  var5.redirect(this.resolve(var3.redirect));
               }

               if ((var3.flags & 4) != 0) {
                  var5.executes(var0 -> 0);
               }

               var4 = var5.build();
            }

            this.nodes.set(var1, var4);

            for(int var8 : var3.children) {
               CommandNode var9 = this.resolve(var8);
               if (!(var9 instanceof RootCommandNode)) {
                  var4.addChild(var9);
               }
            }

            return (CommandNode<SharedSuggestionProvider>)var4;
         }
      }
   }

   interface NodeStub {
      ArgumentBuilder<SharedSuggestionProvider, ?> build(CommandBuildContext var1);

      void write(FriendlyByteBuf var1);
   }
}

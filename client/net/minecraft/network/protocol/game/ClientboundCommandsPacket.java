package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundCommandsPacket implements Packet<ClientGamePacketListener> {
   private RootCommandNode<SharedSuggestionProvider> root;

   public ClientboundCommandsPacket() {
      super();
   }

   public ClientboundCommandsPacket(RootCommandNode<SharedSuggestionProvider> var1) {
      super();
      this.root = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      ClientboundCommandsPacket.Entry[] var2 = new ClientboundCommandsPacket.Entry[var1.readVarInt()];

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var2[var3] = readNode(var1);
      }

      resolveEntries(var2);
      this.root = (RootCommandNode)var2[var1.readVarInt()].node;
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      Object2IntMap var2 = enumerateNodes(this.root);
      CommandNode[] var3 = getNodesInIdOrder(var2);
      var1.writeVarInt(var3.length);
      CommandNode[] var4 = var3;
      int var5 = var3.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         CommandNode var7 = var4[var6];
         writeNode(var1, var7, var2);
      }

      var1.writeVarInt(var2.get(this.root));
   }

   private static void resolveEntries(ClientboundCommandsPacket.Entry[] var0) {
      ArrayList var1 = Lists.newArrayList(var0);

      boolean var2;
      do {
         if (var1.isEmpty()) {
            return;
         }

         var2 = var1.removeIf((var1x) -> {
            return var1x.build(var0);
         });
      } while(var2);

      throw new IllegalStateException("Server sent an impossible command tree");
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

   private static CommandNode<SharedSuggestionProvider>[] getNodesInIdOrder(Object2IntMap<CommandNode<SharedSuggestionProvider>> var0) {
      CommandNode[] var1 = (CommandNode[])(new CommandNode[var0.size()]);

      it.unimi.dsi.fastutil.objects.Object2IntMap.Entry var3;
      for(ObjectIterator var2 = Object2IntMaps.fastIterable(var0).iterator(); var2.hasNext(); var1[var3.getIntValue()] = (CommandNode)var3.getKey()) {
         var3 = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry)var2.next();
      }

      return var1;
   }

   private static ClientboundCommandsPacket.Entry readNode(FriendlyByteBuf var0) {
      byte var1 = var0.readByte();
      int[] var2 = var0.readVarIntArray();
      int var3 = (var1 & 8) != 0 ? var0.readVarInt() : 0;
      ArgumentBuilder var4 = createBuilder(var0, var1);
      return new ClientboundCommandsPacket.Entry(var4, var1, var3, var2);
   }

   @Nullable
   private static ArgumentBuilder<SharedSuggestionProvider, ?> createBuilder(FriendlyByteBuf var0, byte var1) {
      int var2 = var1 & 3;
      if (var2 == 2) {
         String var3 = var0.readUtf(32767);
         ArgumentType var4 = ArgumentTypes.deserialize(var0);
         if (var4 == null) {
            return null;
         } else {
            RequiredArgumentBuilder var5 = RequiredArgumentBuilder.argument(var3, var4);
            if ((var1 & 16) != 0) {
               var5.suggests(SuggestionProviders.getProvider(var0.readResourceLocation()));
            }

            return var5;
         }
      } else {
         return var2 == 1 ? LiteralArgumentBuilder.literal(var0.readUtf(32767)) : null;
      }
   }

   private static void writeNode(FriendlyByteBuf var0, CommandNode<SharedSuggestionProvider> var1, Map<CommandNode<SharedSuggestionProvider>, Integer> var2) {
      byte var3 = 0;
      if (var1.getRedirect() != null) {
         var3 = (byte)(var3 | 8);
      }

      if (var1.getCommand() != null) {
         var3 = (byte)(var3 | 4);
      }

      if (var1 instanceof RootCommandNode) {
         var3 = (byte)(var3 | 0);
      } else if (var1 instanceof ArgumentCommandNode) {
         var3 = (byte)(var3 | 2);
         if (((ArgumentCommandNode)var1).getCustomSuggestions() != null) {
            var3 = (byte)(var3 | 16);
         }
      } else {
         if (!(var1 instanceof LiteralCommandNode)) {
            throw new UnsupportedOperationException("Unknown node type " + var1);
         }

         var3 = (byte)(var3 | 1);
      }

      var0.writeByte(var3);
      var0.writeVarInt(var1.getChildren().size());
      Iterator var4 = var1.getChildren().iterator();

      while(var4.hasNext()) {
         CommandNode var5 = (CommandNode)var4.next();
         var0.writeVarInt((Integer)var2.get(var5));
      }

      if (var1.getRedirect() != null) {
         var0.writeVarInt((Integer)var2.get(var1.getRedirect()));
      }

      if (var1 instanceof ArgumentCommandNode) {
         ArgumentCommandNode var6 = (ArgumentCommandNode)var1;
         var0.writeUtf(var6.getName());
         ArgumentTypes.serialize(var0, var6.getType());
         if (var6.getCustomSuggestions() != null) {
            var0.writeResourceLocation(SuggestionProviders.getName(var6.getCustomSuggestions()));
         }
      } else if (var1 instanceof LiteralCommandNode) {
         var0.writeUtf(((LiteralCommandNode)var1).getLiteral());
      }

   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleCommands(this);
   }

   public RootCommandNode<SharedSuggestionProvider> getRoot() {
      return this.root;
   }

   static class Entry {
      @Nullable
      private final ArgumentBuilder<SharedSuggestionProvider, ?> builder;
      private final byte flags;
      private final int redirect;
      private final int[] children;
      @Nullable
      private CommandNode<SharedSuggestionProvider> node;

      private Entry(@Nullable ArgumentBuilder<SharedSuggestionProvider, ?> var1, byte var2, int var3, int[] var4) {
         super();
         this.builder = var1;
         this.flags = var2;
         this.redirect = var3;
         this.children = var4;
      }

      public boolean build(ClientboundCommandsPacket.Entry[] var1) {
         if (this.node == null) {
            if (this.builder == null) {
               this.node = new RootCommandNode();
            } else {
               if ((this.flags & 8) != 0) {
                  if (var1[this.redirect].node == null) {
                     return false;
                  }

                  this.builder.redirect(var1[this.redirect].node);
               }

               if ((this.flags & 4) != 0) {
                  this.builder.executes((var0) -> {
                     return 0;
                  });
               }

               this.node = this.builder.build();
            }
         }

         int[] var2 = this.children;
         int var3 = var2.length;

         int var4;
         int var5;
         for(var4 = 0; var4 < var3; ++var4) {
            var5 = var2[var4];
            if (var1[var5].node == null) {
               return false;
            }
         }

         var2 = this.children;
         var3 = var2.length;

         for(var4 = 0; var4 < var3; ++var4) {
            var5 = var2[var4];
            CommandNode var6 = var1[var5].node;
            if (!(var6 instanceof RootCommandNode)) {
               this.node.addChild(var6);
            }
         }

         return true;
      }

      // $FF: synthetic method
      Entry(ArgumentBuilder var1, byte var2, int var3, int[] var4, Object var5) {
         this(var1, var2, var3, var4);
      }
   }
}

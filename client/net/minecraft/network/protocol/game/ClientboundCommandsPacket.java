package net.minecraft.network.protocol.game;

import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
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
      ArrayDeque var3 = new ArrayDeque(var2.length);

      for(int var4 = 0; var4 < var2.length; ++var4) {
         var2[var4] = this.readNode(var1);
         var3.add(var2[var4]);
      }

      boolean var7;
      do {
         if (var3.isEmpty()) {
            this.root = (RootCommandNode)var2[var1.readVarInt()].node;
            return;
         }

         var7 = false;
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            ClientboundCommandsPacket.Entry var6 = (ClientboundCommandsPacket.Entry)var5.next();
            if (var6.build(var2)) {
               var5.remove();
               var7 = true;
            }
         }
      } while(var7);

      throw new IllegalStateException("Server sent an impossible command tree");
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      HashMap var2 = Maps.newHashMap();
      ArrayDeque var3 = new ArrayDeque();
      var3.add(this.root);

      while(!var3.isEmpty()) {
         CommandNode var4 = (CommandNode)var3.pollFirst();
         if (!var2.containsKey(var4)) {
            int var5 = var2.size();
            var2.put(var4, var5);
            var3.addAll(var4.getChildren());
            if (var4.getRedirect() != null) {
               var3.add(var4.getRedirect());
            }
         }
      }

      CommandNode[] var9 = (CommandNode[])(new CommandNode[var2.size()]);

      java.util.Map.Entry var6;
      for(Iterator var10 = var2.entrySet().iterator(); var10.hasNext(); var9[(Integer)var6.getValue()] = (CommandNode)var6.getKey()) {
         var6 = (java.util.Map.Entry)var10.next();
      }

      var1.writeVarInt(var9.length);
      CommandNode[] var11 = var9;
      int var12 = var9.length;

      for(int var7 = 0; var7 < var12; ++var7) {
         CommandNode var8 = var11[var7];
         this.writeNode(var1, var8, var2);
      }

      var1.writeVarInt((Integer)var2.get(this.root));
   }

   private ClientboundCommandsPacket.Entry readNode(FriendlyByteBuf var1) {
      byte var2 = var1.readByte();
      int[] var3 = var1.readVarIntArray();
      int var4 = (var2 & 8) != 0 ? var1.readVarInt() : 0;
      ArgumentBuilder var5 = this.createBuilder(var1, var2);
      return new ClientboundCommandsPacket.Entry(var5, var2, var4, var3);
   }

   @Nullable
   private ArgumentBuilder<SharedSuggestionProvider, ?> createBuilder(FriendlyByteBuf var1, byte var2) {
      int var3 = var2 & 3;
      if (var3 == 2) {
         String var4 = var1.readUtf(32767);
         ArgumentType var5 = ArgumentTypes.deserialize(var1);
         if (var5 == null) {
            return null;
         } else {
            RequiredArgumentBuilder var6 = RequiredArgumentBuilder.argument(var4, var5);
            if ((var2 & 16) != 0) {
               var6.suggests(SuggestionProviders.getProvider(var1.readResourceLocation()));
            }

            return var6;
         }
      } else {
         return var3 == 1 ? LiteralArgumentBuilder.literal(var1.readUtf(32767)) : null;
      }
   }

   private void writeNode(FriendlyByteBuf var1, CommandNode<SharedSuggestionProvider> var2, Map<CommandNode<SharedSuggestionProvider>, Integer> var3) {
      byte var4 = 0;
      if (var2.getRedirect() != null) {
         var4 = (byte)(var4 | 8);
      }

      if (var2.getCommand() != null) {
         var4 = (byte)(var4 | 4);
      }

      if (var2 instanceof RootCommandNode) {
         var4 = (byte)(var4 | 0);
      } else if (var2 instanceof ArgumentCommandNode) {
         var4 = (byte)(var4 | 2);
         if (((ArgumentCommandNode)var2).getCustomSuggestions() != null) {
            var4 = (byte)(var4 | 16);
         }
      } else {
         if (!(var2 instanceof LiteralCommandNode)) {
            throw new UnsupportedOperationException("Unknown node type " + var2);
         }

         var4 = (byte)(var4 | 1);
      }

      var1.writeByte(var4);
      var1.writeVarInt(var2.getChildren().size());
      Iterator var5 = var2.getChildren().iterator();

      while(var5.hasNext()) {
         CommandNode var6 = (CommandNode)var5.next();
         var1.writeVarInt((Integer)var3.get(var6));
      }

      if (var2.getRedirect() != null) {
         var1.writeVarInt((Integer)var3.get(var2.getRedirect()));
      }

      if (var2 instanceof ArgumentCommandNode) {
         ArgumentCommandNode var7 = (ArgumentCommandNode)var2;
         var1.writeUtf(var7.getName());
         ArgumentTypes.serialize(var1, var7.getType());
         if (var7.getCustomSuggestions() != null) {
            var1.writeResourceLocation(SuggestionProviders.getName(var7.getCustomSuggestions()));
         }
      } else if (var2 instanceof LiteralCommandNode) {
         var1.writeUtf(((LiteralCommandNode)var2).getLiteral());
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

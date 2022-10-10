package net.minecraft.network.play.server;

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
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketCommandList implements Packet<INetHandlerPlayClient> {
   private RootCommandNode<ISuggestionProvider> field_197697_a;

   public SPacketCommandList() {
      super();
   }

   public SPacketCommandList(RootCommandNode<ISuggestionProvider> var1) {
      super();
      this.field_197697_a = var1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      SPacketCommandList.Entry[] var2 = new SPacketCommandList.Entry[var1.func_150792_a()];
      ArrayDeque var3 = new ArrayDeque(var2.length);

      for(int var4 = 0; var4 < var2.length; ++var4) {
         var2[var4] = this.func_197692_c(var1);
         var3.add(var2[var4]);
      }

      boolean var7;
      do {
         if (var3.isEmpty()) {
            this.field_197697_a = (RootCommandNode)var2[var1.func_150792_a()].field_197730_e;
            return;
         }

         var7 = false;
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            SPacketCommandList.Entry var6 = (SPacketCommandList.Entry)var5.next();
            if (var6.func_197723_a(var2)) {
               var5.remove();
               var7 = true;
            }
         }
      } while(var7);

      throw new IllegalStateException("Server sent an impossible command tree");
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      HashMap var2 = Maps.newHashMap();
      ArrayDeque var3 = new ArrayDeque();
      var3.add(this.field_197697_a);

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

      var1.func_150787_b(var9.length);
      CommandNode[] var11 = var9;
      int var12 = var9.length;

      for(int var7 = 0; var7 < var12; ++var7) {
         CommandNode var8 = var11[var7];
         this.func_197696_a(var1, var8, var2);
      }

      var1.func_150787_b((Integer)var2.get(this.field_197697_a));
   }

   private SPacketCommandList.Entry func_197692_c(PacketBuffer var1) {
      byte var2 = var1.readByte();
      int[] var3 = var1.func_186863_b();
      int var4 = (var2 & 8) != 0 ? var1.func_150792_a() : 0;
      ArgumentBuilder var5 = this.func_197695_a(var1, var2);
      return new SPacketCommandList.Entry(var5, var2, var4, var3);
   }

   @Nullable
   private ArgumentBuilder<ISuggestionProvider, ?> func_197695_a(PacketBuffer var1, byte var2) {
      int var3 = var2 & 3;
      if (var3 == 2) {
         String var4 = var1.func_150789_c(32767);
         ArgumentType var5 = ArgumentTypes.func_197486_a(var1);
         if (var5 == null) {
            return null;
         } else {
            RequiredArgumentBuilder var6 = RequiredArgumentBuilder.argument(var4, var5);
            if ((var2 & 16) != 0) {
               var6.suggests(SuggestionProviders.func_197498_a(var1.func_192575_l()));
            }

            return var6;
         }
      } else {
         return var3 == 1 ? LiteralArgumentBuilder.literal(var1.func_150789_c(32767)) : null;
      }
   }

   private void func_197696_a(PacketBuffer var1, CommandNode<ISuggestionProvider> var2, Map<CommandNode<ISuggestionProvider>, Integer> var3) {
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
      var1.func_150787_b(var2.getChildren().size());
      Iterator var5 = var2.getChildren().iterator();

      while(var5.hasNext()) {
         CommandNode var6 = (CommandNode)var5.next();
         var1.func_150787_b((Integer)var3.get(var6));
      }

      if (var2.getRedirect() != null) {
         var1.func_150787_b((Integer)var3.get(var2.getRedirect()));
      }

      if (var2 instanceof ArgumentCommandNode) {
         ArgumentCommandNode var7 = (ArgumentCommandNode)var2;
         var1.func_180714_a(var7.getName());
         ArgumentTypes.func_197484_a(var1, var7.getType());
         if (var7.getCustomSuggestions() != null) {
            var1.func_192572_a(SuggestionProviders.func_197497_a(var7.getCustomSuggestions()));
         }
      } else if (var2 instanceof LiteralCommandNode) {
         var1.func_180714_a(((LiteralCommandNode)var2).getLiteral());
      }

   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_195511_a(this);
   }

   public RootCommandNode<ISuggestionProvider> func_197693_a() {
      return this.field_197697_a;
   }

   static class Entry {
      @Nullable
      private final ArgumentBuilder<ISuggestionProvider, ?> field_197726_a;
      private final byte field_197727_b;
      private final int field_197728_c;
      private final int[] field_197729_d;
      private CommandNode<ISuggestionProvider> field_197730_e;

      private Entry(@Nullable ArgumentBuilder<ISuggestionProvider, ?> var1, byte var2, int var3, int[] var4) {
         super();
         this.field_197726_a = var1;
         this.field_197727_b = var2;
         this.field_197728_c = var3;
         this.field_197729_d = var4;
      }

      public boolean func_197723_a(SPacketCommandList.Entry[] var1) {
         if (this.field_197730_e == null) {
            if (this.field_197726_a == null) {
               this.field_197730_e = new RootCommandNode();
            } else {
               if ((this.field_197727_b & 8) != 0) {
                  if (var1[this.field_197728_c].field_197730_e == null) {
                     return false;
                  }

                  this.field_197726_a.redirect(var1[this.field_197728_c].field_197730_e);
               }

               if ((this.field_197727_b & 4) != 0) {
                  this.field_197726_a.executes((var0) -> {
                     return 0;
                  });
               }

               this.field_197730_e = this.field_197726_a.build();
            }
         }

         int[] var2 = this.field_197729_d;
         int var3 = var2.length;

         int var4;
         int var5;
         for(var4 = 0; var4 < var3; ++var4) {
            var5 = var2[var4];
            if (var1[var5].field_197730_e == null) {
               return false;
            }
         }

         var2 = this.field_197729_d;
         var3 = var2.length;

         for(var4 = 0; var4 < var3; ++var4) {
            var5 = var2[var4];
            CommandNode var6 = var1[var5].field_197730_e;
            if (!(var6 instanceof RootCommandNode)) {
               this.field_197730_e.addChild(var6);
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

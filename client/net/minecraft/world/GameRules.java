package net.minecraft.world;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;

public class GameRules {
   private static final TreeMap<String, GameRules.ValueDefinition> field_196232_a = (TreeMap)Util.func_200696_a(new TreeMap(), (var0) -> {
      var0.put("doFireTick", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      var0.put("mobGriefing", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      var0.put("keepInventory", new GameRules.ValueDefinition("false", GameRules.ValueType.BOOLEAN_VALUE));
      var0.put("doMobSpawning", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      var0.put("doMobLoot", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      var0.put("doTileDrops", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      var0.put("doEntityDrops", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      var0.put("commandBlockOutput", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      var0.put("naturalRegeneration", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      var0.put("doDaylightCycle", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      var0.put("logAdminCommands", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      var0.put("showDeathMessages", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      var0.put("randomTickSpeed", new GameRules.ValueDefinition("3", GameRules.ValueType.NUMERICAL_VALUE));
      var0.put("sendCommandFeedback", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      var0.put("reducedDebugInfo", new GameRules.ValueDefinition("false", GameRules.ValueType.BOOLEAN_VALUE, (var0x, var1) -> {
         int var2 = var1.func_82758_b() ? 22 : 23;
         Iterator var3 = var0x.func_184103_al().func_181057_v().iterator();

         while(var3.hasNext()) {
            EntityPlayerMP var4 = (EntityPlayerMP)var3.next();
            var4.field_71135_a.func_147359_a(new SPacketEntityStatus(var4, (byte)var2));
         }

      }));
      var0.put("spectatorsGenerateChunks", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      var0.put("spawnRadius", new GameRules.ValueDefinition("10", GameRules.ValueType.NUMERICAL_VALUE));
      var0.put("disableElytraMovementCheck", new GameRules.ValueDefinition("false", GameRules.ValueType.BOOLEAN_VALUE));
      var0.put("maxEntityCramming", new GameRules.ValueDefinition("24", GameRules.ValueType.NUMERICAL_VALUE));
      var0.put("doWeatherCycle", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
      var0.put("doLimitedCrafting", new GameRules.ValueDefinition("false", GameRules.ValueType.BOOLEAN_VALUE));
      var0.put("maxCommandChainLength", new GameRules.ValueDefinition("65536", GameRules.ValueType.NUMERICAL_VALUE));
      var0.put("announceAdvancements", new GameRules.ValueDefinition("true", GameRules.ValueType.BOOLEAN_VALUE));
   });
   private final TreeMap<String, GameRules.Value> field_82771_a = new TreeMap();

   public GameRules() {
      super();
      Iterator var1 = field_196232_a.entrySet().iterator();

      while(var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         this.field_82771_a.put(var2.getKey(), ((GameRules.ValueDefinition)var2.getValue()).func_199595_a());
      }

   }

   public void func_82764_b(String var1, String var2, @Nullable MinecraftServer var3) {
      GameRules.Value var4 = (GameRules.Value)this.field_82771_a.get(var1);
      if (var4 != null) {
         var4.func_201200_a(var2, var3);
      }

   }

   public boolean func_82766_b(String var1) {
      GameRules.Value var2 = (GameRules.Value)this.field_82771_a.get(var1);
      return var2 != null ? var2.func_82758_b() : false;
   }

   public int func_180263_c(String var1) {
      GameRules.Value var2 = (GameRules.Value)this.field_82771_a.get(var1);
      return var2 != null ? var2.func_180255_c() : 0;
   }

   public NBTTagCompound func_82770_a() {
      NBTTagCompound var1 = new NBTTagCompound();
      Iterator var2 = this.field_82771_a.keySet().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         GameRules.Value var4 = (GameRules.Value)this.field_82771_a.get(var3);
         var1.func_74778_a(var3, var4.func_82756_a());
      }

      return var1;
   }

   public void func_82768_a(NBTTagCompound var1) {
      Set var2 = var1.func_150296_c();
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         this.func_82764_b(var4, var1.func_74779_i(var4), (MinecraftServer)null);
      }

   }

   public GameRules.Value func_196230_f(String var1) {
      return (GameRules.Value)this.field_82771_a.get(var1);
   }

   public static TreeMap<String, GameRules.ValueDefinition> func_196231_c() {
      return field_196232_a;
   }

   public static enum ValueType {
      ANY_VALUE(StringArgumentType::greedyString, (var0, var1) -> {
         return (String)var0.getArgument(var1, String.class);
      }),
      BOOLEAN_VALUE(BoolArgumentType::bool, (var0, var1) -> {
         return ((Boolean)var0.getArgument(var1, Boolean.class)).toString();
      }),
      NUMERICAL_VALUE(IntegerArgumentType::integer, (var0, var1) -> {
         return ((Integer)var0.getArgument(var1, Integer.class)).toString();
      });

      private final Supplier<ArgumentType<?>> field_196228_e;
      private final BiFunction<CommandContext<CommandSource>, String, String> field_196229_f;

      private ValueType(Supplier<ArgumentType<?>> var3, BiFunction<CommandContext<CommandSource>, String, String> var4) {
         this.field_196228_e = var3;
         this.field_196229_f = var4;
      }

      public RequiredArgumentBuilder<CommandSource, ?> func_199809_a(String var1) {
         return Commands.func_197056_a(var1, (ArgumentType)this.field_196228_e.get());
      }

      public void func_196222_a(CommandContext<CommandSource> var1, String var2, GameRules.Value var3) {
         var3.func_201200_a((String)this.field_196229_f.apply(var1, var2), ((CommandSource)var1.getSource()).func_197028_i());
      }
   }

   public static class Value {
      private String field_82762_a;
      private boolean field_82760_b;
      private int field_82761_c;
      private double field_82759_d;
      private final GameRules.ValueType field_180256_e;
      private final BiConsumer<MinecraftServer, GameRules.Value> field_201201_f;

      public Value(String var1, GameRules.ValueType var2, BiConsumer<MinecraftServer, GameRules.Value> var3) {
         super();
         this.field_180256_e = var2;
         this.field_201201_f = var3;
         this.func_201200_a(var1, (MinecraftServer)null);
      }

      public void func_201200_a(String var1, @Nullable MinecraftServer var2) {
         this.field_82762_a = var1;
         this.field_82760_b = Boolean.parseBoolean(var1);
         this.field_82761_c = this.field_82760_b ? 1 : 0;

         try {
            this.field_82761_c = Integer.parseInt(var1);
         } catch (NumberFormatException var5) {
         }

         try {
            this.field_82759_d = Double.parseDouble(var1);
         } catch (NumberFormatException var4) {
         }

         if (var2 != null) {
            this.field_201201_f.accept(var2, this);
         }

      }

      public String func_82756_a() {
         return this.field_82762_a;
      }

      public boolean func_82758_b() {
         return this.field_82760_b;
      }

      public int func_180255_c() {
         return this.field_82761_c;
      }

      public GameRules.ValueType func_180254_e() {
         return this.field_180256_e;
      }
   }

   public static class ValueDefinition {
      private final GameRules.ValueType field_199596_a;
      private final String field_199597_b;
      private final BiConsumer<MinecraftServer, GameRules.Value> field_201203_c;

      public ValueDefinition(String var1, GameRules.ValueType var2) {
         this(var1, var2, (var0, var1x) -> {
         });
      }

      public ValueDefinition(String var1, GameRules.ValueType var2, BiConsumer<MinecraftServer, GameRules.Value> var3) {
         super();
         this.field_199596_a = var2;
         this.field_199597_b = var1;
         this.field_201203_c = var3;
      }

      public GameRules.Value func_199595_a() {
         return new GameRules.Value(this.field_199597_b, this.field_199596_a, this.field_201203_c);
      }

      public GameRules.ValueType func_199594_b() {
         return this.field_199596_a;
      }
   }
}

package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.serializers.BrigadierSerializers;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArgumentTypes {
   private static final Logger field_197488_a = LogManager.getLogger();
   private static final Map<Class<?>, ArgumentTypes.Entry<?>> field_197489_b = Maps.newHashMap();
   private static final Map<ResourceLocation, ArgumentTypes.Entry<?>> field_197490_c = Maps.newHashMap();

   public static <T extends ArgumentType<?>> void func_197487_a(ResourceLocation var0, Class<T> var1, IArgumentSerializer<T> var2) {
      if (field_197489_b.containsKey(var1)) {
         throw new IllegalArgumentException("Class " + var1.getName() + " already has a serializer!");
      } else if (field_197490_c.containsKey(var0)) {
         throw new IllegalArgumentException("'" + var0 + "' is already a registered serializer!");
      } else {
         ArgumentTypes.Entry var3 = new ArgumentTypes.Entry(var1, var2, var0);
         field_197489_b.put(var1, var3);
         field_197490_c.put(var0, var3);
      }
   }

   public static void func_197483_a() {
      BrigadierSerializers.func_197511_a();
      func_197487_a(new ResourceLocation("minecraft:entity"), EntityArgument.class, new EntityArgument.Serializer());
      func_197487_a(new ResourceLocation("minecraft:game_profile"), GameProfileArgument.class, new ArgumentSerializer(GameProfileArgument::func_197108_a));
      func_197487_a(new ResourceLocation("minecraft:block_pos"), BlockPosArgument.class, new ArgumentSerializer(BlockPosArgument::func_197276_a));
      func_197487_a(new ResourceLocation("minecraft:column_pos"), ColumnPosArgument.class, new ArgumentSerializer(ColumnPosArgument::func_212603_a));
      func_197487_a(new ResourceLocation("minecraft:vec3"), Vec3Argument.class, new ArgumentSerializer(Vec3Argument::func_197301_a));
      func_197487_a(new ResourceLocation("minecraft:vec2"), Vec2Argument.class, new ArgumentSerializer(Vec2Argument::func_197296_a));
      func_197487_a(new ResourceLocation("minecraft:block_state"), BlockStateArgument.class, new ArgumentSerializer(BlockStateArgument::func_197239_a));
      func_197487_a(new ResourceLocation("minecraft:block_predicate"), BlockPredicateArgument.class, new ArgumentSerializer(BlockPredicateArgument::func_199824_a));
      func_197487_a(new ResourceLocation("minecraft:item_stack"), ItemArgument.class, new ArgumentSerializer(ItemArgument::func_197317_a));
      func_197487_a(new ResourceLocation("minecraft:item_predicate"), ItemPredicateArgument.class, new ArgumentSerializer(ItemPredicateArgument::func_199846_a));
      func_197487_a(new ResourceLocation("minecraft:color"), ColorArgument.class, new ArgumentSerializer(ColorArgument::func_197063_a));
      func_197487_a(new ResourceLocation("minecraft:component"), ComponentArgument.class, new ArgumentSerializer(ComponentArgument::func_197067_a));
      func_197487_a(new ResourceLocation("minecraft:message"), MessageArgument.class, new ArgumentSerializer(MessageArgument::func_197123_a));
      func_197487_a(new ResourceLocation("minecraft:nbt"), NBTArgument.class, new ArgumentSerializer(NBTArgument::func_197131_a));
      func_197487_a(new ResourceLocation("minecraft:nbt_path"), NBTPathArgument.class, new ArgumentSerializer(NBTPathArgument::func_197149_a));
      func_197487_a(new ResourceLocation("minecraft:objective"), ObjectiveArgument.class, new ArgumentSerializer(ObjectiveArgument::func_197157_a));
      func_197487_a(new ResourceLocation("minecraft:objective_criteria"), ObjectiveCriteriaArgument.class, new ArgumentSerializer(ObjectiveCriteriaArgument::func_197162_a));
      func_197487_a(new ResourceLocation("minecraft:operation"), OperationArgument.class, new ArgumentSerializer(OperationArgument::func_197184_a));
      func_197487_a(new ResourceLocation("minecraft:particle"), ParticleArgument.class, new ArgumentSerializer(ParticleArgument::func_197190_a));
      func_197487_a(new ResourceLocation("minecraft:rotation"), RotationArgument.class, new ArgumentSerializer(RotationArgument::func_197288_a));
      func_197487_a(new ResourceLocation("minecraft:scoreboard_slot"), ScoreboardSlotArgument.class, new ArgumentSerializer(ScoreboardSlotArgument::func_197219_a));
      func_197487_a(new ResourceLocation("minecraft:score_holder"), ScoreHolderArgument.class, new ScoreHolderArgument.Serializer());
      func_197487_a(new ResourceLocation("minecraft:swizzle"), SwizzleArgument.class, new ArgumentSerializer(SwizzleArgument::func_197293_a));
      func_197487_a(new ResourceLocation("minecraft:team"), TeamArgument.class, new ArgumentSerializer(TeamArgument::func_197227_a));
      func_197487_a(new ResourceLocation("minecraft:item_slot"), SlotArgument.class, new ArgumentSerializer(SlotArgument::func_197223_a));
      func_197487_a(new ResourceLocation("minecraft:resource_location"), ResourceLocationArgument.class, new ArgumentSerializer(ResourceLocationArgument::func_197197_a));
      func_197487_a(new ResourceLocation("minecraft:mob_effect"), PotionArgument.class, new ArgumentSerializer(PotionArgument::func_197126_a));
      func_197487_a(new ResourceLocation("minecraft:function"), FunctionArgument.class, new ArgumentSerializer(FunctionArgument::func_200021_a));
      func_197487_a(new ResourceLocation("minecraft:entity_anchor"), EntityAnchorArgument.class, new ArgumentSerializer(EntityAnchorArgument::func_201024_a));
      func_197487_a(new ResourceLocation("minecraft:int_range"), RangeArgument.IntRange.class, new RangeArgument.IntRange.Serializer());
      func_197487_a(new ResourceLocation("minecraft:float_range"), RangeArgument.FloatRange.class, new RangeArgument.FloatRange.Serializer());
      func_197487_a(new ResourceLocation("minecraft:item_enchantment"), EnchantmentArgument.class, new ArgumentSerializer(EnchantmentArgument::func_201945_a));
      func_197487_a(new ResourceLocation("minecraft:entity_summon"), EntitySummonArgument.class, new ArgumentSerializer(EntitySummonArgument::func_211366_a));
      func_197487_a(new ResourceLocation("minecraft:dimension"), DimensionArgument.class, new ArgumentSerializer(DimensionArgument::func_212595_a));
   }

   @Nullable
   private static ArgumentTypes.Entry<?> func_197482_a(ResourceLocation var0) {
      return (ArgumentTypes.Entry)field_197490_c.get(var0);
   }

   @Nullable
   private static ArgumentTypes.Entry<?> func_201040_a(ArgumentType<?> var0) {
      return (ArgumentTypes.Entry)field_197489_b.get(var0.getClass());
   }

   public static <T extends ArgumentType<?>> void func_197484_a(PacketBuffer var0, T var1) {
      ArgumentTypes.Entry var2 = func_201040_a(var1);
      if (var2 == null) {
         field_197488_a.error("Could not serialize {} ({}) - will not be sent to client!", var1, var1.getClass());
         var0.func_192572_a(new ResourceLocation(""));
      } else {
         var0.func_192572_a(var2.field_197481_c);
         var2.field_197480_b.func_197072_a(var1, var0);
      }
   }

   @Nullable
   public static ArgumentType<?> func_197486_a(PacketBuffer var0) {
      ResourceLocation var1 = var0.func_192575_l();
      ArgumentTypes.Entry var2 = func_197482_a(var1);
      if (var2 == null) {
         field_197488_a.error("Could not deserialize {}", var1);
         return null;
      } else {
         return var2.field_197480_b.func_197071_b(var0);
      }
   }

   private static <T extends ArgumentType<?>> void func_201042_a(JsonObject var0, T var1) {
      ArgumentTypes.Entry var2 = func_201040_a(var1);
      if (var2 == null) {
         field_197488_a.error("Could not serialize argument {} ({})!", var1, var1.getClass());
         var0.addProperty("type", "unknown");
      } else {
         var0.addProperty("type", "argument");
         var0.addProperty("parser", var2.field_197481_c.toString());
         JsonObject var3 = new JsonObject();
         var2.field_197480_b.func_212244_a(var1, var3);
         if (var3.size() > 0) {
            var0.add("properties", var3);
         }
      }

   }

   public static <S> JsonObject func_200388_a(CommandDispatcher<S> var0, CommandNode<S> var1) {
      JsonObject var2 = new JsonObject();
      if (var1 instanceof RootCommandNode) {
         var2.addProperty("type", "root");
      } else if (var1 instanceof LiteralCommandNode) {
         var2.addProperty("type", "literal");
      } else if (var1 instanceof ArgumentCommandNode) {
         func_201042_a(var2, ((ArgumentCommandNode)var1).getType());
      } else {
         field_197488_a.error("Could not serialize node {} ({})!", var1, var1.getClass());
         var2.addProperty("type", "unknown");
      }

      JsonObject var3 = new JsonObject();
      Iterator var4 = var1.getChildren().iterator();

      while(var4.hasNext()) {
         CommandNode var5 = (CommandNode)var4.next();
         var3.add(var5.getName(), func_200388_a(var0, var5));
      }

      if (var3.size() > 0) {
         var2.add("children", var3);
      }

      if (var1.getCommand() != null) {
         var2.addProperty("executable", true);
      }

      if (var1.getRedirect() != null) {
         Collection var8 = var0.getPath(var1.getRedirect());
         if (!var8.isEmpty()) {
            JsonArray var9 = new JsonArray();
            Iterator var6 = var8.iterator();

            while(var6.hasNext()) {
               String var7 = (String)var6.next();
               var9.add(var7);
            }

            var2.add("redirect", var9);
         }
      }

      return var2;
   }

   static class Entry<T extends ArgumentType<?>> {
      public final Class<T> field_197479_a;
      public final IArgumentSerializer<T> field_197480_b;
      public final ResourceLocation field_197481_c;

      private Entry(Class<T> var1, IArgumentSerializer<T> var2, ResourceLocation var3) {
         super();
         this.field_197479_a = var1;
         this.field_197480_b = var2;
         this.field_197481_c = var3;
      }

      // $FF: synthetic method
      Entry(Class var1, IArgumentSerializer var2, ResourceLocation var3, Object var4) {
         this(var1, var2, var3);
      }
   }
}

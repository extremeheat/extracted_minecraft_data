package net.minecraft.commands.synchronization;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.commands.arguments.AngleArgument;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.EntitySummonArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.ItemEnchantmentArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.arguments.MobEffectArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ObjectiveCriteriaArgument;
import net.minecraft.commands.arguments.OperationArgument;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.RangeArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.ScoreboardSlotArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.SwizzleArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.commands.synchronization.brigadier.BrigadierArgumentSerializers;
import net.minecraft.gametest.framework.TestClassNameArgument;
import net.minecraft.gametest.framework.TestFunctionArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArgumentTypes {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map<Class<?>, ArgumentTypes.Entry<?>> BY_CLASS = Maps.newHashMap();
   private static final Map<ResourceLocation, ArgumentTypes.Entry<?>> BY_NAME = Maps.newHashMap();

   public ArgumentTypes() {
      super();
   }

   public static <T extends ArgumentType<?>> void register(String var0, Class<T> var1, ArgumentSerializer<T> var2) {
      ResourceLocation var3 = new ResourceLocation(var0);
      if (BY_CLASS.containsKey(var1)) {
         throw new IllegalArgumentException("Class " + var1.getName() + " already has a serializer!");
      } else if (BY_NAME.containsKey(var3)) {
         throw new IllegalArgumentException("'" + var3 + "' is already a registered serializer!");
      } else {
         ArgumentTypes.Entry var4 = new ArgumentTypes.Entry(var1, var2, var3);
         BY_CLASS.put(var1, var4);
         BY_NAME.put(var3, var4);
      }
   }

   public static void bootStrap() {
      BrigadierArgumentSerializers.bootstrap();
      register("entity", EntityArgument.class, new EntityArgument.Serializer());
      register("game_profile", GameProfileArgument.class, new EmptyArgumentSerializer(GameProfileArgument::gameProfile));
      register("block_pos", BlockPosArgument.class, new EmptyArgumentSerializer(BlockPosArgument::blockPos));
      register("column_pos", ColumnPosArgument.class, new EmptyArgumentSerializer(ColumnPosArgument::columnPos));
      register("vec3", Vec3Argument.class, new EmptyArgumentSerializer(Vec3Argument::vec3));
      register("vec2", Vec2Argument.class, new EmptyArgumentSerializer(Vec2Argument::vec2));
      register("block_state", BlockStateArgument.class, new EmptyArgumentSerializer(BlockStateArgument::block));
      register("block_predicate", BlockPredicateArgument.class, new EmptyArgumentSerializer(BlockPredicateArgument::blockPredicate));
      register("item_stack", ItemArgument.class, new EmptyArgumentSerializer(ItemArgument::item));
      register("item_predicate", ItemPredicateArgument.class, new EmptyArgumentSerializer(ItemPredicateArgument::itemPredicate));
      register("color", ColorArgument.class, new EmptyArgumentSerializer(ColorArgument::color));
      register("component", ComponentArgument.class, new EmptyArgumentSerializer(ComponentArgument::textComponent));
      register("message", MessageArgument.class, new EmptyArgumentSerializer(MessageArgument::message));
      register("nbt_compound_tag", CompoundTagArgument.class, new EmptyArgumentSerializer(CompoundTagArgument::compoundTag));
      register("nbt_tag", NbtTagArgument.class, new EmptyArgumentSerializer(NbtTagArgument::nbtTag));
      register("nbt_path", NbtPathArgument.class, new EmptyArgumentSerializer(NbtPathArgument::nbtPath));
      register("objective", ObjectiveArgument.class, new EmptyArgumentSerializer(ObjectiveArgument::objective));
      register("objective_criteria", ObjectiveCriteriaArgument.class, new EmptyArgumentSerializer(ObjectiveCriteriaArgument::criteria));
      register("operation", OperationArgument.class, new EmptyArgumentSerializer(OperationArgument::operation));
      register("particle", ParticleArgument.class, new EmptyArgumentSerializer(ParticleArgument::particle));
      register("angle", AngleArgument.class, new EmptyArgumentSerializer(AngleArgument::angle));
      register("rotation", RotationArgument.class, new EmptyArgumentSerializer(RotationArgument::rotation));
      register("scoreboard_slot", ScoreboardSlotArgument.class, new EmptyArgumentSerializer(ScoreboardSlotArgument::displaySlot));
      register("score_holder", ScoreHolderArgument.class, new ScoreHolderArgument.Serializer());
      register("swizzle", SwizzleArgument.class, new EmptyArgumentSerializer(SwizzleArgument::swizzle));
      register("team", TeamArgument.class, new EmptyArgumentSerializer(TeamArgument::team));
      register("item_slot", SlotArgument.class, new EmptyArgumentSerializer(SlotArgument::slot));
      register("resource_location", ResourceLocationArgument.class, new EmptyArgumentSerializer(ResourceLocationArgument::id));
      register("mob_effect", MobEffectArgument.class, new EmptyArgumentSerializer(MobEffectArgument::effect));
      register("function", FunctionArgument.class, new EmptyArgumentSerializer(FunctionArgument::functions));
      register("entity_anchor", EntityAnchorArgument.class, new EmptyArgumentSerializer(EntityAnchorArgument::anchor));
      register("int_range", RangeArgument.Ints.class, new EmptyArgumentSerializer(RangeArgument::intRange));
      register("float_range", RangeArgument.Floats.class, new EmptyArgumentSerializer(RangeArgument::floatRange));
      register("item_enchantment", ItemEnchantmentArgument.class, new EmptyArgumentSerializer(ItemEnchantmentArgument::enchantment));
      register("entity_summon", EntitySummonArgument.class, new EmptyArgumentSerializer(EntitySummonArgument::id));
      register("dimension", DimensionArgument.class, new EmptyArgumentSerializer(DimensionArgument::dimension));
      register("time", TimeArgument.class, new EmptyArgumentSerializer(TimeArgument::time));
      register("uuid", UuidArgument.class, new EmptyArgumentSerializer(UuidArgument::uuid));
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         register("test_argument", TestFunctionArgument.class, new EmptyArgumentSerializer(TestFunctionArgument::testFunctionArgument));
         register("test_class", TestClassNameArgument.class, new EmptyArgumentSerializer(TestClassNameArgument::testClassName));
      }

   }

   @Nullable
   private static ArgumentTypes.Entry<?> get(ResourceLocation var0) {
      return (ArgumentTypes.Entry)BY_NAME.get(var0);
   }

   @Nullable
   private static ArgumentTypes.Entry<?> get(ArgumentType<?> var0) {
      return (ArgumentTypes.Entry)BY_CLASS.get(var0.getClass());
   }

   public static <T extends ArgumentType<?>> void serialize(FriendlyByteBuf var0, T var1) {
      ArgumentTypes.Entry var2 = get(var1);
      if (var2 == null) {
         LOGGER.error("Could not serialize {} ({}) - will not be sent to client!", var1, var1.getClass());
         var0.writeResourceLocation(new ResourceLocation(""));
      } else {
         var0.writeResourceLocation(var2.name);
         var2.serializer.serializeToNetwork(var1, var0);
      }
   }

   @Nullable
   public static ArgumentType<?> deserialize(FriendlyByteBuf var0) {
      ResourceLocation var1 = var0.readResourceLocation();
      ArgumentTypes.Entry var2 = get(var1);
      if (var2 == null) {
         LOGGER.error("Could not deserialize {}", var1);
         return null;
      } else {
         return var2.serializer.deserializeFromNetwork(var0);
      }
   }

   private static <T extends ArgumentType<?>> void serializeToJson(JsonObject var0, T var1) {
      ArgumentTypes.Entry var2 = get(var1);
      if (var2 == null) {
         LOGGER.error("Could not serialize argument {} ({})!", var1, var1.getClass());
         var0.addProperty("type", "unknown");
      } else {
         var0.addProperty("type", "argument");
         var0.addProperty("parser", var2.name.toString());
         JsonObject var3 = new JsonObject();
         var2.serializer.serializeToJson(var1, var3);
         if (var3.size() > 0) {
            var0.add("properties", var3);
         }
      }

   }

   public static <S> JsonObject serializeNodeToJson(CommandDispatcher<S> var0, CommandNode<S> var1) {
      JsonObject var2 = new JsonObject();
      if (var1 instanceof RootCommandNode) {
         var2.addProperty("type", "root");
      } else if (var1 instanceof LiteralCommandNode) {
         var2.addProperty("type", "literal");
      } else if (var1 instanceof ArgumentCommandNode) {
         serializeToJson(var2, ((ArgumentCommandNode)var1).getType());
      } else {
         LOGGER.error("Could not serialize node {} ({})!", var1, var1.getClass());
         var2.addProperty("type", "unknown");
      }

      JsonObject var3 = new JsonObject();
      Iterator var4 = var1.getChildren().iterator();

      while(var4.hasNext()) {
         CommandNode var5 = (CommandNode)var4.next();
         var3.add(var5.getName(), serializeNodeToJson(var0, var5));
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

   public static boolean isTypeRegistered(ArgumentType<?> var0) {
      return get(var0) != null;
   }

   public static <T> Set<ArgumentType<?>> findUsedArgumentTypes(CommandNode<T> var0) {
      Set var1 = Sets.newIdentityHashSet();
      HashSet var2 = Sets.newHashSet();
      findUsedArgumentTypes(var0, var2, var1);
      return var2;
   }

   private static <T> void findUsedArgumentTypes(CommandNode<T> var0, Set<ArgumentType<?>> var1, Set<CommandNode<T>> var2) {
      if (var2.add(var0)) {
         if (var0 instanceof ArgumentCommandNode) {
            var1.add(((ArgumentCommandNode)var0).getType());
         }

         var0.getChildren().forEach((var2x) -> {
            findUsedArgumentTypes(var2x, var1, var2);
         });
         CommandNode var3 = var0.getRedirect();
         if (var3 != null) {
            findUsedArgumentTypes(var3, var1, var2);
         }

      }
   }

   static class Entry<T extends ArgumentType<?>> {
      public final Class<T> clazz;
      public final ArgumentSerializer<T> serializer;
      public final ResourceLocation name;

      Entry(Class<T> var1, ArgumentSerializer<T> var2, ResourceLocation var3) {
         super();
         this.clazz = var1;
         this.serializer = var2;
         this.name = var3;
      }
   }
}

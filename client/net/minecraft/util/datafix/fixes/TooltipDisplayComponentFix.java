package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import net.minecraft.util.Util;

public class TooltipDisplayComponentFix extends DataFix {
   private static final List<String> CONVERTED_ADDITIONAL_TOOLTIP_TYPES = List.of("minecraft:banner_patterns", "minecraft:bees", "minecraft:block_entity_data", "minecraft:block_state", "minecraft:bundle_contents", "minecraft:charged_projectiles", "minecraft:container", "minecraft:container_loot", "minecraft:firework_explosion", "minecraft:fireworks", "minecraft:instrument", "minecraft:map_id", "minecraft:painting/variant", "minecraft:pot_decorations", "minecraft:potion_contents", "minecraft:tropical_fish/pattern", "minecraft:written_book_content");

   public TooltipDisplayComponentFix(Schema var1) {
      super(var1, true);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.DATA_COMPONENTS);
      Type var2 = this.getOutputSchema().getType(References.DATA_COMPONENTS);
      OpticFinder var3 = var1.findField("minecraft:can_place_on");
      OpticFinder var4 = var1.findField("minecraft:can_break");
      Type var5 = var2.findFieldType("minecraft:can_place_on");
      Type var6 = var2.findFieldType("minecraft:can_break");
      return this.fixTypeEverywhereTyped("TooltipDisplayComponentFix", var1, var2, (var4x) -> fix(var4x, var3, var4, var5, var6));
   }

   private static Typed<?> fix(Typed<?> var0, OpticFinder<?> var1, OpticFinder<?> var2, Type<?> var3, Type<?> var4) {
      HashSet var5 = new HashSet();
      var0 = fixAdventureModePredicate(var0, var1, var3, "minecraft:can_place_on", var5);
      var0 = fixAdventureModePredicate(var0, var2, var4, "minecraft:can_break", var5);
      return var0.update(DSL.remainderFinder(), (var1x) -> {
         var1x = fixSimpleComponent(var1x, "minecraft:trim", var5);
         var1x = fixSimpleComponent(var1x, "minecraft:unbreakable", var5);
         var1x = fixComponentAndUnwrap(var1x, "minecraft:dyed_color", "rgb", var5);
         var1x = fixComponentAndUnwrap(var1x, "minecraft:attribute_modifiers", "modifiers", var5);
         var1x = fixComponentAndUnwrap(var1x, "minecraft:enchantments", "levels", var5);
         var1x = fixComponentAndUnwrap(var1x, "minecraft:stored_enchantments", "levels", var5);
         var1x = fixComponentAndUnwrap(var1x, "minecraft:jukebox_playable", "song", var5);
         boolean var2 = var1x.get("minecraft:hide_tooltip").result().isPresent();
         var1x = var1x.remove("minecraft:hide_tooltip");
         boolean var3 = var1x.get("minecraft:hide_additional_tooltip").result().isPresent();
         var1x = var1x.remove("minecraft:hide_additional_tooltip");
         if (var3) {
            for(String var5x : CONVERTED_ADDITIONAL_TOOLTIP_TYPES) {
               if (var1x.get(var5x).result().isPresent()) {
                  var5.add(var5x);
               }
            }
         }

         if (var5.isEmpty() && !var2) {
            return var1x;
         } else {
            Dynamic var10003 = var1x.createString("hide_tooltip");
            Dynamic var10004 = var1x.createBoolean(var2);
            Dynamic var10005 = var1x.createString("hidden_components");
            Stream var10007 = var5.stream();
            Objects.requireNonNull(var1x);
            return var1x.set("minecraft:tooltip_display", var1x.createMap(Map.of(var10003, var10004, var10005, var1x.createList(var10007.map(var1x::createString)))));
         }
      });
   }

   private static Dynamic<?> fixSimpleComponent(Dynamic<?> var0, String var1, Set<String> var2) {
      return fixRemainderComponent(var0, var1, var2, UnaryOperator.identity());
   }

   private static Dynamic<?> fixComponentAndUnwrap(Dynamic<?> var0, String var1, String var2, Set<String> var3) {
      return fixRemainderComponent(var0, var1, var3, (var1x) -> (Dynamic)DataFixUtils.orElse(var1x.get(var2).result(), var1x));
   }

   private static Dynamic<?> fixRemainderComponent(Dynamic<?> var0, String var1, Set<String> var2, UnaryOperator<Dynamic<?>> var3) {
      return var0.update(var1, (var3x) -> {
         boolean var4 = var3x.get("show_in_tooltip").asBoolean(true);
         if (!var4) {
            var2.add(var1);
         }

         return (Dynamic)var3.apply(var3x.remove("show_in_tooltip"));
      });
   }

   private static Typed<?> fixAdventureModePredicate(Typed<?> var0, OpticFinder<?> var1, Type<?> var2, String var3, Set<String> var4) {
      return var0.updateTyped(var1, var2, (var3x) -> Util.writeAndReadTypedOrThrow(var3x, var2, (var2x) -> {
            OptionalDynamic var3x = var2x.get("predicates");
            if (var3x.result().isEmpty()) {
               return var2x;
            } else {
               boolean var4x = var2x.get("show_in_tooltip").asBoolean(true);
               if (!var4x) {
                  var4.add(var3);
               }

               return (Dynamic)var3x.result().get();
            }
         }));
   }
}

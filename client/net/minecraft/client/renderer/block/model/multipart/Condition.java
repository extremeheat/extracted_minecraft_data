package net.minecraft.client.renderer.block.model.multipart;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;

@FunctionalInterface
public interface Condition {
   Codec<Condition> CODEC = Codec.recursive("condition", (var0) -> {
      Codec var1 = Codec.simpleMap(CombinedCondition.Operation.CODEC, var0.listOf(), StringRepresentable.keys(CombinedCondition.Operation.values())).codec().comapFlatMap((var0x) -> {
         if (var0x.size() != 1) {
            return DataResult.error(() -> "Invalid map size for combiner condition, expected exactly one element");
         } else {
            Map.Entry var1 = (Map.Entry)var0x.entrySet().iterator().next();
            return DataResult.success(new CombinedCondition((CombinedCondition.Operation)var1.getKey(), (List)var1.getValue()));
         }
      }, (var0x) -> Map.of(var0x.operation(), var0x.terms()));
      return Codec.either(var1, KeyValueCondition.CODEC).flatComapMap((var0x) -> (Condition)var0x.map((var0) -> var0, (var0) -> var0), (var0x) -> {
         Objects.requireNonNull(var0x);
         byte var3 = 0;
         DataResult var10000;
         //$FF: var3->value
         //0->net/minecraft/client/renderer/block/model/multipart/CombinedCondition
         //1->net/minecraft/client/renderer/block/model/multipart/KeyValueCondition
         switch (var0x.typeSwitch<invokedynamic>(var0x, var3)) {
            case 0:
               CombinedCondition var4 = (CombinedCondition)var0x;
               var10000 = DataResult.success(Either.left(var4));
               break;
            case 1:
               KeyValueCondition var5 = (KeyValueCondition)var0x;
               var10000 = DataResult.success(Either.right(var5));
               break;
            default:
               var10000 = DataResult.error(() -> "Unrecognized condition");
         }

         DataResult var1 = var10000;
         return var1;
      });
   });

   <O, S extends StateHolder<O, S>> Predicate<S> instantiate(StateDefinition<O, S> var1);
}

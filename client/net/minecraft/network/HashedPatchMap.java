package net.minecraft.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record HashedPatchMap(Map<DataComponentType<?>, Integer> addedComponents, Set<DataComponentType<?>> removedComponents) {
   public static final StreamCodec<RegistryFriendlyByteBuf, HashedPatchMap> STREAM_CODEC;

   public HashedPatchMap(Map<DataComponentType<?>, Integer> var1, Set<DataComponentType<?>> var2) {
      super();
      this.addedComponents = var1;
      this.removedComponents = var2;
   }

   public static HashedPatchMap create(DataComponentPatch var0, HashGenerator var1) {
      DataComponentPatch.SplitResult var2 = var0.split();
      IdentityHashMap var3 = new IdentityHashMap(var2.added().size());
      var2.added().forEach((var2x) -> var3.put(var2x.type(), (Integer)var1.apply(var2x)));
      return new HashedPatchMap(var3, var2.removed());
   }

   public boolean matches(DataComponentPatch var1, HashGenerator var2) {
      DataComponentPatch.SplitResult var3 = var1.split();
      if (!var3.removed().equals(this.removedComponents)) {
         return false;
      } else if (this.addedComponents.size() != var3.added().size()) {
         return false;
      } else {
         for(TypedDataComponent var5 : var3.added()) {
            Integer var6 = (Integer)this.addedComponents.get(var5.type());
            if (var6 == null) {
               return false;
            }

            Integer var7 = (Integer)var2.apply(var5);
            if (!var7.equals(var6)) {
               return false;
            }
         }

         return true;
      }
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.map(HashMap::new, ByteBufCodecs.registry(Registries.DATA_COMPONENT_TYPE), ByteBufCodecs.INT, 256), HashedPatchMap::addedComponents, ByteBufCodecs.collection(HashSet::new, ByteBufCodecs.registry(Registries.DATA_COMPONENT_TYPE), 256), HashedPatchMap::removedComponents, HashedPatchMap::new);
   }

   @FunctionalInterface
   public interface HashGenerator extends Function<TypedDataComponent<?>, Integer> {
   }
}

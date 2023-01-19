package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyNameFunction extends LootItemConditionalFunction {
   final CopyNameFunction.NameSource source;

   CopyNameFunction(LootItemCondition[] var1, CopyNameFunction.NameSource var2) {
      super(var1);
      this.source = var2;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.COPY_NAME;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(this.source.param);
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      Object var3 = var2.getParamOrNull(this.source.param);
      if (var3 instanceof Nameable var4 && var4.hasCustomName()) {
         var1.setHoverName(var4.getDisplayName());
      }

      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> copyName(CopyNameFunction.NameSource var0) {
      return simpleBuilder(var1 -> new CopyNameFunction(var1, var0));
   }

   public static enum NameSource {
      THIS("this", LootContextParams.THIS_ENTITY),
      KILLER("killer", LootContextParams.KILLER_ENTITY),
      KILLER_PLAYER("killer_player", LootContextParams.LAST_DAMAGE_PLAYER),
      BLOCK_ENTITY("block_entity", LootContextParams.BLOCK_ENTITY);

      public final String name;
      public final LootContextParam<?> param;

      private NameSource(String var3, LootContextParam<?> var4) {
         this.name = var3;
         this.param = var4;
      }

      public static CopyNameFunction.NameSource getByName(String var0) {
         for(CopyNameFunction.NameSource var4 : values()) {
            if (var4.name.equals(var0)) {
               return var4;
            }
         }

         throw new IllegalArgumentException("Invalid name source " + var0);
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<CopyNameFunction> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, CopyNameFunction var2, JsonSerializationContext var3) {
         super.serialize(var1, var2, var3);
         var1.addProperty("source", var2.source.name);
      }

      public CopyNameFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         CopyNameFunction.NameSource var4 = CopyNameFunction.NameSource.getByName(GsonHelper.getAsString(var1, "source"));
         return new CopyNameFunction(var3, var4);
      }
   }
}

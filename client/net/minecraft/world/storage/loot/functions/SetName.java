package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class SetName extends LootFunction {
   private final ITextComponent field_200539_a;

   public SetName(LootCondition[] var1, @Nullable ITextComponent var2) {
      super(var1);
      this.field_200539_a = var2;
   }

   public ItemStack func_186553_a(ItemStack var1, Random var2, LootContext var3) {
      if (this.field_200539_a != null) {
         var1.func_200302_a(this.field_200539_a);
      }

      return var1;
   }

   public static class Serializer extends LootFunction.Serializer<SetName> {
      public Serializer() {
         super(new ResourceLocation("set_name"), SetName.class);
      }

      public void func_186532_a(JsonObject var1, SetName var2, JsonSerializationContext var3) {
         if (var2.field_200539_a != null) {
            var1.add("name", ITextComponent.Serializer.func_200528_b(var2.field_200539_a));
         }

      }

      public SetName func_186530_b(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3) {
         ITextComponent var4 = ITextComponent.Serializer.func_197672_a(var1.get("name"));
         return new SetName(var3, var4);
      }

      // $FF: synthetic method
      public LootFunction func_186530_b(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3) {
         return this.func_186530_b(var1, var2, var3);
      }
   }
}

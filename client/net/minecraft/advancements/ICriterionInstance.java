package net.minecraft.advancements;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraft.util.ResourceLocation;

public interface ICriterionInstance {
   ResourceLocation func_192244_a();

   default JsonElement func_200288_b() {
      return JsonNull.INSTANCE;
   }
}

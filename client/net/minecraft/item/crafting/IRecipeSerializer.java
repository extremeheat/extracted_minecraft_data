package net.minecraft.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public interface IRecipeSerializer<T extends IRecipe> {
   T func_199425_a_(ResourceLocation var1, JsonObject var2);

   T func_199426_a_(ResourceLocation var1, PacketBuffer var2);

   void func_199427_a_(PacketBuffer var1, T var2);

   String func_199567_a();
}

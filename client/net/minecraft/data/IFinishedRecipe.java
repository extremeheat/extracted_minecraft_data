package net.minecraft.data;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public interface IFinishedRecipe {
   JsonObject func_200441_a();

   ResourceLocation func_200442_b();

   @Nullable
   JsonObject func_200440_c();

   @Nullable
   ResourceLocation func_200443_d();
}

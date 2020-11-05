package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class FluidPredicate {
   public static final FluidPredicate ANY;
   @Nullable
   private final Tag<Fluid> tag;
   @Nullable
   private final Fluid fluid;
   private final StatePropertiesPredicate properties;

   public FluidPredicate(@Nullable Tag<Fluid> var1, @Nullable Fluid var2, StatePropertiesPredicate var3) {
      super();
      this.tag = var1;
      this.fluid = var2;
      this.properties = var3;
   }

   public boolean matches(ServerLevel var1, BlockPos var2) {
      if (this == ANY) {
         return true;
      } else if (!var1.isLoaded(var2)) {
         return false;
      } else {
         FluidState var3 = var1.getFluidState(var2);
         Fluid var4 = var3.getType();
         if (this.tag != null && !this.tag.contains(var4)) {
            return false;
         } else if (this.fluid != null && var4 != this.fluid) {
            return false;
         } else {
            return this.properties.matches(var3);
         }
      }
   }

   public static FluidPredicate fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(var0, "fluid");
         Fluid var2 = null;
         if (var1.has("fluid")) {
            ResourceLocation var3 = new ResourceLocation(GsonHelper.getAsString(var1, "fluid"));
            var2 = (Fluid)Registry.FLUID.get(var3);
         }

         Tag var5 = null;
         if (var1.has("tag")) {
            ResourceLocation var4 = new ResourceLocation(GsonHelper.getAsString(var1, "tag"));
            var5 = SerializationTags.getInstance().getFluids().getTag(var4);
            if (var5 == null) {
               throw new JsonSyntaxException("Unknown fluid tag '" + var4 + "'");
            }
         }

         StatePropertiesPredicate var6 = StatePropertiesPredicate.fromJson(var1.get("state"));
         return new FluidPredicate(var5, var2, var6);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         if (this.fluid != null) {
            var1.addProperty("fluid", Registry.FLUID.getKey(this.fluid).toString());
         }

         if (this.tag != null) {
            var1.addProperty("tag", SerializationTags.getInstance().getFluids().getIdOrThrow(this.tag).toString());
         }

         var1.add("state", this.properties.serializeToJson());
         return var1;
      }
   }

   static {
      ANY = new FluidPredicate((Tag)null, (Fluid)null, StatePropertiesPredicate.ANY);
   }
}

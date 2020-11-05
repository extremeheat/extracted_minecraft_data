package net.minecraft.util.datafix.fixes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.lang.reflect.Type;
import java.util.Iterator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.StringUtils;

public class BlockEntitySignTextStrictJsonFix extends NamedEntityFix {
   public static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(Component.class, new JsonDeserializer<Component>() {
      public MutableComponent deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         if (var1.isJsonPrimitive()) {
            return new TextComponent(var1.getAsString());
         } else if (var1.isJsonArray()) {
            JsonArray var4 = var1.getAsJsonArray();
            MutableComponent var5 = null;
            Iterator var6 = var4.iterator();

            while(var6.hasNext()) {
               JsonElement var7 = (JsonElement)var6.next();
               MutableComponent var8 = this.deserialize(var7, var7.getClass(), var3);
               if (var5 == null) {
                  var5 = var8;
               } else {
                  var5.append((Component)var8);
               }
            }

            return var5;
         } else {
            throw new JsonParseException("Don't know how to turn " + var1 + " into a Component");
         }
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }).create();

   public BlockEntitySignTextStrictJsonFix(Schema var1, boolean var2) {
      super(var1, var2, "BlockEntitySignTextStrictJsonFix", References.BLOCK_ENTITY, "Sign");
   }

   private Dynamic<?> updateLine(Dynamic<?> var1, String var2) {
      String var3 = var1.get(var2).asString("");
      Object var4 = null;
      if (!"null".equals(var3) && !StringUtils.isEmpty(var3)) {
         if (var3.charAt(0) == '"' && var3.charAt(var3.length() - 1) == '"' || var3.charAt(0) == '{' && var3.charAt(var3.length() - 1) == '}') {
            try {
               var4 = (Component)GsonHelper.fromJson(GSON, var3, Component.class, true);
               if (var4 == null) {
                  var4 = TextComponent.EMPTY;
               }
            } catch (JsonParseException var8) {
            }

            if (var4 == null) {
               try {
                  var4 = Component.Serializer.fromJson(var3);
               } catch (JsonParseException var7) {
               }
            }

            if (var4 == null) {
               try {
                  var4 = Component.Serializer.fromJsonLenient(var3);
               } catch (JsonParseException var6) {
               }
            }

            if (var4 == null) {
               var4 = new TextComponent(var3);
            }
         } else {
            var4 = new TextComponent(var3);
         }
      } else {
         var4 = TextComponent.EMPTY;
      }

      return var1.set(var2, var1.createString(Component.Serializer.toJson((Component)var4)));
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), (var1x) -> {
         var1x = this.updateLine(var1x, "Text1");
         var1x = this.updateLine(var1x, "Text2");
         var1x = this.updateLine(var1x, "Text3");
         var1x = this.updateLine(var1x, "Text4");
         return var1x;
      });
   }
}

package net.minecraft.util.datafix.fixes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.lang.reflect.Type;
import java.util.Iterator;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.StringUtils;

public class SignStrictJSON extends NamedEntityFix {
   public static final Gson field_188225_a = (new GsonBuilder()).registerTypeAdapter(ITextComponent.class, new JsonDeserializer<ITextComponent>() {
      public ITextComponent deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         if (var1.isJsonPrimitive()) {
            return new TextComponentString(var1.getAsString());
         } else if (var1.isJsonArray()) {
            JsonArray var4 = var1.getAsJsonArray();
            ITextComponent var5 = null;
            Iterator var6 = var4.iterator();

            while(var6.hasNext()) {
               JsonElement var7 = (JsonElement)var6.next();
               ITextComponent var8 = this.deserialize(var7, var7.getClass(), var3);
               if (var5 == null) {
                  var5 = var8;
               } else {
                  var5.func_150257_a(var8);
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

   public SignStrictJSON(Schema var1, boolean var2) {
      super(var1, var2, "BlockEntitySignTextStrictJsonFix", TypeReferences.field_211294_j, "Sign");
   }

   private Dynamic<?> func_209647_a(Dynamic<?> var1, String var2) {
      String var3 = var1.getString(var2);
      Object var4 = null;
      if (!"null".equals(var3) && !StringUtils.isEmpty(var3)) {
         if (var3.charAt(0) == '"' && var3.charAt(var3.length() - 1) == '"' || var3.charAt(0) == '{' && var3.charAt(var3.length() - 1) == '}') {
            try {
               var4 = (ITextComponent)JsonUtils.func_188176_a(field_188225_a, var3, ITextComponent.class, true);
               if (var4 == null) {
                  var4 = new TextComponentString("");
               }
            } catch (JsonParseException var8) {
            }

            if (var4 == null) {
               try {
                  var4 = ITextComponent.Serializer.func_150699_a(var3);
               } catch (JsonParseException var7) {
               }
            }

            if (var4 == null) {
               try {
                  var4 = ITextComponent.Serializer.func_186877_b(var3);
               } catch (JsonParseException var6) {
               }
            }

            if (var4 == null) {
               var4 = new TextComponentString(var3);
            }
         } else {
            var4 = new TextComponentString(var3);
         }
      } else {
         var4 = new TextComponentString("");
      }

      return var1.set(var2, var1.createString(ITextComponent.Serializer.func_150696_a((ITextComponent)var4)));
   }

   protected Typed<?> func_207419_a(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), (var1x) -> {
         var1x = this.func_209647_a(var1x, "Text1");
         var1x = this.func_209647_a(var1x, "Text2");
         var1x = this.func_209647_a(var1x, "Text3");
         var1x = this.func_209647_a(var1x, "Text4");
         return var1x;
      });
   }
}

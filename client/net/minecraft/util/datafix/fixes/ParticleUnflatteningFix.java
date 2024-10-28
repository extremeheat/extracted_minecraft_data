package net.minecraft.util.datafix.fixes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import org.slf4j.Logger;

public class ParticleUnflatteningFix extends DataFix {
   private static final Logger LOGGER = LogUtils.getLogger();

   public ParticleUnflatteningFix(Schema var1) {
      super(var1, true);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.PARTICLE);
      Type var2 = this.getOutputSchema().getType(References.PARTICLE);
      return this.writeFixAndRead("ParticleUnflatteningFix", var1, var2, this::fix);
   }

   private <T> Dynamic<T> fix(Dynamic<T> var1) {
      Optional var2 = var1.asString().result();
      if (var2.isEmpty()) {
         return var1;
      } else {
         String var3 = (String)var2.get();
         String[] var4 = var3.split(" ", 2);
         String var5 = NamespacedSchema.ensureNamespaced(var4[0]);
         Dynamic var6 = var1.createMap(Map.of(var1.createString("type"), var1.createString(var5)));
         Dynamic var10000;
         switch (var5) {
            case "minecraft:item":
               var10000 = var4.length > 1 ? this.updateItem(var6, var4[1]) : var6;
               break;
            case "minecraft:block":
            case "minecraft:block_marker":
            case "minecraft:falling_dust":
            case "minecraft:dust_pillar":
               var10000 = var4.length > 1 ? this.updateBlock(var6, var4[1]) : var6;
               break;
            case "minecraft:dust":
               var10000 = var4.length > 1 ? this.updateDust(var6, var4[1]) : var6;
               break;
            case "minecraft:dust_color_transition":
               var10000 = var4.length > 1 ? this.updateDustTransition(var6, var4[1]) : var6;
               break;
            case "minecraft:sculk_charge":
               var10000 = var4.length > 1 ? this.updateSculkCharge(var6, var4[1]) : var6;
               break;
            case "minecraft:vibration":
               var10000 = var4.length > 1 ? this.updateVibration(var6, var4[1]) : var6;
               break;
            case "minecraft:shriek":
               var10000 = var4.length > 1 ? this.updateShriek(var6, var4[1]) : var6;
               break;
            default:
               var10000 = var6;
         }

         return var10000;
      }
   }

   private <T> Dynamic<T> updateItem(Dynamic<T> var1, String var2) {
      int var3 = var2.indexOf("{");
      Dynamic var4 = var1.createMap(Map.of(var1.createString("Count"), var1.createInt(1)));
      if (var3 == -1) {
         var4 = var4.set("id", var1.createString(var2));
      } else {
         var4 = var4.set("id", var1.createString(var2.substring(0, var3)));
         CompoundTag var5 = parseTag(var2.substring(var3));
         if (var5 != null) {
            var4 = var4.set("tag", (new Dynamic(NbtOps.INSTANCE, var5)).convert(var1.getOps()));
         }
      }

      return var1.set("item", var4);
   }

   @Nullable
   private static CompoundTag parseTag(String var0) {
      try {
         return TagParser.parseTag(var0);
      } catch (Exception var2) {
         LOGGER.warn("Failed to parse tag: {}", var0, var2);
         return null;
      }
   }

   private <T> Dynamic<T> updateBlock(Dynamic<T> var1, String var2) {
      int var3 = var2.indexOf("[");
      Dynamic var4 = var1.emptyMap();
      if (var3 == -1) {
         var4 = var4.set("Name", var1.createString(NamespacedSchema.ensureNamespaced(var2)));
      } else {
         var4 = var4.set("Name", var1.createString(NamespacedSchema.ensureNamespaced(var2.substring(0, var3))));
         Map var5 = parseBlockProperties(var1, var2.substring(var3));
         if (!var5.isEmpty()) {
            var4 = var4.set("Properties", var1.createMap(var5));
         }
      }

      return var1.set("block_state", var4);
   }

   private static <T> Map<Dynamic<T>, Dynamic<T>> parseBlockProperties(Dynamic<T> var0, String var1) {
      try {
         HashMap var2 = new HashMap();
         StringReader var3 = new StringReader(var1);
         var3.expect('[');
         var3.skipWhitespace();

         while(var3.canRead() && var3.peek() != ']') {
            var3.skipWhitespace();
            String var4 = var3.readString();
            var3.skipWhitespace();
            var3.expect('=');
            var3.skipWhitespace();
            String var5 = var3.readString();
            var3.skipWhitespace();
            var2.put(var0.createString(var4), var0.createString(var5));
            if (var3.canRead()) {
               if (var3.peek() != ',') {
                  break;
               }

               var3.skip();
            }
         }

         var3.expect(']');
         return var2;
      } catch (Exception var6) {
         LOGGER.warn("Failed to parse block properties: {}", var1, var6);
         return Map.of();
      }
   }

   private static <T> Dynamic<T> readVector(Dynamic<T> var0, StringReader var1) throws CommandSyntaxException {
      float var2 = var1.readFloat();
      var1.expect(' ');
      float var3 = var1.readFloat();
      var1.expect(' ');
      float var4 = var1.readFloat();
      Stream var10001 = Stream.of(var2, var3, var4);
      Objects.requireNonNull(var0);
      return var0.createList(var10001.map(var0::createFloat));
   }

   private <T> Dynamic<T> updateDust(Dynamic<T> var1, String var2) {
      try {
         StringReader var3 = new StringReader(var2);
         Dynamic var4 = readVector(var1, var3);
         var3.expect(' ');
         float var5 = var3.readFloat();
         return var1.set("color", var4).set("scale", var1.createFloat(var5));
      } catch (Exception var6) {
         LOGGER.warn("Failed to parse particle options: {}", var2, var6);
         return var1;
      }
   }

   private <T> Dynamic<T> updateDustTransition(Dynamic<T> var1, String var2) {
      try {
         StringReader var3 = new StringReader(var2);
         Dynamic var4 = readVector(var1, var3);
         var3.expect(' ');
         float var5 = var3.readFloat();
         var3.expect(' ');
         Dynamic var6 = readVector(var1, var3);
         return var1.set("from_color", var4).set("to_color", var6).set("scale", var1.createFloat(var5));
      } catch (Exception var7) {
         LOGGER.warn("Failed to parse particle options: {}", var2, var7);
         return var1;
      }
   }

   private <T> Dynamic<T> updateSculkCharge(Dynamic<T> var1, String var2) {
      try {
         StringReader var3 = new StringReader(var2);
         float var4 = var3.readFloat();
         return var1.set("roll", var1.createFloat(var4));
      } catch (Exception var5) {
         LOGGER.warn("Failed to parse particle options: {}", var2, var5);
         return var1;
      }
   }

   private <T> Dynamic<T> updateVibration(Dynamic<T> var1, String var2) {
      try {
         StringReader var3 = new StringReader(var2);
         float var4 = (float)var3.readDouble();
         var3.expect(' ');
         float var5 = (float)var3.readDouble();
         var3.expect(' ');
         float var6 = (float)var3.readDouble();
         var3.expect(' ');
         int var7 = var3.readInt();
         Dynamic var8 = var1.createIntList(IntStream.of(new int[]{Mth.floor(var4), Mth.floor(var5), Mth.floor(var6)}));
         Dynamic var9 = var1.createMap(Map.of(var1.createString("type"), var1.createString("minecraft:block"), var1.createString("pos"), var8));
         return var1.set("destination", var9).set("arrival_in_ticks", var1.createInt(var7));
      } catch (Exception var10) {
         LOGGER.warn("Failed to parse particle options: {}", var2, var10);
         return var1;
      }
   }

   private <T> Dynamic<T> updateShriek(Dynamic<T> var1, String var2) {
      try {
         StringReader var3 = new StringReader(var2);
         int var4 = var3.readInt();
         return var1.set("delay", var1.createInt(var4));
      } catch (Exception var5) {
         LOGGER.warn("Failed to parse particle options: {}", var2, var5);
         return var1;
      }
   }
}

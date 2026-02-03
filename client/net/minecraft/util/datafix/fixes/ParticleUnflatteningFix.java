package net.minecraft.util.datafix.fixes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class ParticleUnflatteningFix extends DataFix {
   private static final Logger LOGGER = LogUtils.getLogger();

   public ParticleUnflatteningFix(final Schema outputSchema) {
      super(outputSchema, true);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> oldType = this.getInputSchema().getType(References.PARTICLE);
      Type<?> newType = this.getOutputSchema().getType(References.PARTICLE);
      return this.writeFixAndRead("ParticleUnflatteningFix", oldType, newType, this::fix);
   }

   private <T> Dynamic<T> fix(final Dynamic<T> input) {
      Optional<String> maybeString = input.asString().result();
      if (maybeString.isEmpty()) {
         return input;
      } else {
         String particleDescription = (String)maybeString.get();
         String[] parts = particleDescription.split(" ", 2);
         String id = NamespacedSchema.ensureNamespaced(parts[0]);
         Dynamic<T> result = input.createMap(Map.of(input.createString("type"), input.createString(id)));
         Dynamic var10000;
         switch (id) {
            case "minecraft:item":
               var10000 = parts.length > 1 ? this.updateItem(result, parts[1]) : result;
               break;
            case "minecraft:block":
            case "minecraft:block_marker":
            case "minecraft:falling_dust":
            case "minecraft:dust_pillar":
               var10000 = parts.length > 1 ? this.updateBlock(result, parts[1]) : result;
               break;
            case "minecraft:dust":
               var10000 = parts.length > 1 ? this.updateDust(result, parts[1]) : result;
               break;
            case "minecraft:dust_color_transition":
               var10000 = parts.length > 1 ? this.updateDustTransition(result, parts[1]) : result;
               break;
            case "minecraft:sculk_charge":
               var10000 = parts.length > 1 ? this.updateSculkCharge(result, parts[1]) : result;
               break;
            case "minecraft:vibration":
               var10000 = parts.length > 1 ? this.updateVibration(result, parts[1]) : result;
               break;
            case "minecraft:shriek":
               var10000 = parts.length > 1 ? this.updateShriek(result, parts[1]) : result;
               break;
            default:
               var10000 = result;
         }

         return var10000;
      }
   }

   private <T> Dynamic<T> updateItem(final Dynamic<T> result, final String contents) {
      int tagPartStart = contents.indexOf("{");
      Dynamic<T> itemStack = result.createMap(Map.of(result.createString("Count"), result.createInt(1)));
      if (tagPartStart == -1) {
         itemStack = itemStack.set("id", result.createString(contents));
      } else {
         itemStack = itemStack.set("id", result.createString(contents.substring(0, tagPartStart)));
         Dynamic<T> itemTag = parseTag(result.getOps(), contents.substring(tagPartStart));
         if (itemTag != null) {
            itemStack = itemStack.set("tag", itemTag);
         }
      }

      return result.set("item", itemStack);
   }

   private static <T> @Nullable Dynamic<T> parseTag(final DynamicOps<T> ops, final String contents) {
      try {
         return new Dynamic(ops, TagParser.create(ops).parseFully(contents));
      } catch (Exception e) {
         LOGGER.warn("Failed to parse tag: {}", contents, e);
         return null;
      }
   }

   private <T> Dynamic<T> updateBlock(final Dynamic<T> result, final String contents) {
      int statePartStart = contents.indexOf("[");
      Dynamic<T> blockState = result.emptyMap();
      if (statePartStart == -1) {
         blockState = blockState.set("Name", result.createString(NamespacedSchema.ensureNamespaced(contents)));
      } else {
         blockState = blockState.set("Name", result.createString(NamespacedSchema.ensureNamespaced(contents.substring(0, statePartStart))));
         Map<Dynamic<T>, Dynamic<T>> properties = parseBlockProperties(result, contents.substring(statePartStart));
         if (!properties.isEmpty()) {
            blockState = blockState.set("Properties", result.createMap(properties));
         }
      }

      return result.set("block_state", blockState);
   }

   private static <T> Map<Dynamic<T>, Dynamic<T>> parseBlockProperties(final Dynamic<T> dynamic, final String contents) {
      try {
         Map<Dynamic<T>, Dynamic<T>> result = new HashMap();
         StringReader reader = new StringReader(contents);
         reader.expect('[');
         reader.skipWhitespace();

         while(reader.canRead() && reader.peek() != ']') {
            reader.skipWhitespace();
            String key = reader.readString();
            reader.skipWhitespace();
            reader.expect('=');
            reader.skipWhitespace();
            String value = reader.readString();
            reader.skipWhitespace();
            result.put(dynamic.createString(key), dynamic.createString(value));
            if (reader.canRead()) {
               if (reader.peek() != ',') {
                  break;
               }

               reader.skip();
            }
         }

         reader.expect(']');
         return result;
      } catch (Exception e) {
         LOGGER.warn("Failed to parse block properties: {}", contents, e);
         return Map.of();
      }
   }

   private static <T> Dynamic<T> readVector(final Dynamic<T> result, final StringReader reader) throws CommandSyntaxException {
      float x = reader.readFloat();
      reader.expect(' ');
      float y = reader.readFloat();
      reader.expect(' ');
      float z = reader.readFloat();
      Stream var10001 = Stream.of(x, y, z);
      Objects.requireNonNull(result);
      return result.createList(var10001.map(result::createFloat));
   }

   private <T> Dynamic<T> updateDust(final Dynamic<T> result, final String contents) {
      try {
         StringReader reader = new StringReader(contents);
         Dynamic<T> vector = readVector(result, reader);
         reader.expect(' ');
         float scale = reader.readFloat();
         return result.set("color", vector).set("scale", result.createFloat(scale));
      } catch (Exception e) {
         LOGGER.warn("Failed to parse particle options: {}", contents, e);
         return result;
      }
   }

   private <T> Dynamic<T> updateDustTransition(final Dynamic<T> result, final String contents) {
      try {
         StringReader reader = new StringReader(contents);
         Dynamic<T> from = readVector(result, reader);
         reader.expect(' ');
         float scale = reader.readFloat();
         reader.expect(' ');
         Dynamic<T> to = readVector(result, reader);
         return result.set("from_color", from).set("to_color", to).set("scale", result.createFloat(scale));
      } catch (Exception e) {
         LOGGER.warn("Failed to parse particle options: {}", contents, e);
         return result;
      }
   }

   private <T> Dynamic<T> updateSculkCharge(final Dynamic<T> result, final String contents) {
      try {
         StringReader reader = new StringReader(contents);
         float roll = reader.readFloat();
         return result.set("roll", result.createFloat(roll));
      } catch (Exception e) {
         LOGGER.warn("Failed to parse particle options: {}", contents, e);
         return result;
      }
   }

   private <T> Dynamic<T> updateVibration(final Dynamic<T> result, final String contents) {
      try {
         StringReader reader = new StringReader(contents);
         float destX = (float)reader.readDouble();
         reader.expect(' ');
         float destY = (float)reader.readDouble();
         reader.expect(' ');
         float destZ = (float)reader.readDouble();
         reader.expect(' ');
         int arrivalInTicks = reader.readInt();
         Dynamic<T> blockPos = result.createIntList(IntStream.of(new int[]{Mth.floor(destX), Mth.floor(destY), Mth.floor(destZ)}));
         Dynamic<T> positionSource = result.createMap(Map.of(result.createString("type"), result.createString("minecraft:block"), result.createString("pos"), blockPos));
         return result.set("destination", positionSource).set("arrival_in_ticks", result.createInt(arrivalInTicks));
      } catch (Exception e) {
         LOGGER.warn("Failed to parse particle options: {}", contents, e);
         return result;
      }
   }

   private <T> Dynamic<T> updateShriek(final Dynamic<T> result, final String contents) {
      try {
         StringReader reader = new StringReader(contents);
         int delay = reader.readInt();
         return result.set("delay", result.createInt(delay));
      } catch (Exception e) {
         LOGGER.warn("Failed to parse particle options: {}", contents, e);
         return result;
      }
   }
}

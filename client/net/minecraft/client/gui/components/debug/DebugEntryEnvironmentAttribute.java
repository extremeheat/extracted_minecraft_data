package net.minecraft.client.gui.components.debug;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.util.ARGB;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeDebugFormat;
import net.minecraft.world.attribute.EnvironmentAttributeDebugger;
import net.minecraft.world.attribute.EnvironmentAttributeLayerValues;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.jspecify.annotations.Nullable;

public class DebugEntryEnvironmentAttribute implements DebugScreenEntry {
   private static final DebugGroup GROUP = DebugGroup.Builder.titled("Environment Attribute").build();
   private static final int MAX_LENGTH = 100;
   private static @Nullable EnvironmentAttribute<?> attribute;

   public DebugEntryEnvironmentAttribute() {
      super();
   }

   public static @Nullable EnvironmentAttribute<?> getAttribute() {
      return attribute;
   }

   public static void setAttribute(final @Nullable EnvironmentAttribute<?> attribute) {
      DebugEntryEnvironmentAttribute.attribute = attribute;
   }

   public boolean isAllowed(final boolean reducedDebugInfo) {
      return !reducedDebugInfo && Minecraft.getInstance().canUseRestrictedDebugFeatures();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      EnvironmentAttribute<?> attribute = DebugEntryEnvironmentAttribute.attribute;
      Minecraft minecraft = Minecraft.getInstance();
      if (attribute != null && minecraft.level != null) {
         displayer.addFactToGroup(GROUP, "Attribute", (fact) -> fact.value(attribute.toString()).text(" (").value(attribute.type().toString()).text(", ").value(attribute.isPositional() ? "positional" : "global").text(")"));
         Vec3 pos = minecraft.gameRenderer.mainCamera().position();
         EnvironmentAttributeLayerValues layerValues = EnvironmentAttributeDebugger.getValuePerLayer(minecraft.level, attribute, pos);

         for(EnvironmentAttributeLayerValues.LayerValue layer : layerValues.layers()) {
            if (layer.layerIndex() < 0) {
               addToGroup(displayer, minecraft.font, "Default", layer.value(), 100);
            } else {
               Font var10001 = minecraft.font;
               int var10002 = layer.layerIndex();
               String var11 = "Layer " + var10002;
               String var10003 = layer.kind();
               addTextToGroup(displayer, var10001, var11, var10003 + (layer.disabled() ? " [disabled]" : ""));
               if (layer.modifier() != null) {
                  addTextToGroup(displayer, minecraft.font, "  Modifier", layer.modifier());
               }

               addToGroup(displayer, minecraft.font, "  After layer", layer.value(), 100);
            }
         }

         addToGroup(displayer, minecraft.font, "Result", layerValues.finalValue(), 100);
         if (!attribute.isSyncable() && minecraft.getSingleplayerServer() == null) {
            addTextToGroup(displayer, minecraft.font, "Non-syncable", "Use /environmentattribute for accurate server sample");
         }

      }
   }

   private static void addTextToGroup(final DebugScreenDisplayer displayer, final Font font, final String name, final String value) {
      displayer.addToGroup(GROUP, new DebugLineWithColorSwatchRenderer(font, formatLine(name, value, 100).result(), (Integer)null));
   }

   private static void addToGroup(final DebugScreenDisplayer displayer, final Font font, final String name, final Object value, final int maxCharacters) {
      String formattedValue = EnvironmentAttributeDebugFormat.describeValue(value);
      DebugFact fact = formatLine(name, formattedValue, maxCharacters);
      Objects.requireNonNull(value);
      byte var9 = 0;
      Integer var10000;
      //$FF: var9->value
      //0->org/joml/Vector3fc
      //1->org/joml/Vector4fc
      switch (((Class)value).typeSwitch<invokedynamic>(value, var9)) {
         case 0:
            Vector3fc rgb = (Vector3fc)value;
            var10000 = ARGB.colorFromVector3f(rgb);
            break;
         case 1:
            Vector4fc argb = (Vector4fc)value;
            var10000 = ARGB.colorFromVector4f(argb);
            break;
         default:
            var10000 = null;
      }

      Integer color = var10000;
      displayer.addToGroup(GROUP, new DebugLineWithColorSwatchRenderer(font, fact.result(), color));
   }

   static DebugFact formatLine(final String name, final String value, final int maxCharacters) {
      String var10000 = name.replace('\n', ' ');
      String label = var10000.replace('\r', ' ') + ": ";
      String singleLineValue = value.replace('\n', ' ').replace('\r', ' ');
      int labelLength = label.codePointCount(0, label.length());
      int valueLength = singleLineValue.codePointCount(0, singleLineValue.length());
      if ((long)labelLength + (long)valueLength <= (long)maxCharacters) {
         return (new DebugFact()).text(label).value(singleLineValue);
      } else {
         int available = maxCharacters - 3;
         return labelLength > available ? (new DebugFact()).text(label.substring(0, label.offsetByCodePoints(0, available))).text("...") : (new DebugFact()).text(label).value(singleLineValue.substring(0, singleLineValue.offsetByCodePoints(0, available - labelLength))).text("...");
      }
   }
}

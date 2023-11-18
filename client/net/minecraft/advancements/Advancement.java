package net.minecraft.advancements;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.apache.commons.lang3.ArrayUtils;

public class Advancement {
   @Nullable
   private final Advancement parent;
   @Nullable
   private final DisplayInfo display;
   private final AdvancementRewards rewards;
   private final ResourceLocation id;
   private final Map<String, Criterion> criteria;
   private final String[][] requirements;
   private final Set<Advancement> children = Sets.newLinkedHashSet();
   private final Component chatComponent;
   private final boolean sendsTelemetryEvent;

   public Advancement(
      ResourceLocation var1,
      @Nullable Advancement var2,
      @Nullable DisplayInfo var3,
      AdvancementRewards var4,
      Map<String, Criterion> var5,
      String[][] var6,
      boolean var7
   ) {
      super();
      this.id = var1;
      this.display = var3;
      this.criteria = ImmutableMap.copyOf(var5);
      this.parent = var2;
      this.rewards = var4;
      this.requirements = var6;
      this.sendsTelemetryEvent = var7;
      if (var2 != null) {
         var2.addChild(this);
      }

      if (var3 == null) {
         this.chatComponent = Component.literal(var1.toString());
      } else {
         Component var8 = var3.getTitle();
         ChatFormatting var9 = var3.getFrame().getChatColor();
         MutableComponent var10 = ComponentUtils.mergeStyles(var8.copy(), Style.EMPTY.withColor(var9)).append("\n").append(var3.getDescription());
         MutableComponent var11 = var8.copy().withStyle(var1x -> var1x.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, var10)));
         this.chatComponent = ComponentUtils.wrapInSquareBrackets(var11).withStyle(var9);
      }
   }

   public Advancement.Builder deconstruct() {
      return new Advancement.Builder(
         this.parent == null ? null : this.parent.getId(), this.display, this.rewards, this.criteria, this.requirements, this.sendsTelemetryEvent
      );
   }

   @Nullable
   public Advancement getParent() {
      return this.parent;
   }

   public Advancement getRoot() {
      return getRoot(this);
   }

   public static Advancement getRoot(Advancement var0) {
      Advancement var1 = var0;

      while(true) {
         Advancement var2 = var1.getParent();
         if (var2 == null) {
            return var1;
         }

         var1 = var2;
      }
   }

   @Nullable
   public DisplayInfo getDisplay() {
      return this.display;
   }

   public boolean sendsTelemetryEvent() {
      return this.sendsTelemetryEvent;
   }

   public AdvancementRewards getRewards() {
      return this.rewards;
   }

   @Override
   public String toString() {
      return "SimpleAdvancement{id="
         + this.getId()
         + ", parent="
         + (this.parent == null ? "null" : this.parent.getId())
         + ", display="
         + this.display
         + ", rewards="
         + this.rewards
         + ", criteria="
         + this.criteria
         + ", requirements="
         + Arrays.deepToString(this.requirements)
         + ", sendsTelemetryEvent="
         + this.sendsTelemetryEvent
         + "}";
   }

   public Iterable<Advancement> getChildren() {
      return this.children;
   }

   public Map<String, Criterion> getCriteria() {
      return this.criteria;
   }

   public int getMaxCriteraRequired() {
      return this.requirements.length;
   }

   public void addChild(Advancement var1) {
      this.children.add(var1);
   }

   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Advancement)) {
         return false;
      } else {
         Advancement var2 = (Advancement)var1;
         return this.id.equals(var2.id);
      }
   }

   @Override
   public int hashCode() {
      return this.id.hashCode();
   }

   public String[][] getRequirements() {
      return this.requirements;
   }

   public Component getChatComponent() {
      return this.chatComponent;
   }

   public static class Builder {
      @Nullable
      private ResourceLocation parentId;
      @Nullable
      private Advancement parent;
      @Nullable
      private DisplayInfo display;
      private AdvancementRewards rewards = AdvancementRewards.EMPTY;
      private Map<String, Criterion> criteria = Maps.newLinkedHashMap();
      @Nullable
      private String[][] requirements;
      private RequirementsStrategy requirementsStrategy = RequirementsStrategy.AND;
      private final boolean sendsTelemetryEvent;

      Builder(@Nullable ResourceLocation var1, @Nullable DisplayInfo var2, AdvancementRewards var3, Map<String, Criterion> var4, String[][] var5, boolean var6) {
         super();
         this.parentId = var1;
         this.display = var2;
         this.rewards = var3;
         this.criteria = var4;
         this.requirements = var5;
         this.sendsTelemetryEvent = var6;
      }

      private Builder(boolean var1) {
         super();
         this.sendsTelemetryEvent = var1;
      }

      public static Advancement.Builder advancement() {
         return new Advancement.Builder(true);
      }

      public static Advancement.Builder recipeAdvancement() {
         return new Advancement.Builder(false);
      }

      public Advancement.Builder parent(Advancement var1) {
         this.parent = var1;
         return this;
      }

      public Advancement.Builder parent(ResourceLocation var1) {
         this.parentId = var1;
         return this;
      }

      public Advancement.Builder display(
         ItemStack var1, Component var2, Component var3, @Nullable ResourceLocation var4, FrameType var5, boolean var6, boolean var7, boolean var8
      ) {
         return this.display(new DisplayInfo(var1, var2, var3, var4, var5, var6, var7, var8));
      }

      public Advancement.Builder display(
         ItemLike var1, Component var2, Component var3, @Nullable ResourceLocation var4, FrameType var5, boolean var6, boolean var7, boolean var8
      ) {
         return this.display(new DisplayInfo(new ItemStack(var1.asItem()), var2, var3, var4, var5, var6, var7, var8));
      }

      public Advancement.Builder display(DisplayInfo var1) {
         this.display = var1;
         return this;
      }

      public Advancement.Builder rewards(AdvancementRewards.Builder var1) {
         return this.rewards(var1.build());
      }

      public Advancement.Builder rewards(AdvancementRewards var1) {
         this.rewards = var1;
         return this;
      }

      public Advancement.Builder addCriterion(String var1, CriterionTriggerInstance var2) {
         return this.addCriterion(var1, new Criterion(var2));
      }

      public Advancement.Builder addCriterion(String var1, Criterion var2) {
         if (this.criteria.containsKey(var1)) {
            throw new IllegalArgumentException("Duplicate criterion " + var1);
         } else {
            this.criteria.put(var1, var2);
            return this;
         }
      }

      public Advancement.Builder requirements(RequirementsStrategy var1) {
         this.requirementsStrategy = var1;
         return this;
      }

      public Advancement.Builder requirements(String[][] var1) {
         this.requirements = var1;
         return this;
      }

      public boolean canBuild(Function<ResourceLocation, Advancement> var1) {
         if (this.parentId == null) {
            return true;
         } else {
            if (this.parent == null) {
               this.parent = (Advancement)var1.apply(this.parentId);
            }

            return this.parent != null;
         }
      }

      public Advancement build(ResourceLocation var1) {
         if (!this.canBuild(var0 -> null)) {
            throw new IllegalStateException("Tried to build incomplete advancement!");
         } else {
            if (this.requirements == null) {
               this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
            }

            return new Advancement(var1, this.parent, this.display, this.rewards, this.criteria, this.requirements, this.sendsTelemetryEvent);
         }
      }

      public Advancement save(Consumer<Advancement> var1, String var2) {
         Advancement var3 = this.build(new ResourceLocation(var2));
         var1.accept(var3);
         return var3;
      }

      public JsonObject serializeToJson() {
         if (this.requirements == null) {
            this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
         }

         JsonObject var1 = new JsonObject();
         if (this.parent != null) {
            var1.addProperty("parent", this.parent.getId().toString());
         } else if (this.parentId != null) {
            var1.addProperty("parent", this.parentId.toString());
         }

         if (this.display != null) {
            var1.add("display", this.display.serializeToJson());
         }

         var1.add("rewards", this.rewards.serializeToJson());
         JsonObject var2 = new JsonObject();

         for(Entry var4 : this.criteria.entrySet()) {
            var2.add((String)var4.getKey(), ((Criterion)var4.getValue()).serializeToJson());
         }

         var1.add("criteria", var2);
         JsonArray var13 = new JsonArray();

         for(String[] var7 : this.requirements) {
            JsonArray var8 = new JsonArray();

            for(String var12 : var7) {
               var8.add(var12);
            }

            var13.add(var8);
         }

         var1.add("requirements", var13);
         var1.addProperty("sends_telemetry_event", this.sendsTelemetryEvent);
         return var1;
      }

      public void serializeToNetwork(FriendlyByteBuf var1) {
         if (this.requirements == null) {
            this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
         }

         var1.writeNullable(this.parentId, FriendlyByteBuf::writeResourceLocation);
         var1.writeNullable(this.display, (var0, var1x) -> var1x.serializeToNetwork(var0));
         Criterion.serializeToNetwork(this.criteria, var1);
         var1.writeVarInt(this.requirements.length);

         for(String[] var5 : this.requirements) {
            var1.writeVarInt(var5.length);

            for(String var9 : var5) {
               var1.writeUtf(var9);
            }
         }

         var1.writeBoolean(this.sendsTelemetryEvent);
      }

      @Override
      public String toString() {
         return "Task Advancement{parentId="
            + this.parentId
            + ", display="
            + this.display
            + ", rewards="
            + this.rewards
            + ", criteria="
            + this.criteria
            + ", requirements="
            + Arrays.deepToString(this.requirements)
            + ", sends_telemetry_event="
            + this.sendsTelemetryEvent
            + "}";
      }

      public static Advancement.Builder fromJson(JsonObject var0, DeserializationContext var1) {
         ResourceLocation var2 = var0.has("parent") ? new ResourceLocation(GsonHelper.getAsString(var0, "parent")) : null;
         DisplayInfo var3 = var0.has("display") ? DisplayInfo.fromJson(GsonHelper.getAsJsonObject(var0, "display")) : null;
         AdvancementRewards var4 = var0.has("rewards")
            ? AdvancementRewards.deserialize(GsonHelper.getAsJsonObject(var0, "rewards"))
            : AdvancementRewards.EMPTY;
         Map var5 = Criterion.criteriaFromJson(GsonHelper.getAsJsonObject(var0, "criteria"), var1);
         if (var5.isEmpty()) {
            throw new JsonSyntaxException("Advancement criteria cannot be empty");
         } else {
            JsonArray var6 = GsonHelper.getAsJsonArray(var0, "requirements", new JsonArray());
            String[][] var7 = new String[var6.size()][];

            for(int var8 = 0; var8 < var6.size(); ++var8) {
               JsonArray var9 = GsonHelper.convertToJsonArray(var6.get(var8), "requirements[" + var8 + "]");
               var7[var8] = new String[var9.size()];

               for(int var10 = 0; var10 < var9.size(); ++var10) {
                  var7[var8][var10] = GsonHelper.convertToString(var9.get(var10), "requirements[" + var8 + "][" + var10 + "]");
               }
            }

            if (var7.length == 0) {
               var7 = new String[var5.size()][];
               int var16 = 0;

               for(String var23 : var5.keySet()) {
                  var7[var16++] = new String[]{var23};
               }
            }

            for(String[] var11 : var7) {
               if (var11.length == 0 && var5.isEmpty()) {
                  throw new JsonSyntaxException("Requirement entry cannot be empty");
               }

               for(String var15 : var11) {
                  if (!var5.containsKey(var15)) {
                     throw new JsonSyntaxException("Unknown required criterion '" + var15 + "'");
                  }
               }
            }

            for(String var22 : var5.keySet()) {
               boolean var25 = false;

               for(String[] var29 : var7) {
                  if (ArrayUtils.contains(var29, var22)) {
                     var25 = true;
                     break;
                  }
               }

               if (!var25) {
                  throw new JsonSyntaxException(
                     "Criterion '" + var22 + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required."
                  );
               }
            }

            boolean var19 = GsonHelper.getAsBoolean(var0, "sends_telemetry_event", false);
            return new Advancement.Builder(var2, var3, var4, var5, var7, var19);
         }
      }

      public static Advancement.Builder fromNetwork(FriendlyByteBuf var0) {
         ResourceLocation var1 = var0.readNullable(FriendlyByteBuf::readResourceLocation);
         DisplayInfo var2 = var0.readNullable(DisplayInfo::fromNetwork);
         Map var3 = Criterion.criteriaFromNetwork(var0);
         String[][] var4 = new String[var0.readVarInt()][];

         for(int var5 = 0; var5 < var4.length; ++var5) {
            var4[var5] = new String[var0.readVarInt()];

            for(int var6 = 0; var6 < var4[var5].length; ++var6) {
               var4[var5][var6] = var0.readUtf();
            }
         }

         boolean var7 = var0.readBoolean();
         return new Advancement.Builder(var1, var2, AdvancementRewards.EMPTY, var3, var4, var7);
      }

      public Map<String, Criterion> getCriteria() {
         return this.criteria;
      }
   }
}

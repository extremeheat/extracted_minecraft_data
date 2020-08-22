package net.minecraft.advancements;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.apache.commons.lang3.ArrayUtils;

public class Advancement {
   private final Advancement parent;
   private final DisplayInfo display;
   private final AdvancementRewards rewards;
   private final ResourceLocation id;
   private final Map criteria;
   private final String[][] requirements;
   private final Set children = Sets.newLinkedHashSet();
   private final Component chatComponent;

   public Advancement(ResourceLocation var1, @Nullable Advancement var2, @Nullable DisplayInfo var3, AdvancementRewards var4, Map var5, String[][] var6) {
      this.id = var1;
      this.display = var3;
      this.criteria = ImmutableMap.copyOf(var5);
      this.parent = var2;
      this.rewards = var4;
      this.requirements = var6;
      if (var2 != null) {
         var2.addChild(this);
      }

      if (var3 == null) {
         this.chatComponent = new TextComponent(var1.toString());
      } else {
         Component var7 = var3.getTitle();
         ChatFormatting var8 = var3.getFrame().getChatColor();
         Component var9 = var7.deepCopy().withStyle(var8).append("\n").append(var3.getDescription());
         Component var10 = var7.deepCopy().withStyle((var1x) -> {
            var1x.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, var9));
         });
         this.chatComponent = (new TextComponent("[")).append(var10).append("]").withStyle(var8);
      }

   }

   public Advancement.Builder deconstruct() {
      return new Advancement.Builder(this.parent == null ? null : this.parent.getId(), this.display, this.rewards, this.criteria, this.requirements);
   }

   @Nullable
   public Advancement getParent() {
      return this.parent;
   }

   @Nullable
   public DisplayInfo getDisplay() {
      return this.display;
   }

   public AdvancementRewards getRewards() {
      return this.rewards;
   }

   public String toString() {
      return "SimpleAdvancement{id=" + this.getId() + ", parent=" + (this.parent == null ? "null" : this.parent.getId()) + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + '}';
   }

   public Iterable getChildren() {
      return this.children;
   }

   public Map getCriteria() {
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
      private ResourceLocation parentId;
      private Advancement parent;
      private DisplayInfo display;
      private AdvancementRewards rewards;
      private Map criteria;
      private String[][] requirements;
      private RequirementsStrategy requirementsStrategy;

      private Builder(@Nullable ResourceLocation var1, @Nullable DisplayInfo var2, AdvancementRewards var3, Map var4, String[][] var5) {
         this.rewards = AdvancementRewards.EMPTY;
         this.criteria = Maps.newLinkedHashMap();
         this.requirementsStrategy = RequirementsStrategy.AND;
         this.parentId = var1;
         this.display = var2;
         this.rewards = var3;
         this.criteria = var4;
         this.requirements = var5;
      }

      private Builder() {
         this.rewards = AdvancementRewards.EMPTY;
         this.criteria = Maps.newLinkedHashMap();
         this.requirementsStrategy = RequirementsStrategy.AND;
      }

      public static Advancement.Builder advancement() {
         return new Advancement.Builder();
      }

      public Advancement.Builder parent(Advancement var1) {
         this.parent = var1;
         return this;
      }

      public Advancement.Builder parent(ResourceLocation var1) {
         this.parentId = var1;
         return this;
      }

      public Advancement.Builder display(ItemStack var1, Component var2, Component var3, @Nullable ResourceLocation var4, FrameType var5, boolean var6, boolean var7, boolean var8) {
         return this.display(new DisplayInfo(var1, var2, var3, var4, var5, var6, var7, var8));
      }

      public Advancement.Builder display(ItemLike var1, Component var2, Component var3, @Nullable ResourceLocation var4, FrameType var5, boolean var6, boolean var7, boolean var8) {
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

      public boolean canBuild(Function var1) {
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
         if (!this.canBuild((var0) -> {
            return null;
         })) {
            throw new IllegalStateException("Tried to build incomplete advancement!");
         } else {
            if (this.requirements == null) {
               this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
            }

            return new Advancement(var1, this.parent, this.display, this.rewards, this.criteria, this.requirements);
         }
      }

      public Advancement save(Consumer var1, String var2) {
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
         Iterator var3 = this.criteria.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            var2.add((String)var4.getKey(), ((Criterion)var4.getValue()).serializeToJson());
         }

         var1.add("criteria", var2);
         JsonArray var13 = new JsonArray();
         String[][] var14 = this.requirements;
         int var5 = var14.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String[] var7 = var14[var6];
            JsonArray var8 = new JsonArray();
            String[] var9 = var7;
            int var10 = var7.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               String var12 = var9[var11];
               var8.add(var12);
            }

            var13.add(var8);
         }

         var1.add("requirements", var13);
         return var1;
      }

      public void serializeToNetwork(FriendlyByteBuf var1) {
         if (this.parentId == null) {
            var1.writeBoolean(false);
         } else {
            var1.writeBoolean(true);
            var1.writeResourceLocation(this.parentId);
         }

         if (this.display == null) {
            var1.writeBoolean(false);
         } else {
            var1.writeBoolean(true);
            this.display.serializeToNetwork(var1);
         }

         Criterion.serializeToNetwork(this.criteria, var1);
         var1.writeVarInt(this.requirements.length);
         String[][] var2 = this.requirements;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String[] var5 = var2[var4];
            var1.writeVarInt(var5.length);
            String[] var6 = var5;
            int var7 = var5.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               String var9 = var6[var8];
               var1.writeUtf(var9);
            }
         }

      }

      public String toString() {
         return "Task Advancement{parentId=" + this.parentId + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + '}';
      }

      public static Advancement.Builder fromJson(JsonObject var0, JsonDeserializationContext var1) {
         ResourceLocation var2 = var0.has("parent") ? new ResourceLocation(GsonHelper.getAsString(var0, "parent")) : null;
         DisplayInfo var3 = var0.has("display") ? DisplayInfo.fromJson(GsonHelper.getAsJsonObject(var0, "display"), var1) : null;
         AdvancementRewards var4 = (AdvancementRewards)GsonHelper.getAsObject(var0, "rewards", AdvancementRewards.EMPTY, var1, AdvancementRewards.class);
         Map var5 = Criterion.criteriaFromJson(GsonHelper.getAsJsonObject(var0, "criteria"), var1);
         if (var5.isEmpty()) {
            throw new JsonSyntaxException("Advancement criteria cannot be empty");
         } else {
            JsonArray var6 = GsonHelper.getAsJsonArray(var0, "requirements", new JsonArray());
            String[][] var7 = new String[var6.size()][];

            int var8;
            int var10;
            for(var8 = 0; var8 < var6.size(); ++var8) {
               JsonArray var9 = GsonHelper.convertToJsonArray(var6.get(var8), "requirements[" + var8 + "]");
               var7[var8] = new String[var9.size()];

               for(var10 = 0; var10 < var9.size(); ++var10) {
                  var7[var8][var10] = GsonHelper.convertToString(var9.get(var10), "requirements[" + var8 + "][" + var10 + "]");
               }
            }

            if (var7.length == 0) {
               var7 = new String[var5.size()][];
               var8 = 0;

               String var21;
               for(Iterator var16 = var5.keySet().iterator(); var16.hasNext(); var7[var8++] = new String[]{var21}) {
                  var21 = (String)var16.next();
               }
            }

            String[][] var17 = var7;
            int var18 = var7.length;

            int var13;
            for(var10 = 0; var10 < var18; ++var10) {
               String[] var11 = var17[var10];
               if (var11.length == 0 && var5.isEmpty()) {
                  throw new JsonSyntaxException("Requirement entry cannot be empty");
               }

               String[] var12 = var11;
               var13 = var11.length;

               for(int var14 = 0; var14 < var13; ++var14) {
                  String var15 = var12[var14];
                  if (!var5.containsKey(var15)) {
                     throw new JsonSyntaxException("Unknown required criterion '" + var15 + "'");
                  }
               }
            }

            Iterator var19 = var5.keySet().iterator();

            String var20;
            boolean var23;
            do {
               if (!var19.hasNext()) {
                  return new Advancement.Builder(var2, var3, var4, var5, var7);
               }

               var20 = (String)var19.next();
               var23 = false;
               String[][] var22 = var7;
               int var24 = var7.length;

               for(var13 = 0; var13 < var24; ++var13) {
                  String[] var25 = var22[var13];
                  if (ArrayUtils.contains(var25, var20)) {
                     var23 = true;
                     break;
                  }
               }
            } while(var23);

            throw new JsonSyntaxException("Criterion '" + var20 + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required.");
         }
      }

      public static Advancement.Builder fromNetwork(FriendlyByteBuf var0) {
         ResourceLocation var1 = var0.readBoolean() ? var0.readResourceLocation() : null;
         DisplayInfo var2 = var0.readBoolean() ? DisplayInfo.fromNetwork(var0) : null;
         Map var3 = Criterion.criteriaFromNetwork(var0);
         String[][] var4 = new String[var0.readVarInt()][];

         for(int var5 = 0; var5 < var4.length; ++var5) {
            var4[var5] = new String[var0.readVarInt()];

            for(int var6 = 0; var6 < var4[var5].length; ++var6) {
               var4[var5][var6] = var0.readUtf(32767);
            }
         }

         return new Advancement.Builder(var1, var2, AdvancementRewards.EMPTY, var3, var4);
      }

      public Map getCriteria() {
         return this.criteria;
      }

      // $FF: synthetic method
      Builder(ResourceLocation var1, DisplayInfo var2, AdvancementRewards var3, Map var4, String[][] var5, Object var6) {
         this(var1, var2, var3, var4, var5);
      }
   }
}

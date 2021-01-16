package com.google.gson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class JsonArray extends JsonElement implements Iterable<JsonElement> {
   private final List<JsonElement> elements = new ArrayList();

   public JsonArray() {
      super();
   }

   JsonArray deepCopy() {
      JsonArray var1 = new JsonArray();
      Iterator var2 = this.elements.iterator();

      while(var2.hasNext()) {
         JsonElement var3 = (JsonElement)var2.next();
         var1.add(var3.deepCopy());
      }

      return var1;
   }

   public void add(Boolean var1) {
      this.elements.add(var1 == null ? JsonNull.INSTANCE : new JsonPrimitive(var1));
   }

   public void add(Character var1) {
      this.elements.add(var1 == null ? JsonNull.INSTANCE : new JsonPrimitive(var1));
   }

   public void add(Number var1) {
      this.elements.add(var1 == null ? JsonNull.INSTANCE : new JsonPrimitive(var1));
   }

   public void add(String var1) {
      this.elements.add(var1 == null ? JsonNull.INSTANCE : new JsonPrimitive(var1));
   }

   public void add(JsonElement var1) {
      if (var1 == null) {
         var1 = JsonNull.INSTANCE;
      }

      this.elements.add(var1);
   }

   public void addAll(JsonArray var1) {
      this.elements.addAll(var1.elements);
   }

   public JsonElement set(int var1, JsonElement var2) {
      return (JsonElement)this.elements.set(var1, var2);
   }

   public boolean remove(JsonElement var1) {
      return this.elements.remove(var1);
   }

   public JsonElement remove(int var1) {
      return (JsonElement)this.elements.remove(var1);
   }

   public boolean contains(JsonElement var1) {
      return this.elements.contains(var1);
   }

   public int size() {
      return this.elements.size();
   }

   public Iterator<JsonElement> iterator() {
      return this.elements.iterator();
   }

   public JsonElement get(int var1) {
      return (JsonElement)this.elements.get(var1);
   }

   public Number getAsNumber() {
      if (this.elements.size() == 1) {
         return ((JsonElement)this.elements.get(0)).getAsNumber();
      } else {
         throw new IllegalStateException();
      }
   }

   public String getAsString() {
      if (this.elements.size() == 1) {
         return ((JsonElement)this.elements.get(0)).getAsString();
      } else {
         throw new IllegalStateException();
      }
   }

   public double getAsDouble() {
      if (this.elements.size() == 1) {
         return ((JsonElement)this.elements.get(0)).getAsDouble();
      } else {
         throw new IllegalStateException();
      }
   }

   public BigDecimal getAsBigDecimal() {
      if (this.elements.size() == 1) {
         return ((JsonElement)this.elements.get(0)).getAsBigDecimal();
      } else {
         throw new IllegalStateException();
      }
   }

   public BigInteger getAsBigInteger() {
      if (this.elements.size() == 1) {
         return ((JsonElement)this.elements.get(0)).getAsBigInteger();
      } else {
         throw new IllegalStateException();
      }
   }

   public float getAsFloat() {
      if (this.elements.size() == 1) {
         return ((JsonElement)this.elements.get(0)).getAsFloat();
      } else {
         throw new IllegalStateException();
      }
   }

   public long getAsLong() {
      if (this.elements.size() == 1) {
         return ((JsonElement)this.elements.get(0)).getAsLong();
      } else {
         throw new IllegalStateException();
      }
   }

   public int getAsInt() {
      if (this.elements.size() == 1) {
         return ((JsonElement)this.elements.get(0)).getAsInt();
      } else {
         throw new IllegalStateException();
      }
   }

   public byte getAsByte() {
      if (this.elements.size() == 1) {
         return ((JsonElement)this.elements.get(0)).getAsByte();
      } else {
         throw new IllegalStateException();
      }
   }

   public char getAsCharacter() {
      if (this.elements.size() == 1) {
         return ((JsonElement)this.elements.get(0)).getAsCharacter();
      } else {
         throw new IllegalStateException();
      }
   }

   public short getAsShort() {
      if (this.elements.size() == 1) {
         return ((JsonElement)this.elements.get(0)).getAsShort();
      } else {
         throw new IllegalStateException();
      }
   }

   public boolean getAsBoolean() {
      if (this.elements.size() == 1) {
         return ((JsonElement)this.elements.get(0)).getAsBoolean();
      } else {
         throw new IllegalStateException();
      }
   }

   public boolean equals(Object var1) {
      return var1 == this || var1 instanceof JsonArray && ((JsonArray)var1).elements.equals(this.elements);
   }

   public int hashCode() {
      return this.elements.hashCode();
   }
}

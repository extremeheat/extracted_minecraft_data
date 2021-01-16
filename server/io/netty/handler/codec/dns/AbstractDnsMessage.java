package io.netty.handler.codec.dns;

import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractDnsMessage extends AbstractReferenceCounted implements DnsMessage {
   private static final ResourceLeakDetector<DnsMessage> leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(DnsMessage.class);
   private static final int SECTION_QUESTION;
   private static final int SECTION_COUNT = 4;
   private final ResourceLeakTracker<DnsMessage> leak;
   private short id;
   private DnsOpCode opCode;
   private boolean recursionDesired;
   private byte z;
   private Object questions;
   private Object answers;
   private Object authorities;
   private Object additionals;

   protected AbstractDnsMessage(int var1) {
      this(var1, DnsOpCode.QUERY);
   }

   protected AbstractDnsMessage(int var1, DnsOpCode var2) {
      super();
      this.leak = leakDetector.track(this);
      this.setId(var1);
      this.setOpCode(var2);
   }

   public int id() {
      return this.id & '\uffff';
   }

   public DnsMessage setId(int var1) {
      this.id = (short)var1;
      return this;
   }

   public DnsOpCode opCode() {
      return this.opCode;
   }

   public DnsMessage setOpCode(DnsOpCode var1) {
      this.opCode = (DnsOpCode)ObjectUtil.checkNotNull(var1, "opCode");
      return this;
   }

   public boolean isRecursionDesired() {
      return this.recursionDesired;
   }

   public DnsMessage setRecursionDesired(boolean var1) {
      this.recursionDesired = var1;
      return this;
   }

   public int z() {
      return this.z;
   }

   public DnsMessage setZ(int var1) {
      this.z = (byte)(var1 & 7);
      return this;
   }

   public int count(DnsSection var1) {
      return this.count(sectionOrdinal(var1));
   }

   private int count(int var1) {
      Object var2 = this.sectionAt(var1);
      if (var2 == null) {
         return 0;
      } else if (var2 instanceof DnsRecord) {
         return 1;
      } else {
         List var3 = (List)var2;
         return var3.size();
      }
   }

   public int count() {
      int var1 = 0;

      for(int var2 = 0; var2 < 4; ++var2) {
         var1 += this.count(var2);
      }

      return var1;
   }

   public <T extends DnsRecord> T recordAt(DnsSection var1) {
      return this.recordAt(sectionOrdinal(var1));
   }

   private <T extends DnsRecord> T recordAt(int var1) {
      Object var2 = this.sectionAt(var1);
      if (var2 == null) {
         return null;
      } else if (var2 instanceof DnsRecord) {
         return castRecord(var2);
      } else {
         List var3 = (List)var2;
         return var3.isEmpty() ? null : castRecord(var3.get(0));
      }
   }

   public <T extends DnsRecord> T recordAt(DnsSection var1, int var2) {
      return this.recordAt(sectionOrdinal(var1), var2);
   }

   private <T extends DnsRecord> T recordAt(int var1, int var2) {
      Object var3 = this.sectionAt(var1);
      if (var3 == null) {
         throw new IndexOutOfBoundsException("index: " + var2 + " (expected: none)");
      } else if (var3 instanceof DnsRecord) {
         if (var2 == 0) {
            return castRecord(var3);
         } else {
            throw new IndexOutOfBoundsException("index: " + var2 + "' (expected: 0)");
         }
      } else {
         List var4 = (List)var3;
         return castRecord(var4.get(var2));
      }
   }

   public DnsMessage setRecord(DnsSection var1, DnsRecord var2) {
      this.setRecord(sectionOrdinal(var1), var2);
      return this;
   }

   private void setRecord(int var1, DnsRecord var2) {
      this.clear(var1);
      this.setSection(var1, checkQuestion(var1, var2));
   }

   public <T extends DnsRecord> T setRecord(DnsSection var1, int var2, DnsRecord var3) {
      return this.setRecord(sectionOrdinal(var1), var2, var3);
   }

   private <T extends DnsRecord> T setRecord(int var1, int var2, DnsRecord var3) {
      checkQuestion(var1, var3);
      Object var4 = this.sectionAt(var1);
      if (var4 == null) {
         throw new IndexOutOfBoundsException("index: " + var2 + " (expected: none)");
      } else if (var4 instanceof DnsRecord) {
         if (var2 == 0) {
            this.setSection(var1, var3);
            return castRecord(var4);
         } else {
            throw new IndexOutOfBoundsException("index: " + var2 + " (expected: 0)");
         }
      } else {
         List var5 = (List)var4;
         return castRecord(var5.set(var2, var3));
      }
   }

   public DnsMessage addRecord(DnsSection var1, DnsRecord var2) {
      this.addRecord(sectionOrdinal(var1), var2);
      return this;
   }

   private void addRecord(int var1, DnsRecord var2) {
      checkQuestion(var1, var2);
      Object var3 = this.sectionAt(var1);
      if (var3 == null) {
         this.setSection(var1, var2);
      } else if (var3 instanceof DnsRecord) {
         ArrayList var5 = newRecordList();
         var5.add(castRecord(var3));
         var5.add(var2);
         this.setSection(var1, var5);
      } else {
         List var4 = (List)var3;
         var4.add(var2);
      }
   }

   public DnsMessage addRecord(DnsSection var1, int var2, DnsRecord var3) {
      this.addRecord(sectionOrdinal(var1), var2, var3);
      return this;
   }

   private void addRecord(int var1, int var2, DnsRecord var3) {
      checkQuestion(var1, var3);
      Object var4 = this.sectionAt(var1);
      if (var4 == null) {
         if (var2 != 0) {
            throw new IndexOutOfBoundsException("index: " + var2 + " (expected: 0)");
         } else {
            this.setSection(var1, var3);
         }
      } else if (var4 instanceof DnsRecord) {
         ArrayList var6;
         if (var2 == 0) {
            var6 = newRecordList();
            var6.add(var3);
            var6.add(castRecord(var4));
         } else {
            if (var2 != 1) {
               throw new IndexOutOfBoundsException("index: " + var2 + " (expected: 0 or 1)");
            }

            var6 = newRecordList();
            var6.add(castRecord(var4));
            var6.add(var3);
         }

         this.setSection(var1, var6);
      } else {
         List var5 = (List)var4;
         var5.add(var2, var3);
      }
   }

   public <T extends DnsRecord> T removeRecord(DnsSection var1, int var2) {
      return this.removeRecord(sectionOrdinal(var1), var2);
   }

   private <T extends DnsRecord> T removeRecord(int var1, int var2) {
      Object var3 = this.sectionAt(var1);
      if (var3 == null) {
         throw new IndexOutOfBoundsException("index: " + var2 + " (expected: none)");
      } else if (var3 instanceof DnsRecord) {
         if (var2 != 0) {
            throw new IndexOutOfBoundsException("index: " + var2 + " (expected: 0)");
         } else {
            DnsRecord var5 = castRecord(var3);
            this.setSection(var1, (Object)null);
            return var5;
         }
      } else {
         List var4 = (List)var3;
         return castRecord(var4.remove(var2));
      }
   }

   public DnsMessage clear(DnsSection var1) {
      this.clear(sectionOrdinal(var1));
      return this;
   }

   public DnsMessage clear() {
      for(int var1 = 0; var1 < 4; ++var1) {
         this.clear(var1);
      }

      return this;
   }

   private void clear(int var1) {
      Object var2 = this.sectionAt(var1);
      this.setSection(var1, (Object)null);
      if (var2 instanceof ReferenceCounted) {
         ((ReferenceCounted)var2).release();
      } else if (var2 instanceof List) {
         List var3 = (List)var2;
         if (!var3.isEmpty()) {
            Iterator var4 = var3.iterator();

            while(var4.hasNext()) {
               Object var5 = var4.next();
               ReferenceCountUtil.release(var5);
            }
         }
      }

   }

   public DnsMessage touch() {
      return (DnsMessage)super.touch();
   }

   public DnsMessage touch(Object var1) {
      if (this.leak != null) {
         this.leak.record(var1);
      }

      return this;
   }

   public DnsMessage retain() {
      return (DnsMessage)super.retain();
   }

   public DnsMessage retain(int var1) {
      return (DnsMessage)super.retain(var1);
   }

   protected void deallocate() {
      this.clear();
      ResourceLeakTracker var1 = this.leak;
      if (var1 != null) {
         boolean var2 = var1.close(this);

         assert var2;
      }

   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof DnsMessage)) {
         return false;
      } else {
         DnsMessage var2 = (DnsMessage)var1;
         if (this.id() != var2.id()) {
            return false;
         } else {
            if (this instanceof DnsQuery) {
               if (!(var2 instanceof DnsQuery)) {
                  return false;
               }
            } else if (var2 instanceof DnsQuery) {
               return false;
            }

            return true;
         }
      }
   }

   public int hashCode() {
      return this.id() * 31 + (this instanceof DnsQuery ? 0 : 1);
   }

   private Object sectionAt(int var1) {
      switch(var1) {
      case 0:
         return this.questions;
      case 1:
         return this.answers;
      case 2:
         return this.authorities;
      case 3:
         return this.additionals;
      default:
         throw new Error();
      }
   }

   private void setSection(int var1, Object var2) {
      switch(var1) {
      case 0:
         this.questions = var2;
         return;
      case 1:
         this.answers = var2;
         return;
      case 2:
         this.authorities = var2;
         return;
      case 3:
         this.additionals = var2;
         return;
      default:
         throw new Error();
      }
   }

   private static int sectionOrdinal(DnsSection var0) {
      return ((DnsSection)ObjectUtil.checkNotNull(var0, "section")).ordinal();
   }

   private static DnsRecord checkQuestion(int var0, DnsRecord var1) {
      if (var0 == SECTION_QUESTION && !(ObjectUtil.checkNotNull(var1, "record") instanceof DnsQuestion)) {
         throw new IllegalArgumentException("record: " + var1 + " (expected: " + StringUtil.simpleClassName(DnsQuestion.class) + ')');
      } else {
         return var1;
      }
   }

   private static <T extends DnsRecord> T castRecord(Object var0) {
      return (DnsRecord)var0;
   }

   private static ArrayList<DnsRecord> newRecordList() {
      return new ArrayList(2);
   }

   static {
      SECTION_QUESTION = DnsSection.QUESTION.ordinal();
   }
}

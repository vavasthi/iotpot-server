/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.hubbleconnected.server.test.patching;

import com.iotpot.server.common.annotations.SkipPatching;
import com.iotpot.server.entity.PatchableEntity;

/**
 * Created by vinay on 1/11/16.
 */
public class TestPatchableObject extends PatchableEntity {
  private int i;
  private long l;
  private String s;
  private float f;
  private double d;
  private String skipped;

  public TestPatchableObject(int i, long l, String s, float f, double d, String skipped) {
    this.i = i;
    this.l = l;
    this.s = s;
    this.f = f;
    this.d = d;
    this.skipped = skipped;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = i;
    result = 31 * result + (int) (l ^ (l >>> 32));
    result = 31 * result + s.hashCode();
    result = 31 * result + (f != +0.0f ? Float.floatToIntBits(f) : 0);
    temp = Double.doubleToLongBits(d);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    TestPatchableObject that = (TestPatchableObject) o;

    if (i != that.i)
      return false;
    if (l != that.l)
      return false;
    if (Float.compare(that.f, f) != 0)
      return false;
    if (Double.compare(that.d, d) != 0)
      return false;
    return s.equals(that.s);

  }

  @Override
  public String toString() {
    return "TestPatchableObject{" +
        "i=" + i +
        ", l=" + l +
        ", s='" + s + '\'' +
        ", f=" + f +
        ", d=" + d +
        ", skipped=" + skipped +
        '}';
  }

  public int getI() {
    return i;
  }

  public void setI(int i) {
    this.i = i;
  }

  public long getL() {
    return l;
  }

  public void setL(long l) {
    this.l = l;
  }

  public String getS() {
    return s;
  }

  public void setS(String s) {
    this.s = s;
  }

  public float getF() {
    return f;
  }

  public void setF(float f) {
    this.f = f;
  }

  public double getD() {
    return d;
  }

  public void setD(double d) {
    this.d = d;
  }

  @SkipPatching
  public String getSkipped() {
    return skipped;
  }

  public void setSkipped(String skipped) {
    this.skipped = skipped;
  }

  private void dummyPrivateMethod() {
    // Does nothing..
  }
}

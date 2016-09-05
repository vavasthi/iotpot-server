package com.hubbleconnected.server.test.patching;/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

import com.iotpot.server.mapper.ObjectPatcher;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotEquals;

/**
 * Created by vinay on 1/11/16.
 */

public class ObjectPatcherTest {

  Logger logger = Logger.getLogger(ObjectPatcher.class.getName());

  @Test
  public void testName() throws Exception {

    TestPatchableObject tpo1 = new TestPatchableObject(5, 10, "My String", 4.5F, 6.7, "Old Skipped String");
    System.out.println("First object is " + tpo1.toString());
    TestPatchableObject tpo2 = new TestPatchableObject(10, 15, "His String", 6.7F, 7.8, "New Skipped String");
    System.out.println("Second object is " + tpo2.toString());


    assertNotEquals(tpo1, tpo2);
    ObjectPatcher.diffAndPatch(tpo1, tpo2);
    System.out.println("First object is " + tpo1.toString());
    System.out.println("Second object is " + tpo2.toString());
    Assert.assertEquals(tpo1, tpo2);
  }
}

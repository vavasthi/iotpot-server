/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.test.policies.strings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iotpot.server.common.policies.strings.Qualifier;
import com.iotpot.server.common.policies.strings.StringPolicy;
import com.iotpot.server.common.policies.strings.StringPolicyComponent;
import com.iotpot.server.common.policies.strings.StringPolicyManager;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Created by vinay on 2/9/16.
 */
public class TestStringPolicies {

  Logger logger = Logger.getLogger(TestStringPolicies.class);

  @BeforeClass
  private void setup() {

    List<StringPolicyComponent> policyComponentList = new ArrayList<>();
    policyComponentList.add(new StringPolicyComponent("[A-Z]", "atleast", 1));
    policyComponentList.add(new StringPolicyComponent("[0-9]", "atmost", 3));
    policyComponentList.add(new StringPolicyComponent("[\\p{Punct}]", "atleast", 1));
    policyComponentList.add(new StringPolicyComponent("[a-z]", "atleast", 5));
    StringPolicy policy
        = new StringPolicy(Qualifier.EXACTLY,
        4,
        policyComponentList.toArray(new StringPolicyComponent[policyComponentList.size()]));
    Gson gson = new GsonBuilder().create();
    String policyString = gson.toJson(policy);
    System.out.println(policyString);
    StringPolicyManager.INSTANCE.loadPolicies(policyString);
    StringPolicyManager.INSTANCE.print(System.out);
  }
  @Test(dataProvider = "providePasswords")
  public void testStringPolicies(String password, Boolean expected) throws NoSuchPaddingException,
      UnsupportedEncodingException,
      IllegalBlockSizeException,
      BadPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidKeyException, InvalidKeySpecException, InvalidAlgorithmParameterException {

    runWithPassword(password, expected);
  }
  @DataProvider(name = "providePasswords")
  public Object[][] provideData() {

      return new Object[][] {
          {"Avinay936,#", true},
          {"AVinay936.", false},
          {"AVinzay936.", true},
          {"AVinay977836.", false},
          {"AVinaysd936.%", true},
          {"avinay936.%", false},
          {"AVINAYasdsf936.&", true},
          {"inasdsdQ936$$*", true},
          {"AVina936ddf999$$", false}
      };
    }

  private String generateRandomText() {
    Random r = new Random((new Date()).getTime());
    int size = r.nextInt(32) + 8;
    byte[] buffer = new byte[size];
    r.nextBytes(buffer);
    return Base64.encodeBase64String(buffer);
  }
  private void runWithPassword(String password, boolean result) {
    System.out.println(String.format("Testing with %s", password));
    assertEquals(StringPolicyManager.INSTANCE.evaluate(password), result);
  }
}

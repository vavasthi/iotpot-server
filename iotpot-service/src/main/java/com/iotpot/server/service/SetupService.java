/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.service;

import com.iotpot.server.dao.*;
import com.iotpot.server.pojos.constants.IoTPotConstants;
import com.iotpot.server.common.utils.IoTPotPasswordEncryptionManager;
import com.iotpot.server.common.enums.Role;
import com.iotpot.server.entity.AccountEntity;
import com.iotpot.server.entity.DataCenterEntity;
import com.iotpot.server.entity.RoleEntity;
import com.iotpot.server.entity.TenantEntity;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SetupService {

  private static final Logger logger = Logger.getLogger(SetupService.class);
  @Autowired
  private DataCenterDao dataCenterDao;
  @Autowired
  private TenantDao tenantDao;
  @Autowired
  private AccountDao accountDao;
  @Autowired
  private RoleDao roleDao;
  @Autowired
  private ApplianceDao applianceDao;

  @Transactional
  public String setup() throws NoSuchPaddingException, InvalidKeySpecException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidParameterSpecException {
    return checkAndInitializeSeedData();
  }

  @Transactional
  public String unsetup() throws NoSuchPaddingException, InvalidKeySpecException, UnsupportedEncodingException,
          IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidParameterSpecException {
    return deleteSeedData();
  }

  private String checkAndInitializeSeedData() throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidKeyException, InvalidKeySpecException {

    StringBuffer sb = new StringBuffer();
    Set<DataCenterEntity> computeRegions = new HashSet<>();
    if (dataCenterDao.count() == 0) {

      logger.log(Level.INFO, "No compute regions found.. Creating...");
      sb.append("No compute regions found.. Creating...\n");
      DataCenterEntity dataCenterEntity = new DataCenterEntity(IoTPotConstants.IOTPOT_DEFAULT_COMPUTE_REGION_NAME,
              IoTPotConstants.IOTPOT_DEFAULT_IDENTITY_URL,
              IoTPotConstants.IOTPOT_DEFAULT_API_URL,
              IoTPotConstants.IOTPOT_DEFAULT_CS_URL,
              IoTPotConstants.IOTPOT_DEFAULT_STUN_URL,
              IoTPotConstants.IOTPOT_DEFAULT_MQTT_URL,
              IoTPotConstants.IOTPOT_DEFAULT_NTP_URL);
      dataCenterDao.save(dataCenterEntity);
      computeRegions.add(dataCenterEntity);
    }
    TenantEntity te = tenantDao.findByDiscriminator(IoTPotConstants.IOTPOT_INTERNAL_TENANT);
    if (te == null) {

      sb.append("No default tenant found.. Creating...\n");
      logger.log(Level.INFO, "No default tenant found.. Creating...");
      te = new TenantEntity(IoTPotConstants.IOTPOT_INTERNAL_TENANT,
              IoTPotConstants.IOTPOT_INTERNAL_ADMIN_EMAIL,
              IoTPotConstants.IOTPOT_INTERNAL_TENANT,
              computeRegions);
      tenantDao.save(te);
    }
    List<AccountEntity> accountEntityList = accountDao.findByTenant(te);
    if (accountEntityList == null || accountEntityList.size() == 0) {
      Set<RoleEntity> roles = roleDao.findByRole(Role.ADMIN.getValue());
      if (roles == null || roles.size() == 0) {
        RoleEntity role = new RoleEntity(Role.ADMIN.getValue());
        roleDao.save(role);
        roles.add(role);
      }
      sb.append("No default user found for tenant "
              + te.getName() + " with uuid " + te.getId() + ". Creating...\n");
      logger.log(Level.INFO, "No default user found for tenant "
              + te.getName() + " with uuid " + te.getId()
              + ". Creating...");
      AccountEntity account = new AccountEntity(te,
              IoTPotConstants.IOTPOT_INTERNAL_DEFAULT_USER,
              IoTPotConstants.IOTPOT_INTERNAL_ADMIN_EMAIL,
              IoTPotPasswordEncryptionManager.INSTANCE.encrypt(te.getName(), IoTPotConstants.IOTPOT_INTERNAL_DEFAULT_USER, IoTPotConstants.IOTPOT_INTERNAL_DEFAULT_PASSWORD),
              new HashSet<String>(),
              roles,
              te.getNextComputeRegionEntity());
      accountDao.save(account);
    }
    logger.log(Level.INFO, "Initialization of seed data done..");
    sb.append("Initialization of see data done..\n");
    return "{ \"message\" : \"" + sb.toString() + "\"}";
  }
  private String deleteSeedData() throws NoSuchPaddingException, UnsupportedEncodingException,
          IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidKeyException, InvalidKeySpecException {

    StringBuffer sb = new StringBuffer();
    accountDao.deleteAll();
    sb.append("Deleting accounts.");
    applianceDao.deleteAll();
    sb.append("Deleting devices.");
    roleDao.deleteAll();
    tenantDao.deleteAll();
    sb.append("Deleting tenants.");
    dataCenterDao.deleteAll();
    sb.append("Deleting compute regions.");
    return "{ \"message\" : \"" + sb.toString() + "\"}";
  }
}

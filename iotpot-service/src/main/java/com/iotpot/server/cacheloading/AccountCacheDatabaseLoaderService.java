/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.cacheloading;

import com.iotpot.server.dao.AccountDao;
import com.iotpot.server.dao.SessionDao;
import com.iotpot.server.dao.TenantDao;
import com.iotpot.server.entity.AccountEntity;
import com.iotpot.server.entity.SessionEntity;
import com.iotpot.server.pojos.Session;
import com.iotpot.server.pojos.Tenant;
import com.iotpot.server.common.utils.IoTPotPasswordEncryptionManager;
import com.iotpot.server.mapper.ObjectPatcher;
import com.iotpot.server.common.exception.IoTPotDatatypeConfigurationException;
import com.iotpot.server.mapper.AccountMapper;
import com.iotpot.server.entity.TenantEntity;
import com.iotpot.server.pojos.Account;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class AccountCacheDatabaseLoaderService {

  @Autowired
  private AccountDao accountDao;

  @Autowired
  private AccountMapper accountMapper;

  @Autowired
  private SessionDao sessionDao;

  @Autowired
  private TenantDao tenantDao;

  @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
  public Account loadFromDatabaseByFindOne(UUID id) {

    Account account = null;
    AccountEntity ae = accountDao.findOne(id);
    if(ae != null){
      account = accountMapper.mapEntityIntoPojo(ae);
    }
    return account;
  }
  @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
  public Account loadFromDatabaseByFindByAuthenticationToken(Tenant tenant, String authenticationToken) {

    Account account = null;
    SessionEntity sessionEntity = sessionDao.findByAuthToken(authenticationToken);
    TenantEntity tenantEntity = tenantDao.findOne(tenant.getId());
    UUID id = sessionEntity.getActorEntity().getId();
    account = accountMapper.mapEntityIntoPojo(accountDao.findOne(id));
    return account;
  }

  @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
  public Account loadFromDatabaseByFindByNameOrEmail(Tenant tenant, String nameOrEmail) {

    Account account = null;
    TenantEntity tenantEntity = tenantDao.findOne(tenant.getId());
    AccountEntity accountEntity = accountDao.findByTenantAndNameOrEmail(tenantEntity, nameOrEmail);
    if(accountEntity != null){

      account = accountMapper.mapEntityIntoPojo(accountEntity);
    }
    return account;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteAccount(Account account) {

    AccountEntity accountEntity = accountDao.findOne(account.getId());
    accountDao.delete(accountEntity);
  }
  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteToken(UUID id) {

    AccountEntity accountEntity = accountDao.findOne(id);
    for (String key : accountEntity.getSessionMap().keySet()) {
      accountEntity.getSessionMap().remove(key);
    }
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteToken(UUID id, String authToken) {

    AccountEntity accountEntity = accountDao.findOne(id);
    accountEntity.getSessionMap().remove(authToken);
  }

  @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
  public List<Account> findAll(Tenant tenant) {
    TenantEntity tenantEntity = tenantDao.findOne(tenant.getId());
    List<AccountEntity> accountEntities = accountDao.findByTenant(tenantEntity);
    List<Account> accounts = new ArrayList<>();
    accountEntities.forEach(e -> accounts.add(accountMapper.mapEntityIntoPojo(e)));
    return accounts;
  }
  /**
   * This method is called when a new authentication takes places. If we already have an account entry in cache,
   * we need to evict that, update the record with a new auth token and then return the new entity after caching that.
   *
   * @param tenant
   * @param account
   * @param remoteAddr
   * @param applicationId
   * @return
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public Account generateAuthToken(Tenant tenant, Account account, String remoteAddr, String applicationId) {

    String authToken = generateAuthToken(tenant.getDiscriminator(), account.getName());
    TenantEntity tenantEntity = tenantDao.findOne(tenant.getId());
    AccountEntity accountEntity = accountDao.findByTenantAndName(tenantEntity, account.getName());
    if (accountEntity.getSessionMap() == null) {
      accountEntity.setSessionMap(new HashMap<>());
      // No session exists, we need to create a new one and return.
    }
    // Irrespective of whether the auth token is present or not, we overwrite it.
    SessionEntity sessionEntity = accountEntity.getSessionMap().get(applicationId);
    if (sessionEntity == null) {

      try {
        sessionEntity
                = new SessionEntity(authToken, remoteAddr, applicationId, accountEntity, Session.SESSION_TYPE.APPLICATION_SESSION.getIValue());
      } catch (DatatypeConfigurationException e) {
        e.printStackTrace();
        throw new IoTPotDatatypeConfigurationException(e);
      }
    }
    else {
      sessionEntity.setAuthToken(authToken);
    }
    accountEntity.getSessionMap().put(applicationId, sessionEntity);
    sessionDao.save(sessionEntity);
    if (!accountEntity.getRemoteAddresses().contains(remoteAddr)) {
      accountEntity.getRemoteAddresses().add(remoteAddr);
    }
    accountEntity = accountDao.save(accountEntity);
    account = accountMapper.mapEntityIntoPojo(accountEntity);
    return account;
  }

  private String generateAuthToken(String tenant, String username) {
    UUID uuid1 = UUID.randomUUID();
    UUID uuid2 = UUID.randomUUID();

    String token = Long.toHexString(uuid1.getLeastSignificantBits()) +
            Long.toHexString(uuid1.getMostSignificantBits()) +
            Hex.encodeHexString(tenant.getBytes()) +
            Long.toHexString(uuid2.getLeastSignificantBits()) +
            Long.toHexString(uuid2.getMostSignificantBits()) +
            Hex.encodeHexString(username.getBytes());
    return token;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public Account save(Tenant tenant, Account account)
          throws NoSuchPaddingException,
          UnsupportedEncodingException,
          IllegalBlockSizeException,
          BadPaddingException, NoSuchAlgorithmException,
          InvalidParameterSpecException,
          InvalidKeyException,
          InvalidKeySpecException {

    TenantEntity tenantEntity = tenantDao.findOne(tenant.getId());
    AccountEntity accountEntity = accountMapper.mapPojoIntoEntity(account);
    String encryptedPassword
            = IoTPotPasswordEncryptionManager
            .INSTANCE.encrypt(tenant.getDiscriminator(), account.getName(), account.getPassword());
    accountEntity.setPassword(encryptedPassword);
    accountEntity.setDataCenterEntity(tenantEntity.getNextComputeRegionEntity());
    account.setPassword(encryptedPassword);
    accountEntity = accountDao.save(accountEntity);
    return accountMapper.mapEntityIntoPojo(accountEntity);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public Account update(Tenant tenant, UUID id, Account account)
          throws NoSuchPaddingException,
          InvalidKeySpecException,
          UnsupportedEncodingException,
          IllegalBlockSizeException,
          BadPaddingException,
          NoSuchAlgorithmException,
          InvalidKeyException,
          InvalidParameterSpecException,
          InvocationTargetException,
          IllegalAccessException {

    AccountEntity storedEntity = accountDao.findOne(id);
    AccountEntity newEntity = accountMapper.mapPojoIntoEntity(account);
    ObjectPatcher.diffAndPatch(storedEntity, newEntity);
    return account;
  }
  @Transactional(propagation = Propagation.REQUIRED)
  public void changePassword(Tenant tenant, UUID id, String newPassword)
          throws NoSuchPaddingException,
          UnsupportedEncodingException,
          IllegalBlockSizeException,
          BadPaddingException,
          NoSuchAlgorithmException,
          InvalidParameterSpecException,
          InvalidKeyException,
          InvalidKeySpecException {

    AccountEntity storedEntity = accountDao.findOne(id);
    String encryptedPassword
            = IoTPotPasswordEncryptionManager
            .INSTANCE.encrypt(tenant.getDiscriminator(), storedEntity.getName(), newPassword);
    storedEntity.setPassword(encryptedPassword);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public List<UUID> deleteAllAccounts(Tenant tenant) {
    TenantEntity tenantEntity = tenantDao.findOne(tenant.getId());
    List<UUID> list = accountDao.findByAllIds(tenantEntity);
    accountDao.deleteAll(tenantEntity);
    return list;
  }
}

/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.caching;

import com.iotpot.server.common.utils.IoTPotPasswordEncryptionManager;
import com.iotpot.server.common.utils.IoTPotUUIDPair;
import com.iotpot.server.common.config.annotations.ORMCache;
import com.iotpot.server.common.enums.Role;
import com.iotpot.server.cacheloading.AccountCacheDatabaseLoaderService;
import com.iotpot.server.common.utils.IoTPotUUIDStringPair;
import com.iotpot.server.entity.ApplianceEntity;
import com.iotpot.server.mapper.AccountMapper;
import com.iotpot.server.dao.AccountDao;
import com.iotpot.server.entity.RoleEntity;
import com.iotpot.server.pojos.Account;
import com.iotpot.server.pojos.Appliance;
import com.iotpot.server.pojos.Session;
import com.iotpot.server.pojos.Tenant;
import com.iotpot.server.pojos.constants.IoTPotConstants;
import com.iotpot.server.dao.ApplianceDao;
import com.iotpot.server.dao.RoleDao;
import com.iotpot.server.entity.AccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.*;

/**
 * Created by vinay on 2/22/16.
 */
@Service
@ORMCache(name = IoTPotConstants.IOTPOT_ACCOUNT_CACHE_NAME,
        expiry = IoTPotConstants.SIX_HOURS,
        prefix = IoTPotConstants.IOTPOT_ACCOUNT_CACHE_PREFIX)
public class AccountCacheService extends AbstractGeneralCacheService {

  @Autowired
  private AccountCacheDatabaseLoaderService accountCacheDatabaseLoaderService;

  @Autowired
  private AccountDao accountDao;
  @Autowired
  private ApplianceDao applianceDao;
  @Autowired
  private RoleDao roleDao;
  @Autowired
  private AccountMapper accountMapper;

  public Account findOne(UUID id) {

    Account account = get(id, Account.class);
    if (account != null) {
      return account;
    }
    return storeToCacheAndReturn(accountCacheDatabaseLoaderService.loadFromDatabaseByFindOne(id));
  }

  public Account findByAuthToken(Tenant tenant, String authToken) {

    IoTPotUUIDStringPair key = new IoTPotUUIDStringPair(tenant.getId(), authToken);
    UUID id = get(key, UUID.class);
    Account account = null;
    if (id != null) {
      account = get(id, Account.class);
      if (account != null) {
        return account;
      }
      deleteKey(key);
    }
    return storeToCacheAndReturn(accountCacheDatabaseLoaderService.loadFromDatabaseByFindByAuthenticationToken(tenant, authToken));
  }

  public Account findByTenantAndNameOrEmail(Tenant tenant, String nameOrEmail) {

    IoTPotUUIDStringPair key = new IoTPotUUIDStringPair(tenant.getId(), nameOrEmail);
    UUID id = get(key, UUID.class);
    Account account = null;
    if (id != null) {
      account = get(id, Account.class);
      if (account != null) {
        return account;
      }
      deleteKey(key);
    }
    return storeToCacheAndReturn(accountCacheDatabaseLoaderService.loadFromDatabaseByFindByNameOrEmail(tenant, nameOrEmail));
  }


  public Account deleteAccount(Tenant tenant, UUID id) {

    Account account = findOne(id);
    accountCacheDatabaseLoaderService.deleteAccount(account);
    evictFromCache(account);
    return account;
  }

  /**
   * This method would delete all the tokens for a particular user. It deletes all the entries from session map,
   * evicts all the cache entries for that user and then recache it.
   *
   * @param tenant
   * @param id
   * @return
     */
  public Account deleteToken(Tenant tenant, UUID id) {

    Account account = findOne(id);
    evictFromCache(account);
    accountCacheDatabaseLoaderService.deleteToken(account.getId());
    return findOne(account.getId());
  }

  /**
   * This method would remove a single token for a particular user from the cache.
   * @param tenant
   * @param token
   * @return
   */

  public Account deleteToken(Tenant tenant, String token) {

    Account account = findByAuthToken(tenant, token);
    evictFromCache(account);
    accountCacheDatabaseLoaderService.deleteToken(account.getId());
    return findOne(account.getId());
  }

  /**
   * This method is called to check if a particular user already exists in the system. This will check for
   * duplication of username and email address.
   * @param tenant
   * @param account
   * @return
   */
  public boolean accountExists(Tenant tenant, Account account) {

    if (findByTenantAndNameOrEmail(tenant, account.getName()) != null ||
            findByTenantAndNameOrEmail(tenant, account.getEmail()) != null) {
      return true;
    }
    return false;
  }

  /**
   * This method is called to query all the users for a particular tenantDiscriminator. This query is directly performed on the
   * database.
   * @param tenant
   * @return
   */
  public List<Account> findByTenant(Tenant tenant) {

    return accountCacheDatabaseLoaderService.findAll(tenant);
  }

  public Account generateAuthToken(Tenant tenant, Account account, String remoteAddr, String applicationId) {
    return accountCacheDatabaseLoaderService.generateAuthToken(tenant, account, remoteAddr, applicationId);
  }

  public Account save(Tenant tenant, Account account)
      throws NoSuchPaddingException,
      InvalidKeySpecException,
      UnsupportedEncodingException,
      IllegalBlockSizeException,
      BadPaddingException,
      NoSuchAlgorithmException,
      InvalidKeyException,
      InvalidParameterSpecException {

    return accountCacheDatabaseLoaderService.save(tenant, account);
  }

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

    Account cacheAccount = findOne(id);
    evictFromCache(cacheAccount);
    return accountCacheDatabaseLoaderService.update(tenant, id, account);
  }

  public Account delete(Account account)
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

    evictFromCache(account);
    AccountEntity storedEntity = accountDao.findOne(account.getId());
    storedEntity.setDataCenterEntity(null);
    accountDao.delete(storedEntity);
    return account;
  }

  public Account changePassword(Tenant tenant, UUID id, String oldPassword, String newPassword)
      throws NoSuchPaddingException,
      InvalidKeySpecException,
      UnsupportedEncodingException,
      IllegalBlockSizeException,
      BadPaddingException,
      NoSuchAlgorithmException,
      InvalidKeyException,
      InvalidParameterSpecException,
      InvocationTargetException,
      IllegalAccessException, InvalidAlgorithmParameterException {

    Account cachedAccount = findOne(id);
    if (IoTPotPasswordEncryptionManager
        .INSTANCE.matches(tenant.getDiscriminator(),
            cachedAccount.getName(),
            cachedAccount.getPassword(),
            oldPassword)) {

      accountCacheDatabaseLoaderService.changePassword(tenant, id, newPassword);
      evictFromCache(cachedAccount);
    }
    return findOne(id);
  }

  private void storeToCache(Account account) {

    Map<Object, Object> keyValuePairs = new HashMap<>();
    keyValuePairs.put(account.getId(), account);
    if (account.getSessionMap() != null && account.getSessionMap().size() > 0) {

      for (Map.Entry<String, Session> entry : account.getSessionMap().entrySet()) {

        keyValuePairs.put(new IoTPotUUIDStringPair(account.getTenant().getId(), entry.getValue().getAuthToken()), account.getId());
      }
    }
    keyValuePairs.put(new IoTPotUUIDPair(account.getTenant().getId(), account.getId()), account.getId());
    keyValuePairs.put(new IoTPotUUIDStringPair(account.getTenant().getId(), account.getName()), account.getId());
    keyValuePairs.put(new IoTPotUUIDStringPair(account.getTenant().getId(), account.getEmail()), account.getId());
    storeObject(keyValuePairs);
  }
  public void evictFromCache(Account account) {
    List<Object> keys = new ArrayList<>();
    keys.add(account.getId());
    if (account.getSessionMap() != null && account.getSessionMap() != null) {

      for (Map.Entry<String, Session> entry : account.getSessionMap().entrySet()) {
        keys.add(new IoTPotUUIDStringPair(account.getTenant().getId(), entry.getValue().getAuthToken()));
      }
    }
    keys.add(new IoTPotUUIDPair(account.getTenant().getId(), account.getId()));
    keys.add(new IoTPotUUIDStringPair(account.getTenant().getId(), account.getName()));
    keys.add(new IoTPotUUIDStringPair(account.getTenant().getId(), account.getEmail()));
    deleteObject(keys);
  }
  public Account adoptDevice(Account account, Appliance appliance) {

    AccountEntity accountEntity = accountDao.findOne(account.getId());

    ApplianceEntity applianceEntity
            = new ApplianceEntity(appliance.getDeviceRegistrationId(),
            accountEntity);
    Set<RoleEntity> roleEntities = new HashSet<>();
    roleEntities.add(new RoleEntity(Role.DEVICE.getValue()));
    roleDao.save(roleEntities);
    applianceEntity.setRoles(roleEntities);
    applianceEntity.setAccountEntity(accountEntity);
    if (accountEntity.getDevices() == null) {
      accountEntity.setDevices(new HashSet<>());
    }
    applianceEntity = applianceDao.save(applianceEntity);
    accountEntity.getDevices().add(applianceEntity);
    account = accountMapper.mapEntityIntoPojo(accountEntity);
    return account;
  }

  /**
   * Unadopt appliance would remove the associaton of a appliance with an account. The appliance still exists in the database, but as an unattached
   * appliance.
   *
   * @param accountId
   * @param appliance
   * @return
   */
  public Account unadoptDevice(UUID accountId, Appliance appliance) {

    AccountEntity accountEntity = accountDao.findOne(accountId);
    ApplianceEntity applianceEntity = applianceDao.findOne(appliance.getId());
    applianceEntity.setAccountEntity(null);
    if (accountEntity.getDevices() != null) {
      Iterator<ApplianceEntity> iterator = accountEntity.getDevices().iterator();
      while (iterator.hasNext()) {
        ApplianceEntity de = iterator.next();
        if (de.getId().equals(applianceEntity.getId())) {
          iterator.remove();
        }
      }
    }
    Account account = accountMapper.mapEntityIntoPojo(accountEntity);
    return account;
  }
  public Account unadoptDevice(Account account, Appliance appliance) {

    return unadoptDevice(account.getId(), appliance);
  }
  private Account storeToCacheAndReturn(Account account) {

    if (account != null) {
      storeToCache(account);
    }
    return account;
  }

  public void deleteAllAccounts(Tenant tenant) {
    List<UUID> allids = accountCacheDatabaseLoaderService.deleteAllAccounts(tenant);
    for (UUID id : allids) {
      Account account = findOne(id);
      evictFromCache(account);
    }
  }

}
